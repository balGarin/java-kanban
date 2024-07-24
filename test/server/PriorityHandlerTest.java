package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import domain.Status;
import domain.Task;
import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PriorityHandlerTest {
    TaskManager manager = Managers.getDefault();
    HttpTaskServer server = new HttpTaskServer(manager);
    Gson gson = new GsonBuilder().setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .serializeNulls()
            .create();

    @Test
    void shouldGetCorrectPriorityList() throws IOException, InterruptedException {
        server.start();
        List<Task> tasks = new ArrayList<>();
        Task firstTask = new Task("name", "description", Status.NEW, LocalDateTime.of(2024
                , 12, 12, 20, 0), Duration.ofMinutes(45));

        Task secondTask = new Task("name", "description", Status.NEW, LocalDateTime.of(2024
                , 12, 12, 18, 35), Duration.ofMinutes(15));
        Task thirdTask = new Task("name", "description", Status.NEW, LocalDateTime.of(2024
                , 12, 12, 14, 35), Duration.ofMinutes(15));
        tasks.add(firstTask);
        tasks.add(secondTask);
        tasks.add(thirdTask);
        manager.addTask(firstTask);
        manager.addTask(secondTask);
        manager.addTask(thirdTask);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        List<Task> prioritizedList = gson.fromJson(response.body(), new TaskListTypeToken().getType());
        assertEquals(tasks.size(), prioritizedList.size(), "Список не корректный");
        assertNotEquals(tasks, prioritizedList, "Приоритет не верный");
        server.stop();
    }

    class TaskListTypeToken extends TypeToken<List<Task>> {
    }

}
package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import domain.*;
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

class HistoryHandlerTest {

    TaskManager manager = Managers.getDefault();
    HttpTaskServer server = new HttpTaskServer(manager);
    Gson gson = new GsonBuilder().setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .serializeNulls()
            .create();

    @Test
    void shouldGetCorrectHistory() throws IOException, InterruptedException {
        server.start();
        List<Task> tasks = new ArrayList<>();
        Task task = new Task("name", "description", Status.NEW);
        manager.addTask(task);
        tasks.add(manager.getTaskById(1));
        Task task1 = new Task("name", "description", Status.NEW);
        manager.addTask(task1);
        tasks.add(manager.getTaskById(2));
        Task task2 = new Task("name1", "description", Status.NEW);
        manager.addTask(task2);
        tasks.add(manager.getTaskById(3));
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        List<Task> history = gson.fromJson(response.body(), new TaskListTypeToken().getType());
        assertEquals(tasks.size(), history.size(), "История не совпадает");
        assertEquals(tasks, history, "История отображается не корректно");
        server.stop();
    }

    class TaskListTypeToken extends TypeToken<List<Task>> {
    }
}
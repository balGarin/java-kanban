package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import domain.Status;
import domain.Subtask;
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

class SubtaskHandlerTest {
    TaskManager manager = Managers.getDefault();
    HttpTaskServer server = new HttpTaskServer(manager);
    Gson gson = new GsonBuilder().setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .serializeNulls()
            .create();

    @Test
    void shouldReturnCorrectStatusCodeWithError() throws IOException, InterruptedException {
        server.start();
        Subtask subtask = new Subtask("name", "description", Status.NEW, 1);
        String jsonSubtask = gson.toJson(subtask);
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(jsonSubtask)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode(), "Задача добавилась без Эпика");
        List<Subtask> emptyList = new ArrayList<>();
        assertEquals(emptyList, manager.getSubtasks(), "Список не пустой");
        server.stop();
    }
}
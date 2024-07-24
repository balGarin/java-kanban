package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import domain.Epic;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EpicHandlerTest {

    TaskManager manager = Managers.getDefault();
    HttpTaskServer server = new HttpTaskServer(manager);
    Gson gson = new GsonBuilder().setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .serializeNulls()
            .create();

    @Test
    void shouldCorrectRemoveAllSubtasksIfRemovedEpic() throws IOException, InterruptedException {
        server.start();
        manager.addEpic(new Epic("name", "description", Status.NEW));
        manager.addSubtask(new Subtask("name", "description", Status.IN_PROGRESS, 1
                , LocalDateTime.of(2024, 12, 10, 12, 0), Duration.ofMinutes(15)));
        manager.addSubtask(new Subtask("name", "description", Status.IN_PROGRESS, 1
                , LocalDateTime.of(2024, 12, 12, 14, 30), Duration.ofMinutes(45)));
        manager.addSubtask(new Subtask("name", "description", Status.IN_PROGRESS, 1
                , LocalDateTime.of(2024, 12, 13, 14, 30), Duration.ofMinutes(45)));
        int expected = 3;
        assertEquals(expected, manager.getSubtasks().size());
        assertEquals(expected, manager.getSubtaskByEpic(1).size());
        URI uri = URI.create("http://localhost:8080/epics/1");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder(uri).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertNotEquals(expected, manager.getSubtasks().size());
        server.stop();
    }

    @Test
    void shouldCorrectChangeStatusOfEpic() throws IOException, InterruptedException {
        server.start();
        manager.addEpic(new Epic("name", "description", Status.NEW));
        Subtask subtask = new Subtask("name", "description", Status.IN_PROGRESS, 1
                , LocalDateTime.of(2024, 12, 10, 12, 0), Duration.ofMinutes(15));
        String jsonSubtask = gson.toJson(subtask);
        HttpClient client = HttpClient.newHttpClient();
        URI uriPost = URI.create("http://localhost:8080/subtasks/");
        HttpRequest requestPost = HttpRequest.newBuilder(uriPost)
                .POST(HttpRequest.BodyPublishers.ofString(jsonSubtask)).build();
        HttpResponse<String> responsePost = client.send(requestPost, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, responsePost.statusCode());
        URI uriGet = URI.create("http://localhost:8080/epics/1");
        HttpRequest requestGet = HttpRequest.newBuilder(uriGet).GET().build();
        HttpResponse<String> responseGet = client.send(requestGet, HttpResponse.BodyHandlers.ofString());
        Epic epic = gson.fromJson(responseGet.body(), Epic.class);
        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Статус не обновился");
        server.stop();
    }

    @Test
    void shouldCorrectCalculateTimeDotesOfEpic() throws IOException, InterruptedException {
        server.start();
        manager.addEpic(new Epic("name", "description", Status.NEW));
        Subtask subtask1 = new Subtask("name", "description", Status.IN_PROGRESS, 1
                , LocalDateTime.of(2024, 12, 10, 12, 0), Duration.ofMinutes(15));
        Subtask subtask2 = new Subtask("name", "description", Status.IN_PROGRESS, 1
                , LocalDateTime.of(2024, 12, 13, 14, 30), Duration.ofMinutes(45));
        assertNull(manager.getEpicById(1).getEndTime());
        assertNull(manager.getEpicById(1).getStartTime());
        assertNull(manager.getEpicById(1).getDuration());
        String jsonSubtask1 = gson.toJson(subtask1);
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/subtasks/");
        HttpRequest request1 = HttpRequest.newBuilder(uri)
                .POST(HttpRequest.BodyPublishers.ofString(jsonSubtask1)).build();
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response1.statusCode());
        assertEquals(subtask1.getStartTime(), manager.getEpicById(1).getStartTime()
                , "Начало Эпика не обновилось");
        assertEquals(subtask1.getEndTime(), manager.getEpicById(1).getEndTime(), "Конец Эпика не обновился");
        assertEquals(subtask1.getDuration(), manager.getEpicById(1).getDuration()
                , "Длительность Эпика не обновилась");
        String jsonSubtask2 = gson.toJson(subtask2);
        HttpRequest request2 = HttpRequest.newBuilder(uri)
                .POST(HttpRequest.BodyPublishers.ofString(jsonSubtask2)).build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response2.statusCode());
        assertEquals(subtask1.getStartTime(), manager.getEpicById(1).getStartTime()
                , "Начало Эпика не обновилось");
        assertEquals(subtask2.getEndTime(), manager.getEpicById(1).getEndTime()
                , "Конец Эпика не обновился");
        assertEquals(subtask1.getDuration().plus(subtask2.getDuration()), manager.getEpicById(1).getDuration()
                , "Длительность Эпика не обновилась");
        server.stop();
    }

    @Test
    void shouldGetCorrectListOfEpic() throws IOException, InterruptedException {
        server.start();
        manager.addEpic(new Epic("name", "description", Status.NEW));
        manager.addSubtask(new Subtask("name", "description", Status.IN_PROGRESS, 1
                , LocalDateTime.of(2024, 12, 10, 12, 0), Duration.ofMinutes(15)));
        manager.addSubtask(new Subtask("name", "description", Status.IN_PROGRESS, 1
                , LocalDateTime.of(2024, 12, 12, 14, 30), Duration.ofMinutes(45)));
        manager.addSubtask(new Subtask("name", "description", Status.IN_PROGRESS, 1
                , LocalDateTime.of(2024, 12, 13, 14, 30), Duration.ofMinutes(45)));

        URI uri = URI.create("http://localhost:8080/epics/1/subtasks");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        List<Subtask> subtasksOfEpic = gson.fromJson(response.body(), new SubtasksListTypeToken().getType());
        assertEquals(manager.getEpicById(1).getSubtasksOfEpic(), subtasksOfEpic, "Лист подзадач не совпадает");
        server.stop();

    }

    class SubtasksListTypeToken extends TypeToken<List<Subtask>> {
    }
}
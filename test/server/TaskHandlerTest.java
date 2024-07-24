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

class TaskHandlerTest {
    TaskManager manager = Managers.getDefault();
    HttpTaskServer server = new HttpTaskServer(manager);
    Gson gson = new GsonBuilder().setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .serializeNulls()
            .create();

    @Test
    void shouldCorrectAddTask() throws IOException, InterruptedException {
        server.start();
        Task task = new Task("name", "description"
                , Status.NEW, LocalDateTime.now(), Duration.ofMinutes(15));
        String jsonTask = gson.toJson(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        List<Task> tasksFromManager = manager.getTasks();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Не верное количество задач");
        assertEquals("name", tasksFromManager.get(0).getName(), "Имя изменилось");
        server.stop();
    }

    @Test
    void shouldCorrectUpdateTask() throws IOException, InterruptedException {
        server.start();
        Task oldTask = new Task("name", "description"
                , Status.NEW, LocalDateTime.now(), Duration.ofMinutes(15));
        manager.addTask(oldTask);
        Task updateTask = new Task("newName", "newDescription"
                , Status.IN_PROGRESS, LocalDateTime.now(), Duration.ofMinutes(20));
        updateTask.setId(1);
        String jsonTask = gson.toJson(updateTask);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        Task taskFromManager = manager.getTaskById(1);
        assertEquals(oldTask, taskFromManager, "Id не совпадает");
        assertNotEquals(oldTask.getName(), taskFromManager.getName(), "Имя не обновилось");
        assertNotEquals(oldTask.getDuration(), taskFromManager.getDuration(), "Длительность не обновилось");
        assertNotEquals(oldTask.getStatus(), taskFromManager.getStatus(), "Статус не обновился");
        server.stop();

    }

    @Test
    void shouldGetTask() throws IOException, InterruptedException {
        server.start();
        Task task = new Task("name", "description"
                , Status.NEW, LocalDateTime.now(), Duration.ofMinutes(15));
        manager.addTask(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        Task taskFromResponse = gson.fromJson(response.body(), Task.class);
        assertEquals(task, taskFromResponse, "Задача не загрузилась");
        server.stop();

    }

    @Test
    void shouldCorrectGetListOfTasks() throws IOException, InterruptedException {
        server.start();
        Task task1 = new Task("name", "description"
                , Status.NEW, LocalDateTime.now(), Duration.ofMinutes(15));
        manager.addTask(task1);
        Task task2 = new Task("name1", "description2", Status.NEW);
        manager.addTask(task2);
        List<Task> listOfTasks = List.of(task1, task2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        List<Task> listTasksFromResponse = gson.fromJson(response.body(), new TaskListTypeToken().getType());
        assertNotNull(listTasksFromResponse, "Список не загрузился");
        assertEquals(listOfTasks, listTasksFromResponse, "Списки не совпадают");
        server.stop();
    }

    @Test
    void shouldRemoveTask() throws IOException, InterruptedException {
        server.start();
        Task task = new Task("name", "description"
                , Status.NEW, LocalDateTime.now(), Duration.ofMinutes(15));
        manager.addTask(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        List<Task> emptyList = new ArrayList<>();
        assertEquals(emptyList, manager.getTasks(), "Список не пустой");
        server.stop();

    }

    @Test
    void shouldRemoveAllTasks() throws IOException, InterruptedException {
        server.start();
        Task task1 = new Task("name", "description"
                , Status.NEW, LocalDateTime.now(), Duration.ofMinutes(15));
        Task task2 = new Task("name1", "description2", Status.NEW);
        manager.addTask(task1);
        manager.addTask(task2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        List<Task> emptyList = new ArrayList<>();
        assertEquals(emptyList, manager.getTasks(), "Список не пустой");
        server.stop();

    }

    @Test
    void shouldReturnCorrectStatusCodeWithError() throws IOException, InterruptedException {
        server.start();
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "Ошибка");
        server.stop();

    }

    class TaskListTypeToken extends TypeToken<List<Task>> {
    }

}


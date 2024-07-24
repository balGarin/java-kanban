package server;

import com.google.gson.*;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import domain.Status;
import domain.Task;
import domain.Type;
import managers.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public class TaskHandler implements HttpHandler {
    TaskManager manager;

    public TaskHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        switch (method) {
            case "GET":
                handleGetRequest(exchange);
                break;
            case "POST":
                handlePostRequest(exchange);
                break;
            case "DELETE":
                handleDeleteRequest(exchange);
                break;
            default:
                writeResponse(exchange, "Такого эндпоинта не существует", 404);

        }

    }

    void handleGetRequest(HttpExchange exchange) throws IOException {
        String request = exchange.getRequestURI().getPath();
        String[] pathParts = request.split("/");
        if (pathParts.length == 2) {
            Gson gson = new GsonBuilder().setPrettyPrinting()
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                    .registerTypeAdapter(Duration.class, new DurationAdapter())
                    .serializeNulls()
                    .create();
            String response = gson.toJson(manager.getTasks());
            writeResponse(exchange, response, 200);
        } else if (pathParts.length == 3) {
            Optional<Integer> id = getTaskId(pathParts[2]);
            if (id.isPresent()) {
                Gson gson = new GsonBuilder().setPrettyPrinting()
                        .registerTypeAdapter(Duration.class, new DurationAdapter())
                        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                        .serializeNulls()
                        .create();
                String response = gson.toJson(manager.getTaskById(id.get()));
                writeResponse(exchange, response, 200);
            } else {
                writeResponse(exchange, "Некорректный идентификатор задачи", 400);
            }
        } else {
            writeResponse(exchange
                    , "Такого эндпоинта пока нет,или запрос составлен не верно!", 400);
        }
    }

    void handlePostRequest(HttpExchange exchange) throws IOException {
        String request = exchange.getRequestURI().getPath();
        String[] pathParts = request.split("/");
        Optional<Task> optionalTask = getTaskFromBody(exchange.getRequestBody());
        if (pathParts.length == 2) {
            if (optionalTask.isPresent()) {
                if (manager.addTask(optionalTask.get())) {
                    writeResponse(exchange, "Задача успешно добавлена!", 201);
                } else {
                    writeResponse(exchange, "Задача пересекается по времени!", 406);
                }
            } else {
                writeResponse(exchange, "Задача введена не корректно!", 400);
            }
        } else if (pathParts.length == 3) {
            Optional<Integer> id = getTaskId(pathParts[2]);

            if (optionalTask.isPresent() && id.isPresent()) {
                if (manager.updateTask(optionalTask.get())) {
                    writeResponse(exchange, "Задача успешно обновлена!", 201);
                } else {
                    writeResponse(exchange
                            , "Задача пересекается во времени!"
                            , 406);
                }

            } else {
                writeResponse(exchange, "Некорректный идентификатор задачи", 400);
            }
        } else {
            writeResponse(exchange
                    , "Такого эндпоинта пока нет,или запрос составлен не верно!", 400);

        }

    }

    void handleDeleteRequest(HttpExchange exchange) throws IOException {
        String request = exchange.getRequestURI().getPath();
        String[] pathParts = request.split("/");
        if (pathParts.length == 2) {
            manager.removeAllTasks();
            writeResponse(exchange, "Все задачи успешно удалены", 201);
        } else if (pathParts.length == 3) {
            Optional<Integer> id = getTaskId(pathParts[2]);
            if (id.isPresent()) {
                if (manager.removeTaskById(id.get())) {
                    writeResponse(exchange, "Задача удалена успешно!", 201);

                } else {
                    writeResponse(exchange, "Задача не найдена", 404);
                }

            } else {
                writeResponse(exchange, "Некорректный идентификатор задачи", 400);
            }

        } else {
            writeResponse(exchange
                    , "Такого эндпоинта пока нет,или запрос составлен не верно!", 400);

        }


    }

    Optional<Integer> getTaskId(String id) {

        try {
            return Optional.of(Integer.parseInt(id));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    Optional<Task> getTaskFromBody(InputStream stream) throws IOException {
        String jsonTask = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        if (jsonTask.isEmpty()) {
            return Optional.empty();
        }
        Gson gson = new GsonBuilder().setPrettyPrinting()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .serializeNulls()
                .create();
        Task task = gson.fromJson(jsonTask, Task.class);
        if (task.getName() != null && task.getDescription() != null) {

            task.setType(Type.TASK);
            if (task.getStatus() == null) task.setStatus(Status.NEW);

            return Optional.of(task);
        } else {
            return Optional.empty();

        }

    }


    void writeResponse(HttpExchange exchange,
                       String responseString,
                       int responseCode) throws IOException {
        try (OutputStream os = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(responseCode, 0);
            os.write(responseString.getBytes(StandardCharsets.UTF_8));
        }
        exchange.close();
    }

}














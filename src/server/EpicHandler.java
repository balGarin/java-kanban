package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import domain.*;
import managers.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

public class EpicHandler extends TaskHandler implements HttpHandler {

    public EpicHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handleGetRequest(HttpExchange exchange) throws IOException {
        String request = exchange.getRequestURI().getPath();
        String[] pathParts = request.split("/");
        if (pathParts.length == 2) {
            Gson gson = new GsonBuilder().setPrettyPrinting()
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                    .registerTypeAdapter(Duration.class, new DurationAdapter())
                    .serializeNulls()
                    .create();
            String response = gson.toJson(manager.getEpics());
            writeResponse(exchange, response, 200);
        } else if (pathParts.length == 3) {
            Optional<Integer> id = getTaskId(pathParts[2]);
            if (id.isPresent()) {
                Gson gson = new GsonBuilder().setPrettyPrinting()
                        .registerTypeAdapter(Duration.class, new DurationAdapter())
                        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                        .serializeNulls()
                        .create();
                String response = gson.toJson(manager.getEpicById(id.get()));
                writeResponse(exchange, response, 200);
            } else {
                writeResponse(exchange, "Некорректный идентификатор задачи", 400);
            }
        } else if (pathParts.length == 4) {
            Optional<Integer> id = getTaskId(pathParts[2]);
            if (pathParts[3].equals("subtasks")) {
                if (id.isPresent()) {
                    Gson gson = new GsonBuilder().setPrettyPrinting()
                            .registerTypeAdapter(Duration.class, new DurationAdapter())
                            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                            .serializeNulls()
                            .create();
                    String response = gson.toJson(manager.getEpicById(id.get()).getSubtasksOfEpic());
                    writeResponse(exchange, response, 200);
                } else {
                    writeResponse(exchange,
                            "Такого эндпоинта пока нет,или запрос составлен не верно!", 400);
                }
            } else {
                writeResponse(exchange, "Некорректный идентификатор задачи", 400);
            }
        } else {
            writeResponse(exchange,
                    "Такого эндпоинта пока нет,или запрос составлен не верно!", 400);
        }
    }

    @Override
    public void handlePostRequest(HttpExchange exchange) throws IOException {
        String request = exchange.getRequestURI().getPath();
        String[] pathParts = request.split("/");
        Optional<Task> optionalTask = getTaskFromBody(exchange.getRequestBody());
        if (pathParts.length == 2) {
            if (optionalTask.isPresent()) {
                manager.addEpic((Epic) optionalTask.get());
                writeResponse(exchange, "Задача успешно добавлена!", 201);

            } else {
                writeResponse(exchange, "Задача введена не корректно!", 400);
            }

        } else {
            writeResponse(exchange,
                    "Такого эндпоинта пока нет,или запрос составлен не верно!", 400);

        }

    }

    @Override
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
        Epic epic = gson.fromJson(jsonTask, Epic.class);
        if (epic.getName() != null && epic.getDescription() != null) {

            epic.setType(Type.EPIC);
            epic.setSubtasksOfEpic(new ArrayList<>());
            return Optional.of(epic);
        } else {
            return Optional.empty();

        }

    }

    @Override
    public void handleDeleteRequest(HttpExchange exchange) throws IOException {
        String request = exchange.getRequestURI().getPath();
        String[] pathParts = request.split("/");
        if (pathParts.length == 2) {
            manager.removeAllEpics();
            writeResponse(exchange, "Все задачи успешно удалены", 201);
        } else if (pathParts.length == 3) {
            Optional<Integer> id = getTaskId(pathParts[2]);
            if (id.isPresent()) {
                if (manager.removeEpicById(id.get())) {
                    writeResponse(exchange, "Задача удалена успешно!", 201);
                } else {
                    writeResponse(exchange, "Задача не найдена", 404);
                }
            } else {
                writeResponse(exchange, "Некорректный идентификатор задачи", 400);
            }

        } else {
            writeResponse(exchange,
                    "Такого эндпоинта пока нет,или запрос составлен не верно!", 400);

        }
    }

    @Override
    Optional<Integer> getTaskId(String id) {
        try {
            if (manager.getEpicById(Integer.parseInt(id)) == null) {
                return Optional.empty();
            }
            return Optional.of(Integer.parseInt(id));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}




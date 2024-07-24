package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import domain.Status;
import domain.Subtask;
import domain.Task;
import domain.Type;
import managers.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public class SubtaskHandler extends TaskHandler implements HttpHandler {


    public SubtaskHandler(TaskManager manager) {
        super(manager);
    }
@Override
     void handleGetRequest(HttpExchange exchange) throws IOException {
        String request = exchange.getRequestURI().getPath();
        String[] pathParts = request.split("/");
        if (pathParts.length == 2) {
            Gson gson = new GsonBuilder().setPrettyPrinting()
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                    .registerTypeAdapter(Duration.class, new DurationAdapter())
                    .serializeNulls()
                    .create();
            String response = gson.toJson(manager.getSubtasks());
            writeResponse(exchange, response, 200);
        } else if (pathParts.length == 3) {
            Optional<Integer> id = getTaskId(pathParts[2]);
            if (id.isPresent()) {
                Gson gson = new GsonBuilder().setPrettyPrinting()
                        .registerTypeAdapter(Duration.class, new DurationAdapter())
                        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                        .serializeNulls()
                        .create();
                String response = gson.toJson(manager.getSubtaskById(id.get()));
                writeResponse(exchange, response, 200);
            } else {
                writeResponse(exchange, "Некорректный идентификатор задачи", 400);
            }
        } else {
            writeResponse(exchange, "Такого эндпоинта пока нет,или запрос составлен не верно!", 400);
        }
    }
   @Override
    void handlePostRequest(HttpExchange exchange) throws IOException {
        String request = exchange.getRequestURI().getPath();
        String[] pathParts = request.split("/");
        Optional<Task> optionalTask = getTaskFromBody(exchange.getRequestBody());
        if (pathParts.length == 2) {
            if (optionalTask.isPresent()) {
                if (manager.addSubtask((Subtask) optionalTask.get())) {
                    writeResponse(exchange, "Задача успешно добавлена!", 201);
                } else {
                    writeResponse(exchange
                            , "Задача пересекается по времени или не найден Эпик для этой подзадачи!"
                            , 406);
                }
            } else {
                writeResponse(exchange, "Задача введена не корректно!", 400);
            }
        } else if (pathParts.length == 3) {
            Optional<Integer> id = getTaskId(pathParts[2]);

            if (optionalTask.isPresent() && id.isPresent()) {
                if(manager.updateSubtask((Subtask) optionalTask.get())){
                    writeResponse(exchange, "Задача успешно обновлена!", 201);

                }else {
                    writeResponse(exchange
                            ,"Задача пересекается во времени,или ее Эпик был удален!",406);
                }
            } else {
                writeResponse(exchange, "Некорректный идентификатор задачи", 400);
            }
        } else {
            writeResponse(exchange, "Такого эндпоинта пока нет,или запрос составлен не верно!", 400);

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
        Subtask subtask = gson.fromJson(jsonTask, Subtask.class);
        if (subtask.getName() != null && subtask.getDescription() != null) {

            subtask.setType(Type.SUBTASK);
            if (subtask.getStatus() == null) subtask.setStatus(Status.NEW);

            return Optional.of(subtask);
        } else {
            return Optional.empty();

        }

    }

    @Override
    void handleDeleteRequest(HttpExchange exchange) throws IOException {
        String request = exchange.getRequestURI().getPath();
        String[] pathParts = request.split("/");
        if (pathParts.length == 2) {
            manager.removeAllSubtasks();
            writeResponse(exchange, "Все задачи успешно удалены", 201);
        } else if (pathParts.length == 3) {
            Optional<Integer> id = getTaskId(pathParts[2]);
            if (id.isPresent()) {
                manager.removeSubtaskById(id.get());
                writeResponse(exchange, "Задача удалена успешно!", 201);
            } else {
                writeResponse(exchange, "Некорректный идентификатор задачи", 400);
            }

        } else {
            writeResponse(exchange, "Такого эндпоинта пока нет,или запрос составлен не верно!", 400);

        }
    }

    @Override
    Optional<Integer> getTaskId(String id) {
        try {
            if (manager.getSubtaskById(Integer.parseInt(id)) == null) {
                return Optional.empty();
            }
            return Optional.of(Integer.parseInt(id));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}

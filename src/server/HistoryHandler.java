package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

import static server.HttpTaskServer.HISTORY_URL;
import static server.Method.GET;

public class HistoryHandler implements HttpHandler {
    TaskManager manager;

    public HistoryHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Method method = Method.valueOf(exchange.getRequestMethod());
        String url = exchange.getRequestURI().getPath();
        if (method.equals(GET) && url.equals(HISTORY_URL)) {
            Gson gson = new GsonBuilder().setPrettyPrinting()
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                    .registerTypeAdapter(Duration.class, new DurationAdapter())
                    .serializeNulls()
                    .create();
            String response = gson.toJson(manager.getHistory());
            writeResponse(exchange, response, 200);

        } else {
            writeResponse(exchange,
                    "Такого эндпоинта пока нет,или запрос составлен не верно!", 400);

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

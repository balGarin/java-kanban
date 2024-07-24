package server;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import domain.Epic;
import domain.Status;
import domain.Subtask;
import domain.Task;
import managers.FileBackedTaskManager;
import managers.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private static TaskManager manager =new FileBackedTaskManager(Paths.get("tasks.txt"));



    public static void main(String[] args) throws IOException {
         HttpServer server = HttpServer.create();
         server.bind(new InetSocketAddress(PORT),0);
         server.createContext("/tasks",new TaskHandler(manager));
        server.createContext("/subtasks",new SubtaskHandler(manager));
        server.createContext("/epics",new EpicHandler(manager));

        manager.addTask(new Task("name", "description", Status.NEW));
        manager.addEpic(new Epic("name", "description", Status.NEW));
//        manager.addSubtask(new Subtask("name", "description", Status.NEW,2));
        manager.addTask(new Task("name", "description", Status.NEW));





        FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(Path.of("tasks.txt"));
     fileBackedTaskManager.getTasks().forEach(System.out::println);
        server.start();


    }
}

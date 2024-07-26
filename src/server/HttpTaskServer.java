package server;


import com.sun.net.httpserver.HttpServer;

import managers.Managers;
import managers.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;


public class HttpTaskServer {
    private static final int PORT = 8080;
    private TaskManager manager;
    private HttpServer server;
    static final String HISTORY_URL = "/history";
    static final String PRIORITY_URL = "/prioritized";
    static final String TASKS_URL = "/tasks";
    static final String SUBTASKS_URL = "/subtasks";
    static final String EPICS_URL = "/epics";

    public HttpTaskServer(TaskManager manager) {
        this.manager = manager;
    }

    public static void main(String[] args) throws IOException {
        TaskManager manager = Managers.getDefault();
        HttpTaskServer server = new HttpTaskServer(manager);
        server.start();
        server.stop();


    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext(TASKS_URL, new TaskHandler(manager));
        server.createContext(SUBTASKS_URL, new SubtaskHandler(manager));
        server.createContext(EPICS_URL, new EpicHandler(manager));
        server.createContext(HISTORY_URL, new HistoryHandler(manager));
        server.createContext(PRIORITY_URL, new PriorityHandler(manager));
        server.start();

    }

    public void stop() {
        server.stop(2);
    }
}


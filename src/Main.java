

import managers.*;

import java.nio.file.Paths;


public class Main {
    public static void main(String[] args) {
        FileBackedTaskManager manager = new FileBackedTaskManager(Paths.get("kanban.txt"));

    }
}

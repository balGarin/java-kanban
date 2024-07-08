

import domain.Epic;
import domain.Status;
import domain.Subtask;
import domain.Task;
import managers.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;


public class Main {
    public static void main(String[] args) {
//    FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(Path.of("kanban.txt"));
//    manager.getTasks().forEach(System.out::println);
//        manager.getSubtasks().forEach(System.out::println);
//        manager.getEpics().forEach(System.out::println);
//
//        Subtask subtask = manager.getSubtaskById(3);
//        subtask.setStatus(Status.IN_PROGRESS);
//        manager.updateSubtask(subtask);
//        System.out.println();
//        manager.getTasks().forEach(System.out::println);
//        manager.getSubtasks().forEach(System.out::println);
//        manager.getEpics().forEach(System.out::println);


//        FileBackedTaskManager manager = new FileBackedTaskManager(Path.of("kanban.txt"));
//        manager.addTask(new Task("name", "description", Status.NEW));
//        manager.addEpic(new Epic("name", "description", Status.NEW));
//        manager.addSubtask(new Subtask("name", "description", Status.NEW, 2));
//        manager.addSubtask(new Subtask("name", "description", Status.NEW, 2));
//        manager.addSubtask(new Subtask("name", "description", Status.NEW, 2));
//        manager.addTask(new Task("name", "description", Status.NEW));

        TaskManager manager = Managers.getDefault();
//        manager.addTask(new Task("name", "description", Status.NEW, LocalDateTime.of(2024,6,12,18,30), Duration.ofMinutes(15)));
//


    }
}

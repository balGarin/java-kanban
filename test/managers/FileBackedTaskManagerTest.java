package managers;

import domain.*;
import org.junit.jupiter.api.Test;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {


    @Test
    void shouldSaveAndLoadEmptyFile() throws IOException {
        File file = File.createTempFile("test", ".txt");
        FileBackedTaskManager manager = new FileBackedTaskManager(file.toPath());
        manager.save();
        String string = Files.readString(file.toPath());
        assertNotNull(string, "Заголовок не сохранился");
        FileBackedTaskManager manager1 = FileBackedTaskManager.loadFromFile(file.toPath());
        assertEquals(manager.getSubtasks(), manager1.getSubtasks(), "Пустой файл не загрузился");
        assertEquals(manager.getEpics(), manager1.getEpics(), "Пустой файл не загрузился");
        assertEquals(manager.getTasks(), manager1.getTasks(), "Пустой файл не загрузился");

    }

    @Test
    void shouldSaveAndLoadSeveralTasks() throws IOException {
        File file = File.createTempFile("test", ".txt");
        FileBackedTaskManager manager = new FileBackedTaskManager(file.toPath());
        List<Task> tasks = new ArrayList<>();
        Task task = new Task("name", "description", Status.NEW);
        manager.addTask(task);
        tasks.add(manager.getTaskById(1));
        Epic epic = new Epic("name", "description", Status.NEW);
        manager.addEpic(epic);
        tasks.add(manager.getEpicById(2));
        Subtask subtask = new Subtask("name1", "description", Status.NEW, 2);
        manager.addSubtask(subtask);
        tasks.add(manager.getSubtaskById(3));
        FileBackedTaskManager managerUploaded = FileBackedTaskManager.loadFromFile(file.toPath());
        List<Task> tasksUploaded = new ArrayList<>();
        tasksUploaded.add(managerUploaded.getTaskById(1));
        tasksUploaded.add(managerUploaded.getEpicById(2));
        tasksUploaded.add(managerUploaded.getSubtaskById(3));
        assertEquals(tasks, tasksUploaded, "Данные не совпадают");
    }

    @Test
    void shouldSaveInFileAfterUpdate() throws IOException {
        File file = File.createTempFile("test", ".txt");
        FileBackedTaskManager manager = new FileBackedTaskManager(file.toPath());
        List<Task> tasks = new ArrayList<>();
        Task task = new Task("name", "description", Status.NEW);
        manager.addTask(task);
        tasks.add(manager.getTaskById(1));
        Epic epic = new Epic("name", "description", Status.NEW);
        manager.addEpic(epic);
        tasks.add(manager.getEpicById(2));
        Task uploadedTask = manager.getTaskById(1);
        uploadedTask.setStatus(Status.DONE);
        manager.updateTask(uploadedTask);
        FileBackedTaskManager managerUploaded = FileBackedTaskManager.loadFromFile(file.toPath());
        List<Task> tasksUploaded = new ArrayList<>();
        tasksUploaded.add(managerUploaded.getTaskById(1));
        tasksUploaded.add(managerUploaded.getEpicById(2));
        assertEquals(tasks, tasksUploaded, "Файл не сохранил изменения");
    }


}
package managers;

import domain.*;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {


    File file = File.createTempFile("test", ".txt");

    FileBackedTaskManagerTest() throws IOException {
    }


    @Test
    void shouldSaveAndLoadEmptyFile() {
        FileBackedTaskManager manager = new FileBackedTaskManager(file.toPath());
        boolean thrown = false;
        try {
            manager.save();
        } catch (Exception e) {
            thrown = true;
        }
        assertFalse(thrown, "Файл не сохранился");
        try {
            FileBackedTaskManager manager1 = FileBackedTaskManager.loadFromFile(file.toPath());
        } catch (Exception e) {
            thrown = true;
        }
        assertFalse(thrown, "Файл не загрузился");

    }

    @Test
    void shouldSaveAndLoadSeveralTasks() {
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
    void shouldSaveInFileAfterUpdate() {
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
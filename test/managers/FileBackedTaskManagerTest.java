package managers;

import domain.*;
import org.junit.jupiter.api.Test;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import java.time.Duration;
import java.time.LocalDateTime;
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

    @Test
    void shouldCorrectLoadPriorityListFromFile() throws IOException {
        File file = File.createTempFile("test", ".txt");
        FileBackedTaskManager manager = new FileBackedTaskManager(file.toPath());
        manager.addTask(new Task("name", "description", Status.NEW, LocalDateTime.of(2024
                , 12, 12, 20, 0), Duration.ofMinutes(15)));
        manager.addTask(new Task("name", "description", Status.NEW, LocalDateTime.of(2024
                , 12, 12, 20, 14), Duration.ofMinutes(15)));
        manager.addTask(new Task("name", "description", Status.NEW, LocalDateTime.of(2024
                , 12, 12, 20, 10), Duration.ofMinutes(15)));
        FileBackedTaskManager managerUploaded = FileBackedTaskManager.loadFromFile(file.toPath());
        assertNotNull(managerUploaded.getPrioritizedTasks(), "Список не загрузился");
        assertEquals(manager.getPrioritizedTasks(), managerUploaded.getPrioritizedTasks()
                , "Список подгружается не верно");
    }

    @Test
    void shouldCorrectRemoveTaskAndCorrectLoadFromFileWithCorrectId() throws IOException {
        File file = File.createTempFile("test", ".txt");
        FileBackedTaskManager manager = new FileBackedTaskManager(file.toPath());
        manager.addTask(new Task("name", "description", Status.NEW));
        manager.addEpic(new Epic("name", "description", Status.NEW));
        manager.addTask(new Task("name", "description", Status.NEW));
        manager.removeEpicById(2);
        FileBackedTaskManager managerUploaded = FileBackedTaskManager.loadFromFile(file.toPath());
        assertEquals(manager.getEpics(), managerUploaded.getEpics(), "Задача не удалилась из файла");
        assertEquals(manager.getTaskById(3), managerUploaded.getTaskById(3), "Id не совпадает");
    }

    @Test
    void shouldThrowExceptionWithSaveToFile() throws IOException {
        FileBackedTaskManager manager = new FileBackedTaskManager((Files.createTempDirectory("test1")));
        Exception exception = assertThrows(ManagerSaveException.class, manager::save);
        String expectedMessage = "Ошибка при записи в файл!!!";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }


}
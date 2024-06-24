package managers;

import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    @Test
    void shouldAddTaskInHistory() {
        TaskManager manager = Managers.getDefault();
        manager.addTask(new Task("name", "description", Status.NEW));
        manager.getTaskById(1);
        assertEquals(1, manager.getHistory().size(), "История не сохранилась");
    }

    @Test
    void shouldGetHistoryReturnCorrectHistory() {
        TaskManager manager = Managers.getDefault();
        Task task = new Task("name", "description", Status.NEW);
        manager.addTask(task);
        Epic epic = new Epic("name", "description", Status.NEW);
        manager.addEpic(epic);
        Subtask subtask = new Subtask("name1", "description", Status.NEW, 2);
        manager.addSubtask(subtask);
        List<Task> tasks = new ArrayList<>();
        tasks.add(manager.getTaskById(1));
        tasks.add(manager.getSubtaskById(2));
        tasks.add(manager.getEpicById(3));
        assertIterableEquals(tasks, manager.getHistory(), "История не корректна!");
        task.setStatus(Status.IN_PROGRESS);
        manager.updateTask(task);
        tasks.add(manager.getTaskById(1));
        assertIterableEquals(tasks, manager.getHistory(), "История не сохраняет предыдущие версии!");
    }


    @Test
    void shouldHistoryIncludesMaz10Elements() {
        TaskManager manager = Managers.getDefault();
        for (int i = 1; i <= 15; i++) {
            Task task = new Task("name", "description", Status.NEW);
            manager.addTask(task);
            manager.getTaskById(i);
        }
        assertEquals(10, manager.getHistory().size(), "Предел истории не корректен!");
    }
}
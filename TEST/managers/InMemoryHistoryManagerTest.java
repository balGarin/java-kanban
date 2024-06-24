package managers;

import org.junit.jupiter.api.Test;
import domain.Epic;
import domain.Status;
import domain.Subtask;
import domain.Task;

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
        assertEquals(tasks, manager.getHistory(), "История не совпадает");
    }


    @Test
    void shouldHistoryRemoveAllSubtasksIfRemoveEpic() {
        TaskManager manager = Managers.getDefault();
        manager.addEpic(new Epic("name", "description", Status.NEW));
        manager.addSubtask(new Subtask("name1", "description", Status.NEW, 1));
        manager.addSubtask(new Subtask("name1", "description", Status.NEW, 1));
        manager.addSubtask(new Subtask("name1", "description", Status.NEW, 1));
        manager.getSubtaskById(2);
        manager.getSubtaskById(3);
        manager.getSubtaskById(4);
        manager.removeEpicById(1);
        assertEquals(0, manager.getHistory().size(), "Сабтаски не были удалены после удаления их Эпика");
    }

    @Test
    void shouldHistoryRemoveOldVersionOfTasks() {
        TaskManager manager = Managers.getDefault();
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
        tasks.add(manager.getTaskById(1));
        assertEquals(3, manager.getHistory().size(), "История добавила дубликат");
        assertNotEquals(tasks, manager.getHistory(), "История не корректна");
        assertNotEquals(task, manager.getHistory().get(1), "Старая версия задачи не удалилась");

    }
}
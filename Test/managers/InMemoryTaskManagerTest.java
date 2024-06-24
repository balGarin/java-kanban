package managers;

import org.junit.jupiter.api.Test;
import domain.Epic;
import domain.Status;
import domain.Subtask;
import domain.Task;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    @Test
    public void shouldAddTaskInMemoryTaskManager() {

        TaskManager manager = Managers.getDefault();
        Task task = new Task("name", "description", Status.NEW);
        manager.addTask(task);
        task.setId(1);
        Task managerTask = manager.getTaskById(task.getId());
        assertNotNull(managerTask, "Задача не сохраняется");
        assertEquals(task, managerTask, "Задачи не совпадают");
    }

    @Test
    public void shouldReturnCorrectListOfTasks() {
        TaskManager manager = Managers.getDefault();
        Epic epic = new Epic("name", "description", Status.NEW);
        manager.addEpic(epic);
        List<Epic> epics = manager.getEpics();
        assertNotNull(epics, "Список пустой!");
        manager.addEpic(new Epic("name", "description", Status.NEW));
        epics = manager.getEpics();
        assertEquals(2, manager.getEpics().size(), "Не верное количество задач");
    }

    @Test
    public void shouldRemoveAllTasks() {
        TaskManager manager = Managers.getDefault();
        for (int i = 1; i <= 10; i++) {
            manager.addTask(new Task("name", "description", Status.NEW));
        }
        manager.removeAllTasks();
        assertEquals(0, manager.getTasks().size(), "Список не пуст");
    }

    @Test
    public void shouldGetTaskByIdAndCompareById() {
        TaskManager manager = Managers.getDefault();
        manager.addTask(new Task("name", "description", Status.NEW));
        Task task = new Task("name", "description", Status.DONE);
        task.setId(1);
        assertNotNull(manager.getTaskById(1), "Задача не возвращается");
        assertEquals(task, manager.getTaskById(1), "Задачи не равны при равном ID");
        manager.addEpic(new Epic("name", "description", Status.NEW));
        Epic epic = new Epic("name", "description", Status.NEW);
        epic.setId(2);
        assertEquals(epic, manager.getEpicById(2), "Наследники не равны при равном ID");
    }

    @Test
    public void shouldUpdateAndRemoveTaskById() {
        TaskManager manager = Managers.getDefault();
        Task updatedTask = new Task("UpdatedName", "UpdatedDescription", Status.IN_PROGRESS);
        updatedTask.setId(1);
        manager.addTask(new Task("name", "description", Status.NEW));
        manager.updateTask(updatedTask);
        assertEquals(updatedTask, manager.getTaskById(1), "Задача не обновилась");
        manager.removeTaskById(1);
        assertNull(manager.getTaskById(1), "Задача не удаляется по ID");

    }

    @Test
    public void shouldUpdateStatusOfEpic() {
        TaskManager manager = Managers.getDefault();
        manager.addEpic(new Epic("name", "description", Status.NEW));
        manager.addSubtask(new Subtask("name", "description", Status.NEW, 1));
        assertEquals(Status.NEW, manager.getEpicById(1).getStatus(), "Статус не корректный");
        manager.addSubtask(new Subtask("name", "description", Status.DONE, 1));
        assertEquals(Status.IN_PROGRESS, manager.getEpicById(1).getStatus(), "Статус не корректный");
        Subtask subtask = manager.getSubtaskById(2);
        subtask.setStatus(Status.DONE);
        manager.updateSubtask(subtask);
        assertEquals(Status.DONE, manager.getEpicById(1).getStatus(), "Статус не корректный");
    }

    @Test
    public void shouldGetCorrectListOfSubTasksByEpic() {
        TaskManager manager = Managers.getDefault();
        List<Subtask> subtasks = new ArrayList<>();
        manager.addEpic(new Epic("name", "description", Status.NEW));
        Subtask subtask = new Subtask("name", "description", Status.DONE, 1);
        manager.addSubtask(subtask);
        subtask.setId(2);
        subtasks.add(subtask);
        Subtask subtask2 = new Subtask("name", "description", Status.DONE, 1);
        manager.addSubtask(subtask2);
        subtask.setId(3);
        subtasks.add(subtask2);
        assertIterableEquals(subtasks, manager.getEpicById(1).getSubtasksOfEpic(), "Список не корректный");
    }

    @Test
    public void shouldNotTaskChangeByAllFields() {
        TaskManager manager = Managers.getDefault();
        Task task = new Task("name", "description", Status.NEW);
        manager.addTask(task);
        assertEquals(task.getName(), manager.getTaskById(1).getName(), "Имя не совпадает");
        assertEquals(task.getDescription(), manager.getTaskById(1).getDescription(), "Описание не совпадает");
        assertEquals(task.getType(), manager.getTaskById(1).getType(), "Статус не совпадает");
    }

    @Test
    public void shouldNotAddSubtaskIntoHimselfAndWithoutEpic() {
        TaskManager manager = Managers.getDefault();
        manager.addSubtask(new Subtask("name", "description", Status.NEW, 1));
        assertNull(manager.getSubtaskById(1), "Подзадача добавлена");
// архитектура моего приложения на функционально уровне не даст добавить Эпик сам в себя в виде подзадачи
    }

    @Test
    public void shouldNotConflictGeneratedIdAndSetId() {
        TaskManager manager = Managers.getDefault();
        manager.addTask(new Task("name", "description", Status.NEW));
        Task task = new Task("name", "description", Status.NEW);
        task.setId(31);
        manager.addTask(task);
        assertNull(manager.getTaskById(31), "Задача существует с таким ID");
        assertEquals(2, manager.getTaskById(2).getId(), "У задачи выставлен не верный ID");
// в ТЗ не было задачи по функционалу добавления задачи с заданным ID, по этому ID всегда задает менеджер =)
    }
}
package managers;

import org.junit.jupiter.api.Test;
import domain.Epic;
import domain.Status;
import domain.Subtask;
import domain.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
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

    }

    @Test
    public void shouldNotSetsMethodsAffectAtManager() {
        TaskManager manager = Managers.getDefault();
        Task task = new Task("name", "description", Status.NEW);
        task.setId(1);
        manager.addTask(new Task("name", "description", Status.NEW));
        assertEquals(task, manager.getTaskById(1), "Заданный ID отличается от сгенерированного");
        task.setName("newName");
        task.setDescription("newDescription");
        task.setStatus(Status.IN_PROGRESS);
        manager.updateTask(task);
        assertEquals(task, manager.getTaskById(1), "Изменение полей задачи не повлияло на менеджера");
    }

    @Test
    public void shouldRemoveIdSubtasksFromEpic() {

        TaskManager manager = Managers.getDefault();
        manager.addEpic(new Epic("name", "description", Status.NEW));
        manager.addSubtask(new Subtask("name", "description", Status.NEW, 1));
        manager.addSubtask(new Subtask("name", "description", Status.NEW, 1));
        manager.addSubtask(new Subtask("name", "description", Status.NEW, 1));
        List<Subtask> tasks = manager.getSubtaskByEpic(1);
        manager.removeSubtaskById(2);
        assertNotEquals(tasks, manager.getSubtaskByEpic(1), "Сабтаска все еще в списки Эпика");


    }

    @Test
    public void shouldCorrectGetPriorityList() {
        TaskManager manager = Managers.getDefault();
        manager.addTask(new Task("name", "description", Status.NEW));
        manager.addEpic(new Epic("name", "description", Status.NEW));
        manager.addTask(new Task("name", "description", Status.NEW));
        List<Task> emptyList = new ArrayList<>();
        assertEquals(emptyList, manager.getPrioritizedTasks()
                , "Менеджер сохранил задачи без параметров времени");
        manager.addTask(new Task("name", "description", Status.NEW, LocalDateTime.of(2024
                , 12, 12, 20, 0), Duration.ofMinutes(15)));
        manager.addTask(new Task("name", "description", Status.NEW, LocalDateTime.of(2024
                , 12, 12, 21, 14), Duration.ofMinutes(15)));
        manager.addTask(new Task("name", "description", Status.NEW, LocalDateTime.of(2024
                , 12, 12, 22, 10), Duration.ofMinutes(15)));
        assertEquals(3, manager.getPrioritizedTasks().size(), "Лист заполнен не корректно!");
        manager.addSubtask(new Subtask("name", "description", Status.IN_PROGRESS, 2
                , LocalDateTime.of(2024, 12, 10, 12, 0), Duration.ofMinutes(15)));
        manager.addSubtask(new Subtask("name", "description", Status.IN_PROGRESS, 2
                , LocalDateTime.of(2024, 12, 12, 14, 30), Duration.ofMinutes(45)));
        manager.addSubtask(new Subtask("name", "description", Status.IN_PROGRESS, 2
                , LocalDateTime.of(2024, 12, 13, 14, 30), Duration.ofMinutes(45)));
        assertEquals(6, manager.getPrioritizedTasks().size(), "Подзадачи Эпика не попадают в лист");
    }

    @Test
    public void shouldCorrectCalculateTimesDotsOfEpic() {
        TaskManager manager = Managers.getDefault();
        manager.addEpic(new Epic("name", "description", Status.NEW));
        manager.addSubtask(new Subtask("name", "description", Status.IN_PROGRESS, 1
                , LocalDateTime.of(2024, 12, 10, 12, 0), Duration.ofMinutes(15)));
        manager.addSubtask(new Subtask("name", "description", Status.IN_PROGRESS, 1
                , LocalDateTime.of(2024, 12, 12, 14, 30), Duration.ofMinutes(45)));
        manager.addSubtask(new Subtask("name", "description", Status.IN_PROGRESS, 1
                , LocalDateTime.of(2024, 12, 13, 14, 30), Duration.ofMinutes(45)));
        Epic epic = manager.getEpicById(1);
        LocalDateTime startTime = LocalDateTime.of(2024, 12, 10, 12, 0);
        LocalDateTime endTime = LocalDateTime.of(2024, 12, 13, 15, 15);
        Duration durationOfEpic = Duration.ofMinutes(105);
        assertEquals(startTime, epic.getStartTime(), "Время старта не совпадает!");
        assertEquals(endTime, epic.getEndTime(), "Время конца не совпадает!");
        assertEquals(durationOfEpic, epic.getDuration(), "Длительность не совпадает");
    }

    @Test
    public void shouldCorrectCheckTheIntersection() {
        TaskManager manager = Managers.getDefault();
        List<Task> tasks = new ArrayList<>();

        Task firstTask = new Task("name", "description", Status.NEW, LocalDateTime.of(2024
                , 12, 12, 20, 0), Duration.ofMinutes(45));
        tasks.add(firstTask);
        manager.addTask(firstTask);
        Task taskWithAcross = new Task("name", "description", Status.NEW, LocalDateTime.of(2024
                , 12, 12, 20, 35), Duration.ofMinutes(15));
        tasks.add(taskWithAcross);
        manager.addTask(taskWithAcross);
        assertNotEquals(tasks, manager.getPrioritizedTasks(), "Добавлена задача с пересечением времени!");
    }

    @Test
    public void shouldCorrectCalculatePriorityOfTasks() {
        TaskManager manager = Managers.getDefault();
        List<Task> tasks = new ArrayList<>();
        Task firstTask = new Task("name", "description", Status.NEW, LocalDateTime.of(2024
                , 12, 12, 20, 0), Duration.ofMinutes(45));

        Task secondTask = new Task("name", "description", Status.NEW, LocalDateTime.of(2024
                , 12, 12, 18, 35), Duration.ofMinutes(15));
        Task thirdTask = new Task("name", "description", Status.NEW, LocalDateTime.of(2024
                , 12, 12, 14, 35), Duration.ofMinutes(15));
        tasks.add(firstTask);
        tasks.add(secondTask);
        tasks.add(thirdTask);
        manager.addTask(firstTask);
        manager.addTask(secondTask);
        manager.addTask(thirdTask);
        assertNotEquals(tasks, manager.getPrioritizedTasks());
        tasks.sort(Comparator.comparing(Task::getStartTime));
        assertEquals(tasks, manager.getPrioritizedTasks(), "Задачи сортируются не верно!");


    }

}
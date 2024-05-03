
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskManager {
    private Map<Integer, Task> tasks;
    private Map<Integer, Subtask> subtasks;
    private Map<Integer, Epic> epics;
    private int id = 0;


    public TaskManager() {
        System.out.println("Hello!");
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();
    }


    public void addTask(Task task) {
        if (task.getType().equals("Task")) {
            task.setId(++id);
            tasks.put(task.getId(), task);
        } else if (task.getType().equals("Subtask")) {
            Subtask subtask = (Subtask) task;
            if (epics.containsKey(subtask.getIdOfEpic())) {
                subtask.setId(++id);
                epics.get(subtask.getIdOfEpic()).setSubtasksOfEpic(subtask);
                updateStatusOfEpic(epics.get(subtask.getIdOfEpic())); // UPDATE
                subtasks.put(task.getId(), subtask);
            } else {
                System.out.println("Эпик для этой подзадачи не найден");
            }
        } else if (task.getType().equals("Epic")) {
            task.setId(++id);
            epics.put(task.getId(), (Epic) task);
        }
    }


    public Map<Integer, Task> getTasks() {
        return tasks;
    }

    public Map<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    public Map<Integer, Epic> getEpics() {
        return epics;
    }


    public void removeAllTasks() {
        tasks.clear();
    }

    public void removeAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubtasksOfEpic();
            updateStatusOfEpic(epic);
        }
    }

    public void removeAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    public Task getById(int id) {
        if (tasks.containsKey(id)) {
            return tasks.get(id);
        } else if (subtasks.containsKey(id)) {
            return subtasks.get(id);
        } else if (epics.containsKey(id)) {
            return epics.get(id);
        } else {
            System.out.println("Такой задачи нет...");
            return null;
        }
    }

    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        } else if (subtasks.containsKey(task.getId())) {
            subtasks.put(task.getId(), (Subtask) task);
            updateStatusOfEpic(epics.get(((Subtask) task).getIdOfEpic()));    //UPDATE
        } else if (epics.containsKey(task.getId())) {
            epics.put(task.getId(), (Epic) task);
        } else {
            System.out.println("Задача не найдена...");
        }
    }

    public void removeById(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        } else if (subtasks.containsKey(id)) {
            Epic epic = epics.get(subtasks.get(id).getIdOfEpic());
            epic.removeSubtask(subtasks.get(id));
            updateStatusOfEpic(epic);      // UPDATE
            subtasks.remove(id);
        } else if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            for (Subtask subtask : epic.getSubtasksOfEpic()) {
                subtasks.remove(subtask.getId());
            }
            epics.remove(id);
        } else {
            System.out.println("Задача не найдена...");
        }
    }

    public List<Subtask> getSubtaskByEpic(int idOfEpic) {
        if (epics.containsKey(idOfEpic)) {
            return epics.get(idOfEpic).getSubtasksOfEpic();
        } else {
            System.out.println("Такой эпик не найден...");
            return null;
        }
    }

    private void updateStatusOfEpic(Epic epic) {
        int count = 0;
        for (Subtask subtask : epic.getSubtasksOfEpic()) {
            if (subtask.getStatus() == Status.NEW) {
                count++;
            } else if (subtask.getStatus() == Status.DONE) {
                count--;
            }
        }
        if (count == epic.getSubtasksOfEpic().size()) {
            epic.setStatus(Status.NEW);
        } else if (count == -(epic.getSubtasksOfEpic().size())) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }
}





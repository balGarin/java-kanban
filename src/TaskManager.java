
import java.util.*;

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
        task.setId(++id);
        tasks.put(task.getId(), task);

    }

    public void addSubtask(Subtask subtask) {
        if (epics.containsKey(subtask.getIdOfEpic())) {
            subtask.setId(++id);
            epics.get(subtask.getIdOfEpic()).setSubtasksOfEpic(subtask);
            updateStatusOfEpic(epics.get(subtask.getIdOfEpic())); // UPDATE
            subtasks.put(subtask.getId(), subtask);
        } else {
            System.out.println("Эпик для этой подзадачи не найден");
        }
    }

    public void addEpic(Epic epic) {
        epic.setId(++id);
        epics.put(epic.getId(), epic);
    }

    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
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

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        updateStatusOfEpic(epics.get(subtask.getIdOfEpic()));  //UPDATE
    }

    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    public void removeTaskById(int id) {
        tasks.remove(id);
    }

    public void removeSubtaskById(int id) {
        Epic epic = epics.get(subtasks.get(id).getIdOfEpic());
        epic.removeSubtask(subtasks.get(id));
        updateStatusOfEpic(epic);      // UPDATE
        subtasks.remove(id);
    }

    public void removeEpicById(int id) {
        Epic epic = epics.get(id);
        for (Subtask subtask : epic.getSubtasksOfEpic()) {
            subtasks.remove(subtask.getId());
        }
        epics.remove(id);
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





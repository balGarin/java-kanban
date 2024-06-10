package managers;

import tasks.*;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private Map<Integer, Task> tasks;
    private Map<Integer, Subtask> subtasks;
    private Map<Integer, Epic> epics;
    private HistoryManager historyManager;

    private int id = 0;


    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();
        historyManager = Managers.getDefaultHistory();

    }

    @Override
    public void addTask(Task task) {
        task.setId(++id);
        tasks.put(task.getId(), task);

    }

    @Override
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

    @Override
    public void addEpic(Epic epic) {
        epic.setId(++id);
        epics.put(epic.getId(), epic);
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void removeAllTasks() {
        tasks.clear();
    }

    @Override
    public void removeAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubtasksOfEpic();
            updateStatusOfEpic(epic);
        }
    }

    @Override
    public void removeAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public Task getTaskById(int id) {
        historyManager.addTask(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        historyManager.addTask(subtasks.get(id));
        return subtasks.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        historyManager.addTask(epics.get(id));
        return epics.get(id);
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        updateStatusOfEpic(epics.get(subtask.getIdOfEpic()));  //UPDATE
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    @Override
    public void removeTaskById(int id) {
        tasks.remove(id);
    }

    @Override
    public void removeSubtaskById(int id) {
        Epic epic = epics.get(subtasks.get(id).getIdOfEpic());
        epic.removeSubtask(subtasks.get(id));
        updateStatusOfEpic(epic);      // UPDATE
        subtasks.remove(id);
    }

    @Override
    public void removeEpicById(int id) {
        Epic epic = epics.get(id);
        for (Subtask subtask : epic.getSubtasksOfEpic()) {
            subtasks.remove(subtask.getId());
        }
        epics.remove(id);
    }

    @Override
    public List<Subtask> getSubtaskByEpic(int idOfEpic) {
        if (epics.containsKey(idOfEpic)) {
            return epics.get(idOfEpic).getSubtasksOfEpic();
        } else {
            System.out.println("Такой эпик не найден...");
            return null;
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
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

package managers;

import domain.*;

import java.util.*;
import java.util.stream.Collectors;

public class
InMemoryTaskManager implements TaskManager {
    private Map<Integer, Task> tasks;
    private Map<Integer, Subtask> subtasks;
    private Map<Integer, Epic> epics;
    private HistoryManager historyManager;

    private TreeSet<Task> priorityList;

    private int id = 0;


    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
        priorityList = new TreeSet<>(Comparator.comparing(Task::getStartTime));

    }

    @Override
    public boolean addTask(Task task) {
        if (task.getStartTime() == null) {
            task.setId(++id);
            tasks.put(task.getId(), task);
            return true;
        } else if (task.getStartTime() != null && priorityList.stream()
                .noneMatch(task1 -> checkTheIntersection(task1, task))) {
            priorityList.add(task);
            task.setId(++id);
            tasks.put(task.getId(), task);
            return true;

        } else {
            return false;
        }

    }

    @Override
    public boolean addSubtask(Subtask subtask) {
        if (epics.containsKey(subtask.getIdOfEpic()) && subtask.getStartTime() == null) {
            subtask.setId(++id);
            epics.get(subtask.getIdOfEpic()).setSubtasksOfEpic(subtask);
            updateStatusOfEpic(epics.get(subtask.getIdOfEpic())); // UPDATE
            subtasks.put(subtask.getId(), subtask);
            return true;
        } else if (epics.containsKey(subtask.getIdOfEpic()) && subtask.getStartTime() != null
                && priorityList.stream()
                .noneMatch(subtask1 -> checkTheIntersection(subtask1, subtask))) {
            priorityList.add(subtask);
            subtask.setId(++id);
            epics.get(subtask.getIdOfEpic()).setSubtasksOfEpic(subtask);
            updateStatusOfEpic(epics.get(subtask.getIdOfEpic())); // UPDATE
            subtasks.put(subtask.getId(), subtask);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean addEpic(Epic epic) {
        epic.setId(++id);
        epics.put(epic.getId(), epic);
        return true;

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
        tasks.values().stream().map(task -> {
            historyManager.remove(task.getId());
            return task;
        }).collect(Collectors.toList());
        tasks.clear();

        priorityList = priorityList.stream()
                .filter(task -> !task.getType()
                        .equals(Type.TASK))
                .collect(Collectors.toCollection(()
                        -> new TreeSet<>(Comparator.comparing(Task::getStartTime))));

    }

    @Override
    public void removeAllSubtasks() {
        subtasks.values().stream().map(subtask -> {
            historyManager.remove(subtask.getId());
            return subtask;
        }).collect(Collectors.toList());


        subtasks.clear();

        priorityList = priorityList.stream()
                .filter(subtask -> !subtask.getType()
                        .equals(Type.SUBTASK))
                .collect(Collectors.toCollection(()
                        -> new TreeSet<>(Comparator.comparing(Task::getStartTime))));

        epics.values().stream().map(epic -> {
            epic.clearSubtasksOfEpic();
            return epic;
        }).collect(Collectors.toList());
    }

    @Override
    public void removeAllEpics() {
        subtasks.values().stream().map(subtask -> {
            historyManager.remove(subtask.getId());
            return subtask;
        }).collect(Collectors.toList());
        epics.values().stream().map(epic -> {
            historyManager.remove(epic.getId());
            return epic;
        }).collect(Collectors.toList());
        priorityList = priorityList.stream()
                .filter(subtask -> !subtask.getType()
                        .equals(Type.SUBTASK))
                .collect(Collectors.toCollection(() ->
                        new TreeSet<>(Comparator.comparing(Task::getStartTime))));
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
    public boolean updateTask(Task task) {
        priorityList.removeIf(task1 -> task1.getId() == task.getId());
        if (task.getStartTime() == null) {
            tasks.put(task.getId(), task);
            return true;
        } else if (task.getStartTime() != null && priorityList.stream()
                .noneMatch(task1 -> checkTheIntersection(task1, task))) {
            priorityList.add(task);
            tasks.put(task.getId(), task);
            return true;

        } else {
            return false;
        }
    }

    @Override
    public boolean updateSubtask(Subtask subtask) {

        priorityList.removeIf(subtask1 -> subtask1.getId() == subtask.getId());
        if (epics.containsKey(subtask.getIdOfEpic()) && subtask.getStartTime() == null) {
            subtasks.put(subtask.getId(), subtask);
            epics.get(subtask.getIdOfEpic()).removeSubtask(subtask);
            epics.get(subtask.getIdOfEpic()).setSubtasksOfEpic(subtask);
            updateStatusOfEpic(epics.get(subtask.getIdOfEpic())); // UPDATE
            return true;
        } else if (epics.containsKey(subtask.getIdOfEpic()) && subtask.getStartTime() != null
                && priorityList.stream()
                .noneMatch(subtask1 -> checkTheIntersection(subtask1, subtask))) {
            priorityList.add(subtask);
            subtasks.put(subtask.getId(), subtask);
            epics.get(subtask.getIdOfEpic()).removeSubtask(subtask);
            epics.get(subtask.getIdOfEpic()).setSubtasksOfEpic(subtask);
            updateStatusOfEpic(epics.get(subtask.getIdOfEpic())); // UPDATE
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        return true;
    }

    @Override
    public boolean removeTaskById(int id) {
        if (tasks.containsKey(id)) {
            priorityList.removeIf(task -> task.equals(tasks.get(id)));
            tasks.remove(id);
            historyManager.remove(id);
            return true;
        } else {
            return false;
        }

    }

    @Override
    public boolean removeSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            Epic epic = epics.get(subtasks.get(id).getIdOfEpic());
            epic.removeSubtask(subtasks.get(id));
            updateStatusOfEpic(epic);      // UPDATE
            priorityList.removeIf(subtask -> subtask.equals(subtasks.get(id)));
            subtasks.remove(id);
            historyManager.remove(id);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean removeEpicById(int id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            epic.getSubtasksOfEpic().stream()
                    .map(subtask -> {
                        subtasks.remove(subtask.getId());
                        historyManager.remove(subtask.getId());
                        priorityList.remove(subtask);
                        return subtask;
                    }).collect(Collectors.toList());
            historyManager.remove(id);
            epics.remove(id);
            return true;
        } else {
            return false;
        }
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
        long countNew;
        long countDone;
        countNew = epic.getSubtasksOfEpic().stream()
                .filter(subtask -> subtask.getStatus() == Status.NEW).count();
        countDone = epic.getSubtasksOfEpic().stream()
                .filter(subtask -> subtask.getStatus() == Status.DONE).count();
        if (countNew == 0 && countDone == 0) {
            epic.setStatus(Status.IN_PROGRESS);
        } else if (countNew == epic.getSubtasksOfEpic().size()) {
            epic.setStatus(Status.NEW);
        } else if (countDone == epic.getSubtasksOfEpic().size()) {
            epic.setStatus(Status.DONE);
        } else epic.setStatus(Status.IN_PROGRESS);
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(priorityList);
    }

    private boolean checkTheIntersection(Task task1, Task task2) {
        return task1.getStartTime().isBefore(task2.getEndTime()) && task1.getEndTime().isAfter(task2.getStartTime());
    }


}

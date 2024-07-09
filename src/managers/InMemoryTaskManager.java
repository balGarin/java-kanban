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
    public void addTask(Task task) {
        task.setId(++id);
        tasks.put(task.getId(), task);
        if (task.getStartTime() != null) {
            if (priorityList.stream().filter(task1 -> checkTheIntersection(task1, task)).count() == 0) {
                priorityList.add(task);
            }
        }

    }

    @Override
    public void addSubtask(Subtask subtask) {
        if (epics.containsKey(subtask.getIdOfEpic())) {
            subtask.setId(++id);
            epics.get(subtask.getIdOfEpic()).setSubtasksOfEpic(subtask);
            updateStatusOfEpic(epics.get(subtask.getIdOfEpic())); // UPDATE
            subtasks.put(subtask.getId(), subtask);
            if (subtask.getStartTime() != null) {
                if (priorityList.stream().filter(task1 -> checkTheIntersection(task1, subtask)).count() == 0) {
                    priorityList.add(subtask);
                }
            }
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
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
        if (task.getStartTime() != null) {
            if (priorityList.stream().filter(task1 -> checkTheIntersection(task1, task)).count() == 0) {
                priorityList.add(task);
            }
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        updateStatusOfEpic(epics.get(subtask.getIdOfEpic()));  //UPDATE
        if (subtask.getStartTime() != null) {
            if (priorityList.stream().filter(task1 -> checkTheIntersection(task1, subtask)).count() == 0) {
                priorityList.add(subtask);
            }
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    @Override
    public void removeTaskById(int id) {
        priorityList.removeIf(task -> task.equals(tasks.get(id)));
        tasks.remove(id);
        historyManager.remove(id);

    }

    @Override
    public void removeSubtaskById(int id) {
        Epic epic = epics.get(subtasks.get(id).getIdOfEpic());
        epic.removeSubtask(subtasks.get(id));
        updateStatusOfEpic(epic);      // UPDATE
        priorityList.removeIf(subtask -> subtask.equals(subtasks.get(id)));
        subtasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void removeEpicById(int id) {
        Epic epic = epics.get(id);
        epic.getSubtasksOfEpic().stream().
                map(subtask -> {
                    subtasks.remove(subtask.getId());
                    historyManager.remove(subtask.getId());
                    priorityList.remove(subtask);
                    return subtask;
                }).collect(Collectors.toList());
        historyManager.remove(id);
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

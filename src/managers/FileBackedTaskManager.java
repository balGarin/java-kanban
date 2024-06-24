package managers;

import domain.*;

import java.io.*;
import java.nio.file.Path;
import java.util.*;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private Path file;

    private final String heading = String.format("%s,%s,%s,%s,%s,%s%n", "id", "type", "name", "status", "description",
            "epic");

    public FileBackedTaskManager(Path path) {
        file = path;

    }


    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    void save() {
        IdTaskComparator comparator = new IdTaskComparator();
        TreeSet<Task> allTasks = new TreeSet<>(comparator);
        allTasks.addAll(getTasks());
        allTasks.addAll(getEpics());
        allTasks.addAll(getSubtasks());
        try (FileWriter writer = new FileWriter(file.toFile())) {
            writer.write(heading);
            for (Task task : allTasks) {
                writer.write(taskToString(task));
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при записи в файл!!!");
        }
    }

    private String taskToString(Task task) {
        if (task instanceof Subtask) {

            return String.format("%s,%s,%s,%s,%s,%s%n", task.getId(), task.getType(), task.getName(),
                    task.getStatus(), task.getDescription(), ((Subtask) task).getIdOfEpic());

        } else if (task instanceof Epic) {
            return String.format("%s,%s,%s,%s,%s,%s%n", task.getId(), task.getType(), task.getName(),
                    task.getStatus(), task.getDescription(), ((Epic) task).getSubtasksOfEpic().size());
        } else {
            return String.format("%s,%s,%s,%s,%s%n", task.getId(), task.getType(), task.getName(),
                    task.getStatus(), task.getDescription());

        }
    }


    public static FileBackedTaskManager loadFromFile(Path path) {
        try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
            FileBackedTaskManager manager = new FileBackedTaskManager(path);
            while (reader.ready()) {
                String taskString = reader.readLine();
                if (taskString.contains(",TASK")) {
                    manager.addTask(manager.fromString(taskString));
                } else if (taskString.contains("SUBTASK")) {
                    manager.addSubtask((Subtask) manager.fromString(taskString));
                } else if (taskString.contains("EPIC")) {
                    manager.addEpic((Epic) manager.fromString(taskString));
                }

            }
            return manager;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private Task fromString(String value) {
        if (value.contains(",TASK")) {
            String[] arrayTask = value.split(",");
            Task task = new Task(arrayTask[2], arrayTask[4], Status.valueOf(arrayTask[3]));
            return task;
        } else if (value.contains("SUBTASK")) {
            String[] arrayTask = value.split(",");
            Subtask subtask = new Subtask(arrayTask[2], arrayTask[4], Status.valueOf(arrayTask[3]),
                    Integer.parseInt(arrayTask[5]));
            return subtask;
        } else {
            String[] arrayTask = value.split(",");
            Epic epic = new Epic(arrayTask[2], arrayTask[4], Status.valueOf(arrayTask[3]));
            return epic;
        }

    }
}

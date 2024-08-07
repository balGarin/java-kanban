package managers;

import domain.*;

import java.io.*;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;


public class FileBackedTaskManager extends InMemoryTaskManager {

    private Path file;

    private final String heading = "id,type,name,status,description,epic,startTime,duration,endTime";

    public FileBackedTaskManager(Path path) {
        file = path;

    }


    @Override
    public boolean addTask(Task task) {
        if (super.addTask(task)) {
            save();
            return true;
        }
        return false;

    }

    @Override
    public boolean addSubtask(Subtask subtask) {
        if (super.addSubtask(subtask)) {
            save();
            return true;
        }
        return false;
    }

    @Override
    public boolean addEpic(Epic epic) {
        if (super.addEpic(epic)) {
            save();
            return true;
        }
        return false;

    }

    @Override
    public boolean updateTask(Task task) {
        if (super.updateTask(task)) {
            save();
            return true;
        }
        return false;
    }

    @Override
    public boolean updateSubtask(Subtask subtask) {
        if (super.updateSubtask(subtask)) {
            save();
            return true;
        }
        return false;
    }

    @Override
    public boolean updateEpic(Epic epic) {
        if (super.updateEpic(epic)) {
            save();
            return true;
        }
        return false;
    }

    @Override
    public boolean removeTaskById(int id) {
        if (super.removeTaskById(id)) {
            save();
            return true;
        }
        return false;
    }

    @Override
    public boolean removeSubtaskById(int id) {
        if (super.removeSubtaskById(id)) {
            save();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean removeEpicById(int id) {
        if (super.removeEpicById(id)) {
            save();
            return true;
        } else {
            return false;
        }
    }


    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    void save() {
        try (FileWriter writer = new FileWriter(file.toFile())) {
            writer.write(heading + "\n");
            for (Task task : getTasks()) {
                writer.write(taskToString(task));
            }
            for (Epic epic : getEpics()) {
                writer.write(taskToString(epic));
            }
            for (Subtask subtask : getSubtasks()) {
                writer.write(taskToString(subtask));
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при записи в файл!!!");
        }

    }

    private String taskToString(Task task) {
        if (task.getStartTime() == null) {
            if (task.getType().equals(Type.SUBTASK)) {
                return String.format("%s,%s,%s,%s,%s,%s%n", task.getId(), task.getType(), task.getName(),
                        task.getStatus(), task.getDescription(), ((Subtask) task).getIdOfEpic());
            } else if (task.getType().equals(Type.EPIC)) {
                return String.format("%s,%s,%s,%s,%s,%s%n", task.getId(), task.getType(), task.getName(),
                        task.getStatus(), task.getDescription(), ((Epic) task).getSizeOfSubtasksList());

            } else {
                return String.format("%s,%s,%s,%s,%s%n", task.getId(), task.getType(), task.getName(),
                        task.getStatus(), task.getDescription());
            }
        } else {
            if (task.getType().equals(Type.SUBTASK)) {
                return String.format("%s,%s,%s,%s,%s,%s,%s,%s%n", task.getId(), task.getType(), task.getName(),
                        task.getStatus(), task.getDescription(), ((Subtask) task).getIdOfEpic(), task.getStartTime(),
                        task.getDuration().toMinutes());
            } else if (task.getType().equals(Type.EPIC)) {
                return String.format("%s,%s,%s,%s,%s,%s,%s,%s%n", task.getId(), task.getType(), task.getName(),
                        task.getStatus(), task.getDescription(), ((Epic) task).getSizeOfSubtasksList(),
                        task.getStartTime(), task.getDuration().toMinutes());
            } else {
                return String.format("%s,%s,%s,%s,%s,----,%s,%s%n", task.getId(), task.getType(), task.getName(),
                        task.getStatus(), task.getDescription(), task.getStartTime(), task.getDuration().toMinutes());
            }
        }
    }


    public static FileBackedTaskManager loadFromFile(Path path) {
        try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
            FileBackedTaskManager manager = new FileBackedTaskManager(path);
            int maxId = 0;
            while (reader.ready()) {
                final String taskString = reader.readLine();
                if (taskString.equals(manager.heading)) {
                    continue;
                }
                Task task = manager.fromString(taskString);

                if (maxId < task.getId()) {
                    maxId = task.getId();
                }
                if (task.getType().equals(Type.TASK)) {
                    manager.updateTask(task);
                } else if (task.getType().equals(Type.SUBTASK)) {
                    manager.getEpicById(((Subtask) task).getIdOfEpic()).setSubtasksOfEpic((Subtask) task);
                    manager.updateSubtask((Subtask) task);
                } else {
                    manager.updateEpic((Epic) task);
                }
            }
            manager.setId(maxId);
            return manager;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Task fromString(String value) {
        String[] arrayTask = value.split(",");

        Type type = Type.valueOf(arrayTask[1]);
        try {
            if (type.equals(Type.TASK)) {
                Task task = new Task(arrayTask[2], arrayTask[4], Status.valueOf(arrayTask[3]),
                        LocalDateTime.parse(arrayTask[6]), Duration.ofMinutes(Long.parseLong(arrayTask[7])));
                task.setId(Integer.parseInt(arrayTask[0]));
                return task;
            } else if (type.equals(Type.SUBTASK)) {
                Subtask subtask = new Subtask(arrayTask[2], arrayTask[4], Status.valueOf(arrayTask[3]),
                        Integer.parseInt(arrayTask[5]), LocalDateTime.parse(arrayTask[6]),
                        Duration.ofMinutes(Long.parseLong(arrayTask[7])));
                subtask.setId(Integer.parseInt(arrayTask[0]));
                return subtask;
            } else {
                Epic epic = new Epic(arrayTask[2], arrayTask[4], Status.valueOf(arrayTask[3]));
                epic.setId(Integer.parseInt(arrayTask[0]));
                return epic;
            }
        } catch (IndexOutOfBoundsException e) {
            if (type.equals(Type.TASK)) {
                Task task = new Task(arrayTask[2], arrayTask[4], Status.valueOf(arrayTask[3]));
                task.setId(Integer.parseInt(arrayTask[0]));
                return task;
            } else if (type.equals(Type.SUBTASK)) {
                Subtask subtask = new Subtask(arrayTask[2], arrayTask[4], Status.valueOf(arrayTask[3]),
                        Integer.parseInt(arrayTask[5]));
                subtask.setId(Integer.parseInt(arrayTask[0]));
                return subtask;
            } else {
                Epic epic = new Epic(arrayTask[2], arrayTask[4], Status.valueOf(arrayTask[3]));
                epic.setId(Integer.parseInt(arrayTask[0]));
                return epic;
            }
        }
    }
}

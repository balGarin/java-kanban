package managers;

import domain.Epic;
import domain.Subtask;
import domain.Task;

import java.util.List;

public interface TaskManager {
    void addTask(Task task);

    void addSubtask(Subtask subtask);

    void addEpic(Epic epic);

    List<Task> getTasks();

    List<Subtask> getSubtasks();

    List<Epic> getEpics();

    void removeAllTasks();

    void removeAllSubtasks();

    void removeAllEpics();

    Task getTaskById(int id);

    Subtask getSubtaskById(int id);

    Epic getEpicById(int id);

    void updateTask(Task task);

    void updateSubtask(Subtask subtask);

    void updateEpic(Epic epic);

    void removeTaskById(int id);

    void removeSubtaskById(int id);

    void removeEpicById(int id);

    List<Subtask> getSubtaskByEpic(int idOfEpic);

    List<Task> getHistory();
}

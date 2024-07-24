package managers;

import domain.Epic;
import domain.Subtask;
import domain.Task;

import java.util.List;

public interface TaskManager {

    boolean addTask(Task task);

    boolean addSubtask(Subtask subtask);

    boolean addEpic(Epic epic);

    List<Task> getTasks();

    List<Subtask> getSubtasks();

    List<Epic> getEpics();

    void removeAllTasks();

    void removeAllSubtasks();

    void removeAllEpics();

    Task getTaskById(int id);

    Subtask getSubtaskById(int id);

    Epic getEpicById(int id);

    boolean updateTask(Task task);

    boolean updateSubtask(Subtask subtask);

    boolean updateEpic(Epic epic);

    void removeTaskById(int id);

    void removeSubtaskById(int id);

    void removeEpicById(int id);

    List<Subtask> getSubtaskByEpic(int idOfEpic);

    List<Task> getHistory();

    void setId(int id);

    List<Task> getPrioritizedTasks();
}

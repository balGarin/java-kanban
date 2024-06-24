package managers;

import domain.Task;

import java.util.List;

public interface HistoryManager {


    void addTask(Task task);

    List<Task> getHistory();
}

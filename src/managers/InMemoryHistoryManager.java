package managers;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    List<Task> history;

    public InMemoryHistoryManager() {
        System.out.println("Hello, i am the InMemoryHistoryManager");
        history = new ArrayList<>();
    }

    @Override
    public void addTask(Task task) {
        if (history.size() == 10) {
            history.remove(0);
            history.add(task);
        } else {
            history.add(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }
}

package managers;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final List<Task> history = new ArrayList<>();

    public InMemoryHistoryManager() {
        System.out.println("Hello, i am the InMemoryHistoryManager");

    }

    @Override
    public void addTask(Task task) {
        if (task == null) {
            return;
        }
        if (history.size() >= 10) {
            history.remove(0);
            history.add(task);
        } else {
            history.add(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history);
    }
}

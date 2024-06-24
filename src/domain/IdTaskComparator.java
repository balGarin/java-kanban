package domain;

import java.util.Comparator;

public class IdTaskComparator implements Comparator<Task> {
    @Override
    public int compare(Task task1, Task task2) {
        return task1.getId() - task2.getId();
    }
}

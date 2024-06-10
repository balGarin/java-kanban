package managers;

import domain.Node;
import domain.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {


    private final Map<Integer, Node<Task>> historyCheck = new HashMap<>();
    private final CustomLinkedList<Task> history = new CustomLinkedList<>();

    public InMemoryHistoryManager() {


    }

    @Override
    public void addTask(Task task) {
        if (historyCheck.containsKey(task.getId())) {
            removeNode(historyCheck.get(task.getId()));
            historyCheck.remove(task.getId());
        }
        history.addLust(task);
        historyCheck.put(task.getId(), history.tail);

    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history.getTasks());
    }

    @Override
    public void remove(int id) {
        if (historyCheck.containsKey(id)) {
            removeNode(historyCheck.get(id));
            historyCheck.remove(id);
        }
    }

    private void removeNode(Node<Task> node) {
        if (history.size == 1) {
            history.head = null;
            history.tail = null;
            history.size = 0;
            return;
        }
        Node<Task> prev = node.prev;
        Node<Task> next = node.next;
        if (prev == null) {
            history.head = next;
            history.head.prev = null;
            history.head.next = next.next;
        } else if (next == null) {
            history.tail = prev;
            history.tail.next = null;
            history.tail.prev = prev.prev;
        } else {
            prev.next = next;
            next.prev = prev;
        }
        history.size--;
    }

    private static class CustomLinkedList<T> {
        private Node<T> head;
        private Node<T> tail;
        private int size = 0;


        public void addLust(T element) {
            Node<T> oldTail = tail;
            Node<T> newNode = new Node<>(tail, element, null);
            if (tail == null) {
                tail = newNode;
                head = newNode;
            } else {
                tail = newNode;
                oldTail.next = tail;
            }
            size++;

        }

        public List<Task> getTasks() {
            List<Task> sortHistory = new ArrayList<>();
            if (head != null) {
                Node<T> curNode = head;
                sortHistory.add((Task) curNode.date);
                for (int i = 0; i < size - 1; i++) {
                    curNode = curNode.next;
                    sortHistory.add((Task) curNode.date);
                }
                return sortHistory;

            }
            return sortHistory;
        }
    }
}

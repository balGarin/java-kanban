package managers;

import domain.Node;
import domain.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private Node<Task> head;
    private Node<Task> tail;
    private int size = 0;

    private final Map<Integer, Node<Task>> history = new HashMap<>();


    @Override
    public void addTask(Task task) {
        if (task == null) {
            return;
        }
        if (history.containsKey(task.getId())) {
            removeNode(history.get(task.getId()));
        }
        history.put(task.getId(), addLust(task));
    }

    @Override
    public List<Task> getHistory() {
        System.out.println(history);
        return new ArrayList<>(getTasks());
    }

    @Override
    public void remove(int id) {
        if (history.containsKey(id)) {
            removeNode(history.get(id));
            history.remove(id);
        }
    }

    public Node<Task> addLust(Task task) {
        Node<Task> oldTail = tail;
        Node<Task> newNode = new Node<>(tail, task, null);
        if (tail == null) {
            tail = newNode;
            head = newNode;
        } else {
            tail = newNode;
            oldTail.next = tail;
        }
        size++;
        return newNode;

    }

    private void removeNode(Node<Task> node) {
        if (size == 1) {
            head = null;
            tail = null;
            size = 0;
            return;
        }
        Node<Task> prev = node.prev;
        Node<Task> next = node.next;
        if (prev == null) {
            head = next;
            head.prev = null;
            head.next = next.next;
        } else if (next == null) {
            tail = prev;
            tail.next = null;
            tail.prev = prev.prev;
        } else {
            prev.next = next;
            next.prev = prev;
        }
        size--;
    }

    public List<Task> getTasks() {
        List<Task> sortHistory = new ArrayList<>();
        if (head != null) {
            Node<Task> curNode = head;
            sortHistory.add(curNode.date);
            for (int i = 0; i < size - 1; i++) {
                curNode = curNode.next;
                sortHistory.add(curNode.date);
            }
            return sortHistory;

        }
        return sortHistory;
    }


}

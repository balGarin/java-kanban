package domain;

public class Node<T> {

    public T date;
    public Node<T> next;
    public Node<T> prev;

    public Node(Node<T> prev, T date, Node<T> next) {
        this.date = date;
        this.next = next;
        this.prev = prev;

    }
}

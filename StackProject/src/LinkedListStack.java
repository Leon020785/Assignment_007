public class LinkedListStack<E> implements Stack<E> {
    private Node<E> top = null;
    private int size = 0;

    private static class Node<E> {
        E value;
        Node<E> next;

        Node(E value, Node<E> next) {
            this.value = value;
            this.next = next;
        }
    }

    @Override
    public void push(E value) {
        top = new Node<>(value, top);
        size++;
    }

    @Override
    public E pop() {
        if (top == null) return null;
        E result = top.value;
        top = top.next;
        size--;
        return result;
    }

    @Override
    public E top() {
        return top != null ? top.value : null;
    }

    @Override
    public int size() {
        return size;
    }
}

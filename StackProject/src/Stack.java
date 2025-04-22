public interface Stack<E> {
    void push(E value);
    E pop();
    E top();
    int size();

    default boolean isEmpty() {
        return size() == 0;
    }
}

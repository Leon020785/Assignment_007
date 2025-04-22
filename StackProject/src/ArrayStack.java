public class ArrayStack<E> implements Stack<E> {
    private Object[] data = new Object[100];
    private int top = 0;

    @Override
    public void push(E value){
        data[top++] = value;
    }

    @Override
    @SuppressWarnings("unchecked")
    public E pop(){
        return (E) data[--top];
    }
    @Override
    @SuppressWarnings("unchecked")
    public E top(){
        return (E) data[top-1];
    }
    @Override
    public int size(){
        return top;
    }
}

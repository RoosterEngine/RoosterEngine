package Utilities;

/**
 * documentation
 * User: davidrusu
 * Date: 28/02/13
 * Time: 5:28 PM
 */
public class UnorderedArrayList<T> implements List<T> {
    private static final int DEFAULT_INIT_CAPACITY = 16;
    private static final double DEFAULT_GROW_RATE = 1.5;
    private double growRate;
    private Object[] list;
    private int size;

    public UnorderedArrayList() {
        this(DEFAULT_INIT_CAPACITY, DEFAULT_GROW_RATE);
    }

    public UnorderedArrayList(int initialCapacity) {
        this(initialCapacity, DEFAULT_GROW_RATE);
    }

    public UnorderedArrayList(int initCapacity, double growRate) {
        this.growRate = growRate;
        list = new Object[initCapacity];
        size = 0;
    }

    @Override
    public void add(T element) {
        if (size == list.length) {
            grow();
        }

        list[size] = element;
        size++;
    }

    @Override
    public T get(int index) {
        assert index < size;

        return (T) list[index];
    }

    @Override
    public boolean remove(T element) {
        for (int i = 0; i < size; i++) {
            if (list[i] == element) {
                size--;
                list[i] = list[size];
                list[size] = null;
                return true;
            }
        }
        return false;
    }

    @Override
    public T remove(int index) {
        assert index < size;

        T removed = (T) list[index];
        size--;
        list[index] = list[size];
        return removed;
    }

    @Override
    public boolean contains(T element) {
        for (int i = 0; i < size; i++) {
            if (list[i] == element) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void clear() {
        for (int i = 0; i < size; i++) {
            list[i] = null;
        }
        size = 0;
    }

    @Override
    public int size() {
        return size;
    }

    private void grow() {
        Object[] temp = list;
        list = new Object[(int) (list.length * growRate) + 1];
        System.arraycopy(temp, 0, list, 0, temp.length);
    }
}

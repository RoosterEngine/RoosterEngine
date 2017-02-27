package Utilities;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Order is not preserved as empty spots get filled with the last element.
 *
 * @author davidrusu
 */
public class UnorderedArrayList<T> implements List<T> {
    private static final int DEFAULT_INIT_CAPACITY = 16;
    private static final double DEFAULT_GROW_RATE = 1.5;
    private double growRate;
    private T[] list;
    private int size = 0;

    public UnorderedArrayList() {
        this(DEFAULT_INIT_CAPACITY, DEFAULT_GROW_RATE);
    }

    public UnorderedArrayList(int initialCapacity) {
        this(initialCapacity, DEFAULT_GROW_RATE);
    }

    public UnorderedArrayList(int initCapacity, double growRate) {
        this.growRate = growRate;
        list = (T[]) new Object[initCapacity];
    }

    @Override
    public void add(T element) {
        if (size == list.length) {
            expandCapacity();
        }
        list[size] = element;
        size++;
    }

    @Override
    public T get(int index) {
        assert index < size;

        return list[index];
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
    public int forEachConditionallyRemove(Predicate<T> condition) {
        int numRemovedItems = 0;
        for (int i = 0; i < size; i++) {
            if (condition.test(list[i])) {
                numRemovedItems++;
                remove(i);
                //The last element replaced this position so we need to test this position again
                i--;
            }
        }
        return numRemovedItems;
    }

    @Override
    public T remove(int index) {
        assert index < size;

        T removed = (T) list[index];
        size--;
        list[index] = list[size];
        list[size] = null;
        return removed;
    }

    @Override
    public void forEach(Consumer<T> consumer) {
        for (int i = 0; i < size; i++) {
            consumer.accept(list[i]);
        }
    }

    @Override
    public int getElementIndex(T element) {
        for (int i = 0; i < size; i++) {
            if (element == list[i]) {
                return i;
            }
        }
        return -1;
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

    private void expandCapacity() {
        Object[] temp = list;
        int newCapacity = (int) (list.length * growRate) + 1;
        list = (T[]) new Object[newCapacity];
        System.arraycopy(temp, 0, list, 0, temp.length);
    }
}

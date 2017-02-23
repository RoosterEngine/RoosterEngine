package Utilities;


/**
 * documentation
 *
 * @author davidrusu
 */
public class ArraySet<T> implements Iterator<T> {
    private UnorderedArrayList<T> list;
    private int index = -1;

    public ArraySet() {
        list = new UnorderedArrayList<>();
    }

    public ArraySet(int initialCapacity) {
        list = new UnorderedArrayList<>(initialCapacity);
    }

    public ArraySet(int initCapacity, double growRate) {
        list = new UnorderedArrayList<>(initCapacity, growRate);
    }

    public void add(T element) {
        list.add(element);
    }

    @Override
    public boolean removeAndResetIterator(T element) {
        index = -1;
        return list.remove(element);
    }

    @Override
    public void resetIterator() {
        index = -1;
    }

    @Override
    public boolean hasNext() {
        return index < list.size();
    }

    @Override
    public T getNext() {
        index++;
        return list.get(index);
    }

    @Override
    public void remove() {
        list.remove(index);
        index--;
    }

    public boolean contains(T element) {
        return list.contains(element);
    }

    public void clear() {
        list.clear();
        index = -1;
    }

    public int size() {
        return list.size();
    }
}

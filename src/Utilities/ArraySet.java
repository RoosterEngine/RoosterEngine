package Utilities;


/**
 * documentation
 * User: davidrusu
 * Date: 16/03/13
 * Time: 5:57 PM
 */
public class ArraySet<T> {
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

    /**
     * Resets the iterator even if the element is not in the list
     * @param element The element to remove
     * @return true if the element was found and removed, false otherwise
     */
    public boolean removeAndResetIterator(T element) {
        index = -1;
        return list.remove(element);
    }

    public void resetIterator() {
        index = -1;
    }

    public boolean hasNext() {
        return index < list.size();
    }

    public T getNext() {
        index++;
        return list.get(index);
    }

    /**
     * Removes the element that was just returned from the getNext() method.
     * If getNext() has not been called since the iterator was initialized or reset,
     * an ArrayOutOfBounds exception will be thrown.
     */
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

package Utilities;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * documentation
 *
 * @author davidrusu
 */
public class HashSet<T> {
    private static final int INITIAL_SIZE = 16; //this must be a power of 2
    private static final double LOAD_FACTOR = 0.75;
    private int maxUseableSize;
    private UnorderedArrayList<T>[] items = (UnorderedArrayList<T>[]) new
            UnorderedArrayList[INITIAL_SIZE];
    private int numItems = 0;

    //not allowing initial size as it must be a power of 2 and we don't want to check / fix that
    public HashSet() {
        createLists();
    }

    private void createLists() {
        maxUseableSize = (int) (items.length * LOAD_FACTOR);
        for (int i = 0; i < items.length; i++) {
            items[i] = new UnorderedArrayList<T>(4);
        }
    }

    public void add(T item) {
        if (numItems >= maxUseableSize) {
            expand();
        }
        UnorderedArrayList<T> list = items[getIndex(item)];
        if (list.getElementIndex(item) < 0) {
            list.add(item);
            numItems++;
        }
    }

    public void remove(T item) {
        if (items[getIndex(item)].remove(item)) {
            numItems--;
        }
    }

    /**
     * Iterates through the elements in this set and removes each element that passes the condition.
     *
     * @param condition The condition that controls if an element should be removed
     * @return The number of items that were removed
     */
    public int forEachConditionallyRemove(Predicate<T> condition) {
        int numRemovedItems = 0;
        for (int i = 0; i < items.length; i++) {
            numRemovedItems += items[i].forEachConditionallyRemove(condition);
        }
        numItems -= numRemovedItems;
        return numRemovedItems;
    }

    public void clear() {
        numItems = 0;
        for (int i = 0; i < items.length; i++) {
            items[i].clear();
        }
    }

    public boolean contains(T item) {
        return items[getIndex(item)].getElementIndex(item) >= 0;
    }

    /**
     * Iterates through the elements in this set notifying the consumer for each one.
     *
     * @param consumer The consumer
     */
    public void forEach(Consumer<T> consumer) {
        for (int i = 0; i < items.length; i++) {
            items[i].forEach(consumer);
        }
    }

    private int getIndex(T item) {
        int h = item.hashCode();
        h ^= (h >>> 20) ^ (h >>> 12);
        h ^= (h >>> 7) ^ (h >>> 4);
        return h & (items.length - 1);
    }

    private void expand() {
        UnorderedArrayList<T>[] oldItems = items;
        //this needs to be a 2 so that the size remains a power of 2
        items = (UnorderedArrayList<T>[]) new UnorderedArrayList[oldItems.length * 2];
        createLists();
        numItems = 0;
        for (int i = 0; i < oldItems.length; i++) {
            UnorderedArrayList<T> list = items[i];
            int listSize = list.size();
            for (int j = 0; j < listSize; j++) {
                add(list.get(j));
            }
        }
    }
}

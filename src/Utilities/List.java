package Utilities;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * List interface.
 *
 * @author davidrusu
 */
public interface List<T> {

    /**
     * Adds the element to the list.
     *
     * @param element the element to add to the list
     */
    void add(T element);

    /**
     * Returns the element at the specified index.
     *
     * @param index the index of the element
     * @return the element at the index
     */
    T get(int index);

    /**
     * Removes the specified element from the list.
     *
     * @param element the element to remove from the list
     * @return true if the entity was found and removed successfully, false otherwise
     */
    boolean remove(T element);

    /**
     * Removes the element at the specified index.
     *
     * @param index the index of the element to remove
     * @return the element that was removed
     * @throws IndexOutOfBoundsException if the index is greater than size
     */
    T remove(int index) throws IndexOutOfBoundsException;

    /**
     * Iterates through the items in this list and removes each item that passes the condition.
     *
     * @param condition The condition that controls if an element should be removed
     * @return The number of items that were removed
     */
    int forEachConditionallyRemove(Predicate<T> condition);

    /**
     * Iterates through the elements in this list notifying the consumer for each one.
     *
     * @param consumer The consumer
     */
    void forEach(Consumer<T> consumer);

    /**
     * Returns the index of the element otherwise -1 if the element is not present in the list.
     *
     * @param element the element to check for
     * @return The index of the element if contained in the list otherwise -1
     */
    int getElementIndex(T element);

    /**
     * Clears the list.
     */
    void clear();

    /**
     * @return The size of the list.
     */
    int size();
}

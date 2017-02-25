package Utilities;

/**
 * List interface.
 *
 * @author davidrusu
 */
public interface List<T> {

    /**
     * Adds the element to the list
     *
     * @param element the element to add to the list
     */
    public void add(T element);

    /**
     * Returns the element at the specified index
     *
     * @param index the index of the element
     * @return the element at the index
     */
    public T get(int index);

    /**
     * Removes the specified element from the list
     *
     * @param element the element to remove from the list
     * @return true if the entity was found and removed successfully, false otherwise
     */
    public boolean remove(T element);

    /**
     * Removes the element at the specified index
     *
     * @param index the index of the element to remove
     * @return the element that was removed
     * @throws IndexOutOfBoundsException if the index is greater than size
     */
    public T remove(int index) throws IndexOutOfBoundsException;

    /**
     * Checks if the list contains the specified element;
     *
     * @param element the element to check for
     * @return true if the entity was found, false otherwise
     */
    public boolean contains(T element);

    /**
     * Clears the list
     */
    public void clear();

    /**
     * Returns the size of the list
     *
     * @return the size of the list
     */
    public int size();
}

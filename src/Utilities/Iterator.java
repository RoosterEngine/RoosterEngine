package Utilities;

/**
 * documentation
 * User: davidrusu
 * Date: 17/03/13
 * Time: 3:26 PM
 */
public interface Iterator<T> {

    /**
     * Resets the iterator even if the element is not in the list
     * @param element The element to remove
     * @return true if the element was found and removed, false otherwise
     */
    public boolean removeAndResetIterator(T element);

    public void resetIterator();

    /**
     * Checks to see if there are anymore elements
     * @return true if there are more elements
     */
    public boolean hasNext();

    /**
     * Returns the next element
     * @return the next element
     */
    public T getNext();

    /**
     * Removes the element that was just returned from the getNext() method.
     * If getNext() has not been called since the iterator was initialized or reset,
     * an ArrayOutOfBounds exception will be thrown.
     */
    public void remove();
}

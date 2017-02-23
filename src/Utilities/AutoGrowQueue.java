package Utilities;

import gameengine.math.Utils;

/**
 * A queue that automatically grows as needed.
 */
public class AutoGrowQueue<T> {
    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    /**
     * The values in the queue.
     */
    private T[] queue;
    private int headIndex = 0, tailIndex = 0, size = 0;
    /**
     * Used for finding the remainder efficiently since x % powerOf2 == x & (powerOf2 - 1).
     */
    private int modMask;

    /**
     * Creates an AutoGrowQueue instance with the default initial capacity ({@value
     * #DEFAULT_INITIAL_CAPACITY}).
     */
    public AutoGrowQueue() {
        this(DEFAULT_INITIAL_CAPACITY);
    }

    /**
     * Creates an AutoGrowQueue instance with at least the specified initial capacity.
     *
     * @param initialCapacity The minimum initial capacity
     */
    public AutoGrowQueue(int initialCapacity) {
        int capacity = Utils.nextPowerOf2(initialCapacity);
        queue = (T[]) new Object[capacity];
        modMask = capacity - 1;
    }

    /**
     * @return The number of entries in the queue.
     */
    public int size() {
        return size;
    }

    /**
     * Adds another entry to the queue.
     *
     * @param value The value to be added
     */
    public void enqueue(T value) {
        if (size == queue.length) {
            expandCapacity();
        }
        queue[tailIndex] = value;
        tailIndex = (tailIndex + 1) & modMask;
        size++;
    }

    /**
     * @return The entry at the beginning of the queue.
     */
    public T peek() {
        assert size > 0;

        return queue[headIndex];
    }

    /**
     * Removes and returns the entry at the beginning of the queue.
     *
     * @return The entry from the beginning of the queue
     */
    public T dequeue() {
        assert size > 0;

        T result = queue[headIndex];
        queue[headIndex] = null;//allow object to be garbage collected
        headIndex = (headIndex + 1) & modMask;
        size--;
        return result;
    }

    /**
     * Expands the queue capacity.
     */
    private void expandCapacity() {
        T[] oldQueue = queue;
        //important: the capacity must always be a power of 2 for the mod mask to be accurate
        int capacity = queue.length << 1; //double the size
        modMask = capacity - 1;
        queue = (T[]) new Object[capacity];
        int firstPart = size - headIndex;
        System.arraycopy(oldQueue, headIndex, queue, 0, firstPart);
        System.arraycopy(oldQueue, 0, queue, firstPart, headIndex);
        headIndex = 0;
        tailIndex = size;
    }
}

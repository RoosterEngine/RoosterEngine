package Utilities;

import gameengine.math.Utils;

/**
 * A queue of primitive long values.
 */
public class LongQueue {
    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    /**
     * The values in the queue.
     */
    private long[] queue;
    private int headIndex = 0, tailIndex = 0;
    private int size = 0;
    /**
     * Used for finding the remainder efficiently since x % powerOf2 == x & (powerOf2 - 1).
     */
    private int modMask;

    /**
     * Creates a LongQueue instance with the default initial capacity ({@value
     * #DEFAULT_INITIAL_CAPACITY}).
     */
    public LongQueue() {
        this(DEFAULT_INITIAL_CAPACITY);
    }

    /**
     * Creates a LongQueue instance with at least the specified initial capacity.
     *
     * @param initialCapacity The minimum initial capacity
     */
    public LongQueue(int initialCapacity) {
        int capacity = Utils.nextPowerOf2(initialCapacity);
        queue = new long[capacity];
        modMask = capacity - 1;
    }

    /**
     * Clears the queue.
     */
    public void clear() {
        headIndex = 0;
        tailIndex = 0;
        size = 0;
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
     * @param num The value to be added
     */
    public void enqueue(long num) {
        if (size == queue.length) {
            expandCapacity();
        }
        queue[tailIndex] = num;
        tailIndex = (tailIndex + 1) & modMask;
        size++;
    }

    /**
     * @return The entry at the beginning of the queue.
     */
    public long peek() {
        assert size > 0;

        return queue[headIndex];
    }

    /**
     * Removes and returns the entry at the beginning of the queue.
     *
     * @return The entry from the beginning of the queue
     */
    public long dequeue() {
        assert size > 0;

        long result = queue[headIndex];
        headIndex = (headIndex + 1) & modMask;
        size--;
        return result;
    }

    /**
     * Expands the queue capacity.
     */
    private void expandCapacity() {
        long[] oldQueue = queue;
        //important: the capacity must always be a power of 2 for the mod mask to be accurate
        int capacity = queue.length << 1; //double the size
        modMask = capacity - 1;
        queue = new long[capacity];
        int firstPart = size - headIndex;
        System.arraycopy(oldQueue, headIndex, queue, 0, firstPart);
        System.arraycopy(oldQueue, 0, queue, firstPart, headIndex);
        headIndex = 0;
        tailIndex = size;
    }
}

package Utilities;

/**
 * A stack for integer primitives.
 */
public class IntStack {
    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    private static final double DEFAULT_EXPANSION_FACTOR = 1.5;

    /**
     * The values in the stack.
     */
    private int[] stack;
    private int size = 0;

    /**
     * Creates a new IntStack instance with the default initial capacity ({@value
     * #DEFAULT_INITIAL_CAPACITY}).
     */
    public IntStack() {
        this(DEFAULT_INITIAL_CAPACITY);
    }

    /**
     * Creates a new IntStack instance with the specified initial capacity.
     *
     * @param initialCapacity The minimum initial capacity
     */
    public IntStack(int initialCapacity) {
        assert initialCapacity >= 0;

        stack = new int[initialCapacity];
    }

    /**
     * Clears the stack.
     */
    public void clear() {
        size = 0;
    }

    /**
     * @return The number of entries in the stack.
     */
    public int size() {
        return size;
    }

    /**
     * Adds another entry to the stack.
     *
     * @param num The value to be added
     */
    public void push(int num) {
        if (size == stack.length) {
            expandCapacity();
        }
        stack[size] = num;
        size++;
    }

    /**
     * @return The entry at the beginning of the stack.
     */
    public long peek() {
        assert size > 0;

        return stack[size - 1];
    }

    /**
     * Removes and returns the entry at the top of the stack.
     *
     * @return The entry from the top of the stack
     */
    public int pop() {
        assert size > 0;

        size--;
        return stack[size];
    }

    /**
     * Expands the stack capacity.
     */
    private void expandCapacity() {
        int[] oldQueue = stack;
        int capacity = stack.length << 1; //double the size
        stack = new int[capacity];
        System.arraycopy(oldQueue, 0, stack, 0, size);
    }
}

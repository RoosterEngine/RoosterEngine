package Utilities;

/**
 * A queue of primitive long values.
 * <p>
 * Created by Dan on 10/19/2016.
 */
public class LongQueue {
   public static final int DEFAULT_INITIAL_CAPACITY = 16;
   /**
    * The values in the queue.
    */
   private long[] queue;
   private int headIndex = 0, tailIndex = 0, size = 0;
   /**
    * Used for finding the remainder efficiently since x % powerOf2 == x & (powerOf2 - 1).
    */
   private int modMask;

   /**
    * Creates a LongQueue instance with the default initial capacity.
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
      int capacity = nextPowerOf2(initialCapacity);
      queue = new long[capacity];
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
    * Expancds the queue capacity.
    */
   private void expandCapacity() {
      long[] oldQueue = queue;
      int capacity = size << 1; //double the size
      modMask = capacity - 1;
      queue = new long[capacity];
      int firstPart = size - headIndex;
      System.arraycopy(oldQueue, headIndex, queue, 0, firstPart);
      System.arraycopy(oldQueue, 0, queue, firstPart, headIndex);
      headIndex = 0;
      tailIndex = size;
   }

   /**
    * Returns the next power of 2 that is greater or equal to the specified value.
    *
    * @param n The value
    * @return The next power of power of 2
    */
   private int nextPowerOf2(int n) {
      assert n > 0;

      n--;
      n |= n >>> 1;
      n |= n >>> 2;
      n |= n >>> 4;
      n |= n >>> 8;
      n |= n >>> 16;
      return n + 1;
   }
}

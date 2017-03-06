package Utilities;

import java.util.Arrays;
import java.util.function.IntConsumer;

/**
 * A set that stores non-negative integer IDs for IDs that are expected to be small.  The add,
 * remove, & contains operations are performed in expected O(1) time, forEach performs in O(size)
 * time, clear operates in O(max(ID)) time.  The space complexity is O(max(ID)) and is very
 * efficient when the IDs are dense.
 */
public class DenseIDSet {
    private static final double DEFAULT_EXPANSION_FACTOR = 1.5;

    int size = 0;
    /**
     * Stores the IDs consecutively;
     */
    int[] ids;
    /**
     * Stores the locations into the ids array such that ids[location[ID]] = ID.
     */
    int[] locations;

    /**
     * Creates a new DenseIDSet instance.
     *
     * @param initialCapacity The initial capacity for the expected number of IDs that will be
     *                        added
     */
    public DenseIDSet(int initialCapacity) {
        this(16, initialCapacity);
    }

    /**
     * Creates a new DenseIDSet instance.  The maximum expected ID is not an upper bound as it's
     * only used as a starting point to reduce the number of times internal structures are resized.
     *
     * @param maxExpectedID   The maximum ID that is expected to be added to this set
     * @param initialCapacity The initial capacity for the expected number of IDs that will be
     *                        added
     */
    public DenseIDSet(int maxExpectedID, int initialCapacity) {
        assert maxExpectedID >= 0;
        assert initialCapacity >= 0;

        ids = new int[initialCapacity];
        locations = new int[maxExpectedID + 1];
        Arrays.fill(locations, Integer.MAX_VALUE);
    }

    /**
     * Checks if the specified ID is in the set.
     *
     * @param id The ID to check, id >= 0
     * @return True if the ID is in the set
     */
    public boolean contains(int id) {
        assert id >= 0;

        return locations[id] < size;
    }

    /**
     * Adds the specified ID to the set.
     *
     * @param id The ID to be added, id >= 0
     */
    public void add(int id) {
        assert id >= 0;

        if (size >= ids.length) {
            expandIdCapacity();
        }
        if (id >= locations.length) {
            expandLocationCapacity(id + 1);
        }
        ids[size] = id;
        locations[id] = size;
        size++;
    }

    /**
     * Removes the specified ID from the set.
     *
     * @param id The ID to be removed, id >= 0
     */
    public void remove(int id) {
        assert id >= 0;

        size--;
        //move the last ID in the spot of the removed ID
        int lastID = ids[size];
        int location = locations[id];
        ids[location] = lastID;
        locations[lastID] = location;

        //Make the location of the removed ID point out of range
        locations[id] = Integer.MAX_VALUE;
    }

    /**
     * Clears the set.
     */
    public void clear() {
        if (size > locations.length / 8) {
            Arrays.fill(locations, Integer.MAX_VALUE);
        } else {
            //it's mostly empty so just clean up the small number of affected entries
            for (int i = 0; i < size; i++) {
                locations[ids[i]] = Integer.MAX_VALUE;
            }
        }
        size = 0;
    }

    /**
     * Notifies the consumer for each ID that is present in the set.
     *
     * @param consumer The consumer to be notified
     */
    public void forEach(IntConsumer consumer) {
        //notice the efficient traversal
        for (int i = 0; i < size; i++) {
            consumer.accept(ids[i]);
        }
    }

    /**
     * Expands the capacity of the array that contains the consecutive list of IDs.
     */
    private void expandIdCapacity() {
        int[] replacement = new int[(int) (ids.length * DEFAULT_EXPANSION_FACTOR)];
        System.arraycopy(ids, 0, replacement, 0, ids.length);
        ids = replacement;
    }

    /**
     * Expands the capacity of the array that stores the locations of the IDs.
     *
     * @param minSize The minimum size of the expanded array
     */
    private void expandLocationCapacity(int minSize) {
        int newSize = Math.max(minSize, (int) (locations.length * DEFAULT_EXPANSION_FACTOR));
        int[] replacement = new int[newSize];
        System.arraycopy(locations, 0, replacement, 0, locations.length);
        Arrays.fill(replacement, locations.length, replacement.length - 1, Integer.MAX_VALUE);
    }
}

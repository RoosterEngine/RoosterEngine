package Utilities;

/**
 * Responsible for generating and recycling IDs.
 */
public class IDGenerator {
    private int maxID = -1;
    private IntStack recycledIDs = new IntStack();

    /**
     * @return A recycled ID if available otherwise a new ID.
     */
    public int generateID() {
        if (recycledIDs.size() > 0) {
            return recycledIDs.pop();
        }

        maxID++;
        return maxID;
    }

    /**
     * Recycles this ID so it can be re-used.
     *
     * @param id The ID to be recycled
     */
    public void recycleID(int id) {
        recycledIDs.push(id);
    }

    /**
     * Resets the ID to start all over.
     */
    public void reset() {
        maxID = -1;
        recycledIDs.clear();
    }

    /**
     * Returns the max ID that has ever been created (even if it has since been recycled).
     *
     * @return The maximum ID that was ever created or -1 if none have been created
     */
    public int getMaxID() {
        return maxID;
    }
}

package bricklets;

/**
 * @author david
 */
public class CollisionData {
    private static final double NO_COLLISION = Double.MAX_VALUE, DEFAULT_ENTRY = -Double.MAX_VALUE;
    private Vector2D collisionNormal, overlapNormal, tempOverlapNormal;
    private double entryTime, leaveTime, overlapTime, overlapVelocity, tempOverlapVelocity;
    private double min, max;
    private boolean overlapUpdated = false;

    public CollisionData() {
        collisionNormal = new Vector2D();
        overlapNormal = new Vector2D();
        tempOverlapNormal = new Vector2D();
        clear();
    }

    public Vector2D getCollisionNormal() {
        return collisionNormal;
    }

    public void setCollisionNormal(Vector2D collisionNormal) {
        this.collisionNormal.set(collisionNormal);
    }

    public Vector2D getOverlapNormal() {
        return overlapNormal;
    }

    public double getEntryTime() {
        return entryTime;
    }

    public double getOverlapVelocity() {
        return overlapVelocity;
    }

    public void setTempOverlapVelocity(double velocity) {
        tempOverlapVelocity = velocity;
    }

    private void setTempOverlapData(double x, double y, double velocity, double time) {
        overlapUpdated = true;
        tempOverlapNormal.set(x, y);
        tempOverlapVelocity = velocity;
        overlapTime = time;
    }

    public void updateTempOverlapData(double dist, double velocity, double normalX, double normalY) {
        if (dist > 0) {
            double time = dist / Math.abs(velocity);
            if (time < overlapTime) {
                setTempOverlapData(normalX, normalY, velocity, time);
            }
        }
    }

    public void clearMinMax() {
        min = Double.MAX_VALUE;
        max = -Double.MAX_VALUE;
    }

    public void updateMinMax(double value) {
        min = Math.min(min, value);
        max = Math.max(max, value);
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public void updateOverlapData() {
        if (overlapUpdated) {
            overlapNormal.set(tempOverlapNormal);
            overlapVelocity = tempOverlapVelocity;
        }
    }

    public void updateEntryTime(double time, Vector2D normal) {
        updateEntryTime(time, normal.getX(), normal.getY());
    }

    public void updateLeaveTime(double time) {
        if (time < leaveTime) {
            leaveTime = time;
        }
    }

    public void updateEntryTime(double time, double normalX, double normalY) {
        if (time > entryTime) {
            entryTime = time;
            collisionNormal.set(normalX, normalY);
        }
    }

    public void resetOverlapUpdated() {
        overlapUpdated = false;
    }

    public void clear() {
        collisionNormal.clear();
        overlapNormal.clear();
        tempOverlapNormal.clear();
        entryTime = DEFAULT_ENTRY;
        leaveTime = NO_COLLISION;
        overlapTime = NO_COLLISION;
        overlapVelocity = 0;
        tempOverlapVelocity = 0;
        overlapUpdated = false;
        min = Double.MAX_VALUE;
        max = -Double.MAX_VALUE;
    }

    public boolean isIntersectingAndTravellingTowardsEachOther() {
        return entryTime == DEFAULT_ENTRY && overlapVelocity < 0;
    }

    public boolean willCollisionHappen(double maxTime) {
        return entryTime <= maxTime && entryTime <= leaveTime && entryTime != DEFAULT_ENTRY;
    }

    public boolean isCollisionNotPossible() {
        return entryTime == NO_COLLISION;
    }

    public boolean hasEntryTimeNotBeenUpdated() {
        return entryTime == -Double.MAX_VALUE;
    }

    public boolean wasEntryTimeNotUpdated() {
        return entryTime != -Double.MAX_VALUE;
    }

    public void setNoCollision() {
        entryTime = NO_COLLISION;
    }

    public void setOverlapNormal(Vector2D overlapNormal) {
        this.overlapNormal = overlapNormal;
    }

    public void setTempOverlapNormal(Vector2D tempOverlapNormal) {
        this.tempOverlapNormal = tempOverlapNormal;
    }
}
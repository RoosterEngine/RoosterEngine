package bricklets;

/**
 *
 * @author david
 */
public class CollisionData {
    private static final double NO_COLLISION = Double.MAX_VALUE, DEFAULT_ENTRY = -Double.MAX_VALUE;
    private Vector2D collisionNormal, overlapNormal, tempOverlapNormal;
    private double entryTime, leaveTime, overlapTime, overlapVelocity, tempOverlapVelocity;
    private boolean overlapUpdated = false;

    public CollisionData(){
        collisionNormal = new Vector2D();
        overlapNormal = new Vector2D();
        tempOverlapNormal = new Vector2D();
        clear();
    }

    public Vector2D getCollisionNormal(){
        return collisionNormal;
    }

    public void setCollisionNormal(Vector2D collisionNormal){
        this.collisionNormal.set(collisionNormal);
    }

    public void setCollisionNormal(double x, double y){
        collisionNormal.set(x, y);
    }

    public Vector2D getOverlapNormal(){
        return  overlapNormal;
    }

    public void setOverlapNormal(Vector2D normal){
        overlapNormal.set(normal);
    }

    public void setOverlapNormal(double x, double y){
        overlapNormal.set(x, y);
    }

    public double getEntryTime(){
        return entryTime;
    }

    public void setEntryTime(double time){
        entryTime = time;
    }

    public double getLeaveTime(){
        return leaveTime;
    }

    public void setLeaveTime(double time){
        leaveTime = time;
    }

    public double getOverlapTime(){
        return overlapTime;
    }

    public void setOverlapTime(double time){
        overlapTime = time;
        overlapUpdated = true;
    }

    public double getOverlapVelocity(){
        return overlapVelocity;
    }

    public void setOverlapVelocity(double velocity){
        overlapVelocity = velocity;
        overlapUpdated = true;
    }

    public Vector2D getTempOverlapNormal(){
        return tempOverlapNormal;
    }

    public void setTempOverlapNormal(Vector2D normal){
        tempOverlapNormal.set(normal);
    }

    public double getTempOverlapVelocity(){
        return tempOverlapVelocity;
    }

    public void setTempOverlapVelocity(double velocity){
        tempOverlapVelocity = velocity;
    }

    public void setTempOverlapData(Vector2D normal, double velocity, double time){
        overlapUpdated = true;
        tempOverlapNormal.set(normal);
        tempOverlapVelocity = velocity;
        overlapTime = time;
    }

    public void updateOverlapData(){
        if(overlapUpdated){
            overlapNormal.set(tempOverlapNormal);
            overlapVelocity = tempOverlapVelocity;
        }
    }

    public void updateEntryTime(double time, Vector2D normal){
        updateEntryTime(time, normal.getX(), normal.getY());
    }

    public void updateLeaveTime(double time){
        if(time < leaveTime){
            leaveTime = time;
        }
    }

    public void updateEntryTime(double time, double normalX, double normalY){
        if(time > entryTime){
            entryTime = time;
            collisionNormal.set(normalX, normalY);
        }
    }

    public void resetOverlapUpdated(){
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
    }

    public boolean isIntersectingAndTravellingTowardsEachOther(){
        return entryTime == DEFAULT_ENTRY && overlapVelocity < 0;
    }

    public boolean willCollisionHappen(double maxTime){
        return entryTime <= maxTime && entryTime <= leaveTime && entryTime != DEFAULT_ENTRY;
    }

    public boolean isCollisionNotPosible(){
        return entryTime == NO_COLLISION;
    }

    public boolean isOverlapping(){
        return entryTime == -Double.MAX_VALUE && overlapUpdated;
    }

    public boolean wasEntryTimeUpdated(){
        return entryTime != -Double.MAX_VALUE;
    }

    public void setNoCollision(){
        entryTime = NO_COLLISION;
    }
}
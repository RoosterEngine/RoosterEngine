package bricklets;

/**
 *
 * @author david
 */
public class CollisionData {
    private Vector2D collisionNormal;
    private double timeToCollision;
    
    public final void set(Vector2D collisionNormal, double timeToCollision){
        this.collisionNormal = collisionNormal;
        this.timeToCollision = timeToCollision;
    }
    
    public Vector2D getCollisionNormal(){
        return collisionNormal;
    }
    
    public double getTimeToCollision(){
        return timeToCollision;
    }
}
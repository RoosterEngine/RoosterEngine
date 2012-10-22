package bricklets;

/**
 * 
 * @author davidrusu
 */
public class Collision {
    public static final Collision NO_COLLISION = new Collision(Double.MAX_VALUE, new Vector2D(), null, null);
    private Vector2D collisionNormal;
    private Shape a, b;
    private double timeToCollision;
    
    public Collision(){
        timeToCollision = 0;
        collisionNormal = new Vector2D();
        a = null;
        b = null;
    }
    
    public Collision(double timeToCollision, Vector2D collisionNormal, Shape a, Shape b){
        set(timeToCollision, collisionNormal, a, b);
    }
    
    public void set(double timeToCollision, Vector2D collisionNormal, Shape a, Shape b){
        this.timeToCollision = timeToCollision;
        this.collisionNormal = collisionNormal;
        this.a = a;
        this.b = b;
    }

    public void set(Collision collision) {
        timeToCollision = collision.timeToCollision;
        collisionNormal = collision.collisionNormal;
        a = collision.a;
        b = collision.b;
    }
    
    public double getTimeToCollision(){
        return timeToCollision;
    }
    
    public Vector2D getCollisionNormal(){
        return collisionNormal;
    }
    
    public Shape getA(){
        return a;
    }
    
    public Shape getB(){
        return b;
    }
}
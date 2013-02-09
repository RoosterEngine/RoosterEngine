package gameengine.collisiondetection;

import gameengine.collisiondetection.shapes.Shape;
import gameengine.entities.Entity;
import gameengine.math.Vector2D;

/**
 * @author davidrusu
 */
public class Collision {
    private static final Collision NO_COLLISION = new Collision();
    private Vector2D collisionNormal;
    private Entity a, b;
    private double timeToCollision;

    public Collision() {
        timeToCollision = Shape.NO_COLLISION;
        collisionNormal = new Vector2D();
        a = null;
        b = null;
    }

    public Collision(double timeToCollision, Vector2D collisionNormal, Entity a, Entity b) {
        this.timeToCollision = timeToCollision;
        this.collisionNormal = collisionNormal;
        this.a = a;
        this.b = b;
    }

    public void set(double timeToCollision, Vector2D collisionNormal, Entity a, Entity b) {
        this.timeToCollision = timeToCollision;
        this.collisionNormal = collisionNormal;
        this.a = a;
        this.b = b;
    }

    public void set(Collision collision) {
        timeToCollision = collision.timeToCollision;
        collisionNormal.set(collision.collisionNormal);
        a = collision.a;
        b = collision.b;
    }

    public void setNoCollision() {
        set(NO_COLLISION);
    }

    public double getCollisionTime() {
        return timeToCollision;
    }

    public Vector2D getCollisionNormal() {
        return collisionNormal;
    }

    public Entity getA() {
        return a;
    }

    public Entity getB() {
        return b;
    }

    public void setCollisionTime(double collisionTime) {
        this.timeToCollision = collisionTime;
    }
}
package gameengine.collisiondetection;

import gameengine.collisiondetection.shapes.Shape;
import gameengine.entities.Entity;
import gameengine.math.Vector2D;

/**
 * Stores collision data
 *
 * @author davidrusu
 */
public class Collision {
    private Vector2D collisionNormal = new Vector2D();
    private Entity a = null, b = null;
    private double timeToCollision = Shape.NO_COLLISION;
    private double tempMin, tempMax;

    public Collision() {
    }

    public Collision(double timeToCollision, Vector2D collisionNormal, Entity a, Entity b) {
        this.timeToCollision = timeToCollision;
        this.collisionNormal = collisionNormal;
        this.a = a;
        this.b = b;
    }

    public void set(double timeToCollision, double normalX, double normalY, Entity a, Entity b) {
        this.timeToCollision = timeToCollision;
        collisionNormal.set(normalX, normalY);
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
        timeToCollision = Shape.NO_COLLISION;
        collisionNormal.clear();
        a = null;
        b = null;
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

    public boolean assertCollision() {
        if (timeToCollision != Shape.NO_COLLISION) {
            assert a != null : timeToCollision;
            assert b != null : timeToCollision;
            assert a.getContainingTree() != null;
            assert a.getContainingTree().isEntityInTree(a);
            assert b.getContainingTree() != null;
            assert b.getContainingTree().isEntityInTree(b);
        } else {
            assert a == null;
            assert b == null;
        }
        return true;
    }

    public double getTempMin() {
        return tempMin;
    }

    public void setTempMin(double min) {
        this.tempMin = min;
    }

    public double getTempMax() {
        return tempMax;
    }

    public void setTempMax(double max) {
        this.tempMax = max;
    }
}

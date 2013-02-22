package gameengine.collisiondetection.tree;

import gameengine.collisiondetection.Collision;

/**
 * Node for {@link CollisionList} linked list
 *
 * User: davidrusu
 * Date: 15/02/13
 * Time: 6:07 PM
 */
public class CollisionNode {
    private CollisionNode prev = null, next = null;
    private Collision collision = new Collision();

    public CollisionNode getPrev() {
        return prev;
    }

    public CollisionNode getNext() {
        return next;
    }

    public void setPrev(CollisionNode prev) {
        this.prev = prev;
    }

    public void setNext(CollisionNode next) {
        this.next = next;
    }

    public double getCollisionTime() {
        return collision.getCollisionTime();
    }

    public void setCollisionTime(double collisionTime) {
        collision.setCollisionTime(collisionTime);
    }

    public Collision getCollision() {
        return collision;
    }

    public void clear() {
        collision.setNoCollision();
        prev = null;
        next = null;
    }

    public void disintegrateList() {
        if (next != null) {
            next.disintegrateList();
        }
        clear();
    }
}

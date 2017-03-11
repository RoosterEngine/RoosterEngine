package gameengine.collisiondetection.tree;

import gameengine.collisiondetection.Collision;
import gameengine.collisiondetection.shapes.CollisionData;

/**
 * A linked list that stores all of the active trees sorted by the time of the
 * next collision in the tree
 *
 * User: davidrusu
 * Date: 19/02/13
 * Time: 2:56 PM
 */
public class CollisionList {
    private CollisionNode sentinel = new CollisionNode();

    public CollisionList() {
        sentinel.setCollisionTime(-1);
    }

    /**
     * Called if a {@link Tree} updated its {@link CollisionNode} with a new collision time.
     * The method inserts the tree's node to it's proper location in the list
     *
     * @param node the {@link CollisionNode} that has been updated
     */
    public void collisionUpdated(CollisionNode node) {
        double collisionTime = node.getCollisionTime();

        if (collisionTime < node.getPrev().getCollisionTime()) {
            CollisionNode before = node.getPrev();
            do {
                before = before.getPrev();
            } while (collisionTime < before.getCollisionTime());

            remove(node);
            insertNodeAfter(node, before);
        } else if (node.getNext() != null && collisionTime > node.getNext().getCollisionTime()) {
            CollisionNode after = node.getNext();
            CollisionNode before;
            do {
                before = after;
                after = after.getNext();
            } while (after != null && collisionTime > after.getCollisionTime());
            remove(node);
            insertNodeAfter(node, before);
        }

        assert areNodesSorted() : "nodes are not sorted after sort";
    }

    /**
     * Adds the {@link CollisionNode} to this list
     *
     * @param node the {@link CollisionNode} to add
     */
    public void add(CollisionNode node) {
        assert node.getPrev() == null;
        assert node.getNext() == null;
        assert node.getCollisionTime() == CollisionData.NO_COLLISION;

        insertNodeAfter(node, sentinel);
        collisionUpdated(node);
    }

    /**
     * Removes the {@link CollisionNode} from the list
     *
     * @param node the {@link CollisionNode} to remove
     */
    public void remove(CollisionNode node) {
        CollisionNode next = node.getNext();
        if (next != null) {
            next.setPrev(node.getPrev());
        }

        assert node.getPrev() != null;
        node.getPrev().setNext(next);
    }

    /**
     * Clears the list
     */
    public void clear() {
        sentinel.disintegrateList();
        sentinel.setCollisionTime(-1);
    }

    /**
     * Returns the next {@link Collision} in the list
     *
     * @return the next {@link Collision} in the list
     */
    public Collision getNextCollision() {
        return sentinel.getNext().getCollision();
    }

    private void insertNodeAfter(CollisionNode node, CollisionNode before) {
        assert before != null;

        node.setPrev(before);
        node.setNext(before.getNext());

        node.getPrev().setNext(node);
        if (node.getNext() != null) {
            node.getNext().setPrev(node);
        }
    }

    //------------------------ testing methods ----------------------------
    public boolean areNodesSorted() {
        CollisionNode prev = sentinel, current = sentinel.getNext();
        while (current != null) {
            assert current.getPrev() == prev : "nodes prev is not what it should be";
            assert prev.getCollisionTime() <= current.getCollisionTime() : "prev: " + prev
                    .getCollisionTime() + " next: " + current.getCollisionTime();
            prev = current;
            current = current.getNext();
        }
        return true;
    }

    public boolean checkNodeCollision() {
        CollisionNode current = sentinel.getNext();
        while (current != null) {
            assert current.getCollision().assertCollision();
            current = current.getNext();
        }
        return true;
    }

    public boolean doAllNodesHaveNoCollision(double elapsedTime) {
        CollisionNode current = sentinel.getNext();
        while (current != null) {
            Collision collision = current.getCollision();
            assert collision.getCollisionTime() == CollisionData.NO_COLLISION : "nodes are not " +
                    "all set to" + " NoCollision: " + collision.getCollisionTime() + " " +
                    elapsedTime;
            assert collision.getA() == null : "nodes are not all set to NoCollision";
            assert collision.getB() == null : "nodes are not all set to NoCollision";
            current = current.getNext();
        }
        return true;
    }
}

package gameengine.collisiondetection.tree;

import gameengine.collisiondetection.Collision;
import gameengine.collisiondetection.shapes.Shape;

/**
 * documentation
 * User: davidrusu
 * Date: 19/02/13
 * Time: 2:56 PM
 */
public class CollisionList {
    private CollisionNode sentinel;

    public CollisionList() {
        sentinel = new CollisionNode();
        sentinel.setCollisionTime(-1);
    }

    public void collisionUpdated(Tree tree) {
        CollisionNode node = tree.getNode();
        double collisionTime = node.getCollisionTime();

        if (collisionTime < node.getPrev().getCollisionTime()) {
            CollisionNode before = node.getPrev();
            do {
                before = before.getPrev();
            } while (collisionTime < before.getCollisionTime());

            remove(tree);
            insertNodeAfter(node, before);
        } else if (node.getNext() != null && collisionTime > node.getNext().getCollisionTime()) {
            CollisionNode after = node.getNext();
            CollisionNode before;
            do {
                before = after;
                after = after.getNext();
            } while (after != null && collisionTime > after.getCollisionTime());
            remove(tree);
            insertNodeAfter(node, before);
        }

        assert areNodesSorted() : "nodes are not sorted after sort";
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

    public void add(Tree tree) {
        CollisionNode node = tree.getNode();

        assert node.getPrev() == null;
        assert node.getNext() == null;
        assert node.getCollisionTime() == Shape.NO_COLLISION;

        insertNodeAfter(node, sentinel);
        collisionUpdated(tree);
    }

    public void remove(Tree tree) {
        CollisionNode node = tree.getNode();

        CollisionNode next = node.getNext();
        if (next != null) {
            next.setPrev(node.getPrev());
        }

        assert node.getPrev() != null;
        node.getPrev().setNext(next);
//        node.clear();
    }

    public void clear() {
        sentinel.disintegrateList();
        sentinel.setCollisionTime(-1);
    }

    public Collision getNextCollision() {
        return sentinel.getNext().getCollision();
    }



    public boolean areNodesSorted() {
        CollisionNode prev = sentinel, current = sentinel.getNext();

        while (current != null) {
            assert current.getPrev() == prev : "nodes prev is not what it should be";
            assert prev.getCollisionTime() <= current.getCollisionTime() : "prev: " + prev.getCollisionTime() + " next: " + current.getCollisionTime();
            prev = current;
            current = current.getNext();
        }
        return true;
    }



    public boolean doAllNodesHaveNoCollision() {
        CollisionNode current = sentinel.getNext();
        while (current != null) {
            Collision collision = current.getCollision();
            assert collision.getCollisionTime() == Shape.NO_COLLISION : "nodes are not all set to NoCollision: " + collision.getCollisionTime();
            assert collision.getA() == null: "nodes are not all set to NoCollision";
            assert collision.getB() == null: "nodes are not all set to NoCollision";
            current = current.getNext();
        }
        return true;
    }
}

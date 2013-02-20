package gameengine.collisiondetection.tree;

import gameengine.collisiondetection.Collision;

/**
 * documentation
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

//    public void sort() {
//        assert collision.getCollisionTime() >= 0;
////        if (isLessThanPrev()) {
////            do {
////                prev.next = next;
////                if (next != null) {
////                    next.prev = prev;
////                }
////                next = prev;
////                next.prev = this;
////                prev = prev.prev;
////                prev.next = this;
////
////            } while (isLessThanPrev());
////        } else if (isGreaterThanNext()) {
////            do {
////                prev.next = next;
////                next.prev = prev;
////                prev = next;
////                prev.next = this;
////                next = next.next;
////                if (next != null) {
////                    next.prev = this;
////                }
////            } while (isGreaterThanNext());
////        }
//
//        double collisionTime = collision.getCollisionTime();
//        if(collisionTime < prev.getCollision().getCollisionTime()){
//            CollisionNode before = prev;
//            do{
//                before = before.prev;
//            }while(collisionTime < before.getCollision().getCollisionTime());
//            if (next != null) {
//                next.prev = prev;
//            }
//            prev.next = next;
//            insertAfter(before);
//        }else if(isGreaterThanNext()){
//            CollisionNode after = next;
//            CollisionNode before;
//            do{
//                before = after;
//                after = after.next;
//            }while(after != null && collisionTime > after.getCollision().getCollisionTime());
//            if (next != null) {
//                next.prev = prev;
//            }
//            prev.next = next;
//            insertAfter(before);
//        }
//    }
//
//    private boolean isLessThanPrev() {
//        // the node where prev == null will never be sorted so we don't need to check if prev == null
//        return collision.getCollisionTime() < prev.collision.getCollisionTime();
//    }
//
//    private boolean isGreaterThanNext() {
//        return next != null && collision.getCollisionTime() > next.collision.getCollisionTime();
//    }
//
//    public void insertAfter(CollisionNode node) {
//        assert node != null;
//
//        next = node.next;
//        if (next != null) {
//            next.prev = this;
//        }
//        prev = node;
//        node.next = this;
//    }
//
//    public void remove() {
//        if (prev != null) {
//            prev.next = next;
//        }
//        if (next != null) {
//            next.prev = prev;
//        }
//        clear();
//    }
}

package gameengine.collisiondetection;

import gameengine.collisiondetection.shapes.Shape;
import gameengine.collisiondetection.tree.SpatialTree;
import gameengine.context.Context;
import gameengine.entities.Entity;

import java.awt.*;
import java.util.ArrayList;

public class CollisionDetector {
    private SpatialTree tree;
    private ArrayList<CollisionPair> collisionPairs = new ArrayList<>();
    private double[] collisionTimes = new double[16];
    private int back = 0, numCollision = 0;
    private double gameTime = 0;

    public CollisionDetector(double centerX, double centerY,
                             double halfWidth, double halfHeight) {
        tree = new SpatialTree(centerX, centerY, halfWidth, halfHeight);
    }

    public void addEntity(Entity entity) {
        tree.addEntity(entity);
    }

    public void removeEntity(Entity entity) {
        // TODO entity has a link to the partition it's in, can use that to
        // remove the entity faster. but you have to make sure the tree shrinks
        tree.removeEntity(entity);
    }

    public void setCollisionPair(CollisionGroup a, CollisionGroup b) {
        int aOrdinal = a.ordinal();
        int bOrdinal = b.ordinal();
        int i = 0;
        boolean foundDuplicate = false;
        while (!foundDuplicate && i < collisionPairs.size()) {
            CollisionPair pair = collisionPairs.get(i);
            if ((pair.getA() == aOrdinal && pair.getB() == bOrdinal)
                    || (pair.getA() == bOrdinal && pair.getB() == aOrdinal)) {
                foundDuplicate = true;
            }
            i++;
        }
        if (!foundDuplicate) {
            collisionPairs.add(new CollisionPair(aOrdinal, bOrdinal));
        }
    }

    public void removeCollisionPair(CollisionGroup a, CollisionGroup b) {
        for (CollisionPair pair : collisionPairs) {
            if ((pair.getA() == a.ordinal() && pair.getB() == b.ordinal())
                    || (pair.getA() == b.ordinal() && pair.getB() == a.ordinal())) {
                collisionPairs.remove(pair);
                return;
            }
        }
    }

    public void clearCollisions() {
        collisionPairs.clear();
        tree.clear();
    }

    public void update(double elapsedTime, Context context) {
        double timeLeft = elapsedTime;
        tree.updateEntityMotions(elapsedTime);
        Collision collision = new Collision();
        while (timeLeft > 0 && !context.isPaused()) {
            tree.ensureEntitiesAreContained(timeLeft);
            collision.setCollisionTime(timeLeft);
            tree.calcCollision(collisionPairs, collision);
            double collisionTime = collision.getCollisionTime();
            double updateTime = Math.min(collisionTime, timeLeft);
            tree.updateEntityPositions(updateTime);
            gameTime += updateTime;
            if (collisionTime != Shape.NO_COLLISION && collisionTime < timeLeft) {
                double collisionRate = getCollisionRate();
                context.handleCollision(collision, collisionRate);
            }
            timeLeft -= updateTime;
        }
        tree.tryResize();
        tree.updateEntities(elapsedTime);
        context.update(elapsedTime);
    }

    private double getCollisionRate() {
        collisionTimes[back] = gameTime;
        double collisionRate;
        if (numCollision >= collisionTimes.length - 1) {
            int front = (back + 1) % collisionTimes.length;
            double dt = collisionTimes[back] - collisionTimes[front];
            back = front;
            collisionRate = collisionTimes.length / dt;
        } else {
            double dt = collisionTimes[back] - collisionTimes[0];
            back = (back + 1) % collisionTimes.length;
            collisionRate = collisionTimes.length / dt;
        }
        return collisionRate;
    }

    public void draw(Graphics2D g, Color color) {
        tree.draw(g, color);
    }
}
package gameengine.collisiondetection;

import gameengine.collisiondetection.tree.SpatialTree;
import gameengine.context.Context;
import gameengine.entities.Entity;

import java.awt.*;
import java.util.HashSet;

public class World {
    private HashSet<Entity> entities = new HashSet<>();
    private SpatialTree tree;
    private int[] collisionGroups = new int[CollisionType.values().length];
    private double[] collisionTimes = new double[16];
    private int back = 0, numCollision = 0;
    private double gameTime = 0;

    public World(double centerX, double centerY, double halfLength) {
        tree = new SpatialTree(centerX, centerY, halfLength);
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
        tree.addEntity(entity);
    }


    public void setCollisionGroup(CollisionType a, CollisionType b) {
        int x = a.ordinal();
        int y = b.ordinal();

        int mask = 1 << y;
        collisionGroups[x] |= mask;
        mask = 1 << x;
        collisionGroups[y] |= mask;
    }

    public void removeCollisionGroup(CollisionType a, CollisionType b) {
        int aOrdinal = a.ordinal();
        int bOrdinal = b.ordinal();
        int mask = 1 << aOrdinal;
        collisionGroups[bOrdinal] &= ~mask;
    }

    public void clearCollisions() {
        for (int i = 0; i < collisionGroups.length; i++) {
            collisionGroups[i] = 0;
        }
        entities.clear();
        tree.clear();
    }

    public void update(double elapsedTime, Context context) {
        updateMotions(elapsedTime);
        tree.ensureEntitiesAreContained(elapsedTime);
        tree.calcCollision(collisionGroups, elapsedTime, context);
        tree.tryResize();
        updateEntities(elapsedTime);
        context.update(elapsedTime);
    }

    public void drawTree(Graphics2D g, Color color) {
        tree.draw(g, color);
    }

    private void updateEntities(double elapsedTime) {
        for (Entity entity : entities) {
            entity.update(elapsedTime);
        }
    }

    private void updateMotions(double elapsedTime) {
        for (Entity entity : entities) {
            entity.updateMotion(elapsedTime);
        }
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
}

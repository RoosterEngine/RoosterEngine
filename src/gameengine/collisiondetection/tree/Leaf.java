package gameengine.collisiondetection.tree;

import gameengine.collisiondetection.Collision;
import gameengine.collisiondetection.CollisionGroup;
import gameengine.collisiondetection.CollisionPair;
import gameengine.collisiondetection.shapes.Shape;
import gameengine.entities.Entity;

import java.awt.*;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * documentation
 * User: davidrusu
 * Date: 15/01/13
 * Time: 9:27 PM
 */
public class Leaf extends Tree {
    private static ArrayDeque<Leaf> recycledLeafs = new ArrayDeque<>();

    private Leaf(Parent parent, double centerX, double centerY,
                 double halfWidth, double halfHeight) {
        super(parent, centerX, centerY, halfWidth, halfHeight);
    }

    public Leaf() {
        super();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public static Leaf getInstance(Parent parent,
                                   double centerX, double centerY,
                                   double halfWidth, double halfHeight) {
        if (recycledLeafs.isEmpty()) {
            return new Leaf(parent, centerX, centerY, halfWidth, halfHeight);
        }
        Leaf leafInstance = recycledLeafs.pop();
        leafInstance.init(parent, centerX, centerY, halfWidth, halfHeight);
        return leafInstance;
    }

    public static Leaf getInstance() {
        if (recycledLeafs.isEmpty()) {
            return new Leaf();
        }
        return recycledLeafs.pop();
    }

    @Override
    public Tree addEntity(Entity entity) {
        entities.add(entity);
        Shape shape = entity.getShape();
        for (CollisionGroup group : shape.getCollisionGroups()) {
            collisionGroups[group.ordinal()].add(shape);
        }
        entity.setPartition(this);
        entityCount++;
        return this;
    }

    @Override
    public boolean removeEntity(Entity entity) {
        boolean removed = entities.remove(entity);
        if (removed) {
            entity.setPartition(null);
            removeShape(entity.getShape());
            entityCount--;
        }
        return removed;
    }

    private void removeShape(Shape shape) {
        ArrayList<CollisionGroup> groups = shape.getCollisionGroups();
        for (CollisionGroup group : groups) {
            collisionGroups[group.ordinal()].remove(shape);
        }
    }

    @Override
    public void addAllEntitiesToTree(Tree tree) {
        for (Entity entity : entities) {
            tree.addEntity(entity);
        }
    }

    @Override
    public void ensureEntitiesAreContained(double time) {
        Iterator<Entity> iterator = entities.iterator();
        while (iterator.hasNext()) {
            Entity entity = iterator.next();
            Shape shape = entity.getShape();
            shape.calculateBoundingBox(time);

            if (shape.getMinCollisionX() < minX) {
                parent.relocateLeft(entity);
                postRelocateRemove(entity, iterator);
            } else if (shape.getMinCollisionY() < minY) {
                parent.relocateUp(entity);
                postRelocateRemove(entity, iterator);
            } else if (shape.getMaxCollisionX() > maxX) {
                parent.relocateRight(entity);
                postRelocateRemove(entity, iterator);
            } else if (shape.getMaxCollisionY() > maxY) {
                parent.relocateDown(entity);
                postRelocateRemove(entity, iterator);
            }
        }
    }

    private void postRelocateRemove(Entity entity, Iterator<Entity> iterator) {
        iterator.remove();
        removeShape(entity.getShape());
        entityCount--;
    }

    @Override
    public void updateEntities(double elapsedTime) {
        for (Entity entity : entities) {
            entity.update(elapsedTime);
        }
    }

    @Override
    public Tree tryResize() {
        if (entityCount >= GROW_THRESH) {
            Quad quad = Quad.getInstance(
                    parent, centerX, centerY, halfWidth, halfHeight);
            Iterator<Entity> iterator = entities.iterator();
            while (iterator.hasNext()) {
                quad.addEntity(iterator.next());
                iterator.remove();
            }
            recycle();
            return quad;
        }
        return this;
    }

    @Override
    public void updateEntityPositions(double elapsedTime) {
        for (Entity entity : entities) {
            entity.updatePosition(elapsedTime);
        }
    }

    @Override
    public void updateEntityMotions(double elapsedTime) {
        for (Entity entity : entities) {
            entity.updateMotion(elapsedTime);
        }
    }

    @Override
    public void calcCollision(ArrayList<CollisionPair> collisionPairs,
                              Collision result) {
        if (entityCount == 0) {
            return;
        }
        for (CollisionPair collisionPair : collisionPairs) {
            ArrayList<Shape> listA = collisionGroups[collisionPair.getA()];
            ArrayList<Shape> listB = collisionGroups[collisionPair.getB()];

            if (listA == listB) {
                checkCollisionsInSingleList(listA, result);
            } else {
                checkCollisionsInLists(listA, listB, result);
            }
        }
        parent.checkForCollisionWithTree(this, collisionPairs, result);
    }

    @Override
    public void recycle() {
        clear();
        recycledLeafs.push(this);
    }

    @Override
    public void draw(Graphics2D g, Color color) {
        g.setColor(color.darker());
        g.fillRect((int) minX, (int) minY, (int) (halfWidth * 2), (int) (halfHeight * 2));
    }
}

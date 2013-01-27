package gameengine.collisiondetection.tree;

import gameengine.collisiondetection.Collision;
import gameengine.collisiondetection.shapes.Shape;
import gameengine.entities.Entity;

import java.awt.*;
import java.util.ArrayDeque;
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
                 double halfLength) {
        super(parent, centerX, centerY, halfLength);
    }

    public Leaf() {
        super();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public static Leaf getInstance(Parent parent,
                                   double centerX, double centerY, double halfLength) {
        if (recycledLeafs.isEmpty()) {
            return new Leaf(parent, centerX, centerY, halfLength);
        }
        Leaf leafInstance = recycledLeafs.pop();
        leafInstance.init(parent, centerX, centerY, halfLength);
        return leafInstance;
    }

    public static Leaf getInstance() {
        if (recycledLeafs.isEmpty()) {
            return new Leaf();
        }
        return recycledLeafs.pop();
    }

    @Override
    public void addEntity(Entity entity) {
        entities.add(entity);
        entity.setPartition(this);
        entityCount++;
    }

    @Override
    public boolean removeEntity(Entity entity) {
        boolean removed = entities.remove(entity);
        if (removed) {
            entity.setPartition(null);
            entityCount--;
        }
        return removed;
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
                    parent, centerX, centerY, halfLength);
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
    public void calcCollision(
            int[] collisionGroups, Collision temp, Collision result) {
        if (entityCount == 0) {
            return;
        }
        checkEntityCollisions(collisionGroups, temp, result);
    }

    @Override
    public void recycle() {
        clear();
        recycledLeafs.push(this);
    }

    @Override
    public void draw(Graphics2D g, Color color) {
        g.setColor(color.darker());
        int length = (int) (halfLength * 2);
        g.fillRect((int) minX, (int) minY, length, length);
    }
}

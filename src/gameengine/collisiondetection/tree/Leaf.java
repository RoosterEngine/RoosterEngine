package gameengine.collisiondetection.tree;

import gameengine.collisiondetection.Collision;
import gameengine.collisiondetection.shapes.Shape;
import gameengine.entities.Entity;

import java.awt.*;
import java.util.ArrayDeque;

/**
 * documentation
 * User: davidrusu
 * Date: 15/01/13
 * Time: 9:27 PM
 */
public class Leaf extends Tree {
    private static ArrayDeque<Leaf> recycledLeafs = new ArrayDeque<>();

    private Leaf(Parent parent, double centerX, double centerY, double halfLength) {
        super(parent, centerX, centerY, halfLength);
    }

    private Leaf() {
        super();
    }

    public static Leaf getInstance(Parent parent, double centerX, double centerY, double halfLength) {
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
        addEntityToList(entity);
        entity.setContainingTree(this);
        entityCount++;
    }

    @Override
    public void ensureEntitiesAreContained(double time) {
        int index = 0;
        int originalEntityListPos = entityListPos;
        for (int i = 0; i < originalEntityListPos; i++) {
            Entity entity = entities[index];
            Shape shape = entity.getShape();
            shape.calculateBoundingBox(time);

            if (shape.getBoundingMinX() < getMinX()) {
                parent.relocateLeft(entity);
                postRelocateRemove(index);
            } else if (shape.getBoundingMinY() < getMinY()) {
                parent.relocateUp(entity);
                postRelocateRemove(index);
            } else if (shape.getBoundingMaxX() > getMaxX()) {
                parent.relocateRight(entity);
                postRelocateRemove(index);
            } else if (shape.getBoundingMaxY() > getMaxY()) {
                parent.relocateDown(entity);
                postRelocateRemove(index);
            } else {
                index++;
            }
        }
    }

    private void postRelocateRemove(int i) {
        removeEntityFromList(i);
        setEntityCount(getEntityCount() - 1);
    }

    @Override
    public void updateEntities(double elapsedTime) {
        for (Entity entity : entities) {
            entity.update(elapsedTime);
        }
    }

    @Override
    public Tree tryResize() {
        if (getEntityCount() >= GROW_THRESH) {
            Quad quad = Quad.getInstance(parent, getCenterX(), getCenterY(), getHalfLength());
            int totalEntities = getEntityCount();
            for (int i = 0; i < totalEntities; i++) {
                quad.addEntity(entities[i]);
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
    public void checkCollisionWithEntity(int[] collisionGroups, Collision temp, Collision result, Entity entity) {
        Shape a = entity.getShape();
        for (int i = 0; i < entityListPos; i++) {
            Entity entityToCheck = entities[i];
            Shape b = entityToCheck.getShape();
            if (doShapeTypesCollide(collisionGroups, a, b)) {
                Shape.collideShapes(a, b, result.getCollisionTime(), temp);
                if (temp.getCollisionTime() < result.getCollisionTime()) {
                    result.set(temp);
                }
            }
        }
    }

    private void checkEntityCollisionsWithinTree(int[] collisionGroups, Collision temp, Collision result) {

        for (int i = 0; i < entityListPos; i++) {
            Shape a = entities[i].getShape();
            for (int j = i + 1; j < entityListPos; j++) {
                Shape b = entities[j].getShape();
                if (doShapeTypesCollide(collisionGroups, a, b)) {
                    Shape.collideShapes(a, b, result.getCollisionTime(), temp);
                    if (temp.getCollisionTime() < result.getCollisionTime()) {
                        result.set(temp);
                    }
                }
            }
        }
    }

    @Override
    public void calcCollision(int[] collisionGroups, Collision temp, Collision result) {
        if (getEntityCount() == 0) {
            return;
        }
        checkEntityCollisionsWithinTree(collisionGroups, temp, result);
    }

    @Override
    public void recycle() {
        clear();
        recycledLeafs.push(this);
    }

    @Override
    public void draw(Graphics2D g, Color color) {
        g.setColor(color.darker());
        int length = (int) (getHalfLength() * 2);
        g.fillRect((int) getMinX(), (int) getMinY(), length, length);
        g.setColor(Color.RED);
        int offset = 3;
        for (int i = 0; i < entityListPos; i++) {
            Entity entity = entities[i];
            g.drawLine((int) entity.getX() + offset, (int) entity.getY() + offset, (int) getCenterX() + offset, (int) getCenterY() + offset);
        }
    }
}

package gameengine.collisiondetection.tree;

import gameengine.collisiondetection.Collision;
import gameengine.collisiondetection.shapes.Shape;
import gameengine.entities.Entity;

import java.awt.*;

/**
 * documentation
 * User: davidrusu
 * Date: 15/01/13
 * Time: 9:31 PM
 */
public class SpatialTree implements Parent {
    private Tree tree;
    private Collision tempCollision = new Collision();
    private double initCenterX, initCenterY, initHalfLength;

    public SpatialTree(double centerX, double centerY,
                       double halfLength) {
        tree = Leaf.getInstance(this, centerX, centerY, halfLength);
        initCenterX = centerX;
        initCenterY = centerY;
        initHalfLength = halfLength;
    }

    public void addEntity(Entity entity) {
        Shape shape = entity.getShape();
        shape.calculateBoundingBox(0);
        // TODO is this variable access ok?
        if (shape.getMinCollisionX() <= tree.minX) {
            relocateLeft(entity);
        } else if (shape.getMinCollisionY() <= tree.minY) {
            relocateUp(entity);
        } else if (shape.getMaxCollisionX() >= tree.maxX) {
            relocateRight(entity);
        } else if (shape.getMaxCollisionY() >= tree.maxY) {
            relocateDown(entity);
        } else {
            tree.addEntity(entity);
        }
    }

    public boolean removeEntity(Entity entity) {
        return tree.removeEntity(entity);
    }

    public void clear() {
        // TODO is this variable access ok?
        Tree newTree = Leaf.getInstance(
                this, initCenterX, initCenterY, initHalfLength);
        tree.recycle();
        tree = newTree;
    }

    public void ensureEntitiesAreContained(double time) {
        tree.ensureEntitiesAreContained(time);
    }

    public void calcCollision(int[] collisionGroups, Collision result) {
        tempCollision.setCollisionTime(result.getCollisionTime());
        tree.calcCollision(collisionGroups, tempCollision, result);
    }

    @Override
    public void checkForCollisionWithTree(Tree tree, int[] collisionGroup,
                                          Collision temp, Collision result) {
    }

    public void updateEntities(double elapsedTime) {
        tree.updateEntities(elapsedTime);
    }

    public void updateEntityPositions(double elapsedTime) {
        tree.updateEntityPositions(elapsedTime);
    }

    public void updateEntityMotions(double elapsedTime) {
        tree.updateEntityMotions(elapsedTime);
    }

    public int getEntityCount() {
        return tree.entityCount;
    }

    public void tryResize() {
        tree = tree.tryResize();
    }

    private void grow(double centerX, double centerY, double halfLength,
                      Tree topLeft, Tree topRight,
                      Tree bottomLeft, Tree bottomRight) {
        double quartLength = halfLength / 2;
        double left = centerX - quartLength;
        double right = centerX + quartLength;
        double top = centerY - quartLength;
        double bottom = centerY + quartLength;
        topLeft.resize(left, top, quartLength);
        topRight.resize(right, top, quartLength);
        bottomLeft.resize(left, bottom, quartLength);
        bottomRight.resize(right, bottom, quartLength);
        int prevEntityCount = tree.entityCount;
        tree = Quad.getInstance(this, centerX, centerY, halfLength,
                topLeft, topRight, bottomLeft, bottomRight);
        tree.entityCount = prevEntityCount - 1;
    }

    @Override
    public void relocateLeft(Entity entity) {
        Shape shape = entity.getShape();
        // TODO is this variable access okay?
        double halfLength = tree.halfLength * 2;
        double centerX = tree.centerX - tree.halfLength, centerY;
        Tree topLeft = Leaf.getInstance(), bottomLeft = Leaf.getInstance();
        Tree topRight, bottomRight;
        if (shape.getY() < tree.centerY) {
            centerY = tree.centerY - tree.halfLength;
            topRight = Leaf.getInstance();
            bottomRight = tree;
        } else {
            centerY = tree.centerY + tree.halfLength;
            topRight = tree;
            bottomRight = Leaf.getInstance();
        }
        grow(centerX, centerY, halfLength,
                topLeft, topRight, bottomLeft, bottomRight);
        // TODO can infer that it'll be on the left
        tree.addEntity(entity);
    }

    @Override
    public void relocateRight(Entity entity) {
        Shape shape = entity.getShape();
        // TODO is this variable access okay?
        double halfLength = tree.halfLength * 2;
        double centerX = tree.centerX + tree.halfLength, centerY;
        Tree topRight = Leaf.getInstance(), bottomRight = Leaf.getInstance();
        Tree topLeft, bottomLeft;
        if (shape.getY() < tree.centerY) {
            centerY = tree.centerY - tree.halfLength;
            topLeft = Leaf.getInstance();
            bottomLeft = tree;
        } else {
            centerY = tree.centerY + tree.halfLength;
            topLeft = tree;
            bottomLeft = Leaf.getInstance();
        }
        grow(centerX, centerY, halfLength,
                topLeft, topRight, bottomLeft, bottomRight);
        // TODO can infer that it'll be on the left
        tree.addEntity(entity);
    }

    @Override
    public void relocateUp(Entity entity) {
        Shape shape = entity.getShape();
        // TODO is this variable access okay?
        double halfLength = tree.halfLength * 2;
        double centerX, centerY = tree.centerY - tree.halfLength;
        Tree topLeft = Leaf.getInstance(), topRight = Leaf.getInstance();
        Tree bottomLeft, bottomRight;
        if (shape.getX() < tree.centerX) {
            centerX = tree.centerX - tree.halfLength;
            bottomLeft = Leaf.getInstance();
            bottomRight = tree;
        } else {
            centerX = tree.centerX + tree.halfLength;
            bottomLeft = tree;
            bottomRight = Leaf.getInstance();
        }
        grow(centerX, centerY, halfLength,
                topLeft, topRight, bottomLeft, bottomRight);
        // TODO can infer that it'll be on the left
        tree.addEntity(entity);
    }

    @Override
    public void relocateDown(Entity entity) {
        Shape shape = entity.getShape();
        // TODO is this variable access okay?
        double halfLength = tree.halfLength * 2;
        double centerX, centerY = tree.centerY + tree.halfLength;
        Tree bottomLeft = Leaf.getInstance(), bottomRight = Leaf.getInstance();
        Tree topLeft, topRight;
        if (shape.getX() < tree.centerX) {
            centerX = tree.centerX - tree.halfLength;
            topLeft = Leaf.getInstance();
            topRight = tree;
        } else {
            centerX = tree.centerX + tree.halfLength;
            topLeft = tree;
            topRight = Leaf.getInstance();
        }
        grow(centerX, centerY, halfLength,
                topLeft, topRight, bottomLeft, bottomRight);
        // TODO can infer that it'll be on the left
        tree.addEntity(entity);
    }

    public void draw(Graphics2D g, Color color) {
        tree.draw(g, color);
    }
}

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

    public SpatialTree(double centerX, double centerY, double halfLength) {
        tree = Leaf.getInstance(this, centerX, centerY, halfLength);
        initCenterX = centerX;
        initCenterY = centerY;
        initHalfLength = halfLength;
    }

    public void addEntity(Entity entity) {
        Shape shape = entity.getShape();
        shape.calculateBoundingBox(0);
        if (shape.getBoundingMinX() <= tree.getMinX()) {
            relocateLeft(entity);
        } else if (shape.getBoundingMinY() <= tree.getMinY()) {
            relocateUp(entity);
        } else if (shape.getBoundingMaxX() >= tree.getMaxX()) {
            relocateRight(entity);
        } else if (shape.getBoundingMaxY() >= tree.getMaxY()) {
            relocateDown(entity);
        } else {
            tree.addEntity(entity);
        }
    }

    public void clear() {
        Tree newTree = Leaf.getInstance(this, initCenterX, initCenterY, initHalfLength);
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
    public void decrementEntityCount() {
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
        return tree.getEntityCount();
    }

    public void tryResize() {
        tree = tree.tryResize();
    }

    private void grow(double centerX, double centerY, double halfLength, Tree topLeft, Tree topRight, Tree bottomLeft, Tree bottomRight) {
        double quartLength = halfLength / 2;
        double left = centerX - quartLength;
        double right = centerX + quartLength;
        double top = centerY - quartLength;
        double bottom = centerY + quartLength;
        topLeft.resize(left, top, quartLength);
        topRight.resize(right, top, quartLength);
        bottomLeft.resize(left, bottom, quartLength);
        bottomRight.resize(right, bottom, quartLength);
        int prevEntityCount = tree.getEntityCount();
        tree = Quad.getInstance(this, centerX, centerY, halfLength, topLeft, topRight, bottomLeft, bottomRight);
        tree.setEntityCount(prevEntityCount - 1);
    }

    @Override
    public void relocateLeft(Entity entity) {
        Shape shape = entity.getShape();
        double halfLength = tree.getHalfLength() * 2;
        double centerX = tree.getCenterX() - tree.getHalfLength(), centerY;
        Tree topLeft = Leaf.getInstance(), bottomLeft = Leaf.getInstance();
        Tree topRight, bottomRight;
        if (shape.getY() < tree.getCenterY()) {
            centerY = tree.getCenterY() - tree.getHalfLength();
            topRight = Leaf.getInstance();
            bottomRight = tree;
        } else {
            centerY = tree.getCenterY() + tree.getHalfLength();
            topRight = tree;
            bottomRight = Leaf.getInstance();
        }
        grow(centerX, centerY, halfLength, topLeft, topRight, bottomLeft, bottomRight);
        // TODO can infer that it'll be on the left
        tree.addEntity(entity);
    }

    @Override
    public void relocateRight(Entity entity) {
        Shape shape = entity.getShape();
        double halfLength = tree.getHalfLength() * 2;
        double centerX = tree.getCenterX() + tree.getHalfLength(), centerY;
        Tree topRight = Leaf.getInstance(), bottomRight = Leaf.getInstance();
        Tree topLeft, bottomLeft;
        if (shape.getY() < tree.getCenterY()) {
            centerY = tree.getCenterY() - tree.getHalfLength();
            topLeft = Leaf.getInstance();
            bottomLeft = tree;
        } else {
            centerY = tree.getCenterY() + tree.getHalfLength();
            topLeft = tree;
            bottomLeft = Leaf.getInstance();
        }
        grow(centerX, centerY, halfLength, topLeft, topRight, bottomLeft, bottomRight);
        // TODO can infer that it'll be on the left
        tree.addEntity(entity);
    }

    @Override
    public void relocateUp(Entity entity) {
        Shape shape = entity.getShape();
        double halfLength = tree.getHalfLength() * 2;
        double centerX, centerY = tree.getCenterY() - tree.getHalfLength();
        Tree topLeft = Leaf.getInstance(), topRight = Leaf.getInstance();
        Tree bottomLeft, bottomRight;
        if (shape.getX() < tree.getCenterX()) {
            centerX = tree.getCenterX() - tree.getHalfLength();
            bottomLeft = Leaf.getInstance();
            bottomRight = tree;
        } else {
            centerX = tree.getCenterX() + tree.getHalfLength();
            bottomLeft = tree;
            bottomRight = Leaf.getInstance();
        }
        grow(centerX, centerY, halfLength, topLeft, topRight, bottomLeft, bottomRight);
        // TODO can infer that it'll be on the left
        tree.addEntity(entity);
    }

    @Override
    public void relocateDown(Entity entity) {
        Shape shape = entity.getShape();
        double halfLength = tree.getHalfLength() * 2;
        double centerX, centerY = tree.getCenterY() + tree.getHalfLength();
        Tree bottomLeft = Leaf.getInstance(), bottomRight = Leaf.getInstance();
        Tree topLeft, topRight;
        if (shape.getX() < tree.getCenterX()) {
            centerX = tree.getCenterX() - tree.getHalfLength();
            topLeft = Leaf.getInstance();
            topRight = tree;
        } else {
            centerX = tree.getCenterX() + tree.getHalfLength();
            topLeft = tree;
            topRight = Leaf.getInstance();
        }
        grow(centerX, centerY, halfLength, topLeft, topRight, bottomLeft, bottomRight);
        // TODO can infer that it'll be on the left
        tree.addEntity(entity);
    }

    public void draw(Graphics2D g, Color color) {
        tree.draw(g, color);
    }
}

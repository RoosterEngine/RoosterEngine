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
        tree = Leaf.createInstance(this, centerX, centerY, halfLength);
        initCenterX = centerX;
        initCenterY = centerY;
        initHalfLength = halfLength;
    }

    public void addEntity(Entity entity) {
        Shape shape = entity.getShape();
        shape.calculateBoundingBox(0);
        if (isNotContainedInTree(entity)) {
            relocate(entity);
        } else {
            tree.addEntity(entity);
        }
    }

    private boolean isNotContainedInTree(Entity entity) {
        Shape shape = entity.getShape();
        return !isContained(shape.getBoundingCenterX(), tree.getCenterX(), shape.getBoundingHalfWidth()) || !isContained(shape.getBoundingCenterY(), tree.getCenterY(), shape.getBoundingHalfHeight());
    }

    private boolean isContained(double shapePosition, double treePosition, double shapeHalfLength) {
        return Math.abs(treePosition - shapePosition) < tree.getHalfLength() - shapeHalfLength;
    }

    public void clear() {
        Tree newTree = Leaf.createInstance(this, initCenterX, initCenterY, initHalfLength);
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
        tree = Quad.createInstance(this, centerX, centerY, halfLength, topLeft, topRight, bottomLeft, bottomRight);
    }

    @Override
    public void relocate(Entity entity) {
        Shape shape = entity.getShape();
        double centerX = tree.getCenterX(), centerY = tree.getCenterY();
        Tree topLeft, topRight, bottomLeft, bottomRight;

        if (shape.getX() < tree.getCenterX()) {
            centerX -= tree.getHalfLength();
            bottomRight = Leaf.createInstance();
            topRight = Leaf.createInstance();
            if (shape.getY() < tree.getCenterY()) {
                centerY -= tree.getHalfLength();
                topLeft = tree;
                bottomLeft = Leaf.createInstance();
            } else {
                centerY += tree.getHalfLength();
                topLeft = Leaf.createInstance();
                bottomLeft = tree;
            }
        } else {
            centerX += tree.getHalfLength();
            topLeft = Leaf.createInstance();
            bottomLeft = Leaf.createInstance();
            if (shape.getY() < tree.getCenterY()) {
                centerY -= tree.getHalfLength();
                topRight = tree;
                bottomRight = Leaf.createInstance();
            } else {
                centerY += tree.getHalfLength();
                topRight = Leaf.createInstance();
                bottomRight = tree;
            }
        }
        grow(centerX, centerY, tree.getHalfLength() * 2, topLeft, topRight, bottomLeft, bottomRight);
        tree.addEntity(entity);
    }

    public void draw(Graphics2D g, Color color) {
        tree.draw(g, color);
    }
}

package gameengine.collisiondetection.tree;

import gameengine.collisiondetection.Collision;
import gameengine.collisiondetection.CollisionPair;
import gameengine.collisiondetection.shapes.Shape;
import gameengine.entities.Entity;

import java.awt.*;
import java.util.ArrayList;

/**
 * documentation
 * User: davidrusu
 * Date: 15/01/13
 * Time: 9:31 PM
 */
public class SpatialTree implements Parent {
    private Tree tree;
    private double initCenterX, initCenterY, initHalfWidth, initHalfHeight;

    public SpatialTree(double centerX, double centerY,
                       double halfWidth, double halfHeight) {
        tree = Leaf.getInstance(this, centerX, centerY, halfWidth, halfHeight);
        initCenterX = centerX;
        initCenterY = centerY;
        initHalfWidth = halfWidth;
        initHalfHeight = halfHeight;
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
            tree = tree.addEntity(entity);
        }
    }

    public boolean removeEntity(Entity entity) {
        return tree.removeEntity(entity);
    }

    public void clear() {
        // TODO is this variable access ok?
        Tree newTree = Leaf.getInstance(
                this, initCenterX, initCenterY, initHalfWidth, initHalfHeight);
        tree.recycle();
        tree = newTree;
    }

    public void ensureEntitiesAreContained(double time) {
        tree.ensureEntitiesAreContained(time);
    }

    public void calcCollision(ArrayList<CollisionPair> collisionPairs,
                              Collision result) {
        tree.calcCollision(collisionPairs, result);
    }

    @Override
    public void checkForCollisionWithTree(Tree tree, ArrayList<CollisionPair> collisionPairs, Collision result) {
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

    public void tryResize() {
        tree = tree.tryResize();
    }

    private void grow(double centerX, double centerY,
                      double halfWidth, double halfHeight,
                      Tree topLeft, Tree topRight,
                      Tree bottomLeft, Tree bottomRight) {
        double quartWidth = halfWidth / 2;
        double quartHeight = halfHeight / 2;
        double left = centerX - quartWidth;
        double right = centerX + quartWidth;
        double top = centerY - quartHeight;
        double bottom = centerY + quartHeight;
        topLeft.resize(left, top, quartWidth, quartHeight);
        topRight.resize(right, top, quartWidth, quartHeight);
        bottomLeft.resize(left, bottom, quartWidth, quartHeight);
        bottomRight.resize(right, bottom, quartWidth, quartHeight);
        int prevEntityCount = tree.entityCount;
        tree = Quad.getInstance(this, centerX, centerY, halfWidth, halfHeight,
                topLeft, topRight, bottomLeft, bottomRight);
        tree.entityCount = prevEntityCount - 1;
    }

    @Override
    public void relocateLeft(Entity entity) {
        Shape shape = entity.getShape();
        // TODO is this variable access okay?
        double halfWidth = tree.halfWidth * 2;
        double halfHeight = tree.halfHeight * 2;
        double centerX = tree.centerX - tree.halfWidth, centerY;
        Tree topLeft = Leaf.getInstance(), bottomLeft = Leaf.getInstance();
        Tree topRight, bottomRight;
        if (shape.getY() < tree.centerY) {
            centerY = tree.centerY - tree.halfHeight;
            topRight = Leaf.getInstance();
            bottomRight = tree;
        } else {
            centerY = tree.centerY + tree.halfHeight;
            topRight = tree;
            bottomRight = Leaf.getInstance();
        }
        grow(centerX, centerY, halfWidth, halfHeight,
                topLeft, topRight, bottomLeft, bottomRight);
        // TODO can infer that it'll be on the left
        tree.addEntity(entity);
    }

    @Override
    public void relocateRight(Entity entity) {
        Shape shape = entity.getShape();
        // TODO is this variable access okay?
        double halfWidth = tree.halfWidth * 2;
        double halfHeight = tree.halfHeight * 2;
        double centerX = tree.centerX + tree.halfWidth, centerY;
        Tree topRight = Leaf.getInstance(), bottomRight = Leaf.getInstance();
        Tree topLeft, bottomLeft;
        if (shape.getY() < tree.centerY) {
            centerY = tree.centerY - tree.halfHeight;
            topLeft = Leaf.getInstance();
            bottomLeft = tree;
        } else {
            centerY = tree.centerY + tree.halfHeight;
            topLeft = tree;
            bottomLeft = Leaf.getInstance();
        }
        grow(centerX, centerY, halfWidth, halfHeight,
                topLeft, topRight, bottomLeft, bottomRight);
        // TODO can infer that it'll be on the left
        tree.addEntity(entity);
    }

    @Override
    public void relocateUp(Entity entity) {
        Shape shape = entity.getShape();
        // TODO is this variable access okay?
        double halfWidth = tree.halfWidth * 2;
        double halfHeight = tree.halfHeight * 2;
        double centerX, centerY = tree.centerY - tree.halfHeight;
        Tree topLeft = Leaf.getInstance(), topRight = Leaf.getInstance();
        Tree bottomLeft, bottomRight;
        if (shape.getX() < tree.centerX) {
            centerX = tree.centerX - tree.halfWidth;
            bottomLeft = Leaf.getInstance();
            bottomRight = tree;
        } else {
            centerX = tree.centerX + tree.halfWidth;
            bottomLeft = tree;
            bottomRight = Leaf.getInstance();
        }
        grow(centerX, centerY, halfWidth, halfHeight,
                topLeft, topRight, bottomLeft, bottomRight);
        // TODO can infer that it'll be on the left
        tree.addEntity(entity);
    }

    @Override
    public void relocateDown(Entity entity) {
        Shape shape = entity.getShape();
        // TODO is this variable access okay?
        double halfWidth = tree.halfWidth * 2;
        double halfHeight = tree.halfHeight * 2;
        double centerX, centerY = tree.centerY + tree.halfHeight;
        Tree bottomLeft = Leaf.getInstance(), bottomRight = Leaf.getInstance();
        Tree topLeft, topRight;
        if (shape.getX() < tree.centerX) {
            centerX = tree.centerX - tree.halfWidth;
            topLeft = Leaf.getInstance();
            topRight = tree;
        } else {
            centerX = tree.centerX + tree.halfWidth;
            topLeft = tree;
            topRight = Leaf.getInstance();
        }
        grow(centerX, centerY, halfWidth, halfHeight, topLeft, topRight, bottomLeft, bottomRight);
        // TODO can infer that it'll be on the left
        tree.addEntity(entity);
    }

    public void draw(Graphics2D g, Color color) {
        tree.draw(g, color);
    }
}

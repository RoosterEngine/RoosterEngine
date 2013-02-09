package gameengine.collisiondetection.tree;

import gameengine.collisiondetection.Collision;
import gameengine.collisiondetection.shapes.Shape;
import gameengine.entities.Entity;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayDeque;

/**
 * documentation
 * User: davidrusu
 * Date: 15/01/13
 * Time: 9:30 PM
 */
public class Quad extends Tree implements Parent {
    private static ArrayDeque<Quad> recycledQuads = new ArrayDeque<>();
    private Tree topLeft;
    private Tree topRight;
    private Tree bottomLeft;
    private Tree bottomRight;

    private Quad(Parent parent, double centerX, double centerY, double halfLength) {
        super(parent, centerX, centerY, halfLength);
        initQuads();
    }

    private Quad(Parent parent, double centerX, double centerY, double halfLength, Tree topLeft, Tree topRight, Tree bottomLeft, Tree bottomRight) {
        super(parent, centerX, centerY, halfLength);
        initQuads(topLeft, topRight, bottomLeft, bottomRight);
    }

    public static Quad getInstance(Parent parent, double centerX, double centerY, double halfLength) {
        if (recycledQuads.isEmpty()) {
            return new Quad(parent, centerX, centerY, halfLength);
        }
        Quad quad = recycledQuads.pop();
        quad.init(parent, centerX, centerY, halfLength);
        quad.initQuads();
        return quad;
    }

    public static Quad getInstance(Parent parent, double centerX, double centerY, double halfLength, Tree topLeft, Tree topRight, Tree bottomLeft, Tree bottomRight) {
        if (recycledQuads.isEmpty()) {
            return new Quad(parent, centerX, centerY, halfLength, topLeft, topRight, bottomLeft, bottomRight);
        }
        Quad quad = recycledQuads.pop();
        quad.init(parent, centerX, centerY, halfLength);
        quad.initQuads(topLeft, topRight, bottomLeft, bottomRight);
        return quad;
    }

    private void initQuads(Tree topLeft, Tree topRight, Tree bottomLeft, Tree bottomRight) {
        this.topLeft = topLeft;
        topLeft.parent = this;
        this.topRight = topRight;
        topRight.parent = this;
        this.bottomLeft = bottomLeft;
        bottomLeft.parent = this;
        this.bottomRight = bottomRight;
        bottomRight.parent = this;
    }

    private void initQuads() {
        double quartLength = getHalfLength() / 2;
        double left = getCenterX() - quartLength;
        double right = getCenterX() + quartLength;
        double top = getCenterY() - quartLength;
        double bottom = getCenterY() + quartLength;

        topLeft = Leaf.getInstance(this, left, top, quartLength);
        topRight = Leaf.getInstance(this, right, top, quartLength);
        bottomLeft = Leaf.getInstance(this, left, bottom, quartLength);
        bottomRight = Leaf.getInstance(this, right, bottom, quartLength);
    }

    @Override
    public void addEntity(Entity entity) {
        Shape shape = entity.getShape();
        if (shape.getBoundingMaxX() < getCenterX()) {
            // left
            if (shape.getBoundingMaxY() < getCenterY()) {
                // top
                addToTopLeft(entity);
                setEntityCount(getEntityCount() + 1);
                return;
            } else if (shape.getBoundingMinY() > getCenterY()) {
                // bottom
                addToBottomLeft(entity);
                setEntityCount(getEntityCount() + 1);
                return;
            }
        } else if (shape.getBoundingMinX() > getCenterX()) {
            // right
            if (shape.getBoundingMaxY() < getCenterY()) {
                // top
                addToTopRight(entity);
                setEntityCount(getEntityCount() + 1);
                return;
            } else if (shape.getBoundingMinY() > getCenterY()) {
                // bottom
                addToBottomRight(entity);
                setEntityCount(getEntityCount() + 1);
                return;
            }
        }
        // stays here
        addToThis(entity);
        setEntityCount(getEntityCount() + 1);
    }

    private void addToBottomRight(Entity entity) {
        bottomRight.addEntity(entity);
    }

    private void addToTopRight(Entity entity) {
        topRight.addEntity(entity);
    }

    private void addToBottomLeft(Entity entity) {
        bottomLeft.addEntity(entity);
    }

    private void addToTopLeft(Entity entity) {
        topLeft.addEntity(entity);
    }

    private void addToThis(Entity entity) {
        addEntityToList(entity);
        entity.setContainingTree(this);
    }

    @Override
    public void ensureEntitiesAreContained(double time) {
        int index = 0;

        int originalEntityListPos = entityListPos;
        for (int i = 0; i < originalEntityListPos; i++) {
            Entity entity = entities[index];
            Shape shape = entity.getShape();
            shape.calculateBoundingBox(time);

            if (shape.getBoundingMaxX() < getCenterX()) { // left
                if (shape.getBoundingMinX() > getMinX()) { // horizontally contained
                    if (shape.getBoundingMaxY() < getCenterY()) { // top
                        if (shape.getBoundingMinY() > getMinY()) {
                            addToTopLeft(entity);
                            removeEntityFromList(index);
                        } else {
                            parent.relocateUp(entity);
                            postRelocateRemove(index);
                        }
                    } else if (shape.getBoundingMinY() > getCenterY()) { // bottom
                        if (shape.getBoundingMaxY() < getMaxY()) {
                            addToBottomLeft(entity);
                            removeEntityFromList(index);
                        } else {
                            parent.relocateDown(entity);
                            postRelocateRemove(index);
                        }
                    } else {
                        index++;
                    }
                } else {
                    parent.relocateLeft(entity);
                    postRelocateRemove(index);
                }
            } else if (shape.getBoundingMinX() > getCenterX()) { // right
                if (shape.getBoundingMaxX() < getMaxX()) { // horizontally contained
                    if (shape.getBoundingMaxY() < getCenterY()) { // top
                        if (shape.getBoundingMinY() > getMinY()) {
                            addToTopRight(entity);
                            removeEntityFromList(index);
                        } else {
                            parent.relocateUp(entity);
                            postRelocateRemove(index);
                        }
                    } else if (shape.getBoundingMinY() > getCenterY()) { // bottom
                        if (shape.getBoundingMaxY() < getMaxY()) {
                            addToBottomRight(entity);
                            removeEntityFromList(index);
                        } else {
                            parent.relocateDown(entity);
                            postRelocateRemove(index);
                        }
                    } else {
                        index++;
                    }
                } else {
                    parent.relocateRight(entity);
                    postRelocateRemove(index);
                }
            } else if (shape.getBoundingMinY() <= getMinY()) {
                parent.relocateUp(entity);
                postRelocateRemove(index);
            } else if (shape.getBoundingMaxY() >= getMaxY()) {
                parent.relocateDown(entity);
                postRelocateRemove(index);
            } else {
                index++;
            }
        }
        topLeft.ensureEntitiesAreContained(time);
        topRight.ensureEntitiesAreContained(time);
        bottomLeft.ensureEntitiesAreContained(time);
        bottomRight.ensureEntitiesAreContained(time);
    }

    private void postRelocateRemove(int i) {
        removeEntityFromList(i);
        setEntityCount(getEntityCount() - 1);
    }

    @Override
    public Tree tryResize() {
        if (getEntityCount() == 0) {
            Leaf leaf = Leaf.getInstance(parent, getCenterX(), getCenterY(), getHalfLength());
            recycle();
            return leaf;
        }
        topLeft = topLeft.tryResize();
        topRight = topRight.tryResize();
        bottomLeft = bottomLeft.tryResize();
        bottomRight = bottomRight.tryResize();
        return this;
    }

    @Override
    public void updateEntities(double elapsedTime) {
        topLeft.updateEntities(elapsedTime);
        topRight.updateEntities(elapsedTime);
        bottomLeft.updateEntities(elapsedTime);
        bottomRight.updateEntities(elapsedTime);
        for (Entity entity : entities) {
            entity.update(elapsedTime);
        }
    }

    @Override
    public void updateEntityPositions(double elapsedTime) {
        topLeft.updateEntityPositions(elapsedTime);
        topRight.updateEntityPositions(elapsedTime);
        bottomLeft.updateEntityPositions(elapsedTime);
        bottomRight.updateEntityPositions(elapsedTime);
        for (Entity entity : entities) {
            entity.updatePosition(elapsedTime);
        }
    }

    @Override
    public void updateEntityMotions(double elapsedTime) {
        topLeft.updateEntityMotions(elapsedTime);
        topRight.updateEntityMotions(elapsedTime);
        bottomLeft.updateEntityMotions(elapsedTime);
        bottomRight.updateEntityMotions(elapsedTime);
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
        checkCollisionInSubTrees(collisionGroups, temp, result, entity);
    }

    private void checkCollisionInSubTrees(int[] collisionGroups, Collision temp, Collision result, Entity entity) {
        checkHalfTree(collisionGroups, temp, result, entity, topLeft, bottomLeft);
        checkHalfTree(collisionGroups, temp, result, entity, topRight, bottomRight);
    }

    private void checkHalfTree(int[] collisionGroups, Collision temp, Collision result, Entity entity, Tree top, Tree bottom) {
        Shape shape = entity.getShape();
        if (Math.abs(top.getCenterX() - shape.getBoundingCenterX()) < shape.getBoundingHalfWidth() + top.getHalfLength()) {
            if (Math.abs(top.getCenterY() - shape.getBoundingCenterY()) < shape.getBoundingHalfHeight() + top.getHalfLength()) {
                top.checkCollisionWithEntity(collisionGroups, temp, result, entity);
            }
            if (Math.abs(bottom.getCenterY() - shape.getBoundingCenterY()) < shape.getBoundingHalfHeight() + bottom.getHalfLength()) {
                bottom.checkCollisionWithEntity(collisionGroups, temp, result, entity);
            }
        }
    }

    private void checkEntityCollisionsWithinTree(int[] collisionGroups, Collision temp, Collision result) {

        for (int i = 0; i < entityListPos; i++) {
            Entity entity = entities[i];
            Shape a = entity.getShape();
            for (int j = i + 1; j < entityListPos; j++) {
                Shape b = entities[j].getShape();
                if (doShapeTypesCollide(collisionGroups, a, b)) {
                    Shape.collideShapes(a, b, result.getCollisionTime(), temp);
                    if (temp.getCollisionTime() < result.getCollisionTime()) {
                        result.set(temp);
                    }
                }
            }
            checkCollisionInSubTrees(collisionGroups, temp, result, entity);
        }
    }

    @Override
    public void calcCollision(int[] collisionGroups, Collision temp, Collision result) {
        topLeft.calcCollision(collisionGroups, temp, result);
        topRight.calcCollision(collisionGroups, temp, result);
        bottomLeft.calcCollision(collisionGroups, temp, result);
        bottomRight.calcCollision(collisionGroups, temp, result);

        checkEntityCollisionsWithinTree(collisionGroups, temp, result);
    }

    @Override
    public void decrementEntityCount() {
        setEntityCount(getEntityCount() - 1);
        parent.decrementEntityCount();
    }

    @Override
    public void recycle() {
        topLeft.recycle();
        topRight.recycle();
        bottomLeft.recycle();
        bottomRight.recycle();
        clear();
        recycledQuads.push(this);
    }

    @Override
    public void draw(Graphics2D g, Color color) {
        Color deeperColor = color;
        topLeft.draw(g, deeperColor);
        topRight.draw(g, deeperColor);
        bottomLeft.draw(g, deeperColor);
        bottomRight.draw(g, deeperColor);
        g.setColor(Color.DARK_GRAY);
        g.drawLine((int) getMinX(), (int) getCenterY(), (int) getMaxX(), (int) getCenterY());
        g.drawLine((int) getCenterX(), (int) getMinY(), (int) getCenterX(), (int) getMaxY());
        g.setColor(color);
        int offset = 10;
        int size = offset * 2;
        g.fillRect((int) getCenterX() - offset, (int) getCenterY() - offset, size, size);
        g.setColor(Color.WHITE);

        String string = "" + getEntityCount();
        FontMetrics metrics = g.getFontMetrics();
        Rectangle2D rect = metrics.getStringBounds(string, g);

        g.drawString(string, (int) (getCenterX() - rect.getWidth() / 2), (int) (getCenterY()));

        g.setColor(Color.RED);
        offset = 3;
        for (int i = 0; i < entityListPos; i++) {
            Entity entity = entities[i];
            g.drawLine((int) entity.getX() + offset, (int) entity.getY() + offset, (int) getCenterX() + offset, (int) getCenterY() + offset);
        }
    }

    @Override
    public void clear() {
        topLeft.clear();
        topRight.clear();
        bottomLeft.clear();
        bottomRight.clear();
        super.clear();
    }

    @Override
    public void relocateLeft(Entity entity) {
        Shape shape = entity.getShape();
        if (shape.getBoundingMaxX() < getCenterX()) {
            // left
            if (shape.getBoundingMinX() > getMinX()) {
                // contained horizontally
                if (shape.getBoundingMaxY() < getCenterY()) {
                    // top
                    if (shape.getBoundingMinY() > getMinY()) {
                        // contained
                        addToTopLeft(entity);
                    } else {
                        parent.relocateUp(entity);
                        setEntityCount(getEntityCount() - 1);
                    }
                } else if (shape.getBoundingMinY() > getCenterY()) {
                    // bottom
                    if (shape.getBoundingMaxY() < getMaxY()) {
                        // contained
                        addToBottomLeft(entity);
                    } else {
                        parent.relocateDown(entity);
                        setEntityCount(getEntityCount() - 1);
                    }
                } else {
                    addToThis(entity);
                }
            } else {
                parent.relocateLeft(entity);
                setEntityCount(getEntityCount() - 1);
            }
        } else {
            addToThis(entity);
        }
    }

    @Override
    public void relocateRight(Entity entity) {
        Shape shape = entity.getShape();
        if (shape.getBoundingMinX() > getCenterX()) {
            // right
            if (shape.getBoundingMaxX() < getMaxX()) {
                // contained horizontally
                if (shape.getBoundingMaxY() < getCenterY()) {
                    // top
                    if (shape.getBoundingMinY() > getMinY()) {
                        // contained
                        addToTopRight(entity);
                    } else {
                        parent.relocateUp(entity);
                        setEntityCount(getEntityCount() - 1);
                    }
                } else if (shape.getBoundingMinY() > getCenterY()) {
                    // bottom
                    if (shape.getBoundingMaxY() < getMaxY()) {
                        // contained
                        addToBottomRight(entity);
                    } else {
                        parent.relocateDown(entity);
                        setEntityCount(getEntityCount() - 1);
                    }
                } else {
                    addToThis(entity);
                }
            } else {
                parent.relocateRight(entity);
                setEntityCount(getEntityCount() - 1);
            }
        } else {
            addToThis(entity);
        }
    }

    @Override
    public void relocateUp(Entity entity) {
        Shape shape = entity.getShape();
        if (shape.getBoundingMaxY() < getCenterY()) {
            // up
            if (shape.getBoundingMinY() > getMinY()) {
                // contained vertically
                if (shape.getBoundingMaxX() < getCenterX()) {
                    // left
                    if (shape.getBoundingMinX() > getMinX()) {
                        // contained
                        addToTopLeft(entity);
                    } else {
                        parent.relocateLeft(entity);
                        setEntityCount(getEntityCount() - 1);
                    }
                } else if (shape.getBoundingMinX() > getCenterX()) {
                    // right
                    if (shape.getBoundingMaxX() < getMaxX()) {
                        // contained
                        addToTopRight(entity);
                    } else {
                        parent.relocateRight(entity);
                        setEntityCount(getEntityCount() - 1);
                    }
                } else {
                    addToThis(entity);
                }
            } else {
                parent.relocateUp(entity);
                setEntityCount(getEntityCount() - 1);
            }
        } else {
            addToThis(entity);
        }
    }

    @Override
    public void relocateDown(Entity entity) {
        Shape shape = entity.getShape();
        if (shape.getBoundingMinY() > getCenterY()) {
            // down
            if (shape.getBoundingMaxY() < getMaxY()) {
                // contained vertically
                if (shape.getBoundingMaxX() < getCenterX()) {
                    // left
                    if (shape.getBoundingMinX() > getMinX()) {
                        // contained
                        addToBottomLeft(entity);
                    } else {
                        parent.relocateLeft(entity);
                        setEntityCount(getEntityCount() - 1);
                    }
                } else if (shape.getBoundingMinX() > getCenterX()) {
                    // right
                    if (shape.getBoundingMaxX() < getMaxX()) {
                        // contained
                        addToBottomRight(entity);
                    } else {
                        parent.relocateRight(entity);
                        setEntityCount(getEntityCount() - 1);
                    }
                } else {
                    addToThis(entity);
                }
            } else {
                parent.relocateDown(entity);
                setEntityCount(getEntityCount() - 1);
            }
        } else {
            addToThis(entity);
        }
    }
}

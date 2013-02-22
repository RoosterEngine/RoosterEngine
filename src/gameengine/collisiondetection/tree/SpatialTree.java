package gameengine.collisiondetection.tree;

import gameengine.collisiondetection.Collision;
import gameengine.collisiondetection.shapes.Shape;
import gameengine.context.Context;
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
    CollisionList list = new CollisionList();

    public SpatialTree(double centerX, double centerY, double halfLength) {
        tree = Leaf.createInstance(this, centerX, centerY, halfLength, list);
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
        tree.clear(list);
        tree.recycle();
        // TODO tree already removes all the nodes from the list, may not have to do this
        list.clear();
        tree = Leaf.createInstance(this, initCenterX, initCenterY, initHalfLength, list);
    }

    public void ensureEntitiesAreContained(double time) {
        assert tree.isEntityCountCorrect();

        tree.ensureEntitiesAreContained(time);

        assert tree.isEntityCountCorrect();
    }

    public void calcCollision(int[] collisionGroups, double elapsedTime, Context context) {
        assert list.doAllNodesHaveNoCollision();
        assert list.areNodesSorted();
        assert tree.isEntityCountCorrect();

        double currentTime = 0;
        double timeLeft = elapsedTime;
        tree.initCalcCollision(collisionGroups, tempCollision, timeLeft, list);

        assert tree.isEntityCountCorrect();
        assert list.areNodesSorted();

        Collision collision = list.getNextCollision();
        double timeToUpdate = collision.getCollisionTime();
        while (timeToUpdate < timeLeft) {
            Entity a = collision.getA();
            Entity b = collision.getB();
            a.getContainingTree().updateEntityPositions(currentTime);
            b.getContainingTree().updateEntityPositions(currentTime);

            currentTime = collision.getCollisionTime();
            Parent aParent = a.getContainingTree().getParent();
            Parent bParent = b.getContainingTree().getParent();
            context.handleCollision(collision);

            assert tree.isEntityCountCorrect();

            timeLeft -= timeToUpdate;
            Tree aTree = a.getContainingTree();
            Tree bTree = b.getContainingTree();
            if (aTree != null) {
                aTree.removeEntityFromList(a.getIndexInTree());
                a.getShape().calculateBoundingBox(timeLeft);
                if (bTree != null) {
                    bTree.removeEntityFromList(b.getIndexInTree());
                    b.getShape().calculateBoundingBox(timeLeft);
                    bTree.entityUpdated(collisionGroups, tempCollision, timeLeft, b, list);
                } else {
                    bParent.entityRemovedDuringCollision(collisionGroups, tempCollision, timeLeft, b, list);
                }
                aTree.entityUpdated(collisionGroups, tempCollision, timeLeft, a, list);
            } else if (b.getContainingTree() != null) {
                bTree.removeEntityFromList(b.getIndexInTree());
                aParent.entityRemovedDuringCollision(collisionGroups, tempCollision, timeLeft, a, list);
                b.getShape().calculateBoundingBox(timeLeft);
                bTree.entityUpdated(collisionGroups, tempCollision, timeLeft, b, list);
            } else {
                aParent.entityRemovedDuringCollision(collisionGroups, tempCollision, timeLeft, a, list);
                bParent.entityRemovedDuringCollision(collisionGroups, tempCollision, timeLeft, b, list);
            }

            assert tree.isEntityCountCorrect();

            collision = list.getNextCollision();
            timeToUpdate = collision.getCollisionTime() - currentTime;
        }
        tree.updateAllEntityPositions(elapsedTime);
        assert list.doAllNodesHaveNoCollision();
        assert tree.isEntityCountCorrect();

//        tree.updateEntityPositions(timeLeft);

        assert tree.isEntityCountCorrect();
    }

    @Override
    public void decrementEntityCount() {
    }

    @Override
    public void entityRemovedDuringCollision(int[] collisionGroups, Collision temp, double timeToCheck, Entity entity,
                                             CollisionList list) {
    }

    public void tryResize() {
        assert tree.isEntityCountCorrect();

        tree = tree.tryResize(list);

        assert tree.isEntityCountCorrect();
    }

    private void grow(double centerX, double centerY, double halfLength,
                      Tree topLeft, Tree topRight, Tree bottomLeft, Tree bottomRight) {
        double quartLength = halfLength / 2;
        double left = centerX - quartLength;
        double right = centerX + quartLength;
        double top = centerY - quartLength;
        double bottom = centerY + quartLength;
        topLeft.resize(left, top, quartLength);
        topRight.resize(right, top, quartLength);
        bottomLeft.resize(left, bottom, quartLength);
        bottomRight.resize(right, bottom, quartLength);
        tree = Quad.createInstance(this, centerX, centerY, halfLength, topLeft, topRight, bottomLeft, bottomRight, list);
    }

    @Override
    public void relocateAndCheck(int[] collisionGroups, Collision temp, double timeToCheck, Entity entity,
                                 CollisionList list) {
        relocate(entity);
        tree.relocateAndCheck(collisionGroups, temp, timeToCheck, entity, list);
    }

    @Override
    public void relocate(Entity entity) {
        Shape shape = entity.getShape();
        double centerX = tree.getCenterX(), centerY = tree.getCenterY();
        Tree topLeft, topRight, bottomLeft, bottomRight;

        if (shape.getX() < tree.getCenterX()) {
            centerX -= tree.getHalfLength();
            topLeft = Leaf.createInstance(list);
            bottomLeft = Leaf.createInstance(list);
            if (shape.getY() < tree.getCenterY()) {
                centerY -= tree.getHalfLength();
                topRight = Leaf.createInstance(list);
                bottomRight = tree;
            } else {
                centerY += tree.getHalfLength();
                topRight = tree;
                bottomRight = Leaf.createInstance(list);
            }
        } else {
            centerX += tree.getHalfLength();
            topRight = Leaf.createInstance(list);
            bottomRight = Leaf.createInstance(list);
            if (shape.getY() < tree.getCenterY()) {
                centerY -= tree.getHalfLength();
                topLeft = Leaf.createInstance(list);
                bottomLeft = tree;
            } else {
                centerY += tree.getHalfLength();
                topLeft = tree;
                bottomLeft = Leaf.createInstance(list);
            }
        }
        grow(centerX, centerY, tree.getHalfLength() * 2, topLeft, topRight, bottomLeft, bottomRight);
        tree.addEntity(entity);
    }

    public void draw(Graphics2D g, Color color) {
        tree.draw(g, color);
    }
}

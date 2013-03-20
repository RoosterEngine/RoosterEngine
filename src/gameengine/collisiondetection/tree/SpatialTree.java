package gameengine.collisiondetection.tree;

import Utilities.UnorderedArrayList;
import gameengine.collisiondetection.Collision;
import gameengine.collisiondetection.World;
import gameengine.collisiondetection.shapes.Shape;
import gameengine.context.Context;
import gameengine.entities.Entity;
import gameengine.motion.environmentmotions.WorldEffect;

import java.awt.*;

/**
 * The root of the spatial tree, used to access the spatial tree
 * <p/>
 * User: davidrusu
 * Date: 15/01/13
 * Time: 9:31 PM
 */
public class SpatialTree implements Parent {
    private Tree tree;
    private Collision tempCollision = new Collision();
    private double initCenterX, initCenterY, initHalfLength;
    CollisionList list = new CollisionList();
    private World world;

    public SpatialTree(World world, double centerX, double centerY, double halfLength) {
        this.world = world;
        tree = Leaf.createInstance(world, this, centerX, centerY, halfLength, list);
        initCenterX = centerX;
        initCenterY = centerY;
        initHalfLength = halfLength;
    }

    public void addEntity(Entity entity) {
        // TODO enforce adding an entity only once
        entity.calculateBoundingBox(0);
        if (isNotContainedInTree(entity)) {
            relocate(entity);
        } else {
            tree.addEntity(entity);
        }
    }

    public void clear() {
        tree.clear(list);
        tree.recycle();
        // TODO tree already removes all the nodes from the list, may not have to do this
        list.clear();
        tree = Leaf.createInstance(world, this, initCenterX, initCenterY, initHalfLength, list);
    }

    public void ensureEntitiesAreContained(double time) {
        assert tree.isEntityCountCorrect();

        tree.ensureEntitiesAreContained(time);

        assert tree.isEntityCountCorrect();
    }

    public void updateMotions(double elapsedTime, UnorderedArrayList<WorldEffect> worldEffects) {
        tree.updateMotions(elapsedTime, worldEffects);
    }

    public void calcCollision(double elapsedTime, Context context) {
        assert list.doAllNodesHaveNoCollision(-1);
        assert list.areNodesSorted();
        assert tree.isEntityCountCorrect();

        double currentTime = 0;
        double timeLeft = elapsedTime;
        tree.initCalcCollision(tempCollision, timeLeft, list);

        assert tree.isEntityCountCorrect();
        assert list.areNodesSorted();

        Collision collision = list.getNextCollision();
        double timeToUpdate = collision.getCollisionTime();
        while (timeToUpdate <= timeLeft) {
            currentTime = collision.getCollisionTime();

            Entity a = collision.getA();
            Entity b = collision.getB();
            assert a != null;
            assert b != null;
            assert a.getContainingTree() != null;
            assert b.getContainingTree() != null;
            a.getContainingTree().updateEntityPositions(currentTime);
            b.getContainingTree().updateEntityPositions(currentTime);

            Tree aTree = a.getContainingTree();
            Tree bTree = b.getContainingTree();
            context.handleCollision(collision);

//            assert ensureNoCollisionAfterHandleCollision(collision);
            assert tree.isEntityCountCorrect();

            timeLeft -= timeToUpdate;
            if (a.getContainingTree() != null) {
                aTree.removeEntityFromList(a.getIndexInTree());
                a.calculateBoundingBox(timeLeft);
                if (b.getContainingTree() != null) {
                    bTree.removeEntityFromList(b.getIndexInTree());
                    b.calculateBoundingBox(timeLeft);
                    bTree.entityUpdated(tempCollision, timeLeft, b, list);
                } else {
                    bTree.entityRemovedDuringCollision(tempCollision, timeLeft, b, currentTime, list);
                }
                aTree.entityUpdated(tempCollision, timeLeft, a, list);
            } else if (b.getContainingTree() != null) {
                bTree.removeEntityFromList(b.getIndexInTree());
                aTree.entityRemovedDuringCollision(tempCollision, timeLeft, a, currentTime, list);
                b.calculateBoundingBox(timeLeft);
                bTree.entityUpdated(tempCollision, timeLeft, b, list);
            } else {
                aTree.entityRemovedDuringCollision(tempCollision, timeLeft, a, currentTime, list);
                bTree.entityRemovedDuringCollision(tempCollision, timeLeft, b, currentTime, list);
            }

            assert tree.isEntityCountCorrect();
            assert list.checkNodeCollision();
            collision = list.getNextCollision();
            timeToUpdate = collision.getCollisionTime() - currentTime;
        }
        tree = tree.updateAllEntitiesAndResize(elapsedTime, list);
        assert list.checkNodeCollision();
        assert list.doAllNodesHaveNoCollision(elapsedTime);
        assert tree.isEntityCountCorrect();
    }

    private boolean ensureNoCollisionAfterHandleCollision(Collision collision) {
        tempCollision.set(collision);
        Entity a = tempCollision.getA();
        Entity b = tempCollision.getB();
        Shape.collideShapes(a.getShape(), b.getShape(), Double.MAX_VALUE, tempCollision);
        if (tempCollision.getCollisionTime() != Shape.NO_COLLISION) {
            throw new AssertionError(tempCollision.getCollisionTime());
        }
        return true;
    }

    @Override
    public void decrementEntityCount() {
    }

    @Override
    public void entityRemovedDuringCollision(Collision temp, double timeToCheck, Entity entity, double currentTime,
                                             CollisionList list) {
    }

    public void tryResize() {
        assert tree.isEntityCountCorrect();

        tree = tree.tryResize(list);

        assert tree.isEntityCountCorrect();
    }

    @Override
    public void childEntityUpdated(Collision temp, double timeToCheck, Entity entity, CollisionList list) {
    }

    @Override
    public void relocateAndCheck(Collision temp, double timeToCheck, Entity entity, CollisionList list) {
        relocate(entity);
        // TODO adding the entity in the relocate method and then removing it here
        assert tree == entity.getContainingTree();
        entity.getContainingTree().removeEntityFromList(entity.getIndexInTree());
        entity.getContainingTree().relocateAndCheck(temp, timeToCheck, entity, list);
    }

    @Override
    public void relocate(Entity entity) {
        Shape shape = entity.getShape();
        double centerX = tree.getCenterX(), centerY = tree.getCenterY();
        Tree topLeft, topRight, bottomLeft, bottomRight;

        if (shape.getX() < tree.getCenterX()) {
            centerX -= tree.getHalfLength();
            topLeft = Leaf.createInstance(world, list);
            bottomLeft = Leaf.createInstance(world, list);
            if (shape.getY() < tree.getCenterY()) {
                centerY -= tree.getHalfLength();
                topRight = Leaf.createInstance(world, list);
                bottomRight = tree;
            } else {
                centerY += tree.getHalfLength();
                topRight = tree;
                bottomRight = Leaf.createInstance(world, list);
            }
        } else {
            centerX += tree.getHalfLength();
            topRight = Leaf.createInstance(world, list);
            bottomRight = Leaf.createInstance(world, list);
            if (shape.getY() < tree.getCenterY()) {
                centerY -= tree.getHalfLength();
                topLeft = Leaf.createInstance(world, list);
                bottomLeft = tree;
            } else {
                centerY += tree.getHalfLength();
                topLeft = tree;
                bottomLeft = Leaf.createInstance(world, list);
            }
        }
        grow(centerX, centerY, tree.getHalfLength() * 2, topLeft, topRight, bottomLeft, bottomRight);
        tree.addEntity(entity);
    }

    public void draw(double minX, double maxX, double minY, double maxY, Graphics2D g) {
        tree.draw(minX, maxX, minY, maxY, g);
    }

    public void drawTree(Graphics2D g, Color color) {
        tree.drawTree(g, color);
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
        tree = Quad.createInstance(
                world, this, centerX, centerY, halfLength, topLeft, topRight, bottomLeft, bottomRight, list);
    }

    private boolean isNotContainedInTree(Entity entity) {
        return !isContained(entity.getBBCenterX(), tree.getCenterX(), entity.getBBHalfWidth())
                || !isContained(entity.getBBCenterY(), tree.getCenterY(), entity.getBBHalfHeight());
    }

    private boolean isContained(double shapePosition, double treePosition, double shapeHalfLength) {
        return Math.abs(treePosition - shapePosition) < tree.getHalfLength() - shapeHalfLength;
    }

    public int getEntityCount() {
        return tree.getEntityCount();
    }
}

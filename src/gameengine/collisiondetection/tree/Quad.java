package gameengine.collisiondetection.tree;

import Utilities.UnorderedArrayList;
import gameengine.collisiondetection.Collision;
import gameengine.collisiondetection.World;
import gameengine.collisiondetection.shapes.Shape;
import gameengine.entities.Entity;
import gameengine.motion.environmentmotions.WorldEffect;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * A node in the spatial tree that has four children
 * User: davidrusu
 * Date: 15/01/13
 * Time: 9:30 PM
 */
public class Quad extends Tree implements Parent {
    private static final int INITIAL_NUM_QUADS = Leaf.INITIAL_NUM_LEAFS / 4 + 1;
    private static final int EXPANSION_FACTOR = 2;
    private static Quad[] recycledQuads = new Quad[INITIAL_NUM_QUADS];
    private static int numRecycledQuads = INITIAL_NUM_QUADS;

    static {
        for (int i = 0; i < INITIAL_NUM_QUADS; i++) {
            recycledQuads[i] = new Quad();
        }
    }

    private Tree topLeft = null;
    private Tree topRight = null;
    private Tree bottomLeft = null;
    private Tree bottomRight = null;

    private Quad() {
        super();
    }

    private Quad(World world, Parent parent, double centerX, double centerY, double halfLength, CollisionList list) {
        super(world, parent, centerX, centerY, halfLength, list);
        initQuads(world, list);
    }

    private Quad(World world, Parent parent, double centerX, double centerY, double halfLength, Tree topLeft, Tree topRight,
                 Tree bottomLeft, Tree bottomRight, CollisionList list) {
        super(world, parent, centerX, centerY, halfLength, list);
        initQuads(topLeft, topRight, bottomLeft, bottomRight);
    }

    public static Quad createInstance(World world, Parent parent, double centerX, double centerY, double halfLength,
                                      CollisionList list) {
        if (numRecycledQuads == 0) {
            return new Quad(world, parent, centerX, centerY, halfLength, list);
        }
        numRecycledQuads--;
        Quad quad = recycledQuads[numRecycledQuads];

        quad.init(world, parent, centerX, centerY, halfLength, list);
        quad.initQuads(world, list);
        return quad;
    }

    public static Quad createInstance(World world, Parent parent, double centerX, double centerY, double halfLength,
                                      Tree topLeft, Tree topRight, Tree bottomLeft, Tree bottomRight,
                                      CollisionList list) {
        if (numRecycledQuads == 0) {
            return new Quad(world, parent, centerX, centerY, halfLength, topLeft, topRight, bottomLeft, bottomRight, list);
        }
        numRecycledQuads--;
        Quad quad = recycledQuads[numRecycledQuads];
        quad.init(world, parent, centerX, centerY, halfLength, list);
        quad.initQuads(topLeft, topRight, bottomLeft, bottomRight);
        return quad;
    }

    @Override
    public void addEntity(Entity entity) {
        insertEntity(entity);
        entityCount++;
    }

    @Override
    public void ensureEntitiesAreContained(double time) {
        int index = 0;
        while (index < entityListPos) {
            Entity entity = entities[index];
            entity.calculateBoundingBox(time);

            if (!isContainedInTree(entity)) {
                assert isEntityCountCorrect();

                preRelocateRemove(index);
                parent.relocate(entity);

                assert isEntityCountCorrect();
            } else if (entity.getBBMinX() > getCenterX()) {
                assert isEntityCountCorrect();
                index = ensureVerticallyContained(
                        index, entity, entity.getBBMinY(), entity.getBBMaxY(), bottomRight, topRight);
                assert isEntityCountCorrect();
            } else if (entity.getBBMaxX() < getCenterX()) {
                assert isEntityCountCorrect();
                index = ensureVerticallyContained(
                        index, entity, entity.getBBMinY(), entity.getBBMaxY(), bottomLeft, topLeft);
                assert isEntityCountCorrect();
            } else {
                index++;
            }
        }

        assert isEntityCountCorrect();
        topLeft.ensureEntitiesAreContained(time);
        topRight.ensureEntitiesAreContained(time);
        bottomLeft.ensureEntitiesAreContained(time);
        bottomRight.ensureEntitiesAreContained(time);

        assert isEntityCountCorrect();
    }

    @Override
    public Tree tryResize(CollisionList list) {
        assert getRealEntityCount() == entityCount : getRealEntityCount() + " " + entityCount;

        if (entityCount == 0) {
            assert world != null;
            Leaf leaf = Leaf.createInstance(world, parent, getCenterX(), getCenterY(), getHalfLength(), list);
            clear(list);
            recycle();
            return leaf;
        }
        topLeft = topLeft.tryResize(list);
        topRight = topRight.tryResize(list);
        bottomLeft = bottomLeft.tryResize(list);
        bottomRight = bottomRight.tryResize(list);

        assert getRealEntityCount() == entityCount : getRealEntityCount() + " " + entityCount;
        return this;
    }

    @Override
    public Tree updateAllEntitiesAndResize(double currentTime, CollisionList list) {
        if (entityCount == 0) {
            assert world != null;
            Leaf leaf = Leaf.createInstance(world, parent, getCenterX(), getCenterY(), getHalfLength(), list);
            clear(list);
            recycle();
            return leaf;
        }
        updateEntityPositions(currentTime);
        updateEntities(currentTime);
        topLeft = topLeft.updateAllEntitiesAndResize(currentTime, list);
        topRight = topRight.updateAllEntitiesAndResize(currentTime, list);
        bottomLeft = bottomLeft.updateAllEntitiesAndResize(currentTime, list);
        bottomRight = bottomRight.updateAllEntitiesAndResize(currentTime, list);
        return this;
    }

    @Override
    public void updateEntityPositions(double currentTime) {
        if (currentTime == timeInTree) {
            return;
        }
        double elapsedTime = currentTime - timeInTree;
        for (int i = 0; i < entityListPos; i++) {
            entities[i].updatePosition(elapsedTime);
        }
        timeInTree = currentTime;
    }

    @Override
    public void updateMotions(double elapsedTime, UnorderedArrayList<WorldEffect> worldEffects) {
        super.updateMotions(elapsedTime, worldEffects);
        topLeft.updateMotions(elapsedTime, worldEffects);
        topRight.updateMotions(elapsedTime, worldEffects);
        bottomLeft.updateMotions(elapsedTime, worldEffects);
        bottomRight.updateMotions(elapsedTime, worldEffects);
    }

    @Override
    public void initCalcCollision(int[] collisionGroups, Collision temp, double timeToCheck, CollisionList list) {
        assert node.getCollision().getCollisionTime() == Shape.NO_COLLISION;
        assert getRealEntityCount() == entityCount : getRealEntityCount() + " " + entityCount;
        timeInTree = 0;

        for (int i = 0; i < entityListPos; i++) {
            Entity a = entities[i];
            for (int j = i + 1; j < entityListPos; j++) {
                collideShapes(collisionGroups, temp, node.getCollision(), timeToCheck, a, entities[j]);
            }
            initCheckCollisionInSubTrees(collisionGroups, temp, node.getCollision(), timeToCheck, a);
        }
        list.collisionUpdated(node);

        topLeft.initCalcCollision(collisionGroups, temp, timeToCheck, list);
        topRight.initCalcCollision(collisionGroups, temp, timeToCheck, list);
        bottomLeft.initCalcCollision(collisionGroups, temp, timeToCheck, list);
        bottomRight.initCalcCollision(collisionGroups, temp, timeToCheck, list);

        assert getRealEntityCount() == entityCount : getRealEntityCount() + " " + entityCount;
    }

    @Override
    public void initCheckCollisionWithEntity(int[] collisionGroups, Collision temp, Collision result,
                                             double timeToCheck, Entity entity) {
        timeInTree = 0;
        for (int i = 0; i < entityListPos; i++) {
            collideShapes(collisionGroups, temp, result, timeToCheck, entity, entities[i]);
        }
        initCheckCollisionInSubTrees(collisionGroups, temp, result, timeToCheck, entity);
    }

    @Override
    public void checkCollisionWithEntity(int[] collisionGroups, Collision temp, Collision result, double timeToCheck,
                                         Entity entity) {
        updateEntityPositions(entity.getContainingTree().timeInTree);
        for (int i = 0; i < entityListPos; i++) {
            collideShapes(collisionGroups, temp, result, timeToCheck, entity, entities[i]);
        }
        checkCollisionInSubTrees(collisionGroups, temp, result, timeToCheck, entity);
    }

    @Override
    public void childEntityUpdated(int[] collisionGroups, Collision temp, double timeToCheck, Entity entity,
                                   CollisionList list) {
        Collision collision = node.getCollision();
        updateEntityPositions(entity.getContainingTree().timeInTree);
        for (int i = 0; i < entityListPos; i++) {
            collideShapes(collisionGroups, temp, collision, timeToCheck, entity, entities[i]);
        }
        list.collisionUpdated(node);
        parent.childEntityUpdated(collisionGroups, temp, timeToCheck, entity, list);
    }

    @Override
    public void relocateAndCheck(int[] collisionGroups, Collision temp, double timeToCheck, Entity entity,
                                 CollisionList list) {
        assert !isEntityInTree(entity) : "Entity should not be in the this tree when this method is called";
        entityCount--;
        Collision collision = node.getCollision();
        if (entity == collision.getA() || entity == collision.getB()) {
            collision.setNoCollision();
            updateEntityPositions(entity.getContainingTree().timeInTree);
            calcCollisionsAtLevel(collisionGroups, temp, timeToCheck, list);
        }
        if (isContainedInTree(entity)) {
            addAndCheck(collisionGroups, temp, timeToCheck, entity, list);
            parent.childEntityUpdated(collisionGroups, temp, timeToCheck, entity, list);
        } else {
            parent.relocateAndCheck(collisionGroups, temp, timeToCheck, entity, list);
        }
    }

    @Override
    public void addAndCheck(int[] collisionGroups, Collision temp, double timeToCheck, Entity entity,
                            CollisionList list) {
        checkCollisionWithEntity(collisionGroups, temp, node.getCollision(), timeToCheck, entity);
        addEntityToList(entity);
        entityCount++;
        list.collisionUpdated(node);
    }

    @Override
    public void entityRemovedDuringCollision(int[] collisionGroups, Collision temp, double timeToCheck, Entity entity,
                                             double currentTime, CollisionList list) {
        assert entity.getContainingTree() == null;
        assert checkEntities();
        assert !isEntityInTree(entity);

        // entityCount has already been decremented by the removeFromWorld method
        Collision collision = node.getCollision();
        if (collision.getA() == entity || collision.getB() == entity) {
            updateEntityPositions(currentTime);
            collision.setNoCollision();
            calcCollisionsAtLevel(collisionGroups, temp, timeToCheck, list);
        }
        parent.entityRemovedDuringCollision(collisionGroups, temp, timeToCheck, entity, currentTime, list);
    }

    @Override
    public void decrementEntityCount() {
        entityCount--;
        parent.decrementEntityCount();
        assert getRealEntityCount() == entityCount : getRealEntityCount() + " " + entityCount;
    }

    @Override
    public void recycle() {
        assert isClean();

        topLeft.recycle();
        topRight.recycle();
        bottomLeft.recycle();
        bottomRight.recycle();
        if (numRecycledQuads == recycledQuads.length) {
            Quad[] temp = new Quad[numRecycledQuads * EXPANSION_FACTOR];
            System.arraycopy(recycledQuads, 0, temp, 0, numRecycledQuads);
            recycledQuads = temp;
        }
        recycledQuads[numRecycledQuads] = this;
        numRecycledQuads++;
    }

    @Override
    public void draw(double minX, double maxX, double minY, double maxY, Graphics2D g) {
        for (int i = 0; i < entityListPos; i++) {
            entities[i].draw(g);
        }
        if (minX < topLeft.getMaxX()) {
            if (minY < topLeft.getMaxY()) {
                topLeft.draw(minX, maxX, minY, maxY, g);
            }
            if (maxY > bottomLeft.getMinY()) {
                bottomLeft.draw(minX, maxX, minY, maxY, g);
            }
        }
        if (maxX > topRight.getMinX()) {
            if (minY < topRight.getMaxY()) {
                topRight.draw(minX, maxX, minY, maxY, g);
            }
            if (maxY > bottomRight.getMinY()) {
                bottomRight.draw(minX, maxX, minY, maxY, g);
            }
        }
    }

    @Override
    public void drawTree(Graphics2D g, Color color) {
        topLeft.drawTree(g, color);
        topRight.drawTree(g, color);
        bottomLeft.drawTree(g, color);
        bottomRight.drawTree(g, color);
        g.setColor(color);
        g.drawLine((int) getMinX(), (int) getCenterY(), (int) getMaxX(), (int) getCenterY());
        g.drawLine((int) getCenterX(), (int) getMinY(), (int) getCenterX(), (int) getMaxY());
//        drawNumEntities(g, Color.BLACK);
    }

    @Override
    public int getRealEntityCount() {
        int count = 0;
        for (int i = 0; i < entities.length; i++) {
            Entity entity = entities[i];
            if (i >= entityListPos) {
                assert entity == null;
            } else {
                assert entity != null;
                count++;
            }
        }
        count += topLeft.getRealEntityCount();
        count += topRight.getRealEntityCount();
        count += bottomLeft.getRealEntityCount();
        count += bottomRight.getRealEntityCount();
        return count;
    }

    @Override
    public void clear(CollisionList list) {
        super.clear(list);
        topLeft.clear(list);
        topRight.clear(list);
        bottomLeft.clear(list);
        bottomRight.clear(list);
    }

    public void relocate(Entity entity) {
        if (!isContainedInTree(entity)) {
            entityCount--;
            parent.relocate(entity);
        } else {
            insertEntity(entity);
        }
    }

    private void drawNumEntities(Graphics2D g, Color color) {
        g.setColor(color);
        int offset = 10;
        int size = offset * 2;
        g.fillRect((int) getCenterX() - offset, (int) getCenterY() - offset, size, size);
        g.setColor(Color.WHITE);

        String string = "" + entityCount;
        FontMetrics metrics = g.getFontMetrics();
        Rectangle2D rect = metrics.getStringBounds(string, g);

        g.drawString(string, (int) (getCenterX() - rect.getWidth() / 2), (int) (getCenterY()));
    }

    private void calcCollisionsAtLevel(int[] collisionGroups, Collision temp, double timeToCheck, CollisionList list) {
        for (int i = 0; i < entityListPos; i++) {
            Entity a = entities[i];
            for (int j = i + 1; j < entityListPos; j++) {
                collideShapes(collisionGroups, temp, node.getCollision(), timeToCheck, a, entities[j]);
            }
            checkCollisionInSubTrees(collisionGroups, temp, node.getCollision(), timeToCheck, a);
        }
        list.collisionUpdated(node);
    }

    private void checkHalfTree(int[] collisionGroups, Collision temp, Collision result, double timeToCheck,
                               Entity entity, Tree top, Tree bottom) {
        if (Math.abs(top.getCenterX() - entity.getBBCenterX())
                < entity.getBBHalfWidth() + top.getHalfLength()) {
            if (Math.abs(top.getCenterY() - entity.getBBCenterY())
                    < entity.getBBHalfHeight() + top.getHalfLength()) {
                top.checkCollisionWithEntity(collisionGroups, temp, result, timeToCheck, entity);
            }
            if (Math.abs(bottom.getCenterY() - entity.getBBCenterY())
                    < entity.getBBHalfHeight() + bottom.getHalfLength()) {
                bottom.checkCollisionWithEntity(collisionGroups, temp, result, timeToCheck, entity);
            }
        }
    }

    private void checkCollisionInSubTrees(int[] collisionGroups, Collision temp, Collision result, double timeToCheck,
                                          Entity entity) {
        checkHalfTree(collisionGroups, temp, result, timeToCheck, entity, topLeft, bottomLeft);
        checkHalfTree(collisionGroups, temp, result, timeToCheck, entity, topRight, bottomRight);
    }

    private void initCheckCollisionInSubTrees(int[] collisionGroups, Collision temp, Collision result,
                                              double timeToCheck, Entity entity) {
        initCheckHalfTree(collisionGroups, temp, result, timeToCheck, entity, topLeft, bottomLeft);
        initCheckHalfTree(collisionGroups, temp, result, timeToCheck, entity, topRight, bottomRight);
    }

    private void initCheckHalfTree(int[] collisionGroups, Collision temp, Collision result, double timeToCheck,
                                   Entity entity, Tree top, Tree bottom) {
        if (Math.abs(top.getCenterX() - entity.getBBCenterX())
                < entity.getBBHalfWidth() + top.getHalfLength()) {
            if (Math.abs(top.getCenterY() - entity.getBBCenterY())
                    < entity.getBBHalfHeight() + top.getHalfLength()) {
                top.initCheckCollisionWithEntity(collisionGroups, temp, result, timeToCheck, entity);
            }
            if (Math.abs(bottom.getCenterY() - entity.getBBCenterY())
                    < entity.getBBHalfHeight() + bottom.getHalfLength()) {
                bottom.initCheckCollisionWithEntity(collisionGroups, temp, result, timeToCheck, entity);
            }
        }
    }

    private int ensureVerticallyContained(int index, Entity entity, double minY, double maxY, Tree bottom, Tree top) {
        if (minY > getCenterY()) {
            removeEntityFromList(index);
            bottom.addEntity(entity);
        } else if (maxY < getCenterY()) {
            removeEntityFromList(index);
            top.addEntity(entity);
        } else {
            return index + 1;
        }
        return index;
    }

    private void addToThis(Entity entity) {
        addEntityToList(entity);
    }

    private void insertVertically(Entity entity, Tree top, Tree bottom) {
        if (entity.getBBMaxY() < getCenterY()) {
            top.addEntity(entity);
        } else if (entity.getBBMinY() > getCenterY()) {
            bottom.addEntity(entity);
        } else {
            addToThis(entity);
        }
    }

    private void insertEntity(Entity entity) {
        assert checkEntities();
        assert entity != null;

        if (entity.getBBMaxX() < getCenterX()) {
            insertVertically(entity, topLeft, bottomLeft);
        } else if (entity.getBBMinX() > getCenterX()) {
            insertVertically(entity, topRight, bottomRight);
        } else {
            addToThis(entity);
        }
        assert checkEntities();
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
        entityCount = topLeft.entityCount + topRight.entityCount + bottomLeft.entityCount + bottomRight.entityCount;
    }

    private void initQuads(World world, CollisionList list) {
        double quadLength = getHalfLength() * 0.5;
        double left = getCenterX() - quadLength;
        double right = getCenterX() + quadLength;
        double top = getCenterY() - quadLength;
        double bottom = getCenterY() + quadLength;

        topLeft = Leaf.createInstance(world, this, left, top, quadLength, list);
        topRight = Leaf.createInstance(world, this, right, top, quadLength, list);
        bottomLeft = Leaf.createInstance(world, this, left, bottom, quadLength, list);
        bottomRight = Leaf.createInstance(world, this, right, bottom, quadLength, list);
    }
}

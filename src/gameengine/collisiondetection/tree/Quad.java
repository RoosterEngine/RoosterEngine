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

    private Quad(World world, Parent parent, double centerX, double centerY, double halfLength) {
        super(world, parent, centerX, centerY, halfLength);
        initQuads(world);
    }

    private Quad(World world, Parent parent, double centerX, double centerY, double halfLength, Tree topLeft,
                 Tree topRight, Tree bottomLeft, Tree bottomRight) {
        super(world, parent, centerX, centerY, halfLength);
        initQuads(topLeft, topRight, bottomLeft, bottomRight);
    }

    public static Quad createInstance(World world, Parent parent, double centerX, double centerY, double halfLength) {
        if (numRecycledQuads == 0) {
            return new Quad(world, parent, centerX, centerY, halfLength);
        }
        numRecycledQuads--;
        Quad quad = recycledQuads[numRecycledQuads];

        quad.init(world, parent, centerX, centerY, halfLength);
        quad.initQuads(world);
        return quad;
    }

    public static Quad createInstance(World world, Parent parent, double centerX, double centerY, double halfLength,
                                      Tree topLeft, Tree topRight, Tree bottomLeft, Tree bottomRight) {
        if (numRecycledQuads == 0) {
            return new Quad(world, parent, centerX, centerY, halfLength, topLeft, topRight, bottomLeft, bottomRight);
        }
        numRecycledQuads--;
        Quad quad = recycledQuads[numRecycledQuads];
        quad.init(world, parent, centerX, centerY, halfLength);
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
        entityCount = topLeft.entityCount + topRight.entityCount + bottomLeft.entityCount + bottomRight.entityCount;
    }

    private void initQuads(World world) {
        double quadLength = getHalfLength() * 0.5;
        double left = getCenterX() - quadLength;
        double right = getCenterX() + quadLength;
        double top = getCenterY() - quadLength;
        double bottom = getCenterY() + quadLength;

        topLeft = Leaf.createInstance(world, this, left, top, quadLength);
        topRight = Leaf.createInstance(world, this, right, top, quadLength);
        bottomLeft = Leaf.createInstance(world, this, left, bottom, quadLength);
        bottomRight = Leaf.createInstance(world, this, right, bottom, quadLength);
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
    public Tree tryResize() {
        assert getRealEntityCount() == entityCount : getRealEntityCount() + " " + entityCount;

        if (entityCount == 0) {
            assert world != null;
            Leaf leaf = Leaf.createInstance(world, parent, getCenterX(), getCenterY(), getHalfLength());
            clear();
            recycle();
            return leaf;
        }
        topLeft = topLeft.tryResize();
        topRight = topRight.tryResize();
        bottomLeft = bottomLeft.tryResize();
        bottomRight = bottomRight.tryResize();

        assert getRealEntityCount() == entityCount : getRealEntityCount() + " " + entityCount;
        return this;
    }

    @Override
    public Tree updateAllEntitiesAndResize(double currentTime) {
        if (entityCount == 0) {
            assert world != null;
            Leaf leaf = Leaf.createInstance(world, parent, getCenterX(), getCenterY(), getHalfLength());
            clear();
            recycle();
            return leaf;
        }
        updateEntityPositions(currentTime);
        updateEntities(currentTime);
        topLeft = topLeft.updateAllEntitiesAndResize(currentTime);
        topRight = topRight.updateAllEntitiesAndResize(currentTime);
        bottomLeft = bottomLeft.updateAllEntitiesAndResize(currentTime);
        bottomRight = bottomRight.updateAllEntitiesAndResize(currentTime);
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
    public void initCalcCollision(double timeToCheck) {
        assert node.getCollision().getCollisionTime() == Shape.NO_COLLISION;
        assert getRealEntityCount() == entityCount : getRealEntityCount() + " " + entityCount;
        timeInTree = 0;

        for (int i = 0; i < entityListPos; i++) {
            Entity a = entities[i];
            for (int j = i + 1; j < entityListPos; j++) {
                collideShapes(node.getCollision(), timeToCheck, a, entities[j]);
            }
            initCheckCollisionInSubTrees(node.getCollision(), timeToCheck, a);
        }
        world.getCollisionList().collisionUpdated(node);

        topLeft.initCalcCollision(timeToCheck);
        topRight.initCalcCollision(timeToCheck);
        bottomLeft.initCalcCollision(timeToCheck);
        bottomRight.initCalcCollision(timeToCheck);

        assert getRealEntityCount() == entityCount : getRealEntityCount() + " " + entityCount;
    }

    @Override
    public void initCheckCollisionWithEntity(Collision result, double timeToCheck, Entity entity) {
        timeInTree = 0;
        for (int i = 0; i < entityListPos; i++) {
            collideShapes(result, timeToCheck, entity, entities[i]);
        }
        initCheckCollisionInSubTrees(result, timeToCheck, entity);
    }

    @Override
    public void checkCollisionWithEntity(Collision result, double timeToCheck, Entity entity) {
        updateEntityPositions(entity.getContainingTree().timeInTree);
        for (int i = 0; i < entityListPos; i++) {
            collideShapes(result, timeToCheck, entity, entities[i]);
        }
        checkCollisionInSubTrees(result, timeToCheck, entity);
    }

    @Override
    public void childEntityUpdated(double timeToCheck, Entity entity) {
        Collision collision = node.getCollision();
        updateEntityPositions(entity.getContainingTree().timeInTree);
        for (int i = 0; i < entityListPos; i++) {
            collideShapes(collision, timeToCheck, entity, entities[i]);
        }
        world.getCollisionList().collisionUpdated(node);
        parent.childEntityUpdated(timeToCheck, entity);
    }

    @Override
    public void relocateAndCheck(double timeToCheck, Entity entity) {
        assert !isEntityInTree(entity) : "Entity should not be in the this tree when this method is called";
        entityCount--;
        Collision collision = node.getCollision();
        if (entity == collision.getA() || entity == collision.getB()) {
            collision.setNoCollision();
            updateEntityPositions(entity.getContainingTree().timeInTree);
            calcCollisionsAtLevel(timeToCheck);
        }
        if (isContainedInTree(entity)) {
            addAndCheck(timeToCheck, entity);
            parent.childEntityUpdated(timeToCheck, entity);
        } else {
            parent.relocateAndCheck(timeToCheck, entity);
        }
    }

    @Override
    public void addAndCheck(double timeToCheck, Entity entity) {
        checkCollisionWithEntity(node.getCollision(), timeToCheck, entity);
        addEntityToList(entity);
        entityCount++;
        world.getCollisionList().collisionUpdated(node);
    }

    @Override
    public void entityRemovedDuringCollision(double timeToCheck, Entity entity, double currentTime) {
        assert entity.getContainingTree() == null;
        assert checkEntities();
        assert !isEntityInTree(entity);

        // entityCount has already been decremented by the removeFromWorld method
        Collision collision = node.getCollision();
        if (collision.getA() == entity || collision.getB() == entity) {
            updateEntityPositions(currentTime);
            collision.setNoCollision();
            calcCollisionsAtLevel(timeToCheck);
        }
        parent.entityRemovedDuringCollision(timeToCheck, entity, currentTime);
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
    public void clear() {
        super.clear();
        topLeft.clear();
        topRight.clear();
        bottomLeft.clear();
        bottomRight.clear();
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

    private void calcCollisionsAtLevel(double timeToCheck) {
        for (int i = 0; i < entityListPos; i++) {
            Entity a = entities[i];
            for (int j = i + 1; j < entityListPos; j++) {
                collideShapes(node.getCollision(), timeToCheck, a, entities[j]);
            }
            checkCollisionInSubTrees(node.getCollision(), timeToCheck, a);
        }
        world.getCollisionList().collisionUpdated(node);
    }

    private void checkHalfTree(Collision result, double timeToCheck, Entity entity, Tree top, Tree bottom) {
        if (Math.abs(top.getCenterX() - entity.getBBCenterX())
                < entity.getBBHalfWidth() + top.getHalfLength()) {
            if (Math.abs(top.getCenterY() - entity.getBBCenterY())
                    < entity.getBBHalfHeight() + top.getHalfLength()) {
                top.checkCollisionWithEntity(result, timeToCheck, entity);
            }
            if (Math.abs(bottom.getCenterY() - entity.getBBCenterY())
                    < entity.getBBHalfHeight() + bottom.getHalfLength()) {
                bottom.checkCollisionWithEntity(result, timeToCheck, entity);
            }
        }
    }

    private void checkCollisionInSubTrees(Collision result, double timeToCheck, Entity entity) {
        checkHalfTree(result, timeToCheck, entity, topLeft, bottomLeft);
        checkHalfTree( result, timeToCheck, entity, topRight, bottomRight);
    }

    private void initCheckCollisionInSubTrees(Collision result, double timeToCheck, Entity entity) {
        initCheckHalfTree(result, timeToCheck, entity, topLeft, bottomLeft);
        initCheckHalfTree(result, timeToCheck, entity, topRight, bottomRight);
    }

    private void initCheckHalfTree(Collision result, double timeToCheck, Entity entity, Tree top, Tree bottom) {
        if (Math.abs(top.getCenterX() - entity.getBBCenterX())
                < entity.getBBHalfWidth() + top.getHalfLength()) {
            if (Math.abs(top.getCenterY() - entity.getBBCenterY())
                    < entity.getBBHalfHeight() + top.getHalfLength()) {
                top.initCheckCollisionWithEntity(result, timeToCheck, entity);
            }
            if (Math.abs(bottom.getCenterY() - entity.getBBCenterY())
                    < entity.getBBHalfHeight() + bottom.getHalfLength()) {
                bottom.initCheckCollisionWithEntity(result, timeToCheck, entity);
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
}

package gameengine.collisiondetection.tree;

import Utilities.UnorderedArrayList;
import gameengine.collisiondetection.Collision;
import gameengine.collisiondetection.World;
import gameengine.collisiondetection.shapes.Shape;
import gameengine.entities.Entity;
import gameengine.motion.environmentmotions.WorldEffect;

import java.awt.*;

/**
 * Base class of spatial tree nodes
 * <p/>
 * User: davidrusu
 * Date: 15/01/13
 * Time: 9:27 PM
 */
public abstract class Tree {
    public static final int GROW_THRESH = 18;
    private static final double EXPAND_RATE = 1.5;
    protected World world = null;
    protected double centerX, centerY, halfLength, minX, minY, maxX, maxY;
    protected double timeInTree = 0;
    protected Entity[] entities = new Entity[GROW_THRESH + 2];
    protected int entityListPos, entityCount;
    protected Parent parent;
    protected CollisionNode node = new CollisionNode();

    public Tree() {
    }

    public Tree(World world, CollisionList list) {
        assert world != null;
        this.world = world;
        init(world, list);
    }

    public Tree(World world, Parent parent, double centerX, double centerY, double halfLength, CollisionList list) {
        assert world != null;
        this.world = world;
        init(world, parent, centerX, centerY, halfLength, list);
    }

    public double getCenterX() {
        return centerX;
    }

    public double getCenterY() {
        return centerY;
    }

    public double getHalfLength() {
        return halfLength;
    }

    public double getMinX() {
        return minX;
    }

    public double getMinY() {
        return minY;
    }

    public double getMaxX() {
        return maxX;
    }

    public double getMaxY() {
        return maxY;
    }

    public CollisionNode getNode() {
        return node;
    }

    public Parent getParent() {
        return parent;
    }

    public void clear(CollisionList list) {
        for (int i = 0; i < entityListPos; i++) {
            entities[i] = null;
        }
        parent = null;
        entityCount = 0;
        entityListPos = 0;
        timeInTree = 0;
        list.remove(node);
        world = null;
        node.clear();
    }

    public boolean isContainedInTree(Entity entity) {
        return isContained(entity.getBBCenterX(), getCenterX(), entity.getBBHalfWidth())
                && isContained(entity.getBBCenterY(), getCenterY(), entity.getBBHalfHeight());
    }

    private boolean isContained(double shapePosition, double treePosition, double shapeHalfLength) {
        return Math.abs(treePosition - shapePosition) <= getHalfLength() - shapeHalfLength;
    }

    public void removeEntityFromList(int index) {
        assert entityListPos > 0;
        entityListPos--;
        Entity relocated = entities[entityListPos];
        entities[index] = relocated;
        relocated.setIndexInTree(index);
        entities[entityListPos] = null;
    }

    public void removeEntityFromWorld(Entity entity) {
        entityCount--;
        removeEntityFromList(entity.getIndexInTree());
        entity.setContainingTree(null, -1);
        parent.decrementEntityCount();
    }

    public void entityUpdated(Collision tempCollision, double timeToCheck, Entity entity,
                              CollisionList list) {
        assert list.areNodesSorted();
        assert !isEntityInTree(entity);

        relocateAndCheck(tempCollision, timeToCheck, entity, list);
    }

    protected void collideShapes(int[] collisionGroups, Collision temp, Collision result,
                                 double timeToCheck, Entity a, Entity b) {
        if ((collisionGroups[a.getEntityType()] & b.getEntityTypeBitMask()) != 0) {
            temp.setNoCollision();
            Shape.collideShapes(a.getShape(), b.getShape(), timeToCheck, temp);
            if (temp.getCollisionTime() < result.getCollisionTime() - timeInTree) {
                assert temp.getCollisionTime() <= timeToCheck : "too long" + temp.getCollisionTime() + ", " + timeToCheck;
                result.set(temp);
                result.setCollisionTime(result.getCollisionTime() + timeInTree);
            }
        }
    }

    protected void preRelocateRemove(int i) {
        removeEntityFromList(i);
        entityCount--;
    }

    /**
     * Adds the specified entity to the list of entities that are contained in
     * this tree.
     * Does not increment entityCount
     *
     * @param entity the entity to add
     */
    protected void addEntityToList(Entity entity) {
        if (entityListPos == entities.length) {
            Entity[] temp = entities;
            entities = new Entity[(int) (temp.length * EXPAND_RATE) + 1];
            System.arraycopy(temp, 0, entities, 0, entityListPos);
        }
        entities[entityListPos] = entity;
        entity.setContainingTree(this, entityListPos);
        entityListPos++;
    }

    protected void init(World world, CollisionList list) {
        assert world != null;
        this.world = world;
        list.add(node);
    }

    protected void init(World world, Parent parent, double centerX, double centerY, double halfLength, CollisionList list) {
        assert world != null;
        this.world = world;
        this.parent = parent;
        list.add(node);
        resize(centerX, centerY, halfLength);
    }

    protected void resize(double centerX, double centerY, double halfLength) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.halfLength = halfLength;
        minX = centerX - halfLength;
        minY = centerY - halfLength;
        maxX = centerX + halfLength;
        maxY = centerY + halfLength;
    }

    public void updateEntities(double elapsedTime) {
        for (int i = 0; i < entityListPos; i++) {
            entities[i].update(elapsedTime);
        }
    }

    public void updateMotions(double elapsedTime, UnorderedArrayList<WorldEffect> worldEffects) {
        for (int i = 0; i < entityListPos; i++) {
            Entity entity = entities[i];
            int collisionTypeBitMask = entity.getEntityTypeBitMask();
            for (int j = 0; j < worldEffects.size(); j++) {
                WorldEffect worldEffect = worldEffects.get(j);
                if (worldEffect.isCollisionTypeAffected(collisionTypeBitMask)) {
                    worldEffect.applyEffect(entity);
                }
            }
            entity.updateMotion(elapsedTime);
        }
    }

    //------------------------------ testing methods --------------------------------

    public boolean areEntityIndexesNull() {
        for (int i = 0; i < entities.length; i++) {
            if (entities[i] != null) {
                return false;
            }
        }
        return true;
    }

    public boolean isClean() {
        assert areEntityIndexesNull() : "all indexes in 'entities' should be null";
        assert entityCount == 0 : "entityCount: " + entityCount;
        assert entityListPos == 0 : "entityListPos: " + entityListPos;
        assert parent == null : "parent: " + parent;
        assert node.getPrev() == null && node.getNext() == null : "node.prev: " + node.getPrev() + " node.next: " + node.getNext();
        assert timeInTree == 0;
        assert world == null;
        return true;
    }

    public boolean doesEntitysIndexMatchIndexInTree(Entity entity) {
        assert entity.getIndexInTree() < entityListPos : "entities index must be less than entityListPos: "
                + entityListPos + ", " + entity.getIndexInTree();
        return entity == entities[entity.getIndexInTree()];
    }

    public boolean isEntityInTree(Entity entity) {
        for (int i = 0; i < entityListPos; i++) {
            if (entities[i] == entity) {
                return true;
            }
        }
        return false;
    }

    public boolean isEntityCountCorrect() {
        assert getRealEntityCount() == entityCount : getRealEntityCount() + " " + entityCount;
        return true;
    }

    public boolean checkEntities() {
        for (int i = 0; i < entityListPos; i++) {
            Entity entity = entities[i];
            assert entity != null;
            assert entity.getContainingTree() == this;
            assert entity.getIndexInTree() == i;
        }
        for (int i = entityListPos; i < entities.length; i++) {
            assert entities[i] == null;
        }
        return true;
    }

    public abstract int getRealEntityCount();

    //----------------------------- end testing methods -----------------------------

    /**
     * Used to add {@link Entity} to the {@link Tree}.
     *
     * @param entity the {@link Entity} to add
     */
    public abstract void addEntity(Entity entity);

    public abstract void ensureEntitiesAreContained(double time);

    public abstract Tree updateAllEntitiesAndResize(double currentTime, CollisionList list);

    public abstract void updateEntityPositions(double elapsedTime);

    public abstract Tree tryResize(CollisionList list);

    public abstract void initCalcCollision(Collision temp, double timeToCheck, CollisionList list);

    public abstract void relocateAndCheck(Collision temp, double timeToCheck, Entity entity,
                                          CollisionList list);

    public abstract void entityRemovedDuringCollision(Collision temp, double timeToCheck,
                                                      Entity entity, double currentTime,
                                                      CollisionList list);

    public abstract void addAndCheck(Collision temp, double timeToCheck, Entity entity,
                                     CollisionList list);

    public abstract void initCheckCollisionWithEntity(Collision temp, Collision result,
                                                      double timeToCheck, Entity entity);

    public abstract void checkCollisionWithEntity(Collision temp, Collision result,
                                                  double timeToCheck, Entity entity);

    public abstract void recycle();

    public abstract void draw(double minX, double maxX, double minY, double maxY, Graphics2D g);

    public abstract void drawTree(Graphics2D g, Color color);

    public int getEntityCount() {
        return entityCount;
    }
}

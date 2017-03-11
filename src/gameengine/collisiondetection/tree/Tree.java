package gameengine.collisiondetection.tree;

import Utilities.UnorderedArrayList;
import gameengine.collisiondetection.Collision;
import gameengine.collisiondetection.World;
import gameengine.collisiondetection.shapes.Shape;
import gameengine.entities.Entity;
import gameengine.entities.RegionSensor;
import gameengine.graphics.RColor;
import gameengine.graphics.Renderer;
import gameengine.motion.environmentmotions.WorldEffect;

/**
 * Base class of spatial tree nodes.
 *
 * @author davidrusu
 */
public abstract class Tree {
    public static final int GROW_THRESH = 2;
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

    public Tree(World world) {
        assert world != null;
        this.world = world;
        init(world);
    }

    public Tree(World world, Parent parent, double centerX, double centerY, double halfLength) {
        assert world != null;
        this.world = world;
        init(world, parent, centerX, centerY, halfLength);
    }

    protected void init(World world) {
        assert world != null;
        this.world = world;
        world.getCollisionList().add(node);
    }

    protected void init(World world, Parent parent, double centerX, double centerY, double
            halfLength) {
        assert world != null;
        this.world = world;
        this.parent = parent;
        world.getCollisionList().add(node);
        resize(centerX, centerY, halfLength);
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

    public int getEntityCount() {
        return entityCount;
    }

    public void clear() {
        for (int i = 0; i < entityListPos; i++) {
            entities[i] = null;
        }
        parent = null;
        entityCount = 0;
        entityListPos = 0;
        timeInTree = 0;
        world.getCollisionList().remove(node);
        world = null;
        node.clear();
    }

    public boolean isContainedInTree(Entity entity) {
        return isContained(entity.getBBCenterX(), getCenterX(), entity.getBBHalfWidth()) &&
                isContained(entity.getBBCenterY(), getCenterY(), entity.getBBHalfHeight());
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

    public void entityUpdated(double timeToCheck, Entity entity) {
        assert world.getCollisionList().areNodesSorted();
        assert !isEntityInTree(entity);

        relocateAndCheck(timeToCheck, entity);
    }

    protected void collideShapes(Collision result, double timeToCheck, Entity a, Entity b) {
        if ((world.getCollisionGroups()[a.getEntityType()] & b.getEntityTypeBitMask()) == 0 || a
                instanceof RegionSensor && ((RegionSensor) a).containsEntity(b) || b instanceof
                RegionSensor && ((RegionSensor) b).containsEntity(a)) {
            return;
        }
        Collision temp = world.getTempCollision();
        temp.setNoCollision(); // TODO might not need to do this because collideShapes
        // overwrites temp anyway
        Shape.collideShapes(a, b, timeToCheck, temp);
        if (temp.getCollisionTime() < result.getCollisionTime() - timeInTree) {
            assert temp.getCollisionTime() <= timeToCheck : "too long" + temp.getCollisionTime()
                    + ", " + timeToCheck;
            result.set(temp);
            result.setCollisionTime(result.getCollisionTime() + timeInTree);
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

    protected void resize(double centerX, double centerY, double halfLength) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.halfLength = halfLength;
        minX = centerX - halfLength;
        minY = centerY - halfLength;
        maxX = centerX + halfLength;
        maxY = centerY + halfLength;
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

    public void updateEntities(double elapsedTime) {
        for (int i = 0; i < entityListPos; i++) {
            entities[i].update(elapsedTime);
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
        assert node.getPrev() == null && node.getNext() == null : "node.prev: " + node.getPrev()
                + " node.next: " + node.getNext();
        assert timeInTree == 0;
        assert world == null;
        return true;
    }

    public boolean doesEntitysIndexMatchIndexInTree(Entity entity) {
        assert entity.getIndexInTree() < entityListPos : "entities index must be less than " +
                "entityListPos: " + entityListPos + ", " + entity.getIndexInTree();
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

    public abstract Tree updateAllEntityPositionsAndResize(double currentTime);

    public abstract void updateEntityPositions(double elapsedTime);

    public abstract Tree tryResize();

    public abstract void initCalcCollision(double timeToCheck);

    public abstract void relocateAndCheck(double timeToCheck, Entity entity);

    public abstract void entityRemovedDuringCollision(double timeToCheck, Entity entity, double
            currentTime);

    public abstract void addAndCheck(double timeToCheck, Entity entity);

    public abstract void initCheckCollisionWithEntity(Collision result, double timeToCheck,
                                                      Entity entity);

    public abstract void checkCollisionWithEntity(Collision result, double timeToCheck, Entity
            entity);

    public abstract void recycle();

    public abstract void draw(double minX, double maxX, double minY, double maxY, Renderer
            renderer);

    public abstract void drawTree(Renderer renderer, RColor color);
}

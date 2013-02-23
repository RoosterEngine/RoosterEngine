package gameengine.collisiondetection.tree;

import gameengine.collisiondetection.Collision;
import gameengine.collisiondetection.shapes.Shape;
import gameengine.entities.Entity;

import java.awt.*;

/**
 * Base class of spatial tree nodes
 *
 * User: davidrusu
 * Date: 15/01/13
 * Time: 9:27 PM
 */
public abstract class Tree {
    public static final int GROW_THRESH = 15;
    private static final double EXPAND_RATE = 1.5;
    protected double timeInTree = 0;
    protected Entity[] entities = new Entity[GROW_THRESH + 2];
    protected int entityListPos, entityCount;
    protected Parent parent;
    protected CollisionNode node = new CollisionNode();
    private double centerX, centerY, halfLength, minX, minY, maxX, maxY;

    public Tree() {
    }

    public Tree(CollisionList list) {
        init(list);
    }

    public Tree(Parent parent, double centerX, double centerY, double halfLength, CollisionList list) {
        init(parent, centerX, centerY, halfLength, list);
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
        list.remove(this);
        node.clear();
    }

    public boolean isContainedInTree(Entity entity) {
        Shape shape = entity.getShape();
        return isContained(shape.getBoundingCenterX(), getCenterX(), shape.getBoundingHalfWidth())
                && isContained(shape.getBoundingCenterY(), getCenterY(), shape.getBoundingHalfHeight());
    }

    public void removeEntityFromList(int index) {
        entityListPos--;
        Entity relocated = entities[entityListPos];
        entities[index] = relocated;
        relocated.setIndexInTree(index);
        entities[entityListPos] = null;
    }

    public void removeEntityFromWorld(Entity item) {
        entityCount--;
        removeEntityFromList(item.getIndexInTree());
        item.setContainingTree(null, -1);
        parent.decrementEntityCount();
    }

    public void entityUpdated(int[] collisionGroups, Collision tempCollision, double timeToCheck, Entity entity,
                              CollisionList list) {
        assert doesEntitysIndexMatchIndexInTree(entity) : "entity index doesn't match index in the tree " + entity.getIndexInTree();
        assert list.areNodesSorted();
        assert !isEntityInTree(entity);

        relocateAndCheck(collisionGroups, tempCollision, timeToCheck, entity, list);
    }

    protected void collideShapes(int[] collisionGroups, Collision temp, Collision result,
                                 double timeToCheck, Shape a, Shape b) {
        if ((collisionGroups[a.getCollisionType()] & 1 << b.getCollisionType()) != 0) {
            temp.setNoCollision();
            Shape.collideShapes(a, b, timeToCheck, temp);
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

    protected void init(CollisionList list) {
        list.add(this);
    }

    protected void init(Parent parent, double centerX, double centerY, double halfLength, CollisionList list) {
        this.parent = parent;
        list.add(this);
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

    private boolean isContained(double shapePosition, double treePosition, double shapeHalfLength) {
        return Math.abs(treePosition - shapePosition) < getHalfLength() - shapeHalfLength;
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
        return getRealEntityCount() == entityCount;
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
     * <p>
     * This method returns a {@link Tree} because when this tree is a
     * {@link Leaf} and the number of entities in the tree is above the
     * GROW_THRESH, the leaf will return a {@link Quad} to replace the
     * leaf. A call to this method inside the tree should look like:
     * </p>
     * <code>
     * tree = tree.addEntity(entity);
     * </code>
     *
     * @param entity the {@link Entity} to add
     * @return the {@link Tree} that will replace this tree
     */
    public abstract void addEntity(Entity entity);

    public abstract void ensureEntitiesAreContained(double time);

    public abstract void updateAllEntityPositions(double currentTime);

    public abstract void updateEntityPositions(double elapsedTime);

    public abstract Tree tryResize(CollisionList list);

    public abstract void initCalcCollision(int[] collisionGroups, Collision temp, double timeToCheck,
                                           CollisionList list);

    public abstract void relocateAndCheck(int[] collisionGroups, Collision temp, double timeToCheck, Entity entity,
                                          CollisionList list);

    public abstract void addAndCheck(int[] collisionGroups, Collision temp, double timeToCheck, Entity entity,
                                     CollisionList list);

    public abstract void initCheckCollisionWithEntity(int[] collisionGroups, Collision temp, Collision result,
                                                      double timeToCheck, Entity entity);

    public abstract void checkCollisionWithEntity(int[] collisionGroups, Collision temp, Collision result,
                                                  double timeToCheck, Entity entity);

    public abstract void recycle();

    public abstract void draw(double minX, double maxX, double minY, double maxY, Graphics2D g);

    public abstract void drawTree(Graphics2D g, Color color);
}

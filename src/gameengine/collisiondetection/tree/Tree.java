package gameengine.collisiondetection.tree;

import gameengine.collisiondetection.Collision;
import gameengine.collisiondetection.shapes.Shape;
import gameengine.entities.Entity;

import java.awt.*;

/**
 * documentation
 * User: davidrusu
 * Date: 15/01/13
 * Time: 9:27 PM
 */
public abstract class Tree {
    public static final int GROW_THRESH =15;
    private static final double EXPAND_RATE = 1.5;
    private double centerX, centerY, halfLength, minX, minY, maxX, maxY;
    protected double timeInTree = 0;
    protected Entity[] entities = new Entity[GROW_THRESH + 2];
    protected int entityListPos, entityCount;
    protected Parent parent;
    protected CollisionNode node = new CollisionNode();

    public Tree() {
    }

    public Tree(CollisionList list) {
        init(list);
    }

    public Tree(Parent parent, double centerX, double centerY, double halfLength, CollisionList list) {
        init(parent, centerX, centerY, halfLength, list);
    }

    public void init(CollisionList list) {
        list.add(this);
    }

    protected void init(Parent parent, double centerX, double centerY, double halfLength, CollisionList list) {
        this.parent = parent;
        list.add(this);
        resize(centerX, centerY, halfLength);
    }

    protected void resize(double centerX, double centerY, double halfLength) {
        this.setCenterX(centerX);
        this.setCenterY(centerY);
        this.setHalfLength(halfLength);
        setMinX(centerX - halfLength);
        setMinY(centerY - halfLength);
        setMaxX(centerX + halfLength);
        setMaxY(centerY + halfLength);
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
//        node.remove();
    }

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

    protected void preRelocateRemove(int i) {
        removeEntityFromList(i);
        entityCount--;
    }

    public boolean isContainedInTree(Entity entity) {
        Shape shape = entity.getShape();
        return isContained(shape.getBoundingCenterX(), getCenterX(), shape.getBoundingHalfWidth())
                && isContained(shape.getBoundingCenterY(), getCenterY(), shape.getBoundingHalfHeight());
    }

    private boolean isContained(double shapePosition, double treePosition, double shapeHalfLength) {
        return Math.abs(treePosition - shapePosition) < getHalfLength() - shapeHalfLength;
    }

    public void removeEntityFromList(int index) {
        entityListPos--;
        Entity relocated = entities[entityListPos];
        entities[index] = relocated;
        relocated.setIndexInTree(index);
        entities[entityListPos] = null;
    }

    public void removeEntityFromWorld(Entity item){
        entityCount--;
        removeEntityFromList(item.getIndexInTree());
        item.setContainingTree(null, -1);
        parent.decrementEntityCount();
    }

    protected void collideShapes(int[] collisionGroups, Collision temp, Collision result,
                                 double timeToCheck, double currentTime, Shape a, Shape b) {
        if ((collisionGroups[a.getCollisionType()] & 1 << b.getCollisionType()) != 0) {
            temp.setNoCollision();
            Shape.collideShapes(a, b, timeToCheck, temp);
            if (temp.getCollisionTime() < result.getCollisionTime() - currentTime) {
                assert temp.getCollisionTime() <= timeToCheck: "too long" + temp.getCollisionTime() + ", " + timeToCheck;
                result.set(temp);
                result.setCollisionTime(result.getCollisionTime() + currentTime);
            }
        }
    }

    public void entityUpdated(int[] collisionGroups, Collision tempCollision, double timeToCheck, Entity entity,
                              CollisionList list) {
        assert doesEntitysIndexMatchIndexInTree(entity) : "entity index doesn't match it's position in the tree " + entity.getIndexInTree();
        assert list.areNodesSorted();
        assert !isEntityInTree(entity);

        relocateAndCheck(collisionGroups, tempCollision, timeToCheck, entity, list);
    }

    public boolean doesEntitysIndexMatchIndexInTree(Entity entity) {
        assert entity.getIndexInTree() < entityListPos : "entities index must be less than entityListPos: "
                + entityListPos + ", " + entity.getIndexInTree();
        return entity == entities[entity.getIndexInTree()];
    }

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

    public abstract void updateEntities(double elapsedTime);

    public abstract void updateAllEntityPositions(double currentTime);

    public abstract void updateEntityPositions(double elapsedTime);

    public abstract Tree tryResize(CollisionList list);

    public abstract void updateEntityMotions(double elapsedTime);

    public abstract void initCalcCollision(int[] collisionGroups, Collision temp, double timeToCheck,
                                           CollisionList list);

    public abstract void calcCollision(int[] collisionGroups, Collision temp, double timeToCheck, CollisionList list);

    public abstract void relocateAndCheck(int[] collisionGroups, Collision temp, double timeToCheck, Entity entity,
                                          CollisionList list);

    public abstract void addAndCheck(int[] collisionGroups, Collision temp, double timeToCheck, Entity entity,
                                     CollisionList list);

    public abstract void initCheckCollisionWithEntity(int[] collisionGroups, Collision temp, Collision result,
                                                  double timeToCheck, Entity entity);

    public abstract void checkCollisionWithEntity(int[] collisionGroups, Collision temp, Collision result,
                                                  double timeToCheck, Entity entity);

    /**
     * When this method is called, the tree should already be cleared and ready to be reused
     */
    public abstract void recycle();

    public abstract void draw(Graphics2D g, Color color);

    public double getCenterX() {
        return centerX;
    }

    public double getCenterY() {
        return centerY;
    }

    public void setCenterX(double centerX) {
        this.centerX = centerX;
    }

    public void setCenterY(double centerY) {
        this.centerY = centerY;
    }

    public double getHalfLength() {
        return halfLength;
    }

    public void setHalfLength(double halfLength) {
        this.halfLength = halfLength;
    }

    public double getMinX() {
        return minX;
    }

    public void setMinX(double minX) {
        this.minX = minX;
    }

    public double getMinY() {
        return minY;
    }

    public void setMinY(double minY) {
        this.minY = minY;
    }

    public double getMaxX() {
        return maxX;
    }

    public void setMaxX(double maxX) {
        this.maxX = maxX;
    }

    public double getMaxY() {
        return maxY;
    }

    public void setMaxY(double maxY) {
        this.maxY = maxY;
    }

    public boolean isEntityInTree(Entity entity) {
        for (int i = 0; i < entityListPos; i++) {
            if (entities[i] == entity) {
                return true;
            }
        }
        return false;
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

    public CollisionNode getNode() {
        return node;
    }

    public int getEntityCount() {
        return entityCount;
    }

    public boolean isEntityCountCorrect() {
//        assert getRealEntityCount() == entityCount : getRealEntityCount() + " " + entityCount;
//        return true;
        return getRealEntityCount() == entityCount;
    }

    public Parent getParent() {
        return parent;
    }
}

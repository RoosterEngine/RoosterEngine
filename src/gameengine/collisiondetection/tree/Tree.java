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
    public static final int GROW_THRESH = 15;
    private static final double EXPAND_RATE = 1.5;
    protected Entity[] entities = new Entity[GROW_THRESH + 2];
    protected int entityListPos, entityCount;
    private double centerX, centerY, halfLength, minX, minY, maxX, maxY;
    protected Parent parent;

    public Tree(Parent parent, double centerX, double centerY, double halfLength) {
        init(parent, centerX, centerY, halfLength);
    }

    public Tree() {
        parent = null;
    }

    protected void init(Parent parent, double centerX, double centerY, double halfLength) {
        this.parent = parent;
        setEntityCount(0);
        entityListPos = 0;
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

    public void clear() {
        for (int i = 0; i < entityListPos; i++) {
            entities[i] = null;
        }
        parent = null;
        setEntityCount(0);
        entityListPos = 0;
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
            grow();
        }
        entities[entityListPos] = entity;
        entityListPos++;
    }

    private void grow() {
        Entity[] temp = entities;
        entities = new Entity[(int) (temp.length * EXPAND_RATE) + 1];
        System.arraycopy(temp, 0, entities, 0, temp.length);
    }

    public void removeEntity(Entity entity) {
        for (int i = 0; i < entityListPos; i++) {
            Entity testEntity = entities[i];
            if (testEntity == entity) {
                removeEntityFromList(i);
                parent.decrementEntityCount();
                setEntityCount(getEntityCount() - 1);
                entity.setContainingTree(null);
                return;
            }
        }
    }

    protected void removeEntityFromList(int index) {
        entityListPos--;
        entities[index] = entities[entityListPos];
        entities[entityListPos] = null;
    }

    protected boolean doShapeTypesCollide(int[] collisionGroups, Shape a, Shape b) {
        return (collisionGroups[a.getCollisionType()] & 1 << b.getCollisionType()) != 0;
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

    public abstract void updateEntityPositions(double elapsedTime);

    public abstract Tree tryResize();

    public abstract void updateEntityMotions(double elapsedTime);

    public abstract void calcCollision(int[] collisionGroups, Collision temp, Collision result);

    public abstract void checkCollisionWithEntity(int[] collisionGroups, Collision temp, Collision result, Entity entity);

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

    public int getEntityCount() {
        return entityCount;
    }

    public void setEntityCount(int entityCount) {
        this.entityCount = entityCount;
    }
}

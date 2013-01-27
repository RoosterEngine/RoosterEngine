package gameengine.collisiondetection.tree;

import gameengine.collisiondetection.Collision;
import gameengine.collisiondetection.shapes.Shape;
import gameengine.entities.Entity;

import java.awt.*;
import java.util.ArrayList;

/**
 * documentation
 * User: davidrusu
 * Date: 15/01/13
 * Time: 9:27 PM
 */
public abstract class Tree {
    public static final int GROW_THRESH = 30;
    protected ArrayList<Entity> entities = new ArrayList<>(GROW_THRESH + 2);
    protected Parent parent;
    protected double centerX, centerY, halfLength;
    protected double minX, minY, maxX, maxY;
    // TODO make sure all methods that add or remove entities affect this counter
    protected int entityCount;

    public Tree(Parent parent, double centerX, double centerY,
                double halfLength) {
        init(parent, centerX, centerY, halfLength);
    }

    public Tree() {
        parent = null;
    }

    protected void init(Parent parent, double centerX, double centerY,
                        double halfLength) {
        this.parent = parent;
        entityCount = 0;
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

    public void clear() {
        entities.clear();
        parent = null;
    }

    protected void checkEntityCollisions(
            int[] collisionGroups, Collision temp, Collision result) {

        int entityCount = entities.size();
        for (int i = 0; i < entityCount; i++) {
            Shape shape1 = entities.get(i).getShape();
            for (int j = i + 1; j < entityCount; j++) {
                Shape shape2 = entities.get(j).getShape();

                if ((collisionGroups[shape1.getCollisionType()]
                        & 1 << shape2.getCollisionType()) != 0) {
                    Shape.collideShapes(
                            shape1, shape2, result.getCollisionTime(), temp);
                    if (temp.getCollisionTime() < result.getCollisionTime()) {
                        result.set(temp);
                    }
                }
            }
        }
        parent.checkForCollisionWithTree(this, collisionGroups, temp, result);
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

    public abstract boolean removeEntity(Entity entity);

    public abstract void ensureEntitiesAreContained(double time);

    public abstract void updateEntities(double elapsedTime);

    public abstract void updateEntityPositions(double elapsedTime);

    public abstract Tree tryResize();

    public abstract void updateEntityMotions(double elapsedTime);

    public abstract void calcCollision(
            int[] collisionGroups, Collision temp, Collision result);

    public abstract void recycle();

    public abstract void draw(Graphics2D g, Color color);

    public double getCenterX() {
        return centerX;
    }

    public double getCenterY() {
        return centerY;
    }
}

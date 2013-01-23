package gameengine.collisiondetection.tree;

import gameengine.collisiondetection.Collision;
import gameengine.collisiondetection.CollisionGroup;
import gameengine.collisiondetection.CollisionPair;
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
    public static final int GROW_THRESH = 3;
    protected ArrayList<Entity> entities = new ArrayList<>();
    protected ArrayList<Shape>[] collisionGroups;
    protected Parent parent;
    protected double centerX, centerY, halfWidth, halfHeight;
    protected double minX, minY, maxX, maxY;
    // TODO make sure all methods that add or remove entities affect this counter
    protected int entityCount;

    public Tree(Parent parent, double centerX, double centerY,
                double halfWidth, double halfHeight) {
        init(parent, centerX, centerY, halfWidth, halfHeight);
        setupCollisionGroups();
    }

    private void setupCollisionGroups() {
        collisionGroups = new ArrayList[CollisionGroup.values().length];
        for (int i = 0; i < collisionGroups.length; i++) {
            collisionGroups[i] = new ArrayList<>();
        }
    }

    public Tree() {
        parent = null;
        setupCollisionGroups();
    }

    protected void init(Parent parent, double centerX, double centerY,
                        double halfWidth, double halfHeight) {
        this.parent = parent;
        entityCount = 0;
        resize(centerX, centerY, halfWidth, halfHeight);
    }

    protected void resize(double centerX, double centerY,
                          double halfWidth, double halfHeight) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.halfWidth = halfWidth;
        this.halfHeight = halfHeight;
        minX = centerX - halfWidth;
        minY = centerY - halfHeight;
        maxX = centerX + halfWidth;
        maxY = centerY + halfHeight;
    }

    public void clear() {
        entities.clear();
        for (ArrayList<Shape> collisionGroup : collisionGroups) {
            collisionGroup.clear();
        }
        parent = null;

        // TODO might not need to do this
        centerX = 0;
        centerY = 0;
        halfWidth = 0;
        halfHeight = 0;
        minX = 0;
        minY = 0;
        maxX = 0;
        maxY = 0;
        entityCount = 0;
    }

    protected void checkCollisionsInSingleList(ArrayList<Shape> list,
                                               Collision result) {
        // TODO get rid of object creation
        Collision temp = new Collision();
        double maxTime = result.getCollisionTime();
        for (int i = 0; i < list.size(); i++) {
            Shape a = list.get(i);
            for (int j = i + 1; j < list.size(); j++) {
                Shape b = list.get(j);
                Shape.collideShapes(a, b, maxTime, temp);
                if (temp.getCollisionTime() < result.getCollisionTime()) {
                    result.set(temp);
                }
            }
        }
    }

    protected void checkCollisionsInLists(ArrayList<Shape> listA,
                                          ArrayList<Shape> listB,
                                          Collision result) {
        // TODO get rid of object creation
        Collision temp = new Collision();
        double maxTime = result.getCollisionTime();
        for (Shape a : listA) {
            for (Shape b : listB) {
                if (a == b) {
                    continue;
                }
                Shape.collideShapes(a, b, maxTime, temp);
                if (temp.getCollisionTime() < result.getCollisionTime()) {
                    result.set(temp);
                }
            }
        }
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
    public abstract Tree addEntity(Entity entity);

    public abstract boolean removeEntity(Entity entity);

    public abstract void addAllEntitiesToTree(Tree tree);

    public abstract void ensureEntitiesAreContained(double time);

    public abstract void updateEntities(double elapsedTime);

    public abstract void updateEntityPositions(double elapsedTime);

    public abstract Tree tryResize();

    public abstract void updateEntityMotions(double elapsedTime);

    public abstract void calcCollision(ArrayList<CollisionPair> collisionPairs,
                                       Collision result);

    public abstract void recycle();

    public abstract void draw(Graphics2D g, Color color);
}

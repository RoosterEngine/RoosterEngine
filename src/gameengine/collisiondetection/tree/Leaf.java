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
public class Leaf extends Tree {
    public static final int INITIAL_NUM_LEAFS = 32;
    private static final int EXPANSION_FACTOR = 2;
    private static Leaf[] recycledLeafs = new Leaf[INITIAL_NUM_LEAFS];
    private static int numRecycledLeafs = INITIAL_NUM_LEAFS;

    static {
        for (int i = 0; i < INITIAL_NUM_LEAFS; i++) {
            recycledLeafs[i] = new Leaf();
        }
    }

    private Leaf(Parent parent, double centerX, double centerY, double halfLength, CollisionList list) {
        super(parent, centerX, centerY, halfLength, list);
    }

    public Leaf(CollisionList list) {
        super(list);
    }

    private Leaf() {
        super();
    }

    public static Leaf createInstance(Parent parent, double centerX, double centerY, double halfLength, CollisionList list) {
        if (numRecycledLeafs == 0) {
            return new Leaf(parent, centerX, centerY, halfLength, list);
        }
        numRecycledLeafs--;
        Leaf leafInstance = recycledLeafs[numRecycledLeafs];
        recycledLeafs[numRecycledLeafs] = null;
        leafInstance.init(parent, centerX, centerY, halfLength, list);
        return leafInstance;
    }

    public static Leaf createInstance(CollisionList list) {
        if (numRecycledLeafs == 0) {
            return new Leaf(list);
        }
        numRecycledLeafs--;
        Leaf leafInstance = recycledLeafs[numRecycledLeafs];
        recycledLeafs[numRecycledLeafs] = null;
        leafInstance.init(list);
        return leafInstance;
    }

    @Override
    public void recycle() {
        assert areEntityIndexesNull() : "all indexes in 'entities' should be null";
        assert entityCount == 0 : "entityCount: " + entityCount;
        assert entityListPos == 0 : "entityListPos: " + entityListPos;
        assert parent == null : "parent: " + parent;
        assert node.getPrev() == null && node.getNext() == null : "node.prev: " + node.getPrev() + " node.next: " + node.getNext();

        if (numRecycledLeafs == recycledLeafs.length) {
            Leaf[] temp = new Leaf[numRecycledLeafs * EXPANSION_FACTOR];
            System.arraycopy(recycledLeafs, 0, temp, 0, numRecycledLeafs);
            recycledLeafs = temp;
        }
        recycledLeafs[numRecycledLeafs] = this;
        numRecycledLeafs++;
    }

    @Override
    public void addEntity(Entity entity) {
        addEntityToList(entity);
        entityCount++;
    }

    @Override
    public void ensureEntitiesAreContained(double time) {
        int index = 0;
        while (index < entityListPos) {
            Entity entity = entities[index];
            Shape shape = entity.getShape();
            shape.calculateBoundingBox(time);

            if (!isContainedInTree(entity)) {
                preRelocateRemove(index);
                parent.relocate(entity);
            } else {
                index++;
            }
        }
    }

    @Override
    public void updateEntities(double elapsedTime) {
        for (int i = 0; i < entityListPos; i++) {
            entities[i].update(elapsedTime);
        }
    }

    @Override
    public Tree tryResize(CollisionList list) {
        if (entityCount >= GROW_THRESH) {
            Quad quad = Quad.createInstance(parent, getCenterX(), getCenterY(), getHalfLength(), list);
            for (int i = 0; i < entityCount; i++) {
                quad.addEntity(entities[i]);
            }
            clear(list);
            recycle();
            return quad;
        }
        return this;
    }

    @Override
    public void updateEntityPositions(double elapsedTime) {
        for (int i = 0; i < entityListPos; i++) {
            entities[i].updatePosition(elapsedTime);
        }
    }

    @Override
    public void updateEntityMotions(double elapsedTime) {
        for (int i = 0; i < entityListPos; i++) {
            entities[i].updateMotion(elapsedTime);
        }
    }

    @Override
    public void checkCollisionWithEntity(int[] collisionGroups, Collision temp, Collision result,
                                         double timeToCheck, double currentTime, Entity entity) {
        Shape a = entity.getShape();
        for (int i = 0; i < entityListPos; i++) {
            Shape b = entities[i].getShape();
            collideShapes(collisionGroups, temp, result, timeToCheck, currentTime, a, b);
        }
    }

    @Override
    public void calcCollision(int[] collisionGroups, Collision temp, double timeToCheck, double currentTime,
                              CollisionList list) {
        assert node.getCollisionTime() == Shape.NO_COLLISION;

        for (int i = 0; i < entityListPos; i++) {
            Shape a = entities[i].getShape();
            for (int j = i + 1; j < entityListPos; j++) {
                Shape b = entities[j].getShape();
                collideShapes(collisionGroups, temp, node.getCollision(), timeToCheck, currentTime, a, b);
            }
        }

        list.collisionUpdated(this);
    }

    @Override
    public void relocateAndCheck(int[] collisionGroups, Collision temp, double timeToCheck, double currentTime,
                                 Entity entity, CollisionList list) {
        assert !isEntityInTree(entity) : "Entity should not be in the this tree when this method is called";

        Collision collision = node.getCollision();
        if (entity == collision.getA() || entity == collision.getB()) {
            collision.setNoCollision();
            calcCollision(collisionGroups, temp, timeToCheck, currentTime, list);
        }

        if (isContainedInTree(entity)) {
            addAndCheck(collisionGroups, temp, timeToCheck, currentTime, entity, list);
        } else {
            entityCount--;
            parent.relocateAndCheck(collisionGroups, temp, timeToCheck, currentTime, entity, list);
        }
    }

    @Override
    public void addAndCheck(int[] collisionGroups, Collision temp, double timeToCheck, double currentTime,
                            Entity entity, CollisionList list) {
//        node.getCollision().setNoCollision();
        checkCollisionWithEntity(collisionGroups, temp, node.getCollision(), timeToCheck, currentTime, entity);
        list.collisionUpdated(this);
        addEntityToList(entity);
    }

    @Override
    public void ensureCollisionIsNotWithEntity(int[] collisionGroups, Collision temp, double timeToCheck,
                                               double currentTime, Entity entity, CollisionList list) {
        Collision collision = node.getCollision();
        if (entity == collision.getA() || entity == collision.getB()) {
            collision.setNoCollision();
            for (int i = 0; i < entityListPos; i++) {
                Entity ent = entities[i];
                Shape a = ent.getShape();
                for (int j = i + 1; j < entityListPos; j++) {
                    Shape b = entities[j].getShape();
                    collideShapes(collisionGroups, temp, collision, timeToCheck, (double) 0, a, b);
                }
            }
            list.collisionUpdated(this);
        }
    }

    @Override
    public void draw(Graphics2D g, Color color) {
    }
}

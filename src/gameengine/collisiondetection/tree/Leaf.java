package gameengine.collisiondetection.tree;

import gameengine.collisiondetection.Collision;
import gameengine.collisiondetection.shapes.Shape;
import gameengine.entities.Entity;

import java.awt.*;

/**
 * The Leaf node of the spatial tree data structure
 * <p/>
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

    private Leaf(CollisionList list) {
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
        assert isClean();

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
                assert isEntityCountCorrect();

                preRelocateRemove(index);
                parent.relocate(entity);

                assert isEntityCountCorrect();
            } else {
                index++;
            }
        }
    }

    @Override
    public Tree tryResize(CollisionList list) {
        assert entityCount == entityListPos : "count: " + entityCount + " pos: " + entityListPos;

        if (entityCount >= GROW_THRESH) {
            assert checkEntities();
            Quad quad = Quad.createInstance(parent, getCenterX(), getCenterY(), getHalfLength(), list);

            assert checkEntities();

            for (int i = 0; i < entityCount; i++) {
                quad.addEntity(entities[i]);
            }
            clear(list);
            recycle();

            assert checkEntities();
            assert getRealEntityCount() == entityCount : getRealEntityCount() + " " + entityCount;
            return quad;
        }
        return this;
    }

    @Override
    public void updateAllEntityPositions(double currentTime) {
        updateEntityPositions(currentTime);
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
    public void initCalcCollision(int[] collisionGroups, Collision temp, double timeToCheck, CollisionList list) {
        timeInTree = 0;
        calcCollision(collisionGroups, temp, timeToCheck, list);
    }

    @Override
    public void initCheckCollisionWithEntity(int[] collisionGroups, Collision temp, Collision result,
                                             double timeToCheck, Entity entity) {
        timeInTree = 0;
        Shape a = entity.getShape();
        for (int i = 0; i < entityListPos; i++) {
            Shape b = entities[i].getShape();
            collideShapes(collisionGroups, temp, result, timeToCheck, a, b);
        }
    }

    @Override
    public void checkCollisionWithEntity(int[] collisionGroups, Collision temp, Collision result, double timeToCheck,
                                         Entity entity) {
        updateEntityPositions(entity.getContainingTree().timeInTree);
        Shape a = entity.getShape();
        for (int i = 0; i < entityListPos; i++) {
            Shape b = entities[i].getShape();
            collideShapes(collisionGroups, temp, result, timeToCheck, a, b);
        }
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
            calcCollision(collisionGroups, temp, timeToCheck, list);
        }

        if (isContainedInTree(entity)) {
            addAndCheck(collisionGroups, temp, timeToCheck, entity, list);
        } else {
            parent.relocateAndCheck(collisionGroups, temp, timeToCheck, entity, list);
        }

        assert entityCount == entityListPos : "count: " + entityCount + " pos: " + entityListPos;
    }

    @Override
    public void addAndCheck(int[] collisionGroups, Collision temp, double timeToCheck,
                            Entity entity, CollisionList list) {
        checkCollisionWithEntity(collisionGroups, temp, node.getCollision(), timeToCheck, entity);
        addEntityToList(entity);
        entityCount++;
        list.collisionUpdated(this);

        assert entityCount == entityListPos : "count: " + entityCount + " pos: " + entityListPos;
    }

    @Override
    public void draw(Graphics2D g, Color color) {
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
        return count;
    }

    private void calcCollision(int[] collisionGroups, Collision temp, double timeToCheck, CollisionList list) {
        assert node.getCollisionTime() == Shape.NO_COLLISION;
        assert entityCount == entityListPos : "count: " + entityCount + " pos: " + entityListPos;

        for (int i = 0; i < entityListPos; i++) {
            Shape a = entities[i].getShape();
            for (int j = i + 1; j < entityListPos; j++) {
                Shape b = entities[j].getShape();
                collideShapes(collisionGroups, temp, node.getCollision(), timeToCheck, a, b);
            }
        }

        list.collisionUpdated(this);

        assert entityCount == entityListPos : "count: " + entityCount + " pos: " + entityListPos;
    }
}

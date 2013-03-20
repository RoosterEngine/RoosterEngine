package gameengine.collisiondetection.tree;

import gameengine.collisiondetection.Collision;
import gameengine.collisiondetection.World;
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

    private Leaf(World world, Parent parent, double centerX, double centerY, double halfLength, CollisionList list) {
        super(world, parent, centerX, centerY, halfLength, list);
    }

    private Leaf(World world, CollisionList list) {
        super(world, list);
    }

    private Leaf() {
        super();
    }

    public static Leaf createInstance(World world, Parent parent, double centerX, double centerY, double halfLength,
                                      CollisionList list) {
        if (numRecycledLeafs == 0) {
            return new Leaf(world, parent, centerX, centerY, halfLength, list);
        }
        numRecycledLeafs--;
        Leaf leafInstance = recycledLeafs[numRecycledLeafs];
        recycledLeafs[numRecycledLeafs] = null;
        leafInstance.init(world, parent, centerX, centerY, halfLength, list);
        return leafInstance;
    }

    public static Leaf createInstance(World world, CollisionList list) {
        if (numRecycledLeafs == 0) {
            return new Leaf(world, list);
        }
        numRecycledLeafs--;
        Leaf leafInstance = recycledLeafs[numRecycledLeafs];
        recycledLeafs[numRecycledLeafs] = null;
        leafInstance.init(world, list);
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
            entity.calculateBoundingBox(time);

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
            assert world != null;
            Quad quad = Quad.createInstance(world, parent, centerX, centerY, halfLength, list);

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
    public Tree updateAllEntitiesAndResize(double currentTime, CollisionList list) {
        assert entityCount == entityListPos : "count: " + entityCount + " pos: " + entityListPos;
        updateEntityPositions(currentTime);
        updateEntities(currentTime);

        if (entityCount >= GROW_THRESH) {
            assert checkEntities();
            assert world != null;
            Quad quad = Quad.createInstance(world, parent, centerX, centerY, halfLength, list);

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
    public void initCalcCollision(Collision temp, double timeToCheck, CollisionList list) {
        timeInTree = 0;
        calcCollision(temp, timeToCheck, list);
    }

    @Override
    public void initCheckCollisionWithEntity(Collision temp, Collision result, double timeToCheck, Entity entity) {
        timeInTree = 0;
        int[] collisionGroups = world.getCollisionGroups();
        for (int i = 0; i < entityListPos; i++) {
            collideShapes(collisionGroups, temp, result, timeToCheck, entity, entities[i]);
        }
    }

    @Override
    public void checkCollisionWithEntity(Collision temp, Collision result, double timeToCheck, Entity entity) {
        updateEntityPositions(entity.getContainingTree().timeInTree);
        int[] collisionGroups = world.getCollisionGroups();
        for (int i = 0; i < entityListPos; i++) {
            collideShapes(collisionGroups, temp, result, timeToCheck, entity, entities[i]);
        }
    }

    @Override
    public void relocateAndCheck(Collision temp, double timeToCheck, Entity entity, CollisionList list) {
        assert !isEntityInTree(entity) :
                "Entity should not be in the this tree when this method is called";
        entityCount--;
        Collision collision = node.getCollision();
        if (entity == collision.getA() || entity == collision.getB()) {
            collision.setNoCollision();
            updateEntityPositions(entity.getContainingTree().timeInTree);
            calcCollision(temp, timeToCheck, list);
        }

        if (isContainedInTree(entity)) {
            addAndCheck(temp, timeToCheck, entity, list);
            parent.childEntityUpdated(temp, timeToCheck, entity, list);
        } else {
            parent.relocateAndCheck(temp, timeToCheck, entity, list);
        }
    }

    @Override
    public void entityRemovedDuringCollision(Collision temp, double timeToCheck, Entity entity, double currentTime,
                                             CollisionList list) {
        assert entity.getContainingTree() == null;
        assert checkEntities();
        assert !isEntityInTree(entity);

        Collision collision = node.getCollision();
        if (collision.getA() == entity || collision.getB() == entity) {
            updateEntityPositions(currentTime);
            collision.setNoCollision();
            calcCollision(temp, timeToCheck, list);
        }
        parent.entityRemovedDuringCollision(temp, timeToCheck, entity, currentTime, list);
    }

    @Override
    public void addAndCheck(Collision temp, double timeToCheck, Entity entity, CollisionList list) {
        checkCollisionWithEntity(temp, node.getCollision(), timeToCheck, entity);
        addEntityToList(entity);
        entityCount++;
        list.collisionUpdated(node);
    }

    @Override
    public void draw(double minX, double maxX, double minY, double maxY, Graphics2D g) {
        for (int i = 0; i < entityListPos; i++) {
            entities[i].draw(g);
        }
    }

    @Override
    public void drawTree(Graphics2D g, Color color) {
        g.setColor(Color.RED);
        int length = (int) (getHalfLength() * 2);
        g.drawRect((int) getMinX(), (int) getMinY(), length, length);
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

    private void calcCollision(Collision temp, double timeToCheck, CollisionList list) {
        assert node.getCollisionTime() == Shape.NO_COLLISION;
        int[] collisionGroups = world.getCollisionGroups();
        Collision collision = node.getCollision();
        for (int i = 0; i < entityListPos; i++) {
            Entity a = entities[i];
            for (int j = i + 1; j < entityListPos; j++) {
                collideShapes(collisionGroups, temp, collision, timeToCheck, a, entities[j]);
            }
        }
        list.collisionUpdated(node);
    }
}

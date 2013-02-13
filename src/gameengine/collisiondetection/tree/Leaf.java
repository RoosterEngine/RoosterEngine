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

    private Leaf(Parent parent, double centerX, double centerY, double halfLength) {
        super(parent, centerX, centerY, halfLength);
    }

    private Leaf() {
        super();
    }

    public static Leaf createInstance(Parent parent, double centerX, double centerY, double halfLength) {
        if (numRecycledLeafs == 0) {
            return new Leaf(parent, centerX, centerY, halfLength);
        }
        numRecycledLeafs--;
        Leaf leafInstance = recycledLeafs[numRecycledLeafs];
        recycledLeafs[numRecycledLeafs] = null;
        leafInstance.init(parent, centerX, centerY, halfLength);
        return leafInstance;
    }

    public static Leaf createInstance() {
        if (numRecycledLeafs == 0) {
            return new Leaf();
        }
        numRecycledLeafs--;
        Leaf leafInstance = recycledLeafs[numRecycledLeafs];
        recycledLeafs[numRecycledLeafs] = null;
        leafInstance.init(null, 0, 0, 0);
        return leafInstance;
    }

    @Override
    public void recycle() {
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
        entity.setContainingTree(this);
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
    public Tree tryResize() {
        if (entityCount >= GROW_THRESH) {
            Quad quad = Quad.createInstance(parent, getCenterX(), getCenterY(), getHalfLength());
            for (int i = 0; i < entityCount; i++) {
                quad.addEntity(entities[i]);
                entities[i] = null;
            }
            parent = null;
            entityCount = 0;
            entityListPos = 0;
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
    public void checkCollisionWithEntity(int[] collisionGroups, Collision temp, Collision result, Entity entity) {
        Shape a = entity.getShape();
        for (int i = 0; i < entityListPos; i++) {
            Shape b = entities[i].getShape();
            collideShapes(collisionGroups, temp, result, a, b);
        }
    }

    @Override
    public void calcCollision(int[] collisionGroups, Collision temp, Collision result) {
        for (int i = 0; i < entityListPos; i++) {
            Shape a = entities[i].getShape();
            for (int j = i + 1; j < entityListPos; j++) {
                Shape b = entities[j].getShape();
                collideShapes(collisionGroups, temp, result, a, b);
            }
        }
    }

    @Override
    public void draw(Graphics2D g, Color color) {
        g.setColor(color.darker());
        int length = (int) (getHalfLength() * 2);
        g.fillRect((int) getMinX(), (int) getMinY(), length, length);
//        g.setColor(Color.RED);
//        int offset = 3;
//        for (int i = 0; i < entityListPos; i++) {
//            Entity entity = entities[i];
//            g.drawLine((int) entity.getX() + offset, (int) entity.getY() + offset, (int) getCenterX() + offset, (int) getCenterY() + offset);
//        }
    }
}

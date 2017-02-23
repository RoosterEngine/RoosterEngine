package gameengine.collisiondetection;

import Utilities.UnorderedArrayList;
import gameengine.collisiondetection.tree.CollisionList;
import gameengine.collisiondetection.tree.SpatialTree;
import gameengine.context.Context;
import gameengine.entities.Entity;
import gameengine.graphics.MutableColor;
import gameengine.graphics.Renderer;
import gameengine.motion.environmentmotions.WorldEffect;

public class World {
    private SpatialTree tree;
    private int[] collisionGroups = new int[EntityType.values().length];
    private Collision tempCollision = new Collision();
    private CollisionList collisionList = new CollisionList();
    private UnorderedArrayList<WorldEffect> worldEffects = new UnorderedArrayList<>();

    public World(double centerX, double centerY, double halfLength) {
        tree = new SpatialTree(this, centerX, centerY, halfLength);
    }

    public void addEnvironmentMotion(WorldEffect worldEffect) {
        worldEffects.add(worldEffect);
    }

    public void addEntity(Entity entity) {
        tree.addEntity(entity);
    }

    public void setCollisionGroup(EntityType a, EntityType b) {
        int x = a.ordinal();
        int y = b.ordinal();

        int mask = 1 << y;
        collisionGroups[x] |= mask;
        mask = 1 << x;
        collisionGroups[y] |= mask;
    }

    /**
     * Sets items of {@link EntityType} 'a' to collide with items of the 'others' types.
     * note: this will not make the items of the 'others' types collide with each other
     *
     * @param a      the {@link EntityType} that will be set to collide with the collision types in
     *               'others'
     * @param others the collision types that will be set to collide with the 'a' collision type
     */
    public void setCollisionGroups(EntityType a, EntityType... others) {
        for (int i = 0; i < others.length; i++) {
            setCollisionGroup(a, others[i]);
        }
    }

    public void clear() {
        for (int i = 0; i < collisionGroups.length; i++) {
            collisionGroups[i] = 0;
        }
        tree.clear();
        worldEffects.clear();
    }

    public void update(double elapsedTime, Context context) {
        for (int i = 0; i < worldEffects.size(); i++) {
            worldEffects.get(i).update(elapsedTime);
        }
        tree.updateMotions(elapsedTime, worldEffects);
        tree.ensureEntitiesAreContained(elapsedTime);
        tree.calcCollision(elapsedTime, context);
    }

    public void draw(Context context, Renderer renderer) {
        Viewport viewPort = context.getViewPort();

        viewPort.applyTransformations(renderer);
        tree.draw(viewPort.getMinX(), viewPort.getMaxX(), viewPort.getMinY(), viewPort.getMaxY(),
                renderer);
//        tree.drawTree(g, MutableColor.RED);
        viewPort.reverseTransformations(renderer);
    }

    public void drawTree(Renderer renderer, MutableColor color) {
        tree.drawTree(renderer, color);
    }

    public int[] getCollisionGroups() {
        return collisionGroups;
    }

    public Collision getTempCollision() {
        return tempCollision;
    }

    public CollisionList getCollisionList() {
        return collisionList;
    }

    public int getEntityCount() {
        return tree.getEntityCount();
    }
}

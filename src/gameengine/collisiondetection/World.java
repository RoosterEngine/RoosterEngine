package gameengine.collisiondetection;

import Utilities.UnsortedArrayList;
import gameengine.collisiondetection.tree.SpatialTree;
import gameengine.context.Context;
import gameengine.entities.Entity;
import gameengine.motion.environmentmotions.WorldEffect;

import java.awt.*;
import java.util.HashSet;

public class World {
    private HashSet<Entity> entities = new HashSet<>();
    private SpatialTree tree;
    private int[] collisionGroups = new int[EntityType.values().length];
    private UnsortedArrayList<Entity> removedEntityBuffer = new UnsortedArrayList<>();
    private UnsortedArrayList<WorldEffect> worldEffects = new UnsortedArrayList<>();

    public World(double centerX, double centerY, double halfLength) {
        tree = new SpatialTree(this, centerX, centerY, halfLength);
    }

    public void addEnvironmentMotion(WorldEffect worldEffect) {
        worldEffects.add(worldEffect);
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
        tree.addEntity(entity);
    }

    public void entityHasBeenRemoved(Entity entity) {
        removedEntityBuffer.add(entity);
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
     * @param a      the {@link EntityType} that will be set to collide with the collision types in 'others'
     * @param others the collision types that will be set to collide with the 'a' collision type
     */
    public void setCollisionGroups(EntityType a, EntityType... others) {
        for (int i = 0; i < others.length; i++) {
            setCollisionGroup(a, others[i]);
        }
    }

    public void removeCollisionGroup(EntityType a, EntityType b) {
        int aOrdinal = a.ordinal();
        int bOrdinal = b.ordinal();
        int mask = 1 << aOrdinal;
        collisionGroups[bOrdinal] &= ~mask;
    }

    public void clear() {
        for (int i = 0; i < collisionGroups.length; i++) {
            collisionGroups[i] = 0;
        }
        entities.clear();
        tree.clear();
        worldEffects.clear();
    }

    public void update(double elapsedTime, Context context) {
        updateMotions(elapsedTime);
        tree.ensureEntitiesAreContained(elapsedTime);
        tree.calcCollision(collisionGroups, elapsedTime, context);
        tree.tryResize();
        updateEntities(elapsedTime);
        removeEntitiesFromBuffer();
        context.update(elapsedTime);
    }

    private void removeEntitiesFromBuffer() {
        for (int i = 0; i < removedEntityBuffer.size(); i++) {
            entities.remove(removedEntityBuffer.get(i));
        }
        removedEntityBuffer.clear();
    }

    public void draw(Context context, Graphics2D g) {
        ViewPort viewPort = context.getViewPort();

        viewPort.applyTransformations(g);
        tree.draw(viewPort.getMinX(), viewPort.getMaxX(), viewPort.getMinY(), viewPort.getMaxY(), g);
        viewPort.reverseTransformations(g);
    }

    public void drawTree(Graphics2D g, Color color) {
        tree.drawTree(g, color);
    }

    private void updateEntities(double elapsedTime) {
        for (Entity entity : entities) {
            entity.update(elapsedTime);
        }
    }

    private void updateMotions(double elapsedTime) {
        for (int i = 0; i < worldEffects.size(); i++) {
            worldEffects.get(i).update(elapsedTime);
        }
        for (Entity entity : entities) {
            int collisionTypeBitMask = entity.getCollisionTypeBitMask();
            for (int i = 0; i < worldEffects.size(); i++) {
                WorldEffect worldEffect = worldEffects.get(i);
                if (worldEffect.isCollisionTypeAffected(collisionTypeBitMask)) {
                    worldEffect.applyMotion(entity);
                }
            }
            entity.updateMotion(elapsedTime);
        }
    }

    public int getEntityCount() {
        return tree.getEntityCount();
    }
}

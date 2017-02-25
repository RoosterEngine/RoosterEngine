package gameengine.motion.environmentmotions;

import gameengine.collisiondetection.EntityType;
import gameengine.entities.Entity;

/**
 * documentation
 *
 * @author davidrusu
 */
public abstract class WorldEffect {
    private int collisionTypes = 0;

    public void addCollisionType(EntityType type) {
        int ordinal = type.ordinal();

        int mask = 1 << ordinal;
        collisionTypes |= mask;
    }

    public void removeCollisionType(EntityType type) {
        int ordinal = type.ordinal();

        int mask = ~(1 << ordinal);
        collisionTypes &= mask;
    }

    public boolean isCollisionTypeAffected(int collisionTypeBitMask) {
        return (collisionTypes & collisionTypeBitMask) != 0;
    }

    public abstract void reset();

    public abstract void update(double elapsedTime);

    public abstract void applyEffect(Entity entity);
}

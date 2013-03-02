package gameengine.motion.environmentmotions;

import gameengine.collisiondetection.CollisionType;
import gameengine.entities.Entity;

/**
 * documentation
 * User: davidrusu
 * Date: 27/02/13
 * Time: 4:45 PM
 */
public abstract class WorldEffect {
    private int collisionTypes = 0;

    public void addCollisionType(CollisionType type) {
        int ordinal = type.ordinal();

        int mask = 1 << ordinal;
        collisionTypes |= mask;
    }

    public void removeCollisionType(CollisionType type) {
        int ordinal = type.ordinal();

        int mask = ~(1 << ordinal);
        collisionTypes &= mask;
    }

    public boolean isCollisionTypeAffected(int collisionTypeBitMask) {
        return (collisionTypes & collisionTypeBitMask) != 0;
    }

    public abstract void reset();

    public abstract void update(double elapsedTime);

    public abstract void applyMotion(Entity entity);
}

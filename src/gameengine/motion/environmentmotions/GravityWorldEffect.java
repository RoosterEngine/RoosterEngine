package gameengine.motion.environmentmotions;

import gameengine.entities.Entity;

/**
 * documentation
 *
 * @author davidrusu
 */
public class GravityWorldEffect extends WorldEffect {
    private double gX, gY, updateGX = 0, updateGY = 0;

    public GravityWorldEffect(double gY) {
        this(0, gY);
    }

    public GravityWorldEffect(double gX, double gY) {
        set(gX, gY);
    }

    public void set(double gX, double gY) {
        this.gX = gX;
        this.gY = gY;
    }

    @Override
    public void reset() {
    }

    @Override
    public void update(double elapsedTime) {
        updateGX = gX * elapsedTime;
        updateGY = gY * elapsedTime;
    }

    @Override
    public void applyEffect(Entity entity) {
        entity.addVelocity(updateGX, updateGY);
    }
}

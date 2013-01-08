package gameengine.motion.motions;

import bricklets.Entity;

/**
 * A {@link Motion} that will always return zero as it's velocity
 *
 * @author davidrusu
 */
public class NoMotion implements Motion {

    public NoMotion() {
    }

    @Override
    public double getVelocityX() {
        return 0;
    }

    @Override
    public double getVelocityY() {
        return 0;
    }

    @Override
    public void reset() {
    }

    @Override
    public void update(Entity entity, double elapsedTime) {
    }
}

package gameengine.motion.motions;

import gameengine.entities.Entity;

/**
 * A {@link Motion} that will return the y velocities from the {@link Motion} passed to it, but will
 * always return 0 as it's x velocity.
 *
 * @author davidrusu
 */
public class VerticalMotionFilter implements Motion {
    private Motion motion;

    public VerticalMotionFilter(Motion motion) {
        this.motion = motion;
    }

    @Override
    public double getVelocityX() {
        return 0;
    }

    @Override
    public double getVelocityY() {
        return motion.getVelocityY();
    }

    @Override
    public void reset() {
        motion.reset();
    }

    /**
     * Updates the {@link Motion} that was supplied when constructed
     *
     * @param entity      The entity to base the update on
     * @param elapsedTime The amount of time to integrate the motion
     */
    @Override
    public void update(Entity entity, double elapsedTime) {
        motion.update(entity, elapsedTime);
    }
}

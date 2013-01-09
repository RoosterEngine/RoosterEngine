package gameengine.motion.motions;

import gameengine.entities.Entity;

/**
 * A {@link Motion} that will return the x velocities from the {@link Motion} passed to it, but will always return 0
 * as it's y velocity
 * User: davidrusu
 * Date: 17/12/12
 * Time: 6:35 PM
 */
public class HorizontalMotionFilter implements Motion {
    private Motion motion;

    public HorizontalMotionFilter(Motion motion) {
        this.motion = motion;
    }

    @Override
    public double getVelocityX() {
        return motion.getVelocityX();
    }

    @Override
    public double getVelocityY() {
        return 0;
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

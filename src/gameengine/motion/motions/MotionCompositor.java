package gameengine.motion.motions;

import bricklets.Entity;

/**
 * Takes a two separate {@link Motion}s, one for horizontal motion and the other
 * for vertical motion.
 *
 * User: davidrusu
 * Date: 29/12/12
 * Time: 12:08 PM
 */
public class MotionCompositor implements Motion {
    private Motion horizontalMotion, verticalMotion;

    public MotionCompositor(Motion horizontalMotion, Motion verticalMotion) {
        this.horizontalMotion = horizontalMotion;
        this.verticalMotion = verticalMotion;
    }

    @Override
    public double getVelocityX() {
        return horizontalMotion.getVelocityX();
    }

    @Override
    public double getVelocityY() {
        return verticalMotion.getVelocityY();
    }

    @Override
    public void reset() {
        verticalMotion.reset();
        horizontalMotion.reset();
    }

    @Override
    public void update(Entity entity, double elapsedTime) {
        verticalMotion.update(entity, elapsedTime);
        horizontalMotion.update(entity, elapsedTime);
    }
}

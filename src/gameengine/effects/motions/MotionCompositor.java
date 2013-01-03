package gameengine.effects.motions;

import bricklets.Entity;

/**
 * Created with IntelliJ IDEA.
 * User: davidrusu
 * Date: 29/12/12
 * Time: 12:08 PM
 * To change this template use File | Settings | File Templates.
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

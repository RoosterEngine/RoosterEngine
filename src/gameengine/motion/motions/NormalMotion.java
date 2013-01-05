package gameengine.motion.motions;

import bricklets.Entity;

/**
 * Created with IntelliJ IDEA.
 * User: davidrusu
 * Date: 29/12/12
 * Time: 12:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class NormalMotion implements Motion {
    private double velocityX, velocityY;

    @Override
    public double getVelocityX() {
        return velocityX;
    }

    @Override
    public double getVelocityY() {
        return velocityY;
    }

    @Override
    public void reset() {
        velocityX = 0;
        velocityY = 0;
    }

    @Override
    public void update(Entity entity, double elapsedTime) {
        velocityX = entity.getDX();
        velocityY = entity.getDY();
    }
}

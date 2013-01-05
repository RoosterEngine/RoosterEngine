package gameengine.motion.motions;

import bricklets.Entity;

/**
 * Created with IntelliJ IDEA.
 * User: davidrusu
 * Date: 14/12/12
 * Time: 9:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class MouseMotion implements Motion {
    private static double mouseVelocityX, mouseVelocityY;

    @Override
    public double getVelocityX() {
        return mouseVelocityX;
    }

    @Override
    public double getVelocityY() {
        return mouseVelocityY;
    }

    @Override
    public void reset() {
    }

    @Override
    public void update(Entity entity, double elapsedTime) {
    }

    /**
     * Sets the velocity that is returned by this {@link MouseMotion}
     * @param velocityX the x velocity of the mouse
     * @param velocityY the y velocity of the mouse
     */
    public static void mouseMoved(double velocityX, double velocityY){
        mouseVelocityX = velocityX;
        mouseVelocityY = velocityY;
    }
}

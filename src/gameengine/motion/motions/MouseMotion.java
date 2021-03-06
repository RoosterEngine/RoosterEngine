package gameengine.motion.motions;

import gameengine.entities.Entity;

/**
 * To be used if you want your entity to be controlled by the mouse.
 *
 * @author davidrusu
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
     *
     * @param velocityX the x velocity of the mouse
     * @param velocityY the y velocity of the mouse
     */
    public static void setVelocity(double velocityX, double velocityY) {
        mouseVelocityX = velocityX;
        mouseVelocityY = velocityY;
    }
}

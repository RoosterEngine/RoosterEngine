package gameengine.motion.motions;

import gameengine.entities.Entity;

/**
 * Motions are used to move your entities.
 * Motions can be used to create complex movement
 * User: davidrusu
 * Date: 08/12/12
 * Time: 8:34 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Motion {

    /**
     * Returns the current x velocity of this Motion
     *
     * @return the current x velocity
     */
    public double getVelocityX();

    /**
     * Returns the current y velocity of this Motion
     *
     * @return the current y velocity
     */
    public double getVelocityY();

    /**
     * Resets the {@link Motion}
     */
    public void reset();

    /**
     * Updates the velocity of the motion by the specified elapsedTime
     *
     * @param entity      The entity to base the update on
     * @param elapsedTime The amount of time to integrate the motion
     */
    public void update(Entity entity, double elapsedTime);
}

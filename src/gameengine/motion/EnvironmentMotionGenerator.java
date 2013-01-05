package gameengine.motion;

import bricklets.Entity;

/**
 * Created with IntelliJ IDEA.
 * User: davidrusu
 * Date: 13/12/12
 * Time: 5:39 PM
 * To change this template use File | Settings | File Templates.
 */
public interface EnvironmentMotionGenerator {

    public double getDeltaVelocityX();

    public double getDeltaVelocityY();

    public void reset();

    public void reset(double x, double y);

    /**
     * Called once per update
     * @param elapsedTime
     */
    public void update(double elapsedTime);

    /**
     * Called once per entity per update
     * @param entity
     */
    public void update(Entity entity);
}

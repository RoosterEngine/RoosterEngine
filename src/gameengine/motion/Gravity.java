package gameengine.motion;

import gameengine.entities.Entity;

/**
 * Created with IntelliJ IDEA.
 * User: davidrusu
 * Date: 13/12/12
 * Time: 5:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class Gravity implements EnvironmentMotionGenerator {
    private double gX, gY;
    private double deltaVelocityX, deltaVelocityY;

    public Gravity(double gX, double gY) {
        this.gX = gX;
        this.gY = gY;
    }

    @Override
    public double getDeltaVelocityX() {
        return deltaVelocityX;
    }

    @Override
    public double getDeltaVelocityY() {
        return deltaVelocityY;
    }

    @Override
    public void reset() {
        deltaVelocityX = 0;
        deltaVelocityY = 0;
    }

    @Override
    public void reset(double x, double y) {
        gX = x;
        gY = y;
        reset();
    }

    @Override
    public void update(double elapsedTime) {
        deltaVelocityX = gX * elapsedTime;
        deltaVelocityY = gY * elapsedTime;
    }

    @Override
    public void update(Entity entity) {
    }
}
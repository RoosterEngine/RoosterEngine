package gameengine.effects;

import bricklets.Entity;

/**
 *
 * @author davidrusu
 */
public class VerticalSpring implements MotionEffect {
    private double destination, velocity, deltaVelocity;
    private Entity entity;
    private Integrator motion;

    public VerticalSpring(Entity entity, double destinationY, Integrator motion){
        destination = destinationY;
        this.motion = motion;
        this.entity = entity;
        reset();
    }

    @Override
    public double getVelocityX() {
        return 0;
    }

    @Override
    public double getVelocityY() {
        return velocity;
    }

    @Override
    public double getDeltaVelocityX() {
        return 0;
    }

    @Override
    public double getDeltaVelocityY() {
        return deltaVelocity;
    }

    @Override
    public void reset(double x, double y){
        destination = y;
        reset();
    }

    @Override
    public final void reset(){
        motion.reset();
        deltaVelocity = 0;
        velocity = 0;
    }

    @Override
    public void update(double elapsedTime) {
        double newVelocity = motion.getVelocity(entity.getY(), destination, velocity, entity.getMass(), elapsedTime);
        deltaVelocity = newVelocity - entity.getDY();
        velocity = newVelocity;
    }
}
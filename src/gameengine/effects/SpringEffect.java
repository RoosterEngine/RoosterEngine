package gameengine.effects;

import bricklets.Entity;

/**
 *
 * @author davidrusu
 */
public class SpringEffect implements MotionEffect {
    private double destinationX, destinationY;
    private double velocityX, velocityY, deltaVelocityX, deltaVelocityY;
    private Entity entity;
    private Integrator motion;

    public SpringEffect(Entity entity, double destinationX, double destinationY, Integrator motion){
        this.destinationX = destinationX;
        this.destinationY = destinationY;
        this.motion = motion;
        this.entity = entity;
        reset();
    }

    @Override
    public double getVelocityX() {
        return velocityX;
    }

    @Override
    public double getVelocityY() {
        return velocityY;
    }

    @Override
    public double getDeltaVelocityX(){
        return deltaVelocityX;
    }

    @Override
    public double getDeltaVelocityY() {
        return deltaVelocityY;
    }

    @Override
    public void reset(){
        deltaVelocityX = 0;
        deltaVelocityY = 0;
        velocityX = 0;
        velocityY = 0;
        motion.reset();
    }
    
    @Override
    public void reset(double x, double y){
        destinationX = x;
        destinationY = y;
        motion.reset();
    }
    
    @Override
    public void update(double elapsedTime) {
        double newVelocityX = motion.getVelocity(entity.getX(), destinationX, entity.getDX(), entity.getMass(), elapsedTime);
        double newVelocityY = motion.getVelocity(entity.getY(), destinationY, entity.getDY(), entity.getMass(), elapsedTime);
        deltaVelocityX = newVelocityX - entity.getDX();
        deltaVelocityY = newVelocityY - entity.getDY();
        velocityX = newVelocityX;
        velocityY = newVelocityY;
    }
}
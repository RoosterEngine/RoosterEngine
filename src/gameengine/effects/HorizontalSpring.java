/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gameengine.effects;

import bricklets.Entity;

/**
 *
 * @author davidrusu
 */
public class HorizontalSpring implements MotionEffect {
    private double velocity, deltaVelocity, destination;
    private Entity entity;
    private Integrator motion;
    
    public HorizontalSpring(Entity entity, double destinationX, Integrator motion){
        this.destination = destinationX;
        this.motion = motion;
        this.entity = entity;
        reset();
    }

    @Override
    public double getVelocityX() {
        return velocity;
    }

    @Override
    public double getVelocityY() {
        return 0;
    }

    @Override
    public double getDeltaVelocityX() {
        return deltaVelocity;
    }

    @Override
    public double getDeltaVelocityY() {
        return 0;
    }
    
    @Override
    public void reset(double x, double y){
        destination = x;
        motion.reset();
        deltaVelocity = 0;
    }
    
    @Override
    public final void reset(){
        motion.reset();
        deltaVelocity = 0;
    }
    
    @Override
    public void update(double elapsedTime) {
        double newVelocity = motion.getVelocity(entity.getX(), destination, entity.getDX(), entity.getMass(), elapsedTime);
        deltaVelocity = newVelocity - entity.getDX();
        velocity = newVelocity;
    }
}
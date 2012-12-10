/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gameengine.effects;

/**
 *
 * @author davidrusu
 */
public class HorizontalSlide implements PositionEffect, VelocityEffect{
    private double destinationX, initialX, initialY, x;
    private double deltaVelocity, currentVelocity;
    private MotionGenerator motion;
    
    public HorizontalSlide(double initialX, double initialY, double destinationX, MotionGenerator motion){
        this.destinationX = destinationX;
        this.initialX = initialX;
        this.initialY = initialY;
        this.motion = motion;
        reset();
    }

    @Override
    public double getX(){
        return x;
    }
    
    @Override
    public double getY(){
        return initialY;
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
    public double getVelocityX() {
        return currentVelocity;
    }

    @Override
    public double getVelocityY() {
        return 0;
    }
    
    @Override
    public void reset(double x, double y){
        this.x = x;
        initialX = x;
        initialY = y;
        motion.reset();
        deltaVelocity = 0;
        currentVelocity = 0;
    }
    
    @Override
    public final void reset(){
        x = initialX;
        motion.reset();
        deltaVelocity = 0;
        currentVelocity = 0;
    }
    
    @Override
    public void update(double elapsedTime) {
        double newVelocity = motion.getVelocity(x, destinationX, elapsedTime);
        deltaVelocity = newVelocity - currentVelocity;
        currentVelocity =   newVelocity;
        x += currentVelocity * elapsedTime;
    }
}
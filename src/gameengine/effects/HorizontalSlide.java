/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gameengine.effects;

/**
 *
 * @author davidrusu
 */
public class HorizontalSlide implements Effect{
    private double destinationX, initialX, initialY, x;
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
    public void reset(double x, double y){
        this.x = x;
        initialX = x;
        initialY = y;
        motion.reset();
    }
    
    @Override
    public final void reset(){
        x = initialX;
        motion.reset();
    }
    
    @Override
    public void update(double elapsedTime) {
        x += motion.getVelocity(x, destinationX, elapsedTime) * elapsedTime;
    }
}
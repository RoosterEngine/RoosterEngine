/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gameengine.effects;

/**
 *
 * @author davidrusu
 */
public class VerticalSlide implements Effect{
    private double destinationY, initialX, initialY, y;
    private MotionGenerator motion;
    
    public VerticalSlide(double initialX, double initialY, double destinationY, MotionGenerator motion){
        this.destinationY = destinationY;
        this.initialX = initialX;
        this.initialY = initialY;
        this.motion = motion;
        reset();
    }

    @Override
    public double getX(){
        return initialX;
    }
    
    @Override
    public double getY(){
        return y;
    }
    
    @Override
    public final void reset(){
        y = initialY;
        motion.reset();
    }
    
    @Override
    public final void reset(double x, double y){
        this.y = y;
        initialX = x;
        initialY = y;
        motion.reset();
    }
    
    @Override
    public void update(double elapsedTime) {
        y += motion.getVelocity(y, destinationY, elapsedTime) * elapsedTime;
    }
}
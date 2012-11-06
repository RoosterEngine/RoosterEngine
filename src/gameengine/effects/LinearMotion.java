/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gameengine.effects;

/**
 *
 * @author davidrusu
 */
public class LinearMotion implements MotionGenerator{
    private double velocity, initialVelocity;
    
    /**
     * 
     * @param totalTime must be greater than 0
     */
    public LinearMotion(double velocity){
        this.velocity = velocity;
        initialVelocity = velocity;
    }
    
    @Override
    public double getVelocity(double currentPosition, double destinationPosition, double elapsedTime) {
        double delta = destinationPosition - currentPosition;
        if(delta >= 0 != velocity >= 0){
            velocity = -velocity;
        }
        double travelDist = velocity * elapsedTime;
        if(Math.abs(delta) < Math.abs(travelDist)){
            return delta / elapsedTime;
        }
        return velocity;
    }

    @Override
    public void reset() {
        velocity = initialVelocity;
    }
}
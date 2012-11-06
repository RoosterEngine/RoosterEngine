/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gameengine.effects;

/**
 *
 * @author davidrusu
 */
public class JumpMotion implements MotionGenerator{
    private double jumpDistance, timeBetweenJumps, timeTillNextJump;
    
    /**
     * 
     * @param numJumps the number of jumps to get to the destination
     * @param totalTime the time in milliseconds to get to the destination
     */
    public JumpMotion(double jumpDistance, double timeBetweenJumps){
        this.jumpDistance = jumpDistance;
        this.timeBetweenJumps = timeBetweenJumps;
        timeTillNextJump = timeBetweenJumps;
    }
    
    @Override
    public double getVelocity(double currentPosition, double destinationPosition, double elapsedTime) {
        timeTillNextJump -= elapsedTime;
        if(timeTillNextJump > 0){
            return 0;
        }
        timeTillNextJump += timeBetweenJumps;
        double delta = destinationPosition - currentPosition;
        if(delta < jumpDistance){
            if(delta >= 0 || -delta < jumpDistance){
                return delta / elapsedTime;
            }
            return -jumpDistance / elapsedTime;
        }
        return jumpDistance / elapsedTime;
    }

    @Override
    public void reset() {
        timeTillNextJump = timeBetweenJumps;
    }
}
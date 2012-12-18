/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gameengine.effects;

/**
 *
 * @author davidrusu
 */
public class JumpMotion implements Integrator {
    private double jumpDistance, timeBetweenJumps, timeTillNextJump;

    public JumpMotion(double jumpDistance, double timeBetweenJumps){
        this.jumpDistance = jumpDistance;
        this.timeBetweenJumps = timeBetweenJumps;
        timeTillNextJump = timeBetweenJumps;
    }
    
    @Override
    public double getVelocity(double currentPosition, double destinationPosition, double velocity, double mass, double elapsedTime) {
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
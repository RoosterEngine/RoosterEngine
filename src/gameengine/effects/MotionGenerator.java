/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gameengine.effects;

/**
 *
 * @author davidrusu
 */
public interface MotionGenerator {
    
    public double getVelocity(double currentPosition, double destinationPosition, double elapsedTime);
    
    public void reset();
}

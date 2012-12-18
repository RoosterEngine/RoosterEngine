/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gameengine.effects;

/**
 *
 * @author davidrusu
 */
public interface Integrator {
    
    public double getVelocity(double currentPosition, double destinationPosition, double velocity, double mass, double elapsedTime);
    
    public void reset();
}

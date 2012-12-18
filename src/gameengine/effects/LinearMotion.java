/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gameengine.effects;

import javax.swing.plaf.synth.SynthTextAreaUI;

/**
 *
 * @author davidrusu
 */
public class LinearMotion implements Integrator {
    private double constantVelocity, initialVelocity;

    public LinearMotion(double constantVelocity){
        this.constantVelocity = constantVelocity;
        initialVelocity = constantVelocity;
    }
    
    @Override
    public double getVelocity(double currentPosition, double destinationPosition, double velocity, double mass, double elapsedTime) {
        double delta = destinationPosition - currentPosition;

        if(delta >= 0 != constantVelocity >= 0){
            constantVelocity = -constantVelocity;
        }
        double travelDist = constantVelocity * elapsedTime;
        if(Math.abs(delta) < Math.abs(travelDist)){
            double vel = delta / elapsedTime;
            return vel;
        }
        return constantVelocity;
    }

    @Override
    public void reset() {
        constantVelocity = initialVelocity;
    }
}
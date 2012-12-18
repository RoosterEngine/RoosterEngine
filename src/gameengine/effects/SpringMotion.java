package gameengine.effects;

import bricklets.Physics;
/**
 *
 * @author davidrusu
 */
public class SpringMotion implements Integrator {
    private double k, dampeningFactor;
    
    public SpringMotion(double k, double dampeningRatio){
        this.k = k;
        this.dampeningFactor = dampeningRatio * Physics.getCriticallyDampedSpringConstant(k);
    }

    @Override
    public double getVelocity(double currentPosition, double destinationPosition, double velocity, double mass, double elapsedTime) {
        return ((destinationPosition - currentPosition) * k - dampeningFactor * velocity) * elapsedTime;
    }

    @Override
    public void reset() {
    }
}
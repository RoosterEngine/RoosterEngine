package gameengine.motion.integrators;

import gameengine.entities.Entity;

/**
 * @author davidrusu
 */
public class LinearIntegrator implements Integrator {
    private double constantVelocity;

    /**
     * Constructs a {@link LinearIntegrator} instance
     *
     * @param constantVelocity the velocity to be returned when getVelocity is called, must be positive
     * @throws IllegalArgumentException if constantVelocity is negative
     */
    public LinearIntegrator(double constantVelocity) {
        if (constantVelocity < 0) {
            throw new IllegalArgumentException("constantVelocity must be positive, constantVelocity: " + constantVelocity);
        }
        this.constantVelocity = constantVelocity;
    }

    @Override
    public double getVelocity(Entity entity, double displacementFromDestination, double elapsedTime) {
        double displacementVector = Math.signum(displacementFromDestination);
        double potentialTravelDist = constantVelocity * elapsedTime;
        if (potentialTravelDist > displacementFromDestination * displacementVector) {
            // the dist from destination is less than the distance we would normally travel at the constant velocity
            // we were given, If we were to return that velocity, we would pass the destination.
            // We need to return a velocity that will not pass the destination
            return displacementFromDestination / elapsedTime;
        }
        return constantVelocity * displacementVector;
    }

    @Override
    public void reset() {
    }
}
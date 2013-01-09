package gameengine.motion.integrators;

import gameengine.entities.Entity;

/**
 * @author davidrusu
 */
public class JumpIntegrator implements Integrator {
    private double jumpDistance, timeBetweenJumps, timeTillNextJump;

    /**
     * Constructs a {@link JumpIntegrator} instance
     *
     * @param jumpDistance     the distance to jump
     * @param timeBetweenJumps the time between jumps. If set to zero, this Integrator will behave like {@link LinearIntegrator}
     * @throws IllegalArgumentException if the specified timeBetweenJumps is negative
     */
    public JumpIntegrator(double jumpDistance, double timeBetweenJumps) {
        if (timeBetweenJumps < 0) {
            throw new IllegalArgumentException("timeBetweenJumps must be positive, timeBetweenJumps: " + timeBetweenJumps);
        }

        this.jumpDistance = jumpDistance;
        this.timeBetweenJumps = timeBetweenJumps;
        timeTillNextJump = timeBetweenJumps;
    }

    @Override
    public double getVelocity(Entity entity, double displacementFromDestination, double elapsedTime) {
        timeTillNextJump -= elapsedTime;
        if (timeTillNextJump > 0) {
            return 0;
        }

        double displacementVector = displacementFromDestination / Math.abs(displacementFromDestination);
        timeTillNextJump += timeBetweenJumps;
        if (displacementFromDestination * displacementVector < jumpDistance) {
            return displacementFromDestination / elapsedTime;
        }
        return jumpDistance * displacementVector / elapsedTime;
    }

    @Override
    public void reset() {
        timeTillNextJump = timeBetweenJumps;
    }

    /**
     * Sets the distance to jump
     *
     * @param jumpDistance the distance to jump in pixels
     */
    public void setJumpDistance(double jumpDistance) {
        this.jumpDistance = jumpDistance;
    }

    /**
     * sets the time between jumps
     *
     * @param timeBetweenJumps the time between jumps in milliseconds
     */
    public void setTimeBetweenJumps(double timeBetweenJumps) {
        this.timeBetweenJumps = timeBetweenJumps;
    }
}
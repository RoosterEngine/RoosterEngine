package gameengine.motion.integrators;

import gameengine.entities.Entity;
import gameengine.motion.motions.Motion;
import gameengine.physics.Physics;

/**
 * A {@link Motion} using this {@link Integrator} will be pulled towards its
 * destination as if by a spring
 * User: davidrusu
 * Date: 25/12/12
 * Time: 11:00 PM
 */
public class SpringIntegrator implements Integrator {
    private double k, d, velocity;

    /**
     * Creates a {@link SpringIntegrator}
     *
     * @param k          the strength of the spring
     * @param d          the dampening of the spring, 1 is critically damped, 0 is no damping
     * @param entityMass the mass of the {@link Entity} that will be affected by this {@link Integrator}
     */
    public SpringIntegrator(double k, double d, double entityMass) {
        this.k = k;
        this.d = d * Physics.getCriticallyDampedSpringConstant(k, entityMass);
    }

    @Override
    public double getVelocity(Entity entity, double displacementFromDestination, double elapsedTime) {
        double entityVelocity = Math.sqrt(entity.getDX() * entity.getDX() + entity.getDY() * entity.getDY());
        double springForce = k * displacementFromDestination - d * velocity;
        velocity += springForce / entity.getMass() * elapsedTime;
        return velocity;
    }

    @Override
    public void reset() {
    }

    /**
     * Sets the strength of the spring
     *
     * @param k the strength of the spring
     */
    public void setK(double k) {
        this.k = k;
    }

    /**
     * Sets the dampening factor of the spring
     *
     * @param d the dampening factor of the spring
     */
    public void setD(double d) {
        this.d = d;
    }
}

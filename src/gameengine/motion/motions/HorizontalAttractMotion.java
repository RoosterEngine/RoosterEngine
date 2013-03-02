package gameengine.motion.motions;

import gameengine.entities.Entity;
import gameengine.physics.Physics;

/**
 * This {@link Motion} will pull entities towards the specified x coordinate
 * <p/>
 * User: davidrusu
 * Date: 29/12/12
 * Time: 11:33 AM
 */
public class HorizontalAttractMotion implements Motion {
    private double destination;
    private double velocity;
    private double k, d;

    /**
     * Constructs a {@link HorizontalAttractMotion}
     *
     * @param destination the destination to attract towards
     * @param k           the strength of the attraction force
     * @param d           the damping factor to be applied to the velocity, 0 is no dampening, 1 is critically damped
     * @param entityMass  the mass of the {@link Entity} that will be affected by this {@link Motion}
     */
    public HorizontalAttractMotion(double destination, double k, double d, double entityMass) {
        this.destination = destination;
        this.k = k;
        this.d = d * Physics.getCriticallyDampedSpringConstant(k, entityMass);
    }

    @Override
    public double getVelocityX() {
        return velocity;
    }

    @Override
    public double getVelocityY() {
        return 0;
    }

    public void setDestination(double x) {
        destination = x;
        velocity = 0;
    }

    @Override
    public void reset() {
    }

    @Override
    public void update(Entity entity, double elapsedTime) {
        double delta = destination - entity.getX();
        double attractAcceleration = k * delta / entity.getMass();
        double damping = d * entity.getDX();
        velocity = entity.getDX() + (attractAcceleration - damping) * elapsedTime;
    }
}

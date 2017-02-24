package gameengine.motion.motions;

import gameengine.entities.Entity;
import gameengine.physics.Physics;

/**
 * This {@link Motion} will pull entities towards the specified y coordinate.
 *
 * @author davidrusu
 */
public class VerticalAttractMotion implements Motion {
    private double destination;
    private double velocity;
    private double k, d;

    /**
     * Constructs a {@link VerticalAttractMotion}
     *
     * @param destination     the destination to attract towards
     * @param SpringConstant  the strength of the attraction force
     * @param dampeningFactor the dampening factor to be applied to velocity, 0 is no dampening, 1
     *                        is critically damped
     * @param entityMass      the mass of the entity this motion will be applied to, the mass is
     *                        assumed to never change
     */
    public VerticalAttractMotion(double destination, double SpringConstant, double
            dampeningFactor, double entityMass) {
        this.destination = destination;
        this.k = SpringConstant;
        this.d = dampeningFactor * Physics.getCriticallyDampedSpringConstant(SpringConstant,
                entityMass);
    }

    @Override
    public double getVelocityX() {
        return 0;
    }

    @Override
    public double getVelocityY() {
        return velocity;
    }

    public void setDestination(double y) {
        destination = y;
        velocity = 0;
    }

    @Override
    public void reset() {
    }


    @Override
    public void update(Entity entity, double elapsedTime) {
        double delta = destination - entity.getY();
        double attract = (k * delta) / entity.getMass();
        double damping = entity.getDY() * d;
        velocity = entity.getDY() + (attract - damping) * elapsedTime;
    }
}

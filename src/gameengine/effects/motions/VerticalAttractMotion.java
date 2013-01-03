package gameengine.effects.motions;

import bricklets.Entity;
import bricklets.Physics;

/**
 * Created with IntelliJ IDEA.
 * User: davidrusu
 * Date: 29/12/12
 * Time: 10:49 AM
 * To change this template use File | Settings | File Templates.
 */
public class VerticalAttractMotion implements Motion {
    private double destination;
    private double velocity;
    private double k, d;

    /**
     * Constructs a {@link VerticalAttractMotion}
     * @param destination the destination to attract towards
     * @param k the strength of the attraction force
     * @param d the dampening factor to be applied to velocity, 0 is no dampening, 1 is critically damped
     * @param entityMass the mass of the entity this motion will be applied to, the mass is assumed to never change
     */
    public VerticalAttractMotion(double destination, double k, double d, double entityMass) {
        this.destination = destination;
        this.k = k;
        this.d = d * Physics.getCriticallyDampedSpringConstant(k, entityMass);
    }

    @Override
    public double getVelocityX() {
        return 0;
    }

    @Override
    public double getVelocityY() {
        return velocity;
    }

    public void setDestination(Entity entity, double x, double y) {
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

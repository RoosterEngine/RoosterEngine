package gameengine.motion.motions;

import gameengine.entities.Entity;
import gameengine.physics.Physics;
import gameengine.motion.integrators.Integrator;

/**
 * A {@link Motion} that follows a straight line towards it's destination
 * User: davidrusu
 * Date: 26/12/12
 * Time: 5:56 PM
 */
public class AttractMotion implements Motion {
    private double destinationX, destinationY;
    private double velocityX, velocityY;
    private double k, d;

    /**
     * Constructs an {@link AttractMotion}
     *
     * @param destinationX the x position of the destination
     * @param destinationY the y position of the destination
     * @param k            the strength of the attraction
     * @param d            the damping that will be applied to the entity, 0 is no damping, 1 is critically damped
     * @param entityMass   the mass of the {@link Entity} that will be affected by this {@link Integrator}
     */
    public AttractMotion(double destinationX, double destinationY, double k, double d, double entityMass) {
        this.destinationX = destinationX;
        this.destinationY = destinationY;
        this.k = k;
        this.d = d * Physics.getCriticallyDampedSpringConstant(k, entityMass);
    }

    @Override
    public double getVelocityX() {
        return velocityX;
    }

    @Override
    public double getVelocityY() {
        return velocityY;
    }

    public void setDestination(double x, double y) {
        destinationX = x;
        destinationY = y;
        velocityX = 0;
        velocityY = 0;
    }

    @Override
    public void reset() {
    }

    @Override
    public void update(Entity entity, double elapsedTime) {
        double deltaX = destinationX - entity.getX();
        double deltaY = destinationY - entity.getY();
        if (deltaX == 0 && deltaY == 0) {
            velocityX = 0;
            velocityY = 0;
            return;
        }
        double dist = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        deltaX /= dist;
        deltaY /= dist;
        double attractAcceleration = (k * dist) / entity.getMass();
        double accelX = deltaX * attractAcceleration;
        double accelY = deltaY * attractAcceleration;
        double dampX = entity.getDX() * d;
        double dampY = entity.getDY() * d;
        velocityX = entity.getDX() + (accelX - dampX) * elapsedTime;
        velocityY = entity.getDY() + (accelY - dampY) * elapsedTime;
    }
}

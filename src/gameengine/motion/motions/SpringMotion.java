package gameengine.motion.motions;

import gameengine.entities.Entity;
import gameengine.geometry.Vector2D;
import gameengine.physics.Physics;

import java.awt.*;

/**
 * A {@link gameengine.motion.motions.Motion} that follows a straight line towards it's destination.
 *
 * @author davidrusu
 */
public class SpringMotion implements Motion {
    private double destinationX, destinationY, targetLength;
    private double velocityX = 0, velocityY = 0;
    private double k, d;
    private double mass;

    /**
     * Constructs an {@link gameengine.motion.motions.SpringMotion}
     *
     * @param destinationX the x position of the destination
     * @param destinationY the y position of the destination
     * @param k            the strength of the attraction
     * @param d            the damping that will be applied to the entity, 0 is no damping, 1 is
     *                     critically damped
     * @param entityMass   the mass of the {@link gameengine.entities.Entity} that will be affected
     *                     by this {@link gameengine.motion.integrators.Integrator}
     */
    public SpringMotion(double destinationX, double destinationY, double targetLength, double k,
                        double d, double entityMass) {
        this.destinationX = destinationX;
        this.destinationY = destinationY;
        this.targetLength = targetLength;
        this.k = k * entityMass;
        this.mass = entityMass;
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
            deltaX = 0.001;
        }
        double dist = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        if (dist <= targetLength) {
            velocityX = entity.getDX();
            velocityY = entity.getDY();
            return;
        }
        deltaX /= dist;
        deltaY /= dist;
        double entityVel = Vector2D.unitScalarProject(entity.getDX(), entity.getDY(), deltaX,
                deltaY);
        dist = targetLength - dist;
        double attractAcceleration = (-k * dist) / mass - (d * entityVel);
        double accelX = deltaX * attractAcceleration;
        double accelY = deltaY * attractAcceleration;

//        double dampX = entity.getDX() * d;
//        double dampY = entity.getDY() * d;
        velocityX = entity.getDX() + accelX * elapsedTime;
        velocityY = entity.getDY() + accelY * elapsedTime;
    }

    public void draw(Graphics2D g, Color color, Entity entity) {
        g.setColor(color);
        g.drawLine((int) destinationX, (int) destinationY, (int) entity.getX(), (int) entity.getY
                ());
    }
}

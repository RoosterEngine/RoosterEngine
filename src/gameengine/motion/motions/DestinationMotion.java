package gameengine.motion.motions;

import gameengine.entities.Entity;
import gameengine.geometry.Vector2D;

/**
 * A motion that accelerates toward the destination.
 */
public class DestinationMotion implements Motion {
    private double maxSpeed;
    private double maxSpeedSquared;
    private double acceleration;
    private Vector2D velocity;
    private Vector2D destination;
    private Vector2D temp = new Vector2D();

    public DestinationMotion(Vector2D initialVelocity, double maxSpeed, double acceleration,
                             Vector2D destination) {
        velocity = initialVelocity;
        this.maxSpeed = maxSpeed;
        maxSpeedSquared = maxSpeed * maxSpeed;
        this.acceleration = acceleration;
        this.destination = new Vector2D(destination);
    }

    public final void setDestination(Vector2D destination) {
        this.destination.set(destination);
    }

    @Override
    public double getVelocityX() {
        return velocity.getX();
    }

    @Override
    public double getVelocityY() {
        return velocity.getY();
    }

    @Override
    public void reset() {
    }

    @Override
    public void update(Entity entity, double elapsedTime) {
        //points from current position to destination
        temp.set(destination.getX() - entity.getX(), destination.getY() - entity.getY());
        //scaled to equal the accelaration * elapsedTime
        temp.scale(acceleration * elapsedTime / temp.length());
        temp.add(velocity);
        double newLengthSquared = temp.lengthSquared();
        double lengthSquared = velocity.lengthSquared();

        velocity.set(temp);
        if (lengthSquared < maxSpeedSquared) {
            if (newLengthSquared > maxSpeedSquared) {
                velocity.scale(maxSpeed / Math.sqrt(newLengthSquared));
            }
        } else if (newLengthSquared > lengthSquared) {
            //Ensure the acceleration vector doesn't make the speed higher
            velocity.scale(Math.sqrt(lengthSquared) / Math.sqrt(newLengthSquared));
        }
    }
}

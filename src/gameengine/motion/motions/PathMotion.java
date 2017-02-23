package gameengine.motion.motions;

import gameengine.entities.Entity;
import gameengine.math.ParametricEquation;
import gameengine.motion.integrators.Integrator;

/**
 * A {@link Motion} that follows a {@link ParametricEquation}.
 *
 * @author davidrusu
 */
public class PathMotion implements Motion {
    private ParametricEquation function;
    private Integrator integrator;
    private double position, destination;
    private double t; // parameter that is passed to the parametric function
    private double velocityX, velocityY;
    private double eps = 0.0001;
    private double rate;
    private double x, y;

    public PathMotion(ParametricEquation parametricEquation, Integrator integrator) {
        this.function = parametricEquation;
        this.integrator = integrator;
        destination = function.getArcLength(function.getStart(), function.getStop(), eps);
        reset();
    }

    @Override
    public double getVelocityX() {
        return velocityX;
    }

    @Override
    public double getVelocityY() {
        return velocityY;
    }

    @Override
    public void reset() {
        t = function.getStart();
        position = 0;
        velocityX = 0;
        velocityY = 0;
        x = function.getX(t);
        y = function.getY(t);
        rate = (function.getStop() - function.getStart()) / destination;
    }

    @Override
    public void update(Entity entity, double elapsedTime) {
        double velocity = integrator.getVelocity(entity, destination - position, elapsedTime);
        double sign = Math.signum(velocity);
        double distToTravel = velocity * elapsedTime;
        double tolerance = Math.max(Math.abs(distToTravel * eps), 1E-5);
        double finalT, actualDist, deltaT;
        if (Math.abs(distToTravel) > tolerance) {
            do {
                deltaT = distToTravel * rate;
                finalT = t + deltaT;
                actualDist = sign * function.getArcLength(t, finalT, tolerance);
                rate = deltaT / actualDist;
            } while (Math.abs(distToTravel - actualDist) > tolerance);
        } else {
            finalT = t + distToTravel * rate;
            actualDist = distToTravel;
        }

        double futureX = function.getX(finalT);
        double futureY = function.getY(finalT);
        double deltaX = futureX - x;
        double deltaY = futureY - y;
        x = futureX;
        y = futureY;
        velocityX = deltaX / elapsedTime;
        velocityY = deltaY / elapsedTime;
        position += actualDist;
        t = finalT;
    }
}
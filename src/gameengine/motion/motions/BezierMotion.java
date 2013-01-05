package gameengine.motion.motions;

import bricklets.Entity;
import gameengine.motion.integrators.Integrator;
import gameengine.math.BezierEquation;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Created with IntelliJ IDEA.
 * User: davidrusu
 * Date: 10/12/12
 * Time: 5:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class BezierMotion implements Motion {
    private double currentTime, totalLength, eps;
    private double velocityX, velocityY;
    private BezierEquation bezierFunction;
    private Integrator motion;

    public BezierMotion(double currentX, double currentY, double x1, double y1, double x2, double y2, Integrator motion){
        this.motion = motion;
        currentTime = 0;
        eps = 0.001;
        bezierFunction = new BezierEquation(currentX, currentY, x1, y1, x2, y2);
        totalLength = bezierFunction.getArcLength(0, 1, 0.001);
    }

    @Override
    public double getVelocityX() {
        return velocityX;
    }

    @Override
    public double getVelocityY() {
        return velocityY;
    }

    public void setDestination(Entity entity, double x, double y) {
        setDestination(entity, x, y, (1 - (bezierFunction.getArcLength(0, currentTime, eps) / totalLength)) * 300);
    }

    @Override
    public void reset() {
        currentTime = 0;
        velocityX = 0;
        velocityY = 0;
    }

    /**
     * Sets the new destination of the path
     * @param entity the {@link Entity} to use as a base to set the new destination of the motion
     * @param x the X position of the destination
     * @param y the Y position of the destination
     * @param lazyMultiplier how sharp of a turn towards the new destination, higher is smoother
     */
    public void setDestination(Entity entity, double x, double y, double lazyMultiplier){
        double x1 = entity.getX() + entity.getDX() * lazyMultiplier;
        double y1 = entity.getY() + entity.getDY() * lazyMultiplier;
        bezierFunction.setPoints(entity.getX(), entity.getY(), x1, y1, x, y);
        currentTime = 0;
        totalLength = bezierFunction.getArcLength(0, 1, 0.001);
        velocityX = 0;
        velocityY = 0;
    }

    @Override
    public void update(Entity entity, double elapsedTime) {
        double delta = totalLength - bezierFunction.getArcLength(0, currentTime, eps);
        double dist = Math.abs(delta);
        double velocity = motion.getVelocity(entity, dist, elapsedTime);
        double velocitySign = velocity / Math.abs(velocity);
        double arcLength = velocity * elapsedTime * velocitySign;
        double approxFinalT = currentTime + 16 * velocitySign;
        double approxArcLength = bezierFunction.getArcLength(currentTime, approxFinalT, eps);
        double lastT = currentTime;
        while(Math.abs(arcLength - approxArcLength) > eps){
            double halfTime = Math.abs((approxFinalT - lastT) / 2);
            lastT = approxFinalT;
            if(approxArcLength < arcLength){
                approxFinalT += halfTime;
            }else{
                approxFinalT -= halfTime;
            }
            approxArcLength = bezierFunction.getArcLength(currentTime, approxFinalT, eps);
        }
        double deltaX = bezierFunction.getX(approxFinalT) - bezierFunction.getX(currentTime);
        double deltaY = bezierFunction.getY(approxFinalT) - bezierFunction.getY(currentTime);
        // unsimplified expression:
        // (disp / travelDist * vel) * (travelDist / originalArcLength)
        // normalize displacement vector and scale by velocity, then scale the velocity vector
        // by the ratio between the final travel dist and the original arcLength
        // simplified:
        // disp * vel / originalArcLength
        double velOverArcLength = velocity / arcLength;
        velocityX = deltaX * velOverArcLength;
        velocityY = deltaY * velOverArcLength;
        currentTime = approxFinalT;
    }

    /**
     * Draws the path of the bezier function
     * @param g the {@link Graphics2D} object to draw to
     * @param color the color of the path
     */
    public void draw(Graphics2D g, Color color){
        bezierFunction.draw(g, color);
    }
}

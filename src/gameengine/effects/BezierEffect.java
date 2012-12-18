package gameengine.effects;

import bricklets.Entity;
import gameengine.math.ArcLength;
import gameengine.math.BezierFunction;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Created with IntelliJ IDEA.
 * User: davidrusu
 * Date: 10/12/12
 * Time: 5:42 PM
 * To change this template use File | Settings | File Templates.
 */

/**
 *
 * @author davidrusu
 */
public class BezierEffect implements MotionEffect {
    private double time, totalLength, eps;
    private double velocityX, velocityY, deltaVelocityX, deltaVelocityY;
    private Entity entity;
    private BezierFunction bezierFunction;
    private ArcLength arcLengthFunction;
    private Integrator motion;

    public BezierEffect(Entity entity, double x1, double y1, double x2, double y2, Integrator motion){
        this.entity = entity;
        this.motion = motion;
        time = 0;
        eps = 0.001;
        bezierFunction = new BezierFunction(entity.getX(), entity.getY(), x1, y1, x2, y2);
        arcLengthFunction = new ArcLength(bezierFunction);
        totalLength = arcLengthFunction.getArcLength(0, 1, 0.001);
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
    public double getDeltaVelocityX() {
        return deltaVelocityX;
    }

    @Override
    public double getDeltaVelocityY() {
        return deltaVelocityY;
    }

    @Override
    public void reset() {

    }

    @Override
    public void reset(double x, double y) {
        setNewDestination(x, y, (1 - (arcLengthFunction.getArcLength(0, time, eps) / totalLength)) * 300);
    }

    @Override
    public void update(double elapsedTime) {
        double entityVelocity = Math.sqrt(entity.getDX() * entity.getDX() + entity.getDY() * entity.getDY());
        double velocity = motion.getVelocity(arcLengthFunction.getArcLength(0, time, eps), totalLength, entityVelocity, entity.getMass(), elapsedTime);
        double arcLength = velocity * elapsedTime;
        double approxFinalT = time + arcLength;
        double approxArcLength = arcLengthFunction.getArcLength(time, approxFinalT, eps);
        double lastT = time;
        while(Math.abs(arcLength - approxArcLength) > eps){
            double halfTime = Math.abs((approxFinalT - lastT) / 2);
            lastT = approxFinalT;
            if(approxArcLength < arcLength){
                approxFinalT += halfTime;
            }else{
                approxFinalT -= halfTime;
            }
            approxArcLength = arcLengthFunction.getArcLength(time, approxFinalT, eps);
        }
        time = approxFinalT;
        double bezierDX = bezierFunction.dxValueAt(time);
        double bezierDY = bezierFunction.dyValueAt(time);
        double bezierVel = Math.sqrt(bezierDX * bezierDX + bezierDY * bezierDY);
        double newVelX = bezierDX / bezierVel * velocity;
        double newVelY = bezierDY / bezierVel * velocity;
        deltaVelocityX = newVelX - velocityX;
        deltaVelocityY = newVelY - velocityY;
        velocityX = newVelX;
        velocityY = newVelY;
    }

    public void setNewDestination(double x, double y, double lazyMultiplier){
        double x1 = entity.getX() + entity.getDX() * lazyMultiplier;
        double y1 = entity.getY() + entity.getDY() * lazyMultiplier;
        bezierFunction.setPoints(entity.getX(), entity.getY(), x1, y1, x, y);
        time = 0;
        totalLength = arcLengthFunction.getArcLength(0, 1, 0.001);
        deltaVelocityX = 0;
        deltaVelocityY = 0;
        velocityX = 0;
        velocityY = 0;
    }

    public void draw(Graphics2D g, Color color){
        bezierFunction.draw(g, color);
    }
}

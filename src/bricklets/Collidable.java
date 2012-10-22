package bricklets;

import gameengine.Context;
import java.awt.Color;

/**
 *
 * @author davidrusu
 */
public abstract class Collidable extends Entity{
    
    protected double width, height;
    protected boolean stationary = false;
    protected Vector2D debugVector = new Vector2D();
    protected boolean isFixated = true;
    
    public Collidable(Context context, Vector2D position, Vector2D velocity, Vector2D acceleration, double restitution, double width, double height, Color color){
        super(context, position, velocity, acceleration, restitution, color);
        this.width = width;
        this.height = height;
    }
    
    public double getX() {
        return position.getX();
    }

    public void setX(double x) {
        position.setX(x);
    }

    public double getY() {
        return position.getY();
    }

    public void setY(double y) {
        position.setY(y);
    }

    public Vector2D getPosition() {
        return position;
    }

    public void setPosition(Vector2D position) {
        this.position.set(position);
    }

    public void setPosition(double x, double y) {
        position.set(x, y);
    }

    public double getVelocityX() {
        return velocity.getX();
    }

    public void setVelocityX(double x) {
        velocity.setX(x);
    }

    public double getVelocityY() {
        return velocity.getY();
    }

    public void setVelocityY(double y) {
        velocity.setY(y);
    }

    public Vector2D getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector2D velocity) {
        this.velocity.set(velocity);
    }

    public void setVelocity(double x, double y) {
        velocity.set(x, y);
    }

    public void addForce(Vector2D force) {
        acceleration.add(force);
    }

    public void addForce(double x, double y) {
        acceleration.add(x, y);
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public double getMass() {
        return mass;
    }
    
    public void setStationary(boolean stationary){
        this.stationary = stationary;
    }
    
    public boolean isStationary(){
        return stationary;
    }
    
    public void setDebugVector(Vector2D vector){
        debugVector.set(vector);
//        debugVector.scale(100);
    }
    
    public boolean isFixated(){
        return isFixated;
    }
    
    public abstract Polygon getPolygon();
    
    public abstract boolean isCircular();
    
    public abstract boolean isPolygonal();
}
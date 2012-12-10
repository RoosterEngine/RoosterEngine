package bricklets;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 *
 * @author davidrusu
 */
public abstract class Entity {
    
    protected static double air = 1, g = 0.0000;
    protected double x, y, dx, dy, ddx, ddy;
    protected double restitution, mass, friction;
    protected Color color;
    protected Vector2D debugVector = new Vector2D();
    
    public Entity(double x, double y, Color color){
        this(x, y, 0, 0, 1, 1, 1, color);
    }
    
    public Entity(double x, double y, double dx, double dy, double restitution, double friction, double mass, Color color){
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
        this.restitution = restitution;
        this.friction = friction;
        this.mass = mass;
        this.color = color;
    }

    public void setDebugVector(Vector2D debugVector){
        this.debugVector.set(debugVector);
    }
    
    public double getX() {
        return x;
    }
    
    public double getY(){
        return y;
    }
    
    public double getDX(){
        return dx;
    }
    
    public double getDY(){
        return dy;
    }

    public double getDDX(){
        return ddx;
    }

    public double getDDY(){
        return ddy;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }

    public double getMass(){
        return mass;
    }

    public void setRestitution(double restitution){
        this.restitution = restitution;
    }

    public double getRestitution(){
        return restitution;
    }

    public void setFriction(double friction){
        this.friction = friction;
    }

    public double getFriction(){
        return friction;
    }

    public void setVelocity(double x, double y){
        dx = x;
        dy = y;
    }

    public void setVelocityX(double dx) {
        this.dx = dx;
    }

    public void setVelocityY(double dy) {
        this.dy = dy;
    }

    public void addVelocityX(double dx) {
        this.dx += dx;
    }

    public void addVelocityY(double dy) {
        this.dy += dy;
    }

    public void addForce(double x, double y){
        ddx += x / mass;
        ddy += y / mass;
    }

    public void setColor(Color color){
        this.color = color;
    }

    public Color getColor(){
        return color;
    }

    public abstract void update(double elapsedTime);

    public abstract void draw(Graphics2D g);

    public void setPosition(double x, double y){
        this.x = x;
        this.y = y;
    }

    public void setAcceleration(double x, double y){
        ddx = x;
        ddy = y;
    }
}
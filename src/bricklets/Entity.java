package bricklets;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 *
 * @author davidrusu
 */
public abstract class Entity {
    
    protected static double air = 1, g = 0;
    protected double x, y, dx, dy, ddx, ddy;
    protected double restitution, mass = 1;
    protected Color color;
    protected Vector2D debugVector = new Vector2D();
    
    public Entity(double x, double y, Color color){
        this(x, y, 0, 0, color);
    }
    
    public Entity(double x, double y, double dx, double dy, Color color){
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
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
    
    public double getMass(){
        return mass;
    }
    
    public void setVelocity(double x, double y){
        dx = x;
        dy = y;
    }
    
    public void addForce(double x, double y){
        ddx += x;
        ddy += y;
    }
    
    public void setColor(Color color){
        this.color = color;
    }
    
    public Color getColor(){
        return color;
    }
    
    public abstract void update(double elapsedTime);
    
    public abstract void draw(Graphics2D g);

    void setMass(double mass) {
        this.mass = mass;
    }
}
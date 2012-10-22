package bricklets;

import gameengine.Context;
import gameengine.GameController;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 *
 * @author davidrusu
 */
public abstract class Entity {
    
    protected static double air = 1, g = 0.0001;
    protected Context context;
    protected Vector2D position, velocity, acceleration;
    protected double restitution, mass = 1;
    protected Color color;
    
    public Entity(Context context, double x, double y, Color color){
        this(context, x, y, 1, color);
    }
    
    public Entity(Context context, double x, double y, double restitution, Color color){
        this(context, new Vector2D(x, y), restitution, color);
    }
    
    public Entity(Context context, Vector2D pos, double restitution, Color color){
        this(context, pos, new Vector2D(), restitution, color);
    }
    
    public Entity(Context context, double x, double y, double dx, double dy, double restitution, Color color){
        this(context, new Vector2D(x, y), new Vector2D(dx, dy), restitution, color);
    }
    
    public Entity(Context context, Vector2D pos, Vector2D vel, double restitution, Color color){
        this(context, pos, vel, new Vector2D(), restitution, color);
    }
    
    public Entity(Context context, Vector2D pos, Vector2D vel, Vector2D accel, double restitution, Color color){
        this.context = context;
        this.position = pos;
        this.velocity = vel;
        this.acceleration = accel;
        this.restitution = restitution;
        this.color = color;
    }
    
    public void setColor(Color color){
        this.color = color;
    }
    
    public Color getColor(){
        return color;
    }
    
    public abstract void update(double elapsedTime);
    
    public abstract void draw(Graphics2D g);
}
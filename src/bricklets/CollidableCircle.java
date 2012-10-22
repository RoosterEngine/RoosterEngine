/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bricklets;

import gameengine.Context;
import java.awt.Color;

/**
 *
 * @author david
 */
public abstract class CollidableCircle extends Collidable{
    
    protected double radius;
    
    public CollidableCircle(Context context, Vector2D position, Vector2D velocity, double radius, double restitution, Color color){
        this(context, position, velocity, new Vector2D(0, 0), radius, restitution, color);
    }
    
    public CollidableCircle(Context context, Vector2D position, Vector2D velocity, Vector2D acceleration, double radius, double restitution, Color color){
        super(context, position, velocity, acceleration, radius * 2, radius * 2, restitution, color);
        this.radius = radius;
    }

    public double getRadius(){
        return radius;
    }
    
    @Override
    public Polygon getPolygon() {
        return null;
    }

    @Override
    public boolean isCircular() {
        return true;
    }

    @Override
    public boolean isPolygonal() {
        return false;
    }
}

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
public abstract class CollidablePolygon extends Collidable{

    protected Polygon polygon;
    
    public CollidablePolygon(Context context, Vector2D position, Vector2D velocity, double restitution, Color color, Polygon polygon){
        this(context, position, velocity, new Vector2D(), restitution, color, polygon);
    }
    
    public CollidablePolygon(Context context, Vector2D position, Vector2D velocity, Vector2D acceleration, double restitution, Color color, Polygon polygon){
        super(context, position, velocity, acceleration, restitution, polygon.getWidth(), polygon.getHeight(), color);
        this.polygon = polygon;
    }

    @Override
    public Polygon getPolygon() {
        return polygon;
    }

    @Override
    public boolean isCircular() {
        return false;
    }

    @Override
    public boolean isPolygonal() {
        return true;
    }
}
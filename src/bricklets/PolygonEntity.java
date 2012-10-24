/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bricklets;

import gameengine.Context;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 *
 * @author david
 */
public class PolygonEntity extends Entity{
    private Polygon polygon;
    
    public PolygonEntity(Context context, double x, double y, double minRadius, double maxRadius, int minPoints, int maxPoints){
        super(context, x, y, Color.BLACK);
        polygon = Polygon.getRandomConvexPolygon(x, y, minRadius, maxRadius, minPoints, maxPoints, this);
//        polygon = Polygon.getRectanglePolygon(x, y, maxRadius, maxRadius, this);
    }
    
    public Polygon getPolygonShape(){
        return polygon;
    }
    
    @Override
    public void update(double elapsedTime) {
        x += dx * elapsedTime;
        y += dy * elapsedTime;
        dx += ddx * elapsedTime;
        dy += ddy * elapsedTime;
        ddy = g * elapsedTime;
        ddx = 0;
    }

    @Override
    public void draw(Graphics2D g) {
        polygon.draw(g, Color.BLACK);
    }
    
}

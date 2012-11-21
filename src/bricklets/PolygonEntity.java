/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bricklets;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 *
 * @author david
 */
public class PolygonEntity extends Entity{
    private Polygon polygon;
    
    public PolygonEntity(double x, double y, double minRadius, double maxRadius, int minPoints, int maxPoints){
        super(x, y, Color.BLACK);
        polygon = Polygon.getRandomConvexPolygon(x, y, minRadius, maxRadius, minPoints, maxPoints, this);
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
        polygon.draw(g, color);
        g.setColor(Color.ORANGE);
        double scale = 100000;
        g.drawLine((int)(x), (int)(y), (int)(x + debugVector.getX() * scale), (int)(y + debugVector.getY() * scale));
//        g.setColor(Color.RED);
//        g.drawLine((int)(x), (int)(y), (int)(x + dx * scale), (int)(y + dy * scale));
    }
    
}

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
public class Circle extends Shape{
    
    public Circle(double x, double y, double dx, double dy, double radius, Entity parentEntity){
        super(x, y, dx, dy, radius, parentEntity);
    }
    
    @Override
    public int getShapeType() {
        return TYPE_CIRCLE;
    }
    
    @Override
    public void draw(Graphics2D g, Color color){
        g.setColor(color);
        g.fillOval((int)(x - radius), (int)(y - radius), (int)(radius * 2), (int)(radius * 2));
    }
    
}

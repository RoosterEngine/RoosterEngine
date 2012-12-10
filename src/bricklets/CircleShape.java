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
public class CircleShape extends Shape{
    
    public CircleShape(double x, double y, double radius, Entity parentEntity, Material material){
        super(x, y, radius, parentEntity, material);
        System.out.println(dx + " " + dy);
    }
    
    @Override
    public int getShapeType() {
        return TYPE_CIRCLE;
    }
    
    @Override
    public void draw(Graphics2D g, Color color){
        g.setColor(color);
        g.drawOval((int)(x - radius), (int)(y - radius), (int)(radius * 2), (int)(radius * 2));
    }
    
}

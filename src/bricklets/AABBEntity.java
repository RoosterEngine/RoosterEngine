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
public class AABBEntity extends Entity{
    private double width, height, halfWidth, halfHeight;
    
    public AABBEntity(double x, double y, double width, double height){
        super(x, y, Color.BLACK);
        this.width = width;
        this.height = height;
        halfWidth = width / 2;
        halfHeight = height / 2;
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
        g.setColor(color);
        g.fillRect((int)(x - halfWidth), (int)(y - halfHeight), (int)width, (int)height);
        g.setColor(Color.ORANGE);
        double scale = 100;
        g.drawLine((int)(x + debugVector.getX() * scale), (int)(y + debugVector.getY() * scale), (int)x, (int)y);
    }
    
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bricklets;

import java.awt.Color;
import java.awt.Graphics2D;

public class AABBEntity extends Entity{
    protected Color color;

    public AABBEntity(double x, double y, double width, double height) {
        super(x, y, width, height);
        color = Color.BLACK;
    }

    @Override
    public void update(double elapsedTime) {
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(color);
        g.fillRect((int)(x - halfWidth), (int)(y - halfHeight), (int)width, (int)height);
    }
    
}

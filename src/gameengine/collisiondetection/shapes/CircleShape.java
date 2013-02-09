/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gameengine.collisiondetection.shapes;

import gameengine.entities.Entity;
import gameengine.physics.Material;

import java.awt.*;

public class CircleShape extends Shape {

    public CircleShape(Entity parent, double x, double y, double radius, Material material, double mass) {
        super(parent, x, y, radius, radius, radius, material, mass);
    }

    @Override
    public int getShapeType() {
        return TYPE_CIRCLE;
    }

    @Override
    public void draw(Graphics2D g, Color color) {
        g.setColor(color);
        g.drawOval((int) (x - radius), (int) (y - radius), (int) (radius * 2), (int) (radius * 2));
    }

}

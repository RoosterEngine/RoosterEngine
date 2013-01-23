/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gameengine.entities;

import gameengine.collisiondetection.shapes.AABBShape;
import gameengine.physics.Material;

import java.awt.*;

public class BoxEntity extends Entity {
    protected Color color;

    public BoxEntity(double x, double y, double width, double height) {
        super(x, y, width, height);
        setShape(new AABBShape(
                this, x, y, width, height, Material.getRubber(), 1));
        color = Color.WHITE;
    }

    @Override
    public void update(double elapsedTime) {
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(color);
        g.fillRect((int) (x - halfWidth), (int) (y - halfHeight),
                (int) width, (int) height);
    }

}

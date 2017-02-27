/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bricklets;

import gameengine.collisiondetection.shapes.Rectangle;
import gameengine.graphics.RColor;
import gameengine.graphics.Renderer;

public class BoxEntity extends TestingEntity {

    public BoxEntity(double x, double y, double width, double height) {
        super(x, y, new Rectangle(width, height));
        color = RColor.WHITE;
    }

    @Override
    public void update(double elapsedTime) {
    }

    @Override
    public void draw(Renderer renderer) {
        renderer.setForegroundColor(color);
        renderer.fillRect(x, y, getHalfWidth(), getHalfHeight());
    }
}

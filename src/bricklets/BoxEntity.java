/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bricklets;

import gameengine.collisiondetection.shapes.Rectangle;
import gameengine.graphics.RColor;

public class BoxEntity extends TestingEntity {

    public BoxEntity(double x, double y, double width, double height) {
        super(x, y, new Rectangle(width, height));
        setColor(RColor.WHITE);
    }
}

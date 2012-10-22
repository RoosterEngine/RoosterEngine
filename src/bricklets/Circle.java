/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bricklets;

/**
 *
 * @author david
 */
public class Circle extends Shape{

    public Circle(double x, double y, double dx, double dy, double radius){
        super(x, y, dx, dy, radius);
    }
    
    @Override
    public int getShapeType() {
        return TYPE_CIRCLE;
    }
    
}

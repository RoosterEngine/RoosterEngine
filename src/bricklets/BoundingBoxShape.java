package bricklets;

import gameengine.Context;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 *
 * @author david
 */
public class BoundingBoxShape extends Shape{
    private double width, height, halfWidth, halfHeight;
    
    private static double calcRadius(double halfWidth, double halfHeight){
        return Math.sqrt(halfWidth * halfWidth + halfHeight * halfHeight);
    }
    
    public BoundingBoxShape(double x, double y, double width, double height, Entity parentEntity){
        super(x, y, 0, 0, calcRadius(width / 2, height / 2), parentEntity);
        this.width = width;
        this.height = height;
        halfWidth = width / 2;
        halfHeight = height / 2;
    }
    
    public double getWidth(){
        return width;
    }
    
    public double getHeight(){
        return height;
    }
    
    public double getHalfWidth(){
        return halfWidth;
    }
    
    public double getHalfHeight(){
        return halfHeight;
    }
    
    @Override
    public int getShapeType(){
        return Shape.TYPE_AA_BOUNDING_BOX;
    }

    @Override
    public void draw(Graphics2D g, Color color) {
        g.setColor(color);
        g.fillRect((int)(x - halfWidth), (int)(y - halfHeight), (int) width, (int) height);
    }
}
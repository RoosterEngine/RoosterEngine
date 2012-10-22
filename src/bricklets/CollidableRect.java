package bricklets;

import gameengine.Context;
import java.awt.Color;

/**
 * @author davidrusu
 */
public abstract class CollidableRect extends CollidablePolygon{
    
    protected double radius;
    protected double halfWidth, halfHeight;
    
    public CollidableRect(CollidableRect rect){
        this(rect.context, rect.position, rect.velocity, rect.restitution, rect.width, rect.height, rect.color);
    }
    
    public CollidableRect(Context context, double x, double y, double width, double height){
        this(context, new Vector2D(x, y), width, height, Color.WHITE);
    }
    
    public CollidableRect(Context context, Vector2D pos, double width, double height){
        this(context, pos, width, height, Color.WHITE);
    }
    
    public CollidableRect(Context context, double x, double y, double width, double height, Color color){
        this(context, new Vector2D(x, y), width, height, color);
    }
    
    public CollidableRect(Context context, Vector2D pos, double width, double height, Color color){
        this(context, pos , new Vector2D(), 1, width, height, color);
    }
    
    public CollidableRect(Context context, Vector2D pos, Vector2D vel, double restitution, double width, double height, Color color){
        super(context, pos, vel, restitution, color, null);
        halfWidth = width / 2;
        halfHeight = height / 2;
        radius = Math.sqrt(halfWidth * halfWidth  + halfHeight * halfHeight) / 2;
    }
    
//    private static Polygon createPolygon(double x, double y, double width, double height){
//        double halfWidth = width / 2;
//        double halfHeight = height / 2;
//        double[] xPoints = new double[4];
//        double[] yPoints = new double[4];
//        xPoints[0] = x - halfWidth;
//        yPoints[0] = y - halfHeight;
//        xPoints[1] = x + halfWidth;
//        yPoints[1] = yPoints[0];
//        xPoints[2] = xPoints[1];
//        yPoints[2] = y + halfHeight;
//        xPoints[3] = xPoints[0];
//        yPoints[3] = yPoints[2];
//        return new Polygon(xPoints, yPoints);        
//    }
    
    public void moveX(double a){
        acceleration.add(a, 0);
    }
}

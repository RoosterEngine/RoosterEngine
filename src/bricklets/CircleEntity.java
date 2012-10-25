package bricklets;

import gameengine.Context;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 *
 * @author david
 */
public class CircleEntity extends Entity{
    private double radius;
    private int[] pastX = new int[100];
    private int[] pastY = new int[100];
    private int start = 0;
    
    public CircleEntity(Context context, double x, double y, double radius){
        super(context, x, y, Color.BLACK);
        this.radius = radius;
        for(int i = 0; i < pastX.length; i++){
            pastX[i] = (int)x;
            pastY[i] = (int)y;
        }
    }
    
    public double getRadius(){
        return radius;
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
//        int lastX = pastX[start];
//        int lastY = pastY[start];
//        for(int i = 0; i < pastX.length; i++){
//            int index = (i + start) % pastX.length;
//            int x = pastX[index];
//            int y = pastY[index];
//            g.setColor(Color.LIGHT_GRAY);
//            g.drawLine(lastX, lastY, x, y);
//            g.setColor(Color.DARK_GRAY);
//            g.drawOval((int)(x - radius), (int)(y - radius), (int)(radius * 2), (int)(radius * 2));
//            lastX = x;
//            lastY = y;
//        }
//        pastX[start] = (int)x;
//        pastY[start] = (int)y;
//        start = (start + 1) % pastX.length;
        g.setColor(color);
        g.fillOval((int)(x - radius), (int)(y - radius), (int)(radius * 2), (int)(radius * 2));
        double scale = 100;
        g.setColor(Color.ORANGE);
        g.drawLine((int)x, (int)y, (int)(x + debugVector.getX() * scale), (int)(y + debugVector.getY() * scale));
    }
}
package bricklets;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.Random;

/**
 *
 * @author davidrusu
 */
public class WavyBall {
    private double radius, x, y, dx = 0, dy = 0, air = 0.5, jitter = 1.5;
    private double maxRadius = 5, timeScale = 0.1;
    private Point[] pastPositions;
    private double[] pastRadiuses;
    private int pastIndex;
    private Random rand = new Random();
    
    public WavyBall(double x, double y) {
        pastPositions = new Point[10];
        for(int i = 0; i < pastPositions.length; i++){
            pastPositions[i] = new Point((int)x, (int)y);
        }
        pastRadiuses = new double[pastPositions.length];
        for(int i = 0; i < pastPositions.length; i++){
            pastRadiuses[i] = 0;
        }
        pastIndex = pastPositions.length - 1;
        this.x = x;
        this.y = y;
    }
    
    void clearVel() {
        dx = 0;
        dy = 0;
    }
    
    public void setPos(double x, double y){
        this.x = x;
        this.y = y;
    }
    
    public void update(double elapsedTime, double scale, double shiftX, double shiftY,int x, int y, int width, int height) {
        elapsedTime *= timeScale;
        dx += (Math.sin(this.y / scale)) * jitter;
        dy += (Math.sin(this.x / scale)) * jitter;
        dx *= air;
        dy *= air;
        this.x += dx * elapsedTime + shiftX;
        this.y += dy * elapsedTime + shiftY;
        
        checkBounds(x, y, width, height);
    }
    
    private void checkBounds(int x, int y, int width, int height){
        if(this.x + radius >= width + x){
            this.x = x + radius;
        }else if(this.x - radius <= x){
            this.x = width + x - radius;
        }
        if(this.y + radius >= height + y){
            this.y = y + radius;
        }else if( this.y - radius <= y){
            this.y = height + y - radius;
        }
    }
    
    public void draw(Graphics2D g) {
        for(int i = 0; i < pastPositions.length; i++){
            int index = (i + pastIndex) % pastPositions.length;
            double pastRadius = pastRadiuses[index];
            g.fillOval((int)(pastPositions[index].x - pastRadius), (int)(pastPositions[index].y - pastRadius), (int)(pastRadius * 2), (int)(pastRadius * 2));
        }
        
        radius = ((Math.sqrt(dx * dx + dy * dy)) * maxRadius + pastRadiuses[pastIndex]) / 2;
        g.fillOval((int)(x - radius), (int)(y - radius), (int)(radius * 2), (int)(radius * 2));
        
        pastIndex--;
        if(pastIndex == -1){
            pastIndex = pastPositions.length - 1;
        }
        pastPositions[pastIndex].setLocation((int)x, (int)y);
        pastRadiuses[pastIndex] = radius;
    }
}
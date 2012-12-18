package bricklets;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Random;

public class Polygon extends Shape{
    private static Random rand = new Random(0);
    private Vector2D[] normals, points; // points are relative to the center
    private double[] normalMins, normalMaxs;
    private int[] xInts, yInts;
    private double width, height, minX, maxX, minY, maxY;
    private int numPoints;
    private boolean usingBoundingBox = true;

    public Polygon(double x, double y, double[] xPoints, double[] yPoints, Entity parentEntity, Material material){
        super(x, y, getRadius(xPoints, yPoints), parentEntity, material);
        numPoints = xPoints.length;
        points = new Vector2D[numPoints];
        normals = new Vector2D[numPoints];
        normalMins = new double[numPoints];
        normalMaxs = new double[numPoints];
        xInts = new int[numPoints];
        yInts = new int[numPoints];
        setupPoints(xPoints, yPoints);
        setup();
    }

    public void setup(){
        setupMaxMin();
        setupNormalsAndShadows();
        setupBounding();
    }
    
    public static void setRandomSeed(long seed){
        if(rand == null){
            rand = new Random(seed);
        }else{
            rand.setSeed(seed);
        }
    }
    
    public static Polygon getRandomConvexPolygon(double x, double y, double radiusMin, double radiusMax, int numPointsMin, int numPointsMax, Entity parentEntity, Material material){
        double radius = rand.nextDouble() * (radiusMax - radiusMin) + radiusMin;
        int numPoints = (int)(rand.nextDouble() * (numPointsMax - numPointsMin)) + numPointsMin;
        double[] xPoints = new double[numPoints];
        double[] yPoints = new double[numPoints];
        double angle = 2 * Math.PI / numPoints;
        double initAngle = 0; //rand.nextDouble() * 2 * Math.PI;
        for(int p = 0; p < numPoints; p++){
            double radomAngle = 0; //rand.nextDouble() * angle / 2 - angle;
            double angleFluctuated = angle * p + radomAngle + initAngle;
            double pX = Math.cos(angleFluctuated) * radius;
            double pY = Math.sin(angleFluctuated) * radius;
            xPoints[p] = pX;
            yPoints[p] = pY;
        }
        return new Polygon(x, y, xPoints, yPoints, parentEntity, material);
    }
    
    private static double getRadius(double[] xPoints, double[] yPoints){
        double maxDistSquared = 0;
        for(int p = 0; p < xPoints.length; p++){
            double x = xPoints[p];
            double y = yPoints[p];
            double distSquared = x * x + y * y;
            maxDistSquared = Math.max(distSquared, maxDistSquared);
        }
        return Math.sqrt(maxDistSquared);
    }
    
    @Override
    public int getShapeType(){
        return TYPE_POLYGON;
    }
    
    /**
     * the first normal in the array is for the line points[points.length - 1] and points[0],
     * the second normal is for line points[0] and points[1].
     * @return an array of {@link Vector2D} point outwards
     */
    public Vector2D[] getNormals(){
        return normals;
    }
    
    public double[] getNormalMins(){
        return normalMins;
    }
    
    public double[] getNormalMaxs(){
        return normalMaxs;
    }
    
    public double getWidth(){
        return width;
    }
    
    public double getHeight(){
        return height;
    }
    
    public double getMinX(){
        return minX;
    }
    
    public double getMaxX(){
        return maxX;
    }
    
    public double getMinY(){
        return minY;
    }
    
    public double getMaxY(){
        return maxY;
    }
    
    public boolean isUsingBoundingBox(){
        return usingBoundingBox;
    }
    
    /**
     * Points are relative to the center
     * @return {@link Vector2D} array with points that are relative to the center
     */
    public Vector2D[] getPoints(){
        return points;
    }
    
    public int getNumPoints(){
        return numPoints;
    }
    
    public double getArea(){
        double sum = 0;
        double lastX = points[numPoints - 1].getX(), lastY = points[numPoints - 1].getY();
        for(int i = 0; i < numPoints; i++){
            double x = points[i].getX();
            double y = points[i].getY();
            sum += lastX * y - lastY * x;
            lastX = x;
            lastY = y;
        }
        return Math.abs(sum * 0.5);
    }
    
    @Override
    public void draw(Graphics2D g, Color color){
        for(int i = 0; i < numPoints; i++){
            xInts[i] = (int)(points[i].getX() + x);
            yInts[i] = (int)(points[i].getY() + y);
        }
        g.setColor(color);
        g.fillPolygon(xInts, yInts, numPoints);
        drawPoints(g, Color.RED);
//        drawBounding(g);
//        drawNormals(g, Color.RED);
    }

    private void drawPoints(Graphics2D g, Color color){
        int width = 2;
        g.setColor(color);
        for (int i = 0; i < numPoints; i++) {
            g.fillOval(xInts[i], yInts[i], width, width);
        }
    }

    private void drawBounding(Graphics2D g){
        g.setColor(Color.ORANGE);
        if(usingBoundingBox){
            g.drawRect((int)(x + minX), (int)(y + minY), (int)(width), (int)(height));
        }else{
            g.drawOval((int)(x - radius), (int)(y - radius), (int)(radius * 2), (int)(radius * 2));
        }
    }

    private void drawNormals(Graphics2D g, Color color){
        double scale = 100;
        g.setColor(color);
        for(Vector2D normal: normals){
            g.drawLine((int)(x), (int)(y), (int)(x + normal.getX() * scale), (int)(y + normal.getY() * scale));
        }
    }
    
    private void setupPoints(double[] xPoints, double[] yPoints){
        for(int i = 0; i < numPoints; i++){
            points[i] = new Vector2D(xPoints[i], yPoints[i]);
        }
    }
    
    private void setupBounding(){
        double boxArea = (width) * (height);
        double circleArea = Math.PI * radius * radius;
        usingBoundingBox = circleArea > boxArea;
    }
    
    private void setupMaxMin(){
        minX = Double.MAX_VALUE;
        maxX = -Double.MAX_VALUE;
        minY = Double.MAX_VALUE;
        maxY = -Double.MAX_VALUE;
        for(Vector2D point: points){
            double x = point.getX();
            double y = point.getY();
            if(x < minX){
                minX = x;
            }
            if(x > maxX){
                maxX = x;
            }
            if(y < minY){
                minY = y;
            }
            if(y > maxY){
                maxY = y;
            }
        }
        width = maxX - minX;
        height = maxY - minY;
    }
    
    private void setupNormalsAndShadows(){
        Vector2D point = points[numPoints - 1];
        double lastX = point.getX();
        double lastY = point.getY();
        for(int i = 0; i < numPoints; i++){
            point = points[i];
            double x = point.getX();
            double y = point.getY();
            Vector2D normal = new Vector2D(y - lastY, lastX - x);
            normals[i] = normal.unit();
            lastX = x;
            lastY = y;
            
            //finding the min and max when each point is projected onto the normal
            //used for collision detection
            double min = Double.MAX_VALUE;
            double max = -Double.MAX_VALUE;
            for(int q = 0; q < numPoints; q++){
                double dist = points[q].unitScalarProject(normal);
                if(dist < min){
                    min = dist;
                }
                if(dist > max){
                    max = dist;
                }
            }
            normalMins[i] = min;
            normalMaxs[i] = max;
        }
    }
}
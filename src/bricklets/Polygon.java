package bricklets;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Random;

/**
 * @author davidrusu
 */
public class Polygon extends Shape{
    private static Random rand = new Random(0);
    private Color color = Color.orange;
    private Vector2D[] normals, points; // points are relative to the center
    private double[] normalMins, normalMaxs;
    private int[] xInts, yInts;
    private double width, height, minX, maxX, minY, maxY;
    private int numPoints;
    private boolean usingBoundingBox = true;
    
    /**
     * Constructs a polygon shape. the points must be relative to the center
     * @param x
     * @param y
     * @param dx
     * @param dy
     * @param xPoints
     * @param yPoints 
     */
    public Polygon(double x, double y, double dx, double dy, double[] xPoints, double[] yPoints, Entity parentEntity){
        super(x, y, dx, dy, getRadius(xPoints, yPoints), parentEntity);
        numPoints = xPoints.length;
        points = new Vector2D[numPoints];
        normals = new Vector2D[numPoints];
        normalMins = new double[numPoints];
        normalMaxs = new double[numPoints];
        xInts = new int[numPoints];
        yInts = new int[numPoints];
        setupPoints(xPoints, yPoints);
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
    
    public static Polygon getRectanglePolygon(double x, double y, double width, double height, Entity parentEntity){
        double[] xPoints = {0, width, width, 0};
        double[] yPoints = {0, 0, height, height};
        return new Polygon(x, y, 0, 0, xPoints, yPoints, parentEntity);
    }
    
    public static Polygon getRandomConvexPolygon(double x, double y, double radiusMin, double radiusMax, int numPointsMin, int numPointsMax, Entity parentEntity){
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
        return new Polygon(x, y, 0, 0, xPoints, yPoints, parentEntity);
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
    
    public void setColor(Color color){
        this.color = color;
    }

    public boolean isIntersectingBounding(Polygon polygon){
        if(usingBoundingBox){
            if(polygon.usingBoundingBox){
                if((maxX < polygon.minX || minX > polygon.maxX) && (maxY < polygon.minY || minY > polygon.maxY)){
                    return false;
                }
            }else{ // polygon is using bounding circle
                if((polygon.x + polygon.radius < minX || polygon.x - polygon.radius > maxX) && (polygon.y + polygon.radius < minY || polygon.y - polygon.radius > maxY)){
                    return false;
                }
            }
        }else{
            if(polygon.usingBoundingBox){
                // not accurate circle is treated as a square
                if((x + radius < polygon.minX || x - radius > polygon.maxX) && (y + radius < polygon.minY || y - radius > polygon.maxY)){
                    return false;
                }
            }else{ // both polygons are using bounding circle
                double dx = polygon.x - x;
                double dy = polygon.y - y;
                double combinedRadius = radius + polygon.radius;
                if(dx * dx + dy * dy >= combinedRadius * combinedRadius){
                    return false;
                }
            }
        }
        return true;
    }
    
    public boolean isIntersecting(Polygon polygon){
        if(!isIntersectingBounding(polygon)){
            return false;
        }
        Polygon poly1 = this, poly2 = polygon;
        if(numPoints < polygon.numPoints){
            poly1 = polygon;
            poly2 = this;
        }
        int numPoints = poly2.numPoints;
        for(int p = 0; p < numPoints; p++){
            Vector2D normal = poly2.normals[p];
            double min = Double.MAX_VALUE;
            double max = -Double.MAX_VALUE;
            for(int q = 0; q < poly1.numPoints; q++){
                double dist = poly1.points[q].unitScalarProject(normal);
                if(dist < min){
                    min = dist;
                }
                if(dist > max){
                    max = dist;
                }
            }
            
            double poly1Dist = Vector2D.unitScalarProject(poly1.x, poly1.y, normal);
            double poly2Dist = Vector2D.unitScalarProject(poly2.x, poly2.y, normal);
            if(poly1Dist < poly2Dist){
                double distBetween = poly2Dist - poly1Dist;
                if(max - poly2.normalMins[p] < distBetween){
                    return false;
                }
            }else{
                double distBetween = poly1Dist - poly2Dist;
                if(poly2.normalMaxs[p] - min < distBetween){
                    return false;
                }
            }
        }
        return true;
    }
    
    @Override
    public void draw(Graphics2D g, Color color){
        for(int i = 0; i < numPoints; i++){
            xInts[i] = (int)(points[i].getX() + x);
            yInts[i] = (int)(points[i].getY() + y);
        }
        g.setColor(color);
        g.fillPolygon(xInts, yInts, numPoints);
        g.setColor(Color.GRAY);
        double scale = 300;
        for(int i = 0; i < numPoints; i++){
            g.drawLine((int)(normals[i].getX() * scale + x), (int)(normals[i].getY() * scale + y), (int)x, (int)y);
        }
        drawBounding(g);
    }
    
    private void drawBounding(Graphics2D g){
        g.setColor(Color.ORANGE);
        if(usingBoundingBox){
            g.drawRect((int)(x + minX), (int)(y + minY), (int)(width), (int)(height));
        }else{
            g.drawOval((int)(x - radius), (int)(y - radius), (int)(radius * 2), (int)(radius * 2));
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
            //used when checking if there is an intersection
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
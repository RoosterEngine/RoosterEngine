package bricklets;

import java.awt.Color;
import java.awt.Point;

/**
 *
 * @author davidrusu
 */
public class Collider {
    public static final double NO_COLLISION = Math.sqrt(Double.MAX_VALUE);
    
    private Collidable collidable;
    private Vector2D center, centerOffset = new Vector2D();
    private Vector2D[] pointOffsets;
    private double radius;
    private Vector2D combinedVelocity = new Vector2D();
    private Vector2D collisionNormal = new Vector2D();
    private Vector2D overlapCollisionNormal = new Vector2D();
    private Vector2D proj = new Vector2D();
    private double timeToNotOverlap, timeToCollision, shortestTime, shortestTimeWhenOverlap;
    private boolean allShadowsOverlapping, travellingAwayFromEachOther;
    public Collider(Collidable collidable){
        this.collidable = collidable;
        setupCenter();
        setupPointOffsets();
    }
    
    public void update(){
        Vector2D position = collidable.getPosition();
        center.set(position.getX(), position.getY());
    }
    
    public void getTimeToCollision(Collider b, double maxTime, Collision result){
        if(collidable.isCircular() && b.collidable.isCircular()){
            circleCircleCollision(b, maxTime, result);
        }else if(collidable.isCircular() && b.collidable.isPolygonal()){
            circlePolyCollision(this, b, maxTime, result);
        }else if(collidable.isPolygonal() && b.collidable.isCircular()){
            circlePolyCollision(b, this, maxTime, result);
        }else if(collidable.isPolygonal() && b.collidable.isPolygonal()){
//            double time = doPolyPolyCollision((CollidablePolygon) b.collidable, maxTime);
            ((BoundingBox)collidable).getTimeToCollision((BoundingBox)b.collidable, maxTime, result);
        }
    }
//    
    private void circleCircleCollision(Collider b, double maxTime, Collision result){
        // b is static and a is moving
        combinedVelocity.set(collidable.getVelocity()).subtract(b.collidable.getVelocity());
        Vector2D centerA = center;
        Vector2D centerB = b.center;
        double distToLineSquared = centerB.distToLineSquared(centerA.getX(),
                                                             centerA.getY(),
                                                             centerA.getX() + combinedVelocity.getX(),
                                                             centerA.getY() + combinedVelocity.getY());
        double radiiSum = radius + b.radius;
        double timeToCollision = NO_COLLISION;
        if(distToLineSquared < radiiSum * radiiSum){ // possible collision
            proj.set(centerB).subtract(centerA);
            double projLength = proj.length();
            double projVel = combinedVelocity.scalarProject(proj, projLength);
            if(projVel > 0){
                projLength -= radiiSum;
                timeToCollision =  projLength / projVel;
            }
        }
//        result.set(timeToCollision, new Vector2D(centerB).subtract(centerA), collidable, b.collidable);
    }
    
    private void circlePolyCollision(Collider circle, Collider poly, double maxTime, Collision result){
//        result.set(NO_COLLISION, null, circle.collidable, poly.collidable);
    }
    
    public Collidable getCollidable(){
        return collidable;
    }
    
    public double getRadius(){
        return radius;
    }
    
    public Vector2D getCombinedVelocity(){
        return combinedVelocity;
    }
    
    public Vector2D getCollisionNormal(){
        return collisionNormal;
    }
    
    private void setupCenter(){
        if(collidable.isPolygonal()){
            Polygon polygon = collidable.getPolygon();
            double maxX = -Double.MAX_VALUE, maxY = -Double.MAX_VALUE, minX = Double.MAX_VALUE, minY = Double.MAX_VALUE;
            for(int i = 0; i < polygon.getNumPoints(); i++){
                double x = polygon.getPoints()[i].getX();
                double y = polygon.getPoints()[i].getY();
                if(x > maxX){
                    maxX = x;
                }
                if(x < minX){
                    minX = x;
                }
                if(y > maxY){
                    maxY = y;
                }
                if(y < minY){
                    minY = y;
                }
            }
//            center = polygon.getCenter();
        }else{
            center = collidable.getPosition();
        }
    }
    
    private void setupPointOffsets(){
        if(collidable.isPolygonal()){
            Polygon polygon = collidable.getPolygon();
            double maxDistSquared = -Double.MAX_VALUE;
            for(int i = 0; i < polygon.getNumPoints(); i++){
                double dx = polygon.getPoints()[i].getX() - center.getX();
                double dy = polygon.getPoints()[i].getY() - center.getY();
                double distSquared = dx * dx + dy * dy;
                if(distSquared > maxDistSquared){
                    maxDistSquared = distSquared;
                }
            }
            radius = Math.sqrt(maxDistSquared);
            pointOffsets = new Vector2D[collidable.getPolygon().getNumPoints()];
            for(int i = 0; i < pointOffsets.length; i++){
                pointOffsets[i] = new Vector2D();
            }
        }else{
            CollidableCircle circle = (CollidableCircle)(collidable);
            radius = circle.getRadius();
        }
    }
}
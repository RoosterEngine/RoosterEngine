package bricklets;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 *
 * @author david
 */
public abstract class Shape {
    public static final int TYPE_CIRCLE = 0, TYPE_AA_BOUNDING_BOX = 1, TYPE_O_BOUNDING_BOX = 2, TYPE_POLYGON = 3;
    public static final double NO_COLLISION = Double.MAX_VALUE;
    protected double x, y, dx, dy, radius, parentOffsetX, parentOffsetY;
    protected Entity parentEntity;
    
    public Shape(double x, double y, double dx, double dy, double radius, Entity parentEntity){
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
        this.radius = radius;
        this.parentEntity = parentEntity;
        parentOffsetX = parentEntity.getX() - x;
        parentOffsetY = parentEntity.getY() - y;
    }
    
    public double getX(){
        return x;
    }
    
    public double getY(){
        return y;
    }
    
    public double getDX(){
        return dx;
    }
    
    public double getDY(){
        return dy;
    }
    
    public double getRadius(){
        return radius;
    }
    
    public Entity getParentEntity() {
        return parentEntity;
    }
    
    public void update(){
        x = parentEntity.getX() + parentOffsetX;
        y = parentEntity.getY() + parentOffsetY;
        dx = parentEntity.getDX();
        dy = parentEntity.getDY();
    }
    
    public static void collideCircleCircle(Shape a, Shape b, double maxTime, Collision result){
        double combinedVelX = b.dx - a.dx;
        double combinedVelY = b.dy - a.dy;
        if(combinedVelX == 0 && combinedVelY == 0){
            result.set(Collision.NO_COLLISION);
            return;
        }
        double distToLineSquared = Vector2D.distToLineSquared(a.x, a.y, b.x, b.y, b.x + combinedVelX, b.y + combinedVelY);
        double radiiSum = a.radius + b.radius;
        double radiiSumSquared = radiiSum * radiiSum;
        if(distToLineSquared > radiiSumSquared){
            result.set(Collision.NO_COLLISION);
            return;
        }

        Vector2D velocity = result.getCollisionNormal().set(combinedVelX, combinedVelY);
        double deltaX = a.x - b.x;
        double deltaY = a.y - b.y;
        double distBetween = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        double projVelocity = velocity.scalarProject(deltaX, deltaY, distBetween);
        if(projVelocity <= 0){
            result.set(Collision.NO_COLLISION);
            return;
        }
        double travelDist = distBetween - radiiSum;
        double travelTime = travelDist / projVelocity;
        if(travelTime > maxTime){
            result.set(Collision.NO_COLLISION);
            return;
        }
        velocity.unit();
        double centerAProjectedOnVelocity = Vector2D.unitScalarProject(a.x - b.x, a.y - b.y, velocity);
        double subLength = Math.sqrt(radiiSumSquared - distToLineSquared);
        velocity.scale(centerAProjectedOnVelocity - subLength);
        velocity.add(b.x - a.x, b.y - a.y).unit();
        result.set(travelTime, velocity, a, b);
    }
    
    private static boolean willBoundingCollide(Polygon a, Polygon b, double maxTime){
        if(!a.isUsingBoundingBox() && !b.isUsingBoundingBox()){
            return willBoundingCircleCircleCollide(a, b, maxTime);
        }else if(!a.isUsingBoundingBox() && b.isUsingBoundingBox()){
            return willBoundingCircleBoxCollide(a, b, maxTime);
        }else if(a.isUsingBoundingBox() && !b.isUsingBoundingBox()){
            return willBoundingCircleBoxCollide(b, a, maxTime);
        }else if(a.isUsingBoundingBox() && a.isUsingBoundingBox()){
            return willBoundingBoxBoxCollide(a, b, maxTime);
        }
        return false;
    }
    
    private static boolean willBoundingCircleCircleCollide(Polygon a, Polygon b, double maxTime){
        double combinedVelX = b.dx - a.dx;
        double combinedVelY = b.dy - a.dy;
        double distToLineSquared = Vector2D.distToLineSquared(a.x, a.y, b.x, b.y, b.x + combinedVelX, b.y + combinedVelY);
        double radiiSum = a.radius + b.radius;
        if(distToLineSquared > radiiSum * radiiSum){
            return false;
        }

        double deltaX = b.x - a.x;
        double deltaY = b.y - a.y;
        double distBetween = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        double projVel = Vector2D.scalarProject(combinedVelX, combinedVelY, deltaX, deltaY, distBetween);
        if(projVel < 0){
            return false;
        }

        distBetween -= radiiSum;
        double travelTime = distBetween / projVel;
        return travelTime <= maxTime;
    }
        
    /**
     * @param a the polygon with a bounding circle
     * @param b the polygon with a bounding box
     * @param maxTime
     * @return 
     */
    private static boolean willBoundingCircleBoxCollide(Polygon a, Polygon b, double maxTime){
        double relativeVelX = a.dx - b.dx, relativeVelY = a.dy - b.dy;
        double maxTimeToCollision;
        
        double timeToCollide = getTimeToCollisionPlanePlane(b.x + b.getMinX(), a.x + a.radius, relativeVelX);
        if(timeToCollide < 0){
            return false;
        }
        maxTimeToCollision = timeToCollide;
        
        timeToCollide = getTimeToCollisionPlanePlane(b.y + b.getMinY(), a.y + a.radius, relativeVelY);
        if(timeToCollide < 0){
            return false;
        }
        maxTimeToCollision = Math.max(timeToCollide, maxTimeToCollision);
        
        timeToCollide = getTimeToCollisionPlanePlane(b.x + b.getMaxX(), a.x - a.radius, relativeVelX);
        if(timeToCollide < 0){
            return false;
        }
        maxTimeToCollision = Math.max(timeToCollide, maxTimeToCollision);
        
        timeToCollide = getTimeToCollisionPlanePlane(b.y + b.getMaxY(), a.y - a.radius, relativeVelY);
        if(timeToCollide < 0){
            return false;
        }
        maxTimeToCollision = Math.max(timeToCollide, maxTimeToCollision);
        
        //checking collision with points
        relativeVelX *= -1;
        relativeVelY *= -1;
        double radiusSquared = a.radius * a.radius;
        maxTimeToCollision = Math.max(getTimeToCollisionPointAndCircle(a.x, a.y, radiusSquared, b.getMinX(), b.getMinY(), relativeVelX, relativeVelY), maxTimeToCollision);
        maxTimeToCollision = Math.max(getTimeToCollisionPointAndCircle(a.x, a.y, radiusSquared, b.getMaxX(), b.getMinY(), relativeVelX, relativeVelY), maxTimeToCollision);
        maxTimeToCollision = Math.max(getTimeToCollisionPointAndCircle(a.x, a.y, radiusSquared, b.getMaxX(), b.getMaxY(), relativeVelX, relativeVelY), maxTimeToCollision);
        maxTimeToCollision = Math.max(getTimeToCollisionPointAndCircle(a.x, a.y, radiusSquared, b.getMinX(), b.getMaxY(), relativeVelX, relativeVelY), maxTimeToCollision);
        return maxTimeToCollision <= maxTime;
    }
    
    private static boolean willBoundingBoxBoxCollide(Polygon a, Polygon b, double maxTime){
        double relativeVelX = a.dx - b.dx, relativeVelY = a.dy - b.dy;
        double maxTimeToCollision = -Double.MAX_VALUE;
        
        if(relativeVelX > 0){
            double actualBX = b.x + b.getMinX(), actualAX = a.x + a.getMaxX();
            double timeToCollision= getTimeToCollisionPlanePlane(actualBX, actualAX, relativeVelX);
            if(timeToCollision < 0){
                return false;
            }
            maxTimeToCollision = timeToCollision;
        }else if(relativeVelX < 0){
            double actualBX = b.x + b.getMaxX(), actualAX = a.x + a.getMinX();
            double timeToCollision= getTimeToCollisionPlanePlane(actualBX, actualAX, relativeVelX);
            if(timeToCollision < 0){
                return false;
            }
            maxTimeToCollision = timeToCollision;
        }else{
            double minA = a.x + a.getMinX(), maxA = a.x + a.getMaxX();
            double minB = b.x + b.getMinX(), maxB = b.x + b.getMaxX();
            if(minA > maxB || maxA < minB){
                return false;
            }
            maxTimeToCollision = 0;
        }
        
        if(relativeVelY > 0){
            double actualBY = b.y + b.getMinY(), actualAY = a.y + a.getMaxY();
            double timeToCollision = getTimeToCollisionPlanePlane(actualBY, actualAY, relativeVelY);
            if(timeToCollision < 0){
                return false;
            }
            maxTimeToCollision = Math.max(timeToCollision, maxTimeToCollision);
        }else if(relativeVelY < 0){
            double actualBY = b.y + b.getMaxY(), actualAY = a.y + a.getMinY();
            double timeToCollision = getTimeToCollisionPlanePlane(actualBY, actualAY, relativeVelY);
            if(timeToCollision < 0){
                return false;
            }
            maxTimeToCollision = Math.max(timeToCollision, maxTimeToCollision);
        }else{
            double minA = a.y + a.getMinY(), maxA = a.y + a.getMaxY();
            double minB = b.y + b.getMinY(), maxB = b.y + b.getMaxY();
            if(minA > maxB || maxA < minB){
                return false;
            }
        }
        
        return maxTimeToCollision < maxTime;
    }
    
    private static  double getTimeToCollisionPointAndCircle(double circleX, double circleY, double radiusSquared, double pointX, double pointY, double pointVelX, double pointVelY){
        double distToLineSquared = Vector2D.distToLineSquared(circleX, circleY, pointX, pointY, pointX + pointVelX, pointY + pointVelY);
        if(distToLineSquared <= radiusSquared){
            double subLength = Math.sqrt(radiusSquared - distToLineSquared);
            double velLength = Math.sqrt(pointVelX * pointVelX + pointVelY * pointVelY);
            double projLength = Vector2D.scalarProject(circleX - pointX, circleY - pointY, pointVelX, pointVelY, velLength);
            if(projLength > 0){
                return (projLength - subLength) / velLength;
            }
        }
        return -Double.MAX_VALUE;
    }
    
    private static double getTimeToCollisionPlanePlane(double actualBPos, double actualAPos, double relativeVel){
        double dist = actualBPos - actualAPos;
        if(relativeVel == 0){
            if(dist < 0){
                return 0;
            }
            return -Double.MAX_VALUE;
        }
        return dist / relativeVel;
    }
    
    public static void colliderPolyPoly(Polygon a, Polygon b, double maxTime, Collision result){
        
    }
    
    public abstract int getShapeType();
    
    public abstract void draw(Graphics2D g, Color color);

}

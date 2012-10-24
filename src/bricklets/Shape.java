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
    
    public static void collidePolyPoly(Polygon a, Polygon b, double maxTime, Collision result){
        if(!willBoundingCollide(a, b, maxTime)){
            result.set(Collision.NO_COLLISION);
            return;
        }
        Vector2D collisionNormal = result.getCollisionNormal().clear();
        double combinedVelocityX = b.dx - a.dx, combinedVelocityY = b.dy - a.dy;
        double maxEntryTime = -Double.MAX_VALUE, minLeaveTime = Double.MAX_VALUE;
        
        for(int i = 0; i < b.getNormals().length; i++){
            Vector2D normal = b.getNormals()[i];
            double aMin = Double.MAX_VALUE;
            double aMax = -Double.MAX_VALUE;
            for(Vector2D point: a.getPoints()){
                double dist = point.unitScalarProject(normal);
                if(dist < aMin){
                    aMin = dist;
                }
                if(dist > aMax){
                    aMax = dist;
                }
            }
            double centerA = Vector2D.unitScalarProject(a.x, a.y, normal);
            double centerB = Vector2D.unitScalarProject(b.x, b.y, normal);
            aMin += centerA;
            aMax += centerA;
            double bMin = b.getNormalMins()[i] + centerB;
            double bMax = b.getNormalMaxs()[i] + centerB;
            double projVel = Vector2D.unitScalarProject(combinedVelocityX, combinedVelocityY, normal);
            if(aMax <= bMin){
                if(projVel < 0){
                    double timeToOverlap = (aMax - bMin) / projVel;
                    if(timeToOverlap > maxEntryTime){
                        maxEntryTime = timeToOverlap;
                        collisionNormal.set(normal);
                    }
                }else{
                    // not travelling away from each other
                    //TODO should have an early return here
                    maxEntryTime = NO_COLLISION;
                }
            }else if(bMax <= aMin){
                if(projVel > 0){
                    double timeToOverlap = (aMin - bMax) / projVel;
                    if(timeToOverlap > maxEntryTime){
                        maxEntryTime = timeToOverlap;
                        collisionNormal.set(normal);
                    }
                }else{
                    // not travelling away from each other
                    //TODO should have an early return here
                    maxEntryTime = NO_COLLISION;
                }
            }
            
            if(bMax > aMin && projVel < 0){
                double timeToLeave = (aMin - bMax) / projVel;
                if(timeToLeave < minLeaveTime){
                    minLeaveTime = timeToLeave;
                }
            }else if(aMax > bMin && projVel > 0){
                double timeToLeave = (aMax - bMin) / projVel;
                if(timeToLeave < minLeaveTime){
                    minLeaveTime = timeToLeave;
                }
            }
        }
        
        Vector2D[] bPoints = b.getPoints();
        for(int i = 0; i < a.getNormals().length; i++){
            Vector2D normal = a.getNormals()[i];
            double bMin = Double.MAX_VALUE;
            double bMax = -Double.MAX_VALUE;
            for(Vector2D point: bPoints){
                double dist = point.unitScalarProject(normal);
                if(dist < bMin){
                    bMin = dist;
                }
                if(dist > bMax){
                    bMax = dist;
                }
            }
            double centerA = Vector2D.unitScalarProject(a.x, a.y, normal);
            double centerB = Vector2D.unitScalarProject(b.x, b.y, normal);
            bMin += centerB;
            bMax += centerB;
            double aMin = a.getNormalMins()[i] + centerA;
            double aMax = a.getNormalMaxs()[i] + centerA;
            double projVel = -Vector2D.unitScalarProject(combinedVelocityX, combinedVelocityY, normal);
            if(bMax <= aMin){
                if(projVel < 0){
                    double timeToOverlap = (bMax - aMin) / projVel;
                    if(timeToOverlap > maxEntryTime){
                        maxEntryTime = timeToOverlap;
                        collisionNormal.set(normal);
                    }
                }else{
                    // not travelling towards each other
                    //TODO should have an early return here
                    maxEntryTime = NO_COLLISION;
                }
            }else if(aMax <= bMin){
                if(projVel > 0){
                    double timeToOverlap = (bMin - aMax) / projVel;
                    if(timeToOverlap > maxEntryTime){
                        maxEntryTime = timeToOverlap;
                        collisionNormal.set(normal);
                    }
                }else{
                    // not travelling towards each other
                    //TODO should have an early return here
                    maxEntryTime = NO_COLLISION;
                }
            }
            
            if(aMax > bMin && projVel < 0){
                double timeToLeave = (bMin - aMax) / projVel;
                if(timeToLeave < minLeaveTime){
                    minLeaveTime = timeToLeave;
                }
            }else if(bMax > aMin && projVel > 0){
                double timeToLeave = (bMax - aMin) / projVel;
                if(timeToLeave < minLeaveTime){
                    minLeaveTime = timeToLeave;
                }
            }
        }
        if(maxEntryTime == -Double.MAX_VALUE || maxEntryTime > minLeaveTime){
            maxEntryTime = NO_COLLISION;
        }
        result.set(maxEntryTime, collisionNormal, b, a);
    }
    
    public static void collideCirclePoly(Shape a, Polygon b, double maxTime, Collision result){
        double combinedVelocityX = b.dx - a.dx, combinedVelocityY = b.dy - a.dy;
        double maxEntryTime = -Double.MAX_VALUE, minLeaveTime = Double.MAX_VALUE;
        Vector2D collisionNormal = new Vector2D(result.getCollisionNormal());
        int normalIndex = -1;
        double centerAProjOnNormal = 0, centerBProjOnNormal = 0;
        for(int i = 0; i < b.getNumPoints(); i++){
            Vector2D normal = b.getNormals()[i];
            double centerA = Vector2D.unitScalarProject(a.x, a.y, normal);
            double centerB = Vector2D.unitScalarProject(b.x, b.y, normal);
            double aMin = centerA - a.radius;
            double aMax = centerA + a.radius;
            double bMin = b.getNormalMins()[i] + centerB;
            double bMax = b.getNormalMaxs()[i] + centerB;
            double projVel = Vector2D.unitScalarProject(combinedVelocityX, combinedVelocityY, normal);
            if(aMax <= bMin){
                if(projVel < 0){
                    double timeToOverlap = (aMax - bMin) / projVel;
                    if(timeToOverlap > maxEntryTime){
                        maxEntryTime = timeToOverlap;
                        normalIndex = i;
                        centerAProjOnNormal = centerA;
                        centerBProjOnNormal = centerB;
                    }
                }else{
                    // not travelling away from each other
                    result.set(Collision.NO_COLLISION);
                    return;
                }
            }else if(bMax <= aMin){
                if(projVel > 0){
                    double timeToOverlap = (aMin - bMax) / projVel;
                    if(timeToOverlap > maxEntryTime){
                        maxEntryTime = timeToOverlap;
                        normalIndex = i;
                        centerAProjOnNormal = centerA;
                        centerBProjOnNormal = centerB;
                    }
                }else{
                    // not travelling away from each other
                    result.set(Collision.NO_COLLISION);
                    return;
                }
            }
            
            if(bMax > aMin && projVel < 0){
                double timeToLeave = (aMin - bMax) / projVel;
                if(timeToLeave < minLeaveTime){
                    minLeaveTime = timeToLeave;
                }
            }else if(aMax > bMin && projVel > 0){
                double timeToLeave = (aMax - bMin) / projVel;
                if(timeToLeave < minLeaveTime){
                    minLeaveTime = timeToLeave;
                }
            }
        }
        if(normalIndex < 0){
            result.set(Collision.NO_COLLISION);
            return;
        }
        collisionNormal.set(b.getNormals()[normalIndex]);
        result.set(maxEntryTime, collisionNormal, a, b);
        double velLength = Math.sqrt(combinedVelocityX * combinedVelocityX + combinedVelocityY * combinedVelocityY);
        for(Vector2D point: b.getPoints()){
            double x = point.getX();
            double y = point.getY();
            boolean willCollide = checkCirclePointCollide(a.x, a.y, a.radius, x, y, combinedVelocityX, combinedVelocityY, velLength, maxEntryTime,  result, a, b);
            maxEntryTime = result.getTimeToCollision();
        }
        if(maxEntryTime == -Double.MAX_VALUE || maxEntryTime > maxTime){
            result.set(Collision.NO_COLLISION);
            return;
        }
    }
    
    private static boolean checkCirclePointCollide(double cx, double cy, double radius, double px, double py, double pVelX, double pVelY, double velLength, double maxEntryTime, Collision result, Shape a, Shape b){
        double distToVelRaySquared = Vector2D.distToLineSquared(cx, cy, px, py, px + pVelX, py + pVelY);
        double radiusSquared = radius * radius;
        if(distToVelRaySquared <= radiusSquared){
            // in the path of the velocity ray
            double projLength = Vector2D.scalarProject(cx - px, cy - py, pVelX, pVelY, velLength);
            if(projLength >= 0){
                // travelling towards each other
                System.out.println("test");
                double subLength = Math.sqrt(radiusSquared - distToVelRaySquared);
                double travelDist = projLength - subLength;
                double travelTime = travelDist / velLength;
                if(travelTime < maxEntryTime){
                    System.out.println("pass " + travelTime);
                    double distOverVel = travelDist / velLength;
                    result.getCollisionNormal().set(cx - px - pVelX * distOverVel, cy - py - pVelY * distOverVel).unit();
                    result.set(travelTime, result.getCollisionNormal(), a, b);
                    return true;
                }
            }
        }
        return false;
        
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
        if(combinedVelX == 0 && combinedVelY == 0){
            // the speed relative to each other is 0;
            return false;
        }
        double distToLineSquared = Vector2D.distToLineSquared(a.x, a.y, b.x, b.y, b.x + combinedVelX, b.y + combinedVelY);
        double radiiSum = a.radius + b.radius;
        if(distToLineSquared > radiiSum * radiiSum){
            // not in the path of the combined velocity
            return false;
        }
        double deltaX = a.x - b.x;
        double deltaY = a.y - b.y;
        double distBetween = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        double projVel = Vector2D.scalarProject(combinedVelX, combinedVelY, deltaX, deltaY, distBetween);
        if(projVel < 0){
            // travelling away from each other
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
        double maxOverlapTime = getBoxOverlapTime(a.x, a.y, a.radius, a.radius, b.x, b.y, b.getMaxX(), b.getMaxY(), relativeVelX, relativeVelY);
        if(maxOverlapTime == NO_COLLISION){
            return false;
        }
        
        //checking collision with points
        relativeVelX *= -1;
        relativeVelY *= -1;
        double radiusSquared = a.radius * a.radius;
        maxOverlapTime = Math.max(getTOICirclePoint(a.x, a.y, radiusSquared, b.getMinX(), b.getMinY(), relativeVelX, relativeVelY), maxOverlapTime);
        maxOverlapTime = Math.max(getTOICirclePoint(a.x, a.y, radiusSquared, b.getMaxX(), b.getMinY(), relativeVelX, relativeVelY), maxOverlapTime);
        maxOverlapTime = Math.max(getTOICirclePoint(a.x, a.y, radiusSquared, b.getMaxX(), b.getMaxY(), relativeVelX, relativeVelY), maxOverlapTime);
        maxOverlapTime = Math.max(getTOICirclePoint(a.x, a.y, radiusSquared, b.getMinX(), b.getMaxY(), relativeVelX, relativeVelY), maxOverlapTime);
        return maxOverlapTime <= maxTime;
    }
    
    private static boolean willBoundingBoxBoxCollide(Polygon a, Polygon b, double maxTime){
        double relativeVelX = a.dx - b.dx, relativeVelY = a.dy - b.dy;
        double maxOverlapTime = getBoxOverlapTime(a.x, a.y, a.getMaxX(), a.getMaxY(), b.x, b.y, b.getMaxX(), b.getMaxY(), relativeVelX, relativeVelY);
        return maxOverlapTime < maxTime;
    }
    
    private static double getBoxOverlapTime(double aX, double aY, double aHalfX, double aHalfY, double bX, double bY, double bHalfX, double bHalfY, double relativeVelX, double relativeVelY){
        double overlapTime;
        if(relativeVelX > 0){
            overlapTime = getOverlapTime(bX - bHalfX, aX + aHalfX, relativeVelX);
        }else if(relativeVelX < 0){
            overlapTime = getOverlapTime(bX + bHalfX, aX - aHalfX, relativeVelX);
        }else{
            if(aX - aHalfX > bX + bHalfX || aX + aHalfX < bX - bHalfX){
                return NO_COLLISION;
            }
            overlapTime = 0;
        }
        if(relativeVelY > 0){
            overlapTime = Math.max(getOverlapTime(bY - bHalfY, aY + aHalfY, relativeVelY), overlapTime);
        }else if(relativeVelY < 0){
            overlapTime = Math.max(getOverlapTime(bY + bHalfY, aY - aHalfY, relativeVelY), overlapTime);
        }else{
            if(aY - aHalfY > bY + bHalfY || aY + aHalfY < bY - bHalfY){
                return NO_COLLISION;
            }
        }
        return overlapTime;
    }
    
    private static  double getTOICirclePoint(double circleX, double circleY, double radiusSquared, double pointX, double pointY, double pointVelX, double pointVelY){
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
    
    private static double getOverlapTime(double actualBPos, double actualAPos, double relativeVel){
        double dist = actualBPos - actualAPos;
//        if(relativeVel == 0){
//            if(dist < 0){
//                return 0;
//            }
//            return -Double.MAX_VALUE;
//        }
        return dist / relativeVel;
    }
    
    public abstract int getShapeType();
    
    public abstract void draw(Graphics2D g, Color color);
}
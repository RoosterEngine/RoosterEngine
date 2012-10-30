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
        double relativeVelX = b.dx - a.dx, relativeVelY = b.dy - a.dy;
        double[] maxMinTimes = {-Double.MAX_VALUE, Double.MAX_VALUE};
        getEntryAndLeaveTime(b.getNormals(), a.getPoints(), b.getNormalMins(), b.getNormalMaxs(), b.x, b.y, a.x, a.y, relativeVelX, relativeVelY, maxMinTimes, collisionNormal);
        if(maxMinTimes[0] == NO_COLLISION){
            result.set(Collision.NO_COLLISION);
            return;
        }
        getEntryAndLeaveTime(a.getNormals(), b.getPoints(), a.getNormalMins(), a.getNormalMaxs(), a.x, a.y, b.x, b.y, -relativeVelX, -relativeVelY, maxMinTimes, collisionNormal);
        if(maxMinTimes[0] == NO_COLLISION){
            result.set(Collision.NO_COLLISION);
            return;
        }
        if(maxMinTimes[0] == -Double.MAX_VALUE || maxMinTimes[0] > maxTime || maxMinTimes[0] > maxMinTimes[1]){
            result.set(Collision.NO_COLLISION);
            return;
        }
        result.set(maxMinTimes[0], collisionNormal, b, a);
    }
    
    public static void collideCirclePoly(Shape a, Polygon b, double maxTime, Collision result){
        // TODO rethink the leave time here, currently it is not being used
        double relativeVelX = b.dx - a.dx, relativeVelY = b.dy - a.dy;
        double maxEntryTime = -Double.MAX_VALUE, minLeaveTime = Double.MAX_VALUE;
        Vector2D collisionNormal = new Vector2D(result.getCollisionNormal());
        Vector2D vertex = b.getPoints()[b.getNumPoints() - 1];
        double lastX = vertex.getX() + b.x, lastY = vertex.getY() + b.y;
        for(int i = 0; i < b.getNumPoints(); i++){
            Vector2D normal = b.getNormals()[i];
            double projVel = Vector2D.unitScalarProject(relativeVelX, relativeVelY, normal);
            Vector2D point = b.getPoints()[i];
            double x = point.getX();
            double y = point.getY();
            double aPos = Vector2D.unitScalarProject(a.x, a.y, normal);
            double bPos = Vector2D.unitScalarProject(b.x, b.y, normal);
            double aMin = aPos - a.radius;
            double aMax = aPos + a.radius;
            double bMin = b.getNormalMins()[i] + bPos;
            double bMax = b.getNormalMaxs()[i] + bPos;
            double timeOfInpact = getTOIAlongAxis(bMin, bMax, aMin, aMax, projVel);
            if(timeOfInpact == NO_COLLISION){
                result.set(Collision.NO_COLLISION);
                return;
            }
            if(timeOfInpact > maxEntryTime){
                double collisionX = -relativeVelX * timeOfInpact + a.x;
                double collisionY = -relativeVelY * timeOfInpact + a.y;
                if(Vector2D.isPointsProjectionWithinLine(collisionX, collisionY, x, y, lastX, lastY)){
                    maxEntryTime = timeOfInpact;
                    collisionNormal.set(normal);
                }
            }
            minLeaveTime = Math.min(minLeaveTime, getLeaveTimeAlongAxis(bMin, bMax, aMin, aMax, projVel));
            lastX = x;
            lastY = y;
        }
        if(maxEntryTime <= minLeaveTime){
            result.set(maxEntryTime, collisionNormal, a, b);
        }
        double velLength = Math.sqrt(relativeVelX * relativeVelX + relativeVelY * relativeVelY);
        if(maxEntryTime == -Double.MAX_VALUE){
            result.set(Double.MAX_VALUE, collisionNormal, a, b);
            for(Vector2D point: b.getPoints()){
                double x = point.getX() + b.x;
                double y = point.getY() + b.y;
                getCirclePointTOI(a, b, x, y, relativeVelX, relativeVelY, velLength,  result);
            }
        }
        if(result.getTimeToCollision() > maxTime){
            result.set(Collision.NO_COLLISION);
        }
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
    
    public static void collideAABBAABB(AABBShape a, AABBShape b, double maxTime, Collision result){
        double relativeVelX = a.dx - b.dx, relativeVelY = a.dy - b.dy;
        double maxEntryTime, minLeaveTime = Double.MAX_VALUE;
        double aMaxX = a.x + a.getHalfWidth(), aMinX = a.x - a.getHalfWidth(), aMaxY = a.y + a.getHalfHeight(), aMinY = a.y - a.getHalfHeight();
        double bMaxX = b.x + b.getHalfWidth(), bMinX = b.x - b.getHalfWidth(), bMaxY = b.y + b.getHalfHeight(), bMinY = b.y - b.getHalfHeight();
        
        maxEntryTime = getTOIAlongAxis(aMinX, aMaxX, bMinX, bMaxX, relativeVelX);
        if (maxEntryTime == NO_COLLISION){
            result.set(Collision.NO_COLLISION);
            return;
        }else if(maxEntryTime != -Double.MAX_VALUE){
            result.getCollisionNormal().set(1, 0);
        }
        minLeaveTime = getLeaveTimeAlongAxis(aMinX, aMaxX, bMinX, bMaxX, relativeVelX);
        
        double time = getTOIAlongAxis(aMinY, aMaxY, bMinY, bMaxY, relativeVelY);
        if(time == NO_COLLISION){
            result.set(Collision.NO_COLLISION);
            return;
        }else if(time > maxEntryTime){
            maxEntryTime = time;
            result.getCollisionNormal().set(0, 1);
        }
        minLeaveTime = Math.min(minLeaveTime, getLeaveTimeAlongAxis(aMinY, aMaxY, bMinY, bMaxY, relativeVelY));
        if(maxEntryTime == -Double.MAX_VALUE || maxEntryTime > maxTime || maxEntryTime > minLeaveTime){
            result.set(Collision.NO_COLLISION);
            return;
        }
        result.set(maxEntryTime, result.getCollisionNormal(), a, b);
    }
    
    public static void collideAABBCircle(AABBShape a, Shape b, double maxTime, Collision result){
        double relativeVelX = a.dx - b.dx, relativeVelY = a.dy - b.dy;
        double aMaxX = a.x + a.getHalfWidth(), aMinX = a.x - a.getHalfWidth();
        double aMaxY = a.y + a.getHalfHeight(), aMinY = a.y - a.getHalfHeight();
        double bMaxX = b.x + b.radius, bMinX = b.x - b.radius;
        double bMaxY = b.y + b.radius, bMinY = b.y - b.radius;
        Vector2D collisionNormal = result.getCollisionNormal();
        double maxEntryTime = getCircleBoxTOIBeforeCheckingPoints(b.x, b.y, bMinX, bMaxX, bMinY, bMaxY, aMinX, aMaxX, aMinY, aMaxY, relativeVelX, relativeVelY, result);
        if(maxEntryTime == NO_COLLISION){
            result.set(Collision.NO_COLLISION);
            return;
        }
        if(maxEntryTime == -Double.MAX_VALUE){
            maxEntryTime *= -1;
        }
        result.set(maxEntryTime, collisionNormal, b, a);
        double velLength = Math.sqrt(relativeVelX * relativeVelX + relativeVelY * relativeVelY);
        maxEntryTime = Math.min(maxEntryTime, getCirclePointTOI(b, a, aMinX, aMinY, relativeVelX, relativeVelY, velLength,  result));
        maxEntryTime = Math.min(maxEntryTime, getCirclePointTOI(b, a, aMaxX, aMinY, relativeVelX, relativeVelY, velLength,  result));
        maxEntryTime = Math.min(maxEntryTime, getCirclePointTOI(b, a, aMaxX, aMaxY, relativeVelX, relativeVelY, velLength,  result));
        maxEntryTime = Math.min(maxEntryTime, getCirclePointTOI(b, a, aMinX, aMaxY, relativeVelX, relativeVelY, velLength,  result));
        if(maxEntryTime == -Double.MAX_VALUE || maxEntryTime > maxTime){
            result.set(Collision.NO_COLLISION);
        }
    }
    
    private static void getEntryAndLeaveTime(Vector2D[] aNormals, Vector2D[] bPoints, double[] aMins, double[]aMaxs, double aX, double aY, double bX, double bY, double relativeVelX, double relativeVelY, double[] result, Vector2D collisionNormal){
        for(int i = 0; i < aNormals.length; i++){
            Vector2D normal = aNormals[i];
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
            double centerA = Vector2D.unitScalarProject(aX, aY, normal);
            double centerB = Vector2D.unitScalarProject(bX, bY, normal);
            bMin += centerB;
            bMax += centerB;
            double aMin = aMins[i] + centerA;
            double aMax = aMaxs[i] + centerA;
            double projVel = -Vector2D.unitScalarProject(relativeVelX, relativeVelY, normal);
            double time = getTOIAlongAxis(bMin, bMax, aMin, aMax, projVel);
            if(time == NO_COLLISION){ 
                result[0] = NO_COLLISION;
                result[1] = -Double.MAX_VALUE;
                return;
            }
            if(time >= result[0]){
                result[0] = time;
                collisionNormal.set(normal);
            }
            result[1] = Math.min(result[1], getLeaveTimeAlongAxis(bMin, bMax, aMin, aMax, projVel));
        }
    }
    
    private static double getLeaveTimeAlongAxis(double aMin, double aMax, double bMin, double bMax, double vel){
        double timeToLeave = Double.MAX_VALUE;
        if(bMax > aMin && vel < 0){
            timeToLeave = (aMin - bMax) / vel;
        }else if(aMax > bMin && vel > 0){
            timeToLeave = (aMax - bMin) / vel;
        }
        return timeToLeave;
    }
    
    /**
     * -Double.MAX_VALUE is returned if already overlapping
     * NO_COLLISION is return if a collision will not happen;
     * @param aMin
     * @param aMax
     * @param bMin
     * @param bMax
     * @param vel
     * @return 
     */
    private static double getTOIAlongAxis(double aMin, double aMax, double bMin, double bMax, double vel){
        double travelTime = -Double.MAX_VALUE;
        if(aMax <= bMin){
            if(vel <= 0){
                return NO_COLLISION;
            }
            travelTime = (bMin - aMax) / vel;
        }else if(aMin >= bMax){
            if(vel >= 0){
                return NO_COLLISION;
            }
            travelTime = (bMax - aMin) / vel;
        }
        return travelTime;
    }
    
    private static double getCircleBoxTOIBeforeCheckingPoints(double aX, double aY, double aMinX, double aMaxX, double aMinY, double aMaxY,
                                                              double bMinX, double bMaxX, double bMinY, double bMaxY,
                                                              double velX, double velY, Collision result){
        double travelTime = -Double.MAX_VALUE;
        if(aY >= bMinY && aY <= bMaxY){
            travelTime = getTOIAlongAxis(aMinX, aMaxX, bMinX, bMaxX, velX);
            if(travelTime != -Double.MAX_VALUE){
                result.getCollisionNormal().set(1, 0);
            }
        }
        if(aX >= bMinX && aX <= bMaxX){
            double time = getTOIAlongAxis(aMinY, aMaxY, bMinY, bMaxY, velY);
            if(time > travelTime){
                travelTime = time;
                result.getCollisionNormal().set(0, 1);
            }
        }
        return travelTime;
    }
    
    private static double getCirclePointTOI(Shape a, Shape b, double px, double py, double pVelX, double pVelY, double velLength, Collision result){
        double distToVelRaySquared = Vector2D.distToLineSquared(a.x, a.y, px, py, px + pVelX, py + pVelY);
        double radiusSquared = a.radius * a.radius;
        if(distToVelRaySquared <= radiusSquared){
            // in the path of the velocity ray
            double deltaX = a.x - px;
            double deltaY = a.y - py;
            double projLength = Vector2D.scalarProject(deltaX, deltaY, pVelX, pVelY, velLength);
            if(projLength >= 0){
                // travelling towards each other
                double subLength = Math.sqrt(radiusSquared - distToVelRaySquared);
                double travelDist = projLength - subLength;
                double travelTime = travelDist / velLength;
                if(travelTime <= result.getTimeToCollision()){
                    double distOverVel = travelDist / velLength;
                    result.getCollisionNormal().set(a.x - px - pVelX * distOverVel, a.y - py - pVelY * distOverVel).unit();
                    result.set(travelTime, result.getCollisionNormal(), a, b);
                    return travelTime;
                }
            }
        }
        return Double.MAX_VALUE;
        
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
        maxOverlapTime = Math.min(getTOICirclePoint(a.x, a.y, radiusSquared, b.getMinX(), b.getMinY(), relativeVelX, relativeVelY), maxOverlapTime);
        maxOverlapTime = Math.min(getTOICirclePoint(a.x, a.y, radiusSquared, b.getMaxX(), b.getMinY(), relativeVelX, relativeVelY), maxOverlapTime);
        maxOverlapTime = Math.min(getTOICirclePoint(a.x, a.y, radiusSquared, b.getMaxX(), b.getMaxY(), relativeVelX, relativeVelY), maxOverlapTime);
        maxOverlapTime = Math.min(getTOICirclePoint(a.x, a.y, radiusSquared, b.getMinX(), b.getMaxY(), relativeVelX, relativeVelY), maxOverlapTime);
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
        return dist / relativeVel;
    }
    
    public abstract int getShapeType();
    
    public abstract void draw(Graphics2D g, Color color);
}
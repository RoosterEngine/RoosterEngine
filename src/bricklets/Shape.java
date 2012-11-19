package bricklets;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;

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
    
    public static void collidePolyPolyBackup(Polygon a, Polygon b,double maxTime, Collision result){
        double dx = a.dx - b.dx;
        double dy = a.dy - b.dy;
        Vector2D[] normalsA = a.getNormals();
        Vector2D collisionNormal = result.getCollisionNormal().clear();
        double[] aMins = a.getNormalMins();
        double[] aMaxs = a.getNormalMaxs();
        double entryTime = -Double.MAX_VALUE;
        double leaveTime = Double.MAX_VALUE;
        for(int i = 0; i < a.getNumPoints(); i++){
            Vector2D normal = normalsA[i];
            double bMin = Double.MAX_VALUE;
            double bMax = -Double.MAX_VALUE;
            for(Vector2D vertex: b.getPoints()){
                double relX = vertex.getX() + b.x - a.x;
                double relY = vertex.getY() + b.y - a.y;
                double dist = Vector2D.unitScalarProject(relX, relY, normal);
                bMin = Math.min(bMin, dist);
                bMax = Math.max(bMax, dist);
            }
            double aMin = aMins[i];
            double aMax = aMaxs[i];
            double projVel = Vector2D.unitScalarProject(dx, dy, normal);
            if(aMax < bMin){
                // a is on the left of b and is not overlapping
                if(projVel <= 0){
                    // not travelling towards each other
                    result.set(Collision.NO_COLLISION);
                    return;
                }
                double TOI = (bMin - aMax) / projVel;
                if(TOI > entryTime){
                    entryTime = TOI;
                    collisionNormal.set(normal);
                }
            }else if(bMax < aMin){
                // a is on the right of b and is not overlapping
                if(projVel >= 0){
                    // not travelling towards each other
                    result.set(Collision.NO_COLLISION);
                    return;
                }
                double TOI = (aMin - bMax) / -projVel;
                if(TOI > entryTime){
                    entryTime = TOI;
                    collisionNormal.set(normal);
                }
            }
            
            // when calculating the leave time, I don't need to check to see if
            // a and b are not overlapping and travelling away from each other
            // because we have early outs when calculating the entrytime that
            // handle these cases
            if(projVel > 0){
                leaveTime = Math.min(leaveTime, (bMax - aMin) / projVel);
            }else if(projVel < 0){
                leaveTime = Math.min(leaveTime, (aMax - bMin) / -projVel);
            }
        }
        Vector2D[] normalsB = b.getNormals();
        double[] bMins = b.getNormalMins();
        double[] bMaxs = b.getNormalMaxs();
        for(int i = 0; i < b.getNumPoints(); i++){
            Vector2D normal = normalsB[i];
            double aMin = Double.MAX_VALUE;
            double aMax = -Double.MAX_VALUE;
            for(Vector2D vertex: a.getPoints()){
                double relX = vertex.getX() + a.x - b.x;
                double relY = vertex.getY() + a.y - b.y;
                double dist = Vector2D.unitScalarProject(relX, relY, normal);
                aMin = Math.min(aMin, dist);
                aMax = Math.max(aMax, dist);
            }
            double bMin = bMins[i];
            double bMax = bMaxs[i];
            double projVel = Vector2D.unitScalarProject(dx, dy, normal);
            if(aMax < bMin){
                // a is on the left of b and is not overlapping
                if(projVel <= 0){
                    // not travelling towards each other
                    result.set(Collision.NO_COLLISION);
                    return;
                }
                double TOI = (bMin - aMax) / projVel;
                if(TOI > entryTime){
                    entryTime = TOI;
                    collisionNormal.set(normal);
                }
            }else if(bMax < aMin){
                // a is on the right of b and is not overlapping
                if(projVel >= 0){
                    // not travelling towards each other
                    result.set(Collision.NO_COLLISION);
                    return;
                }
                double TOI = (aMin - bMax) / -projVel;
                if(TOI > entryTime){
                    entryTime = TOI;
                    collisionNormal.set(normal);
                }
            }
            
            // when calculating the leave time, I don't need to check to see if
            // a and b are not overlapping and travelling away from each other
            // because we have early outs when calculating the entrytime that
            // handle these cases
            if(projVel > 0){
                leaveTime = Math.min(leaveTime, (bMax - aMin) / projVel);
            }else if(projVel < 0){
                leaveTime = Math.min(leaveTime, (aMax - bMin) / -projVel);
            }
        }
        if(entryTime > maxTime || entryTime > leaveTime || entryTime == -Double.MAX_VALUE){
            result.set(Collision.NO_COLLISION);
            return;
        }
        result.set(entryTime, collisionNormal, a, b);
        a.getParentEntity().setDebugVector(collisionNormal);
        b.getParentEntity().setDebugVector(collisionNormal);
    }
    
    public static void collidePolyPoly(Polygon a, Polygon b, double maxTime, Collision result){
        if(!willBoundingCollide(a, b, maxTime)){
            result.set(Collision.NO_COLLISION);
            return;
        }
        double dx = a.dx - b.dx;
        double dy = a.dy - b.dy;
        Vector2D collisionNormal = result.getCollisionNormal().clear();
        double[] entryLeaveOverlapTimes = {-Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE};
        Vector2D overlapNormal = new Vector2D();
        double overlapVel = getEntryLeaveAndOverlapTime(a, b, dx, dy, entryLeaveOverlapTimes, collisionNormal, overlapNormal, 0);
        if(entryLeaveOverlapTimes[0] == NO_COLLISION){
            result.set(Collision.NO_COLLISION);
            return;
        }
        overlapVel = getEntryLeaveAndOverlapTime(b, a, -dx, -dy, entryLeaveOverlapTimes, collisionNormal, overlapNormal, overlapVel);
        double entryTime = entryLeaveOverlapTimes[0];
        if(entryTime == NO_COLLISION){
            result.set(Collision.NO_COLLISION);
            return;
        }
        if(entryTime == -Double.MAX_VALUE){
            if(overlapVel < 0){
                a.getParentEntity().setDebugVector(overlapNormal);
                System.out.println(overlapNormal);
                result.set(0, overlapNormal, a, b);
                return;
            }
        }
        double leaveTime = entryLeaveOverlapTimes[1];
        if(entryTime > maxTime || entryTime > leaveTime || entryTime == -Double.MAX_VALUE){
            result.set(Collision.NO_COLLISION);
            a.getParentEntity().setColor(Color.RED);
            return;
        }
        result.set(entryTime, collisionNormal, a, b);
    }
    
    public static void collideAABBPoly(AABBShape a, Polygon b, double maxTime, Collision result){
        Vector2D collisionNormal = result.getCollisionNormal().clear();
        double relativeVelX = a.dx - b.dx;
        double relativeVelY = a.dy - b.dy;
        double maxEntryTime = -Double.MAX_VALUE, minLeaveTime = Double.MAX_VALUE;
        Vector2D[] normals = b.getNormals();
        double[] bMins = b.getNormalMins();
        double[] bMaxs = b.getNormalMaxs();
        double left = a.x - a.getHalfWidth(), right = a.x + a.getHalfWidth();
        double top = a.y - a.getHalfHeight(), bottom = a.y + a.getHalfHeight();
        
        for(int i = 0; i < b.getNumPoints(); i++){
            Vector2D normal = normals[i];
            double projDist = Vector2D.unitScalarProject(left, top, normal);
            double aMin = projDist;
            double aMax = projDist;
            projDist = Vector2D.unitScalarProject(right, top, normal);
            aMin = Math.min(aMin, projDist);
            aMax = Math.max(aMax, projDist);
            projDist = Vector2D.unitScalarProject(right, bottom, normal);
            aMin = Math.min(aMin, projDist);
            aMax = Math.max(aMax, projDist);
            projDist = Vector2D.unitScalarProject(left, bottom, normal);
            aMin = Math.min(aMin, projDist);
            aMax = Math.max(aMax, projDist);
            
            double bPos = Vector2D.unitScalarProject(b.x, b.y, normal);
            double bMin = bMins[i] + bPos;
            double bMax = bMaxs[i] + bPos;
            double projVel = Vector2D.unitScalarProject(relativeVelX, relativeVelY, normal);
            double TOI = getTOIAlongAxis(aMin, aMax, bMin, bMax, projVel);
            if(TOI == NO_COLLISION){
                result.set(Collision.NO_COLLISION);
                return;
            }
            if(TOI > maxEntryTime){
                maxEntryTime = TOI;
                collisionNormal.set(normal);
            }
            minLeaveTime = Math.min(minLeaveTime, getLeaveTimeAlongAxis(aMin, aMax, bMin, bMax, projVel));
        }
        
        Vector2D[] bPoints = b.getPoints();
        double bMinX = Double.MAX_VALUE;
        double bMaxX = -Double.MAX_VALUE;
        double bMinY = Double.MAX_VALUE;
        double bMaxY = -Double.MAX_VALUE;
        for(int i = 0; i < b.getNumPoints(); i++){
            Vector2D point = bPoints[i];
            double x = point.getX() + b.x;
            bMinX = Math.min(bMinX, x);
            bMaxX = Math.max(bMaxX, x);
            double y = point.getY() + b.y;
            bMinY = Math.min(bMinY, y);
            bMaxY = Math.max(bMaxY, y);
        }
        double TOI = getTOIAlongAxis(left, right, bMinX, bMaxX, relativeVelX);
        if(TOI > maxEntryTime){
            maxEntryTime = TOI;
            collisionNormal.set(1, 0);
        }
        minLeaveTime = Math.min(minLeaveTime, getLeaveTimeAlongAxis(left, right, bMinX, bMaxX, relativeVelX));
        TOI = getTOIAlongAxis(top, bottom, bMinY, bMaxY, relativeVelY);
        if(TOI > maxEntryTime){
            maxEntryTime = TOI;
            collisionNormal.set(0, 1);
        }
        minLeaveTime = Math.min(minLeaveTime, getLeaveTimeAlongAxis(top, bottom, bMinY, bMaxY, relativeVelY));
        if(maxEntryTime == -Double.MAX_VALUE || maxEntryTime > maxTime || maxEntryTime > minLeaveTime){
            result.set(Collision.NO_COLLISION);
            return;
        }
        result.set(maxEntryTime, collisionNormal, a, b);
    }
    
    public static void collideCirclePoly(Shape a, Polygon b, double maxTime, Collision result){
        satCollideCirclePoly(a, b, maxTime, result);
//        naiveCollideCirclePoly(a, b, maxTime, result);
    }
    
    private static void naiveCollideCirclePolybACKUP(Shape a, Polygon b, double maxTime, Collision result){
        double relativeVelX = a.dx - b.dx, relativeVelY = a.dy - b.dy;
        double minTOI = Double.MAX_VALUE;
        double minOverlapTime = Double.MAX_VALUE;
        Vector2D[] bPoints = b.getPoints();
        Vector2D[] bNormals = b.getNormals();
        Vector2D collisionNormal = result.getCollisionNormal().clear();
        Vector2D vertex = bPoints[b.getNumPoints() - 1];
        double lastX = vertex.getX() + b.x, lastY = vertex.getY() + b.y;
        for(int i = 0; i < b.getNumPoints(); i++){
            Vector2D normal = bNormals[i];
            Vector2D point = bPoints[i];
            double x = point.getX() + b.x;
            double y = point.getY() + b.y;
            double projVel = Vector2D.unitScalarProject(relativeVelX, relativeVelY, normal);
            if(projVel < 0){
                double closestX = a.x - normal.getX() * a.radius;
                double closestY = a.y - normal.getY() * a.radius;
                double dist = Vector2D.distToLine(closestX, closestY, lastX, lastY, x, y);
                double TOI = dist / -projVel;
                double collisionX = a.x + relativeVelX * TOI;
                double collisionY = a.y + relativeVelY * TOI;
                if(TOI < minTOI && Vector2D.isPointsProjectionWithinLine(collisionX, collisionY, lastX, lastY, x, y)){
                    minTOI = TOI;
                    collisionNormal.set(normal);
                }
            }
            lastX = x;
            lastY = y;
        }
        if(minTOI != Double.MAX_VALUE && minTOI < maxTime){
            result.set(minTOI, collisionNormal, a, b);
            return;
        }
        double velLength = Math.sqrt(relativeVelX * relativeVelX + relativeVelY * relativeVelY);
        result.set(minTOI, collisionNormal, a, b);
        for(Vector2D point: b.getPoints()){
            double x = point.getX() + b.x;
            double y = point.getY() + b.y;
            getCirclePointTOI(a, b, x, y, -relativeVelX, -relativeVelY, velLength,  result);
        }
        if(result.getTimeToCollision() > maxTime){
            result.set(Collision.NO_COLLISION);
        }
    }
    
    private static void naiveCollideCirclePoly(Shape a, Polygon b, double maxTime, Collision result){
        double relVelX = a.dx - b.dx, relVelY = a.dy - b.dy;
        double minTOI = Double.MAX_VALUE;
        double minOverlapTime = Double.MAX_VALUE;
        boolean overlapping = true;
        Vector2D[] bPoints = b.getPoints();
        Vector2D[] bNormals = b.getNormals();
        Vector2D collisionNormal = result.getCollisionNormal().clear();
        Vector2D vertex = bPoints[b.getNumPoints() - 1];
        double lastX = vertex.getX() + b.x, lastY = vertex.getY() + b.y;
        for(int i = 0; i < b.getNumPoints(); i++){
            Vector2D normal = bNormals[i];
            Vector2D point = bPoints[i];
            double x = point.getX() + b.x;
            double y = point.getY() + b.y;
            double projVel = Vector2D.unitScalarProject(relVelX, relVelY, normal);
//            if(projVel < 0){
                double xAlongNegativeNormal = a.x - normal.getX() * a.radius;
                double yAlongNegativeNormal = a.y - normal.getY() * a.radius;
                double dist = Vector2D.signedDistToLine
                        (xAlongNegativeNormal, yAlongNegativeNormal,
                        lastX, lastY, x, y);
                if(dist < 0){
                    // overlapping
                    double timeSinceOverlap = dist/ projVel;
                    double collisionX = a.x - relVelX * timeSinceOverlap;
                    double collisionY = a.y - relVelY * timeSinceOverlap;
                    a.getParentEntity().setDebugVector(new Vector2D(collisionX - a.x, collisionY - a.y));
                    if(timeSinceOverlap < minOverlapTime
                            && Vector2D.isPointsProjectionWithinLine
                            (collisionX, collisionY, lastX, lastY, x, y)){
                        minOverlapTime = timeSinceOverlap;
                        collisionNormal.set(normal);
                    }
                }else{
                    overlapping = false;
                    double TOI = dist / -projVel;
                    double collisionX = a.x + relVelX * TOI;
                    double collisionY = a.y + relVelY * TOI;
                    if(TOI < minTOI && Vector2D.isPointsProjectionWithinLine
                            (collisionX, collisionY, lastX, lastY, x, y)){
                        minTOI = TOI;
                        collisionNormal.set(normal);
                    }
                }
//                double closestX = a.x - normal.getX() * a.radius;
//                double closestY = a.y - normal.getY() * a.radius;
//                double dist = Vector2D.distToLine(closestX, closestY, lastX, lastY, x, y);
//                double TOI = dist / -projVel;
//                double collisionX = a.x + relativeVelX * TOI;
//                double collisionY = a.y + relativeVelY * TOI;
//                if(TOI < minTOI && Vector2D.isPointsProjectionWithinLine(collisionX, collisionY, lastX, lastY, x, y)){
//                    minTOI = TOI;
//                    collisionNormal.set(normal);
//                }
//            }
            lastX = x;
            lastY = y;
        }
        if(overlapping && minOverlapTime != Double.MAX_VALUE){
            result.set(0, collisionNormal, a, b);
            return;
        }
        if(minTOI != Double.MAX_VALUE && minTOI < maxTime){
            result.set(minTOI, collisionNormal, a, b);
            return;
        }
        double velLength = Math.sqrt(relVelX * relVelX + relVelY * relVelY);
        result.set(minTOI, collisionNormal, a, b);
        for(Vector2D point: b.getPoints()){
            double x = point.getX() + b.x;
            double y = point.getY() + b.y;
            getCirclePointTOI(a, b, x, y, -relVelX, -relVelY, velLength,  result);
        }
        if(result.getTimeToCollision() > maxTime){
            result.set(Collision.NO_COLLISION);
        }
    }
    
    private static void satCollideCirclePoly(Shape a, Polygon b, double maxTime, Collision result){
        double relVelX = a.dx - b.dx;
        double relVelY = a.dy - b.dy;
        double entryTime = -Double.MAX_VALUE;
        double leaveTime = Double.MAX_VALUE;
        Vector2D[] normals = b.getNormals();
        double[] bMins = b.getNormalMins();
        double[] bMaxs = b.getNormalMaxs();
        Vector2D collisionNormal = result.getCollisionNormal();
        for(int i = 0; i < b.getNumPoints(); i++){
            Vector2D normal = normals[i];
            double aPos = Vector2D.unitScalarProject(a.x - b.x, a.y - b.y, normal);
            double aMin = aPos - a.radius;
            double aMax = aPos + a.radius;
            double bMin = bMins[i];
            double bMax = bMaxs[i];
            double projVel = Vector2D.unitScalarProject(relVelX, relVelY, normal);
            double TOI = getTOIAlongAxis(aMin, aMax, bMin, bMax, projVel);
            if(TOI == NO_COLLISION){
                result.set(Collision.NO_COLLISION);
                return;
            }
            if(TOI > entryTime){
                entryTime = TOI;
                collisionNormal.set(normal);
            }
            leaveTime = Math.min(leaveTime, getLeaveTimeAlongAxis(aMin, aMax, bMin, bMax, projVel));
        }
        Vector2D[] bPoints = b.getPoints();
        for(Vector2D vertex: bPoints){
            // checking for collision with points
            double dx = vertex.getX() + b.x - a.x;
            double dy = vertex.getY() + b.y - a.y;
            double dist = Math.sqrt(dx * dx + dy * dy);
            dx /= dist;
            dy /= dist;
            double bMin = Double.MAX_VALUE;
            double bMax = -Double.MAX_VALUE;
            for(Vector2D point: bPoints){
                double projDist = Vector2D.unitScalarProject(point.getX() + b.x - a.x, point.getY() + b.y - a.y, dx, dy);
                bMin = Math.min(bMin, projDist);
                bMax = Math.max(bMax, projDist);
            }
            double projVel = Vector2D.unitScalarProject(relVelX, relVelY, dx, dy);
            double TOI = getTOIAlongAxis(-a.radius, a.radius, bMin, bMax, projVel);
            if(TOI == NO_COLLISION){
                result.set(Collision.NO_COLLISION);
                return;
            }
            if(TOI > entryTime){
                entryTime = TOI;
                collisionNormal.set(dx, dy);
            }
            leaveTime = Math.min(leaveTime, getLeaveTimeAlongAxis(-a.radius, a.radius, bMin, bMax, projVel));
        }
        if(entryTime == -Double.MAX_VALUE){
            // a and b are overlapping
            double minTOI = Double.MAX_VALUE;
            double velOnNormal = 0;
            Vector2D lastPoint = bPoints[bPoints.length - 1];
            double lastX = lastPoint.getX() + b.x;
            double lastY = lastPoint.getY() + b.y;
            for(int i = 0; i < b.getNumPoints(); i++){
                Vector2D normal = normals[i];
                Vector2D point = bPoints[i];
                double x = point.getX() + b.x;
                double y = point.getY() + b.y;
                double projVel = Vector2D.unitScalarProject(relVelX, relVelY, normal);
                double distSquared = Vector2D.distToLineSquared(
                        a.x - normal.getX() * a.radius,
                        a.y - normal.getY() * a.radius,
                        lastX, lastY, x, y);
                double timeSquared = distSquared /(projVel * projVel);
                if(timeSquared < minTOI){
                    minTOI = timeSquared;
                    collisionNormal.set(normal);
                    velOnNormal = projVel;
                }
                lastX = x;
                lastY = y;
            }
            if(velOnNormal < 0){
                result.set(0, collisionNormal, a, b);
                return;
            }
        }
        if(entryTime > maxTime || entryTime > leaveTime || entryTime == -Double.MAX_VALUE){
            result.set(Collision.NO_COLLISION);
            return;
        }
        result.set(entryTime, collisionNormal, a, b);
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
        if(travelTime < 0){
            velocity.set(deltaX / distBetween, deltaY / distBetween);
            result.set(0, velocity, a, b);
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
        double relativeVelX = b.dx - a.dx, relativeVelY = b.dy - a.dy;
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
        relativeVelX *= -1;
        relativeVelY *= -1;
        double velLength = Math.sqrt(relativeVelX * relativeVelX + relativeVelY * relativeVelY);
        maxEntryTime = Math.min(maxEntryTime, getCirclePointTOI(b, a, aMinX, aMinY, relativeVelX, relativeVelY, velLength,  result));
        maxEntryTime = Math.min(maxEntryTime, getCirclePointTOI(b, a, aMaxX, aMinY, relativeVelX, relativeVelY, velLength,  result));
        maxEntryTime = Math.min(maxEntryTime, getCirclePointTOI(b, a, aMaxX, aMaxY, relativeVelX, relativeVelY, velLength,  result));
        maxEntryTime = Math.min(maxEntryTime, getCirclePointTOI(b, a, aMinX, aMaxY, relativeVelX, relativeVelY, velLength,  result));
        if(maxEntryTime == -Double.MAX_VALUE || maxEntryTime > maxTime){
            result.set(Collision.NO_COLLISION);
        }
    }
    
    private static double getEntryLeaveAndOverlapTime(Polygon a, Polygon b, double relativeVelX, double relativeVelY, double[] result, Vector2D collisionNormal, Vector2D overlapNormal, double currentOverlapVel){
        Vector2D[] aNormals = a.getNormals();
        Vector2D[] bPoints = b.getPoints();
        double[] aMins = a.getNormalMins();
        double[] aMaxs = a.getNormalMaxs();
        double overlapNX = 0;
        double overlapNY = 0;
        double overlapVel = currentOverlapVel;
        boolean overlapUpdated = false;
        for(int i = 0; i < aNormals.length; i++){
            Vector2D normal = aNormals[i];
            double bMin = Double.MAX_VALUE;
            double bMax = -Double.MAX_VALUE;
            for(Vector2D point: bPoints){
                double x = point.getX() + b.x - a.x;
                double y = point.getY() + b.y - a.y;
                double dist = Vector2D.unitScalarProject(x, y, normal);
                if(dist < bMin){
                    bMin = dist;
                }
                if(dist > bMax){
                    bMax = dist;
                }
            }
            double aMin = aMins[i];
            double aMax = aMaxs[i];
            double projVel = Vector2D.unitScalarProject(relativeVelX, relativeVelY, normal);
            double time = getTOIAlongAxis(aMin, aMax, bMin, bMax, projVel);
            if(time == NO_COLLISION){ 
                result[0] = NO_COLLISION;
                result[1] = -Double.MAX_VALUE;
                return overlapVel;
            }
            if(time >= result[0]){
                result[0] = time;
                collisionNormal.set(normal);
            }
            result[1] = Math.min(result[1], getLeaveTimeAlongAxis(aMin, aMax, bMin, bMax, projVel));
            // need to set the overlap time
//            double maxMin = Math.max(aMin, bMin);
//            double minMax = Math.min(aMax, bMax);
//            double dist = minMax - maxMin;
            double dist = bMax - aMin;
            if(dist > 0){
                time = dist / Math.abs(projVel);
                if(time < result[2]){
                    overlapUpdated = true;
                    result[2] = time;
                    overlapNX = normal.getX();
                    overlapNY = normal.getY();
                    overlapVel = projVel;
                }
            }
        }
        
        if(result[0] == -Double.MAX_VALUE && overlapUpdated){
            overlapNormal.set(overlapNX, overlapNY);
            return overlapVel;
        }
        return overlapVel;
    }
    
    private static double getLeaveTimeAlongAxis(double aMin, double aMax, double bMin, double bMax, double vel){
        double leaveTime = Double.MAX_VALUE;
        if(vel > 0){
            leaveTime = Math.min(leaveTime, (bMax - aMin) / vel);
        }else if(vel < 0){
            leaveTime = Math.min(leaveTime, (aMax - bMin) / -vel);
        }
        return leaveTime;
    }
    
    /**
     * -Double.MAX_VALUE is returned if already overlapping
     * NO_COLLISION is returned if a collision will not happen; 
     * assuming that a is moving and b is staying still;
     * @param aMin
     * @param aMax
     * @param bMin
     * @param bMax
     * @param vel
     * @return 
     */
    private static double getTOIAlongAxis(double aMin, double aMax, double bMin, double bMax, double vel){
        double travelTime = -Double.MAX_VALUE;
//        if(aMin <= bMin){
        if(aMax <= bMin){
            if(vel <= 0){
                return NO_COLLISION;
            }
            travelTime = (bMin - aMax) / vel;
//        }else if(aMax >= bMax){
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
        distBetween -= radiiSum;
        if(distBetween <= 0){
            return true;
        }
        double projVel = Vector2D.scalarProject(combinedVelX, combinedVelY, deltaX, deltaY, distBetween);
        if(projVel < 0){
            // travelling away from each other
            return false;
        }
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
//    
//    /**
//     * return Vector2D is temporary
//     * @param directionX
//     * @param directionY
//     * @return 
//     */
//    public abstract Vector2D support(Vector2D direction);
    
    public abstract int getShapeType();
    
    public abstract void draw(Graphics2D g, Color color);
}
package bricklets;

import gameengine.Context;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 *
 * @author david
 */
public class BoundingBox{
    private Polygon polygon;
    private Vector2D velocity;
    
    public void getTimeToCollision(BoundingBox b, double maxTime, Collision result){
        Vector2D[] aNormals = polygon.getNormals();
        Vector2D[] bNormals = b.polygon.getNormals();
        Vector2D[] aPoints = polygon.getPoints();
        Vector2D collisionNormal = new Vector2D();
        double[] bMins = b.polygon.getNormalMins();
        double[] bMaxs = b.polygon.getNormalMaxs();
        Vector2D combinedVelocity = new Vector2D(b.velocity).subtract(velocity);
        double maxEntryTime = -Double.MAX_VALUE, minLeaveTime = Double.MAX_VALUE;
        
        for(int i = 0; i < bNormals.length; i++){
            Vector2D normal = bNormals[i];
            double aMin = Double.MAX_VALUE;
            double aMax = -Double.MAX_VALUE;
            for(Vector2D point: aPoints){
                double dist = point.unitScalarProject(normal);
                if(dist < aMin){
                    aMin = dist;
                }
                if(dist > aMax){
                    aMax = dist;
                }
            }
//            double centerA = polygon.getCenter().unitScalarProject(normal);
//            double centerB = b.polygon.getCenter().unitScalarProject(normal);
//            aMin += centerA;
//            aMax += centerA;
//            double bMin = bMins[i] + centerB;
//            double bMax = bMaxs[i] + centerB;
            double projVel = combinedVelocity.unitScalarProject(normal);
//            if(aMax <= bMin){
//                if(projVel < 0){
//                    double timeToOverlap = (aMax - bMin) / projVel;
//                    if(timeToOverlap > maxEntryTime){
//                        maxEntryTime = timeToOverlap;
//                        collisionNormal = normal;
//                    }
//                }else{
//                    // not travelling away from each other
//                    //TODO should have an early return here
//                    maxEntryTime = Collider.NO_COLLISION;
//                }
//            }else if(bMax <= aMin){
//                if(projVel > 0){
//                    double timeToOverlap = (aMin - bMax) / projVel;
//                    if(timeToOverlap > maxEntryTime){
//                        maxEntryTime = timeToOverlap;
//                        collisionNormal = normal;
//                    }
//                }else{
//                    // not travelling away from each other
//                    //TODO should have an early return here
//                    maxEntryTime = Collider.NO_COLLISION;
//                }
//            }
            
//            if(bMax > aMin && projVel < 0){
//                double timeToLeave = (aMin - bMax) / projVel;
//                if(timeToLeave < minLeaveTime){
//                    minLeaveTime = timeToLeave;
//                }
//            }else if(aMax > bMin && projVel > 0){
//                double timeToLeave = (aMax - bMin) / projVel;
//                if(timeToLeave < minLeaveTime){
//                    minLeaveTime = timeToLeave;
//                }
//            }
        }
        
        Vector2D[] bPoints = b.polygon.getPoints();
        double[] aMins = polygon.getNormalMins();
        double[] aMaxs = polygon.getNormalMaxs();
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
//            double centerA = polygon.getCenter().unitScalarProject(normal);
//            double centerB = b.polygon.getCenter().unitScalarProject(normal);
//            bMin += centerB;
//            bMax += centerB;
//            double aMin = aMins[i] + centerA;
//            double aMax = aMaxs[i] + centerA;
//            double projVel = -combinedVelocity.unitScalarProject(normal);
//            if(bMax <= aMin){
//                if(projVel < 0){
//                    double timeToOverlap = (bMax - aMin) / projVel;
//                    if(timeToOverlap > maxEntryTime){
//                        maxEntryTime = timeToOverlap;
//                        collisionNormal = normal;
//                    }
//                }else{
//                    // not travelling towards each other
//                    //TODO should have an early return here
//                    maxEntryTime = Collider.NO_COLLISION;
//                }
//            }else if(aMax <= bMin){
//                if(projVel > 0){
//                    double timeToOverlap = (bMin - aMax) / projVel;
//                    if(timeToOverlap > maxEntryTime){
//                        maxEntryTime = timeToOverlap;
//                        collisionNormal = normal;
//                    }
//                }else{
//                    // not travelling towards each other
//                    //TODO should have an early return here
//                    maxEntryTime = Collider.NO_COLLISION;
//                }
//            }
            
//            if(aMax > bMin && projVel < 0){
//                double timeToLeave = (bMin - aMax) / projVel;
//                if(timeToLeave < minLeaveTime){
//                    minLeaveTime = timeToLeave;
//                }
//            }else if(bMax > aMin && projVel > 0){
//                double timeToLeave = (bMax - aMin) / projVel;
//                if(timeToLeave < minLeaveTime){
//                    minLeaveTime = timeToLeave;
//                }
//            }
        }
        double velProj = combinedVelocity.unitScalarProject(collisionNormal);
        if(maxEntryTime == -Double.MAX_VALUE || maxEntryTime > minLeaveTime){
            maxEntryTime = Shape.NO_COLLISION;
        }
//        result.set(maxEntryTime, collisionNormal, b, this);
    }
}
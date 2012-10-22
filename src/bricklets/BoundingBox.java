package bricklets;

import gameengine.Context;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 *
 * @author david
 */
public class BoundingBox extends Collidable {
    private double halfWidth, halfHeight;
    private Polygon polygon;
    private String debugString = "";
    
    public BoundingBox(Context context, double x, double y, double width, double height){
        this(context, x, y, width, height, false, false);
    }
    
    public BoundingBox(Context context, double x, double y, double width, double height, boolean isFixated, boolean useRandomShape){
        super(context, new Vector2D(x, y), new Vector2D(), new Vector2D(), 1, width, height, new Color((int)(Math.random() * 255), (int)(Math.random() * 255), (int)(Math.random() * 255)));
        halfWidth = width / 2;
        halfHeight = height / 2;
        if(!useRandomShape){
            double[] xPoints = {x - halfWidth, x - halfWidth, x + halfWidth, x + halfWidth};
            double[] yPoints = {y - halfHeight, y + halfHeight, y + halfHeight, y - halfHeight};
//            polygon = new Polygon(xPoints, yPoints);
        }else{
//            polygon = Polygon.getRandomConvexPolygon(10, 30, 3, 9, 0);
            polygon.updatePosition(x, y);
        }
        mass = 0.1;
        this.isFixated = isFixated;
    }
    
    public void setMass(double mass){
        this.mass = mass;
    }

    @Override
    public Polygon getPolygon() {
        return polygon;
    }

    @Override
    public boolean isCircular() {
        return false;
    }

    @Override
    public boolean isPolygonal() {
        return true;
    }
    
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
        debugString = maxEntryTime + " " + minLeaveTime;
        double velProj = combinedVelocity.unitScalarProject(collisionNormal);
        if(maxEntryTime == -Double.MAX_VALUE || maxEntryTime > minLeaveTime){
            maxEntryTime = Collider.NO_COLLISION;
        }
//        result.set(maxEntryTime, collisionNormal, b, this);
    }
    
    @Override
    public void update(double elapsedTime) {
        position.add(new Vector2D(velocity).scale(elapsedTime));
        polygon.updatePosition(position.getX(), position.getY());
        if(!isFixated){
            acceleration.add(0, g * elapsedTime);
        }
        velocity.add(acceleration.getX() * elapsedTime, acceleration.getY() * elapsedTime);
        acceleration.clear();
    }

    @Override
    public void draw(Graphics2D g) {
//        g.setColor(color);
//        g.fillRect((int)(position.getX() - halfWidth), (int)(position.getY() - halfHeight), (int)width, (int)height);
        polygon.setColor(color);
        polygon.draw(g);
        
//        int scale = 100;
//        int vX = (int)(velocity.getX() * scale + position.getX());
//        int vY = (int)(velocity.getY() * scale + position.getY());
//        int centerX = (int)(position.getX());
//        int centerY = (int)(position.getY());
//        g.setColor(Color.CYAN);
//        g.drawLine(centerX, centerY, vX, vY);
        
//        scale = 50;
//        vX = (int)(debugVector.getX() * scale + position.getX());
//        vY = (int)(debugVector.getY() * scale + position.getY());
//        centerX = (int)(position.getX());
//        centerY = (int)(position.getY());
//        g.setColor(Color.ORANGE);
//        g.drawLine(centerX, centerY, vX, vY);
//        g.setColor(Color.GREEN);
//        g.drawString(debugString, centerX, centerY);
//        color = Color.WHITE;
    }
    
}
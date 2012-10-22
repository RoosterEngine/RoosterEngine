package bricklets;

import java.awt.Color;

/**
 *
 * @author davidrusu
 */
public class Physics {

    private static Vector2D unitY = new Vector2D();
    private static Vector2D unitX = new Vector2D();
    private static Vector2D normal = new Vector2D();
    
    private Physics(){
    }
    
    public static double springForce(Vector2D a, Vector2D b, double k, double restingLength){
        double displacement = new Vector2D(a).subtract(b).length() - restingLength;
        return displacement * k;
    }
    
    public static void performElasiticCollision(Collidable a, Collidable b, double collisionsPerMilli, Vector2D collisionNormal){
        performCollision(a, b, 1, collisionsPerMilli, collisionNormal);
    }
    
    private static void performCircleCircleCollision(Collidable a, Collidable b, double restitution, double collisionRate){
        unitY.set(a.getX() - b.getX(), a.getY() - b.getY()).unit();
        performCollisionAlongNormal(a, b, restitution, collisionRate, unitY);
    }
    
    private static void performPolyPolyCollision(Collidable a, Collidable b, double restitution, double collisionRate, Vector2D collisionNormal){
        performCollisionAlongNormal(b, a, restitution, collisionRate, collisionNormal);
    }
    
    private static void performCollisionAlongNormal(Collidable a, Collidable b, double restitution, double collisionRate, Vector2D normalUnit){
        unitY.set(normalUnit);
        unitX.set(-unitY.getY(), unitY.getX());
        double xLengthA = a.getVelocity().unitScalarProject(unitX);
        double yLengthA = a.getVelocity().unitScalarProject(unitY);
        double xLengthB = b.getVelocity().unitScalarProject(unitX);
        double yLengthB = b.getVelocity().unitScalarProject(unitY);
        
        double velDiff = yLengthB - yLengthA;
//        double collisionsThresh = 0.1;
//        if(collisionRate > collisionsThresh && Math.abs(velDiff) < 0.3){
//            restitution *= 1 + collisionRate / 2;
//        }
        double combinedMomentum = yLengthB * b.getMass() + yLengthA * a.getMass();
        double combinedMass = b.getMass() + a.getMass();
        double yFinalA = (restitution * b.getMass() * velDiff + combinedMomentum) / combinedMass;
        double yFinalB = (combinedMomentum - restitution * a.getMass() * velDiff) / combinedMass;
        
        double xA = unitY.getX() * yFinalA + unitX.getX() * xLengthA;
        double yA = unitY.getY() * yFinalA + unitX.getY() * xLengthA;
        double xB = unitY.getX() * yFinalB + unitX.getX() * xLengthB;
        double yB = unitY.getY() * yFinalB + unitX.getY() * xLengthB;
        
        a.setVelocity(xA, yA);
        b.setVelocity(xB, yB);
        
    }
    
    public static void performCollision(Collidable a, Collidable b, double restitution, double collisionRate, Vector2D collisionNormal){
        if(a.isCircular() && b.isCircular()){
            performCircleCircleCollision(a, b, restitution, collisionRate);
        }else if(a.isPolygonal() && b.isPolygonal()){
            
            performPolyPolyCollision(a, b, restitution, collisionRate, collisionNormal);
        }
    }
}

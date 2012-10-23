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
    
    public static void performCollision(Collision collision, double restitution, double collisionRate){
        unitY.set(collision.getCollisionNormal());
        unitX.set(-unitY.getY(), unitY.getX());
        Entity a = collision.getA().getParentEntity();
        Entity b = collision.getB().getParentEntity();
        
        double xLengthA = Vector2D.unitScalarProject(a.getDX(), a.getDY(), unitX);
        double yLengthA = Vector2D.unitScalarProject(a.getDX(), a.getDY(), unitY);
        double xLengthB = Vector2D.unitScalarProject(b.getDX(), b.getDY(), unitX);
        double yLengthB = Vector2D.unitScalarProject(b.getDX(), b.getDY(), unitY);
        
        double velDiff = yLengthB - yLengthA;
        double collisionsThresh = 0.1;
        if(collisionRate > collisionsThresh && Math.abs(velDiff) < 0.3){
            restitution *= 1 + collisionRate / 2;
        }
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
}
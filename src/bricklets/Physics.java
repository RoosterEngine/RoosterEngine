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
    
    public static double getCriticallyDampedSpringConstant(double k){
        return 2 * Math.sqrt(k);
    }
    
    public static double springForce(double x, double y, double restingX, double restingY, double k, double restingLength, double dampning){
        double dx = restingX - x;
        double dy = restingY - y;
        double displacement = Math.sqrt(dx * dx + dy * dy) - restingLength;
        return displacement * k;
    }
    
    public static double springForce(Vector2D a, Vector2D b, double k, double restingLength){
        double displacement = new Vector2D(a).subtract(b).length() - restingLength;
        return displacement * k;
    }

//    public static void performCollision(Collision collision, double collisionRate){
//        Entity a = collision.getA().getParentEntity();
//        Entity b = collision.getB().getParentEntity();
//        performCollision(collision, Math.sqrt(a.getFriction() + b.getFriction()), Math.sqrt(a.getRestitution() + b.getRestitution()), collisionRate);
//    }
    
    public static void performCollision(Collision collision, double friction, double restitution, double collisionRate){
        unitY.set(collision.getCollisionNormal());
        unitX.set(-unitY.getY(), unitY.getX());
        Entity a = collision.getA().getParentEntity();
        Entity b = collision.getB().getParentEntity();
        
        double xLengthA = Vector2D.unitScalarProject(a.getDX(), a.getDY(), unitX);
        double yLengthA = Vector2D.unitScalarProject(a.getDX(), a.getDY(), unitY);
        double xLengthB = Vector2D.unitScalarProject(b.getDX(), b.getDY(), unitX);
        double yLengthB = Vector2D.unitScalarProject(b.getDX(), b.getDY(), unitY);
        
        double yVelDiff = yLengthB - yLengthA;
        double xVelDiff = xLengthB - xLengthA;

//        double collisionsThresh = 0.1;
//        if(collisionRate > collisionsThresh && Math.abs(velDiff) < 0.3){
//            restitution *= 1 + collisionRate / 2;
//        }
        double combinedMomentum = yLengthB * b.getMass() + yLengthA * a.getMass();
        double combinedMass = b.getMass() + a.getMass();
        double yFinalA = (restitution * b.getMass() * yVelDiff + combinedMomentum) / combinedMass;
        double yFinalB = (combinedMomentum - restitution * a.getMass() * yVelDiff) / combinedMass;
        double frictionA = friction * Math.abs(yFinalA - yLengthA);
        double frictionB = friction * Math.abs(yFinalB - yLengthB);
//        xLengthA += friction * 5 * (yFinalA - yLengthA);// * b.getMass() / combinedMass;
//        xLengthB -= friction * 5 * (yFinalB - yLengthB);// * a.getMass() / combinedMass;
        if(xVelDiff < 0){
            if(frictionA > xLengthA){
                xLengthA = 0;
            }else{
                xLengthA -= frictionA;
            }
            if(frictionB > xLengthB){
                xLengthB = 0;
            }else{
                xLengthB -= frictionB;
            }
        } else if(xVelDiff > 0){
            if(frictionA < -xLengthA){
                xLengthA = 0;
            }else{
                xLengthA += frictionA;
            }
            if(frictionB < -xLengthB){
                xLengthB = 0;
            }else{
                xLengthB += frictionB;
            }
        }

        double xA = unitY.getX() * yFinalA + unitX.getX() * xLengthA;
        double yA = unitY.getY() * yFinalA + unitX.getY() * xLengthA;
        double xB = unitY.getX() * yFinalB + unitX.getX() * xLengthB;
        double yB = unitY.getY() * yFinalB + unitX.getY() * xLengthB;
        
        a.setVelocity(xA, yA);
        b.setVelocity(xB, yB);
        
    }
}
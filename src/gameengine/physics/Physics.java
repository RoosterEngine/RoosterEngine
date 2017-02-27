package gameengine.physics;

import gameengine.collisiondetection.Collision;
import gameengine.entities.Entity;
import gameengine.geometry.Vector2D;

/**
 * @author davidrusu
 */
public class Physics {

    private static Vector2D unitY = new Vector2D();
    private static Vector2D unitX = new Vector2D();

    private Physics() {
    }

    public static double getCriticallyDampedSpringConstant(double k, double mass) {
        return 2 * Math.sqrt(k / mass);
    }

    public static double springForce(Vector2D a, Vector2D b, double k, double restingLength) {
        double dx = a.getX() - b.getX();
        double dy = a.getY() - b.getY();
        double length = Math.sqrt(dx * dx + dy * dy);
        double displacement = length - restingLength;
        return displacement * k;
    }

    public static void performCollision(Collision collision) {
        unitY.set(collision.getCollisionNormal());
        unitX.set(-unitY.getY(), unitY.getX());
        Entity a = collision.getA();
        Entity b = collision.getB();
        double friction = Material.getFriction(a.getMaterial(), b.getMaterial());
        double restitution = Material.getRestitution(a.getMaterial(), b.getMaterial());

        double xLengthA = Vector2D.unitScalarProject(a.getDX(), a.getDY(), unitX);
        double yLengthA = Vector2D.unitScalarProject(a.getDX(), a.getDY(), unitY);
        double xLengthB = Vector2D.unitScalarProject(b.getDX(), b.getDY(), unitX);
        double yLengthB = Vector2D.unitScalarProject(b.getDX(), b.getDY(), unitY);

        double aMass = a.getMass();
        double bMass = b.getMass();
        boolean isMassAInfinite = aMass == Double.POSITIVE_INFINITY;
        boolean isMassBInfinite = bMass == Double.POSITIVE_INFINITY;
        if (isMassAInfinite && isMassBInfinite) {
            a.setVelocity(unitX.getX() * xLengthA, unitX.getY() * xLengthA);
            b.setVelocity(unitX.getX() * xLengthB, unitX.getY() * xLengthB);
            return;
        } else if (isMassAInfinite) {
            performInfiniteMassCollision(b, friction, restitution, unitX, unitY, yLengthA,
                    xLengthB, yLengthB);
            return;
        } else if (isMassBInfinite) {
            performInfiniteMassCollision(a, friction, restitution, unitX, unitY, yLengthB,
                    xLengthA, yLengthA);
            return;
        }

        double yVelDiff = yLengthB - yLengthA;

//        double collisionsThresh = 0.1;
//        if(collisionRate > collisionsThresh && Math.abs(velDiff) < 0.3){
//            restitution *= 1 + collisionRate / 2;
//        }
        double combinedMomentum = yLengthB * bMass + yLengthA * aMass;
        double combinedMass = bMass + aMass;
        double yFinalA = (restitution * bMass * yVelDiff + combinedMomentum) / combinedMass;
        double yFinalB = (combinedMomentum - restitution * aMass * yVelDiff) / combinedMass;

        double impulse = (yFinalA - yLengthA) * aMass;
        double xVelDiff = xLengthB - xLengthA;
        double frictionImpulse = Math.min(friction * Math.abs(impulse), Math.abs(xVelDiff) *
                aMass * bMass / combinedMass);
        double frictionDirection = Math.signum(xVelDiff);

        double xFinalA = xLengthA + frictionDirection * frictionImpulse / aMass;
        double xFinalB = xLengthB - frictionDirection * frictionImpulse / bMass;


        double xA = unitY.getX() * yFinalA + unitX.getX() * xFinalA;
        double yA = unitY.getY() * yFinalA + unitX.getY() * xFinalA;
        double xB = unitY.getX() * yFinalB + unitX.getX() * xFinalB;
        double yB = unitY.getY() * yFinalB + unitX.getY() * xFinalB;

        a.setVelocity(xA, yA);
        b.setVelocity(xB, yB);
    }

    private static void performInfiniteMassCollision(Entity b, double friction, double
            restitution, Vector2D unitX, Vector2D unitY, double yLengthA, double xLengthB, double
            yLengthB) {
        double yFinalB = yLengthA * (1 + restitution) - restitution * yLengthB;
        double xB = unitY.getX() * yFinalB + unitX.getX() * xLengthB;
        double yB = unitY.getY() * yFinalB + unitX.getY() * xLengthB;
        b.setVelocity(xB, yB);
    }
}

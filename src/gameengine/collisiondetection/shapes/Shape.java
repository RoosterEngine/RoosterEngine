package gameengine.collisiondetection.shapes;

import gameengine.collisiondetection.Collision;
import gameengine.entities.Entity;
import gameengine.math.Vector2D;
import gameengine.physics.Material;

import java.awt.*;

/**
 * @author david
 */
public abstract class Shape {
    public static final int TYPE_CIRCLE = 0, TYPE_AA_BOUNDING_BOX = 1, TYPE_POLYGON = 2;
    public static final double NO_COLLISION = Double.MAX_VALUE;
    private static CollisionData collisionData = new CollisionData();
    protected double x, y, dx, dy, radius, parentOffsetX, parentOffsetY;
    protected Entity parentEntity;
    protected Material material;

    public Shape(double x, double y, double radius, Entity parentEntity, Material material) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.parentEntity = parentEntity;
        parentOffsetX = parentEntity.getX() - x;
        parentOffsetY = parentEntity.getY() - y;
        this.material = material;
    }

    public Material getMaterial() {
        return material;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getDX() {
        return dx;
    }

    public double getDY() {
        return dy;
    }

    public double getRadius() {
        return radius;
    }

    public Entity getParentEntity() {
        return parentEntity;
    }

    public void update() {
        x = parentEntity.getX() + parentOffsetX;
        y = parentEntity.getY() + parentOffsetY;
        dx = parentEntity.getDX();
        dy = parentEntity.getDY();
    }

    public static void collidePolyPoly(gameengine.collisiondetection.shapes.Polygon a, gameengine.collisiondetection.shapes.Polygon b, double maxTime, Collision result) {
        if (!willBoundingCollide(a, b, maxTime)) {
            result.setNoCollision();
            return;
        }
        collisionData.clear();
        getEntryLeaveAndOverlapTime(a, b, collisionData);
        if (collisionData.isCollisionNotPossible()) {
            result.setNoCollision();
            return;
        }
        getEntryLeaveAndOverlapTime(b, a, collisionData);
        if (collisionData.isCollisionNotPossible()) {
            result.setNoCollision();
            return;
        }
        if (collisionData.isIntersectingAndTravellingTowardsEachOther()) {
            result.set(0, collisionData.getOverlapNormal(), a, b);
            return;
        }
        if (collisionData.willCollisionHappen(maxTime)) {
            result.set(collisionData.getEntryTime(), collisionData.getCollisionNormal(), a, b);
            return;
        }
        result.setNoCollision();
    }

    public static void collideAABBPoly(AABBShape a, gameengine.collisiondetection.shapes.Polygon b, double maxTime, Collision result) {
        collisionData.clear();
        double relVelX = a.dx - b.dx;
        double relVelY = a.dy - b.dy;

        double minX = a.x - a.getHalfWidth() - b.x, maxX = a.x + a.getHalfWidth() - b.x;
        double minY = a.y - a.getHalfHeight() - b.y, maxY = a.y + a.getHalfHeight() - b.y;
        for (int i = 0; i < b.getNumPoints(); i++) {
            Vector2D normal = b.getNormals()[i];
            double bMin = b.getNormalMins()[i];
            double bMax = b.getNormalMaxs()[i];
            double projVel = Vector2D.unitScalarProject(relVelX, relVelY, normal);
            calcTOIUsingPolyNormalAndSetMinMaxValues
                    (minX, maxX, minY, maxY, normal, bMin, bMax, projVel, collisionData);
            if (collisionData.isCollisionNotPossible()) {
                result.setNoCollision();
                return;
            }
            double dist = bMax - collisionData.getMin();
            collisionData.updateTempOverlapData(dist, projVel, normal.getX(), normal.getY());
        }

        calcCollisionWithBoxNormals(a, b, relVelX, relVelY, collisionData);
        if (collisionData.isCollisionNotPossible()) {
            result.setNoCollision();
            return;
        }
        collisionData.updateOverlapData();
        if (collisionData.isIntersectingAndTravellingTowardsEachOther()) {
            result.set(0, collisionData.getOverlapNormal(), a, b);
        } else if (collisionData.willCollisionHappen(maxTime)) {
            result.set(collisionData.getEntryTime(), collisionData.getCollisionNormal(), a, b);
        } else {
            result.setNoCollision();
        }
    }

    private static void calcTOIUsingPolyNormalAndSetMinMaxValues(double minX, double maxX, double minY, double maxY,
                                                                 Vector2D normal, double bMin, double bMax,
                                                                 double projVel, CollisionData collisionData) {
        collisionData.clearMinMax();
        double projDist = Vector2D.unitScalarProject(minX, minY, normal);
        collisionData.updateMinMax(projDist);
        projDist = Vector2D.unitScalarProject(maxX, minY, normal);
        collisionData.updateMinMax(projDist);
        projDist = Vector2D.unitScalarProject(maxX, maxY, normal);
        collisionData.updateMinMax(projDist);
        projDist = Vector2D.unitScalarProject(minX, maxY, normal);
        collisionData.updateMinMax(projDist);
        calcTOIAlongAxis(collisionData.getMin(), collisionData.getMax(), bMin, bMax, projVel, collisionData, normal);
    }

    private static void calcCollisionWithBoxNormals(AABBShape a, gameengine.collisiondetection.shapes.Polygon b,
                                                    double relVelX, double relVelY, CollisionData collisionData) {
        double bMinX = Double.MAX_VALUE;
        double bMaxX = -Double.MAX_VALUE;
        double bMinY = Double.MAX_VALUE;
        double bMaxY = -Double.MAX_VALUE;
        for (int i = 0; i < b.getNumPoints(); i++) {
            Vector2D point = b.getPoints()[i];
            double x = point.getX() + b.x - a.x;
            bMinX = Math.min(bMinX, x);
            bMaxX = Math.max(bMaxX, x);
            double y = point.getY() + b.y - a.y;
            bMinY = Math.min(bMinY, y);
            bMaxY = Math.max(bMaxY, y);
        }
        double minX = -a.getHalfWidth();
        double maxX = a.getHalfWidth();
        double minY = -a.getHalfHeight();
        double maxY = a.getHalfHeight();
        calcTOIAlongAxis(minX, maxX, bMinX, bMaxX, relVelX, collisionData, 1, 0);
        if (collisionData.isCollisionNotPossible()) {
            return;
        }
        calcTOIAlongAxis(minY, maxY, bMinY, bMaxY, relVelY, collisionData, 0, 1);
        if (collisionData.isCollisionNotPossible()) {
            return;
        }
        collisionData.updateTempOverlapData(bMaxX - minX, relVelX, -1, 0);
        collisionData.updateTempOverlapData(maxX - bMinX, -relVelX, 1, 0);
        collisionData.updateTempOverlapData(bMaxY - minY, relVelY, 0, -1);
        collisionData.updateTempOverlapData(maxY - bMinY, -relVelY, 0, 1);
    }

    public static void collideCirclePoly(Shape a, gameengine.collisiondetection.shapes.Polygon b, double maxTime, Collision result) {
        collisionData.clear();
        checkCollisionWithLines(a, b, collisionData);
        if (collisionData.isCollisionNotPossible()) {
            result.setNoCollision();
            return;
        }
        checkCollisionWithPoints(a, b, collisionData);
        if (collisionData.isCollisionNotPossible()) {
            result.setNoCollision();
            return;
        }
        collisionData.updateOverlapData();
        if (collisionData.isIntersectingAndTravellingTowardsEachOther()) {
            result.set(0, collisionData.getOverlapNormal(), a, b);
            return;
        }
        if (collisionData.willCollisionHappen(maxTime)) {
            result.set(collisionData.getEntryTime(), collisionData.getCollisionNormal(), a, b);
        } else {
            result.setNoCollision();
        }
    }

    private static void checkCollisionWithLines(Shape a, gameengine.collisiondetection.shapes.Polygon b, CollisionData collisionData) {
        double relVelX = a.dx - b.dx;
        double relVelY = a.dy - b.dy;
        for (int i = 0; i < b.getNumPoints(); i++) {
            Vector2D normal = b.getNormals()[i];
            double aPos = Vector2D.unitScalarProject(a.x - b.x, a.y - b.y, normal);
            double aMin = aPos - a.radius;
            double aMax = aPos + a.radius;
            double bMin = b.getNormalMins()[i];
            double bMax = b.getNormalMaxs()[i];
            double projVel = Vector2D.unitScalarProject(relVelX, relVelY, normal);
            calcTOIAlongAxis(aMin, aMax, bMin, bMax, projVel, collisionData, normal);
            if (collisionData.isCollisionNotPossible()) {
                return;
            }
            double dist = bMax - aMin;
            collisionData.updateTempOverlapData(dist, projVel, normal.getX(), normal.getY());
        }
    }

    private static void checkCollisionWithPoints(Shape a, gameengine.collisiondetection.shapes.Polygon b, CollisionData collisionData) {
        double relVelX = a.dx - b.dx;
        double relVelY = a.dy - b.dy;
        double relX = b.x - a.x;
        double relY = b.y - a.y;
        for (Vector2D vertex : b.getPoints()) {
            double dx = vertex.getX() + relX;
            double dy = vertex.getY() + relY;
            double dist = Math.sqrt(dx * dx + dy * dy);
            dx /= dist;
            dy /= dist;
            double bMin = Double.MAX_VALUE;
            double bMax = -Double.MAX_VALUE;
            for (Vector2D point : b.getPoints()) {
                double projDist = Vector2D.unitScalarProject(point.getX() + relX, point.getY() + relY, dx, dy);
                bMin = Math.min(bMin, projDist);
                bMax = Math.max(bMax, projDist);
            }
            double projVel = Vector2D.unitScalarProject(relVelX, relVelY, dx, dy);
            calcTOIAlongAxis(-a.radius, a.radius, bMin, bMax, projVel, collisionData, dx, dy);
            if (collisionData.isCollisionNotPossible()) {
                return;
            }
        }
    }

    public static void collideCircleCircle(Shape a, Shape b, double maxTime, Collision result) {
        double combinedVelX = b.dx - a.dx;
        double combinedVelY = b.dy - a.dy;
        if (combinedVelX == 0 && combinedVelY == 0) {
            result.setNoCollision();
            return;
        }
        double distToLineSquared =
                Vector2D.distToLineSquared(a.x, a.y, b.x, b.y, b.x + combinedVelX, b.y + combinedVelY);
        double radiiSum = a.radius + b.radius;
        double radiiSumSquared = radiiSum * radiiSum;
        if (distToLineSquared > radiiSumSquared) {
            result.setNoCollision();
            return;
        }
        // using the collision normal as a scratch pad
        Vector2D velocity = result.getCollisionNormal().set(combinedVelX, combinedVelY);
        double deltaX = a.x - b.x;
        double deltaY = a.y - b.y;
        double distBetween = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        double projVelocity = velocity.scalarProject(deltaX, deltaY, distBetween);
        if (projVelocity <= 0) {
            // not travelling towards each other
            result.setNoCollision();
            return;
        }
        double travelDist = distBetween - radiiSum;
        double travelTime = travelDist / projVelocity;
        if (travelTime > maxTime) {
            result.setNoCollision();
            return;
        }
        if (travelTime < 0) {
            // overlapping
            velocity.set(deltaX / distBetween, deltaY / distBetween);
            result.set(0, velocity, a, b);
            return;
        }
        velocity.unit();
        double centerAProjectedOnVelocity = Vector2D.unitScalarProject(a.x - b.x, a.y - b.y, velocity);
        double subLength = Math.sqrt(radiiSumSquared - distToLineSquared); // a = sqrt(c^2 - b^2) pythagoras
        velocity.scale(centerAProjectedOnVelocity - subLength);
        velocity.add(b.x - a.x, b.y - a.y); // this is now the collision normal
        velocity.divide(radiiSum); // normalizes the vector
        result.set(travelTime, velocity, a, b);
    }

    public static void collideAABBAABB(AABBShape a, AABBShape b, double maxTime, Collision result) {
        collisionData.clear();

        double relVelX = a.dx - b.dx, relVelY = a.dy - b.dy;

        double aMaxX = a.getHalfWidth();
        double aMinX = -a.getHalfWidth();
        double aMaxY = a.getHalfHeight();
        double aMinY = -a.getHalfHeight();

        double bMaxX = b.x + b.getHalfWidth() - a.x;
        double bMinX = b.x - b.getHalfWidth() - a.x;
        double bMaxY = b.y + b.getHalfHeight() - a.y;
        double bMinY = b.y - b.getHalfHeight() - a.y;

        calcTOIAlongAxis(aMinX, aMaxX, bMinX, bMaxX, relVelX, collisionData, 1, 0);
        if (collisionData.isCollisionNotPossible()) {
            result.setNoCollision();
            return;
        }

        calcTOIAlongAxis(aMinY, aMaxY, bMinY, bMaxY, relVelY, collisionData, 0, 1);
        if (collisionData.isCollisionNotPossible()) {
            result.setNoCollision();
            return;
        }

        if (collisionData.hasEntryTimeNotBeenUpdated()) {
            calcOverlapNormalBox(
                    aMinX, aMaxX, aMinY, aMaxY, bMinX, bMaxX, bMinY, bMaxY, relVelX, relVelY, collisionData);
            if (collisionData.getOverlapVelocity() < 0) {
                result.set(0, collisionData.getOverlapNormal(), a, b);
                return;
            }
        }

        if (collisionData.willCollisionHappen(maxTime)) {
            result.set(collisionData.getEntryTime(), collisionData.getCollisionNormal(), a, b);
        } else {
            result.setNoCollision();
        }
    }

    public static void collideCircleAABB(Shape a, AABBShape b, double maxTime, Collision result) {
        collisionData.clear();
        double relVelX = a.dx - b.dx;
        double relVelY = a.dy - b.dy;
        double bMaxX = b.x + b.getHalfWidth();
        double bMinX = b.x - b.getHalfWidth();
        double bMaxY = b.y + b.getHalfHeight();
        double bMinY = b.y - b.getHalfHeight();
        calcCircleBoxTOIBeforeCheckingPoints(a, bMinX, bMaxX, bMinY, bMaxY, relVelX, relVelY, collisionData);
        if (collisionData.isCollisionNotPossible()) {
            result.setNoCollision();
            return;
        }
        calculateCircleBoxPointsTOI(a, bMinX, bMaxX, bMinY, bMaxY, relVelX, relVelY, collisionData);
        if (collisionData.isCollisionNotPossible()) {
            result.setNoCollision();
            return;
        }
        if (collisionData.hasEntryTimeNotBeenUpdated()) {
            calcOverlapNormalBox(-a.radius, a.radius, -a.radius, a.radius, bMinX - a.x, bMaxX - a.x,
                    bMinY - a.y, bMaxY - a.y, relVelX, relVelY, collisionData);
            if (collisionData.getOverlapVelocity() < 0) {
                result.set(0, collisionData.getOverlapNormal(), a, b);
                return;
            }
        }
        if (collisionData.willCollisionHappen(maxTime)) {
            result.set(collisionData.getEntryTime(), collisionData.getCollisionNormal(), a, b);
            return;
        }
        result.setNoCollision();
    }

    private static void getEntryLeaveAndOverlapTime(gameengine.collisiondetection.shapes.Polygon a, gameengine.collisiondetection.shapes.Polygon b, CollisionData collisionData) {
        collisionData.setTempOverlapVelocity(collisionData.getOverlapVelocity());
        collisionData.resetOverlapUpdated();
        double relVelX = a.dx - b.dx;
        double relVelY = a.dy - b.dy;
        for (int i = 0; i < a.getNumPoints(); i++) {
            Vector2D normal = a.getNormals()[i];
            collisionData.clearMinMax();
            for (Vector2D point : b.getPoints()) {
                double dist = Vector2D.unitScalarProject(point.getX() + b.x - a.x, point.getY() + b.y - a.y, normal);
                collisionData.updateMinMax(dist);
            }
            double aMin = a.getNormalMins()[i];
            double aMax = a.getNormalMaxs()[i];
            double projVel = Vector2D.unitScalarProject(relVelX, relVelY, normal);
            calcTOIAlongAxis(
                    aMin, aMax, collisionData.getMin(), collisionData.getMax(), projVel, collisionData, normal);
            if (collisionData.isCollisionNotPossible()) {
                return;
            }
            double dist = collisionData.getMax() - aMin;
            collisionData.updateTempOverlapData(dist, projVel, normal.getX(), normal.getY());
        }
        collisionData.updateOverlapData();
    }

    private static double getLeaveTimeAlongAxis(double aMin, double aMax, double bMin, double bMax, double vel) {
        double leaveTime = Double.MAX_VALUE;
        if (vel > 0) {
            leaveTime = (bMax - aMin) / vel;
        } else if (vel < 0) {
            leaveTime = (bMin - aMax) / vel;
        }
        return leaveTime;
    }

    private static void calcTOIAlongAxis(double aMin, double aMax, double bMin, double bMax, double vel,
                                         CollisionData collisionData, Vector2D axis) {
        calcTOIAlongAxis(aMin, aMax, bMin, bMax, vel, collisionData, axis.getX(), axis.getY());
    }

    private static void calcTOIAlongAxis(double aMin, double aMax, double bMin, double bMax, double vel,
                                         CollisionData collisionData, double axisX, double axisY) {
        double travelTime = -Double.MAX_VALUE;
        if (aMax <= bMin) {
            if (vel <= 0) {
                collisionData.setNoCollision();
                return;
            }
            travelTime = (bMin - aMax) / vel;
        } else if (aMin >= bMax) {
            if (vel >= 0) {
                collisionData.setNoCollision();
                return;
            }
            travelTime = (bMax - aMin) / vel;
        }
        collisionData.updateEntryTime(travelTime, axisX, axisY);
        collisionData.updateLeaveTime(getLeaveTimeAlongAxis(aMin, aMax, bMin, bMax, vel));
    }

    private static void calcOverlapNormalBox(double aMinX, double aMaxX, double aMinY, double aMaxY,
                                             double bMinX, double bMaxX, double bMinY, double bMaxY,
                                             double velX, double velY, CollisionData collisionData) {
        double dist = aMaxX - bMinX;
        collisionData.updateTempOverlapData(dist, -velX, -1, 0);
        dist = bMaxX - aMinX;
        collisionData.updateTempOverlapData(dist, velX, 1, 0);
        dist = aMaxY - bMinY;
        collisionData.updateTempOverlapData(dist, -velY, 0, -1);
        dist = bMaxY - aMinY;
        collisionData.updateTempOverlapData(dist, velY, 0, 1);
        collisionData.updateOverlapData();
    }

    private static void calcCircleBoxTOIBeforeCheckingPoints(Shape a, double minX, double maxX,
                                                             double minY, double maxY, double velX, double velY,
                                                             CollisionData collisionData) {
        calcTOIAlongAxis(-a.radius, a.radius, minX - a.x, maxX - a.x, velX, collisionData, 1, 0);
        if (collisionData.isCollisionNotPossible()) {
            return;
        }
        calcTOIAlongAxis(-a.radius, a.radius, minY - a.y, maxY - a.y, velY, collisionData, 0, 1);
    }

    private static void calculateCircleBoxPointsTOI(Shape a, double bMinX, double bMaxX, double bMinY, double bMaxY,
                                                    double relVelX, double relVelY, CollisionData collisionData) {
        calculateCirclePointTOI(a, bMinX, bMaxX, bMinY, bMaxY, bMinX, bMinY, relVelX, relVelY, collisionData);
        if (collisionData.isCollisionNotPossible()) {
            return;
        }
        calculateCirclePointTOI(a, bMinX, bMaxX, bMinY, bMaxY, bMaxX, bMinY, relVelX, relVelY, collisionData);
        if (collisionData.isCollisionNotPossible()) {
            return;
        }
        calculateCirclePointTOI(a, bMinX, bMaxX, bMinY, bMaxY, bMaxX, bMaxY, relVelX, relVelY, collisionData);
        if (collisionData.isCollisionNotPossible()) {
            return;
        }
        calculateCirclePointTOI(a, bMinX, bMaxX, bMinY, bMaxY, bMinX, bMaxY, relVelX, relVelY, collisionData);
    }

    private static void calculateCirclePointTOI(Shape a, double bMinX, double bMaxX, double bMinY, double bMaxY,
                                                double x, double y, double relVelX, double relVelY,
                                                CollisionData collisionData) {
        double dx = x - a.x;
        double dy = y - a.y;
        double dist = Math.sqrt(dx * dx + dy * dy);
        dx /= dist;
        dy /= dist;
        collisionData.clearMinMax();
        calculateCircleAABBMinAndMax(a, bMinX, bMaxX, bMinY, bMaxY, dx, dy, collisionData);
        double projVel = Vector2D.unitScalarProject(relVelX, relVelY, dx, dy);
        calcTOIAlongAxis(-a.radius, a.radius,
                collisionData.getMin(), collisionData.getMax(), projVel, collisionData, dx, dy);
    }

    private static void calculateCircleAABBMinAndMax(Shape a, double bMinX, double bMaxX, double bMinY, double bMaxY,
                                                     double normalX, double normalY, CollisionData collisionData) {
        double projDist = Vector2D.unitScalarProject(bMinX - a.x, bMinY - a.y, normalX, normalY);
        collisionData.updateMinMax(projDist);

        projDist = Vector2D.unitScalarProject(bMaxX - a.x, bMinY - a.y, normalX, normalY);
        collisionData.updateMinMax(projDist);

        projDist = Vector2D.unitScalarProject(bMaxX - a.x, bMaxY - a.y, normalX, normalY);
        collisionData.updateMinMax(projDist);

        projDist = Vector2D.unitScalarProject(bMinX - a.x, bMaxY - a.y, normalX, normalY);
        collisionData.updateMinMax(projDist);
    }

    private static boolean willBoundingCollide(gameengine.collisiondetection.shapes.Polygon a, gameengine.collisiondetection.shapes.Polygon b, double maxTime) {
        if (!a.isUsingBoundingBox() && !b.isUsingBoundingBox()) {
            return willBoundingCircleCircleCollide(a, b, maxTime);
        } else if (!a.isUsingBoundingBox() && b.isUsingBoundingBox()) {
            return willBoundingCircleBoxCollide(a, b, maxTime);
        } else if (a.isUsingBoundingBox() && !b.isUsingBoundingBox()) {
            return willBoundingCircleBoxCollide(b, a, maxTime);
        } else if (a.isUsingBoundingBox() && a.isUsingBoundingBox()) {
            return willBoundingBoxBoxCollide(a, b, maxTime);
        }
        return false;
    }

    private static boolean willBoundingCircleCircleCollide(gameengine.collisiondetection.shapes.Polygon a, gameengine.collisiondetection.shapes.Polygon b, double maxTime) {
        double combinedVelX = b.dx - a.dx;
        double combinedVelY = b.dy - a.dy;
        if (combinedVelX == 0 && combinedVelY == 0) {
            // the speed relative to each other is 0;
            return false;
        }
        double distToLineSquared =
                Vector2D.distToLineSquared(a.x, a.y, b.x, b.y, b.x + combinedVelX, b.y + combinedVelY);
        double radiiSum = a.radius + b.radius;
        if (distToLineSquared > radiiSum * radiiSum) {
            // not in the path of the combined velocity
            return false;
        }
        double deltaX = a.x - b.x;
        double deltaY = a.y - b.y;
        double distBetween = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        distBetween -= radiiSum;
        if (distBetween <= 0) {
            return true;
        }
        double projVel = Vector2D.scalarProject(combinedVelX, combinedVelY, deltaX, deltaY, distBetween);
        if (projVel < 0) {
            // travelling away from each other
            return false;
        }
        double travelTime = distBetween / projVel;
        return travelTime <= maxTime;
    }

    /**
     * @param a       the polygon with a bounding circle
     * @param b       the polygon with a bounding box
     * @param maxTime the maximum amount of time for a collision to occur
     * @return true if a collision will occur, false otherwise
     */
    private static boolean willBoundingCircleBoxCollide(gameengine.collisiondetection.shapes.Polygon a, gameengine.collisiondetection.shapes.Polygon b, double maxTime) {
        double relativeVelX = a.dx - b.dx, relativeVelY = a.dy - b.dy;
        double maxOverlapTime = getBoxOverlapTime(
                a.x, a.y, a.radius, a.radius, b.x, b.y, b.getMaxX(), b.getMaxY(), relativeVelX, relativeVelY);
        if (maxOverlapTime == NO_COLLISION) {
            return false;
        }

        //checking collision with points
        relativeVelX *= -1;
        relativeVelY *= -1;
        double radiusSquared = a.radius * a.radius;
        maxOverlapTime = Math.min(getTOICirclePoint(
                a.x, a.y, radiusSquared, b.getMinX(), b.getMinY(), relativeVelX, relativeVelY), maxOverlapTime);
        maxOverlapTime = Math.min(getTOICirclePoint(
                a.x, a.y, radiusSquared, b.getMaxX(), b.getMinY(), relativeVelX, relativeVelY), maxOverlapTime);
        maxOverlapTime = Math.min(getTOICirclePoint(
                a.x, a.y, radiusSquared, b.getMaxX(), b.getMaxY(), relativeVelX, relativeVelY), maxOverlapTime);
        maxOverlapTime = Math.min(getTOICirclePoint(
                a.x, a.y, radiusSquared, b.getMinX(), b.getMaxY(), relativeVelX, relativeVelY), maxOverlapTime);
        return maxOverlapTime <= maxTime;
    }

    private static boolean willBoundingBoxBoxCollide(gameengine.collisiondetection.shapes.Polygon a, gameengine.collisiondetection.shapes.Polygon b, double maxTime) {
        double relativeVelX = a.dx - b.dx, relativeVelY = a.dy - b.dy;
        double maxOverlapTime = getBoxOverlapTime(a.x, a.y, a.getMaxX(), a.getMaxY(),
                b.x, b.y, b.getMaxX(), b.getMaxY(), relativeVelX, relativeVelY);
        return maxOverlapTime < maxTime;
    }

    private static double getBoxOverlapTime(double aX, double aY, double aHalfX, double aHalfY,
                                            double bX, double bY, double bHalfX, double bHalfY,
                                            double relativeVelX, double relativeVelY) {
        double overlapTime;
        if (relativeVelX > 0) {
            overlapTime = getOverlapTime(bX - bHalfX, aX + aHalfX, relativeVelX);
        } else if (relativeVelX < 0) {
            overlapTime = getOverlapTime(bX + bHalfX, aX - aHalfX, relativeVelX);
        } else {
            if (aX - aHalfX > bX + bHalfX || aX + aHalfX < bX - bHalfX) {
                return NO_COLLISION;
            }
            overlapTime = 0;
        }
        if (relativeVelY > 0) {
            overlapTime = Math.max(getOverlapTime(bY - bHalfY, aY + aHalfY, relativeVelY), overlapTime);
        } else if (relativeVelY < 0) {
            overlapTime = Math.max(getOverlapTime(bY + bHalfY, aY - aHalfY, relativeVelY), overlapTime);
        } else {
            if (aY - aHalfY > bY + bHalfY || aY + aHalfY < bY - bHalfY) {
                return NO_COLLISION;
            }
        }
        return overlapTime;
    }

    private static double getTOICirclePoint(double circleX, double circleY, double radiusSquared, double pointX,
                                            double pointY, double pointVelX, double pointVelY) {
        double distToLineSquared =
                Vector2D.distToLineSquared(circleX, circleY, pointX, pointY, pointX + pointVelX, pointY + pointVelY);
        if (distToLineSquared <= radiusSquared) {
            double subLength = Math.sqrt(radiusSquared - distToLineSquared);
            double velLength = Math.sqrt(pointVelX * pointVelX + pointVelY * pointVelY);
            double projLength =
                    Vector2D.scalarProject(circleX - pointX, circleY - pointY, pointVelX, pointVelY, velLength);
            if (projLength > 0) {
                return (projLength - subLength) / velLength;
            }
        }
        return -Double.MAX_VALUE;
    }

    private static double getOverlapTime(double actualBPos, double actualAPos, double relativeVel) {
        double dist = actualBPos - actualAPos;
        return dist / relativeVel;
    }

    public abstract int getShapeType();

    public abstract void draw(Graphics2D g, Color color);
}
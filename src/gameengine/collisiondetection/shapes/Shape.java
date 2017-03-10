package gameengine.collisiondetection.shapes;

import gameengine.collisiondetection.Collision;
import gameengine.entities.Entity;
import gameengine.geometry.Vector2D;
import gameengine.graphics.Renderer;

/**
 * @author david
 */
public abstract class Shape {
    protected final double halfWidth, halfHeight, width, height;

    public Shape(double halfWidth, double halfHeight) {
        this.halfWidth = halfWidth;
        this.halfHeight = halfHeight;
        width = halfWidth * 2;
        height = halfHeight * 2;
    }

    public static void collideShapes(Entity a, Entity b, double maxTime, Collision result) {
        double combinedHalfWidths = a.getBBHalfWidth() + b.getBBHalfWidth();
        double combinedHalfHeights = a.getBBHalfHeight() + b.getBBHalfHeight();

        if (Math.abs(a.getBBCenterX() - b.getBBCenterX()) > combinedHalfWidths || Math.abs(a
                .getBBCenterY() - b.getBBCenterY()) > combinedHalfHeights) {
            result.setNoCollision();
            return;
        }

        a.getShape().collideWithShape(a, b, maxTime, result);
    }

    public static void collidePolyPoly(Entity aParent, Polygon a, Entity bParent, Polygon b,
                                       double maxTime, Collision result) {
        CollisionData collisionData = result.getCollisionData();
        collisionData.clear();
        getEntryLeaveAndOverlapTime(aParent, a, bParent, b, collisionData);
        if (collisionData.isCollisionNotPossible()) {
            result.setNoCollision();
            return;
        }
        getEntryLeaveAndOverlapTime(bParent, b, aParent, a, collisionData);
        if (collisionData.isCollisionNotPossible()) {
            result.setNoCollision();
            return;
        }
        if (collisionData.isIntersectingAndTravellingTowardsEachOther()) {
            result.set(0, collisionData.getOverlapNormal(), aParent, bParent);
            return;
        }
        if (collisionData.willCollisionHappen(maxTime)) {
            result.set(collisionData.getEntryTime(), collisionData.getCollisionNormal(), aParent,
                    bParent);
            return;
        }
        result.setNoCollision();
    }

    public static void collideRectanglePoly(Entity aParent, Rectangle a, Entity bParent, Polygon
            b, double maxTime, Collision result) {
        CollisionData collisionData = result.getCollisionData();
        collisionData.clear();
        double relVelX = aParent.getDX() - bParent.getDX();
        double relVelY = aParent.getDY() - bParent.getDY();

        double aX = aParent.getX();
        double bX = bParent.getX();
        double minX = aX - a.halfWidth - bX, maxX = aX + a.halfWidth - bX;
        double minY = aParent.getY() - a.halfHeight - bParent.getY(), maxY = aParent.getY() + a
                .halfHeight - bParent.getY();
        for (int i = 0; i < b.getNumPoints(); i++) {
            Vector2D normal = b.getNormals()[i];
            double bMin = b.getNormalMins()[i];
            double bMax = b.getNormalMaxs()[i];
            double projVel = Vector2D.unitScalarProject(relVelX, relVelY, normal);
            calcTOIUsingPolyNormalAndSetMinMaxValues(minX, maxX, minY, maxY, normal, bMin, bMax,
                    projVel, collisionData);
            if (collisionData.isCollisionNotPossible()) {
                result.setNoCollision();
                return;
            }
            double dist = bMax - collisionData.getMin();
            collisionData.updateTempOverlapData(dist, projVel, normal.getX(), normal.getY());
        }

        calcCollisionWithBoxNormals(aParent, a, bParent, b, relVelX, relVelY, collisionData);
        if (collisionData.isCollisionNotPossible()) {
            result.setNoCollision();
            return;
        }
        collisionData.updateOverlapData();
        if (collisionData.isIntersectingAndTravellingTowardsEachOther()) {
            result.set(0, collisionData.getOverlapNormal(), aParent, bParent);
        } else if (collisionData.willCollisionHappen(maxTime)) {
            result.set(collisionData.getEntryTime(), collisionData.getCollisionNormal(), aParent,
                    bParent);
        } else {
            result.setNoCollision();
        }
    }

    private static void calcTOIUsingPolyNormalAndSetMinMaxValues(double minX, double maxX, double
            minY, double maxY, Vector2D normal, double bMin, double bMax, double projVel,
                                                                 CollisionData collisionData) {
        collisionData.clearMinMax();
        double projDist = Vector2D.unitScalarProject(minX, minY, normal);
        collisionData.updateMinMax(projDist);
        projDist = Vector2D.unitScalarProject(maxX, minY, normal);
        collisionData.updateMinMax(projDist);
        projDist = Vector2D.unitScalarProject(maxX, maxY, normal);
        collisionData.updateMinMax(projDist);
        projDist = Vector2D.unitScalarProject(minX, maxY, normal);
        collisionData.updateMinMax(projDist);
        calcTOIAlongAxis(collisionData.getMin(), collisionData.getMax(), bMin, bMax, projVel,
                collisionData, normal);
    }

    private static void calcCollisionWithBoxNormals(Entity aParent, Rectangle a, Entity bParent,
                                                    Polygon b, double relVelX, double relVelY,
                                                    CollisionData collisionData) {
        double bMinX = Double.MAX_VALUE;
        double bMaxX = -Double.MAX_VALUE;
        double bMinY = Double.MAX_VALUE;
        double bMaxY = -Double.MAX_VALUE;
        double offsetX = bParent.getX() - aParent.getX();
        double offsetY = bParent.getY() - aParent.getY();
        for (int i = 0; i < b.getNumPoints(); i++) {
            Vector2D point = b.getPoints()[i];
            double x = point.getX() + offsetX;
            bMinX = Math.min(bMinX, x);
            bMaxX = Math.max(bMaxX, x);
            double y = point.getY() + offsetY;
            bMinY = Math.min(bMinY, y);
            bMaxY = Math.max(bMaxY, y);
        }
        double minX = -a.halfWidth;
        double maxX = a.halfWidth;
        double minY = -a.halfHeight;
        double maxY = a.halfHeight;
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

    public static void collideCirclePoly(Entity aParent, Circle a, Entity bParent, Polygon b,
                                         double maxTime, Collision result) {
        double entryTime = -Double.MAX_VALUE;
        double leaveTime = Double.MAX_VALUE;
        double collisionNormalX = 0, collisionNormalY = 0;
        double overlapExitTime = Double.MAX_VALUE;
        double overlapVel = 0;
        double overlapNormalX = 0, overlapNormalY = 0;

        final double relVelX = aParent.getDX() - bParent.getDX();
        final double relVelY = aParent.getDY() - bParent.getDY();
        final double aX = aParent.getX(), aY = aParent.getY(), bX = bParent.getX(), bY = bParent
                .getY();
        final double aMax = a.getRadius(), aMin = -aMax;

        Vector2D[] normals = b.getNormals();
        double[] mins = b.getNormalMins();
        double[] maxs = b.getNormalMaxs();

        for (int i = 0; i < normals.length; i++) {
            Vector2D normal = normals[i];
            double aPos = Vector2D.unitScalarProject(aX, aY, normal);
            double bPos = Vector2D.unitScalarProject(bX, bY, normal);
            double deltaPos = bPos - aPos;
            double bMin = mins[i] + deltaPos;
            double bMax = maxs[i] + deltaPos;

            double projVel = Vector2D.unitScalarProject(relVelX, relVelY, normal);
            double normalEntryTime = getEntryTimeAlongAxis(aMin, aMax, bMin, bMax, projVel);
            if (normalEntryTime == CollisionData.NO_COLLISION) {
                result.setNoCollision();
                return;
            }
            if (normalEntryTime > entryTime) {
                entryTime = normalEntryTime;
                collisionNormalX = normal.getX();
                collisionNormalY = normal.getY();
            }
            leaveTime = Math.min(getLeaveTimeAlongAxis(aMin, aMax, bMin, bMax, projVel), leaveTime);

            // because we are working with convex polygons bMax will always be the farthest edge
            // from the center
            double dist = bMax - aMin;
            if (dist > 0) { // true if overlapping along this normal
                double exitTime = dist / Math.abs(projVel);
                if (exitTime < overlapExitTime) {
                    overlapExitTime = exitTime;
                    overlapVel = projVel;
                    overlapNormalX = normal.getX();
                    overlapNormalY = normal.getY();
                }
            }
        }

        double diffX = bX - aX;
        double diffY = bY - aY;

        Vector2D[] verticies = b.getPoints();
        for (int i = 0; i < verticies.length; i++) {
            Vector2D vertex = verticies[i];
            double bRelX = vertex.getX() + diffX;
            double bRelY = vertex.getY() + diffY;
            double dist = Math.sqrt(bRelX * bRelX + bRelY * bRelY);
            double normalX = bRelX / dist;
            double normalY = bRelY / dist;

            double bMin = Double.MAX_VALUE;
            double bMax = -Double.MAX_VALUE;
            for (int j = 0; j < verticies.length; j++) {
                Vector2D point = verticies[j];
                double x = point.getX() + diffX;
                double y = point.getY() + diffY;
                double projection = Vector2D.unitScalarProject(x, y, normalX, normalY);
                bMin = Math.min(projection, bMin);
                bMax = Math.max(projection, bMax);
            }

            double projVel = Vector2D.unitScalarProject(relVelX, relVelY, normalX, normalY);
            double normalEntryTime = getEntryTimeAlongAxis(aMin, aMax, bMin, bMax, projVel);
            if (normalEntryTime == CollisionData.NO_COLLISION) {
                result.setNoCollision();
                return;
            }

            if (normalEntryTime > entryTime) {
                entryTime = normalEntryTime;
                collisionNormalX = normalX;
                collisionNormalY = normalY;
            }

            leaveTime = Math.min(getLeaveTimeAlongAxis(aMin, aMax, bMin, bMax, projVel), leaveTime);
        }

        if (entryTime == -Double.MAX_VALUE) {
            if (overlapVel < 0) {
                result.set(0, overlapNormalX, overlapNormalY, aParent, bParent);
                return;
            } else {
                result.setNoCollision();
                return;
            }
        }

        if (entryTime <= maxTime && entryTime < leaveTime) {
            result.set(entryTime, collisionNormalX, collisionNormalY, aParent, bParent);
        } else {
            result.setNoCollision();
        }
    }

    public static void collideCircleCircle(Entity aParent, Circle a, Entity bParent, Circle b,
                                           double maxTime, Collision result) {
        double combinedVelX = bParent.getDX() - aParent.getDX();
        double combinedVelY = bParent.getDY() - aParent.getDY();
        if (combinedVelX == 0 && combinedVelY == 0) {
            result.setNoCollision();
            return;
        }

        double aX = aParent.getX();
        double aY = aParent.getY();
        double bX = bParent.getX();
        double bY = bParent.getY();
        double distToLineSquared = Vector2D.distToLineSquared(aX, aY, bX, bY, bX + combinedVelX,
                bY + combinedVelY);
        double radiiSum = a.getRadius() + b.getRadius();
        double radiiSumSquared = radiiSum * radiiSum;
        if (distToLineSquared > radiiSumSquared) {
            result.setNoCollision();
            return;
        }
        // using the collision normal as a scratch pad
        Vector2D velocity = result.getCollisionNormal();
        velocity.set(combinedVelX, combinedVelY);
        double deltaX = aX - bX;
        double deltaY = aY - bY;
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
            result.set(0, velocity, aParent, bParent);
            return;
        }
        velocity.unit();
        double centerAProjectedOnVelocity = Vector2D.unitScalarProject(aX - bX, aY - bY, velocity);
        double subLength = Math.sqrt(radiiSumSquared - distToLineSquared); // a = sqrt(c^2 - b^2)
        // pythagoras
        velocity.scale(centerAProjectedOnVelocity - subLength);
        velocity.add(bX - aX, bY - aY); // this is now the collision normal
        velocity.scale(1 / radiiSum); // normalizes the vector
        result.set(travelTime, velocity, aParent, bParent);
    }

    private static double getEntryTimeAlongAxis(double aMin, double aMax, double bMin, double
            bMax, double relVel) {
        if (aMax <= bMin) {
            if (relVel <= 0) {
                return CollisionData.NO_COLLISION;
            }
            return (bMin - aMax) / relVel;
        } else if (bMax <= aMin) {
            if (relVel >= 0) {
                return CollisionData.NO_COLLISION;
            }
            return (bMax - aMin) / relVel;
        }
        return -Double.MAX_VALUE;
    }

    public static void collideRectangleRectangle(Entity aParent, Rectangle a, Entity bParent,
                                                 Rectangle b, double maxTime, Collision result) {
        double relVelX = aParent.getDX() - bParent.getDX(), relVelY = aParent.getDY() - bParent
                .getDY();

        // calculating entry time along the x axis
        double aMaxX = a.halfWidth, aMinX = -a.halfWidth;
        double aX = aParent.getX(), bX = bParent.getX();
        double bCenter = bX - aX;
        double bMaxX = bCenter + b.halfWidth;
        double bMinX = bCenter - b.halfWidth;
        double entryTime = getEntryTimeAlongAxis(aMinX, aMaxX, bMinX, bMaxX, relVelX);
        assert entryTime == -Double.MAX_VALUE || entryTime >= 0;
        if (entryTime == CollisionData.NO_COLLISION) {
            result.setNoCollision();
            return;
        }

        // calculating entry time along the y axis
        double aMaxY = a.halfHeight, aMinY = -a.halfHeight;
        double aY = aParent.getY(), bY = bParent.getY();
        bCenter = bY - aY;
        double bMaxY = bCenter + b.halfHeight;
        double bMinY = bCenter - b.halfHeight;

        double yEntryTime = getEntryTimeAlongAxis(aMinY, aMaxY, bMinY, bMaxY, relVelY);
        if (yEntryTime == CollisionData.NO_COLLISION) {
            result.setNoCollision();
            return;
        }
        assert yEntryTime == -Double.MAX_VALUE || yEntryTime >= 0;


        double collisionNormalX, collisionNormalY;
        if (yEntryTime > entryTime) {
            entryTime = yEntryTime;
            collisionNormalX = 0;
            collisionNormalY = 1;
        } else {
            collisionNormalX = 1;
            collisionNormalY = 0;
        }

        if (entryTime == -Double.MAX_VALUE) { // if true than the AABBs are overlapping
            if (relVelX > 0 && aX < bX || relVelX < 0 && bX < aX) {
                result.set(0, 1, 0, aParent, bParent);
                return;
            }
            if (relVelY > 0 && aY < bY || relVelY < 0 && bY < aY) {
                result.set(0, 0, 1, aParent, bParent);
                return;
            }
            result.setNoCollision();
            return;
        }
        assert entryTime >= 0 : entryTime;

        // calculating leave time along the x axis
        // cases where they are travelling away from each other have already been checked
        double leaveTime = Math.min(getLeaveTimeAlongAxis(aMinX, aMaxX, bMinX, bMaxX, relVelX),
                getLeaveTimeAlongAxis(aMinY, aMaxY, bMinY, bMaxY, relVelY));

        if (entryTime <= maxTime && entryTime <= leaveTime) {
            result.set(entryTime, collisionNormalX, collisionNormalY, aParent, bParent);
        } else {
            result.setNoCollision();
        }
    }

    public static void collideCircleRectangle(Entity aParent, Circle a, Entity bParent, Rectangle
            b, double maxTime, Collision result) {
        double aX = aParent.getX();
        double bX = bParent.getX();
        double bCenterX = bX - aX;
        final double bMaxX = bCenterX + b.halfWidth;
        final double bMinX = bCenterX - b.halfWidth;
        final double relVelX = aParent.getDX() - bParent.getDX();
        final double aMax = a.getRadius();
        final double aMin = -aMax;
        double entryTime = getEntryTimeAlongAxis(aMin, aMax, bMinX, bMaxX, relVelX);
        if (entryTime == CollisionData.NO_COLLISION) {
            result.setNoCollision();
            return;
        }

        double aY = aParent.getY();
        double bY = bParent.getY();
        double bCenterY = bY - aY;
        final double bMaxY = bCenterY + b.halfHeight;
        final double bMinY = bCenterY - b.halfHeight;
        final double relVelY = aParent.getDY() - bParent.getDY();
        double yEntryTime = getEntryTimeAlongAxis(aMin, aMax, bMinY, bMaxY, relVelY);
        double collisionNormalX;
        double collisionNormalY;
        if (yEntryTime > entryTime) {
            if (yEntryTime == CollisionData.NO_COLLISION) {
                result.setNoCollision();
                return;
            }
            entryTime = yEntryTime;
            collisionNormalX = 0;
            collisionNormalY = 1;
        } else {
            collisionNormalX = 1;
            collisionNormalY = 0;
        }

        double dist = Math.sqrt(bMinX * bMinX + bMinY * bMinY);
        double normalX = bMinX / dist;
        double normalY = bMinY / dist;

        double projDist = Vector2D.unitScalarProject(bMinX, bMinY, normalX, normalY);
        result.setTempMin(projDist);
        result.setTempMax(projDist);

        projDist = Vector2D.unitScalarProject(bMaxX, bMinY, normalX, normalY);
        if (projDist < result.getTempMin()) {
            result.setTempMin(projDist);
        } else {
            result.setTempMax(projDist);
        }

        calcMinMaxAlongAxis(bMaxX, bMaxY, normalX, normalY, result);

        calcMinMaxAlongAxis(bMinX, bMaxY, normalX, normalY, result);

        double projVel = Vector2D.unitScalarProject(relVelX, relVelY, normalX, normalY);

        double axisEntryTime = getEntryTimeAlongAxis(aMin, aMax, result.getTempMin(), result
                .getTempMax(), projVel);
        if (axisEntryTime == CollisionData.NO_COLLISION) {
            result.setNoCollision();
            return;
        }
        if (axisEntryTime > entryTime) {
            entryTime = axisEntryTime;
            collisionNormalX = normalX;
            collisionNormalY = normalY;
        }
        double leaveTime = getLeaveTimeAlongAxis(aMin, aMax, bMinX, bMaxX, relVelX);
        leaveTime = Math.min(getLeaveTimeAlongAxis(aMin, aMax, bMinY, bMaxY, relVelY), leaveTime);
        leaveTime = Math.min(getLeaveTimeAlongAxis(aMin, aMax, result.getTempMin(), result
                .getTempMax(), projVel), leaveTime);

        dist = Math.sqrt(bMaxX * bMaxX + bMinY * bMinY);
        normalX = bMaxX / dist;
        normalY = bMinY / dist;

        projDist = Vector2D.unitScalarProject(bMinX, bMinY, normalX, normalY);
        result.setTempMin(projDist);
        result.setTempMax(projDist);

        projDist = Vector2D.unitScalarProject(bMaxX, bMinY, normalX, normalY);
        if (projDist < result.getTempMin()) {
            result.setTempMin(projDist);
        } else {
            result.setTempMax(projDist);
        }

        calcMinMaxAlongAxis(bMaxX, bMaxY, normalX, normalY, result);

        calcMinMaxAlongAxis(bMinX, bMaxY, normalX, normalY, result);

        projVel = Vector2D.unitScalarProject(relVelX, relVelY, normalX, normalY);

        axisEntryTime = getEntryTimeAlongAxis(aMin, aMax, result.getTempMin(), result.getTempMax
                (), projVel);
        if (axisEntryTime == CollisionData.NO_COLLISION) {
            result.setNoCollision();
            return;
        }
        if (axisEntryTime > entryTime) {
            entryTime = axisEntryTime;
            collisionNormalX = normalX;
            collisionNormalY = normalY;
        }
        leaveTime = Math.min(getLeaveTimeAlongAxis(aMin, aMax, result.getTempMin(), result
                .getTempMax(), projVel), leaveTime);

        dist = Math.sqrt(bMaxX * bMaxX + bMaxY * bMaxY);
        normalX = bMaxX / dist;
        normalY = bMaxY / dist;

        projDist = Vector2D.unitScalarProject(bMinX, bMinY, normalX, normalY);
        result.setTempMin(projDist);
        result.setTempMax(projDist);

        projDist = Vector2D.unitScalarProject(bMaxX, bMinY, normalX, normalY);
        if (projDist < result.getTempMin()) {
            result.setTempMin(projDist);
        } else {
            result.setTempMax(projDist);
        }

        calcMinMaxAlongAxis(bMaxX, bMaxY, normalX, normalY, result);

        calcMinMaxAlongAxis(bMinX, bMaxY, normalX, normalY, result);

        projVel = Vector2D.unitScalarProject(relVelX, relVelY, normalX, normalY);

        axisEntryTime = getEntryTimeAlongAxis(aMin, aMax, result.getTempMin(), result.getTempMax
                (), projVel);
        if (axisEntryTime == CollisionData.NO_COLLISION) {
            result.setNoCollision();
            return;
        }
        if (axisEntryTime > entryTime) {
            entryTime = axisEntryTime;
            collisionNormalX = normalX;
            collisionNormalY = normalY;
        }
        leaveTime = Math.min(getLeaveTimeAlongAxis(aMin, aMax, result.getTempMin(), result
                .getTempMax(), projVel), leaveTime);

        dist = Math.sqrt(bMinX * bMinX + bMaxY * bMaxY);
        normalX = bMinX / dist;
        normalY = bMaxY / dist;

        projDist = Vector2D.unitScalarProject(bMinX, bMinY, normalX, normalY);
        result.setTempMin(projDist);
        result.setTempMax(projDist);

        projDist = Vector2D.unitScalarProject(bMaxX, bMinY, normalX, normalY);
        if (projDist < result.getTempMin()) {
            result.setTempMin(projDist);
        } else {
            result.setTempMax(projDist);
        }

        calcMinMaxAlongAxis(bMaxX, bMaxY, normalX, normalY, result);

        calcMinMaxAlongAxis(bMinX, bMaxY, normalX, normalY, result);

        projVel = Vector2D.unitScalarProject(relVelX, relVelY, normalX, normalY);

        axisEntryTime = getEntryTimeAlongAxis(aMin, aMax, result.getTempMin(), result.getTempMax
                (), projVel);
        if (axisEntryTime == CollisionData.NO_COLLISION) {
            result.setNoCollision();
            return;
        }
        if (axisEntryTime > entryTime) {
            entryTime = axisEntryTime;
            collisionNormalX = normalX;
            collisionNormalY = normalY;
        }
        leaveTime = Math.min(getLeaveTimeAlongAxis(aMin, aMax, result.getTempMin(), result
                .getTempMax(), projVel), leaveTime);

        if (entryTime == -Double.MAX_VALUE) { // overlapping if true
            if (relVelX > 0 && aX < bX || relVelX < 0 && bX < aX) {
                result.set(0, 1, 0, aParent, bParent);
                return;
            }
            if (relVelY > 0 && aY < bY || relVelY < 0 && bY < aY) {
                result.set(0, 0, 1, aParent, bParent);
                return;
            }
            result.setNoCollision();
            return;
        }
        if (entryTime <= maxTime && entryTime <= leaveTime && entryTime != -Double.MAX_VALUE) {
            result.set(entryTime, collisionNormalX, collisionNormalY, aParent, bParent);
        } else {
            result.setNoCollision();
        }
    }

    private static void calcMinMaxAlongAxis(double x, double y, double normalX, double normalY,
                                            Collision result) {
        double projDist;
        projDist = Vector2D.unitScalarProject(x, y, normalX, normalY);
        if (projDist < result.getTempMin()) {
            result.setTempMin(projDist);
        } else if (projDist > result.getTempMax()) {
            result.setTempMax(projDist);
        }
    }

    private static void getEntryLeaveAndOverlapTime(Entity aParent, Polygon a, Entity bParent,
                                                    Polygon b, CollisionData collisionData) {
//        double tempVelocity = collisionData.getOverlapVelocity(); // TODO why are these here
//        collisionData.setTempOverlapVelocity(collisionData.getOverlapVelocity());
        collisionData.resetOverlapUpdated();
        double relVelX = aParent.getDX() - bParent.getDX();
        double relVelY = aParent.getDY() - bParent.getDY();
        for (int i = 0; i < a.getNumPoints(); i++) {
            Vector2D normal = a.getNormals()[i];
            collisionData.clearMinMax();
            for (Vector2D point : b.getPoints()) {
                double dist = Vector2D.unitScalarProject(point.getX() + bParent.getX() - aParent
                        .getX(), point.getY() + bParent.getY() - aParent.getY(), normal);
                collisionData.updateMinMax(dist);
            }
            double aMin = a.getNormalMins()[i];
            double aMax = a.getNormalMaxs()[i];
            double projVel = Vector2D.unitScalarProject(relVelX, relVelY, normal);
            calcTOIAlongAxis(aMin, aMax, collisionData.getMin(), collisionData.getMax(), projVel,
                    collisionData, normal);
            if (collisionData.isCollisionNotPossible()) {
                return;
            }
            double dist = collisionData.getMax() - aMin;
            collisionData.updateTempOverlapData(dist, projVel, normal.getX(), normal.getY());
        }
        collisionData.updateOverlapData();
    }

    private static void calcTOIAlongAxis(double aMin, double aMax, double bMin, double bMax,
                                         double vel, CollisionData collisionData, Vector2D axis) {
        calcTOIAlongAxis(aMin, aMax, bMin, bMax, vel, collisionData, axis.getX(), axis.getY());
    }

    private static double getLeaveTimeAlongAxis(double aMin, double aMax, double bMin, double
            bMax, double vel) {
        double leaveTime = Double.MAX_VALUE;
        if (vel > 0) {
            leaveTime = (bMax - aMin) / vel;
        } else if (vel < 0) {
            leaveTime = (bMin - aMax) / vel;
        }
        return leaveTime;
    }

    private static void calcTOIAlongAxis(double aMin, double aMax, double bMin, double bMax,
                                         double vel, CollisionData collisionData, double axisX,
                                         double axisY) {
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

    public static boolean isOverlappingPolyPoly(Entity aParent, Polygon a, Entity bParent,
                                                Polygon b) {
        final double aX = aParent.getX(), aY = aParent.getY();
        final double bX = bParent.getX(), bY = bParent.getY();
        final double deltaX = aX - bX;
        final double deltaY = aY - bY;
        Vector2D[] aPoints = a.getPoints();
        Vector2D[] bNormals = b.getNormals();
        double[] bMins = b.getNormalMins();
        double[] bMaxs = b.getNormalMaxs();
        for (int i = 0; i < bNormals.length; i++) {
            Vector2D normal = bNormals[i];
            double min = Double.MAX_VALUE;
            double max = -Double.MIN_VALUE;
            for (int j = 0; j < aPoints.length; j++) {
                Vector2D aPoint = aPoints[j];
                double pX = aPoint.getX() - deltaX;
                double pY = aPoint.getY() - deltaY;
                double dist = Vector2D.unitScalarProject(pX, pY, normal);
                if (dist < min) {
                    min = dist;
                }
                if (dist > max) {
                    max = dist;
                }
            }

            double bMin = bMins[i];
            double bMax = bMaxs[i];
            if (max < bMin || min > bMax) {
                return false;
            }
        }
        Vector2D[] bPoints = b.getPoints();
        Vector2D[] aNormals = a.getNormals();
        double[] aMins = a.getNormalMins();
        double[] aMaxs = a.getNormalMaxs();
        for (int i = 0; i < aNormals.length; i++) {
            Vector2D normal = aNormals[i];
            double min = Double.MAX_VALUE;
            double max = -Double.MIN_VALUE;
            for (int j = 0; j < bPoints.length; j++) {
                Vector2D bPoint = bPoints[j];
                double pX = bPoint.getX() + deltaX;
                double pY = bPoint.getY() + deltaY;
                double dist = Vector2D.unitScalarProject(pX, pY, normal);
                if (dist < min) {
                    min = dist;
                }
                if (dist > max) {
                    max = dist;
                }
            }

            double aMin = aMins[i];
            double aMax = aMaxs[i];
            if (max < aMin || min > aMax) {
                return false;
            }
        }
        return true;
    }

    public static boolean isOverlappingPolyCircle(Entity aParent, Polygon a, Entity bParent,
                                                  Circle b) {
        final double aX = aParent.getX(), aY = aParent.getY();
        final double bX = bParent.getX(), bY = bParent.getY();
        double bRadius = b.getRadius();

        final double deltaX = bX - aX;
        final double deltaY = bY - aY;
        Vector2D[] aNormals = a.getNormals();
        double[] aMins = a.getNormalMins();
        double[] aMaxs = a.getNormalMaxs();
        for (int i = 0; i < aNormals.length; i++) {
            Vector2D normal = aNormals[i];
            double bPos = Vector2D.unitScalarProject(deltaX, deltaY, normal);
            double bMin = bPos - bRadius;
            double bMax = bPos + bRadius;

            double aMin = aMins[i];
            double aMax = aMaxs[i];
            if (bMax < aMin || bMin > aMax) {
                return false;
            }
        }

        double minDX = 0, minDY = 0;
        double minDistSquard = Double.MAX_VALUE;
        Vector2D[] aPoints = a.getPoints();
        for (int i = 0; i < aPoints.length; i++) {
            Vector2D point = aPoints[i];
            double pX = point.getX() + aX;
            double pY = point.getY() + aY;
            double dX = bX - pX;
            double dY = bY - pY;
            double distSquared = dX * dX + dY * dY;
            if (distSquared < minDistSquard) {
                minDistSquard = distSquared;
                minDX = dX;
                minDY = dY;
            }
        }
        double dist = Math.sqrt(minDistSquard);
        double normalX = minDX / dist;
        double normalY = minDY / dist;
        double aMin = Double.MAX_VALUE;
        double aMax = -Double.MAX_VALUE;
        for (int i = 0; i < aPoints.length; i++) {
            Vector2D point = aPoints[i];
            double pX = point.getX();
            double pY = point.getY();
            double shadow = Vector2D.unitScalarProject(pX, pY, normalX, normalY);
            if (shadow < aMin) {
                aMin = shadow;
            }
            if (shadow > aMax) {
                aMax = shadow;
            }
        }
        double bPos = Vector2D.unitScalarProject(deltaX, deltaY, normalX, normalY);
        if (bPos + bRadius < aMin || bPos - bRadius > aMax) {
            return false;
        }
        return true;
    }

    public static boolean isOverlappingPolyRectangle(Entity aParent, Polygon a, Entity bParent,
                                                     Rectangle b) {
        final double aX = aParent.getX(), aY = aParent.getY();
        final double bX = bParent.getX(), bY = bParent.getY();
        double bHalfWidth = b.getHalfWidth();
        double bHalfHeight = b.getHalfHeight();
        final double deltaX = bX - aX;
        final double deltaY = bY - aY;
        final double bMinX = deltaX - bHalfWidth;
        final double bMaxX = deltaX + bHalfWidth;
        final double bMinY = deltaY - bHalfHeight;
        final double bMaxY = deltaY + bHalfHeight;
        Vector2D[] aNormals = a.getNormals();
        double[] aMins = a.getNormalMins();
        double[] aMaxs = a.getNormalMaxs();
        for (int i = 0; i < aNormals.length; i++) {
            Vector2D normal = aNormals[i];

            double bPointShadow = Vector2D.unitScalarProject(bMinX, bMinY, normal);
            double bMin = bPointShadow;
            double bMax = bPointShadow;

            bPointShadow = Vector2D.unitScalarProject(bMaxX, bMinY, normal);
            if (bPointShadow < bMin) {
                bMin = bPointShadow;
            } else {
                bMax = bPointShadow;
            }

            bPointShadow = Vector2D.unitScalarProject(bMaxX, bMaxY, normal);
            if (bPointShadow < bMin) {
                bMin = bPointShadow;
            } else if (bPointShadow > bMax) {
                bMax = bPointShadow;
            }

            bPointShadow = Vector2D.unitScalarProject(bMinX, bMaxY, normal);
            if (bPointShadow < bMin) {
                bMin = bPointShadow;
            } else if (bPointShadow > bMax) {
                bMax = bPointShadow;
            }

            double aMin = aMins[i];
            double aMax = aMaxs[i];
            if (bMax < aMin || bMin > aMax) {
                return false;
            }
        }

        double normalX = 1;
        double normalY = 0;
        double aMin = Double.MAX_VALUE;
        double aMax = -Double.MAX_VALUE;
        Vector2D[] aPoints = a.getPoints();
        for (int i = 0; i < aPoints.length; i++) {
            Vector2D point = aPoints[i];
            double pX = point.getX();
            double pY = point.getY();
            double shadow = Vector2D.unitScalarProject(pX, pY, normalX, normalY);
            if (shadow < aMin) {
                aMin = shadow;
            }
            if (shadow > aMax) {
                aMax = shadow;
            }
        }
        if (bMaxX < aMin || bMinX > aMax) {
            return false;
        }

        normalX = 0;
        normalY = 1;
        aMin = Double.MAX_VALUE;
        aMax = -Double.MAX_VALUE;
        for (int i = 0; i < aPoints.length; i++) {
            Vector2D point = aPoints[i];
            double pX = point.getX();
            double pY = point.getY();
            double shadow = Vector2D.unitScalarProject(pX, pY, normalX, normalY);
            if (shadow < aMin) {
                aMin = shadow;
            }
            if (shadow > aMax) {
                aMax = shadow;
            }
        }
        if (bMaxY < aMin || bMinY > aMax) {
            return false;
        }
        return true;
    }

    public static boolean isOverlappingCircleCircle(Entity aParent, Circle a, Entity bParent,
                                                    Circle b) {
        double deltaX = aParent.getX() - bParent.getX();
        double deltaY = aParent.getY() - bParent.getY();
        double distSquared = deltaX * deltaX + deltaY * deltaY;
        double radiiSum = a.getRadius() + b.getRadius();
        double radiiSumSquared = radiiSum * radiiSum;
        return distSquared < radiiSumSquared;
    }

    public static boolean isOverlappingCircleRectangle(Entity aParent, Circle a, Entity bParent,
                                                       Rectangle b) {
        final double aX = aParent.getX(), aY = aParent.getY();
        final double bX = bParent.getX(), bY = bParent.getY();
        double bHalfWidth = b.getHalfWidth();
        double bHalfHeight = b.getHalfHeight();
        final double deltaX = bX - aX;
        final double deltaY = bY - aY;
        final double bMinX = deltaX - bHalfWidth;
        final double bMaxX = deltaX + bHalfWidth;
        final double bMinY = deltaY - bHalfHeight;
        final double bMaxY = deltaY + bHalfHeight;
        final double aMax = a.getRadius();
        final double aMin = -aMax;

        if (bMaxX < aMin || bMinX > aMax || bMaxY < aMin || bMinY > aMax) {
            return false;
        }
        double bMin, bMax;
        double normalX, normalY;
        if (aX < bX) {
            double bMinXSquared = bMinX * bMinX;
            if (aY < bY) {
                double dist = Math.sqrt(bMinXSquared + bMinY * bMinY);
                normalX = bMinX / dist;
                normalY = bMinY / dist;
            } else {
                double dist = Math.sqrt(bMinXSquared + bMaxY * bMaxY);
                normalX = bMinX / dist;
                normalY = bMaxY / dist;
            }
        } else {
            double bMaxXSquared = bMaxX * bMaxX;
            if (aY < bY) {
                double dist = Math.sqrt(bMaxXSquared + bMinY * bMinY);
                normalX = bMaxX / dist;
                normalY = bMinY / dist;
            } else {
                double dist = Math.sqrt(bMaxXSquared + bMaxY * bMaxY);
                normalX = bMaxX / dist;
                normalY = bMaxY / dist;
            }
        }

        double bPointShadow = Vector2D.unitScalarProject(bMinX, bMinY, normalX, normalY);
        bMin = bPointShadow;
        bMax = bPointShadow;

        bPointShadow = Vector2D.unitScalarProject(bMaxX, bMinY, normalX, normalY);
        if (bPointShadow < bMin) {
            bMin = bPointShadow;
        } else {
            bMax = bPointShadow;
        }

        bPointShadow = Vector2D.unitScalarProject(bMaxX, bMaxY, normalX, normalY);
        if (bPointShadow < bMin) {
            bMin = bPointShadow;
        } else if (bPointShadow > bMax) {
            bMax = bPointShadow;
        }

        bPointShadow = Vector2D.unitScalarProject(bMinX, bMaxY, normalX, normalY);
        if (bPointShadow < bMin) {
            bMin = bPointShadow;
        } else if (bPointShadow > bMax) {
            bMax = bPointShadow;
        }

        if (bMax < aMin || bMin > aMax) {
            return false;
        }
        return true;
    }

    public static boolean isOverlappingRectangleRectangle(Entity aParent, Rectangle a, Entity
            bParent, Rectangle b) {
        double deltaX = aParent.getX() - bParent.getX();
        double deltaY = aParent.getY() - bParent.getY();
        double combinedHalfWidth = a.getHalfWidth() + b.getHalfWidth();
        double combinedHalfHeight = a.getHalfHeight() + b.getHalfHeight();
        return Math.abs(deltaX) <= combinedHalfWidth && Math.abs(deltaY) <= combinedHalfHeight;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public double getHalfWidth() {
        return halfWidth;
    }

    public double getHalfHeight() {
        return halfHeight;
    }

    public abstract double getArea();

    public abstract void collideWithShape(Entity current, Entity other, double maxTime, Collision
            result);

    public abstract void collideWithCircle(Entity current, Entity other, Circle circleShape,
                                           double maxTime, Collision result);

    public abstract void collideWithRectangle(Entity current, Entity other, Rectangle aabbShape,
                                              double maxTime, Collision result);

    public abstract void collideWithPolygon(Entity current, Entity other, Polygon polygonShape,
                                            double maxTime, Collision result);

    public abstract boolean isOverlappingShape(Entity current, Entity other);

    public abstract boolean isOverlappingPolygon(Entity current, Entity other, Polygon shape);

    public abstract boolean isOverlappingCircle(Entity current, Entity other, Circle shape);

    public abstract boolean isOverlappingRectangle(Entity current, Entity other, Rectangle shape);

    /**
     * Draws the shape using the current foreground color.
     *
     * @param renderer The screen renderer
     * @param x        The X component of the location
     * @param y        The Y component of the location
     */
    public abstract void draw(Renderer renderer, double x, double y);
}

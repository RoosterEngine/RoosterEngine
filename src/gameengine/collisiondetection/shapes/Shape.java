package gameengine.collisiondetection.shapes;

import gameengine.collisiondetection.Collision;
import gameengine.entities.Entity;
import gameengine.geometry.Vector2D;
import gameengine.graphics.Renderer;

/**
 * @author david
 */
public abstract class Shape {
    public static final double NO_COLLISION = Double.MAX_VALUE;
    protected double parentOffsetX, parentOffsetY;
    protected double halfWidth, halfHeight, width, height;
    protected Entity parent = null;

    public Shape(double halfWidth, double halfHeight) {
        this.halfWidth = halfWidth;
        this.halfHeight = halfHeight;
        width = halfWidth * 2;
        height = halfHeight * 2;
        parentOffsetX = 0;
        parentOffsetY = 0;
    }

    public static void collideShapes(Shape a, Shape b, double maxTime, Collision result) {
        Entity aParent = a.parent;
        Entity bParent = b.parent;
        double combinedHalfWidths = aParent.getBBHalfWidth() + bParent.getBBHalfWidth();
        double combinedHalfHeights = aParent.getBBHalfHeight() + bParent.getBBHalfHeight();

        if (Math.abs(aParent.getBBCenterX() - bParent.getBBCenterX()) > combinedHalfWidths ||
                Math.abs(aParent.getBBCenterY() - bParent.getBBCenterY()) > combinedHalfHeights) {
            result.setNoCollision();
            return;
        }

        a.collideWithShape(b, maxTime, result);
    }

    public static void collidePolyPoly(PolygonShape a, PolygonShape b, double maxTime, Collision
            result) {
        CollisionData collisionData = result.getCollisionData();
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
            result.set(0, collisionData.getOverlapNormal(), a.parent, b.parent);
            return;
        }
        if (collisionData.willCollisionHappen(maxTime)) {
            result.set(collisionData.getEntryTime(), collisionData.getCollisionNormal(), a
                    .parent, b.parent);
            return;
        }
        result.setNoCollision();
    }

    public static void collideAABBPoly(AABBShape a, PolygonShape b, double maxTime, Collision
            result) {
        CollisionData collisionData = result.getCollisionData();
        collisionData.clear();
        double relVelX = a.getDX() - b.getDX();
        double relVelY = a.getDY() - b.getDY();

        double aX = a.getX();
        double bX = b.getX();
        double minX = aX - a.halfWidth - bX, maxX = aX + a.halfWidth - bX;
        double minY = a.getY() - a.halfHeight - b.getY(), maxY = a.getY() + a.halfHeight - b.getY();
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

        calcCollisionWithBoxNormals(a, b, relVelX, relVelY, collisionData);
        if (collisionData.isCollisionNotPossible()) {
            result.setNoCollision();
            return;
        }
        collisionData.updateOverlapData();
        if (collisionData.isIntersectingAndTravellingTowardsEachOther()) {
            result.set(0, collisionData.getOverlapNormal(), a.parent, b.parent);
        } else if (collisionData.willCollisionHappen(maxTime)) {
            result.set(collisionData.getEntryTime(), collisionData.getCollisionNormal(), a
                    .parent, b.parent);
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

    private static void calcCollisionWithBoxNormals(AABBShape a, PolygonShape b, double relVelX,
                                                    double relVelY, CollisionData collisionData) {
        double bMinX = Double.MAX_VALUE;
        double bMaxX = -Double.MAX_VALUE;
        double bMinY = Double.MAX_VALUE;
        double bMaxY = -Double.MAX_VALUE;
        double offsetX = b.getX() - a.getX();
        double offsetY = b.getY() - a.getY();
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

    public static void collideCirclePoly(CircleShape a, PolygonShape b, double maxTime, Collision
            result) {
        double entryTime = -Double.MAX_VALUE;
        double leaveTime = Double.MAX_VALUE;
        double collisionNormalX = 0, collisionNormalY = 0;
        double overlapExitTime = Double.MAX_VALUE;
        double overlapVel = 0;
        double overlapNormalX = 0, overlapNormalY = 0;

        final double relVelX = a.getDX() - b.getDX();
        final double relVelY = a.getDY() - b.getDY();
        final double aX = a.getX(), aY = a.getY(), bX = b.getX(), bY = b.getY();
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
            if (normalEntryTime == NO_COLLISION) {
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
            if (normalEntryTime == NO_COLLISION) {
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
                result.set(0, overlapNormalX, overlapNormalY, a.parent, b.parent);
                return;
            } else {
                result.setNoCollision();
                return;
            }
        }

        if (entryTime <= maxTime && entryTime < leaveTime) {
            result.set(entryTime, collisionNormalX, collisionNormalY, a.parent, b.parent);
        } else {
            result.setNoCollision();
        }
    }

    public static void collideCircleCircle(CircleShape a, CircleShape b, double maxTime,
                                           Collision result) {
        double combinedVelX = b.getDX() - a.getDX();
        double combinedVelY = b.getDY() - a.getDY();
        if (combinedVelX == 0 && combinedVelY == 0) {
            result.setNoCollision();
            return;
        }

        double aX = a.getX();
        double aY = a.getY();
        double bX = b.getX();
        double bY = b.getY();
        double distToLineSquared = Vector2D.distToLineSquared(aX, aY, bX, bY, bX + combinedVelX,
                bY + combinedVelY);
        double radiiSum = a.getRadius() + b.getRadius();
        double radiiSumSquared = radiiSum * radiiSum;
        if (distToLineSquared > radiiSumSquared) {
            result.setNoCollision();
            return;
        }
        // using the collision normal as a scratch pad
        Vector2D velocity = result.getCollisionNormal().set(combinedVelX, combinedVelY);
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
            result.set(0, velocity, a.parent, b.parent);
            return;
        }
        velocity.unit();
        double centerAProjectedOnVelocity = Vector2D.unitScalarProject(aX - bX, aY - bY, velocity);
        double subLength = Math.sqrt(radiiSumSquared - distToLineSquared); // a = sqrt(c^2 - b^2)
        // pythagoras
        velocity.scale(centerAProjectedOnVelocity - subLength);
        velocity.add(bX - aX, bY - aY); // this is now the collision normal
        velocity.divide(radiiSum); // normalizes the vector
        result.set(travelTime, velocity, a.parent, b.parent);
    }

    private static double getEntryTimeAlongAxis(double aMin, double aMax, double bMin, double
            bMax, double relVel) {
        if (aMax <= bMin) {
            if (relVel <= 0) {
                return NO_COLLISION;
            }
            return (bMin - aMax) / relVel;
        } else if (bMax <= aMin) {
            if (relVel >= 0) {
                return NO_COLLISION;
            }
            return (bMax - aMin) / relVel;
        }
        return -Double.MAX_VALUE;
    }

    public static void collideAABBAABB(AABBShape a, AABBShape b, double maxTime, Collision result) {
        double relVelX = a.getDX() - b.getDX(), relVelY = a.getDY() - b.getDY();

        // calculating entry time along the x axis
        double aMaxX = a.halfWidth, aMinX = -a.halfWidth;
        double aX = a.getX(), bX = b.getX();
        double bCenter = bX - aX;
        double bMaxX = bCenter + b.halfWidth;
        double bMinX = bCenter - b.halfWidth;
        double entryTime = getEntryTimeAlongAxis(aMinX, aMaxX, bMinX, bMaxX, relVelX);
        assert entryTime == -Double.MAX_VALUE || entryTime >= 0;
        if (entryTime == NO_COLLISION) {
            result.setNoCollision();
            return;
        }

        // calculating entry time along the y axis
        double aMaxY = a.halfHeight, aMinY = -a.halfHeight;
        double aY = a.getY(), bY = b.getY();
        bCenter = bY - aY;
        double bMaxY = bCenter + b.halfHeight;
        double bMinY = bCenter - b.halfHeight;

        double yEntryTime = getEntryTimeAlongAxis(aMinY, aMaxY, bMinY, bMaxY, relVelY);
        if (yEntryTime == NO_COLLISION) {
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
                result.set(0, 1, 0, a.parent, b.parent);
                return;
            }
            if (relVelY > 0 && aY < bY || relVelY < 0 && bY < aY) {
                result.set(0, 0, 1, a.parent, b.parent);
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
            result.set(entryTime, collisionNormalX, collisionNormalY, a.parent, b.parent);
        } else {
            result.setNoCollision();
        }
    }

    public static void collideCircleAABB(CircleShape a, AABBShape b, double maxTime, Collision
            result) {
        double aX = a.getX();
        double bX = b.getX();
        double bCenterX = bX - aX;
        final double bMaxX = bCenterX + b.halfWidth;
        final double bMinX = bCenterX - b.halfWidth;
        final double relVelX = a.getDX() - b.getDX();
        final double aMax = a.getRadius();
        final double aMin = -aMax;
        double entryTime = getEntryTimeAlongAxis(aMin, aMax, bMinX, bMaxX, relVelX);
        if (entryTime == NO_COLLISION) {
            result.setNoCollision();
            return;
        }

        double aY = a.getY();
        double bY = b.getY();
        double bCenterY = bY - aY;
        final double bMaxY = bCenterY + b.halfHeight;
        final double bMinY = bCenterY - b.halfHeight;
        final double relVelY = a.getDY() - b.getDY();
        double yEntryTime = getEntryTimeAlongAxis(aMin, aMax, bMinY, bMaxY, relVelY);
        double collisionNormalX;
        double collisionNormalY;
        if (yEntryTime > entryTime) {
            if (yEntryTime == NO_COLLISION) {
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
        if (axisEntryTime == NO_COLLISION) {
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
        if (axisEntryTime == NO_COLLISION) {
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
        if (axisEntryTime == NO_COLLISION) {
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
        if (axisEntryTime == NO_COLLISION) {
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
                result.set(0, 1, 0, a.parent, b.parent);
                return;
            }
            if (relVelY > 0 && aY < bY || relVelY < 0 && bY < aY) {
                result.set(0, 0, 1, a.parent, b.parent);
                return;
            }
            result.setNoCollision();
            return;
        }
        if (entryTime <= maxTime && entryTime <= leaveTime && entryTime != -Double.MAX_VALUE) {
            result.set(entryTime, collisionNormalX, collisionNormalY, a.parent, b.parent);
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

    private static void getEntryLeaveAndOverlapTime(PolygonShape a, PolygonShape b, CollisionData
            collisionData) {
//        double tempVelocity = collisionData.getOverlapVelocity(); // TODO why are these here
//        collisionData.setTempOverlapVelocity(collisionData.getOverlapVelocity());
        collisionData.resetOverlapUpdated();
        double relVelX = a.getDX() - b.getDX();
        double relVelY = a.getDY() - b.getDY();
        for (int i = 0; i < a.getNumPoints(); i++) {
            Vector2D normal = a.getNormals()[i];
            collisionData.clearMinMax();
            for (Vector2D point : b.getPoints()) {
                double dist = Vector2D.unitScalarProject(point.getX() + b.getX() - a.getX(),
                        point.getY() + b.getY() - a.getY(), normal);
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

    public static boolean isOverlappingPolyPoly(PolygonShape a, PolygonShape b) {
        final double aX = a.parent.getX(), aY = a.parent.getY();
        final double bX = b.parent.getX(), bY = b.parent.getY();
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

    public static boolean isOverlappingPolyCircle(PolygonShape a, CircleShape b) {
        final double aX = a.parent.getX(), aY = a.parent.getY();
        final double bX = b.parent.getX(), bY = b.parent.getY();
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

    public static boolean isOverlappingPolyAABB(PolygonShape a, AABBShape b) {
        final double aX = a.parent.getX(), aY = a.parent.getY();
        final double bX = b.parent.getX(), bY = b.parent.getY();
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

    public static boolean isOverlappingCircleCircle(CircleShape a, CircleShape b) {
        double deltaX = a.getX() - b.getX();
        double deltaY = a.getY() - b.getY();
        double distSquared = deltaX * deltaX + deltaY * deltaY;
        double radiiSum = a.getRadius() + b.getRadius();
        double radiiSumSquared = radiiSum * radiiSum;
        return distSquared < radiiSumSquared;
    }

    public static boolean isOverlappingCircleAABB(CircleShape a, AABBShape b) {
        final double aX = a.parent.getX(), aY = a.parent.getY();
        final double bX = b.parent.getX(), bY = b.parent.getY();
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

    public static boolean isOverlappingAABBAABB(AABBShape a, AABBShape b) {
        double deltaX = a.getX() - b.getX();
        double deltaY = a.getY() - b.getY();
        double combinedHalfWidth = a.getHalfWidth() + b.getHalfWidth();
        double combinedHalfHeight = a.getHalfHeight() + b.getHalfHeight();
        return Math.abs(deltaX) <= combinedHalfWidth && Math.abs(deltaY) <= combinedHalfHeight;
    }

    public void setParent(Entity parent) {
        this.parent = parent;
    }

    public Entity getParent() {
        return parent;
    }

    public void setParentOffset(double offsetX, double offsetY) {
        parentOffsetX = offsetX;
        parentOffsetY = offsetY;
    }

    public double getX() {
        return parent.getX() + parentOffsetX;
    }

    public double getY() {
        return parent.getY() + parentOffsetY;
    }

    public double getDX() {
        return parent.getDX();
    }

    public double getDY() {
        return parent.getDY();
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

    public abstract void collideWithShape(Shape shape, double maxTime, Collision result);

    public abstract void collideWithCircle(CircleShape circleShape, double maxTime, Collision
            result);

    public abstract void collideWithAABB(AABBShape aabbShape, double maxTime, Collision result);

    public abstract void collideWithPolygon(PolygonShape polygonShape, double maxTime, Collision
            result);

    public abstract boolean isOverlappingShape(Shape shape);

    public abstract boolean isOverlappingPolygon(PolygonShape shape);

    public abstract boolean isOverlappingCircle(CircleShape shape);

    public abstract boolean isOverlappingAABB(AABBShape shape);

    /**
     * Draws the shape using the current foreground color.
     *
     * @param renderer The screen renderer
     */
    public abstract void draw(Renderer renderer);
}

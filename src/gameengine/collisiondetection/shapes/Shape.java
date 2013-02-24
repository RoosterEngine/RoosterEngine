package gameengine.collisiondetection.shapes;

import gameengine.collisiondetection.Collision;
import gameengine.collisiondetection.CollisionType;
import gameengine.entities.Entity;
import gameengine.math.Vector2D;
import gameengine.physics.Material;

import java.awt.*;

/**
 * @author david
 */
public abstract class Shape {
    public static final int TYPE_CIRCLE = 0, TYPE_AABB = 1, TYPE_POLYGON = 2;
    public static final double NO_COLLISION = Double.MAX_VALUE;
    private static CollisionData collisionData = new CollisionData();
    private int collisionType = CollisionType.DEFAULT.ordinal();
    private double boundingHalfWidth, boundingHalfHeight, boundingCenterX, boundingCenterY;
    private double boundingMinX, boundingMaxX, boundingMinY, boundingMaxY;
    protected double x, y, dx, dy, parentOffsetX, parentOffsetY;
    protected double halfWidth, halfHeight;
    // TODO store radius in CircleShape and remove from here
    protected Material material;
    protected double mass;
    protected Entity parent = null;

    public Shape(double x, double y, double halfWidth, double halfHeight, double mass) {
        this(x, y, halfWidth, halfHeight, mass, Material.getDefaultMaterial());
    }

    public Shape(double x, double y, double halfWidth, double halfHeight, double mass, Material material) {
        this.x = x;
        this.y = y;
        this.halfWidth = halfWidth;
        this.halfHeight = halfHeight;
        this.mass = mass;
        this.material = material;
        calculateBoundingBox(0);
        parentOffsetX = 0;
        parentOffsetY = 0;
    }

    public void calculateBoundingBox(double time) {
        boundingMinX = x - halfWidth;
        boundingMaxX = x + halfWidth;
        boundingMinY = y - halfHeight;
        boundingMaxY = y + halfHeight;
        double scale = 1;
        double xTravelDist = dx * time * scale;
        double yTravelDist = dy * time * scale;
        if (xTravelDist > 0) {
            boundingMaxX += xTravelDist;
        } else {
            boundingMinX += xTravelDist;
        }

        if (yTravelDist > 0) {
            boundingMaxY += yTravelDist;
        } else {
            boundingMinY += yTravelDist;
        }
        boundingHalfWidth = (boundingMaxX - boundingMinX) * 0.5;
        boundingHalfHeight = (boundingMaxY - boundingMinY) * 0.5;
        boundingCenterX = boundingMinX + boundingHalfWidth;
        boundingCenterY = boundingMinY + boundingHalfHeight;
    }

    public double getBoundingMinX() {
        return boundingMinX;
    }

    public double getBoundingMaxX() {
        return boundingMaxX;
    }

    public double getBoundingMinY() {
        return boundingMinY;
    }

    public double getBoundingMaxY() {
        return boundingMaxY;
    }

    public void setCollisionType(CollisionType type) {
        collisionType = type.ordinal();
    }

    public int getCollisionType() {
        return collisionType;
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

    public void setMaterial(Material material) {
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

    public void setMass(double mass) {
        this.mass = mass;
    }

    public double getMass() {
        return mass;
    }

    public void updateVelocity(double dx, double dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public void updatePosition(double x, double y) {
        this.x = x + parentOffsetX;
        this.y = y + parentOffsetY;
    }

    public static void collideShapes(Shape a, Shape b, double maxTime, Collision result) {
        double combinedHalfWidths = a.boundingHalfWidth + b.boundingHalfWidth;
        double combinedHalfHeights = a.boundingHalfHeight + b.boundingHalfHeight;

        if (Math.abs(a.boundingCenterX - b.boundingCenterX) > combinedHalfWidths
                || Math.abs(a.boundingCenterY - b.boundingCenterY) > combinedHalfHeights) {
            result.setNoCollision();
            return;
        }

        a.collideWithShape(b, maxTime, result);
    }

    public static void collidePolyPoly(PolygonShape a, PolygonShape b, double maxTime, Collision result) {
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
            result.set(collisionData.getEntryTime(),
                    collisionData.getCollisionNormal(), a.parent, b.parent);
            return;
        }
        result.setNoCollision();
    }

    public static void collideAABBPoly(AABBShape a, PolygonShape b, double maxTime, Collision result) {
        collisionData.clear();
        double relVelX = a.dx - b.dx;
        double relVelY = a.dy - b.dy;

        double minX = a.x - a.halfWidth - b.x, maxX = a.x + a.halfWidth - b.x;
        double minY = a.y - a.halfHeight - b.y, maxY = a.y + a.halfHeight - b.y;
        for (int i = 0; i < b.getNumPoints(); i++) {
            Vector2D normal = b.getNormals()[i];
            double bMin = b.getNormalMins()[i];
            double bMax = b.getNormalMaxs()[i];
            double projVel = Vector2D.unitScalarProject(relVelX, relVelY, normal);
            calcTOIUsingPolyNormalAndSetMinMaxValues(minX, maxX, minY, maxY, normal, bMin, bMax, projVel, collisionData);
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
            result.set(collisionData.getEntryTime(), collisionData.getCollisionNormal(), a.parent, b.parent);
        } else {
            result.setNoCollision();
        }
    }

    private static void calcTOIUsingPolyNormalAndSetMinMaxValues(double minX, double maxX, double minY, double maxY, Vector2D normal, double bMin, double bMax, double projVel, CollisionData collisionData) {
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

    private static void calcCollisionWithBoxNormals(AABBShape a, PolygonShape b, double relVelX, double relVelY, CollisionData collisionData) {
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

    public static void collideCirclePoly(CircleShape a, PolygonShape b, double maxTime, Collision result) {
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
            result.set(0, collisionData.getOverlapNormal(), a.parent, b.parent);
            return;
        }
        if (collisionData.willCollisionHappen(maxTime)) {
            result.set(collisionData.getEntryTime(), collisionData.getCollisionNormal(), a.parent, b.parent);
        } else {
            result.setNoCollision();
        }
    }

    private static void checkCollisionWithLines(CircleShape a, PolygonShape b, CollisionData collisionData) {
        double relVelX = a.dx - b.dx;
        double relVelY = a.dy - b.dy;
        for (int i = 0; i < b.getNumPoints(); i++) {
            Vector2D normal = b.getNormals()[i];
            double aPos = Vector2D.unitScalarProject(a.x - b.x, a.y - b.y, normal);
            double aMin = aPos - a.getRadius();
            double aMax = aPos + a.getRadius();
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

    private static void checkCollisionWithPoints(CircleShape a, PolygonShape b, CollisionData collisionData) {
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
            calcTOIAlongAxis(-a.getRadius(), a.getRadius(), bMin, bMax, projVel, collisionData, dx, dy);
            if (collisionData.isCollisionNotPossible()) {
                return;
            }
        }
    }

    public static void collideCircleCircle(CircleShape a, CircleShape b, double maxTime, Collision result) {
        double combinedVelX = b.dx - a.dx;
        double combinedVelY = b.dy - a.dy;
        if (combinedVelX == 0 && combinedVelY == 0) {
            result.setNoCollision();
            return;
        }
        double distToLineSquared = Vector2D.distToLineSquared(a.x, a.y, b.x, b.y, b.x + combinedVelX, b.y + combinedVelY);
        double radiiSum = a.getRadius() + b.getRadius();
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
            result.set(0, velocity, a.parent, b.parent);
            return;
        }
        velocity.unit();
        double centerAProjectedOnVelocity = Vector2D.unitScalarProject(a.x - b.x, a.y - b.y, velocity);
        double subLength = Math.sqrt(radiiSumSquared - distToLineSquared); // a = sqrt(c^2 - b^2) pythagoras
        velocity.scale(centerAProjectedOnVelocity - subLength);
        velocity.add(b.x - a.x, b.y - a.y); // this is now the collision normal
        velocity.divide(radiiSum); // normalizes the vector
        result.set(travelTime, velocity, a.parent, b.parent);
    }

    public static void collideAABBAABB(AABBShape a, AABBShape b, double maxTime, Collision result) {
        collisionData.clear();

        double relVelX = a.dx - b.dx, relVelY = a.dy - b.dy;

        double aMaxX = a.halfWidth;
        double aMinX = -a.halfWidth;
        double aMaxY = a.halfHeight;
        double aMinY = -a.halfHeight;

        double bMaxX = b.x + b.halfWidth - a.x;
        double bMinX = b.x - b.halfWidth - a.x;
        double bMaxY = b.y + b.halfHeight - a.y;
        double bMinY = b.y - b.halfHeight - a.y;

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
            calcOverlapNormalBox(aMinX, aMaxX, aMinY, aMaxY, bMinX, bMaxX, bMinY, bMaxY, relVelX, relVelY, collisionData);
            if (collisionData.getOverlapVelocity() < 0) {
                result.set(0, collisionData.getOverlapNormal(), a.parent, b.parent);
                return;
            }
        }

        if (collisionData.willCollisionHappen(maxTime)) {
            result.set(collisionData.getEntryTime(), collisionData.getCollisionNormal(), a.parent, b.parent);
        } else {
            result.setNoCollision();
        }
    }

    public static void collideCircleAABB(CircleShape a, AABBShape b, double maxTime, Collision result) {
        collisionData.clear();
        double relVelX = a.dx - b.dx;
        double relVelY = a.dy - b.dy;
        double bMaxX = b.x + b.halfWidth;
        double bMinX = b.x - b.halfWidth;
        double bMaxY = b.y + b.halfHeight;
        double bMinY = b.y - b.halfHeight;
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
            double radius = a.getRadius();
            double minRadius = -radius;
            calcOverlapNormalBox(minRadius, radius, minRadius, radius, bMinX - a.x, bMaxX - a.x, bMinY - a.y,
                    bMaxY - a.y, relVelX, relVelY, collisionData);
            if (collisionData.getOverlapVelocity() < 0) {
                result.set(0, collisionData.getOverlapNormal(), a.parent, b.parent);
                return;
            }
        }
        if (collisionData.willCollisionHappen(maxTime)) {
            result.set(collisionData.getEntryTime(), collisionData.getCollisionNormal(), a.parent, b.parent);
            return;
        }
        result.setNoCollision();
    }

    private static void getEntryLeaveAndOverlapTime(PolygonShape a, PolygonShape b, CollisionData collisionData) {
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

    private static void calcTOIAlongAxis(double aMin, double aMax, double bMin, double bMax, double vel, CollisionData collisionData, Vector2D axis) {
        calcTOIAlongAxis(aMin, aMax, bMin, bMax, vel, collisionData, axis.getX(), axis.getY());
    }

    private static void calcTOIAlongAxis(double aMin, double aMax, double bMin, double bMax, double vel, CollisionData collisionData, double axisX, double axisY) {
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

    private static void calcOverlapNormalBox(double aMinX, double aMaxX, double aMinY, double aMaxY, double bMinX, double bMaxX, double bMinY, double bMaxY, double velX, double velY, CollisionData collisionData) {
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

    private static void calcCircleBoxTOIBeforeCheckingPoints(CircleShape a, double minX, double maxX, double minY, double maxY, double velX, double velY, CollisionData collisionData) {
        calcTOIAlongAxis(-a.getRadius(), a.getRadius(), minX - a.x, maxX - a.x, velX, collisionData, 1, 0);
        if (collisionData.isCollisionNotPossible()) {
            return;
        }
        calcTOIAlongAxis(-a.getRadius(), a.getRadius(), minY - a.y, maxY - a.y, velY, collisionData, 0, 1);
    }

    private static void calculateCircleBoxPointsTOI(CircleShape a, double bMinX, double bMaxX, double bMinY, double bMaxY, double relVelX, double relVelY, CollisionData collisionData) {
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

    private static void calculateCirclePointTOI(CircleShape a, double bMinX, double bMaxX, double bMinY, double bMaxY, double x, double y, double relVelX, double relVelY, CollisionData collisionData) {
        double dx = x - a.x;
        double dy = y - a.y;
        double dist = Math.sqrt(dx * dx + dy * dy);
        dx /= dist;
        dy /= dist;
        collisionData.clearMinMax();
        calculateCircleAABBMinAndMax(a, bMinX, bMaxX, bMinY, bMaxY, dx, dy, collisionData);
        double radius = a.getRadius();
        double projVel = Vector2D.unitScalarProject(relVelX, relVelY, dx, dy);
        calcTOIAlongAxis(-radius, radius, collisionData.getMin(), collisionData.getMax(), projVel, collisionData, dx, dy);
    }

    private static void calculateCircleAABBMinAndMax(CircleShape a, double bMinX, double bMaxX, double bMinY, double bMaxY, double normalX, double normalY, CollisionData collisionData) {
        double projDist = Vector2D.unitScalarProject(bMinX - a.x, bMinY - a.y, normalX, normalY);
        collisionData.updateMinMax(projDist);

        projDist = Vector2D.unitScalarProject(bMaxX - a.x, bMinY - a.y, normalX, normalY);
        collisionData.updateMinMax(projDist);

        projDist = Vector2D.unitScalarProject(bMaxX - a.x, bMaxY - a.y, normalX, normalY);
        collisionData.updateMinMax(projDist);

        projDist = Vector2D.unitScalarProject(bMinX - a.x, bMaxY - a.y, normalX, normalY);
        collisionData.updateMinMax(projDist);
    }

    private static double getOverlapTime(double actualBPos, double actualAPos, double relativeVel) {
        double dist = actualBPos - actualAPos;
        return dist / relativeVel;
    }

    public abstract void collideWithShape(Shape shape, double maxTime, Collision result);

    public abstract void collideWithCircle(CircleShape circleShape, double maxTime, Collision result);

    public abstract void collideWithAABB(AABBShape aabbShape, double maxTime, Collision result);

    public abstract void collideWithPolygon(PolygonShape polygonShape, double maxTime, Collision result);


    public abstract int getShapeType();

    public abstract void draw(Graphics2D g, Color color);

    public void drawBoundingBoxes(Graphics2D g, Color color) {
        g.setColor(color);
        double width = boundingHalfWidth * 2;
        double height = boundingHalfHeight * 2;
        g.drawRect((int)boundingMinX, (int)boundingMinY, (int)width, (int) height);
    }

    @Override
    public String toString() {
        return "x: " + x + " y: " + y
                + "\nminX: " + boundingMinX + " minY: " + boundingMinY
                + "\nmaxX: " + boundingMaxX + " maxY: " + boundingMaxY;
    }

    public double getBoundingCenterX() {
        return boundingCenterX;
    }

    public double getBoundingHalfWidth() {
        return boundingHalfWidth;
    }

    public double getBoundingCenterY() {
        return boundingCenterY;
    }

    public double getBoundingHalfHeight() {
        return boundingHalfHeight;
    }
}

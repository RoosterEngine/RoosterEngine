package bricklets;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 *
 * @author david
 */
public abstract class Shape {
    public static final int TYPE_CIRCLE = 0, TYPE_AA_BOUNDING_BOX = 1, TYPE_O_BOUNDING_BOX = 2, TYPE_POLYGON = 3;
    public static final double NO_COLLISION = Double.MAX_VALUE;
    private static CollisionData collisionData = new CollisionData();
    protected double x, y, dx, dy, radius, parentOffsetX, parentOffsetY;
    protected Entity parentEntity;
    public Shape(double x, double y, double dx, double dy, double radius, Entity parentEntity){
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
        this.radius = radius;
        this.parentEntity = parentEntity;
        parentOffsetX = parentEntity.getX() - x;
        parentOffsetY = parentEntity.getY() - y;
    }
    
    public double getX(){
        return x;
    }
    
    public double getY(){
        return y;
    }
    
    public double getDX(){
        return dx;
    }
    
    public double getDY(){
        return dy;
    }
    
    public double getRadius(){
        return radius;
    }
    
    public Entity getParentEntity() {
        return parentEntity;
    }
    
    public void update(){
        x = parentEntity.getX() + parentOffsetX;
        y = parentEntity.getY() + parentOffsetY;
        dx = parentEntity.getDX();
        dy = parentEntity.getDY();
    }

    public static void collidePolyPoly(Polygon a, Polygon b, double maxTime, Collision result){
        if(!willBoundingCollide(a, b, maxTime)){
            result.setNoCollision();
            return;
        }
        collisionData.clear();
        getEntryLeaveAndOverlapTime(a, b, collisionData);
        if(collisionData.isCollisionNotPossible()){
            result.setNoCollision();
            return;
        }
        getEntryLeaveAndOverlapTime(b, a, collisionData);
        if(collisionData.isCollisionNotPossible()){
            result.setNoCollision();
            return;
        }
        if(collisionData.isIntersectingAndTravellingTowardsEachOther()){
            result.set(0, collisionData.getOverlapNormal(), a, b);
            return;
        }
        if(collisionData.willCollisionHappen(maxTime)){
            result.set(collisionData.getEntryTime(), collisionData.getCollisionNormal(), a, b);
            return;
        }
        result.setNoCollision();
        return;
    }
    
    public static void collideAABBPoly(AABBShape a, Polygon b, double maxTime, Collision result){
        collisionData.clear();
        double relVelX = a.dx - b.dx;
        double relVelY = a.dy - b.dy;

        double minX = a.x - a.getHalfWidth() - b.x, maxX = a.x + a.getHalfWidth() - b.x;
        double minY = a.y - a.getHalfHeight() - b.y, maxY = a.y + a.getHalfHeight() - b.y;
        for(int i = 0; i < b.getNumPoints(); i++){
            Vector2D normal = b.getNormals()[i];
            double bMin = b.getNormalMins()[i];
            double bMax = b.getNormalMaxs()[i];
            double projVel = Vector2D.unitScalarProject(relVelX, relVelY, normal);
            double TOI = getTOIUsingPolyNormalAndSetMinMaxValues(minX, maxX, minY, maxY, normal, bMin, bMax, projVel);
            if(TOI == NO_COLLISION){
                result.setNoCollision();
                return;
            }
            collisionData.updateEntryTime(TOI, normal);
            collisionData.updateLeaveTime(getLeaveTimeAlongAxis(collisionData.getMin(), collisionData.getMax(), bMin, bMax, projVel));
            double dist = bMax - collisionData.getMin();
            collisionData.updateTempOverlapData(dist, projVel, normal.getX(), normal.getY());
        }

        calcCollisionWithBoxNormals(a, b, relVelX, relVelY, collisionData);
        collisionData.updateOverlapData();
        if(collisionData.isIntersectingAndTravellingTowardsEachOther()){
            result.set(0, collisionData.getOverlapNormal(), a, b);
        }else if(collisionData.willCollisionHappen(maxTime)){
            result.set(collisionData.getEntryTime(), collisionData.getCollisionNormal(), a, b);
        }else{
            result.setNoCollision();
        }
    }

    private static double getTOIUsingPolyNormalAndSetMinMaxValues(double minX, double maxX, double minY, double maxY,
                                                                  Vector2D normal, double bMin, double bMax,
                                                                  double projVel){
        collisionData.clearMinMax();
        double projDist = Vector2D.unitScalarProject(minX, minY, normal);
        collisionData.updateMinMax(projDist);
        projDist = Vector2D.unitScalarProject(maxX, minY, normal);
        collisionData.updateMinMax(projDist);
        projDist = Vector2D.unitScalarProject(maxX, maxY, normal);
        collisionData.updateMinMax(projDist);
        projDist = Vector2D.unitScalarProject(minX, maxY, normal);
        collisionData.updateMinMax(projDist);
        return getTOIAlongAxis(collisionData.getMin(), collisionData.getMax(), bMin, bMax, projVel);
    }

    private static void calcCollisionWithBoxNormals(AABBShape a, Polygon b, double relVelX, double relVelY, CollisionData collisionData) {
        double bMinX = Double.MAX_VALUE;
        double bMaxX = -Double.MAX_VALUE;
        double bMinY = Double.MAX_VALUE;
        double bMaxY = -Double.MAX_VALUE;
        for(int i = 0; i < b.getNumPoints(); i++){
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
        double TOI = getTOIAlongAxis(minX, maxX, bMinX, bMaxX, relVelX);
        collisionData.updateEntryTime(TOI, 1, 0);
        collisionData.updateLeaveTime(getLeaveTimeAlongAxis(minX, maxX, bMinX, bMaxX, relVelX));
        TOI = getTOIAlongAxis(minY, maxY, bMinY, bMaxY, relVelY);
        collisionData.updateEntryTime(TOI, 0, 1);
        collisionData.updateLeaveTime(getLeaveTimeAlongAxis(minY, maxY, bMinY, bMaxY, relVelY));
        //In the following code block the velocities are reversed because now the box is stationary and the polygon is moving
        collisionData.updateTempOverlapData(bMaxX - minX, relVelX, -1, 0);
        collisionData.updateTempOverlapData(maxX - bMinX, -relVelX, 1, 0);
        collisionData.updateTempOverlapData(bMaxY - minY, relVelY, 0, -1);
        collisionData.updateTempOverlapData(maxY - bMinY, -relVelY, 0, 1);
    }

    public static void collideCirclePoly(Shape a, Polygon b, double maxTime, Collision result){
        collisionData.clear();
        checkCollisionWithLines(a, b, collisionData);
        if(collisionData.isCollisionNotPossible()){
            result.setNoCollision();
            return;
        }
        checkCollisionWithPoints(a, b, collisionData);
        if(collisionData.isCollisionNotPossible()){
            result.setNoCollision();
            return;
        }
        if(!collisionData.wasEntryTimeUpdated()){
            // a and b are overlapping
            double velTowardsLine = getVelocityTowardsTheLineWhereDistanceDividedBySpeedIsSmallest(a, b, collisionData);
            if(velTowardsLine < 0){
                result.set(0, collisionData.getCollisionNormal(), a, b);
                return;
            }
        }
        if(collisionData.willCollisionHappen(maxTime)){
            result.set(collisionData.getEntryTime(), collisionData.getCollisionNormal(), a, b);
        }else{
            result.setNoCollision();
        }
    }

    private static void checkCollisionWithLines(Shape a, Polygon b, CollisionData collisionData) {
        double relVelX = a.dx - b.dx;
        double relVelY = a.dy - b.dy;
        for(int i = 0; i < b.getNumPoints(); i++){
            Vector2D normal = b.getNormals()[i];
            double aPos = Vector2D.unitScalarProject(a.x - b.x, a.y - b.y, normal);
            double aMin = aPos - a.radius;
            double aMax = aPos + a.radius;
            double bMin = b.getNormalMins()[i];
            double bMax = b.getNormalMaxs()[i];
            double projVel = Vector2D.unitScalarProject(relVelX, relVelY, normal);
            double TOI = getTOIAlongAxis(aMin, aMax, bMin, bMax, projVel);
            if(TOI == NO_COLLISION){
                collisionData.setNoCollision();
                return;
            }
            collisionData.updateEntryTime(TOI, normal);
            collisionData.updateLeaveTime(getLeaveTimeAlongAxis(aMin, aMax, bMin, bMax, projVel));
        }
    }

    private static void checkCollisionWithPoints(Shape a, Polygon b, CollisionData collisionData) {
        double relVelX = a.dx - b.dx;
        double relVelY = a.dy - b.dy;
        for(Vector2D vertex: b.getPoints()){
            // checking for collision with points
            double dx = vertex.getX() + b.x - a.x;
            double dy = vertex.getY() + b.y - a.y;
            double dist = Math.sqrt(dx * dx + dy * dy);
            dx /= dist;
            dy /= dist;
            double bMin = Double.MAX_VALUE;
            double bMax = -Double.MAX_VALUE;
            for(Vector2D point: b.getPoints()){
                double projDist = Vector2D.unitScalarProject(point.getX() + b.x - a.x, point.getY() + b.y - a.y, dx, dy);
                bMin = Math.min(bMin, projDist);
                bMax = Math.max(bMax, projDist);
            }
            double projVel = Vector2D.unitScalarProject(relVelX, relVelY, dx, dy);
            double TOI = getTOIAlongAxis(-a.radius, a.radius, bMin, bMax, projVel);
            if(TOI == NO_COLLISION){
                collisionData.setNoCollision();
                return;
            }
            collisionData.updateEntryTime(TOI, dx, dy);
            collisionData.updateLeaveTime(getLeaveTimeAlongAxis(-a.radius, a.radius, bMin, bMax, projVel));
        }
    }

    private static double getVelocityTowardsTheLineWhereDistanceDividedBySpeedIsSmallest(Shape a, Polygon b, CollisionData collisionData) {
        double minTOI = Double.MAX_VALUE;
        double velOnNormal = 0;
        double relVelX = a.dx - b.dx;
        double relVelY = a.dy - b.dy;
        Vector2D lastPoint = b.getPoints()[b.getNumPoints() - 1];
        double prevX = lastPoint.getX() + b.x;
        double prevY = lastPoint.getY() + b.y;
        for(int i = 0; i < b.getNumPoints(); i++){
            Vector2D normal = b.getNormals()[i];
            Vector2D point = b.getPoints()[i];
            double x = point.getX() + b.x;
            double y = point.getY() + b.y;
            double projVel = Vector2D.unitScalarProject(relVelX, relVelY, normal);
            double distSquared = Vector2D.distToLineSquared(
                    a.x - normal.getX() * a.radius, a.y - normal.getY() * a.radius, prevX, prevY, x, y);
            double timeSquared = distSquared /(projVel * projVel);
            if(timeSquared < minTOI){
                minTOI = timeSquared;
                collisionData.setCollisionNormal(normal);
                velOnNormal = projVel;
            }
            prevX = x;
            prevY = y;
        }
        return velOnNormal;
    }

    public static void collideCircleCircle(Shape a, Shape b, double maxTime, Collision result){
        double combinedVelX = b.dx - a.dx;
        double combinedVelY = b.dy - a.dy;
        if(combinedVelX == 0 && combinedVelY == 0){
            result.setNoCollision();
            return;
        }
        double distToLineSquared = Vector2D.distToLineSquared(a.x, a.y, b.x, b.y, b.x + combinedVelX, b.y + combinedVelY);
        double radiiSum = a.radius + b.radius;
        double radiiSumSquared = radiiSum * radiiSum;
        if(distToLineSquared > radiiSumSquared){
            result.setNoCollision();
            return;
        }
        Vector2D velocity = result.getCollisionNormal().set(combinedVelX, combinedVelY);
        double deltaX = a.x - b.x;
        double deltaY = a.y - b.y;
        double distBetween = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        double projVelocity = velocity.scalarProject(deltaX, deltaY, distBetween);
        if(projVelocity <= 0){
            result.setNoCollision();
            return;
        }
        double travelDist = distBetween - radiiSum;
        double travelTime = travelDist / projVelocity;
        if(travelTime > maxTime){
            result.setNoCollision();
            return;
        }
        if(travelTime < 0){
            velocity.set(deltaX / distBetween, deltaY / distBetween);
            result.set(0, velocity, a, b);
            return;
        }
        velocity.unit();
        double centerAProjectedOnVelocity = Vector2D.unitScalarProject(a.x - b.x, a.y - b.y, velocity);
        double subLength = Math.sqrt(radiiSumSquared - distToLineSquared);
        velocity.scale(centerAProjectedOnVelocity - subLength);
        velocity.add(b.x - a.x, b.y - a.y).unit();
        result.set(travelTime, velocity, a, b);
    }
    
    public static void collideAABBAABB(AABBShape a, AABBShape b, double maxTime, Collision result){
        double relativeVelX = a.dx - b.dx, relativeVelY = a.dy - b.dy;
        double maxEntryTime, minLeaveTime = Double.MAX_VALUE;
        double aMaxX = a.x + a.getHalfWidth(), aMinX = a.x - a.getHalfWidth(), aMaxY = a.y + a.getHalfHeight(), aMinY = a.y - a.getHalfHeight();
        double bMaxX = b.x + b.getHalfWidth(), bMinX = b.x - b.getHalfWidth(), bMaxY = b.y + b.getHalfHeight(), bMinY = b.y - b.getHalfHeight();
        
        maxEntryTime = getTOIAlongAxis(aMinX, aMaxX, bMinX, bMaxX, relativeVelX);
        if (maxEntryTime == NO_COLLISION){
            result.setNoCollision();
            return;
        }else if(maxEntryTime != -Double.MAX_VALUE){
            result.getCollisionNormal().set(1, 0);
        }
        minLeaveTime = getLeaveTimeAlongAxis(aMinX, aMaxX, bMinX, bMaxX, relativeVelX);
        
        double time = getTOIAlongAxis(aMinY, aMaxY, bMinY, bMaxY, relativeVelY);
        if(time == NO_COLLISION){
            result.setNoCollision();
            return;
        }else if(time > maxEntryTime){
            maxEntryTime = time;
            result.getCollisionNormal().set(0, 1);
        }
        minLeaveTime = Math.min(minLeaveTime, getLeaveTimeAlongAxis(aMinY, aMaxY, bMinY, bMaxY, relativeVelY));
        if(maxEntryTime == -Double.MAX_VALUE || maxEntryTime > maxTime || maxEntryTime > minLeaveTime){
            result.setNoCollision();
            return;
        }
        result.set(maxEntryTime, result.getCollisionNormal(), a, b);
    }
    
    public static void collideCircleAABB(Shape a, AABBShape b, double maxTime, Collision result){
        collisionData.clear();
        double relVelX = a.dx - b.dx;
        double relVelY = a.dy - b.dy;
        double bMaxX = b.x + b.getHalfWidth();
        double bMinX = b.x - b.getHalfWidth();
        double bMaxY = b.y + b.getHalfHeight();
        double bMinY = b.y - b.getHalfHeight();
        calcCircleBoxTOIBeforeCheckingPoints(a, bMinX, bMaxX, bMinY, bMaxY, relVelX, relVelY, collisionData);
        if(collisionData.isCollisionNotPossible()){
            result.setNoCollision();
            return;
        }
        calculateCircleBoxPointsTOI(a, bMinX, bMaxX, bMinY, bMaxY, relVelX, relVelY, collisionData);
        if(collisionData.isCollisionNotPossible()){
            result.setNoCollision();
            return;
        }
        if(!collisionData.hasEntryTimeBeenUpdated()){
            calcOverlapNormal(a, bMinX, bMaxX, bMinY, bMaxY, relVelX, relVelY, collisionData);
            if(collisionData.getOverlapVelocity() < 0){
                result.set(0, collisionData.getOverlapNormal(), a, b);
                return;
            }
        }
        if(collisionData.willCollisionHappen(maxTime)){
            result.set(collisionData.getEntryTime(), collisionData.getCollisionNormal(), a, b);
            return;
        }
        result.setNoCollision();
    }

    private static void getEntryLeaveAndOverlapTime(Polygon a, Polygon b, CollisionData collisionData){
        collisionData.setTempOverlapVelocity(collisionData.getOverlapVelocity());
        collisionData.resetOverlapUpdated();
        double relVelX = a.dx - b.dx;
        double relVelY = a.dy - b.dy;
        for(int i = 0; i < a.getNumPoints(); i++){
            Vector2D normal = a.getNormals()[i];
            double bMin = Double.MAX_VALUE;
            double bMax = -Double.MAX_VALUE;
            for(Vector2D point: b.getPoints()){
                double dist = Vector2D.unitScalarProject(point.getX() + b.x - a.x, point.getY() + b.y - a.y, normal);
                bMin = Math.min(bMin, dist);
                bMax = Math.max(bMax, dist);
            }
            double aMin = a.getNormalMins()[i];
            double aMax = a.getNormalMaxs()[i];
            double projVel = Vector2D.unitScalarProject(relVelX, relVelY, normal);
            double time = getTOIAlongAxis(aMin, aMax, bMin, bMax, projVel);
            if(time == NO_COLLISION){
                collisionData.setNoCollision();
                return;
            }
            if(time >= collisionData.getEntryTime()){
                collisionData.setEntryTime(time);
                collisionData.setCollisionNormal(normal);
            }
            collisionData.setLeaveTime(
                    Math.min(collisionData.getLeaveTime(), getLeaveTimeAlongAxis(aMin, aMax, bMin, bMax, projVel)));
            double dist = bMax - aMin;
            if(dist > 0){
                time = dist / Math.abs(projVel);
                if(time < collisionData.getOverlapTime()){
                    collisionData.setTempOverlapData(normal, projVel, time);
                }
            }
        }
        collisionData.updateOverlapData();
    }
    
    private static double getLeaveTimeAlongAxis(double aMin, double aMax, double bMin, double bMax, double vel){
        double leaveTime = Double.MAX_VALUE;
        if(vel > 0){
            leaveTime = Math.min(leaveTime, (bMax - aMin) / vel);
        }else if(vel < 0){
            leaveTime = Math.min(leaveTime, (aMax - bMin) / -vel);
        }
        return leaveTime;
    }
    
    /**
     * -Double.MAX_VALUE is returned if already overlapping
     * NO_COLLISION is returned if a collision will not happen; 
     * assuming that a is moving and b is staying still;
     * @param aMin
     * @param aMax
     * @param bMin
     * @param bMax
     * @param vel
     * @return 
     */
    private static double getTOIAlongAxis(double aMin, double aMax, double bMin, double bMax, double vel){
        double travelTime = -Double.MAX_VALUE;
//        if(aMin <= bMin){
        if(aMax <= bMin){
            if(vel <= 0){
                return NO_COLLISION;
            }
            travelTime = (bMin - aMax) / vel;
//        }else if(aMax >= bMax){
        }else if(aMin >= bMax){
            if(vel >= 0){
                return NO_COLLISION;
            }
            travelTime = (bMax - aMin) / vel;
        }
        return travelTime;
    }

    private static void calcOverlapNormal(Shape a, double minX, double maxX, double minY, double maxY,
                                          double velX, double velY, CollisionData collisionData){
        double dist = a.x + a.radius - minX;
        collisionData.updateTempOverlapData(dist, -velX, -1, 0);
        dist = maxX - a.x + a.radius;
        collisionData.updateTempOverlapData(dist, velX, 1, 0);
        dist = a.y + a.radius - minY;
        collisionData.updateTempOverlapData(dist, -velY, 0, -1);
        dist = maxY - a.y + a.radius;
        collisionData.updateTempOverlapData(dist, velY, 0, 1);
        collisionData.updateOverlapData();
    }

    private static void calcCircleBoxTOIBeforeCheckingPoints(Shape a, double minX, double maxX, double minY, double maxY,
                                                             double velX, double velY, CollisionData collisionData){
        double time = getTOIAlongAxis(-a.radius, a.radius, minX - a.x, maxX - a.x, velX);
        if(time == NO_COLLISION){
            collisionData.setNoCollision();
            return;
        }
        collisionData.updateEntryTime(time, 1, 0);
        collisionData.updateLeaveTime(getLeaveTimeAlongAxis(-a.radius, a.radius, minX - a.x, maxX - a.y, velX));

        time = getTOIAlongAxis(-a.radius, a.radius, minY - a.y, maxY - a.y, velY);
        if(time == NO_COLLISION){
            collisionData.setNoCollision();
            return;
        }
        collisionData.updateEntryTime(time, 0, 1);
        collisionData.updateLeaveTime(getLeaveTimeAlongAxis(-a.radius, a.radius, minY - a.y, maxX - a.y, velY));
    }

    private static void calculateCircleBoxPointsTOI(Shape a, double bMinX, double bMaxX, double bMinY, double bMaxY,
                                                    double relVelX, double relVelY, CollisionData collisionData){
        calculateCirclePointTOI(a, bMinX, bMaxX, bMinY, bMaxY, bMinX, bMinY, relVelX, relVelY, collisionData);
        if(collisionData.isCollisionNotPossible()){
            return;
        }
        calculateCirclePointTOI(a, bMinX, bMaxX, bMinY, bMaxY, bMaxX, bMinY, relVelX, relVelY, collisionData);
        if(collisionData.isCollisionNotPossible()){
            return;
        }
        calculateCirclePointTOI(a, bMinX, bMaxX, bMinY, bMaxY, bMaxX, bMaxY, relVelX, relVelY, collisionData);
        if(collisionData.isCollisionNotPossible()){
            return;
        }
        calculateCirclePointTOI(a, bMinX, bMaxX, bMinY, bMaxY, bMinX, bMaxY, relVelX, relVelY, collisionData);
    }

    private static void calculateCirclePointTOI(Shape a, double bMinX, double bMaxX, double bMinY, double bMaxY,
                                                double x, double y, double relVelX, double relVelY,
                                                CollisionData collisionData){
        double dx = x - a.x;
        double dy = y - a.y;
        double dist = Math.sqrt(dx * dx + dy * dy);
        dx /= dist;
        dy /= dist;
        collisionData.clearMinMax();
        calculateCircleAABBMinAndMax(a, bMinX, bMaxX, bMinY, bMaxY, dx, dy, collisionData);
        double projVel = Vector2D.unitScalarProject(relVelX, relVelY, dx, dy);
        double TOI = getTOIAlongAxis(-a.radius, a.radius, collisionData.getMin(), collisionData.getMax(), projVel);
        if(TOI == NO_COLLISION){
            collisionData.setNoCollision();
            return;
        }
        collisionData.updateEntryTime(TOI, dx, dy);
        collisionData.updateLeaveTime(
                getLeaveTimeAlongAxis(-a.radius, a.radius, collisionData.getMin(), collisionData.getMax(), projVel));
    }

    private static void calculateCircleAABBMinAndMax(Shape a, double bMinX, double bMaxX, double bMinY, double bMaxY,
                                                     double normalX, double normalY, CollisionData collisionData){
        double projDist = Vector2D.unitScalarProject(bMinX - a.x, bMinY - a.y, normalX, normalY);
        collisionData.updateMinMax(projDist);

        projDist = Vector2D.unitScalarProject(bMaxX - a.x, bMinY - a.y, normalX, normalY);
        collisionData.updateMinMax(projDist);

        projDist = Vector2D.unitScalarProject(bMaxX - a.x, bMaxY - a.y, normalX, normalY);
        collisionData.updateMinMax(projDist);

        projDist = Vector2D.unitScalarProject(bMinX - a.x, bMaxY - a.y, normalX, normalY);
        collisionData.updateMinMax(projDist);
    }

    private static double getCirclePointTOI(Shape a, Shape b, double px, double py, double pVelX, double pVelY, double velLength, Collision result){
        double distToVelRaySquared = Vector2D.distToLineSquared(a.x, a.y, px, py, px + pVelX, py + pVelY);
        double radiusSquared = a.radius * a.radius;
        if(distToVelRaySquared <= radiusSquared){
            // in the path of the velocity ray
            double deltaX = a.x - px;
            double deltaY = a.y - py;
            double projLength = Vector2D.scalarProject(deltaX, deltaY, pVelX, pVelY, velLength);
            if(projLength >= 0){
                // travelling towards each other
                double subLength = Math.sqrt(radiusSquared - distToVelRaySquared);
                double travelDist = projLength - subLength;
                double travelTime = travelDist / velLength;
                if(travelTime <= result.getTimeToCollision()){
                    double distOverVel = travelDist / velLength;
                    result.getCollisionNormal().set(a.x - px - pVelX * distOverVel, a.y - py - pVelY * distOverVel).unit();
                    result.set(travelTime, result.getCollisionNormal(), a, b);
                    return travelTime;
                }
            }
        }
        return Double.MAX_VALUE;
        
    }
    
    private static boolean willBoundingCollide(Polygon a, Polygon b, double maxTime){
        if(!a.isUsingBoundingBox() && !b.isUsingBoundingBox()){
            return willBoundingCircleCircleCollide(a, b, maxTime);
        }else if(!a.isUsingBoundingBox() && b.isUsingBoundingBox()){
            return willBoundingCircleBoxCollide(a, b, maxTime);
        }else if(a.isUsingBoundingBox() && !b.isUsingBoundingBox()){
            return willBoundingCircleBoxCollide(b, a, maxTime);
        }else if(a.isUsingBoundingBox() && a.isUsingBoundingBox()){
            return willBoundingBoxBoxCollide(a, b, maxTime);
        }
        return false;
    }
    
    private static boolean willBoundingCircleCircleCollide(Polygon a, Polygon b, double maxTime){
        double combinedVelX = b.dx - a.dx;
        double combinedVelY = b.dy - a.dy;
        if(combinedVelX == 0 && combinedVelY == 0){
            // the speed relative to each other is 0;
            return false;
        }
        double distToLineSquared = Vector2D.distToLineSquared(a.x, a.y, b.x, b.y, b.x + combinedVelX, b.y + combinedVelY);
        double radiiSum = a.radius + b.radius;
        if(distToLineSquared > radiiSum * radiiSum){
            // not in the path of the combined velocity
            return false;
        }
        double deltaX = a.x - b.x;
        double deltaY = a.y - b.y;
        double distBetween = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        distBetween -= radiiSum;
        if(distBetween <= 0){
            return true;
        }
        double projVel = Vector2D.scalarProject(combinedVelX, combinedVelY, deltaX, deltaY, distBetween);
        if(projVel < 0){
            // travelling away from each other
            return false;
        }
        double travelTime = distBetween / projVel;
        return travelTime <= maxTime;
    }
        
    /**
     * @param a the polygon with a bounding circle
     * @param b the polygon with a bounding box
     * @param maxTime
     * @return 
     */
    private static boolean willBoundingCircleBoxCollide(Polygon a, Polygon b, double maxTime){
        double relativeVelX = a.dx - b.dx, relativeVelY = a.dy - b.dy;
        double maxOverlapTime = getBoxOverlapTime(a.x, a.y, a.radius, a.radius, b.x, b.y, b.getMaxX(), b.getMaxY(), relativeVelX, relativeVelY);
        if(maxOverlapTime == NO_COLLISION){
            return false;
        }
        
        //checking collision with points
        relativeVelX *= -1;
        relativeVelY *= -1;
        double radiusSquared = a.radius * a.radius;
        maxOverlapTime = Math.min(getTOICirclePoint(a.x, a.y, radiusSquared, b.getMinX(), b.getMinY(), relativeVelX, relativeVelY), maxOverlapTime);
        maxOverlapTime = Math.min(getTOICirclePoint(a.x, a.y, radiusSquared, b.getMaxX(), b.getMinY(), relativeVelX, relativeVelY), maxOverlapTime);
        maxOverlapTime = Math.min(getTOICirclePoint(a.x, a.y, radiusSquared, b.getMaxX(), b.getMaxY(), relativeVelX, relativeVelY), maxOverlapTime);
        maxOverlapTime = Math.min(getTOICirclePoint(a.x, a.y, radiusSquared, b.getMinX(), b.getMaxY(), relativeVelX, relativeVelY), maxOverlapTime);
        return maxOverlapTime <= maxTime;
    }
    
    private static boolean willBoundingBoxBoxCollide(Polygon a, Polygon b, double maxTime){
        double relativeVelX = a.dx - b.dx, relativeVelY = a.dy - b.dy;
        double maxOverlapTime = getBoxOverlapTime(a.x, a.y, a.getMaxX(), a.getMaxY(), b.x, b.y, b.getMaxX(), b.getMaxY(), relativeVelX, relativeVelY);
        return maxOverlapTime < maxTime;
    }
    
    private static double getBoxOverlapTime(double aX, double aY, double aHalfX, double aHalfY, double bX, double bY, double bHalfX, double bHalfY, double relativeVelX, double relativeVelY){
        double overlapTime;
        if(relativeVelX > 0){
            overlapTime = getOverlapTime(bX - bHalfX, aX + aHalfX, relativeVelX);
        }else if(relativeVelX < 0){
            overlapTime = getOverlapTime(bX + bHalfX, aX - aHalfX, relativeVelX);
        }else{
            if(aX - aHalfX > bX + bHalfX || aX + aHalfX < bX - bHalfX){
                return NO_COLLISION;
            }
            overlapTime = 0;
        }
        if(relativeVelY > 0){
            overlapTime = Math.max(getOverlapTime(bY - bHalfY, aY + aHalfY, relativeVelY), overlapTime);
        }else if(relativeVelY < 0){
            overlapTime = Math.max(getOverlapTime(bY + bHalfY, aY - aHalfY, relativeVelY), overlapTime);
        }else{
            if(aY - aHalfY > bY + bHalfY || aY + aHalfY < bY - bHalfY){
                return NO_COLLISION;
            }
        }
        return overlapTime;
    }
    
    private static  double getTOICirclePoint(double circleX, double circleY, double radiusSquared, double pointX, double pointY, double pointVelX, double pointVelY){
        double distToLineSquared = Vector2D.distToLineSquared(circleX, circleY, pointX, pointY, pointX + pointVelX, pointY + pointVelY);
        if(distToLineSquared <= radiusSquared){
            double subLength = Math.sqrt(radiusSquared - distToLineSquared);
            double velLength = Math.sqrt(pointVelX * pointVelX + pointVelY * pointVelY);
            double projLength = Vector2D.scalarProject(circleX - pointX, circleY - pointY, pointVelX, pointVelY, velLength);
            if(projLength > 0){
                return (projLength - subLength) / velLength;
            }
        }
        return -Double.MAX_VALUE;
    }
    
    private static double getOverlapTime(double actualBPos, double actualAPos, double relativeVel){
        double dist = actualBPos - actualAPos;
        return dist / relativeVel;
    }
//    
//    /**
//     * return Vector2D is temporary
//     * @param directionX
//     * @param directionY
//     * @return 
//     */
//    public abstract Vector2D support(Vector2D direction);
    
    public abstract int getShapeType();
    
    public abstract void draw(Graphics2D g, Color color);
}
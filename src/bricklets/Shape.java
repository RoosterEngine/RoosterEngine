package bricklets;

/**
 *
 * @author david
 */
public abstract class Shape {
    public static final int TYPE_CIRCLE = 0, TYPE_AA_BOUNDING_BOX = 1, TYPE_O_BOUNDING_BOX = 2, TYPE_POLYGON = 3;
    protected double x, y, dx, dy, radius;
    
    public Shape(double x, double y, double dx, double dy, double radius){
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
        this.radius = radius;
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
    
    public void updatePosition(double x, double y){
        this.x = x;
        this.y = y;
    }
    
    public void updateVelocity(double dx, double dy){
        this.dx = dx;
        this.dy = dy;
    }
    
    public static void collideCircleCircle(Circle a, Circle b, double maxTime, Collision result){
        double combinedVelX = b.dx - a.dx;
        double combinedVelY = b.dy - a.dy;
        double distToLineSquared = Vector2D.distToLineSquared(a.x, a.y, b.x, b.y, b.x + combinedVelX, b.y + combinedVelY);
        double radiiSum = a.radius + b.radius;
        if(distToLineSquared > radiiSum * radiiSum){
            result.set(Collision.NO_COLLISION);
            return;
        }

        Vector2D velocity = result.getCollisionNormal().set(combinedVelX, combinedVelY);
        double deltaX = b.x - a.x;
        double deltaY = b.y - a.y;
        double distBetween = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        double projVel = Vector2D.scalarProject(combinedVelX, combinedVelY, deltaX, deltaY, distBetween);
        if(projVel < 0){
            result.set(Collision.NO_COLLISION);
            return;
        }

        distBetween -= radiiSum;
        double travelTime = distBetween / projVel;
        result.set(travelTime, null, a, b);
    }
    
    public abstract int getShapeType();
}

package Utilities;

import bricklets.Wall;
import gameengine.collisiondetection.EntityType;
import gameengine.collisiondetection.World;
import gameengine.collisiondetection.shapes.Circle;
import gameengine.collisiondetection.shapes.Polygon;
import gameengine.collisiondetection.shapes.Shape;
import gameengine.entities.RegionSensor;
import gameengine.geometry.Vector2D;
import gameengine.motion.motions.DestinationMotion;
import gameengine.motion.motions.Motion;

/**
 * Utilities for making games.
 */
public class GameUtils {
    /**
     * Creates a path that would be followed by entities after they encounter the first location.
     * Note that the path is not followed exactly, the entities simply try to reach the general
     * area of each point and then transitions to the next point.
     *
     * Make sure that the world is configured for this path type to collide with the types of
     * entities that you want to follow this path.
     *
     * @param world        The world
     * @param pathType     The path type
     * @param acceleration The acceleration
     * @param maxSpeed     The maximum speed
     * @param radius       Defines how close an entity needs to get to a point before pursuing the
     *                     next point.  The radius should be less than half of the distance between
     *                     points.
     * @param points       The destination points in order
     */
    public static void createEntityPath(World world, EntityType pathType, double acceleration,
                                        double maxSpeed, double radius, Vector2D... points) {
        assert radius > 0;

        for (int i = 0; i < points.length - 1; i++) {
            Vector2D point = points[i];
            Vector2D destination = points[i + 1];
            RegionSensor sensor = new RegionSensor(point.getX(), point.getY(), new Circle(radius)
                    , entity -> {
                Vector2D initialVelocity = new Vector2D(entity.getDX(), entity.getDY());
                Motion motion = entity.getMotion();
                if (motion instanceof DestinationMotion) {
                    ((DestinationMotion) motion).setDestination(destination, maxSpeed,
                            acceleration);
                } else {
                    entity.setMotion(new DestinationMotion(initialVelocity, maxSpeed,
                            acceleration, destination));
                }

            }, null);
            world.addEntity(sensor);
        }
    }

    public static void createWalls(World world, EntityType wallType, double thickness,
                                   Vector2D... points) {
        for (int i = 0; i < points.length - 1; i++) {
            Vector2D from = points[i];
            Vector2D to = points[i + 1];
            createWall(world, wallType, thickness, from, to);
        }
    }

    public static void createWall(World world, EntityType wallType, double thickness, Vector2D
            from, Vector2D to) {
        double[] xPoints = new double[4];
        double[] yPoints = new double[4];

        Vector2D length = new Vector2D(to.getX() - from.getX(), to.getY() - from.getY());
        Vector2D width = new Vector2D(length);
        width.perpendicular();
        width.scale(thickness / width.length());

        xPoints[0] = from.getX() + width.getX() * 0.5;
        yPoints[0] = from.getY() + width.getY() * 0.5;
        xPoints[1] = xPoints[0] + length.getX();
        yPoints[1] = yPoints[0] + length.getY();
        xPoints[2] = xPoints[1] - width.getX();
        yPoints[2] = yPoints[1] - width.getY();
        xPoints[3] = from.getX() - width.getX() * 0.5;
        yPoints[3] = from.getY() - width.getY() * 0.5;

        Shape shape = new Polygon(xPoints, yPoints);
        Wall wall = new Wall((from.getX() + to.getX()) / 2, (from.getY() + to
                .getY()) / 2, shape);
        world.addEntity(wall);
    }
}

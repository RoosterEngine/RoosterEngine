package gameengine.collisiondetection.shapes;

import gameengine.collisiondetection.Collision;
import gameengine.physics.Material;

import java.awt.*;

/**
 * @author david
 */
public class AABBShape extends Shape {
    private double width, height;

    public AABBShape(double x, double y, double width, double height, double mass) {
        super(x, y, width * 0.5, height * 0.5, mass);
        init(width, height);
    }

    public AABBShape(double x, double y, double width, double height, double mass, Material material) {
        super(x, y, width * 0.5, height * 0.5, mass, material);
        init(width, height);
    }

    @Override
    public void collideWithShape(Shape shape, double maxTime, Collision result) {
        shape.collideWithAABB(this, maxTime, result);
    }

    @Override
    public void collideWithCircle(CircleShape circleShape, double maxTime, Collision result) {
        Shape.collideCircleAABB(circleShape, this, maxTime, result);
    }

    @Override
    public void collideWithAABB(AABBShape aabbShape, double maxTime, Collision result) {
        Shape.collideAABBAABB(this, aabbShape, maxTime,  result);
    }

    @Override
    public void collideWithPolygon(PolygonShape polygonShape, double maxTime, Collision result) {
        Shape.collideAABBPoly(this, polygonShape, maxTime, result);
    }

    private void init(double width, double height) {
        this.width = width;
        this.height = height;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    @Override
    public int getShapeType() {
        return Shape.TYPE_AABB;
    }

    @Override
    public void draw(Graphics2D g, Color color) {
        g.setColor(color);
        g.drawRect((int) (x - halfWidth), (int) (y - halfHeight), (int) width, (int) height);
    }
}

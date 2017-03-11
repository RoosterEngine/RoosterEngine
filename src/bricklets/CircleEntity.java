package bricklets;

import gameengine.collisiondetection.shapes.Circle;

public class CircleEntity extends TestingEntity {
    public CircleEntity(double x, double y, double radius) {
        super(x, y, new Circle(radius));
    }
}

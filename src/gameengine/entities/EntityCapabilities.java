package gameengine.entities;

import gameengine.collisiondetection.shapes.Shape;
import gameengine.graphics.Renderer;

/**
 * Defines the core entity capabilities.
 */
public interface EntityCapabilities {

    Shape getShape();

    double getX();

    double getY();

    default void update(double elapsedTime) {
    }

    default void draw(Renderer renderer) {
    }
}

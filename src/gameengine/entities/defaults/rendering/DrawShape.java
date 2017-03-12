package gameengine.entities.defaults.rendering;

import gameengine.graphics.Renderer;

/**
 * Draws the outline of the shape.
 */
public interface DrawShape extends Drawable {
    @Override
    default void draw(Renderer renderer) {
        setForegroundColor(renderer);
        getShape().draw(renderer, getX(), getY());
    }
}

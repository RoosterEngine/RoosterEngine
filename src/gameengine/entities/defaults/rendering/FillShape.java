package gameengine.entities.defaults.rendering;

import gameengine.graphics.Renderer;

/**
 * Fills the shape.
 */
public interface FillShape extends Drawable {
    @Override
    default void draw(Renderer renderer) {
        setForegroundColor(renderer);
        getShape().fill(renderer, getX(), getY());
    }
}

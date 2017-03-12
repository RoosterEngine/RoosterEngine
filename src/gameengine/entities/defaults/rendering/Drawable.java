package gameengine.entities.defaults.rendering;

import gameengine.entities.EntityCapabilities;
import gameengine.graphics.RColor;
import gameengine.graphics.Renderer;

/**
 * Designates that this entity is drawable.
 */
public interface Drawable extends EntityCapabilities {
    default void setForegroundColor(Renderer renderer) {
        renderer.setForegroundColor(RColor.WHITE);
    }
}

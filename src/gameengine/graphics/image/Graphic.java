package gameengine.graphics.image;

import gameengine.graphics.Renderer;

/**
 * Represents a graphic that can be rendered to the screen.
 */
public interface Graphic {
    /**
     * @return The width of the graphic.
     */
    int getWidth();

    /**
     * @return The height of the graphic.
     */
    int getHeight();

    /**
     * Draws the graphic at the specified location.
     *
     * @param renderer The screen renderer
     * @param x        The X component of the location
     * @param y        The Y component of the location
     */
    void draw(Renderer renderer, double x, double y);

    /**
     * Releases all resources associated with this graphic.  This graphic cannot be used after
     * calling this method.
     */
    void discardAndCleanup();
}
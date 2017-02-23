package gameengine.graphics.image;

import gameengine.graphics.MutableColor;
import gameengine.graphics.Renderer;

/**
 * A {@link Graphic} that is a solid color.
 *
 * @author david
 */
public class SolidColorGraphic implements Graphic {
    private MutableColor color;
    private int halfWidth, halfHeight;

    /**
     * Creates a graphic of the specified dimensions
     *
     * @param color  the fill color of the graphic
     * @param width  the halfWidth of the graphic
     * @param height the halfWidth of the graphic
     */
    public SolidColorGraphic(MutableColor color, int width, int height) {
        this.color = color;
        this.halfWidth = width / 2;
        this.halfHeight = height / 2;
    }

    @Override
    public int getWidth() {
        return halfWidth * 2;
    }

    @Override
    public int getHeight() {
        return halfHeight * 2;
    }

    public void resize(int width, int height) {
        halfWidth = width / 2;
        halfHeight = height / 2;
    }

    @Override
    public void draw(Renderer renderer, double x, double y) {
        renderer.setForegroundColor(color);
        renderer.fillRect(x, y, halfWidth, halfHeight);
    }

    @Override
    public void discardAndCleanup() {
    }
}
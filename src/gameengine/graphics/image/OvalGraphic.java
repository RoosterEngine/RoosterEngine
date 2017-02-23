package gameengine.graphics.image;

import gameengine.graphics.MutableColor;
import gameengine.graphics.Renderer;

/**
 * A {@link Graphic} that renders an oval.
 *
 * @author davidrusu
 */
public class OvalGraphic implements Graphic {
    private int width, height, xRadius, yRadius;
    private MutableColor color;

    public OvalGraphic(int width, int height, MutableColor color) {
        setSize(width, height);
        this.color = color;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
        xRadius = width / 2;
        yRadius = height / 2;
    }

    @Override
    public void draw(Renderer renderer, double x, double y) {
        renderer.setForegroundColor(color);
        renderer.fillOval(x, y, width, height);
    }

    @Override
    public void discardAndCleanup() {
    }
}

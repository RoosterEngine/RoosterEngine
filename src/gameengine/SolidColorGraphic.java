package gameengine;

import java.awt.*;

/**
 * A {@link Graphic} that is a solid color
 *
 * @author david
 */
public class SolidColorGraphic implements Graphic {
    private Color color;
    private int width, height;

    /**
     * Creates a graphic of the specified dimensions
     *
     * @param color  the fill color of the graphic
     * @param width  the width of the graphic
     * @param height the width of the graphic
     */
    public SolidColorGraphic(Color color, int width, int height) {
        this.color = color;
        this.width = width;
        this.height = height;
    }

    @Override
    public void reset() {
    }

    @Override
    public void update(double elapsedTime) {
    }


    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void draw(Graphics2D g, int x, int y) {
        g.setColor(color);
        g.fillRect(x, y, width, height);
    }

    @Override
    public void resize(int width, int height) {
        this.width = width;
        this.height = height;
    }
}
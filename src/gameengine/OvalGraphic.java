package gameengine;

import java.awt.*;

/**
 * A {@link Graphic} that renders an oval
 *
 * User: davidrusu
 * Date: 17/12/12
 * Time: 6:57 PM
 */
public class OvalGraphic implements Graphic {
    private int width, height, xRadius, yRadius;
    private Color color;

    public OvalGraphic(int width, int height, Color color) {
        resize(width, height);
        this.color = color;
    }

    @Override
    public void update(double elapsedTime) {
    }

    @Override
    public void reset() {
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
    public void resize(int width, int height) {
        this.width = width;
        this.height = height;
        xRadius = width / 2;
        yRadius = height / 2;
    }

    @Override
    public void draw(Graphics2D g, int x, int y) {
        g.setColor(color);
        g.fillOval(x - xRadius, y - yRadius, width, height);
    }
}

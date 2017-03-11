package bricklets;

import gameengine.graphics.RColor;
import gameengine.graphics.Renderer;

import java.awt.*;

/**
 * A brick to be used in brick breaker levels.
 *
 * User: davidrusu
 */
public class Brick extends BoxEntity {
    private static final double TOTAL_HEALTH = 100;
    private double health = TOTAL_HEALTH;

    public Brick(double x, double y, double width, double height) {
        super(x, y, width, height);
        color = RColor.GREY;
        color.darken();
    }

    public void doDamage(double amount) {
        health -= amount;
        if (health < 0) {
            health = 0;
        }
    }

    public boolean isDead() {
        return health <= 0;
    }

    @Override
    public void draw(Renderer renderer) {
        double offset = 0.1;
        float grad = (float) (((1 - health / TOTAL_HEALTH) + offset) / (offset + 1));
        renderer.setForegroundColor(grad, grad, grad);
        renderer.fillRect(x, y, getHalfWidth(), getHalfHeight());
//        drawBoundingBoxes(g, RColor.RED);
    }

    private void drawHealth(Graphics2D g) {
        String text = "\u2665 : " + health;
        FontMetrics metrics = g.getFontMetrics();
        int textWidth = metrics.stringWidth(text);
        int textHeight = metrics.getHeight();
        g.setColor(Color.WHITE);
        double xPadding = (getWidth() - textWidth) * 0.5;
        double yPadding = (getHeight() + textHeight / 2) * 0.5;
        g.drawString(text, (int) (x - getWidth() / 2 + xPadding), (int) (y - getHeight() / 2 +
                yPadding));
    }

}

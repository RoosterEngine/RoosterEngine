package bricklets;

import gameengine.entities.BoxEntity;

import java.awt.*;

/**
 * A brick to be used in brick breaker levels
 * <p/>
 * User: davidrusu
 * Date: 09/12/12
 * Time: 12:35 AM
 */
public class Brick extends BoxEntity {
    private static final double TOTAL_HEALTH = 100;
    private double health = TOTAL_HEALTH;

    public Brick(double x, double y, double width, double height) {
        super(x, y, width, height);
        color = Color.BLACK;
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
    public void update(double elapsedTime) {
        super.update(elapsedTime);
    }

    @Override
    public void draw(Graphics2D g) {
        int grad = (int) ((1 - health / TOTAL_HEALTH) * 255);
        g.setColor(new Color(grad, grad, grad));
        g.fillRect((int) (x - halfWidth), (int) (y - halfHeight), (int) (width), (int) (height));
        g.setColor(Color.RED.darker().darker());
        String text = "\u2665 : " + health;
        FontMetrics metrics = g.getFontMetrics();
        int textWidth = metrics.stringWidth(text);
        int textHeight = metrics.getHeight();
        g.setColor(Color.WHITE);
        double xPadding = (width - textWidth) * 0.5;
        double yPadding = (height + textHeight / 2) * 0.5;
        g.drawString(text, (int) (x - width / 2 + xPadding),
                (int) (y - height / 2 + yPadding));
    }

}

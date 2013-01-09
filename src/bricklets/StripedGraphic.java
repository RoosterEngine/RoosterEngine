package bricklets;

import gameengine.GameController;
import gameengine.graphics.Graphic;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Diagonal stripes graphic
 *
 * @author david
 */
public class StripedGraphic implements Graphic {
    private Color backgroundColor, foregroundColor;
    private int width, height;
    private GameController controller;
    private BufferedImage image;

    public StripedGraphic(GameController controller, Color background, Color foreground, int width, int height) {
        this.controller = controller;
        this.backgroundColor = background;
        this.foregroundColor = foreground;
        this.width = width;
        this.height = height;
        setupImage();
    }

    @Override
    public void reset() {
    }

    private void setupImage() {
        image = controller.createCompatibleImage(width, height);
        Graphics2D g = image.createGraphics();
        g.setColor(backgroundColor);
        g.fillRect(0, 0, width, height);
        g.setColor(foregroundColor);
        double x1, y1, x2, y2;
        x1 = y1 = x2 = y2 = 0;
        double padding = 10, thickness = 10;
        int[] xPoints = new int[4];
        int[] yPoints = new int[4];
        for (int i = 0; i < Math.max(width, height) * 2 / (padding + thickness); i++) {
            x2 += padding + thickness;
            y1 += padding + thickness;

            xPoints[0] = (int) x1;
            yPoints[0] = (int) y1;
            xPoints[1] = (int) x1;
            yPoints[1] = (int) (y1 + thickness);
            xPoints[2] = (int) (x2 + thickness);
            yPoints[2] = (int) y2;
            xPoints[3] = (int) x2;
            yPoints[3] = (int) y2;
            g.fillPolygon(xPoints, yPoints, 4);
        }
        g.dispose();
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
        g.drawImage(image, x, y, null);
    }

    @Override
    public void resize(int width, int height) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
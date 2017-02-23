package gameengine.collisiondetection;

import gameengine.context.Context;
import gameengine.graphics.Renderer;

/**
 * documentation
 *
 * @author davidrusu
 */
public class Viewport {
    private double x = 0, y = 0, scale = 1, offsetX = 0, offsetY = 0, screenHalfWidth,
            screenHalfHeight;
    private double minX, minY, maxX, maxY;

    /**
     * @param x            0 is the horizontal center of the screen
     * @param y            0 is the vertical center of the screen
     * @param scale        1 is normal scale, 2 is scaled by a factor of 2
     * @param screenWidth  the width of the {@link Context} that this ViewPort will be used in
     * @param screenHeight the height of the {@link Context} that this ViewPort will be used in
     */
    public Viewport(double x, double y, double scale, double screenWidth, double screenHeight) {
        this.x = x;
        this.y = y;
        this.scale = scale;
        this.screenHalfWidth = screenWidth / 2;
        this.screenHalfHeight = screenHeight / 2;
        calcDim();
    }

    private void calcDim() {
        double scaledHWidth = screenHalfWidth / scale;
        double scaledHHeight = screenHalfHeight / scale;
        double realX = x + screenHalfWidth;
        double realY = y + screenHalfHeight;
        minX = realX - scaledHWidth;
        maxX = realX + scaledHWidth;
        minY = realY - scaledHHeight;
        maxY = realY + scaledHHeight;
        offsetX = x - (1 - scale) * (screenHalfWidth + x);
        offsetY = y - (1 - scale) * (screenHalfHeight + y);
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
        // TODO can be optimized to only recalc the x components
        calcDim();
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
        // TODO can be optimized to only recalc the y components
        calcDim();
    }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
        calcDim();
    }

    public double getMinX() {
        return minX;
    }

    public double getMinY() {
        return minY;
    }

    public double getMaxX() {
        return maxX;
    }

    public double getMaxY() {
        return maxY;
    }

    public void scaleScale(double amount) {
        scale *= amount;
        calcDim();
    }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
        calcDim();
    }

    public void addPosition(double dx, double dy) {
        x += dx;
        y += dy;
        calcDim();
    }

    public void applyTransformations(Renderer renderer) {
        renderer.translate(-offsetX, -offsetY);
        renderer.setScale(scale);
    }

    public void reverseTransformations(Renderer renderer) {
        renderer.setScale(1);
        renderer.translate(offsetX, offsetY);
    }
}

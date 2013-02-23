package gameengine.collisiondetection;

/**
 * documentation
 * User: davidrusu
 * Date: 22/02/13
 * Time: 5:23 PM
 */
public class Viewport {
    private double x = 0, y = 0, scale = 1;

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public void scaleScale(double amount) {
        scale *= amount;
    }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }
}

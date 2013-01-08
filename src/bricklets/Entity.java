package bricklets;

import gameengine.motion.motions.Motion;
import gameengine.motion.motions.NormalMotion;

import java.awt.*;

public abstract class Entity {
    protected double x, y, dx, dy, ddx, ddy, width, height, halfWidth, halfHeight;
    protected double mass;
    private Motion motion;

    public Entity(double x, double y, double width, double height) {
        this(x, y, width, height, 1);
    }

    public Entity(double x, double y, double width, double height, double mass) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        halfWidth = width / 2;
        halfHeight = height / 2;
        this.mass = mass;
        motion = new NormalMotion();
    }

    /**
     * Sets the {@link Motion} that will controlling the velocity of this {@link Entity}
     *
     * @param motion the {@link Motion} that will control the velocity of this {@link Entity}
     */
    public void setMotion(Motion motion) {
        this.motion = motion;
    }

    public void resetMotion() {
        motion.reset();
    }

    /**
     * Updates the current {@link Motion} and then updates the this entities velocities
     *
     * @param elapsedTime the amount of time to integrate
     */
    public void updateMotion(double elapsedTime) {
        motion.update(this, elapsedTime);
        dx = motion.getVelocityX();
        dy = motion.getVelocityY();
    }

    /**
     * Updates the position of the entity
     *
     * @param elapsedTime the amount of time to integrate
     */
    public void updatePosition(double elapsedTime) {
        x += dx * elapsedTime;
        y += dy * elapsedTime;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getDX() {
        return dx;
    }

    public double getDY() {
        return dy;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public void setWidth(double width) {
        this.width = width;
        halfWidth = width / 2;
    }

    public void setHeight(double height) {
        this.height = height;
        halfHeight = height / 2;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }

    public double getMass() {
        return mass;
    }

    public void setVelocity(double x, double y) {
        dx = x;
        dy = y;
    }

    public void addVelocity(double dx, double dy) {
        this.dx += dx;
        this.dy += dy;
    }

    public abstract void update(double elapsedTime);

    public abstract void draw(Graphics2D g);

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }
}
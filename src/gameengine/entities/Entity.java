package gameengine.entities;

import gameengine.collisiondetection.Collision;
import gameengine.collisiondetection.shapes.CircleShape;
import gameengine.collisiondetection.shapes.Shape;
import gameengine.collisiondetection.tree.Tree;
import gameengine.math.Utils;
import gameengine.motion.motions.Motion;
import gameengine.motion.motions.NormalMotion;
import gameengine.physics.Material;

import java.awt.*;

public abstract class Entity {
    private Motion motion;
    private Shape shape;
    private Tree containingTree;
    private int indexInTree;
    protected double x, y, dx, dy, ddx, ddy, width, height, halfWidth, halfHeight;

    public Entity(double x, double y, double width, double height) {
        this(x, y, width, height, new CircleShape(null, x, y, Utils.pythagoras(width / 2, height / 2), Material.getRubber(), 1));
        shape.setParent(this);
    }

    public Entity(double x, double y, double width, double height, Shape shape) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        halfWidth = width / 2;
        halfHeight = height / 2;
        motion = new NormalMotion();
        this.shape = shape;
        shape.setParentOffset(x - shape.getX(), y - shape.getY());
    }

    public void setShape(Shape shape) {
        this.shape = shape;
    }

    public Shape getShape() {
        return shape;
    }

    /**
     * Sets the {@link Motion} that will controlling the velocity of this
     * {@link Entity}
     *
     * @param motion the {@link Motion} that will control the velocity of this
     *               {@link Entity}
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
        shape.updateVelocity(dx, dy);
    }

    /**
     * Updates the position of the entity
     *
     * @param elapsedTime the amount of time to integrate
     */
    public void updatePosition(double elapsedTime) {
        x += dx * elapsedTime;
        y += dy * elapsedTime;
        shape.updatePosition(x, y);
        shape.updateVelocity(dx, dy);
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

    public void setVelocity(double dx, double dy) {
        this.dx = dx;
        this.dy = dy;
        shape.updateVelocity(this.dx, this.dy);
    }

    public void addVelocity(double dx, double dy) {
        this.dx += dx;
        this.dy += dy;
    }

    public int getIndexInTree() {
        return indexInTree;
    }

    public void setIndexInTree(int indexInTree) {
        this.indexInTree = indexInTree;
    }

    public void setContainingTree(Tree containingTree, int indexInTree) {
        this.containingTree = containingTree;
        setIndexInTree(indexInTree);
    }

    public Tree getContainingTree() {
        return containingTree;
    }

    public void removeFromWorld() {
        containingTree.removeEntityFromList(indexInTree);
        setContainingTree(null, 0);
    }

    public abstract void update(double elapsedTime);

    public abstract void draw(Graphics2D g);

    public void drawLineToPartition(Graphics2D g, Color color) {
        g.setColor(color);
        double endX;
        double endY;
        if (containingTree != null) {
            endX = containingTree.getCenterX();
            endY = containingTree.getCenterY();
        } else {
            endX = Math.random() * 1900;
            endY = Math.random() * 1024;
        }
        g.drawLine((int) x, (int) y, (int) endX, (int) endY);
        int shapeWidth = (int) (shape.getBoundingMaxX() - shape.getBoundingMinX());
        int shapeHeight = (int) (shape.getBoundingMaxY() - shape.getBoundingMinY());
        g.drawRect((int) shape.getBoundingMinX(), (int) shape.getBoundingMinY(), shapeWidth, shapeHeight);
    }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }
}

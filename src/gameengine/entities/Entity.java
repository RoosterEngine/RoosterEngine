package gameengine.entities;

import gameengine.collisiondetection.EntityType;
import gameengine.collisiondetection.shapes.Shape;
import gameengine.collisiondetection.tree.Tree;
import gameengine.motion.motions.Motion;
import gameengine.motion.motions.NormalMotion;
import gameengine.physics.Material;

import java.awt.*;

public abstract class Entity {
    private static EntityType defaultEntityType = EntityType.DEFAULT;
    private static Material defaultMaterial = Material.getDefaultMaterial();
    protected Material material;
    protected double mass;
    protected double x, y, dx, dy;
    private double boundingHalfWidth, boundingHalfHeight, boundingCenterX, boundingCenterY;
    private double boundingMinX, boundingMaxX, boundingMinY, boundingMaxY;
    private int collisionType = EntityType.DEFAULT.ordinal();
    private int collisionTypeBitMask = 1 << collisionType;
    private Motion motion;
    private Shape shape;
    private Tree containingTree;
    private int indexInTree;

    public Entity(double x, double y, Shape shape) {
        this(x, y, defaultMaterial, shape);
    }

    public Entity(double x, double y, Material material, Shape shape) {
        //TODO we should have an overloaded constructor that accepts the entityType as a parameter
        init(x, y, material, shape, defaultEntityType);
        updateMass();
    }

    public Entity(double x, double y, double mass, Material material, Shape shape) {
        //TODO we should have an overloaded constructor that accepts the entityType as a parameter
        init(x, y, material, shape, defaultEntityType);
        this.mass = mass;
    }

    private void init(double x, double y, Material material, Shape shape, EntityType entityType) {
        this.x = x;
        this.y = y;
        this.material = material;
        this.shape = shape;
        motion = new NormalMotion();
        shape.setParent(this);
        shape.setParentOffset(x - shape.getX(), y - shape.getY());
        setEntityType(entityType);
    }

    public static void setDefaultEntityType(EntityType defaultType) {
        defaultEntityType = defaultType;
    }

    public static void setDefaultMaterial(Material material) {
        defaultMaterial = material;
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
        return shape.getWidth();
    }

    public double getHeight() {
        return shape.getHeight();
    }

    public double getHalfWidth() {
        return shape.getHalfWidth();
    }

    public double getHalfHeight() {
        return shape.getHalfHeight();
    }

    public void setVelocity(double dx, double dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public void calculateBoundingBox(double time) {
        double x = getX();
        double y = getY();
        double halfWidth = getHalfWidth();
        double halfHeight = getHalfHeight();
        boundingMinX = x - halfWidth;
        boundingMaxX = x + halfWidth;
        boundingMinY = y - halfHeight;
        boundingMaxY = y + halfHeight;
        double scale = 1;
        double xTravelDist = getDX() * time * scale;
        double yTravelDist = getDY() * time * scale;
        if (xTravelDist > 0) {
            boundingMaxX += xTravelDist;
        } else {
            boundingMinX += xTravelDist;
        }

        if (yTravelDist > 0) {
            boundingMaxY += yTravelDist;
        } else {
            boundingMinY += yTravelDist;
        }
        boundingHalfWidth = (boundingMaxX - boundingMinX) * 0.5;
        boundingHalfHeight = (boundingMaxY - boundingMinY) * 0.5;
        boundingCenterX = boundingMinX + boundingHalfWidth;
        boundingCenterY = boundingMinY + boundingHalfHeight;
    }

    public double getBoundingMinX() {
        return boundingMinX;
    }

    public double getBoundingMaxX() {
        return boundingMaxX;
    }

    public double getBoundingMinY() {
        return boundingMinY;
    }

    public double getBoundingMaxY() {
        return boundingMaxY;
    }

    public double getBoundingCenterX() {
        return boundingCenterX;
    }

    public double getBoundingHalfWidth() {
        return boundingHalfWidth;
    }

    public double getBoundingCenterY() {
        return boundingCenterY;
    }

    public double getBoundingHalfHeight() {
        return boundingHalfHeight;
    }

    public void drawBoundingBoxes(Graphics2D g, Color color) {
        g.setColor(color);
        double width = boundingHalfWidth * 2;
        double height = boundingHalfHeight * 2;
        g.drawRect((int) boundingMinX, (int) boundingMinY, (int) width, (int) height);
    }

    public void setEntityType(EntityType type) {
        collisionType = type.ordinal();
        collisionTypeBitMask = 1 << collisionType;
    }

    public int getCollisionType() {
        return collisionType;
    }

    public int getCollisionTypeBitMask() {
        return collisionTypeBitMask;
    }

    public void addVelocity(double dx, double dy) {
        this.dx += dx;
        this.dy += dy;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }

    public double getMass() {
        return mass;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public Material getMaterial() {
        return material;
    }

    public int getIndexInTree() {
        return indexInTree;
    }

    public void setIndexInTree(int indexInTree) {
        this.indexInTree = indexInTree;
    }

    public Tree getContainingTree() {
        return containingTree;
    }

    public void setContainingTree(Tree containingTree, int indexInTree) {
        this.containingTree = containingTree;
        setIndexInTree(indexInTree);
    }

    public Shape getShape() {
        return shape;
    }

    public void setShape(Shape shape) {
        this.shape = shape;
        shape.setParent(this);
    }

    public void updateMass() {
        mass = material.getDensity() * shape.getArea();
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

    public void removeFromWorld() {
        containingTree.removeEntityFromWorld(this);
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
        int shapeWidth = (int) (getBoundingMaxX() - getBoundingMinX());
        int shapeHeight = (int) (getBoundingMaxY() - getBoundingMinY());
        g.drawRect((int) getBoundingMinX(), (int) getBoundingMinY(), shapeWidth, shapeHeight);
    }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Motion getMotion() {
        return motion;
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
}

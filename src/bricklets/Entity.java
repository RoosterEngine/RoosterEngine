package bricklets;

import gameengine.effects.MotionEffect;
import gameengine.effects.NoMotionEffect;

import java.awt.Graphics2D;

public abstract class Entity {
    protected double x, y, dx, dy, ddx, ddy, width, height, halfWidth, halfHeight;
    protected double mass;
    private MotionEffect motionEffect;

    public Entity(double x, double y, double width, double height){
        this(x, y, width, height, 1);
    }
    
    public Entity(double x, double y, double width, double height, double mass){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        halfWidth = width / 2;
        halfHeight = height / 2;
        this.mass = mass;
        motionEffect = new NoMotionEffect();
    }

    public MotionEffect getMotionEffect(){
        return motionEffect;
    }

    public void setMotionEffect(MotionEffect motionEffect){
        this.motionEffect = motionEffect;
        dx += motionEffect.getVelocityX();
        dy += motionEffect.getVelocityY();
    }

    public void updateMotionGenerator(double elapsedTime) {
        motionEffect.update(elapsedTime);
        dx += motionEffect.getDeltaVelocityX();
        dy += motionEffect.getDeltaVelocityY();
    }

    public void resetMotionEffect(){
        motionEffect.reset();
    }

    public void updatePosition(double elapsedTime) {
        x += dx * elapsedTime;
        y += dy * elapsedTime;
    }
    
    public double getX() {
        return x;
    }
    
    public double getY(){
        return y;
    }
    
    public double getDX(){
        return dx;
    }
    
    public double getDY(){
        return dy;
    }

    public double getWidth(){
        return width;
    }

    public double getHeight(){
        return height;
    }

    public void setWidth(double width){
        this.width = width;
        halfWidth = width / 2;
    }

    public void setHeight(double height){
        this.height = height;
        halfHeight = height / 2;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }

    public double getMass(){
        return mass;
    }

    public void setVelocity(double x, double y){
        dx = x;
        dy = y;
    }

    public void setVelocityX(double dx) {
        this.dx = dx;
    }

    public void setVelocityY(double dy) {
        this.dy = dy;
    }

    public void addVelocityX(double dx) {
        this.dx += dx;
    }

    public void addVelocityY(double dy) {
        this.dy += dy;
    }

    public void addVelocity(double dx, double dy){
        this.dx += dx;
        this.dy += dy;
    }

    public void addForce(double x, double y){
        ddx += x / mass;
        ddy += y / mass;
    }

    public abstract void update(double elapsedTime);

    public abstract void draw(Graphics2D g);

    public void setPosition(double x, double y){
        this.x = x;
        this.y = y;
    }

    public void setAcceleration(double x, double y){
        ddx = x;
        ddy = y;
    }
}
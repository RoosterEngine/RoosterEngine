package gameengine.effects;

/**
 *
 * @author davidrusu
 */
public class Slide implements PositionEffect, VelocityEffect {
    private double x, y, destinationX, destinationY, initialX, initialY;
    private double deltaVelocityX, deltaVelocityY, currentVelocityX, currentVelocityY;
    private MotionGenerator motion;

    public Slide(double initialX, double initialY, double destinationX, double destinationY, MotionGenerator motion){
        this.destinationX = destinationX;
        this.destinationY = destinationY;
        this.initialX = initialX;
        this.initialY = initialY;
        this.motion = motion;
        reset();
    }

    @Override
    public double getX(){
        return x;
    }
    
    @Override
    public double getY(){
        return y;
    }

    @Override
    public double getDeltaVelocityX(){
        return deltaVelocityX;
    }

    @Override
    public double getDeltaVelocityY() {
        return deltaVelocityY;
    }

    @Override
    public double getVelocityX() {
        return currentVelocityX;
    }

    @Override
    public double getVelocityY() {
        return currentVelocityY;
    }

    @Override
    public void reset(){
        x = initialX;
        y = initialY;
        deltaVelocityX = 0;
        deltaVelocityY = 0;
        currentVelocityX = 0;
        currentVelocityY = 0;
        motion.reset();
    }
    
    @Override
    public void reset(double x, double y){
        this.x = x;
        this.y = y;
        initialX = x;
        initialY = y;
        deltaVelocityX = 0;
        deltaVelocityY = 0;
        currentVelocityX = 0;
        currentVelocityY = 0;
        motion.reset();
    }
    
    @Override
    public void update(double elapsedTime) {
        double newVelocityX = motion.getVelocity(x, destinationX, elapsedTime);
        double newVelocityY = motion.getVelocity(y, destinationY, elapsedTime);
        deltaVelocityX = newVelocityX - currentVelocityX;
        deltaVelocityY = newVelocityY - currentVelocityY;
        currentVelocityX = newVelocityX;
        currentVelocityY = newVelocityY;
        x += currentVelocityX * elapsedTime;
        y += currentVelocityY * elapsedTime;
    }
}
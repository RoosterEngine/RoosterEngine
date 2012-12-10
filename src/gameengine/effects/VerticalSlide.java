package gameengine.effects;

/**
 *
 * @author davidrusu
 */
public class VerticalSlide implements PositionEffect, VelocityEffect{
    private double destinationY, initialX, initialY, y;
    private double deltaVelocity, currentVelocity;
    private MotionGenerator motion;
    
    public VerticalSlide(double initialX, double initialY, double destinationY, MotionGenerator motion){
        this.destinationY = destinationY;
        this.initialX = initialX;
        this.initialY = initialY;
        this.motion = motion;
        reset();
    }

    @Override
    public double getX(){
        return initialX;
    }
    
    @Override
    public double getY(){
        return y;
    }

    @Override
    public double getDeltaVelocityX() {
        return deltaVelocity;
    }

    @Override
    public double getDeltaVelocityY() {
        return 0;
    }

    @Override
    public double getVelocityX() {
        return currentVelocity;
    }

    @Override
    public double getVelocityY() {
        return 0;
    }

    @Override
    public void reset(double x, double y){
        this.y = y;
        initialX = x;
        initialY = y;
        motion.reset();
        deltaVelocity = 0;
        currentVelocity = 0;
    }

    @Override
    public final void reset(){
        y = initialY;
        motion.reset();
        deltaVelocity = 0;
        currentVelocity = 0;
    }

    @Override
    public void update(double elapsedTime) {
        double newVelocity = motion.getVelocity(y, destinationY, elapsedTime);
        deltaVelocity = newVelocity - currentVelocity;
        currentVelocity =   newVelocity;
        y += currentVelocity * elapsedTime;
    }
}
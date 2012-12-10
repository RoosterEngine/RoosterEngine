package gameengine.effects;

/**
 *
 * @author davidrusu
 */
public class SpringMotion implements MotionGenerator{
    private double velocity, k, dampeningFactor;
    
    public SpringMotion(double k, double dampeningRatio){
        this.k = k;
        this.dampeningFactor = dampeningRatio * 2 * Math.sqrt(k);
    }

    @Override
    public double getVelocity(double currentPosition, double destinationPosition, double elapsedTime) {
        velocity += ((destinationPosition - currentPosition) * k - dampeningFactor * velocity) * elapsedTime;
        return velocity;
    }

    @Override
    public void reset() {
        velocity = 0;
    }
}
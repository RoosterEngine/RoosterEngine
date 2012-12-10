package gameengine.effects;

/**
 *
 * @author davidrusu
 */
public class StationaryEffect implements PositionEffect, VelocityEffect {
    private double x, y;
    
    public StationaryEffect(double x, double y){
        this.x = x;
        this.y = y;
    }
    
    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public double getDeltaVelocityX() {
        return 0;
    }

    @Override
    public double getDeltaVelocityY() {
        return 0;
    }

    @Override
    public double getVelocityX() {
        return 0;
    }

    @Override
    public double getVelocityY() {
        return 0;
    }

    @Override
    public void reset() {
    }
    
    @Override
    public void reset(double x, double y){
        this.x = x;
        this.y = y;
    }

    @Override
    public void update(double elapsedTime) {
    }
    
}

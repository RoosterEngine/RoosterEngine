package gameengine.effects;

/**
 *
 * @author davidrusu
 */
public class NoMotionEffect implements MotionEffect {

    public NoMotionEffect(){
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
    public double getDeltaVelocityX() {
        return 0;
    }

    @Override
    public double getDeltaVelocityY() {
        return 0;
    }

    @Override
    public void reset() {
    }
    
    @Override
    public void reset(double x, double y){
    }

    @Override
    public void update(double elapsedTime) {
    }
    
}

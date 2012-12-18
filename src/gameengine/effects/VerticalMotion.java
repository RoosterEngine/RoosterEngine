package gameengine.effects;

/**
 * Created with IntelliJ IDEA.
 * User: davidrusu
 * Date: 17/12/12
 * Time: 6:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class VerticalMotion implements MotionEffect {
    private MotionEffect motionEffect;

    public VerticalMotion(MotionEffect motionEffect){
        this.motionEffect = motionEffect;
    }

    @Override
    public double getVelocityX() {
        return 0;
    }

    @Override
    public double getVelocityY() {
        return motionEffect.getVelocityY();
    }

    @Override
    public double getDeltaVelocityX() {
        return 0;
    }

    @Override
    public double getDeltaVelocityY() {
        return motionEffect.getDeltaVelocityY();
    }

    @Override
    public void reset() {
        motionEffect.reset();
    }

    @Override
    public void reset(double x, double y) {
        motionEffect.reset(x, y);
    }

    @Override
    public void update(double elapsedTime) {
        motionEffect.update(elapsedTime);
    }
}

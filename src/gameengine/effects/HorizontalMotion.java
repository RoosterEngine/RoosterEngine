package gameengine.effects;

/**
 * Created with IntelliJ IDEA.
 * User: davidrusu
 * Date: 17/12/12
 * Time: 6:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class HorizontalMotion implements MotionEffect {
    private MotionEffect motionEffect;

    public HorizontalMotion(MotionEffect motionEffect){
        this.motionEffect = motionEffect;
    }

    @Override
    public double getVelocityX() {
        return motionEffect.getVelocityX();
    }

    @Override
    public double getVelocityY() {
        return 0;
    }

    @Override
    public double getDeltaVelocityX() {
        return motionEffect.getDeltaVelocityX();
    }

    @Override
    public double getDeltaVelocityY() {
        return 0;
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

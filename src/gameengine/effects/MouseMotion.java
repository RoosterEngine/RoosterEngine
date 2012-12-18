package gameengine.effects;

/**
 * Created with IntelliJ IDEA.
 * User: davidrusu
 * Date: 14/12/12
 * Time: 9:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class MouseMotion implements MotionEffect {
    private static double mouseVelocityX, mouseVelocityY, deltaVelocityX, deltaVelocityY;

    @Override
    public double getVelocityX() {
        return mouseVelocityX;
    }

    @Override
    public double getVelocityY() {
        return mouseVelocityY;
    }

    @Override
    public double getDeltaVelocityX() {
        double returnValue = deltaVelocityX;
        deltaVelocityX = 0;
        return returnValue;
    }

    @Override
    public double getDeltaVelocityY() {
        double returnValue = deltaVelocityY;
        deltaVelocityY = 0;
        return returnValue;
    }

    @Override
    public void reset() {
    }

    @Override
    public void reset(double x, double y) {
    }

    @Override
    public void update(double elapsedTime) {
    }

    public static void mouseMoved(double velocityX, double velocityY){
        deltaVelocityX = velocityX - mouseVelocityX;
        deltaVelocityY = velocityY - mouseVelocityY;
        mouseVelocityX = velocityX;
        mouseVelocityY = velocityY;
    }
}

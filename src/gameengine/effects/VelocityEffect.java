package gameengine.effects;

/**
 * Created with IntelliJ IDEA.
 * User: davidrusu
 * Date: 08/12/12
 * Time: 8:34 PM
 * To change this template use File | Settings | File Templates.
 */
public interface VelocityEffect {

    public double getDeltaVelocityX();

    public double getDeltaVelocityY();

    public double getVelocityX();

    public double getVelocityY();

    public void reset();

    public void reset(double x, double y);

    public void update(double elapsedTime);
}

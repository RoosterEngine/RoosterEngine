package gameengine.motion.environmentmotions;

import gameengine.entities.Entity;

/**
 * documentation
 * User: davidrusu
 * Date: 27/02/13
 * Time: 6:04 PM
 */
public class VelocityEnforcerWorldEffect extends WorldEffect {
    private double maxSpeed;
    private double ratio;
    private double maxSpeedRatio;

    public VelocityEnforcerWorldEffect(double maxSpeed, double ratio) {
        this.maxSpeed = maxSpeed;
        this.ratio = ratio;
        calcMaxSpeedRatio();
    }

    private void calcMaxSpeedRatio() {
        maxSpeedRatio = (1 - ratio) * maxSpeed;
    }

    public void setMaxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
        calcMaxSpeedRatio();
    }

    public void setRatio(double ratio) {
        this.ratio = ratio;
        calcMaxSpeedRatio();
    }

    @Override
    public void reset() {
    }

    @Override
    public void update(double elapsedTime) {
    }

    @Override
    public void applyEffect(Entity entity) {
        double dx = entity.getDX();
        double dy = entity.getDY();
        double currentVel = Math.sqrt(dx * dx + dy * dy);
        double speedMultiplier = (ratio + maxSpeedRatio / currentVel);
        entity.setVelocity(dx * speedMultiplier, dy * speedMultiplier);
    }
}

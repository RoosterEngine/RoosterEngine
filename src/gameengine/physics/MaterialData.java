package gameengine.physics;

/**
 * Wrapper for material properties that are between two surfaces, like friction and restitution.
 *
 * @author davidrusu
 */
public class MaterialData {
    private double friction, restitution;

    public MaterialData(double friction, double restitution) {
        setData(friction, restitution);
    }

    public double getFriction() {
        return friction;
    }

    public double getRestitution() {
        return restitution;
    }

    public void setData(double friction, double restitution) {
        this.friction = friction;
        this.restitution = restitution;
    }
}

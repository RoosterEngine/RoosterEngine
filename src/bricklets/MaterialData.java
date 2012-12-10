package bricklets;

/**
 * Created with IntelliJ IDEA.
 * User: davidrusu
 * Date: 28/11/12
 * Time: 3:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class MaterialData {
    private double friction, restitution;

    public MaterialData(double friction, double restitution) {
        setData(friction, restitution);
    }

    public double getFriction(){
        return friction;
    }

    public double getRestitution(){
        return restitution;
    }

    public void setData(double friction, double restitution){
        this.friction = friction;
        this.restitution = restitution;
    }
}

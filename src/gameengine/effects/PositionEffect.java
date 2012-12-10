package gameengine.effects;

/**
 *
 * @author davidrusu
 */
public interface PositionEffect {
    
    public double getX();
    
    public double getY();
    
    public void reset();
    
    public void reset(double x, double y);
    
    public void update(double elapsedTime);
}

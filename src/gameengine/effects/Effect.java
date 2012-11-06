/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gameengine.effects;

/**
 *
 * @author davidrusu
 */
public interface Effect {
    
    public double getX();
    
    public double getY();
    
    public void reset();
    
    public void reset(double x, double y);
    
    public void update(double elapsedTime);
}

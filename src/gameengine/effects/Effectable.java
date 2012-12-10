/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gameengine.effects;

/**
 *
 * @author davidrusu
 */
public interface Effectable {
    
    public double getX();
    
    public double getY();
    
    public double getWidth();
    
    public double getHeight();
    
    public PositionEffect getCurrentEffect();
    
    public void setPositionEffect(PositionEffect positionEffect);
    
}

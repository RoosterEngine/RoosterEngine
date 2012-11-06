/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gameengine.effects;

/**
 *
 * @author davidrusu
 */
public class NoEffect implements Effect{
    private double x, y;
    
    public NoEffect(double x, double y){
        this.x = x;
        this.y = y;
    }
    
    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public void reset() {
    }
    
    @Override
    public void reset(double x, double y){
        this.x = x;
        this.y = y;
    }

    @Override
    public void update(double elapsedTime) {
    }
    
}

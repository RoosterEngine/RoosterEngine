/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gameengine.math;

/**
 *
 * @author davidrusu
 */
public abstract class ParametricFunction {
    
    public double slope(double t){
        return dyValueAt(t) / dxValueAt(t);
    }
    
    public abstract double xValueAt(double t);
    
    public abstract double yValueAt(double t);
    
    public abstract double dxValueAt(double t);
    
    public abstract double dyValueAt(double t);
}

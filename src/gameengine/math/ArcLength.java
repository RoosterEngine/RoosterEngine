package gameengine.math;

/**
 *
 * @author davidrusu
 */
public class ArcLength implements Function{
    
    private ParametricFunction function;
    
    public ArcLength(ParametricFunction function){
        this.function = function;
    }

    @Override
    public double valueAt(double t) {
        double dx = function.dxValueAt(t);
        double dy = function.dyValueAt(t);
        return Math.sqrt(dx * dx + dy * dy);
    }
    
    public double getArcLength(double from, double to, int numSteps){
        return Utilities.simpsonsRule(from, to, numSteps, this);
    }
}
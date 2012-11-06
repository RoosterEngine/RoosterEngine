package gameengine.math;

/**
 *
 * @author davidrusu
 */
public class Utilities {
    
    /**
     * 
     * @param a
     * @param b
     * @param n must be even
     * @return 
     */
    public static double simpsonsRule(double a, double b, int n, Function f) {
        double delta = (b - a) / n;
        double x = a;
        double sum4 = 0; // to be multiplied by 4
        double sum2 = 0; // to be multiplied by 2
        int iterations = (n - 2) / 2;
        for(int i = 0; i < iterations; i++){
            x += delta;
            sum4 += f.valueAt(x);
            x += delta;
            sum2 += f.valueAt(x);
        }
        double sum = f.valueAt(a) + 4 * (f.valueAt(b - delta) + sum4) + 2 * sum2 + f.valueAt(b);
        return sum * delta * (1 / 3.0);
    }
    
    public static double simpsonsRule(Function f, double a, double b){
        double half = (b - a) / 2;
        return half / 3 * (f.valueAt(a) + 4 * f.valueAt(a + half) + f.valueAt(b));
    }
    
    public static double recursiveSimpsonsRule(Function f, double a, double b, double eps, double whole){
        double c = (a + b) / 2;
        double left = simpsonsRule(f, a, c);
        double right = simpsonsRule(f, c, b);
        double leftRightSum = left + right;
        double sumDifference = leftRightSum - whole;
        if(Math.abs(sumDifference) / 15 <= eps){
            return leftRightSum + sumDifference / 15.0;
        }
        return recursiveSimpsonsRule(f, a, c, eps / 2, left) + recursiveSimpsonsRule(f, c, b, eps/ 2, right);
    }
    
    public static double adaptiveSimpsonsRule(Function f, double a, double b, double eps){
        return recursiveSimpsonsRule(f, a, b, eps, simpsonsRule(f, a, b));
    }
}
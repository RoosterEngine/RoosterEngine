package gameengine.math;

/**
 * @author davidrusu
 */
public class Utils {
    private static double simpsonsRule(Function f, double from, double to) {
        double halfDelta = (to - from) / 2;
        return halfDelta / 3 * (f.valueAt(from) + 4 * f.valueAt(from + halfDelta) + f.valueAt(to));
    }

    private static double recursiveSimpsonsRule(Function f, double from, double to, double eps,
                                                double whole) {
        double center = (from + to) * 0.5;
        double left = simpsonsRule(f, from, center);
        double right = simpsonsRule(f, center, to);
        double leftRightSum = left + right;
        double sumDifference = leftRightSum - whole;
        if (Math.abs(sumDifference) / 15 <= eps) {
            return leftRightSum + sumDifference / 15.0;
        }
        double halfEps = eps * 0.5;
        return recursiveSimpsonsRule(f, from, center, halfEps, left) + recursiveSimpsonsRule(f,
                center, to, halfEps, right);
    }

    public static double adaptiveSimpsonsRule(Function f, double from, double to, double eps) {
        double a = Math.min(from, to);
        double b = Math.max(from, to);
        return recursiveSimpsonsRule(f, a, b, eps, simpsonsRule(f, a, b));
    }

    /**
     * Returns the next power of 2 that is greater or equal to the specified value.
     *
     * @param n The value
     * @return The next power of power of 2
     */
    public static int nextPowerOf2(int n) {
        assert n > 0;

        n--;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        return n + 1;
    }
}
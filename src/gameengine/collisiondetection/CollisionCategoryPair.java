package gameengine.collisiondetection;

/**
 * @author davidrusu
 */
public class CollisionCategoryPair {
    private int a, b;

    public CollisionCategoryPair(int a, int b) {
        this.a = a;
        this.b = b;
    }

    public int getA() {
        return a;
    }

    public int getB() {
        return b;
    }
}
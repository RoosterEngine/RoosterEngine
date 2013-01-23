package gameengine.collisiondetection;

/**
 * @author davidrusu
 */
public class CollisionPair {
    private int a, b;

    public CollisionPair(int a, int b) {
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
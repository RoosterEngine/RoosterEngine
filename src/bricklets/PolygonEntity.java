package bricklets;

import java.awt.*;

/**
 * @author david
 */
public class PolygonEntity extends Entity {
    private Polygon polygon;
    private Color color = Color.BLACK;

    public PolygonEntity(double x, double y, double radius, int minPoints, int maxPoints) {
        super(x, y, radius, radius);
        Material material = Material.createCustomMaterial(0.2, 1);
        polygon = Polygon.getRandomConvexPolygon(x, y, radius, radius, minPoints, maxPoints, this, material);
    }

    public Polygon getPolygonShape() {
        return polygon;
    }

    @Override
    public void update(double elapsedTime) {
    }

    @Override
    public void draw(Graphics2D g) {
        polygon.draw(g, color);
    }

}

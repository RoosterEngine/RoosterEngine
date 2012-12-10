package bricklets;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 *
 * @author david
 */
public class CircleEntity extends Entity{
    protected double radius;
    
    public CircleEntity(double x, double y, double radius){
        super(x, y, Color.BLACK);
        this.radius = radius;
    }

    public CircleEntity(Entity entity, double radius){
        super(entity.x, entity.y, entity.dx, entity.dy, entity.restitution, entity.friction, entity.mass, entity.color);
        this.radius = radius;
    }
    
    public double getRadius(){
        return radius;
    }
    
    @Override
    public void update(double elapsedTime) {
        x += dx * elapsedTime;
        y += dy * elapsedTime;
        dx += ddx * elapsedTime;
        dy += ddy * elapsedTime;
        ddy = g * elapsedTime;
        ddx = 0;
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(color);
        g.fillOval((int)(x - radius), (int)(y - radius), (int)(radius * 2), (int)(radius * 2));
        g.setColor(color.darker());
        double scale = radius * 2;
        g.drawLine((int)x, (int)y, (int)(x + dx * scale), (int)(y + dy * scale));
    }
}

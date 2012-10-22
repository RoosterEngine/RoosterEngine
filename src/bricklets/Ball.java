package bricklets;

import gameengine.Context;
import gameengine.GameController;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 *
 * @author davidrusu
 */
public class Ball extends CollidableCircle{
    private String name = "";
    
    public Ball(Ball ball){
        this(ball.context, new Vector2D(ball.position), new Vector2D(ball.velocity), ball.restitution, ball.radius, ball.color, ball.mass);
    }
    
    public Ball(Context context, double x, double y, double radius){
        this(context, x, y, 0, 0, 1, radius, Color.ORANGE);
    }
    
    public Ball(Context context, double x, double y, double radius, double mass){
        this(context, x, y, 0, 0, 1, radius, Color.ORANGE, mass);
    }
    
    public Ball(Context context, double x, double y, double radius, String name){
        this(context, x, y, 0, 0, 1, radius, Color.ORANGE);
        this.name = name;
    }
    
    public Ball(Context context, double x, double y, double dx, double dy, double restitution, double radius, Color color){
        this(context, new Vector2D(x, y), new Vector2D(dx, dy), restitution, radius, color, 1);
    }
    
    public Ball(Context context, double x, double y, double dx, double dy, double restitution, double radius, Color color, double mass){
        this(context, new Vector2D(x, y), new Vector2D(dx, dy), restitution, radius, color, mass);
    }
    
    public Ball(Context context, Vector2D position, Vector2D velocity, double restitution, double radius, Color color, double mass){
        super(context, position, velocity, radius, restitution, color);
        this.mass = mass;
    }
    
    public double getRadius(){
        return radius;
    }
    
    @Override
    public void update(double elapsedTime) {
        Vector2D tempVelocity = new Vector2D(velocity);
        position.add(tempVelocity.scale(elapsedTime));
        acceleration.add(0, g);
        velocity.add(acceleration);
        velocity.scale(air);
        acceleration.clear();
    }

    @Override
    public void draw(Graphics2D g) {
        int x = (int)(position.getX());
        int y = (int)(position.getY());
        g.setColor(color);
        g.fillOval((int)(x - radius), (int)(y - radius), (int)(radius * 2), (int)(radius * 2));
//        g.setColor(Color.RED);
//        g.drawString("vel " + velocity, 20, (int)(controller.getHeight() - 50));
    }
    
    @Override
    public String toString(){
        return name + " (" + (int)position.getX() + ", " + (int)position.getY() + ")";
    }
}
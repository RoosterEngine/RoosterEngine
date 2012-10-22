package bricklets;

import gameengine.Context;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 *
 * @author davidrusu
 */
public class Paddle extends CollidableRect{
    
    public Paddle(Paddle paddle){
        this(paddle.context, new Vector2D(paddle.position), new Vector2D(paddle.velocity), paddle.restitution, paddle.width, paddle.height, paddle.color);
    }
    
    public Paddle(Context context, double x, double y, double width, double height){
        this(context, new Vector2D(x, y), new Vector2D(), 1, width, height, Color.WHITE);
    }
    
    public Paddle(Context context, Vector2D pos, Vector2D vel, double restitution, double width, double height, Color color){
        super(context, pos, vel, restitution, width, height, color);
        mass = 10;
    }
    
    public void clearVelocity(){
        velocity.clear();
    }
    
    public void clearAcceleration(){
        acceleration.clear();
    }
    
    @Override
    public void update(double elapsedTime) {
        Vector2D tempVelocity = new Vector2D(velocity);
        position.add(tempVelocity.scale(elapsedTime));
//        acceleration.add(0, g);
        velocity.add(acceleration.scale(elapsedTime));
//        velocity.scale(air);
        acceleration.clear();
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(color);
        g.fillRect((int)(position.getX() - halfWidth), (int)(position.getY() - halfHeight), (int)width, (int)height);
        g.drawString(toString(), (int)(position.getX() + width), (int)(position.getY() + height));
    }
    
    @Override
    public String toString(){
        return "Paddle y: " + (int)position.getX() + " x: " + (int)position.getY() + " width: " + width + " height: " + height;
    }
}
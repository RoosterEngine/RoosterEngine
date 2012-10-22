package bricklets;

import gameengine.Context;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 *
 * @author davidrusu
 */
class Brick extends CollidableRect {
    
    public Brick(Brick brick){
        this(brick.context, new Vector2D(brick.position), new Vector2D(brick.velocity), brick.restitution, brick.width, brick.height, brick.color);
    }
    
    public Brick(Context context, double x, double y, double width, double height){
        this(context, new Vector2D(x, y), new Vector2D(), 1, width, height, Color.WHITE);
    }
    
    public Brick(Context context, Vector2D pos, Vector2D vel, double restitution, double width, double height, Color color){
        super(context, pos, vel, restitution, width, height, color);
        mass = 1;
//        polygon = Polygon.getRandomConvexPolygon(50, 50, 3, 3, 0);
        polygon = Polygon.getRectanglePolygon(0, 0, 50, 50);
        polygon.updatePosition(pos.getX(), pos.getY());
    }
    
    @Override
    public void update(double elapsedTime) {
        Vector2D tempVector = new Vector2D(velocity);
        position.add(tempVector.scale(elapsedTime));
        polygon.updatePosition(position.getX(), position.getY());
        acceleration.add(0, g);
        tempVector.set(acceleration).scale(elapsedTime);
        velocity.add(tempVector);
        velocity.scale(air);
        acceleration.clear();
    }

    @Override
    public void draw(Graphics2D g) {
        polygon.draw(g);
        
        g.setColor(Color.RED);
        g.drawString(velocity.toString(), (int)(position.getX() + radius), (int)(position.getY()));
        
        double scale = 50;
        int vX = (int)(debugVector.getX() * scale + position.getX());
        int vY = (int)(debugVector.getY() * scale + position.getY());
        int centerX = (int)(position.getX());
        int centerY = (int)(position.getY());
        g.setColor(Color.GREEN);
        g.drawLine(centerX, centerY, vX, vY);
        scale = 100;
        vX = (int)(velocity.getX() * scale + position.getX());
        vY = (int)(velocity.getY() * scale + position.getY());
        centerX = (int)(position.getX());
        centerY = (int)(position.getY());
        g.setColor(Color.CYAN);
        g.drawLine(centerX, centerY, vX, vY);
    }
    
    @Override
    public String toString(){
        return "Brick y: " + (int)position.getX() + " x: " + (int)position.getY() + " width: " + width + " height: " + height;
    }
}
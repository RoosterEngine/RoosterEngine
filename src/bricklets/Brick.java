package bricklets;

import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: davidrusu
 * Date: 09/12/12
 * Time: 12:35 AM
 * To change this template use File | Settings | File Templates.
 */
public class Brick extends AABBEntity {
    private double health = 100;

    public Brick(double x, double y, double width, double height) {
        super(x, y, width, height);
    }

    public void doDamage(double amount){
        health -= amount;
    }

    public boolean isAlive(){
        return health > 0;
    }

    @Override
    public void update(double elapsedTime){
        super.update(elapsedTime);
    }

    @Override
    public void draw(Graphics2D g){
        g.setColor(color);
        g.fillRect((int)(x - halfWidth), (int)(y - halfHeight), (int)(width), (int)(height));
    }

}

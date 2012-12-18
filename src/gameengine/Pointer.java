package gameengine;

import bricklets.Entity;
import gameengine.effects.MouseMotion;

import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: davidrusu
 * Date: 17/12/12
 * Time: 6:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class Pointer extends Entity {
    Graphic graphic;

    public Pointer(Graphic graphic){
        super(0, 0, graphic.getWidth(), graphic.getHeight());
        this.graphic = graphic;
        setMotionEffect(new MouseMotion());
    }

    @Override
    public void update(double elapsedTime) {
    }

    @Override
    public void draw(Graphics2D g) {
        graphic.draw(g, (int)x, (int)y);
    }
}

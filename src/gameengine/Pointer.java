package gameengine;

import bricklets.Entity;
import gameengine.motion.motions.MouseMotion;

import java.awt.*;

/**
 * Used as a mouse pointer
 *
 * User: davidrusu
 * Date: 17/12/12
 * Time: 6:53 PM
 */
public class Pointer extends Entity {
    Graphic graphic;

    public Pointer(Graphic graphic, double startX, double startY) {
        super(0, 0, graphic.getWidth(), graphic.getHeight());
        setPosition(startX, startY);
        this.graphic = graphic;
        setMotion(new MouseMotion());
    }

    @Override
    public void update(double elapsedTime) {
    }

    @Override
    public void draw(Graphics2D g) {
        graphic.draw(g, (int) x, (int) y);
    }
}

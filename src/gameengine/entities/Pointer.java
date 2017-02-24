package gameengine.entities;

import gameengine.collisiondetection.shapes.CircleShape;
import gameengine.graphics.Renderer;
import gameengine.graphics.image.Graphic;
import gameengine.motion.motions.MouseMotion;

/**
 * Used as a mouse pointer.
 *
 * @author davidrusu
 */
public class Pointer extends Entity {
    Graphic graphic;

    public Pointer(Graphic graphic, double startX, double startY) {
        super(startX, startY, new CircleShape(graphic.getWidth() / 2));
        this.graphic = graphic;
        setMotion(new MouseMotion());
    }

    @Override
    public void update(double elapsedTime) {
    }

    @Override
    public void draw(Renderer renderer) {
        graphic.draw(renderer, x, y);
    }
}

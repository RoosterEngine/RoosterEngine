package gameengine.graphics.image;

import gameengine.graphics.Renderer;

/**
 * A {@link Graphic} that does not render anything.
 *
 * @author davidrusu
 */
public class NoGraphic implements Graphic {
    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public void draw(Renderer renderer, double x, double y) {
    }

    @Override
    public void discardAndCleanup() {
    }
}

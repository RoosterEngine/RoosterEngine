package gameengine.math;

import gameengine.Graphic;

import java.awt.*;

/**
 * A {@link Graphic} that does not render anything
 *
 * User: davidrusu
 * Date: 17/12/12
 * Time: 8:50 PM
 */
public class NoGraphic implements Graphic {

    @Override
    public void update(double elapsedTime) {
    }

    @Override
    public void reset() {
    }

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void draw(Graphics2D g, int x, int y) {
    }
}

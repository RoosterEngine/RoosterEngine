package gameengine;

import java.awt.Graphics2D;

/**
 *
 * @author danrusu
 */
public interface Graphic {

    public void update(double elapsedTime);

    public void reset();
    
    public int getWidth();

    public int getHeight();
    
    public void resize(int width, int height);

    public void draw(Graphics2D g, int x, int y);
}
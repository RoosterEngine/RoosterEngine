package bricklets;

import gameengine.graphics.Graphic;

import java.awt.*;
import java.util.Random;

/**
 * @author david
 */
public class WavyGraphic implements Graphic {

    private int x, y, width, height;
    private double waveScale = 1, shiftX = 0, shiftY = 0;
    private WavyBall[] balls;
    private Color backgroundColor, foregroundColor;

    public WavyGraphic() {
        this(Color.BLACK, Color.WHITE);
    }

    public WavyGraphic(Color bg, Color fg) {
        this(bg, fg, 0, 0, 0, 0);
    }

    public WavyGraphic(Color backgroundColor, Color foregroundColor, int x, int y, int width, int height) {
        this.foregroundColor = foregroundColor;
        this.backgroundColor = backgroundColor;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        balls = new WavyBall[1000];
        for (int i = 0; i < balls.length; i++) {
            balls[i] = new WavyBall(0, 0);
        }
        reset();
    }

    public void setForeGround(Color fg) {
        this.foregroundColor = fg;
    }

    public void setBackGround(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setDim(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        reset();
    }

    @Override
    public void reset() {
        Random rand = new Random();
        for (WavyBall ball : balls) {
            ball.setPos(rand.nextDouble() * width, rand.nextDouble() * height);
            ball.clearVel();
        }
    }

    @Override
    public void update(double elapsedTime) {
        for (WavyBall ball : balls) {
            ball.update(elapsedTime, waveScale, shiftX, shiftY, x, y, width, height);
        }
        double sinTime = Math.sin(System.nanoTime() / 10000000000.0);
        waveScale = sinTime * 100 + 125;
        shiftX += 0;//sinTime * 0.0005;
        shiftY += 0;//sinTime * 0.0005;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void draw(Graphics2D g, int x, int y) {
        g.translate(x, y);
        g.setColor(backgroundColor);
        g.fillRect(x, y, width, height);
        g.setColor(foregroundColor);
        for (WavyBall ball : balls) {
            ball.draw(g);
        }
        g.translate(-x, -y);
    }

    @Override
    public void resize(int width, int height) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
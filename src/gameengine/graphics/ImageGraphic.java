package gameengine.graphics;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author danrusu
 */
public class ImageGraphic implements Graphic {

    private BufferedImage image;

    public ImageGraphic(BufferedImage image) {
        this.image = image;
    }

    @Override
    public void reset() {
    }

    @Override
    public void update(double elapsedTime) {
    }

    @Override
    public int getWidth() {
        return image.getWidth();
    }

    @Override
    public int getHeight() {
        return image.getHeight();
    }

    public Color[][] getPixels(int pixelSize) {
        int width = image.getWidth() / pixelSize;
        int height = image.getHeight() / pixelSize;
        Color[][] result = new Color[width][height];
        int numPixels = pixelSize * pixelSize;
        int red, green, blue, alpha;
        int xx = 0;
        int yy = 0;

        for (int x = 0; x < width; x++) {
            yy = 0;
            for (int y = 0; y < height; y++) {
                red = 0;
                green = 0;
                blue = 0;
                alpha = 0;
                for (int i = 0; i < pixelSize; i++) {
                    for (int j = 0; j < pixelSize; j++) {
                        Color temp = new Color(image.getRGB(xx + i, yy + j), true);
                        red += temp.getRed();
                        green += temp.getGreen();
                        blue += temp.getBlue();
                        alpha += temp.getAlpha();
                    }
                }
                yy += pixelSize;
                result[x][y] = new Color(red / numPixels, green / numPixels, blue / numPixels, alpha / numPixels);
            }
            xx += pixelSize;
        }
        return result;
    }

    @Override
    public void draw(Graphics2D g, int x, int y) {
        g.drawImage(image, x, y, null);
    }

    @Override
    public void resize(int width, int height) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
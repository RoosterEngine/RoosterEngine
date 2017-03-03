package gameengine.graphics.java2D;

import gameengine.geometry.Vector2D;
import gameengine.graphics.Renderer;
import gameengine.graphics.ScreenManager;
import gameengine.graphics.image.Graphic;
import gameengine.input.KeyController;
import gameengine.input.MouseController;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Java2DScreenManager implements ScreenManager {
    private GraphicsDevice device = null;
    private BufferStrategy bufferStrategy = null;
    private Java2DRenderer renderer;
    private Graphics2D g2 = null;
    private Frame frame;

    public Java2DScreenManager(KeyController keyboard) {
        frame = new Frame();
        frame.addKeyListener(keyboard);
        // allows input of the Tab and other focus traversal keys
        frame.setFocusTraversalKeysEnabled(false);

        device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        renderer = new Java2DRenderer();
    }

    @Override
    public void addMouseListener(MouseController mouse) {
        frame.addMouseListener(mouse);
        frame.addMouseMotionListener(mouse);
        frame.addMouseWheelListener(mouse);
    }

    @Override
    public int getWidth() {
        return device.getDisplayMode().getWidth();
    }

    @Override
    public int getHeight() {
        return device.getDisplayMode().getHeight();
    }

    public DisplayMode[] getCompatibleDisplayModes() {
        return device.getDisplayModes();
    }

    public DisplayMode getCurrentDisplayMode() {
        return device.getDisplayMode();
    }

    //returns true if successful
    public boolean changeDisplayMode(DisplayMode displayMode) {
        if (displayMode == null || !device.isDisplayChangeSupported()) {
            return false;
        }
        try {
            device.setDisplayMode(displayMode);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }

    @Override
    public void initializeWindow() {
        setFullScreen();
//        setWindowed(800, 600);
        frame.setIgnoreRepaint(true);
        frame.createBufferStrategy(2);
        bufferStrategy = frame.getBufferStrategy();
        //hide mouse cursor
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        frame.setCursor(toolkit.createCustomCursor(toolkit.getImage(""), new Point(0, 0),
                "invisible"));
    }

    @Override
    public void restoreWindow() {
        Window window = device.getFullScreenWindow();
        if (window != null) {
            //restore mouse cursor
            window.setCursor(Cursor.getDefaultCursor());
            window.dispose();
        }

        device.setFullScreenWindow(null);
    }

    public void setFullScreen() {
        frame.setUndecorated(true);
        frame.setResizable(false);
        device.setFullScreenWindow(frame);
    }

    public void setWindowed(int width, int height) {
        frame.setTitle("RoosterEngine");
        frame.setSize(new Dimension(width, height));
        frame.setUndecorated(false);
        frame.setVisible(true);
    }

    @Override
    public Renderer initializeFrame() {
        g2 = (Graphics2D) bufferStrategy.getDrawGraphics();
        g2.fillRect(0, 0, getWidth(), getHeight());
        return renderer;
    }

    @Override
    public void RenderFrame() {
        g2.dispose();
        g2 = null;//makes sure that nobody tries to draw until the next frame is initialized
        if (!bufferStrategy.contentsLost()) {
            bufferStrategy.show();
        }
        Toolkit.getDefaultToolkit().sync(); //sync the display for some systems
    }

    @Override
    public Graphic loadImage(String path) throws IOException {
        BufferedImage im = ImageIO.read(getClass().getResource(path));
        Graphic result = new Java2DImageGraphic(createCompatibleImage(im));
        im.flush();//save the memory right away
        return result;
    }

    /**
     * Copies the specified image into a new BufferedImage that is compatible with the screen
     * resulting in better performance.
     *
     * @param image The image to be copied
     * @return A copy of the specified image
     */
    private BufferedImage createCompatibleImage(BufferedImage image) {
        int transparency = image.getColorModel().getTransparency();
        BufferedImage im = device.getDefaultConfiguration().createCompatibleImage(image.getWidth
                (), image.getHeight(), transparency);
        Graphics2D g = im.createGraphics();
        g.drawImage(image, 0, 0, null); //copy
        g.dispose();
        return im;
    }

    /**
     * A renderer that is compatible with the Java2DScreenManager.
     */
    public class Java2DRenderer extends Renderer {
        private int pointDiameter = 1;
        /**
         * Cache the current foreground color to avoid setting the foreground color to the save
         * value unnecessarily.
         */
        private float foregroundRed = -1.0f, foregroundGreen = -1.0f, foregroundBlue = -1.0f;

        private int[] tempX = new int[10];
        private int[] tempY = new int[10];

        private Java2DRenderer() {
        }

        @Override
        public void setForegroundColor(float red, float green, float blue) {
            if (red == foregroundRed & green == foregroundGreen & blue == foregroundBlue) {
                return;//ignore since the foreground color is already the same
            }
            foregroundRed = red;
            foregroundGreen = green;
            foregroundBlue = blue;
            g2.setColor(new Color(red, green, blue));
        }

        @Override
        public void setBackgroundColor(float red, float green, float blue) {
            g2.setBackground(new Color(red, green, blue));
        }

        @Override
        public void setZIndex(float index) {
            //don't draw everything right away but instead keep track of all request in lists and
            // perform all the rendering when finalizing the frame
            //store a list of lists (1 list for each z-index)
            //when changing the z-index, I could binary search for the location to insert
        }

        @Override
        public void scale(double scale) {
            g2.scale(scale, scale);
        }

        @Override
        public void setLineWidth(double width) {
            // TODO Auto-generated method stub
        }

        @Override
        public void setPointSize(double diameter) {
            pointDiameter = (int) diameter;
        }

        @Override
        public void rotate(double radians, double aboutX, double aboutY) {
            g2.rotate(radians, aboutX, aboutY);
        }

        @Override
        public void translate(double x, double y) {
            g2.translate(x, y);

        }

        @Override
        public void drawString(String s, double x, double y) {
            g2.drawString(s, (int) x, (int) y);
        }

        @Override
        public FontMetrics getFontMetrics() {
            return g2.getFontMetrics();
        }

        @Override
        public void drawPoint(double x, double y) {
            g2.fillOval((int) x, (int) y, pointDiameter, pointDiameter);
        }

        @Override
        public void drawPoints(Vector2D[] points) {
            for (int i = 0; i < points.length; i++) {
                Vector2D p = points[i];
                g2.fillOval((int) p.getX(), (int) p.getY(), pointDiameter, pointDiameter);
            }
        }

        @Override
        public void drawLine(double x1, double y1, double x2, double y2) {
            g2.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
        }

        @Override
        public void drawLineStrip(Vector2D[] points, double x, double y) {
            populateTempValues(points);
            g2.translate(x, y);
            g2.drawPolyline(tempX, tempY, points.length);
            g2.translate(-x, -y);
        }

        @Override
        public void drawRect(double centerX, double centerY, double halfWidth, double halfHeight) {
            int x = (int) (centerX - halfWidth);
            int y = (int) (centerY - halfHeight);
            g2.drawRect(x, y, (int) (halfWidth + halfWidth), (int) (halfHeight + halfHeight));

        }

        @Override
        public void fillRect(double centerX, double centerY, double halfWidth, double halfHeight) {
            int x = (int) (centerX - halfWidth);
            int y = (int) (centerY - halfHeight);
            g2.fillRect(x, y, (int) (halfWidth + halfWidth), (int) (halfHeight + halfHeight));
        }

        @Override
        public void drawCircle(double centerX, double centerY, double radius) {
            int diameter = (int) (2 * radius);
            g2.drawOval((int) (centerX - radius), (int) (centerY - radius), diameter, diameter);
        }

        @Override
        public void fillCircle(double centerX, double centerY, double radius) {
            int diameter = (int) (2 * radius);
            g2.fillOval((int) (centerX - radius), (int) (centerY - radius), diameter, diameter);
        }

        @Override
        public void drawOval(double centerX, double centerY, double width, double height) {
            g2.drawOval((int) (centerX - width / 2), (int) (centerY - height / 2), (int) width,
                    (int) height);
        }

        @Override
        public void fillOval(double centerX, double centerY, double width, double height) {
            g2.fillOval((int) (centerX - width / 2), (int) (centerY - height / 2), (int) width,
                    (int) height);
        }

        @Override
        public void drawPolygon(Vector2D[] points, double offsetX, double offsetY) {
            populateTempValues(points);
            g2.translate(offsetX, offsetY);
            g2.drawPolygon(tempX, tempY, points.length);
            g2.translate(-offsetX, -offsetY);
        }

        @Override
        public void fillPolygon(Vector2D[] points, double offsetX, double offsetY) {
            populateTempValues(points);
            g2.translate(offsetX, offsetY);
            g2.fillPolygon(tempX, tempY, points.length);
            g2.translate(-offsetX, -offsetY);
        }

        private void populateTempValues(Vector2D[] points) {
            int numPoints = points.length;
            if (numPoints > tempX.length) {
                tempX = new int[numPoints];
                tempY = new int[numPoints];
            }
            for (int i = 0; i < numPoints; i++) {
                tempX[i] = (int) points[i].getX();
                tempY[i] = (int) points[i].getY();
            }
        }
    }

    /**
     * A graphic that is compatible with the Java2DScreenManager.
     */
    private class Java2DImageGraphic implements Graphic {
        private BufferedImage image;

        /**
         * Creates a Java2DImageGraphic instance.
         *
         * @param image The buffered image to be drawn
         */
        private Java2DImageGraphic(BufferedImage image) {
            this.image = image;
        }

        @Override
        public int getWidth() {
            return image.getWidth();
        }

        @Override
        public int getHeight() {
            return image.getHeight();
        }

        @Override
        public void draw(Renderer renderer, double x, double y) {
            g2.drawImage(image, (int) x, (int) y, null);
        }

        @Override
        public void discardAndCleanup() {
            image.flush();
            image = null;
        }
    }
}
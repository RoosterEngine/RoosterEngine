package gameengine;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

/**
 *
 * @author danrusu
 */
public class ScreenManager {
    
    private GraphicsDevice device;
    private BufferStrategy bufferStrategy;

    public ScreenManager(){
        device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    }

    public Window getFullScreenWindow(){
        return device.getFullScreenWindow();
    }

    public DisplayMode[] getCompatibleDisplayModes(){
        return device.getDisplayModes();
    }

    public DisplayMode getCurrentDisplayMode(){
        return device.getDisplayMode();
    }

    //returns true if successful
    public boolean changeDisplayMode(DisplayMode displayMode){
        if(displayMode == null || !device.isDisplayChangeSupported()){
            return false;
        }
        try{
            device.setDisplayMode(displayMode);
        }catch(IllegalArgumentException e){
            return false;
        }
        return true;
    }

    public void setFullScreen(){
        Frame frame = new Frame();
        frame.setUndecorated(true);
        frame.setIgnoreRepaint(true);
        frame.setResizable(false);
        device.setFullScreenWindow(frame);
        frame.createBufferStrategy(2);
        bufferStrategy = frame.getBufferStrategy();
    }
    
    /**
     * double buffering is used, so applications need to call updateGraphics()
     * to show any graphics.
     * The graphics object must be disposed by the user
     * @return {@link Graphics2d}
     */
    public Graphics2D getGraphics(){
        return (Graphics2D)bufferStrategy.getDrawGraphics();
    }

    public void updateGraphics(){
        if(!bufferStrategy.contentsLost()){
            bufferStrategy.show();
        }
        Toolkit.getDefaultToolkit().sync(); //sync the display for some systems
    }

    /**
     * Resets the display mode.
     */
    public void restoreScreen(){
        Window window = device.getFullScreenWindow();
        if(window != null){
            window.dispose();
        }
        device.setFullScreenWindow(null);
    }
    
    /**
     * Copies the provided image into an image that is compatible with the screen
     * @param image
     * @return compatible version of the provided image
     */
    public BufferedImage getCompatibleImageVersion(BufferedImage image){
        int transparency = image.getColorModel().getTransparency();
        BufferedImage result = device.getDefaultConfiguration().createCompatibleImage(image.getWidth(), image.getHeight(), transparency);
        Graphics2D g = result.createGraphics();
        g.drawImage(image, 0, 0, null); //copy
        g.dispose();
        return result;
    }
    
    /**
     * Creates a {@link BufferedImage} that is compatible with the screen
     * @param width the width of the image
     * @param height the height of the image
     * @param transparencyType must be either Transparency.OPAQUE, Transparency.BITMASK, or Transparency.TRANSLUCENT
     * @return an image that is compatible with the screen
     */
    public BufferedImage createCompatibleImage(int width, int height, int transparencyType){
        return device.getDefaultConfiguration().createCompatibleImage(width, height, transparencyType);
    }
}
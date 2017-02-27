package gameengine.graphics;

import gameengine.graphics.image.Graphic;
import gameengine.input.MouseController;

import java.io.IOException;

/**
 * Represents a screen manager for interacting with the screen and with graphics.
 */
public interface ScreenManager {
    /**
     * Initialize a new frame.  This should be called at the beginning of each frame.
     *
     * @return A renderer that can be used to draw into the frame
     */
    Renderer initializeFrame();

    /**
     * Render the current frame.  This must be called after calling {@link #initializeFrame()} and
     * after drawing in the current frame has completed.
     */
    void RenderFrame();

    /**
     * Initializes the window (eg. full screen, resolution, refresh rate, etc.).
     */
    void initializeWindow();

    /**
     * Restores the screen back to how it was.  This should be called prior to exiting the game.
     */
    void restoreWindow();

    /**
     * Registers the specified mouse controller to listen for mouse events.
     *
     * @param mouse The mouse controller
     */
    void addMouseListener(MouseController mouse);

    /**
     * Loads a Graphic from the specified path.
     *
     * @param path The path to the graphic
     * @return A graphic containing the image data from the specified path
     * @throws IOException If any errors are encountered while trying to load the image
     */
    Graphic loadImage(String path) throws IOException;

    /**
     * @return The screen width.
     */
    int getWidth();

    /**
     * @return The screen height.
     */
    int getHeight();
}

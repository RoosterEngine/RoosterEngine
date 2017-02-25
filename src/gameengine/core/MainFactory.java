package gameengine.core;

import gameengine.graphics.ScreenManager;
import gameengine.graphics.java2D.Java2DScreenManager;
import gameengine.input.KeyController;
import gameengine.input.MouseController;

/**
 * The main factory creates appropriate components based on the environment.
 */
public class MainFactory {
    /**
     * Creates the screen manager.
     *
     * @return a new Java2DScreenManager
     */
    protected static ScreenManager createScreenManager(KeyController keyboard) {
        return new Java2DScreenManager(keyboard);
    }

    /**
     * Creates the keyboard controller.
     *
     * @param core The game core
     * @return a new KeyController
     */
    protected static KeyController createKeyController(GameCore core) {
        return new KeyController(core);
    }

    /**
     * Creates the mouse controller.
     *
     * @param core             The game core
     * @param screen           The screen manager
     * @param desiredFramerate The desired frame rate
     * @return a new MouseController
     */
    protected static MouseController createMouseController(GameCore core, ScreenManager screen,
                                                           int desiredFramerate) {
        MouseController mouse = new MouseController(core, screen, desiredFramerate);
        screen.addMouseListener(mouse);
        return mouse;
    }

    /**
     * Creates the game timer.
     *
     * @param core             The game core
     * @param desiredFrameRate The desired frame rate
     * @param minFrameRate     The minimum acceptable frame rate
     * @return a new GameTimer
     */
    protected static GameTimer createGameTimer(GameCore core, int desiredFrameRate, int
            minFrameRate) {
        return new GameTimer(core, desiredFrameRate, minFrameRate);
    }
}

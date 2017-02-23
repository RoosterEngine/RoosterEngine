package gameengine.core;

import gameengine.context.Context;
import gameengine.graphics.ScreenManager;

import java.util.function.Consumer;


/**
 * Main interface for controlling the core portions of the game.
 */
public interface GameCore {
    /**
     * Set the screen manager.
     *
     * @param screen The screen manager that will be used
     */
    void setScreenManager(ScreenManager screen);

    /**
     * Advance the world by the elapsed time.
     *
     * @param elapsedTime Amount of time since the last update (in nanoseconds)
     */
    void update(long elapsedTime);

    /**
     * Render the world.
     */
    void render();

    /**
     * Adds the provided event to the queue of events that need to be handled.
     * Events are handled at the beginning of the next frame in the order that
     * they arrived.
     *
     * @param event The event that needs to be handled
     */
    void addGameEvent(Consumer<Context> event);

    /**
     * Called when exiting the game.
     */
    void cleanup();
}

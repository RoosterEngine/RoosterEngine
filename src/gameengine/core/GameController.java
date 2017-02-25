package gameengine.core;

import Utilities.AutoGrowQueue;
import Utilities.RateCounter;
import gameengine.context.Context;
import gameengine.graphics.Renderer;
import gameengine.graphics.ScreenManager;
import gameengine.input.KeyController;
import gameengine.input.MouseController;
import gameengine.input.MouseProperties;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Consumer;

public class GameController {
    private final GameTimer timer;
    private RateCounter frameRate;
    private ScreenManager screen;
    private final KeyController keyboard;
    private final MouseController mouse;
    private final Deque<Context> activeContexts = new ArrayDeque<>();

    public GameController() {
        this(60, 40);
    }

    /**
     * @param targetFPS render this many times per second
     */
    public GameController(int targetFPS) {
        this(targetFPS, (int) (0.6 * targetFPS));
    }

    /**
     * @param desiredFramerate render this many times per second
     * @param minFramerate     minimum allowable frame rate before the UPS slows down.
     */
    public GameController(int desiredFramerate, int minFramerate) {
        GameCore core = new Core();
        timer = MainFactory.createGameTimer(core, desiredFramerate, minFramerate);
        keyboard = MainFactory.createKeyController(core);
        screen = MainFactory.createScreenManager(keyboard);
        screen.initializeWindow();
        mouse = MainFactory.createMouseController(core, screen, desiredFramerate);
    }

    /**
     * @return The screen manager.
     */
    public ScreenManager getScreenManager() {
        return screen;
    }

    /**
     * @return The mouse properties that controls the mouse behavior.
     */
    public MouseProperties getMouseProperties() {
        return mouse;
    }

    /**
     * @return The frame rate counter.
     */
    public RateCounter getFrameRateCounter() {
        return frameRate;
    }

    /**
     * Enters the specified {@link Context}.  The context is set as the active context and begin
     * to be updated and rendered.  The previous context is paused and will be resumed when the new
     * context will be exited.
     *
     * @param context the {@link Context} to enter
     */
    public void enterContext(Context context) {
        activeContexts.push(context);
        if (activeContexts.size() == 1) {
            frameRate = new RateCounter();
            new Thread(timer).start();
        }

        System.gc(); // perform gc when context is switched to reduce gc stutters
    }

    /**
     * Switches contexts to the previous context.  If there is no previous context the game exits
     */
    public void exitContext() {
        activeContexts.poll();
        if (activeContexts.isEmpty()) {
            timer.stop();
        }
        System.gc();
    }

    private class Core implements GameCore {
        /**
         * Only the eventQueue variable is accessed by multiple threads but not the actual
         * processingQueue since it's swapped.
         */
        private volatile AutoGrowQueue<Consumer<Context>> eventQueue = new
                AutoGrowQueue<Consumer<Context>>();
        private AutoGrowQueue<Consumer<Context>> processingQueue = new
                AutoGrowQueue<Consumer<Context>>();
        private Object queueLock = new Object();

        private Core() {
        }

        /**
         * @see GameCore#setScreenManager(ScreenManager)
         */
        @Override
        public void setScreenManager(ScreenManager screen) {
            GameController.this.screen = screen;
        }

        /**
         * @see GameCore#update(long)
         */
        @Override
        public void update(long elapsedTime) {
            assert elapsedTime > 0;

            //swapping the queue to minimize locking the EDT thread
            synchronized (queueLock) {
                AutoGrowQueue<Consumer<Context>> swap = eventQueue;
                eventQueue = processingQueue;
                processingQueue = swap;
            }
            Context activeContext = activeContexts.peek();
            int size = processingQueue.size();
            for (int i = 0; i < size; i++) {
                processingQueue.dequeue().accept(activeContext);
            }

            mouse.updateMouseDelta(elapsedTime);

            double mouseWheelRotation = mouse.getWheelRotation();

            activeContext.update(elapsedTime, mouse.getDeltaX(), mouse.getDeltaY(),
                    mouseWheelRotation);
        }

        /**
         * @see GameCore#render()
         */
        @Override
        public void render() {
            if (activeContexts.isEmpty()) {
                return;
            }
            frameRate.registerTick();
            Renderer renderer = screen.initializeFrame();
            activeContexts.peek().render(renderer);
            screen.RenderFrame();
        }

        /**
         * @see GameCore#addGameEvent(Consumer)
         */
        @Override
        public void addGameEvent(Consumer<Context> event) {
            synchronized (queueLock) {
                eventQueue.enqueue(event);
            }
        }

        /**
         * @see GameCore#cleanup()
         */
        @Override
        public void cleanup() {
            screen.restoreWindow();
        }
    }
}

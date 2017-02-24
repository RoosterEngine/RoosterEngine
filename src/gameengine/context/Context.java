package gameengine.context;

import gameengine.collisiondetection.Collision;
import gameengine.collisiondetection.Viewport;
import gameengine.collisiondetection.World;
import gameengine.core.Action;
import gameengine.core.GameController;
import gameengine.graphics.Renderer;
import gameengine.graphics.ScreenManager;
import gameengine.motion.motions.MouseMotion;

import java.util.HashMap;
import java.util.PriorityQueue;

/**
 * A context is something that the user interacts with (eg. a specific menu or the game).
 */
public abstract class Context {
    protected GameController controller;

    /**
     * The amount of time that this context has been active (in nanoseconds)
     */
    long gameTime = 0;

    /**
     * A map from input code to the action.  Eg. space => "Shoot"
     */
    private final HashMap<Integer, String> inputActionMap = new HashMap<>();

    /**
     * A map from the pressed action to the handler for that action.  Eg. "Shoot" => handler
     */
    private final HashMap<String, Action> pressedActionHandlerMap = new HashMap<>();

    /**
     * A map from the released action to the handler for that action.  Eg. "Shoot" => handler
     */
    private final HashMap<String, Action> releasedActionHandlerMap = new HashMap<>();

    /**
     * A priority queue of future actions.
     */
    private final PriorityQueue<RepeatedAction> actions = new PriorityQueue<>();

    protected World world;

    private Viewport viewPort;

    /**
     * Constructs a Context.
     *
     * @param controller The {@link GameController} controlling the game
     */
    protected Context(GameController controller) {
        this.controller = controller;

        ScreenManager screen = controller.getScreenManager();
        int width = screen.getWidth();
        int height = screen.getHeight();
        world = new World(width * 0.5, height * 0.5, Math.max(width, height) * 0.5);
        viewPort = new Viewport(0, 0, 1, width, height);
    }

    public Viewport getViewPort() {
        return viewPort;
    }

    /**
     * The game has progressed by elapsedTime amount of time so we need to
     * update the context to bring it up to date.  To calculate mouse velocity,
     * divide the deltas by the elapsedTime.
     *
     * @param elapsedTime        The amount of time that has elapsed.  This is how much time we need
     *                           to update the context by so that it catches up to the current time
     * @param mouseDeltaX        The horizontal mouse movement since the last update
     * @param mouseDeltaY        The vertical mouse movement since the last update
     * @param mouseWheelRotation The wheel rotation since the last update.  The value is negative if
     *                           the mouse wheel was rotated up (away from the user), and positive
     *                           if the mouse wheel was rotated down (towards the user).  A partial
     *                           rotation occurs if the mouse has a high resolution wheel or if the
     *                           wheel sensitivity is less than 1.
     */
    public final void update(long elapsedTime, double mouseDeltaX, double mouseDeltaY, double
            mouseWheelRotation) {
        double elapsedTimeMillis = elapsedTime / 1000000.0;
        MouseMotion.setVelocity(mouseDeltaX / elapsedTimeMillis, mouseDeltaY / elapsedTimeMillis);
        world.update(elapsedTimeMillis, this);
        gameTime += (elapsedTime + 500) / 1000; //round to nearest microsecond
        //perform all the actions that have met the delay requirement
        while (!actions.isEmpty() && actions.peek().timeOfNextAction <= gameTime) {
            actions.poll().performAction();
        }
        updateContext(gameTime, mouseDeltaX, mouseDeltaY, mouseWheelRotation);
    }

    /**
     * Subclasses should override this if they wish to be notified when the context is updated
     *
     * @param gameTime           The current game time
     * @param mouseDeltaX        The number of pixels the mouse moved horizontally since the last
     *                           update
     * @param mouseDeltaY        The number of pixels the mouse moved vertically since the last
     *                           update
     * @param mouseWheelRotation The mouse wheel rotation
     */
    protected void updateContext(long gameTime, double mouseDeltaX, double mouseDeltaY, double
            mouseWheelRotation) {
    }

    public final void render(Renderer renderer) {
        //TODO: render only the portion of the world that is visible
        world.draw(this, renderer);
        renderContext(renderer, gameTime);
    }

    /**
     * Subclasses should override this if they wish to be notified when this context is being
     * rendered
     *
     * @param renderer The renderer to be used for drawing on screen
     * @param gameTime The current time
     */
    protected void renderContext(Renderer renderer, long gameTime) {
    }

    public abstract void handleCollision(Collision collision);

    public void mapInputAction(String action, int inputCode) {
        inputActionMap.put(inputCode, action);
    }

    public void mapMultiInputAction(String action, int... inputCodes) {

    }

    public void mapInputSequenceAction(String action, int... inputSequence) {

    }

    public void removeInputMapping(int inputCode) {
        inputActionMap.remove(inputCode);
    }

    public void removeAllInputMappings() {
        inputActionMap.clear();
    }

    public void mapActionStartedHandler(String action, Action handler) {
        pressedActionHandlerMap.put(action, handler);
    }

    public void mapActionStoppedHandler(String action, Action handler) {
        releasedActionHandlerMap.put(action, handler);
    }

    public void removeActionStartedHandler(String action) {
        pressedActionHandlerMap.remove(action);
    }

    public void removeActionStoppedHandler(String action) {
        releasedActionHandlerMap.remove(action);
    }

    public void removeAllActionHandlers() {
        pressedActionHandlerMap.clear();
        releasedActionHandlerMap.clear();
    }

    public final void inputPressed(int inputCode) {
        String action = inputActionMap.get(inputCode);
        Action handler = pressedActionHandlerMap.get(action);
        if (handler != null) {
            handler.performAction();
        }
    }

    public final void inputReleased(int inputCode) {
        String action = inputActionMap.get(inputCode);
        Action handler = releasedActionHandlerMap.get(action);
        if (handler != null) {
            handler.performAction();
        }
    }

    /**
     * Performs the specified action after delay amount of time.
     *
     * @param delay  The amount of time before the action will be performed
     * @param action The action to be performed
     */
    public final void addOneTimeAction(long delay, Action action) {
        RepeatedAction oneTime = createRepeatedAction(delay, false, false, action);
        oneTime.stopRepeatingAfterNextAction();
    }

    /**
     * Create an action that will be repeated.
     *
     * @param repeatDelay The delay before the action is repeated
     * @param action      The action to be performed
     * @return The repeated action
     */
    public final RepeatedAction createRepeatedAction(long repeatDelay, Action action) {
        return createRepeatedAction(repeatDelay, true, false, action);
    }

    /**
     * Using this is much more efficient than managing delays directly for many items.
     *
     * @param repeatDelay            The delay between actions.  If this delay is less than the
     *                               frame time then performAction() is called repeatedly until it
     *                               is caught up
     * @param zeroDelayToFirstAction Determines if there should also be a delay before the very
     *                               first action
     * @param freezeDelayOnPause     If true then the delay is frozen when the action is paused. Set
     *                               this to true for a count-down timer that can be paused. Set
     *                               this to false for a machine gun that has a minimum reload time
     *                               so that if the shooting action is resumed but the delay between
     *                               shots has already been exceeded then shoot right away.
     * @param action                 The GameAction that should be performed
     * @return A RepeatedAction that can be paused, resumed, & stopped
     */
    public final RepeatedAction createRepeatedAction(long repeatDelay, boolean
            zeroDelayToFirstAction, boolean freezeDelayOnPause, Action action) {
        return new RepeatedAction(repeatDelay, zeroDelayToFirstAction, freezeDelayOnPause, action);
    }

    public final class RepeatedAction implements Comparable<RepeatedAction> {
        /**
         * The action to be performed.
         */
        private Action action;

        /**
         * The delay between actions
         */
        private long delay;

        /**
         * The game time when the next action is supposed to be performed.
         */
        private long timeOfNextAction;

        private boolean paused = false;

        /**
         * Determines if the delay count down should freeze when this is paused.
         * <p>
         * Set this to false for a weapon that has a minimum cool down time between shots so that
         * if you pause your shooting then it still cools down.
         * <p>
         * Set this to true for a count down timer that triggers some action and the timer can be
         * paused and resumed.
         */
        private boolean freezeDelayOnPause;

        private RepeatedAction(long repeatDelay, boolean zeroDelayToFirstAction, boolean
                freezeDelayOnPause, Action action) {
            assert repeatDelay > 0;
            this.action = action;
            delay = repeatDelay * 1000;
            this.freezeDelayOnPause = freezeDelayOnPause;

            if (!zeroDelayToFirstAction) {
                timeOfNextAction = repeatDelay;
            }
            timeOfNextAction += gameTime;
            actions.add(this);
        }

        private void performAction() {
            do {
                action.performAction();
                timeOfNextAction += delay;
            } while (timeOfNextAction <= gameTime && !paused);

            if (!paused) {
                actions.offer(this);//re-insert this in the actions queue
            }
        }

        /**
         * Leave this action in the queue so  that it gets performed one more time but don't
         * repeat after that.
         */
        private void stopRepeatingAfterNextAction() {
            paused = true;
        }

        /**
         * Pause this repeated action.
         */
        public void pause() {
            if (paused) {
                return;
            }
            paused = true;
            if (freezeDelayOnPause) {
                //store the time remaining until the next action
                timeOfNextAction -= gameTime;
            }
            actions.remove(this);
        }

        /**
         * Resume this repeated action.
         */
        public void resume() {
            if (!paused) {
                return;
            }
            paused = false;
            if (freezeDelayOnPause) {
                timeOfNextAction += gameTime;
            } else {
                timeOfNextAction = Math.max(timeOfNextAction, gameTime);
            }
            actions.offer(this);
        }

        public void setPaused(boolean paused) {
            if (this.paused) {
                resume();
            } else {
                pause();
            }
        }

        /**
         * @return Returns true if this repeated action is paused.
         */
        public boolean isPaused() {
            return paused;
        }

        /**
         * @see Comparable#compareTo(Object)
         */
        @Override
        public int compareTo(RepeatedAction other) {
            return Long.compare(timeOfNextAction, other.timeOfNextAction);
        }
    }
}

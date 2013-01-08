package gameengine.input;

import gameengine.GameController;

import java.awt.*;
import java.awt.event.*;

/**
 * Manages incoming input events
 *
 * @author davidrusu
 */
public class InputManager implements
        MouseListener, MouseMotionListener, MouseWheelListener, KeyListener {
    private InputHandler inputHandler;
    private GameController gameController;
    private EventQueue eventQueue;
    private double mouseWheelRes = 1;

    /**
     * This accumulates as the mouse wheel rotates, used for mice that allow the
     * wheel to move a fraction of a tick, when the absolute value of the
     * accumulator rises above the specified mouseWheelRes, an event is fired
     */
    private double mouseWheelAccumulator = 0;
    private int centerX, centerY;
    private Robot robot = null;
    private final Object mouseLock = new Object();
    private int currentMouseX, currentMouseY;
    private double gameMouseX = 0, gameMouseY = 0;
    private double mouseVelX = 0, mouseVelY = 0;
    private boolean mouseMoving = true, isRelativeMouseMode = false;

    public InputManager(GameController gameController) {
        this.gameController = gameController;
        eventQueue = new EventQueue();

        try {
            robot = new Robot();
        } catch (AWTException e) {
            System.err.println(e.getMessage() + ". Robot was not instantiated");
        }
    }

    /**
     * Sets the {@link InputHandler} that will handle the input events
     *
     * @param inputHandler the {@link InputHandler} to handle input events
     */
    public void setInputHandler(InputHandler inputHandler) {
        this.inputHandler = inputHandler;
    }

    /**
     * When enabled the mouse is set to the specified x and y coordinates after
     * each mouse move event. The x and y mouse coordinates provided to the
     * handler are relative the the specified x and y coordinates
     *
     * @param x the x coordinate of the resting position of the mouse
     * @param y the y coordinate of the resting position of the mouse
     */
    public void enableRelativeMouseMode(int x, int y) {
        isRelativeMouseMode = true;
        resetMouseInfo(0, 0);
        this.centerX = x;
        this.centerY = y;
        robot.mouseMove(x, y);
    }

    /**
     * Clears all events that are currently in the {@link EventQueue}
     */
    public void clearInputQueue() {
        eventQueue.clearQueue();
    }

    public void disableRelativeMouseMove() {
        isRelativeMouseMode = false;
        resetMouseInfo(centerX, centerY);
    }

    private void resetMouseInfo(int x, int y) {
        currentMouseX = x;
        currentMouseY = y;
        gameMouseX = x;
        gameMouseY = y;
        mouseVelX = 0;
        mouseVelY = 0;
    }

    /**
     * Handles all input events up to the specified cut off time
     *
     * @param cutOffTime the time up to which to handle events
     */
    public void handleEvents(long cutOffTime) {
        eventQueue.handleEvents(cutOffTime, inputHandler);
    }

    /**
     * Returns the time in nanoseconds of the next event.
     *
     * @return the time of the next event in the queue. Long.MAX_VALUE is
     *         returned if there are no events in queue
     */
    public long getNextEventTime() {
        return eventQueue.getNextEventTime();
    }

    /**
     * Updates the mouse velocity
     *
     * @param frameTime the amount of time last frame took to complete
     */
    public void updateMouseVelocity(double frameTime) {
        synchronized (mouseLock) {
            if (isRelativeMouseMode) {
                mouseVelX = currentMouseX / frameTime;
                mouseVelY = currentMouseY / frameTime;
                currentMouseX = 0;
                currentMouseY = 0;
            } else {
                mouseVelX = (currentMouseX - gameMouseX) / frameTime;
                mouseVelY = (currentMouseY - gameMouseY) / frameTime;
            }
        }
    }

    /**
     * Updates the {@link MouseMovedHandler} with the new mouse velocity
     *
     * @param elapsedTime The amount of time in milliseconds since last update
     */
    public void updateMouseMovedHandler(double elapsedTime) {
        if (mouseVelX == 0 && mouseVelY == 0) {
            if (!mouseMoving) {
                return;
            }
            mouseMoving = false;
        } else {
            mouseMoving = true;
        }
        double dx = mouseVelX * elapsedTime;
        double dy = mouseVelY * elapsedTime;
        if (isRelativeMouseMode) {
            gameController.getMouseMovedHandler().mouseMoved(dx, dy, mouseVelX, mouseVelY);
        } else {
            gameMouseX += dx;
            gameMouseY += dy;
            gameController.getMouseMovedHandler().mouseMoved(gameMouseX, gameMouseY, mouseVelX, mouseVelY);
        }
    }

    private void handlePressInput(int inputCode) {
        eventQueue.addPressedAction(inputCode, System.nanoTime());
    }

    private void handleReleaseInput(int inputCode) {
        eventQueue.addReleasedAction(inputCode, System.nanoTime());
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        e.consume();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        int inputCode = InputCode.getMouseButtonInputCode(e.getButton());
        handlePressInput(inputCode);
        e.consume();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        int inputCode = InputCode.getMouseButtonInputCode(e.getButton());
        handleReleaseInput(inputCode);
        e.consume();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        e.consume();
    }

    @Override
    public void mouseExited(MouseEvent e) {
        e.consume();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        synchronized (mouseLock) {
            int x = e.getX();
            int y = e.getY();
            if (isRelativeMouseMode) {
                if (robot != null && (x != centerX || y != centerY)) {
                    currentMouseX += x - centerX;
                    currentMouseY += y - centerY;
                    robot.mouseMove(centerX - x + e.getXOnScreen(), centerY - y + e.getYOnScreen());
                }
            } else {
                currentMouseX = e.getX();
                currentMouseY = e.getY();
            }
        }
        e.consume();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseMoved(e);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        e.consume();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int inputCode = InputCode.getKeyInputCode(e.getKeyCode());
        handlePressInput(inputCode);
        e.consume();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int inputCode = InputCode.getKeyInputCode(e.getKeyCode());
        handleReleaseInput(inputCode);
        e.consume();
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        mouseWheelAccumulator += e.getWheelRotation();
        if (mouseWheelAccumulator < 0) {
            int inputCode = InputCode.getWheelUpInputCode();
            while (mouseWheelAccumulator <= -mouseWheelRes) {
                mouseWheelAccumulator += mouseWheelRes;
                handlePressInput(inputCode);
            }
        } else {
            int inputCode = InputCode.getWheelDownInputCode();
            while (mouseWheelAccumulator >= mouseWheelRes) {
                mouseWheelAccumulator -= mouseWheelRes;
                handlePressInput(inputCode);
            }
        }
        e.consume();
    }
}

package gameengine.input;

import gameengine.core.GameCore;
import gameengine.core.GameTimer;
import gameengine.graphics.ScreenManager;

import java.awt.*;
import java.awt.event.*;

/**
 * The MouseController handles mouse input.
 */
public class MouseController implements MouseMotionListener, MouseListener, MouseWheelListener,
        MouseProperties {
    public static final double DEFAULT_SENSITIVITY = 1;
    public static final double DEFAULT_WHEEL_SENSITIVITY = 1;
    public static final double DEFAULT_ACCELERATION = 0;
    /**
     * some smoothing with no noticeable lag.
     */
    public static final double DEFAULT_SMOOTHING_FACTOR = 1.5;
    private static final double INVERTED = -1;
    private static final double NON_INVERTED = 1;

    private double horizontalInversion = NON_INVERTED;
    private double verticalInversion = NON_INVERTED;
    private double sensitivity = DEFAULT_SENSITIVITY;
    private double wheelSensitivity = DEFAULT_WHEEL_SENSITIVITY;
    private double acceleration = DEFAULT_ACCELERATION;
    private double smoothingFactor = DEFAULT_SMOOTHING_FACTOR;

    /**
     * The accumulation of mouse movements since the last frame.
     * Updated and read on both the game and EDT threads
     */
    private double deltaX = 0, deltaY = 0;

    /**
     * The reported movement for the last frame taking into account smoothing
     */
    private double dx = 0, dy = 0;

    /**
     * The delta from the reported position and the true position since
     * smoothing adds some delay
     */
    private double trueXDelta = 0, trueYDelta = 0;

    /**
     * The average elapsed time per frame in nanoseconds
     */
    private double averageFrameTime;

    /**
     * Updated & read on both the game and EDT threads
     */
    private double wheelDelta = 0;

    /**
     * The center position of the screen
     */
    private final int centerX, centerY;

    /**
     * The robot for moving the mouse back to the center
     */
    private Robot robot = null;

    private final GameCore core;

    /**
     * Creates a MouseController instance.
     *
     * @param core             The game core
     * @param screen           The screen managerr
     * @param desiredFramerate The desired frame rate
     */
    public MouseController(GameCore core, ScreenManager screen, int desiredFramerate) {
        this.core = core;
        centerX = screen.getWidth() / 2;
        centerY = screen.getHeight() / 2;

        //Use the desired frame rate so that the average frame rate reaches steady state sooner
        averageFrameTime = GameTimer.NANOS_PER_SECOND / desiredFramerate;

        try {
            robot = new Robot();
            robot.mouseMove(centerX, centerY);
        } catch (AWTException e) {
            System.err.println(e.getMessage() + ". Robot was not instantiated");
        }
    }

    /**
     * @see MouseProperties#isInvertedVerticalAxis()
     */
    @Override
    public boolean isInvertedVerticalAxis() {
        return verticalInversion == INVERTED;
    }

    /**
     * @see MouseProperties#setInvertedVerticalAxis(boolean)
     */
    @Override
    public void setInvertedVerticalAxis(boolean inverted) {
        if (inverted) {
            verticalInversion = INVERTED;
        } else {
            verticalInversion = NON_INVERTED;
        }
    }

    /**
     * @see MouseProperties#isInvertedHorizontalAxis()
     */
    @Override
    public boolean isInvertedHorizontalAxis() {
        return horizontalInversion == INVERTED;
    }

    /**
     * @see MouseProperties#setInvertedHorizontalAxis(boolean)
     */
    @Override
    public void setInvertedHorizontalAxis(boolean inverted) {
        if (inverted) {
            horizontalInversion = INVERTED;
        } else {
            horizontalInversion = NON_INVERTED;
        }
    }

    /**
     * @see MouseProperties#getSensitivity()
     */
    @Override
    public double getSensitivity() {
        return sensitivity;
    }

    /**
     * @see MouseProperties#setSensitivity(double)
     */
    @Override
    public void setSensitivity(double sensitivity) {
        assert sensitivity > 0;

        this.sensitivity = sensitivity;
    }

    /**
     * @see MouseProperties#getWheelSensitivity()
     */
    @Override
    public double getWheelSensitivity() {
        return wheelSensitivity;
    }

    /**
     * @see MouseProperties#setWheelSensitivity(double)
     */
    @Override
    public void setWheelSensitivity(double wheelSensitivity) {
        this.wheelSensitivity = wheelSensitivity;
    }

    /**
     * @see MouseProperties#getAcceleration()
     */
    @Override
    public double getAcceleration() {
        return acceleration;
    }

    /**
     * @see MouseProperties#setAcceleration(double)
     */
    @Override
    public void setAcceleration(double acceleration) {
        assert acceleration >= 0;

        this.acceleration = acceleration;
    }

    /**
     * @see MouseProperties#getSmoothingFactor()
     */
    @Override
    public double getSmoothingFactor() {
        return smoothingFactor;
    }

    /**
     * @see MouseProperties#setSmoothingFactor(double)
     */
    @Override
    public void setSmoothingFactor(double smoothingFactor) {
        assert smoothingFactor >= 1;

        this.smoothingFactor = smoothingFactor;
    }

    /**
     * Updates the mouse movement deltas based on the movement that occurred
     * during the last frame.  This should be called once per frame prior to
     * calling the {@link #getDeltaX()} and {@link #getDeltaY()} methods
     */
    public void updateMouseDelta(long elapsedTime) {
        double tempDeltaX, tempDeltaY;

        //deltaX & deltaY are modified by the EDT and game threads
        synchronized (this) {
            tempDeltaX = deltaX;
            tempDeltaY = deltaY;
            deltaX = 0;
            deltaY = 0;
        }

        double distance = Math.sqrt(tempDeltaX * tempDeltaX + tempDeltaY * tempDeltaY);
        //acceleration should be applied per unit of time so divide by elapsedTime
        double multiplier = acceleration * distance / elapsedTime + sensitivity;
        tempDeltaX *= multiplier * horizontalInversion;
        tempDeltaY *= multiplier * verticalInversion;

        trueXDelta += tempDeltaX;
        trueYDelta += tempDeltaY;

        //use weighted average so that averageFrameTime doesn't jump around too much
        averageFrameTime = 0.1 * elapsedTime + 0.9 * averageFrameTime;
        double frameRatio = elapsedTime / averageFrameTime;

        //multiply by the frameRatio to smooth out mouse velocity
        dx = trueXDelta * frameRatio / smoothingFactor;
        dy = trueYDelta * frameRatio / smoothingFactor;

        double dSquared = dx * dx + dy * dy;

        //it's important to check the overshoot condition first even if the mouse
        //is moving slowly
        if (trueXDelta * trueXDelta + trueYDelta * trueYDelta < dSquared) {
            //moving too fast will overshoot the destination so jump to destination
            dx = trueXDelta;
            dy = trueYDelta;
        } else if (dSquared < 4E-18 * elapsedTime * elapsedTime) {
            //stop the mouse from moving tiny fractions of a pixel so pretend that we'll reach the
            //destination this frame it's important to set the true deltas to the current values
            //instead of the other way around otherwise there will be a noticeable jump and that
            // won't be smooth at all
            trueXDelta = dx;
            trueYDelta = dy;
        }

        trueXDelta -= dx;
        trueYDelta -= dy;
    }

    /**
     * @return The horizontal mouse movement that occurred during the last frame
     */
    public double getDeltaX() {
        return dx;
    }

    /**
     * @return The vertical mouse movement that occurred during the last frame
     */
    public double getDeltaY() {
        return dy;
    }

    /**
     * Retrieves the accumulated wheel rotation that occurred since the last
     * time this method was called.  The value is negative if the mouse wheel
     * was rotated up (away from the user), and positive if the mouse wheel was
     * rotated down (towards the user).  A partial rotation occurs if the mouse
     * has a high resolution wheel or if the wheel sensitivity is less than 1.
     *
     * @return The total wheel rotation
     */
    public double getWheelRotation() {
        double delta;
        synchronized (this) {
            delta = wheelDelta;
            wheelDelta = 0;
        }
        return delta * wheelSensitivity;
    }

    /**
     * @see MouseWheelListener#mouseWheelMoved(MouseWheelEvent)
     */
    @Override
    public synchronized void mouseWheelMoved(MouseWheelEvent e) {
        wheelDelta += e.getPreciseWheelRotation();
    }

    /**
     * @see MouseListener#mouseClicked(MouseEvent)
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        e.consume(); //do nothing
    }

    /**
     * @see MouseListener#mousePressed(MouseEvent)
     */
    @Override
    public void mousePressed(MouseEvent e) {
        int inputCode = InputCode.getMouseButtonInputCode(e.getButton());
        core.addGameEvent(Context -> Context.inputPressed(inputCode));
    }

    /**
     * @see MouseListener#mouseReleased(MouseEvent)
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        int inputCode = InputCode.getMouseButtonInputCode(e.getButton());
        core.addGameEvent(Context -> Context.inputReleased(inputCode));
    }

    /**
     * @see MouseListener#mouseEntered(MouseEvent)
     */
    @Override
    public void mouseEntered(MouseEvent e) {
        e.consume(); //do nothing
    }

    /**
     * @see MouseListener#mouseExited(MouseEvent)
     */
    @Override
    public void mouseExited(MouseEvent e) {
        e.consume(); //do nothing
    }

    /**
     * @see MouseMotionListener#mouseDragged(MouseEvent)
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        mouseMoved(e);
    }

    /**
     * @see MouseMotionListener#mouseMoved(MouseEvent)
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        int x = e.getX(), y = e.getY();
        e.consume();

        //ignore robot movements back to the center
        if (x == centerX & y == centerY) {
            return;
        }

        synchronized (this) {
            deltaX += x - centerX;
            deltaY += y - centerY;
        }
        if (robot != null) {
            //move the mouse back to the center
            robot.mouseMove(centerX, centerY);
        }
    }
}

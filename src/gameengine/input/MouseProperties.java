package gameengine.input;

/**
 * Interface for controlling the mouse properties.
 */
public interface MouseProperties {
    /**
     * @return True if the vertical mouse axis is inverted.
     */
    boolean isInvertedVerticalAxis();

    /**
     * Controls whether the vertical axis should be inverted.
     *
     * @param inverted True if the vertical axis will be inverted
     */
    void setInvertedVerticalAxis(boolean inverted);

    /**
     * @return True if the horizontal mouse axis is inverted.
     */
    boolean isInvertedHorizontalAxis();

    /**
     * Controls whether the horizontal axis should be inverted.
     *
     * @param inverted True if the horizontal axis will be inverted
     */
    void setInvertedHorizontalAxis(boolean inverted);

    /**
     * @return The mouse sensitivity.
     */
    double getSensitivity();

    /**
     * Sets the mouse sensitivity.  Set to 1 for no effect, less than 1 to slow down, or greater
     * than 1 to speed up.
     *
     * @param sensitivity The mouse sensitivity.  Must be > 0
     */
    void setSensitivity(double sensitivity);

    /**
     * @return The mouse wheel sensitivity.
     */
    double getWheelSensitivity();

    /**
     * Sets the mouse wheel sensitivity.  Set to 1 for no effect.  Greater than 1 to speed up, a
     * fraction to slow down, and negative to reverse direction.
     *
     * @param wheelSensitivity The wheel sensitivity
     */
    void setWheelSensitivity(double wheelSensitivity);

    /**
     * @return The mouse acceleration.
     */
    double getAcceleration();

    /**
     * Sets the mouse acceleration.  Set to 0 for no acceleration.  A good value is 0.08.
     *
     * @param acceleration The mouse acceleration.  Must be >= 0
     */
    void setAcceleration(double acceleration);

    /**
     * @return The mouse smoothing factor.
     */
    double getSmoothingFactor();

    /**
     * Sets the mouse smoothing factor.  A larger smoothing factor smoothes out the mouse movement
     * jumps.  This adds some lag but the mouse starts moving right away towards the destination.
     * The mouse reaches the destination location (smoothingFactor - 1) frames late.
     *
     * @param smoothingFactor The mouse smoothing factor.  Must be >= 1
     */
    void setSmoothingFactor(double smoothingFactor);
}

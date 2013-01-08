package gameengine.input;

/**
 * A wrapper for input codes
 *
 * @author davidrusu
 */
public abstract class InputEvent {
    public static final int PRESSED_EVENT = 0, RELEASED_EVENT = 1;
    protected long eventTime;
    protected int inputCode;

    public InputEvent(int inputCode, long eventTime) {
        this.inputCode = inputCode;
        this.eventTime = eventTime;
    }

    /**
     * Returns the time that this input was triggered
     *
     * @return the time that this input was triggered
     */
    public long getEventTime() {
        return eventTime;
    }

    /**
     * Recycles this {@link InputEvent} to be used with a new input event
     *
     * @param inputCode the {@link InputCode} of the input that was triggered
     * @param eventTime the the time in nanoseconds when this input was triggered
     */
    public void setup(int inputCode, long eventTime) {
        this.inputCode = inputCode;
        this.eventTime = eventTime;
    }

    /**
     * Creates a new instance of the current type of {@link InputEvent}.
     *
     * @param inputCode the {@link InputCode} of the input that was triggered
     * @param eventTime the the time in nanoseconds when this input was triggered
     * @return a new instance of the current type of {@link InputEvent}
     */
    public abstract InputEvent createInstance(int inputCode, long eventTime);

    /**
     * Handles the {@link Action} associated with the provided {@link InputCode}
     *
     * @param inputHandler the {@link InputHandler} to handle this event
     */
    public abstract void handleAction(InputHandler inputHandler);

    /**
     * Returns a constant identifying the type of {@link InputEvent}
     *
     * @return either InputEvent.PRESSED_EVENT or InputEvent.RELEASED_EVENT
     */
    public abstract int getEventType();
}

package gameengine.input;

import gameengine.core.Action;
import gameengine.core.GameTimer;

/**
 * Represents an action that is performed after a specific sequence of inputs are pressed.  Eg. Left
 * -> Left -> Right -> Space ==> fireball
 */
public class SequenceInputAction {
    public static final long MAX_SEQUENCE_DELAY = GameTimer.NANOS_PER_SECOND / 2;
    /**
     * The action to be performed after the sequence of inputs are pressed.
     */
    private Action action;

    /**
     * A sequence of input codes that need to be pressed.
     */
    private int[] sequence;

    /**
     * The index into the sequence array of the next input code that needs to be pressed.
     */
    private int nextInputIndex = 0;

    /**
     * The timestamp when the last input was pressed.
     */
    private long lastInputTime = System.nanoTime();

    /**
     * Creates a SequenceInputAction instance.
     *
     * @param action   The action to be performed after the sequence of inputs are pressed
     * @param sequence The sequence of input codes
     */
    public SequenceInputAction(Action action, int... sequence) {
        assert sequence != null && sequence.length > 0;

        this.action = action;
        this.sequence = sequence;
    }

    /**
     * Notifies this SequenceInputAction that an input was pressed.  If this input matches the next
     * input of the sequence then the state is advanced otherwise the state is reset to the
     * beginning.  If the last input of the sequence is pressed then the action is performed.
     *
     * @param inputCode The input code that was pressed
     */
    public void inputPressed(int inputCode) {
        long currentTime = System.nanoTime();
        if (currentTime - lastInputTime > MAX_SEQUENCE_DELAY) {
            nextInputIndex = 0;//restart the sequence
        }
        lastInputTime = currentTime;

        if (inputCode != sequence[nextInputIndex]) {
            nextInputIndex = 0;
            return;
        }
        nextInputIndex++;
        if (nextInputIndex > sequence.length) {
            nextInputIndex = 0;
            action.performAction();
        }
    }
}

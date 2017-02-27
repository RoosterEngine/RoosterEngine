package gameengine.input;

import gameengine.core.Action;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents an action that happens when all of the specified inputs have been pressed.  Eg. Only
 * shoot when both the control and space keys are pressed.
 */
public class MultiInputAction {
    /**
     * The set of input codes that need to be pressed for the action to be triggered.
     */
    private Set<Integer> inputCodes = new HashSet<>();
    private int numPressedInputs = 0;
    /**
     * The action to be performed when all the necessary inputs are pressed.
     */
    Action pressedAction;
    /**
     * The action to be performed when all the necessary inputs are no longer pressed.
     */
    Action releasedAction;

    /**
     * Creates a MultiInputAction instance.
     *
     * @param pressedAction  The action to be performed when all the inputs are pressed
     * @param releasedAction The action to be performed when the inputs are no longer pressed
     * @param inputCodes     The input codes that need to be pressed
     */
    public MultiInputAction(Action pressedAction, Action releasedAction, int... inputCodes) {
        this.pressedAction = pressedAction;
        this.releasedAction = releasedAction;
        for (int code : inputCodes) {
            this.inputCodes.add(code);
        }
    }

    /**
     * Notifies this MultiInputAction that an input has been pressed.  If this causes all the
     * necessary inputs to be pressed then the pressed action is performed.
     *
     * @param inputCode The input code that was pressed
     */
    public void inputPressed(Integer inputCode) {
        if (!inputCodes.contains(inputCode)) {
            return;
        }
        numPressedInputs++;
        if (numPressedInputs == inputCodes.size() && pressedAction != null) {
            pressedAction.performAction();
        }
    }

    /**
     * Notifies this MultiInputAction that an input has been released.  If this causes all the
     * necessary inputs to no longer be pressed then the released action is performed.
     *
     * @param inputCode The input code that was released
     */
    public void inputReleased(Integer inputCode) {
        if (!inputCodes.contains(inputCode)) {
            return;
        }
        if (numPressedInputs == inputCodes.size() && releasedAction != null) {
            releasedAction.performAction();
        }
        numPressedInputs--;
    }
}

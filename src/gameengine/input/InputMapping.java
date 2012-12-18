package gameengine.input;

/**
 * Stores a mapping between {@link InputCode} and {@link Action}
 * @author davidrusu
 */
public class InputMapping {
    private int inputCode;
    private Action action;

    public InputMapping(int inputCode, Action action) {
        this.inputCode = inputCode;
        this.action = action;
    }
    
    public int getInputCode() {
        return inputCode;
    }

    public Action getAction() {
        return action;
    }
}
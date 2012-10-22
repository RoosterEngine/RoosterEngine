package gameengine.input;

/**
 * A handler for {@link Action}s
 *
 * @author davidrusu
 */
public interface ActionHandler {

    /**
     * Called when the action starts
     *
     * @param inputCode the {@link InputCode} of the event that triggered this action
     */
    public void startAction(int inputCode);

    /**
     * Called when the action finishes
     *
     * @param inputCode the {@link InputCode} of the event that triggered this action
     */
    public void stopAction(int inputCode);
}
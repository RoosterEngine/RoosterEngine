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
     * @param action    The action that triggered this handler
     * @param inputCode the {@link InputCode} of the event that triggered this action
     */
    public void startAction(Action action, int inputCode);

    /**
     * Called when the action finishes
     *
     * @param action    The action that triggered this handler
     * @param inputCode the {@link InputCode} of the event that triggered this action
     */
    public void stopAction(Action action, int inputCode);
}
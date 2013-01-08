package gameengine.input;

/**
 * Created with IntelliJ IDEA.
 * User: davidrusu
 * Date: 07/01/13
 * Time: 11:02 AM
 * To change this template use File | Settings | File Templates.
 */
public interface InputHandler {

    /**
     * Clears all mappings between {@link gameengine.input.InputCode}s and {@link Action}
     */
    public void clearInputMappings();

    /**
     * Adds a mapping between an {@link gameengine.input.InputCode} and an {@link Action}
     *
     * @param inputCode the {@link gameengine.input.InputCode} to map
     * @param action the {@link Action} to map
     */
    public void addInputMapping(int inputCode, Action action);

    /**
     * Starts the handler for the specified {@link InputCode}
     *
     * @param inputCode the {@link InputCode} of the {@link InputEvent}
     */
    public void startInput(int inputCode);

    /**
     * Stops the handler for the specified {@link InputCode}
     *
     * @param inputCode the {@link InputCode} of the {@link InputEvent}
     */
    public void stopInput(int inputCode);
}

package gameengine.input;

/**
 * Wrapper for {@link ActionHandler}.
 * When a pressed input action occurs (such as key_pressed), the handler is stored in a PressedEvent instance and placed in a queue
 * for the event to be handled at the appropriate time.
 *
 * @author davidrusu
 */
public class PressedEvent extends InputEvent {
    
    /**
     * @param inputCode the input code of the event (input codes can be acquired from {@link InputCode})
     */
    public PressedEvent(int inputCode, long eventTime) {
        super(inputCode, eventTime);
    }
    
    @Override
    public InputEvent createInstance(int inputCode, long eventTime){
        return new PressedEvent(inputCode, eventTime);
    }
    
    @Override
    public int getEventType(){
        return InputEvent.PRESSED_EVENT;
    }
    
    @Override
    public void handleAction(InputHandler inputHandler) {
        inputHandler.startInput(inputCode);
    }
}
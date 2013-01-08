package gameengine.input;

/**
 * Wrapper for {@link ActionHandler}.
 * When a released input action occurs (such as key_released), the handler is stored in a ReleasedEvent instance and placed in a queue
 * for the event to be handled at the appropriate time.
 * 
 * @author davidrusu
 */
public class ReleasedEvent extends InputEvent {

    /**
     * @param inputCode the input code of the event (input codes can be acquired from {@link InputCode})
     */
    public ReleasedEvent(int inputCode, long eventTime) {
        super(inputCode, eventTime);
    }

    @Override
    public InputEvent createInstance(int inputCode, long eventTime){
        return new ReleasedEvent(inputCode, eventTime);
    }
    
    @Override
    public int getEventType(){
        return InputEvent.RELEASED_EVENT;
    }

    @Override
    public void handleAction(InputHandler inputHandler) {
        inputHandler.stopInput(inputCode);
    }
}
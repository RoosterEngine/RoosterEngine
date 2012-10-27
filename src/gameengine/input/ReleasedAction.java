package gameengine.input;

/**
 * Wrapper for {@link ActionHandler}.
 * When a released input action occurs (such as key_released), the handler is stored in a ReleasedAction instance and placed in a queue
 * for the event to be handled at the appropriate time.
 * 
 * @author davidrusu
 */
public class ReleasedAction extends InputAction {
    private ActionHandler handler;
    private int inputCode;

    /**
     * @param handler the handler for this released input action
     * @param inputCode the input code of the event (input codes can be acquired from {@link InputCode})
     */
    public ReleasedAction(ActionHandler handler, int inputCode, long eventTime) {
        super(eventTime);
        this.handler = handler;
        this.inputCode = inputCode;
    }
    
    public InputAction createInstance(){
        return new ReleasedAction(null, 0, 0);
    }

    /**
     * Used to get a ReleasedAction instance.
     *
     * @param handler the handler for this released input action
     * @param inputCode the input code of the event (input codes can be acquired from {@link InputCode})
     * @return a ReleasedAction instance
     */
    
    @Override
    public void clearHandler(){
        handler = null;
    }

    /**
     * @param handler the handler for this released input action
     * @param inputCode the input code of the event (input codes can be acquired from {@link InputCode})
     */
    public void setup(ActionHandler handler, int inputCode, long eventTime){
        this.handler = handler;
        this.inputCode = inputCode;
        this.eventTime = eventTime;
    }
    
    @Override
    public int getActionType(){
        return InputAction.RELEASED_ACTION;
    }

    @Override
    public void handleAction() {
        handler.stopAction(inputCode);
    }
}
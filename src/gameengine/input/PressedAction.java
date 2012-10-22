package gameengine.input;

/**
 * Wrapper for {@link ActionHandler}.
 * When a pressed input action occurs (such as key_pressed), the handler is stored in a PressedAction instance and placed in a queue
 * for the event to be handled at the appropriate time.
 *
 * @author davidrusu
 */
//look at docs from ReleasedAction and mimick!!!
public class PressedAction extends InputAction {
    public static final int ACTION_TYPE = 0;
    private ActionHandler handler;
    private int inputCode;
    
    /**
     * @param handler the handler for this pressed input action
     * @param inputCode the input code of the event (input codes can be acquired from {@link InputCode})
     */
    public PressedAction(ActionHandler handler, int inputCode, long eventTime) {
        super(eventTime);
        this.handler = handler;
        this.inputCode = inputCode;
    }
    
    @Override
    public InputAction createInstance(){
        return new PressedAction(null, 0, 0);
    }

    @Override
    public void clearHandler(){
        handler = null;
    }

    /**
     * @param handler the handler for this pressed input action
     * @param inputCode the input code of the event (input codes can be acquired from {@link InputCode})
     */
    public void setup(ActionHandler handler, int inputCode, long eventTime){
        this.handler = handler;
        this.inputCode = inputCode;
        this.eventTime = eventTime;
    }
    
    @Override
    public int getActionType(){
        return ACTION_TYPE;
    }
    
    @Override
    public void handleAction() {
        handler.startAction(inputCode);
    }
}
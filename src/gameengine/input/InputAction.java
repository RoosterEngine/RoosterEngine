package gameengine.input;

/**
 * A wrapper of input handlers
 *
 * @author davidrusu
 */
public abstract class InputAction {
    public static final int PRESSED_ACTION = 0, RELEASED_ACTION = 1, MOUSE_MOVED_ACTION = 2;
    protected long eventTime;
    
    public InputAction(long eventTime){
        this.eventTime = eventTime;
    }
    
    //The user needs to call setup to initialize the values on the returned instance
    public abstract InputAction createInstance();
    
    public long getEventTime(){
        return eventTime;
    }
    
    /**
     * Handles the action
     */
    public abstract void handleAction();
    
    public abstract int getActionType();
    
    public abstract void clearHandler();
}

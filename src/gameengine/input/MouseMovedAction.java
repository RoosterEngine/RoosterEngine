package gameengine.input;

/**
 * Wrapper for the {@link MouseMovedHandler}. 
 * When a mouse moved input action occurs, the handler is stored in a MouseMovedAction instance and placed in a queue
 * for the event to be handled at the appropriate time.
 *
 * @author davidrusu
 */
public class MouseMovedAction extends InputAction {
    private MouseMovedHandler handler;
    private int mouseX, mouseY;

    /**
     * @param handler the handler for this mouse moved input action
     * @param mouseX the mouse x position
     * @param mouseY the mouse y position
     */
    public MouseMovedAction(MouseMovedHandler handler, int mouseX, int mouseY, long eventTime) {
        super(eventTime);
        this.handler = handler;
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }
    
    @Override
    public InputAction createInstance(){
        return new MouseMovedAction(null, 0, 0, 0);
    }
    
    @Override
    public void clearHandler(){
        handler = null;
    }
    
    /**
     * @param handler the handler for this mouse moved input action
     * @param mouseX the mouse x position
     * @param mouseY the mouse y position
     */
    public void setup(MouseMovedHandler handler, int x, int y, long eventTime){
        this.handler = handler;
        mouseX = x;
        mouseY = y;
        this.eventTime = eventTime;
    }
    
    @Override
    public int getActionType(){
        return InputAction.MOUSE_MOVED_ACTION;
    }

    @Override
    public void handleAction() {
        handler.mouseMoved(mouseX, mouseY);
    }
}
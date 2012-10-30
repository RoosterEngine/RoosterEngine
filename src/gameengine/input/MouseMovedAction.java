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
    public static int gameMouseX = 0, gameMouseY = 0;
    public int mouseX, mouseY;

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
    
    public static void setGameMousePosition(int mouseX, int mouseY){
        gameMouseX = mouseX;
        gameMouseY = mouseY;
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
        handler.mouseMoved(gameMouseX, gameMouseY, 0, 0);
    }
    
    public void updateMouseVelocity(long currentTime){
        double timeDelta = (eventTime - currentTime) / 1000000.0;
        if(InputManager.isRelativeMouseMode()){
            handler.mouseMoved(0, 0, mouseX / timeDelta, mouseY / timeDelta);
        }else{
            handler.mouseMoved(gameMouseX, gameMouseY, (mouseX - gameMouseX) / timeDelta, (mouseY - gameMouseY) / timeDelta);
            gameMouseX = mouseX;
            gameMouseY = mouseY;
        }
    }
}
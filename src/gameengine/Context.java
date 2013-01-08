package gameengine;

import bricklets.Collision;
import bricklets.Entity;
import bricklets.Group;
import gameengine.input.Action;
import gameengine.input.ActionHandler;
import gameengine.input.InputHandler;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class Context implements ActionHandler, InputHandler{
    //TODO switch to EnumMap
    private HashMap<Action, ActionHandler> actionMap;
    private HashMap<Integer, Action> inputMap = new HashMap<>();
    private InputHandler inputHandler = this;
    private boolean isShowingMouseCursor, isRelativeMouseMovedEnabled;
    private boolean paused = false;
    protected ArrayList<Entity> entities = new ArrayList<>();
    protected ArrayList<Group> groups = new ArrayList<>();
    protected GameController controller;
    protected ContextType contextType;
    protected int width, height;

    /**
     * Constructs a Context
     * @param controller 
     * @param contextType
     * @param showMouseCursor if disabled the mouse cursor will not be shown
     * @param enableRelativeMouseMoved if enabled the mouse will reset to the
     * center of the screen after each time the mouse moves. When the mouse
     * handler is called the position of the mouse will be a vector in the
     * direction that the mouse was moved
     */
    protected Context(GameController controller, ContextType contextType, boolean showMouseCursor, boolean enableRelativeMouseMoved){
        this.controller = controller;
        this.contextType = contextType;
        this.isShowingMouseCursor = showMouseCursor;
        isRelativeMouseMovedEnabled = enableRelativeMouseMoved;
        actionMap = new HashMap<>();
        setSize(controller.getWidth(), controller.getHeight());
    }

    public void reset(){
        entities.clear();
        groups.clear();
        paused = false;
    }

    public boolean isPaused(){
        return paused;
    }

    public void togglePause(){
        paused = !paused;
    }

    public boolean isRelativeMouseMovedEnabled(){
        return isRelativeMouseMovedEnabled;
    }

    public boolean isShowingMouseCursor(){
        return isShowingMouseCursor;
    }

    public final void setSize(int width, int height){
        this.width = width;
        this.height = height;
    }
    
    public int getWidth(){
        return width;
    }
    
    public int getHeight(){
        return height;
    }

    /**
     * Returns the {@link ContextType} of this context
     * @return {@link ContextType}
     */
    public final ContextType getContextType() {
        return contextType;
    }
    
    public void updatePositions(double elapsedTime) {
        for (Entity entity : entities) {
            entity.updatePosition(elapsedTime);
        }
    }

    public void updateMotions(double elapsedTime) {
        for (Entity entity : entities) {
            entity.updateMotion(elapsedTime);
        }
    }

    /**
     * Binds an {@link Action} to an {@link ActionHandler}
     * @param action
     * @param handler
     */
    protected final void bindAction(Action action, ActionHandler handler) {
        actionMap.put(action, handler);
    }

    /**
     * Enters binding mode. <br></br>
     * When in binding mode all input events except mouse movement will be
     * handled, the specified {@link Action} is given to the
     * {@link ActionHandler} when an input event happens.
     *
     * @param bindingInputHandler the {@link InputHandler} that will handle the
     *                            input while in binding mode
     */
    public void enterBindingMode(InputHandler bindingInputHandler) {
        inputHandler = bindingInputHandler;
    }

    /**
     * Exits binding mode.
     */
    public void exitBindingMode() {
        inputHandler = this;
    }

    public InputHandler getInputHandler() {
        return inputHandler;
    }

    //-------------------- InputHandler implementation -------------------------
    @Override
    public void clearInputMappings() {
        inputMap.clear();
    }

    @Override
    public void addInputMapping(int inputCode, Action action) {
        inputMap.put(inputCode, action);
    }


    @Override
    public final void startInput(int inputCode) {
        Action action = inputMap.get(inputCode);
        if (action == null) {
            return;
        }
        startAction(action, inputCode);
    }

    @Override
    public final void stopInput(int inputCode) {
        Action action = inputMap.get(inputCode);
        if (action == null) {
            return;
        }
        stopAction(action, inputCode);
    }


    //------------------- ActionHandler implementation -------------------------
    @Override
    public void startAction(Action action, int inputCode) {
        ActionHandler actionHandler = actionMap.get(action);
        if (actionHandler == null) {
            return;
        }
        actionHandler.startAction(action, inputCode);
    }

    @Override
    public void stopAction(Action action, int inputCode) {
        ActionHandler actionHandler = actionMap.get(action);
        if (actionHandler == null) {
            return;
        }
        actionHandler.stopAction(action, inputCode);
    }
    
    public abstract void update(double elapsedTime);

    public abstract void draw(Graphics2D g);

    public abstract void handleCollision(Collision collision, double collisionsPerMilli);
}
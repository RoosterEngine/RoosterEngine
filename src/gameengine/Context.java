package gameengine;

import bricklets.Collision;
import bricklets.Entity;
import bricklets.Group;
import gameengine.input.Action;
import gameengine.input.ActionHandler;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class Context{
    //TODO switch to EnumMap
    private HashMap<Action, ActionHandler> actionHandlers;
    private boolean isShowingMouseCursor, isRelativeMouseMovedEnabled;

    protected ArrayList<Entity> entities = new ArrayList<Entity>();
    protected ArrayList<Group> groups = new ArrayList<Group>();
    protected boolean paused = false;
    protected GameController controller;
    protected ContextType contextType;
    protected int width, height;
    protected double timeScale = 1;

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
        actionHandlers = new HashMap<Action, ActionHandler>();
        setSize(controller.getWidth(), controller.getHeight());
    }

    public void reset(){
        entities.clear();
        groups.clear();
        paused = false;
        timeScale = 1;
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

    public double getTimeScale(){
        return timeScale;
    }
    
    /**
     * Returns the {@link ContextType} of this context
     * @return {@link ContextType}
     */
    public final ContextType getContextType() {
        return contextType;
    }
    
    /**
     * Returns the {@link ActionHandler} associated with the specified {@link Action}
     * @param action the action to use to find the {@link ActionHandler}
     * @return {@link ActionHandler}
     */
    public final ActionHandler getActionHandler(Action action) {
        return actionHandlers.get(action);
    }

    /**
     * Binds an {@link Action} to an {@link ActionHandler}
     * @param action
     * @param handler 
     */
    protected final void bindAction(Action action, ActionHandler handler) {
        actionHandlers.put(action, handler);
    }

    public void updatePositions(double elapsedTime) {
        for (Entity entity : entities) {
            entity.updatePosition(elapsedTime);
        }
    }

    public void updateMotionGenerators(double elapsedTime) {
//        for (Group group : groups) {
//            group.updateEnvironmentMotionGenerator(elapsedTime);
//        }
        for (Entity entity : entities) {
            entity.updateMotionGenerator(elapsedTime);
        }

    }
    
    public abstract void update(double elapsedTime);

    public abstract void draw(Graphics2D g);

    public abstract void handleCollision(Collision collision, double collisionsPerMilli);
}
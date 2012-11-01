package gameengine;

import gameengine.input.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import javax.imageio.ImageIO;

public class GameController {
    
    private EnumMap<ContextType, ArrayList<InputMapping>> contextTypeContextControlsMap;
    private EnumMap<ContextType, HashMap<Integer, Action>> contextTypeControlsMap;
    private HashMap<Integer, Action> currentControls;
    private InputManager input;
    private ScreenManager screen;
    private Thread gameThread;
    private GameTimer timer;
    private UserProfile currentProfile;
    private Deque<Context> contextStack;    // the built in stack extends Vector, recommended to use Deque instead
    private Context currentContext;
    private ActionHandler singleHandler;
    private boolean inSingleHandlerMode = false, showingMouseCursor = true;

    public GameController(){
        this(120, 60);
    }
    
    /**
     * @param updateRate update the game state this many times per second
     * (updateRate needs to be greater or equal to desiredFrameRate)
     * @param desiredFrameRate render to the screen this many times per second
     */
    public GameController(int updateRate, int desiredFrameRate){
        this(updateRate, desiredFrameRate, (int)(0.6 * desiredFrameRate));
    }
    
    /**
     * @param updateRate update the game state this many times per second
     * (updateRate needs to be greater or equal to desiredFrameRate)
     * @param desiredFrameRate render to the screen this many times per second
     * @param minFrameRate minimum allowable frame rate before the updateRate
     * slows down (game play doesn't slow down though)
     */
    public GameController(int updatRate, int desiredFrameRate, int minFrameRate){
        UserProfile profile = new UserProfile("Default");
        profile.setInputBinding(InputCode.KEY_A, Action.EXIT_GAME);
        init(updatRate, desiredFrameRate, minFrameRate, profile);
    }
    /**
     * @param updateRate update the game state this many times per second
     * (updateRate needs to be greater or equal to desiredFrameRate)
     * @param desiredFrameRate render to the screen this many times per second
     * @param minFrameRate minimum allowable frame rate before the updateRate
     * slows down (game play doesn't slow down though)
     * @param userProfile
     */
    public GameController(int updateRate, int desiredFrameRate, int minFrameRate, UserProfile userProfile) {
        init(updateRate, desiredFrameRate, minFrameRate, userProfile);
    }
    
    private void init(int updateRate, int desiredFrameRate, int minFrameRate, UserProfile userProfile){
        currentProfile = userProfile;
        input = new InputManager(this);
        screen = new ScreenManager();
        screen.setFullScreen();
        contextStack = new ArrayDeque<Context>();
        contextTypeControlsMap = new EnumMap<ContextType, HashMap<Integer, Action>>(ContextType.class);
        contextTypeContextControlsMap = new EnumMap<ContextType, ArrayList<InputMapping>>(ContextType.class);
        ContextType[] contextTypes = ContextType.values();
        for(ContextType type: contextTypes){
            contextTypeContextControlsMap.put(type, new ArrayList<InputMapping>());
        }
        timer = new GameTimer(this, updateRate, desiredFrameRate, minFrameRate);
        gameThread = new Thread(timer);
    }
    
    public void startGame(){
        System.out.println("starting game");
        addInputListeners(screen.getFullScreenWindow());
        gameThread.start();
    }
    
    public void stopGame(){
        resetCursor();
        timer.stop();
    }

    /**
     * Changes the current {@link UserProfile} to the specified one and updates
     * the controls to the new users controls.
     * @param userProfile the new user profile
     */
    public void changeUserProfile(UserProfile userProfile) {
        this.currentProfile = userProfile;
        contextTypeControlsMap.clear();
        currentControls = getControlsMap(userProfile, currentContext.getContextType());
        contextTypeControlsMap.put(currentContext.getContextType(), currentControls);
    }
    
    public void updateMouseVelocity(double frameTime){
        input.updateMouseVelocity(frameTime);
    }
    
    public void updateMouseMovedHandler(double updateTime){
        input.updateMouseMovedHandler(updateTime);
    }
    
    private HashMap<Integer, Action> getControlsMap(UserProfile userProfile, ContextType contextType){
        HashMap<Integer, Action> controlMap = new HashMap<Integer, Action>();

        Iterator<Map.Entry<Integer, Action>> iter = userProfile.getControlsIterator();
        while(iter.hasNext()){
            Map.Entry<Integer, Action> entry = iter.next();
            controlMap.put(entry.getKey(), entry.getValue());
        }

        ArrayList<InputMapping> contextMappings = contextTypeContextControlsMap.get(contextType);
        
        for(InputMapping mapping : contextMappings){
            controlMap.put(mapping.getInputCode(), mapping.getAction());
        }
        
        return controlMap;
    }

    /**
     * Starts updating and rendering the specified context, the state of the
     * previous context is preserved on a stack and can be reentered by calling
     * exitContext. the current control map is updated to have the new contexts
     * default controls
     * 
     * @param context the context to be entered
     */
    public void enterContext(Context context) {
        input.clearInputQueue();
        if(!showingMouseCursor && context.isShowingMouseCursor()){
            showingMouseCursor = true;
            resetMouseCursor(screen.getFullScreenWindow());
        }else if(showingMouseCursor && !context.isShowingMouseCursor()){
            showingMouseCursor = false;
            setInvisibleMouseCursor(screen.getFullScreenWindow());
        }
        if(context.isRelativeMouseMovedEnabled()){
            input.enableRelativeMouseMove(context.getWidth() / 2, context.getHeight() / 2);
        }else{
            input.disableRelativeMouseMove();
        }
        contextStack.push(context);
        currentContext = context;
        currentControls = contextTypeControlsMap.get(context.getContextType());
        if(currentControls == null){
            currentControls = getControlsMap(currentProfile, currentContext.getContextType());
            contextTypeControlsMap.put(context.getContextType(), currentControls);
        }
    }
    
    private static void setInvisibleMouseCursor(Window window){
        window.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(Toolkit.getDefaultToolkit().getImage(""), new Point(0, 0), "invisible"));
    }
    
    private static void resetMouseCursor(Window window){
        window.setCursor(Cursor.getDefaultCursor());
    }

    /**
     * Switches contexts to the previous context and the current control map is
     * updated to have the previous contexts default controls
     * If there is no previous context the game exits
     */
    public void exitContext() {
        if(!contextStack.isEmpty()){
            contextStack.pop();
            if(contextStack.isEmpty()){
                stopGame();
            }else{
                enterContext(contextStack.pop());
            }
        }
    }

    /**
     * Gets the {@link MouseMovedHandler} from the currentContext
     * @return MouseMovedHandler
     */
    public MouseMovedHandler getMouseMovedHandler() {
            return currentContext;
    }
    
    /**
     * Gets the {@link ActionHandler} that is associated with the specified
     * inputCode in the currentContext
     * @param inputCode
     * @return 
     */
    public ActionHandler getActionHandler(int inputCode){
        if(inSingleHandlerMode){
            return singleHandler;
        }
        return currentContext.getActionHandler(currentControls.get(inputCode));
    }
    
    /**
     * adds {@link MouseListener}, {@link MouseMotionListener},
     * {@link MouseWheelListener}, {@link KeyListener} to a specified container
     * @param component the container to add listeners to
     */
    public final void addInputListeners(Component component) {
        component.addMouseListener(input);
        component.addMouseMotionListener(input);
        component.addMouseWheelListener(input);
        component.addKeyListener(input);
        component.setFocusTraversalKeysEnabled(false); // allows input of the Tab and other focus traversal keys
    }

    /**
     * Change input bindings, should only be used in a control menu where the
     * player is changing bindings
     *
     * @param newCode the code to bind, this code should be got from one of the
     * get*EventType*InputCode() methods
     * @param originalCode the code that is currently bound
     */
    public void changeUserBinding(int newCode, int originalCode) {
        Action action = currentControls.remove(originalCode);
        currentControls.put(newCode, action);
        currentProfile.removeInputBinding(originalCode);
        currentProfile.setInputBinding(newCode, action);
    }
    
    /**
     * Sets context specific input bindings. The combined context specific
     * bindings and {@link UserProfile} bindings are used when the context is
     * entered. When the bindings are combined, the context specific bindings
     * always override the user profile bindings. Game actions should not be
     * reused in menus to avoid having multiple keys bound to the same action
     * @param contextType the contextType to bind the controls to
     * @param inputCode the inputCode to bind
     * @param action the action to bind
     */
    public void setContextBinding(ContextType contextType, int inputCode, Action action){
        ArrayList<InputMapping> mappings = contextTypeContextControlsMap.get(contextType);
        boolean duplicateNotFound = true;
        int i = 0;
        while(duplicateNotFound && i < mappings.size()){
            InputMapping mapping = mappings.get(i);
            if(mapping.getInputCode() == inputCode && mapping.getAction() == action){
                mappings.remove(mapping);
            }
            i++;
        }
        mappings.add(new InputMapping(inputCode, action));
    }

    /**
     * All events are handled by one handler that the user specifies, 
     * {@link Action}s are not user in single handler mode
     * @param singleHandler the handler to catch all events
     */
    public void enterSingleHandlerMode(ActionHandler singleHandler) {
        this.singleHandler = singleHandler;
        inSingleHandlerMode = true;
    }

    /**
     * Reverts back to multi handler mode
     */
    public void exitSingleHandlerMode() {
        singleHandler = null;
        inSingleHandlerMode = false;
    }

    public Object getUserProperty(String propertyName){
        return currentProfile.getProperty(propertyName);
    }
    
    public void setUserProperty(String propertyName, Serializable property){
        currentProfile.setProperty(propertyName, property);
    }
    
    
    
    /**
     * Returns the time in nanoseconds of the next event. Long.MAX_VALUE is returned if there are no events in queue
     * @return 
     */
    public long getNextInputEventTime(){
        return input.getNextEventTime();
    }
    
    public void handleEvents(long cutOffTime){
        input.handleEvents(cutOffTime);
    }
    
    public void setFullScreen(){
        screen.setFullScreen();
    }
    
    public void restoreScreen(){
        screen.restoreScreen();
    }
    
    public double getFrameRate(){
        return timer.getFrameRate();
    }
    
    public double getUpdateRate(){
        return timer.getUpdateRate();
    }
    
    public int getWidth(){
        return screen.getFullScreenWindow().getWidth();
    }
    
    public int getHeight(){
        return screen.getFullScreenWindow().getHeight();
    }
    
    public void update(double elapsedTime) {
        currentContext.update(elapsedTime);
    }
    
    public Graphic loadImage(String path) throws IOException{
        BufferedImage im = ImageIO.read(getClass().getResource(path));
        Graphic result = new ImageGraphic(screen.getCompatibleImageVersion(im));
        im.flush();//to save some memmory right away
        return result;
    }
    
    public BufferedImage createCompatibleImage(int width, int height){
        return screen.createCompatibleImage(width, height, BufferedImage.BITMASK);
    }
    
    public void resetCursor(){
        if(!showingMouseCursor){
            resetMouseCursor(screen.getFullScreenWindow());
        }
    }
    
    public void draw() {
        Graphics2D g2D = screen.getGraphics();
        currentContext.draw(g2D);
        //clean up the graphics object and update the screen
        g2D.dispose();
        screen.updateGraphics();
    }
}
package gameengine;

import gameengine.context.Context;
import gameengine.context.ContextType;
import gameengine.graphics.Graphic;
import gameengine.graphics.ImageGraphic;
import gameengine.input.*;
import gameengine.motion.motions.MouseMotion;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

public class GameController implements MouseMovedHandler {
    private EnumMap<ContextType, ArrayList<InputMapping>> contextTypeMap;
    private InputManager inputManager;
    private ScreenManager screenManager;
    private Thread gameThread;
    private GameTimer gameTimer;
    private User user;
    private Deque<Context> contextStack; // the built in stack extends Vector
    private Context activeContext;

    public GameController() {
        this(120, 60);
    }

    /**
     * @param UPS       update the game state this many times per second. UPS needs to
     *                  be greater or equal to targetFPS
     * @param targetFPS render this many times per second
     */
    public GameController(int UPS, int targetFPS) {
        this(UPS, targetFPS, (int) (0.6 * targetFPS));
    }

    /**
     * @param UPS       update the game state this many times per second. UPS needs to
     *                  be greater or equal to targetFPS
     * @param targetFPS render this many times per second
     * @param minFPS    minimum allowable frame rate before the UPS slows down.
     */
    public GameController(int UPS, int targetFPS, int minFPS) {
        User profile = new User("Default");
        profile.setInputBinding(InputCode.KEY_A, Action.EXIT_GAME);
        init(UPS, targetFPS, minFPS, profile);
    }

    /**
     * @param UPS       update the game state this many times per second. UPS needs to
     *                  be greater or equal to targetFPS
     * @param targetFPS render this many times per second
     * @param minFPS    minimum allowable frame rate before the UPS slows down.
     * @param user      the user profile from which to load user preferences from
     */
    public GameController(int UPS, int targetFPS, int minFPS, User user) {
        init(UPS, targetFPS, minFPS, user);
    }

    private void init(int UPS, int targetFPS, int minFPS, User user) {
        this.user = user;
        inputManager = new InputManager(this);
        screenManager = new ScreenManager();
        screenManager.setFullScreen();
        contextStack = new ArrayDeque<>();

        contextTypeMap = new EnumMap<>(ContextType.class);
        ContextType[] contextTypes = ContextType.values();
        for (ContextType type : contextTypes) {
            contextTypeMap.put(type, new ArrayList<InputMapping>());
        }

        gameTimer = new GameTimer(this, UPS, targetFPS, minFPS);
        gameThread = new Thread(gameTimer, "Game");
    }

    public void startGame() {
        addInputListeners(screenManager.getFullScreenWindow());
        gameThread.start();
    }

    public void stopGame() {
        resetCursor();
        gameTimer.stop();
    }

    /**
     * Changes the current {@link User} to the specified one and updates
     * the controls to the new users controls.
     *
     * @param user the new user profile
     */
    public void changeUser(User user) {
        this.user = user;
        setInputHandler(activeContext.getInputHandler());
    }

    public void updateMouseVelocity(double frameTime) {
        inputManager.updateMouseVelocity(frameTime);
    }

    public void updateMouseMovedHandler(double updateTime) {
        inputManager.updateMouseMovedHandler(updateTime);
    }

    /**
     * Enters the specified {@link Context}.
     * <p>
     * The specified context is set as the active context and begin to be
     * updated and rendered by the {@link GameTimer}.
     * </p>
     * <p>
     * The State of the previous context is preserved and placed on a stack. The
     * previous context will be entered when the exitContext() method is called.
     * </p>
     * When a context is entered, the {@link InputHandler} from the context will
     * be given the bindings between {@link InputCode} and {@link Action}.
     *
     * @param context the {@link Context} to enter
     */
    public void enterContext(Context context) {
        contextStack.push(context);
        activeContext = context;

        InputHandler inputHandler = context.getInputHandler();
        // TODO if the context type doesn't change, we might not have to update
        setInputHandler(inputHandler);

        setMouseCursor(context.isShowingMouseCursor());
        setMouseMode(context.isRelativeMouseMovedEnabled());
        // mouse move event is injected for cases when the mouse would be moving
        // when a context was exited.
        mouseMoved(0, 0, 0, 0);
    }

    /**
     * Sets the {@link InputHandler} that will handle input events.
     * <p>
     * The active {@link Context} is used to generate mappings between
     * {@link InputCode}s and {@link Action}, so make sure the active context is
     * the context that the specified {@link InputHandler} will be handling
     * input for.
     * </p>
     *
     * @param inputHandler the {@link InputHandler} to handle input events
     */
    public void setInputHandler(InputHandler inputHandler) {
        inputHandler.clearInputMappings();

        Iterator<Map.Entry<Integer, Action>> iterator = user.getControlsIterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Action> entry = iterator.next();
            inputHandler.addInputMapping(entry.getKey(), entry.getValue());
        }

        ContextType contextType = activeContext.getContextType();
        ArrayList<InputMapping> contextMappings = contextTypeMap.get(contextType);
        for (InputMapping mapping : contextMappings) {
            inputHandler.addInputMapping(mapping.getInputCode(), mapping.getAction());
        }
        inputManager.setInputHandler(inputHandler);
    }

    private void setMouseMode(boolean isRelativeMouseModeEnabled) {
        if (isRelativeMouseModeEnabled) {
            int centerX = activeContext.getWidth() / 2;
            int centerY = activeContext.getHeight() / 2;
            inputManager.enableRelativeMouseMode(centerX, centerY);
        } else {
            inputManager.disableRelativeMouseMove();
        }
    }

    private void setMouseCursor(boolean visible) {
        Window window = screenManager.getFullScreenWindow();
        if (visible) {
            resetMouseCursor(window);
        } else {
            setInvisibleMouseCursor(window);
        }
    }

    private static void setInvisibleMouseCursor(Window window) {
        window.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(Toolkit.getDefaultToolkit().getImage(""),
                new Point(0, 0), "invisible"));
    }

    private static void resetMouseCursor(Window window) {
        window.setCursor(Cursor.getDefaultCursor());
    }

    /**
     * Switches contexts to the previous context and the current control map is
     * updated to have the previous contexts default controls
     * If there is no previous context the game exits
     */
    public void exitContext() {
        if (!contextStack.isEmpty()) {
            contextStack.pop();
            if (contextStack.isEmpty()) {
                stopGame();
            } else {
                enterContext(contextStack.pop());
            }
        }
    }

    /**
     * Gets the {@link MouseMovedHandler} from the activeContext
     *
     * @return MouseMovedHandler
     */
    public MouseMovedHandler getMouseMovedHandler() {
        return this;
    }

    /**
     * adds inputManager listeners to the specified component
     *
     * @param component the container to add listeners to
     */
    public final void addInputListeners(Component component) {
        component.addMouseListener(inputManager);
        component.addMouseMotionListener(inputManager);
        component.addMouseWheelListener(inputManager);
        component.addKeyListener(inputManager);
        // allows inputManager of the Tab and other focus traversal keys
        component.setFocusTraversalKeysEnabled(false);
    }

    /**
     * Change inputManager bindings, should only be used in a control menu where the
     * player is changing bindings
     *
     * @param originalCode the code that is currently bound
     * @param newCode      the code to bind, this code should be got from one of the
     *                     get*EventType*InputCode() methods
     */
    public void changeUserBinding(int originalCode, int newCode) {
        user.removeInputBinding(originalCode);
        user.setInputBinding(newCode, user.getAction(originalCode));
        setInputHandler(activeContext.getInputHandler());
    }

    /**
     * Sets context specific inputManager bindings. The combined context specific
     * bindings and {@link User} bindings are used when the context is
     * entered. When the bindings are combined, the context specific bindings
     * always override the user profile bindings. Game actions should not be
     * reused in menus to avoid having multiple keys bound to the same action
     *
     * @param contextType the contextType to bind the controls to
     * @param inputCode   the inputCode to bind
     * @param action      the action to bind
     */
    public void setContextBinding(ContextType contextType, int inputCode, Action action) {
        ArrayList<InputMapping> mappings = contextTypeMap.get(contextType);
        boolean duplicateNotFound = true;
        int i = 0;
        while (duplicateNotFound && i < mappings.size()) {
            InputMapping mapping = mappings.get(i);
            if (mapping.getInputCode() == inputCode && mapping.getAction() == action) {
                mappings.remove(mapping);
                duplicateNotFound = false;
            }
            i++;
        }
        mappings.add(new InputMapping(inputCode, action));
    }

    public Object getUserProperty(String propertyName) {
        return user.getProperty(propertyName);
    }

    public void setUserProperty(String propertyName, Serializable property) {
        user.setProperty(propertyName, property);
    }


    /**
     * Returns the time in nanoseconds of the next input event.
     *
     * @return the time in nanoseconds of the next input event.<br></br>
     *         Long.MAX_VALUE is returned if no events are in the queue
     */
    public long getNextInputEventTime() {
        return inputManager.getNextEventTime();
    }

    public void handleEvents(long cutOffTime) {
        inputManager.handleEvents(cutOffTime);
    }

    public void setFullScreen() {
        screenManager.setFullScreen();
    }

    public void restoreScreen() {
        screenManager.restoreScreen();
    }

    public double getFrameRate() {
        return gameTimer.getFrameRate();
    }

    public double getUpdateRate() {
        return gameTimer.getUpdateRate();
    }

    public int getWidth() {
        return screenManager.getFullScreenWindow().getWidth();
    }

    public int getHeight() {
        return screenManager.getFullScreenWindow().getHeight();
    }

    public void update(double elapsedTime) {
        activeContext.updateWorld(elapsedTime);
    }

    public Graphic loadImage(String path) throws IOException {
        BufferedImage im = ImageIO.read(getClass().getResource(path));
        Graphic result = new ImageGraphic(screenManager.getCompatibleImageVersion(im));
        im.flush();//to save some memory right away
        return result;
    }

    public BufferedImage createCompatibleImage(int width, int height) {
        return screenManager.createCompatibleImage(width, height, BufferedImage.BITMASK);
    }

    public void resetCursor() {
        if (!activeContext.isShowingMouseCursor()) {
            resetMouseCursor(screenManager.getFullScreenWindow());
        }
    }

    public void draw() {
        Graphics2D g2D = screenManager.getGraphics();
        activeContext.draw(g2D);
        //clean up the graphics object and update the screenManager
        g2D.dispose();
        screenManager.updateGraphics();
    }

    @Override
    public void mouseMoved(double x, double y, double velocityX, double velocityY) {
        // TODO mouse is static think about making it not so
        MouseMotion.mouseMoved(velocityX, velocityY);
    }

    public User getUser() {
        return user;
    }
}

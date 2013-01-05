package gameengine.input;

import gameengine.GameController;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

/**
 * Manages incoming input events
 * @author davidrusu
 */
public class InputManager implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener{
    private GameController gameController;
    private EventQueue eventQueue;
    private double mouseWheelRes = 1;
    private double mouseWheelRotation = 0;//This accumulates the wheel rotations for when the wheel moves a fraction of a tick
    private boolean isRelativeMouseMode = false;
    private int centerX, centerY;
    private Robot robot = null;
    private final Object mouseLock = new Object();
    private int currentMouseX, currentMouseY;
    private double gameMouseX = 0, gameMouseY = 0;
    private double mouseVelX = 0, mouseVelY = 0;
    private boolean mouseMoving = true;
    
    public InputManager(GameController gameController){
        this.gameController = gameController;
        eventQueue = new EventQueue(gameController);
        try{
            robot = new Robot();
        }catch(Exception e){
            //ignore
        }
    }

    public boolean isRelativeMouseMode(){
        return isRelativeMouseMode;
    }
    
    /**
     * When enabled the mouse is reset to the specified x and y coordinates(usually the center of the screen).
     * When a mouse event happens the x and y mouse coordinates provided to the handler are relative the the specified x and y coordinates
     * @param centerX
     * @param centerY
     */
    public void enableRelativeMouseMove(int centerX, int centerY){
        isRelativeMouseMode = true;
        resetMouseInfo(0, 0);
        this.centerX = centerX;
        this.centerY = centerY;
        robot.mouseMove(centerX, centerY);
    }
    
    public void clearInputQueue(){
        eventQueue.clearQueue();
    }

    public void disableRelativeMouseMove(){
        isRelativeMouseMode = false;
        resetMouseInfo(centerX, centerY);
    }
    
    private void resetMouseInfo(int x, int y){
        currentMouseX = x;
        currentMouseY = y;
        gameMouseX = x;
        gameMouseY = y;
        mouseVelX = 0;
        mouseVelY = 0;
    }

    public void handleEvents(long cutOffTime){
        eventQueue.handleEvents(cutOffTime);
    }

    /**
     * Returns the time in nanoseconds of the next event. Long.MAX_VALUE is returned if there are no events in queue
     * @return
     */
    public long getNextEventTime(){
        return eventQueue.getNextEventTime();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        e.consume();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        int inputCode = InputCode.getMouseButtonInputCode(e.getButton());
        handlePressInput(inputCode);
        e.consume();
    }
    @Override
    public void mouseReleased(MouseEvent e) {
        int inputCode = InputCode.getMouseButtonInputCode(e.getButton());
        handleReleaseInput(inputCode);
        e.consume();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        e.consume();
    }

    @Override
    public void mouseExited(MouseEvent e) {
        e.consume();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        synchronized(mouseLock){
            int x = e.getX();
            int y = e.getY();
            if(isRelativeMouseMode){
                if(robot != null && (x != centerX || y != centerY)){
                    currentMouseX += x - centerX;
                    currentMouseY += y - centerY;
                    robot.mouseMove(centerX - x + e.getXOnScreen(), centerY - y + e.getYOnScreen());
                }
            }else{
                currentMouseX = e.getX();
                currentMouseY = e.getY();
            }
        }
        e.consume();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseMoved(e);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        e.consume();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int inputCode = InputCode.getKeyInputCode(e.getKeyCode());
        handlePressInput(inputCode);
        e.consume();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int inputCode = InputCode.getKeyInputCode(e.getKeyCode());
        handleReleaseInput(inputCode);
        e.consume();
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        mouseWheelRotation += e.getWheelRotation();
        if(mouseWheelRotation < 0){
            int inputCode = InputCode.getWheelUpInputCode();
            while(mouseWheelRotation <= -mouseWheelRes){
                mouseWheelRotation += mouseWheelRes;
                handlePressInput(inputCode);
            }
        }else{
            int inputCode = InputCode.getWheelDownInputCode();
            while(mouseWheelRotation >= mouseWheelRes){
                mouseWheelRotation -= mouseWheelRes;
                handlePressInput(inputCode);
            }
        }
        e.consume();
    }
    
    /**
     * Updates the mouse velocity
     *
     * @param frameTime the amount of time last frame took to complete
     */
    public void updateMouseVelocity(double frameTime){
        synchronized(mouseLock){
            if(isRelativeMouseMode){
                mouseVelX = currentMouseX / frameTime;
                mouseVelY = currentMouseY / frameTime;
                currentMouseX = 0;
                currentMouseY = 0;
            }else{
                mouseVelX = (currentMouseX - gameMouseX) / frameTime;
                mouseVelY = (currentMouseY - gameMouseY) / frameTime;
            }
        }
    }

    /**
     *
     * Updates the {@link MouseMovedHandler} with the new mouse velocity
     *
     * @param elapsedTime The amount of time in milliseconds since last update
     */
    public void updateMouseMovedHandler(double elapsedTime){
        if(mouseVelX == 0 && mouseVelY == 0){
            if(!mouseMoving){
                return;
            }
            mouseMoving = false;
        }else{
            mouseMoving = true;
        }
        double dx = mouseVelX * elapsedTime;
        double dy = mouseVelY * elapsedTime;
        if(isRelativeMouseMode){
            gameController.getMouseMovedHandler().mouseMoved(dx, dy, mouseVelX, mouseVelY);
        }else{
            gameMouseX += dx;
            gameMouseY += dy;
            gameController.getMouseMovedHandler().mouseMoved(gameMouseX, gameMouseY, mouseVelX, mouseVelY);
        }
    }

    private void handlePressInput(int inputCode){
        if(gameController.isInputCodeMappedToAction(inputCode)){
            eventQueue.addPressedAction(inputCode, System.nanoTime());
        }
    }

    private void handleReleaseInput(int inputCode){
        if(gameController.isInputCodeMappedToAction(inputCode)) {
            eventQueue.addReleasedAction(inputCode, System.nanoTime());
        }
    }
}

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
    private static boolean isRelativeMouseMode = false;
    private int centerX, centerY;
    private Robot robot = null;

    public InputManager(GameController gameController){
        this.gameController = gameController;
        eventQueue = new EventQueue();
        try{
            robot = new Robot();
        }catch(Exception e){
            //ignore
        }
    }

    public static boolean isRelativeMouseMode(){
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
       this.centerX = centerX;
       this.centerY = centerY;
       robot.mouseMove(centerX, centerY);
    }
    
    public void clearInputQueue(){
        eventQueue.clearQueue();
    }

    public void disableRelativeMouseMove(){
        isRelativeMouseMode = false;
    }

    public void handleEvents(long cutOffTime){
        eventQueue.handleEvents(cutOffTime);
    }

    public void interpolateMouse(long currentTimeNanos, long updatTimeNanos, boolean branch){
        eventQueue.interpolateMouse(currentTimeNanos, updatTimeNanos, branch);
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
        handlePressedEvent(inputCode);
        e.consume();
    }
    @Override
    public void mouseReleased(MouseEvent e) {
        int inputCode = InputCode.getMouseButtonInputCode(e.getButton());
        handleReleasedEvent(inputCode);
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
        MouseMovedHandler handler = gameController.getMouseMovedHandler();
        int x = e.getX();
        int y = e.getY();
        if(isRelativeMouseMode && robot != null){
            if(x != centerX || y != centerY){
                eventQueue.addMouseMovedAction(handler, x - centerX, y - centerY, System.nanoTime());
                int deltaX = e.getXOnScreen() - x;
                int deltaY = e.getYOnScreen() - y;
                robot.mouseMove(centerX + deltaX, centerY + deltaY);
            }
        }else{
            eventQueue.addMouseMovedAction(handler, x, y, System.nanoTime());
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
//        System.out.println(KeyEvent.getKeyText(e.getKeyCode()));
        int inputCode = InputCode.getKeyInputCode(e.getKeyCode());
        handlePressedEvent(inputCode);
        e.consume();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int inputCode = InputCode.getKeyInputCode(e.getKeyCode());
        handleReleasedEvent(inputCode);
        e.consume();
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        mouseWheelRotation += e.getWheelRotation();
        if(mouseWheelRotation < 0){
            int inputCode = InputCode.getWheelUpInputCode();
            while(mouseWheelRotation <= -mouseWheelRes){
                mouseWheelRotation += mouseWheelRes;
                handlePressedEvent(inputCode);
            }
        }else{
            int inputCode = InputCode.getWheelDownInputCode();
            while(mouseWheelRotation >= mouseWheelRes){
                mouseWheelRotation -= mouseWheelRes;
                handlePressedEvent(inputCode);
            }
        }
        e.consume();
    }

    private void handlePressedEvent(int inputCode){
        ActionHandler handler = gameController.getActionHandler(inputCode);
        if(handler != null){
            eventQueue.addPressedAction(handler, inputCode, System.nanoTime());
        }
    }

    private void handleReleasedEvent(int inputCode){
        ActionHandler handler = gameController.getActionHandler(inputCode);
        if(handler != null){
            eventQueue.addReleasedAction(handler, inputCode, System.nanoTime());
        }
    }
}
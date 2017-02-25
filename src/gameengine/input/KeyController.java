package gameengine.input;

import gameengine.core.GameCore;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Handles keyboard input.
 */
public class KeyController implements KeyListener {
    private final GameCore core;

    /**
     * Create a KeyController instance
     *
     * @param core The game core
     */
    public KeyController(GameCore core) {
        this.core = core;
    }

    /**
     * @see KeyListener#keyTyped(KeyEvent)
     */
    @Override
    public void keyTyped(KeyEvent e) {
        e.consume(); //do nothing
    }

    /**
     * @see KeyListener#keyPressed(KeyEvent)
     */
    @Override
    public void keyPressed(KeyEvent e) {
        int inputCode = InputCode.getKeyInputCode(e.getKeyCode());
        core.addGameEvent(Context -> Context.inputPressed(inputCode));
    }

    /**
     * @see KeyListener#keyReleased(KeyEvent)
     */
    @Override
    public void keyReleased(KeyEvent e) {
        int inputCode = InputCode.getKeyInputCode(e.getKeyCode());
        core.addGameEvent(Context -> Context.inputReleased(inputCode));
    }
}

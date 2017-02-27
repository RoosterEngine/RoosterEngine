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
     * Create a KeyController instance.
     *
     * @param core The game core
     */
    public KeyController(GameCore core) {
        this.core = core;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        e.consume(); //do nothing
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int inputCode = InputCode.getKeyInputCode(e.getKeyCode());
        core.addGameEvent(Context -> Context.inputPressed(inputCode));
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int inputCode = InputCode.getKeyInputCode(e.getKeyCode());
        core.addGameEvent(Context -> Context.inputReleased(inputCode));
    }
}

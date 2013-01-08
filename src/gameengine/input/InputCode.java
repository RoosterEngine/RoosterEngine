package gameengine.input;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 * List of input code constants and utility methods. the
 * representation formatting can be changed in the formatKeyString method
 *
 * @author davidrusu
 */
public class InputCode {

    private static final int PARTITION = Integer.MAX_VALUE / 4;
    private static final int KEY_OFFSET = 0;
    private static final int MOUSE_BUTTON_OFFSET = PARTITION * 2;
    private static final int MOUSE_WHEEL_OFFSET = PARTITION * 3;

    public static final int KEY_A = getKeyInputCode(KeyEvent.VK_A);
    public static final int KEY_B = getKeyInputCode(KeyEvent.VK_B);
    public static final int KEY_C = getKeyInputCode(KeyEvent.VK_C);
    public static final int KEY_D = getKeyInputCode(KeyEvent.VK_D);
    public static final int KEY_E = getKeyInputCode(KeyEvent.VK_E);
    public static final int KEY_F = getKeyInputCode(KeyEvent.VK_F);
    public static final int KEY_G = getKeyInputCode(KeyEvent.VK_G);
    public static final int KEY_H = getKeyInputCode(KeyEvent.VK_H);
    public static final int KEY_I = getKeyInputCode(KeyEvent.VK_I);
    public static final int KEY_J = getKeyInputCode(KeyEvent.VK_J);
    public static final int KEY_K = getKeyInputCode(KeyEvent.VK_K);
    public static final int KEY_L = getKeyInputCode(KeyEvent.VK_L);
    public static final int KEY_M = getKeyInputCode(KeyEvent.VK_M);
    public static final int KEY_N = getKeyInputCode(KeyEvent.VK_N);
    public static final int KEY_O = getKeyInputCode(KeyEvent.VK_O);
    public static final int KEY_P = getKeyInputCode(KeyEvent.VK_P);
    public static final int KEY_Q = getKeyInputCode(KeyEvent.VK_Q);
    public static final int KEY_R = getKeyInputCode(KeyEvent.VK_R);
    public static final int KEY_S = getKeyInputCode(KeyEvent.VK_S);
    public static final int KEY_T = getKeyInputCode(KeyEvent.VK_T);
    public static final int KEY_U = getKeyInputCode(KeyEvent.VK_U);
    public static final int KEY_V = getKeyInputCode(KeyEvent.VK_V);
    public static final int KEY_W = getKeyInputCode(KeyEvent.VK_W);
    public static final int KEY_X = getKeyInputCode(KeyEvent.VK_X);
    public static final int KEY_Y = getKeyInputCode(KeyEvent.VK_Y);
    public static final int KEY_Z = getKeyInputCode(KeyEvent.VK_Z);
    public static final int KEY_0 = getKeyInputCode(KeyEvent.VK_0);
    public static final int KEY_1 = getKeyInputCode(KeyEvent.VK_1);
    public static final int KEY_2 = getKeyInputCode(KeyEvent.VK_2);
    public static final int KEY_3 = getKeyInputCode(KeyEvent.VK_3);
    public static final int KEY_4 = getKeyInputCode(KeyEvent.VK_4);
    public static final int KEY_5 = getKeyInputCode(KeyEvent.VK_5);
    public static final int KEY_6 = getKeyInputCode(KeyEvent.VK_6);
    public static final int KEY_7 = getKeyInputCode(KeyEvent.VK_7);
    public static final int KEY_8 = getKeyInputCode(KeyEvent.VK_8);
    public static final int KEY_9 = getKeyInputCode(KeyEvent.VK_9);
    public static final int KEY_UP = getKeyInputCode(KeyEvent.VK_UP);
    public static final int KEY_DOWN = getKeyInputCode(KeyEvent.VK_DOWN);
    public static final int KEY_LEFT = getKeyInputCode(KeyEvent.VK_LEFT);
    public static final int KEY_RIGHT = getKeyInputCode(KeyEvent.VK_RIGHT);
    public static final int KEY_ALT = getKeyInputCode(KeyEvent.VK_ALT);
    public static final int KEY_FRONT_SLASH = getKeyInputCode(KeyEvent.VK_SLASH);
    public static final int KEY_BACK_SLASH = getKeyInputCode(KeyEvent.VK_BACK_SLASH);
    public static final int KEY_BACK_SPACE = getKeyInputCode(KeyEvent.VK_BACK_SPACE);
    public static final int KEY_OPEN_BRACKET = getKeyInputCode(KeyEvent.VK_OPEN_BRACKET);
    public static final int KEY_CLOSE_BRACKET = getKeyInputCode(KeyEvent.VK_CLOSE_BRACKET);
    public static final int KEY_CAPS_LOCK = getKeyInputCode(KeyEvent.VK_CAPS_LOCK);
    public static final int KEY_COLON = getKeyInputCode(KeyEvent.VK_COLON);
    public static final int KEY_COMMA = getKeyInputCode(KeyEvent.VK_COMMA);
    public static final int KEY_CONTROL = getKeyInputCode(KeyEvent.VK_CONTROL);
    public static final int KEY_END = getKeyInputCode(KeyEvent.VK_END);
    public static final int KEY_ENTER = getKeyInputCode(KeyEvent.VK_ENTER);
    public static final int KEY_EQUALS = getKeyInputCode(KeyEvent.VK_EQUALS);
    public static final int KEY_ESCAPE = getKeyInputCode(KeyEvent.VK_ESCAPE);
    public static final int KEY_F1 = getKeyInputCode(KeyEvent.VK_F1);
    public static final int KEY_F2 = getKeyInputCode(KeyEvent.VK_F2);
    public static final int KEY_F3 = getKeyInputCode(KeyEvent.VK_F3);
    public static final int KEY_F4 = getKeyInputCode(KeyEvent.VK_F4);
    public static final int KEY_F5 = getKeyInputCode(KeyEvent.VK_F5);
    public static final int KEY_F6 = getKeyInputCode(KeyEvent.VK_F6);
    public static final int KEY_F7 = getKeyInputCode(KeyEvent.VK_F7);
    public static final int KEY_F8 = getKeyInputCode(KeyEvent.VK_F8);
    public static final int KEY_F9 = getKeyInputCode(KeyEvent.VK_F9);
    public static final int KEY_F10 = getKeyInputCode(KeyEvent.VK_F10);
    public static final int KEY_F11 = getKeyInputCode(KeyEvent.VK_F11);
    public static final int KEY_F12 = getKeyInputCode(KeyEvent.VK_F12);
    public static final int KEY_HOME = getKeyInputCode(KeyEvent.VK_HOME);
    public static final int KEY_INSERT = getKeyInputCode(KeyEvent.VK_INSERT);
    public static final int KEY_KP_UP = getKeyInputCode(KeyEvent.VK_KP_UP);
    public static final int KEY_KP_DOWN = getKeyInputCode(KeyEvent.VK_KP_DOWN);
    public static final int KEY_KP_LEFT = getKeyInputCode(KeyEvent.VK_KP_LEFT);
    public static final int KEY_KP_RIGHT = getKeyInputCode(KeyEvent.VK_KP_RIGHT);
    public static final int KEY_KP_MULTIPLY = getKeyInputCode(KeyEvent.VK_MULTIPLY);
    public static final int KEY_META = getKeyInputCode(KeyEvent.VK_META);
    public static final int KEY_MINUS = getKeyInputCode(KeyEvent.VK_MINUS);
    public static final int KEY_NUMPAD_0 = getKeyInputCode(KeyEvent.VK_NUMPAD0);
    public static final int KEY_NUMPAD_1 = getKeyInputCode(KeyEvent.VK_NUMPAD1);
    public static final int KEY_NUMPAD_2 = getKeyInputCode(KeyEvent.VK_NUMPAD2);
    public static final int KEY_NUMPAD_3 = getKeyInputCode(KeyEvent.VK_NUMPAD3);
    public static final int KEY_NUMPAD_4 = getKeyInputCode(KeyEvent.VK_NUMPAD4);
    public static final int KEY_NUMPAD_5 = getKeyInputCode(KeyEvent.VK_NUMPAD5);
    public static final int KEY_NUMPAD_6 = getKeyInputCode(KeyEvent.VK_NUMPAD6);
    public static final int KEY_NUMPAD_7 = getKeyInputCode(KeyEvent.VK_NUMPAD7);
    public static final int KEY_NUMPAD_8 = getKeyInputCode(KeyEvent.VK_NUMPAD8);
    public static final int KEY_NUMPAD_9 = getKeyInputCode(KeyEvent.VK_NUMPAD9);
    public static final int KEY_PAGE_UP = getKeyInputCode(KeyEvent.VK_PAGE_UP);
    public static final int KEY_PAGE_DOWN = getKeyInputCode(KeyEvent.VK_PAGE_DOWN);
    public static final int KEY_PERIOD = getKeyInputCode(KeyEvent.VK_PERIOD);
    public static final int KEY_PRINTSCREEN = getKeyInputCode(KeyEvent.VK_PRINTSCREEN);
    public static final int KEY_QUOTE = getKeyInputCode(KeyEvent.VK_QUOTE);
    public static final int KEY_SCROLL_LOCK = getKeyInputCode(KeyEvent.VK_SCROLL_LOCK);
    public static final int KEY_SEMICOLON = getKeyInputCode(KeyEvent.VK_SEMICOLON);
    public static final int KEY_SHIFT = getKeyInputCode(KeyEvent.VK_SHIFT);
    public static final int KEY_SPACE = getKeyInputCode(KeyEvent.VK_SPACE);
    public static final int KEY_SUBTRACT = getKeyInputCode(KeyEvent.VK_SUBTRACT);
    public static final int KEY_TAB = getKeyInputCode(KeyEvent.VK_TAB);
    public static final int MOUSE_LEFT_BUTTON = getMouseButtonInputCode(MouseEvent.BUTTON1);
    public static final int MOUSE_MIDDLE_BUTTON = getMouseButtonInputCode(MouseEvent.BUTTON2);
    public static final int MOUSE_RIGHT_BUTTON = getMouseButtonInputCode(MouseEvent.BUTTON3);
    public static final int MOUSE_WHEEL_UP = MOUSE_WHEEL_OFFSET;
    public static final int MOUSE_WHEEL_DOWN = MOUSE_WHEEL_OFFSET + 1;

    //private constructor to avoid any instances of this class from being created
    private InputCode() {
    }

    /**
     * Generates the input code from a {@link KeyEvent} code
     *
     * @param keyCode the VK code form {@link  KeyEvent}
     * @return the integer input code associated with the key event code
     */
    public static int getKeyInputCode(int keyCode) {
        return keyCode + KEY_OFFSET;
    }

    /**
     * Gets the input code associated with the a specified mouse button
     *
     * @param button the integer returned when getMouseButton is called on a MouseEvent
     * @return the integer input code for the specified mouse button
     */
    public static int getMouseButtonInputCode(int button) {
        return button + MOUSE_BUTTON_OFFSET;
    }

    /**
     * Gets the input code associated with a mouse wheel up event
     *
     * @return the integer input code for a mouse wheel up event
     */
    public static int getWheelUpInputCode() {
        return MOUSE_WHEEL_UP;
    }

    /**
     * Gets the input code associated with a mouse wheel down event
     *
     * @return the integer input code for a mouse wheel down event
     */
    public static int getWheelDownInputCode() {
        return MOUSE_WHEEL_DOWN;
    }

    /**
     * gets the string representation of an input code.
     *
     * @param inputCode the input code
     * @return the string representation of the provided input code
     */
    public static String getStringRep(int inputCode) {

        if (inputCode < MOUSE_BUTTON_OFFSET) {
            return formatKeyString(KeyEvent.getKeyText(inputCode - KEY_OFFSET));
        }

        if (inputCode == MOUSE_LEFT_BUTTON) {
            return "LEFT MOUSE BUTTON";
        } else if (inputCode == MOUSE_RIGHT_BUTTON) {
            return "RIGHT MOUSE BUTTON";
        } else if (inputCode == MOUSE_MIDDLE_BUTTON) {
            return "MIDDLE MOUSE BUTTON";
        } else if (inputCode == MOUSE_WHEEL_UP) {
            return "MOUSE WHEEL UP";
        } else if (inputCode == MOUSE_WHEEL_DOWN) {
            return "MOUSE_WHEEL_DOWN";
        }
        return "NOT RECOGNIZED";
    }

    /**
     * Formats the string representation of the key input codes. Modify this
     * method to change how key input codes are represented. since there are so
     * few mouse input codes, those are hard coded where they are created and
     * can be modified there
     *
     * @param name the input string representation to format
     * @return the formated string
     */
    private static String formatKeyString(String name) {
        return name + " Key";
    }

    public static boolean isKeyboardInput(int inputCode) {
        return inputCode >= KEY_OFFSET && inputCode < KEY_OFFSET + PARTITION;
    }

    public static boolean isMouseButtonInput(int inputCode) {
        return inputCode >= MOUSE_BUTTON_OFFSET
                && inputCode < MOUSE_BUTTON_OFFSET + PARTITION;
    }

    public static boolean isMouseWheelInput(int inputCode) {
        return inputCode >= MOUSE_WHEEL_OFFSET
                && inputCode < MOUSE_WHEEL_OFFSET + PARTITION;
    }
}
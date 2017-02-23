package gameengine.context;

import gameengine.collisiondetection.Collision;
import gameengine.core.GameController;
import gameengine.entities.BasicButton;
import gameengine.entities.Pointer;
import gameengine.graphics.MutableColor;
import gameengine.graphics.Renderer;
import gameengine.graphics.ScreenManager;
import gameengine.graphics.image.Graphic;
import gameengine.graphics.image.OvalGraphic;
import gameengine.input.InputCode;
import gameengine.motion.EffectFactory;

/**
 * @author davidrusu
 */
public class BasicMenu extends Context {
    private static final String MENU_UP = "Menu Up";
    private static final String MENU_DOWN = "Menu Down";
    private static final String EXIT = "Exit";
    private static final String MENU_SELECT = "Menu Select";
    private static final String MENU_MOUSE_SELECT = "Menu Mouse Select";

    private BasicButton[] buttons;
    private Pointer pointer;
    private int selectedIndex;
    private ButtonHandler buttonHandler;
    private Graphic background;
    private boolean isMousePressed = false;

    public BasicMenu(GameController controller, BasicButton[] buttons, ButtonHandler handler,
                     Graphic background) {
        this(controller, buttons, handler, background, 0.25, 0.25, 0.1, 0.25, 0.25);
    }

    public BasicMenu(GameController controller, BasicButton[] buttons, ButtonHandler handler,
                     Graphic background, double leftBorderRatio, double rightBorderRatio, double
                             topBorderRatio, double bottomBorderRatio, double paddingRatio) {
        super(controller);

        ScreenManager screen = controller.getScreenManager();
        pointer = new Pointer(new OvalGraphic(10, 10, new MutableColor(23, 44, 80)), screen
                .getWidth() / 2, screen.getHeight() / 2);

        this.buttons = buttons;


        world.addEntity(pointer);

        this.background = background;
        setupButtons(leftBorderRatio, rightBorderRatio, topBorderRatio, bottomBorderRatio,
                paddingRatio);
        for (BasicButton button : buttons) {
            world.addEntity(button);
        }
        selectedIndex = 0;
        buttons[0].select();
        buttonHandler = handler;
        setUpInput();
    }

    public void reset() {
        for (BasicButton button : buttons) {
            button.reset();
        }
    }

    @Override
    protected void updateContext(long gameTime, double mouseDeltaX, double mouseDeltaY, double
            mouseWheelRotation) {
        updateButtons();
    }

    private void updateButtons() {
        int buttonIndex = getButtonIndex(pointer.getX(), pointer.getY());
        if (buttonIndex >= 0) {
            changeSelectedButton(buttonIndex);
            BasicButton button = buttons[selectedIndex];
            if (isMousePressed) {
                button.setPressed();
            } else {
                button.select();
            }
        } else if (selectedIndex != buttonIndex && buttons[selectedIndex].isPressed()) {
            buttons[selectedIndex].setUnpressed();
        }
    }

    @Override
    protected void renderContext(Renderer renderer, long gameTime) {
//        background.draw(renderer, 0, 0);
    }

    @Override
    public void handleCollision(Collision collision) {
    }

    private void setupButtons(double leftBorderRatio, double rightBorderRatio, double
            topBorderRatio, double bottomBorderRatio, double paddingRatio) {
        ScreenManager screen = controller.getScreenManager();
        int width = screen.getWidth();
        int height = screen.getHeight();

        int leftBorder = (int) (width * leftBorderRatio);
        int topBorder = (int) (height * topBorderRatio);
        int bottomBorder = (int) (height * bottomBorderRatio);
        int padding = (int) ((height - topBorder - bottomBorder) / buttons.length * paddingRatio);
        int buttonWidth = width - leftBorder - (int) (width * rightBorderRatio);
        int buttonHeight = (height - topBorder - bottomBorder - padding * (buttons.length - 1)) /
                buttons.length;
        int currentY = topBorder + buttonHeight / 2;
        for (BasicButton button : buttons) {
            button.initialize(width / 2, currentY, buttonWidth, buttonHeight);
            currentY += buttonHeight + padding;
        }
        EffectFactory.setZipperEffect(buttons, width / 2, width, 0.2);
    }

    /**
     * Returns the number of the button under the specified point.
     *
     * @param x the x coordinate of where to check for a button
     * @param y the y coordinate of where to check for a button
     * @return the number of the button that is under the specified point,the number corresponds to
     * the the button order top to bottom when displayed. -1 is returned if there is no point below
     * the specified point
     */
    private int getButtonIndex(double x, double y) {
        for (int i = 0; i < buttons.length; i++) {
            if (buttons[i].contains(x, y)) {
                return i;
            }
        }
        return -1;
    }

    private void changeSelectedButton(int newSelectedIndex) {
        buttons[selectedIndex].deSelect();
        selectedIndex = newSelectedIndex;
        buttons[selectedIndex].select();
    }

    private void setUpInput() {
        mapInputAction(MENU_UP, InputCode.KEY_UP);
        mapInputAction(MENU_DOWN, InputCode.KEY_DOWN);
        mapInputAction(EXIT, InputCode.KEY_ESCAPE);
        mapInputAction(MENU_SELECT, InputCode.KEY_ENTER);
        mapInputAction(MENU_SELECT, InputCode.KEY_SPACE);
        mapInputAction(MENU_MOUSE_SELECT, InputCode.MOUSE_LEFT_BUTTON);

        mapActionStartedHandler(MENU_UP, () -> {
            int newSelectedIndex = selectedIndex - 1;
            if (newSelectedIndex == -1) {
                newSelectedIndex = buttons.length - 1;
            }
            changeSelectedButton(newSelectedIndex);
        });

        mapActionStartedHandler(MENU_DOWN, () -> {
            changeSelectedButton((selectedIndex + 1) % buttons.length);
        });

        mapActionStartedHandler(EXIT, () -> {
            controller.exitContext();
        });

        mapActionStartedHandler(MENU_SELECT, () -> {
            BasicButton button = buttons[selectedIndex];
            button.setPressed();
        });

        mapActionStoppedHandler(MENU_SELECT, () -> {
            BasicButton button = buttons[selectedIndex];
            buttonHandler.buttonActivated(button);
            button.setUnpressed();
        });

        mapActionStartedHandler(MENU_MOUSE_SELECT, () -> {
            if (selectedIndex >= 0) {
                BasicButton button = buttons[selectedIndex];
                button.setPressed();
            }
            isMousePressed = true;
        });

        mapActionStoppedHandler(MENU_MOUSE_SELECT, () -> {
            if (selectedIndex >= 0) {
                BasicButton button = buttons[selectedIndex];
                buttonHandler.buttonActivated(button);
                button.setUnpressed();
            }
            isMousePressed = false;
        });
    }
}

package gameengine.context;

import gameengine.GameController;
import gameengine.collisiondetection.Collision;
import gameengine.entities.BasicButton;
import gameengine.entities.Pointer;
import gameengine.graphics.Graphic;
import gameengine.graphics.OvalGraphic;
import gameengine.input.Action;
import gameengine.input.ActionHandler;
import gameengine.input.InputCode;
import gameengine.motion.EffectFactory;

import java.awt.*;

/**
 * @author davidrusu
 */
public class BasicMenu extends Context {

    private BasicButton[] buttons;
    private Pointer pointer = new Pointer(new OvalGraphic(10, 10, new Color(23, 44, 80)), getWidth() / 2, getHeight() / 2);
    private int selectedIndex;
    private ButtonHandler buttonHandler;
    private Graphic background;
    private boolean isMousePressed = false;

    public BasicMenu(GameController controller, ContextType type, BasicButton[] buttons, ButtonHandler handler, Graphic background) {
        this(controller, type, buttons, handler, background, 0.25, 0.25, 0.1, 0.25, 0.25);
    }

    public BasicMenu(GameController controller, ContextType type, BasicButton[] buttons, ButtonHandler handler, Graphic background, double leftBorderRatio, double rightBorderRatio, double topBorderRatio, double bottomBorderRatio, double paddingRatio) {
        super(controller, type);
        this.buttons = buttons;


        world.addEntity(pointer);

        this.background = background;
        setupButtons(leftBorderRatio, rightBorderRatio, topBorderRatio, bottomBorderRatio, paddingRatio);
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
    public void update(double elapsedTime) {
        updateButtons();
        background.update(elapsedTime);
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
    public void draw(Graphics2D g) {
        background.draw(g, 0, 0);
        world.draw(this, g);
    }

    @Override
    public void handleCollision(Collision collision) {
    }

    private void setupButtons(double leftBorderRatio, double rightBorderRatio, double topBorderRatio, double bottomBorderRatio, double paddingRatio) {
        int leftBorder = (int) (width * leftBorderRatio);
        int topBorder = (int) (height * topBorderRatio);
        int bottomBorder = (int) (height * bottomBorderRatio);
        int padding = (int) ((height - topBorder - bottomBorder) / buttons.length * paddingRatio);
        int buttonWidth = width - leftBorder - (int) (width * rightBorderRatio);
        int buttonHeight = (height - topBorder - bottomBorder - padding * (buttons.length - 1)) / buttons.length;
        int currentY = topBorder + buttonHeight / 2;
        for (BasicButton button : buttons) {
            button.initialize(width / 2, currentY, buttonWidth, buttonHeight);
            currentY += buttonHeight + padding;
        }
        EffectFactory.setZipperEffect(buttons, width / 2, width, 0.2);
    }

    /**
     * Returns the number of the button under the specified point
     *
     * @param x the x coordinate of where to check for a button
     * @param y the y coordinate of where to check for a button
     * @return the number of the button that is under the specified point,the
     *         number corresponds to the the button order top to bottom when displayed.
     *         -1 is returned if there is no point below the specified point
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
        controller.setContextBinding(contextType, InputCode.KEY_R, Action.RESTART_GAME);
        bindAction(Action.RESTART_GAME, new ActionHandler() {

            @Override
            public void startAction(Action action, int inputCode) {
            }

            @Override
            public void stopAction(Action action, int inputCode) {
            }
        });

        bindAction(Action.MENU_UP, new ActionHandler() {
            @Override
            public void startAction(Action action, int inputCode) {
                int newSelectedIndex = selectedIndex - 1;
                if (newSelectedIndex == -1) {
                    newSelectedIndex = buttons.length - 1;
                }
                changeSelectedButton(newSelectedIndex);
            }

            @Override
            public void stopAction(Action action, int inputCode) {
            }
        });

        bindAction(Action.MENU_DOWN, new ActionHandler() {
            @Override
            public void startAction(Action action, int inputCode) {
                changeSelectedButton((selectedIndex + 1) % buttons.length);
            }

            @Override
            public void stopAction(Action action, int inputCode) {
            }
        });

        bindAction(Action.EXIT_GAME, new ActionHandler() {
            @Override
            public void startAction(Action action, int inputCode) {
            }

            @Override
            public void stopAction(Action action, int inputCode) {
                controller.stopGame();
            }
        });

        bindAction(Action.MENU_SELECT, new ActionHandler() {
            @Override
            public void startAction(Action action, int inputCode) {
                BasicButton button = buttons[selectedIndex];
                button.setPressed();
            }

            @Override
            public void stopAction(Action action, int inputCode) {
                BasicButton button = buttons[selectedIndex];
                buttonHandler.buttonActivated(button);
                button.setUnpressed();
            }
        });

        bindAction(Action.MENU_MOUSE_SELECT, new ActionHandler() {

            @Override
            public void startAction(Action action, int inputCode) {
                if (selectedIndex >= 0) {
                    BasicButton button = buttons[selectedIndex];
                    button.setPressed();
                }
                isMousePressed = true;
            }

            @Override
            public void stopAction(Action action, int inputCode) {
                if (selectedIndex >= 0) {
                    BasicButton button = buttons[selectedIndex];
                    buttonHandler.buttonActivated(button);
                    button.setUnpressed();
                }
                isMousePressed = false;
            }
        });
    }
}

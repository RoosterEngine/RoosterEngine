package gameengine;

import gameengine.input.Action;
import gameengine.input.ActionHandler;
import gameengine.input.InputCode;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

/**
 *
 * @author davidrusu
 */
public class BasicMenu extends Context{
    
    private BasicButton[] buttons;
    private int selectedIndex;
    private double mouseX, mouseY;
    private ArrayList<Integer> pastMouseX = new ArrayList<Integer>();
    private ArrayList<Integer> pastMouseY = new ArrayList<Integer>();
    private boolean isMousePressed = false;
    private ButtonHandler buttonHandler;
    private Graphic background;
    
    public BasicMenu(GameController controller, ContextType type, BasicButton[] buttons, ButtonHandler handler, Graphic background){
        this(controller, type, buttons, handler, background, 0.25, 0.25, 0.1, 0.25, 0.25);
    }
    
    public BasicMenu(GameController controller, ContextType type, BasicButton[] buttons, ButtonHandler handler, Graphic background, double leftBorderRatio, double rightBorderRatio, double topBorderRatio, double bottomBorderRatio, double paddingRatio){
        super(controller, type, false, false);
        this.buttons = buttons;
        this.background = background;
        setupButtons(leftBorderRatio, rightBorderRatio, topBorderRatio, bottomBorderRatio, paddingRatio);
        selectedIndex = 0;
        buttons[0].select();
        buttonHandler = handler;
        setUpInput();
    }
    
    @Override
    public void update(double elapsedTime) {
        for(BasicButton button: buttons){
            button.update(elapsedTime);
        }
        background.update(elapsedTime);
    }

    @Override
    public void mouseMoved(double x, double y, double velocityX, double velocityY) {
        mouseX = x;
        mouseY = y;
        int buttonIndex = getButtonIndex(x, y);
        if(buttonIndex >= 0){
            changeSelectedButton(buttonIndex);
            BasicButton button = buttons[selectedIndex];
            if(isMousePressed){
                button.setPressed();
            }else{
                button.select();
            }
        }else if(selectedIndex != buttonIndex && buttons[selectedIndex].isPressed()){
            buttons[selectedIndex].setUnpressed();
        }
    }
    
    @Override
    public void draw(Graphics2D g) {
        background.draw(g, 0, 0);
        for(BasicButton button: buttons){
            button.draw(g);
        }
//        g.setColor(Color.ORANGE);
//        g.fillRect(0, 0, width, height);
        double cursorRadius = 10;
        g.setColor(new Color(80, 10, 70));
        g.fillOval((int)(mouseX - cursorRadius), (int)(mouseY - cursorRadius), (int)(cursorRadius * 2), (int)(cursorRadius * 2));
//        
//        cursorRadius = 2;
//        double color = 1.0 / pastMouseX.size() * 255;
//        for(int i = 0; i < pastMouseX.size(); i++){
//            g.setColor(new Color((int)(color * i), (int)(color * i), (int)(color * i)));
//            g.fillRect((int)(pastMouseX.get(i) - cursorRadius), (int)(pastMouseY.get(i) - cursorRadius), (int)(cursorRadius * 2), (int)(cursorRadius * 2));
//        }
    }
    
    private void setupButtons(double leftBorderRatio, double rightBorderRatio, double topBorderRatio, double bottomBorderRatio, double paddingRatio){
        int leftBorder = (int)(width * leftBorderRatio);
        int topBorder = (int)(height * topBorderRatio);
        int bottomBorder = (int)(height * bottomBorderRatio);
        int padding = (int)((height - topBorder - bottomBorder) / buttons.length * paddingRatio);
        int buttonWidth = width - leftBorder - (int)(width * rightBorderRatio);
        int buttonHeight = (int)(height - topBorder - bottomBorder - padding * (buttons.length - 1))/ buttons.length;
        int currentY = topBorder;
        for(int i = 0; i < buttons.length; i++){
            buttons[i].setDimensions(leftBorder, currentY, buttonWidth, buttonHeight);
            currentY += buttonHeight + padding;
        }
    }
    
    /**
     * Returns the number of the button under the specified point
     * @param x
     * @param y
     * @return the number of the button that is under the specified point,the
     * number corresponds to the the button order top to bottom when displayed.
     * -1 is returned if there is no point below the specified point
     */
    private int getButtonIndex(double x, double y){
        for(int i = 0; i < buttons.length; i++){
            if(buttons[i].contains(x, y)){
                return i;
            }
        }
        return -1;
    }
    
    private void changeSelectedButton(int newSelectedIndex){
        buttons[selectedIndex].deSelect();
        selectedIndex = newSelectedIndex;
        buttons[selectedIndex].select();
    }
    
    private void setUpInput(){
        controller.setContextBinding(contextType, InputCode.KEY_R, Action.RESTART_GAME);
        bindAction(Action.RESTART_GAME, new ActionHandler() {

            @Override
            public void startAction(int inputCode) {
            }

            @Override
            public void stopAction(int inputCode) {
                pastMouseX.clear();
                pastMouseY.clear();
            }
        });
        
        bindAction(Action.MENU_UP, new ActionHandler() {
            @Override
            public void startAction(int inputCode) {
                int newSelectedIndex = selectedIndex - 1;
                if(newSelectedIndex == -1){
                    newSelectedIndex = buttons.length - 1;
                }
                changeSelectedButton(newSelectedIndex);
            }

            @Override
            public void stopAction(int inputCode) {
            }
        });
        
        bindAction(Action.MENU_DOWN, new ActionHandler() {
            @Override
            public void startAction(int inputCode) {
                changeSelectedButton((selectedIndex + 1) % buttons.length);
            }

            @Override
            public void stopAction(int inputCode) {
            }
        });
        
        bindAction(Action.EXIT_GAME, new ActionHandler() {
            @Override
            public void startAction(int inputCode) {
            }

            @Override
            public void stopAction(int inputCode) {
                controller.stopGame();
            }
        });
        
        bindAction(Action.MENU_SELECT, new ActionHandler() {
            @Override
            public void startAction(int inputCode) {
                BasicButton button = buttons[selectedIndex];
                button.setPressed();
            }

            @Override
            public void stopAction(int inputCode) {
                BasicButton button = buttons[selectedIndex];
                buttonHandler.buttonActivated(button);
                button.setUnpressed();
            }
        });
        
        bindAction(Action.MENU_MOUSE_SELECT, new ActionHandler() {

            @Override
            public void startAction(int inputCode) {
                int buttonIndex = getButtonIndex(mouseX, mouseY);
                if(buttonIndex >= 0){
                    BasicButton button = buttons[buttonIndex];
                    button.setPressed();
                }
                isMousePressed = true;
            }

            @Override
            public void stopAction(int inputCode) {
                int buttonIndex = getButtonIndex(mouseX, mouseY);
                if(buttonIndex >= 0){
                    BasicButton button = buttons[buttonIndex];
                    buttonHandler.buttonActivated(button);
                    button.setUnpressed();
                }
                isMousePressed = false;
            }
        });
    }
}

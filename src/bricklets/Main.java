package bricklets;

import gameengine.GameController;
import gameengine.context.BasicMenu;
import gameengine.context.ButtonHandler;
import gameengine.context.ContextType;
import gameengine.entities.BasicButton;
import gameengine.graphics.SolidColorGraphic;
import gameengine.input.Action;
import gameengine.input.InputCode;

import java.awt.*;

public class Main implements ButtonHandler {
    private GameController controller;
    private BasicButton gameButton = new BasicButton("Start");
    private BasicButton testingButton = new BasicButton("Testing");
    private BasicButton exitButton = new BasicButton("Exit");
    private BasicButton optionsButton = new BasicButton("Options");
    private BasicButton controlsButton = new BasicButton("Controls");
    private BrickBreaker bricks;
    private BasicMenu menu;
    private Testing testing;

    public Main(GameController controller) {
        this.controller = controller;
        BasicButton[] buttons = {gameButton, testingButton, exitButton};
        testing = new Testing(controller);
        bricks = new BrickBreaker(controller);
        menu = new BasicMenu(controller, ContextType.MENU, buttons, this, new SolidColorGraphic(new Color(19, 9, 18), controller.getWidth(), controller.getHeight()));
    }

    public static void main(String[] args) {
//        Scanner input = new Scanner(System.in);
//        System.out.println("press Enter to start");
//        input.nextLine();
//        System.setProperty("sun.java2d.opengl","true");
//        System.setProperty("apple.awt.graphics.UseQuartz", "true");
        GameController controller = new GameController(60, 60, 50);
        setupInputToActionMappings(controller);
        Main main = new Main(controller);
        main.startGame();
    }

    public void startGame() {
        controller.enterContext(menu);
        controller.startGame();
    }

    @Override
    public void buttonActivated(BasicButton button) {
        if (button == gameButton) {
//            testColl.init();
//            controller.enterContext(testColl);
            controller.enterContext(bricks);
            menu.reset();
        } else if (button == testingButton) {
            testing.init();
            controller.enterContext(testing);
            menu.reset();
        } else if (button == exitButton) {
            controller.exitContext();
        }
    }

    private static void setupInputToActionMappings(GameController controller) {
        controller.setContextBinding(ContextType.MENU, InputCode.KEY_UP, Action.MENU_UP);
        controller.setContextBinding(ContextType.MENU, InputCode.KEY_DOWN, Action.MENU_DOWN);
        controller.setContextBinding(ContextType.MENU, InputCode.KEY_ESCAPE, Action.EXIT_GAME);
        controller.setContextBinding(ContextType.MENU, InputCode.KEY_ENTER, Action.MENU_SELECT);
        controller.setContextBinding(ContextType.MENU, InputCode.MOUSE_LEFT_BUTTON, Action.MENU_MOUSE_SELECT);
    }

}

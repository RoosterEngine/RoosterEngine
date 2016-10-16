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
    private BasicButton benchmarkButton = new BasicButton("Benchmark");
    private BasicButton testButton = new BasicButton("Test");

    private BasicButton exitButton = new BasicButton("Exit");
    private BasicButton optionsButton = new BasicButton("Options");
    private BasicButton controlsButton = new BasicButton("Controls");
    private BrickBreaker bricks;
    private BasicMenu menu;
    private Benchmark benchmark;
    private Test testing;

    public Main(GameController controller) {
        this.controller = controller;
        BasicButton[] buttons = {gameButton, benchmarkButton, testButton, exitButton};
        benchmark = new Benchmark(controller);
        bricks = new BrickBreaker(controller);
        testing = new Test(controller);
        menu = new BasicMenu(controller, ContextType.MENU, buttons, this, new SolidColorGraphic(new Color(19, 9, 18), controller.getWidth(), controller.getHeight()));
    }

    public static void main(String[] args) {
//        Scanner input = new Scanner(System.in);
//        System.out.println("press Enter to start");
//        input.nextLine();
//        input.close();

        GameController controller = new GameController(200);
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
        } else if (button == benchmarkButton) {
            benchmark.init();
            controller.enterContext(benchmark);
            menu.reset();
        } else if (button == exitButton) {
            controller.exitContext();
        } else if (button == testButton) {
            testing.init();
            controller.enterContext(testing);
            menu.reset();
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

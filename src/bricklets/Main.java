package bricklets;

import gameengine.context.BasicMenu;
import gameengine.context.ButtonHandler;
import gameengine.core.GameController;
import gameengine.entities.BasicButton;
import gameengine.graphics.RColor;
import gameengine.graphics.ScreenManager;
import gameengine.graphics.image.SolidColorGraphic;

public class Main implements ButtonHandler {
    private GameController controller;
    private BasicButton gameButton = new BasicButton("Start");
    private BasicButton benchmarkButton = new BasicButton("Benchmark");
    private BasicButton testButton = new BasicButton("Test");

    private BasicButton exitButton = new BasicButton("Exit");
    private BasicButton optionsButton = new BasicButton("Options");
    private BasicButton controlsButton = new BasicButton("Controls");
    private BasicMenu menu;

    public Main(GameController controller) {
        this.controller = controller;
        BasicButton[] buttons = {gameButton, benchmarkButton, testButton, exitButton};
        ScreenManager screen = controller.getScreenManager();
        menu = new BasicMenu(controller, buttons, this, new SolidColorGraphic(new RColor(19, 9,
                18), screen.getWidth(), screen.getHeight()));
    }

    public static void main(String[] args) {
        GameController controller = new GameController(60);
        Main main = new Main(controller);
        main.startGame();
    }

    public void startGame() {
        controller.enterContext(menu);
    }

    @Override
    public void buttonActivated(BasicButton button) {
        if (button == gameButton) {
            controller.enterContext(new BrickBreaker(controller));
            menu.reset();
        } else if (button == benchmarkButton) {
            controller.enterContext(new Benchmark(controller));
            menu.reset();
        } else if (button == exitButton) {
            controller.exitContext();
        } else if (button == testButton) {
            controller.enterContext(new Test(controller));
            menu.reset();
        }
    }
}

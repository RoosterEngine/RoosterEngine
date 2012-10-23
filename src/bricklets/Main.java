package bricklets;

import gameengine.*;
import gameengine.input.Action;
import gameengine.input.InputCode;
import java.awt.Color;

/**
 *
 * @author davidrusu
 */
public class Main implements ButtonHandler{
    
    private GameController controller;
    private BasicButton physicsButton = new BasicButton("Physics");
    private BasicButton seedButton = new BasicButton("Change Seed");
    private BasicButton testingButton = new BasicButton("Testing");
    private BasicButton exitButton = new BasicButton("Exit");
    private BasicButton optionsButton = new BasicButton("Options");
    private BasicButton controlsButton = new BasicButton("Controls");
    private Game game;
    private Context testing;
    
    public Main(GameController controller){
        this.controller = controller;
        game = new Game(controller);
        testing = new Testing(controller);
        BasicButton[] buttons = {physicsButton, seedButton, testingButton, optionsButton, controlsButton, exitButton};
        BasicMenu menu = new BasicMenu(controller, ContextType.MENU, buttons, this, new SolidColorGraphic(new Color(232,221,203), controller.getWidth(), controller.getHeight()));
        controller.enterContext(menu);
    }
    
    public static void main(String[] args){
        UserProfile profile = new UserProfile("Player");
        profile.setInputBinding(InputCode.KEY_LEFT, Action.GAME_LEFT);
        profile.setInputBinding(InputCode.KEY_RIGHT, Action.GAME_RIGHT);
        GameController controller = new GameController(60, 60, 1, profile);
        setupInputToActionMappings(controller);
        Main main = new Main(controller);
        main.startGame();
    }
    
    public void startGame(){
        controller.startGame();
    }
    
    @Override
    public void buttonActivated(BasicButton button) {
        if(button == physicsButton){
            controller.enterContext(game);
        }else if(button == seedButton){
            game.setSeed((long)(Math.random() * 100));
        }else if(button == testingButton){
            controller.enterContext(testing);
        }else if(button == exitButton){
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
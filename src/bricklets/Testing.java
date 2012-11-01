package bricklets;

import gameengine.Context;
import gameengine.ContextType;
import gameengine.GameController;
import gameengine.input.Action;
import gameengine.input.ActionHandler;
import gameengine.input.InputCode;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 *
 * @author david
 */
public class Testing extends Context{
    private double mouseX, mouseY;
    private Polygon polygon;
    
    public Testing(GameController controller){
        super(controller, ContextType.GAME, true, false);
        setupInput();
    }
    
    @Override
    public void update(double elapsedTime) {
    }

    @Override
    public void draw(Graphics2D g) {
        double x1 = width / 2, y1 = height / 2, x2 = 500, y2 = 300;
        Color bg = Color.BLACK;
        if(Vector2D.isPointsProjectionWithinLine(mouseX, mouseY, x2, y2, x1, y1)){
            bg = Color.DARK_GRAY;
        }
        g.setColor(bg);
        g.fillRect(0, 0, width, height);
        g.setColor(Color.WHITE);
        g.drawLine((int)x1, (int)y1, (int)x2, (int)y2);
//        g.translate(width / 2, height / 2);
    }

    @Override
    public void mouseMoved(double x, double y, double velocityX, double velocityY) {
        mouseX = x;
        mouseY = y;
    }
    
    private void setupInput(){
        controller.setContextBinding(contextType, InputCode.KEY_ESCAPE, Action.EXIT_GAME);
        bindAction(Action.EXIT_GAME, new ActionHandler() {

            @Override
            public void startAction(int inputCode) {
            }

            @Override
            public void stopAction(int inputCode) {
                controller.exitContext();
            }
        });
    }
}

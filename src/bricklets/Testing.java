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
    private int mouseX, mouseY;
    private Polygon polygon;
    
    public Testing(GameController controller){
        super(controller, ContextType.GAME, true, false);
        init();
        setupInput();
    }
    
    public void init(){
//        polygon = Polygon.getRandomConvexPolygon(350, 350, 3, 500, 0);
    }
    
    @Override
    public void update(double elapsedTime) {
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, width, height);
//        g.translate(width / 2, height / 2);
        
        Vector2D testLine = new Vector2D(width, width);
        double x1 = 0;
        double y1 = 0;
        double x2 = testLine.getX();
        double y2 = testLine.getY();
        
//        double dist = Vector2D.distToLine(mouseX, mouseY, x1, y1, x2, y2);
        double dist = new Vector2D(mouseX, mouseY).distToLine(new Vector2D(x1, y1), new Vector2D(x2, y2));
        g.setColor(Color.RED);
        g.drawLine((int)x1, (int)y1, (int)x2, (int)y2);
//        g.translate(-width / 2, -height / 2);
        g.drawString("dist " + dist, mouseX, mouseY);
    }

    @Override
    public void mouseMoved(int x, int y) {
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
        
//        controller.setContextBinding(contextType, InputCode.KEY_SPACE, Action.NEW_SHAPE);
//        bindAction(Action.NEW_SHAPE, new ActionHandler() {
//
//            @Override
//            public void startAction(int inputCode) {
//            }
//
//            @Override
//            public void stopAction(int inputCode) {
//                init();
//            }
//        });
    }
}
package bricklets;

import gameengine.Context;
import gameengine.ContextType;
import gameengine.GameController;
import gameengine.input.Action;
import gameengine.input.ActionHandler;
import gameengine.input.InputCode;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Random;

/**
 *
 * @author david
 */
public class TestingSeperateAxisCollision extends Context{

    private Polygon[] polygons;
    private Vector2D[] velocities;
    private Color defaultColor = Color.DARK_GRAY;
    private Color intersectingColor = Color.RED.darker();
    
    public TestingSeperateAxisCollision(GameController controller){
        super(controller, ContextType.GAME, true, false);
        createPolygons();
        setupInput();
    }
    
    private void setupInput(){
        controller.setContextBinding(contextType, InputCode.KEY_ESCAPE, Action.EXIT_GAME);
        
        bindAction(Action.EXIT_GAME, new ActionHandler() {

            @Override
            public void startAction(int inputCode) {
            }

            @Override
            public void stopAction(int inputCode) {
                createPolygons();
                controller.exitContext();
            }
        });
    }
    
    private void createPolygons(){
        polygons = new Polygon[500];
        velocities = new Vector2D[polygons.length];
        Random rand = new Random();
        double maxVel = 0.1;
        for(int i = 0; i < polygons.length; i++){
            velocities[i] = new Vector2D(rand.nextDouble() * maxVel - maxVel / 2, rand.nextDouble() * maxVel - maxVel / 2);
            double radius = rand.nextDouble() * 20 + 5;
            double x = rand.nextDouble() * (width - radius * 2) + radius;
            double y = rand.nextDouble() * (height - radius * 2) + radius;
            int numPoints = (int)(rand.nextDouble() * 9) + 3;
            double[] xPoints = new double[numPoints];
            double[] yPoints = new double[numPoints];
            double angle = 2 * Math.PI / numPoints;
            for(int p = 0; p < numPoints; p++){
                double pX = Math.cos(angle * p + rand.nextDouble() * angle/2 - angle) * radius;
                double pY = Math.sin(angle * p + rand.nextDouble() * angle/2 - angle) * radius;
                xPoints[p] = pX;
                yPoints[p] = pY;
            }
            polygons[i] = new Polygon(x, y, 0, 0, xPoints, yPoints);
        }
    }
    
    @Override
    public void update(double elapsedTime) {
        for(Polygon polygon: polygons){
            polygon.setColor(defaultColor);
        }
        for(int i = 0; i < polygons.length - 1; i++){
            for(int j = i + 1; j < polygons.length; j++){
                if(polygons[i].isIntersecting(polygons[j])){
                    polygons[i].setColor(intersectingColor);
                    polygons[j].setColor(intersectingColor);
                }
            }
        }
        updatePolygons(elapsedTime);
    }
    
    private void updatePolygons(double elapsedTime){
        double k = 0.0001;
        for(int i = 1; i < polygons.length; i++){
            double x = polygons[i].getX();
            double y = polygons[i].getY();
            if(x <= 0){
                velocities[i].add(-x * k, 0);
            }else if(x >= width){
                velocities[i].add((width - x) * k, 0);
            }else if(y <= 0){
                velocities[i].add(0, -y * k);
            }else if(y >= height){
                velocities[i].add(0, (height - y) * k);
            }
            polygons[i].updatePosition(x + velocities[i].getX() * elapsedTime, y + velocities[i].getY() * elapsedTime);
        }
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        for(Polygon polygon: polygons){
            polygon.draw(g);
        }
        g.setColor(Color.BLACK);
        g.drawString("fps: " + controller.getFrameRate(), 50, 50);
        g.drawString("ups: " + controller.getUpdateRate(), 50, 70);
    }

    @Override
    public void mouseMoved(int x, int y) {
        polygons[0].updatePosition(x, y);
    }
    
}

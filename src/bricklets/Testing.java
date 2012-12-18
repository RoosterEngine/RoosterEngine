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
    private CircleEntity ball;
    private CircleShape ballShape;
    private AABBEntity box;
    private Polygon boxShape;
    private double a = 0;

    public Testing(GameController controller){
        super(controller, ContextType.GAME, true, false);
        init();
    }

    private void init(){
//        reset();
        entities.clear();
        controller.clearCollisions(this);
        ball = new CircleEntity(width / 2, 0, 10);
        ballShape = new CircleShape(ball.getX(), ball.getY(), ball.getRadius(), ball, Material.createCustomMaterial(0, 0.1));
        controller.addShapeToCollisionDetector(this, ballShape, 0);
        entities.add(ball);
        double boxWidth = 200;
        double boxHeight = 10;
        box = new AABBEntity(width / 2, height / 2, boxWidth, boxHeight);
        box.setMass(100000);
        entities.add(box);
        double halfWidth = boxWidth / 2;
        double halfHeight = boxHeight / 2;
        double[] xPoints = {-halfWidth, -halfWidth, halfWidth, halfWidth};
        double[] yPoints = {halfHeight, -halfHeight, -halfHeight, halfHeight};
        boxShape = new Polygon(box.getX(), box.getY(), xPoints, yPoints, box, Material.createCustomMaterial(0, 0.1));
        controller.addShapeToCollisionDetector(this, boxShape, 0);
        controller.setCollisionPair(this, 0, 0);
        setupInput();
        resetBall();
    }

    private void resetBall(){
        ball.setPosition(width / 2, 0);
        ball.setVelocity(0, 0.5);
    }

    private void addBall(){
        CircleEntity ball = new CircleEntity(width / 2, 0, 10);
        ball.setVelocity(0, 0.5);
        CircleShape ballShape = new CircleShape(ball.getX(), ball.getY(), ball.getRadius(), ball, Material.createCustomMaterial(0, 1));
        controller.addShapeToCollisionDetector(this, ballShape, 0);
        controller.addShapeToCollisionDetector(this, ballShape, 0);
        entities.add(ball);
    }

    @Override
    public void update(double elapsedTime) {
        rotateBox(0.003 * elapsedTime);
    }

    @Override
    public void draw(Graphics2D g) {
        g.setBackground(Color.BLACK);
        g.clearRect(0, 0, width, height);
        for (Entity entity : entities) {
            entity.draw(g);
        }
        boxShape.draw(g, Color.WHITE);
    }

    @Override
    public void handleCollision(Collision collision, double collisionsPerMilli) {
        Physics.performCollision(collision);

    }

    private void rotateBox(double angle){
        a += angle;
        Vector2D[] points = boxShape.getPoints();
        for(Vector2D point: points){
            double length = point.length();
            double currentAngle = Math.asin(point.getY() / length);
            double newAngle = angle + currentAngle;
            double cosAngle = Math.cos(angle);
            double sinAngle = Math.sin(angle);
            double newX = point.getX() * cosAngle - point.getY() * sinAngle;
            double newY = point.getX() * sinAngle + point.getY() * cosAngle;
            point.set(newX, newY);
        }
        boxShape.setup();
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
                init();
            }
        });
        controller.setContextBinding(contextType, InputCode.MOUSE_LEFT_BUTTON, Action.MOUSE_CLICK);
        bindAction(Action.MOUSE_CLICK, new ActionHandler() {

            @Override
            public void startAction(int inputCode) {
            }

            @Override
            public void stopAction(int inputCode) {
                addBall();
            }
        });
    }
}


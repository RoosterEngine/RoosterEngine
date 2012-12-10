package bricklets;

import gameengine.Context;
import gameengine.ContextType;
import gameengine.GameController;
import gameengine.input.Action;
import gameengine.input.ActionHandler;
import gameengine.input.InputCode;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: davidrusu
 * Date: 24/11/12
 * Time: 9:55 AM
 * To change this template use File | Settings | File Templates.
 */
public class BrickBreaker extends Context {
    private ArrayList<Entity>[] history = new ArrayList[50];
    private int historyPosition = 0, historySize = 0;

    private ArrayList<Entity> entities = new ArrayList<Entity>();
    private ArrayList<Brick> bricks = new ArrayList<Brick>();
    private AABBEntity paddle;
    private CircleEntity ball;
    private AABBEntity topBounds, rightBounds, leftBounds, bottomBounds;

    private Entity controlledEntity;
    private double thrust = 0.001;
    private double xThrustMultiplier = 0;
    private double yThrustMultiplier = 0;
    private Random rand = new Random(1);

    public BrickBreaker(GameController controller){
        super(controller, ContextType.GAME, false, true);
        init();
    }

    private void init(){
        Arrays.fill(history, null);
        historyPosition = 0;
        historySize = 0;

        entities.clear();
        rand.setSeed(1);
        controller.clearCollisions();

        controller.setCollisionPair(0, 0);


        paddle = new AABBEntity(width / 2, height - 150, 100, 20);
        paddle.setMass(Double.POSITIVE_INFINITY);
        paddle.setColor(Color.WHITE);
        entities.add(paddle);
        controller.addShapeToCollisionDetector(new AABBShape(paddle.getX(), paddle.getY(), paddle.getWidth(), paddle.getHeight(), paddle, Material.createCustomMaterial(0, 1)), 0);

        ball = new CircleEntity(width / 2, height / 2, 15);
        ball.setColor(Color.WHITE);
        ball.setMass(50);
        controller.addShapeToCollisionDetector(new CircleShape(ball.getX(), ball.getY(), ball.getRadius(), ball, Material.createCustomMaterial(0, 1)), 0);
        entities.add(ball);
        ball.setVelocity(0.1, 0.5);

        initBricks();
        initBounding();

        controlledEntity = paddle;

        takeHistorySnapshot();
        setupInput();
    }

    public void initBounding(){
        double borderThickness = 10;
        topBounds = new AABBEntity(width / 2, 0, width, borderThickness);
        topBounds.setColor(Color.GRAY);
        bottomBounds = new AABBEntity(width / 2, height - 50, width, borderThickness);
        bottomBounds.setColor(Color.GRAY);
        leftBounds = new AABBEntity(0, height / 2, borderThickness, height);
        leftBounds.setColor(Color.GRAY);
        rightBounds = new AABBEntity(width, height / 2, borderThickness, height);
        rightBounds.setColor(Color.gray);
        topBounds.setMass(Double.POSITIVE_INFINITY);
        bottomBounds.setMass(Double.POSITIVE_INFINITY);
        leftBounds.setMass(Double.POSITIVE_INFINITY);
        rightBounds.setMass(Double.POSITIVE_INFINITY);
        controller.setCollisionPair(0, 1);
        Material material = Material.createCustomMaterial(0, 1);
        controller.addShapeToCollisionDetector(new AABBShape(
                topBounds.getX(), topBounds.getY(), topBounds.getWidth(), topBounds.getHeight(), topBounds, material), 1);
        controller.addShapeToCollisionDetector(new AABBShape(
                bottomBounds.getX(), bottomBounds.getY(), bottomBounds.getWidth(), bottomBounds.getHeight(), bottomBounds, material), 1);
        controller.addShapeToCollisionDetector(new AABBShape(
                leftBounds.getX(), leftBounds.getY(), leftBounds.getWidth(), leftBounds.getHeight(), leftBounds, material), 1);
        controller.addShapeToCollisionDetector(new AABBShape(
                rightBounds.getX(), rightBounds.getY(), rightBounds.getWidth(), rightBounds.getHeight(), rightBounds, material), 1);
        entities.add(topBounds);
        entities.add(bottomBounds);
        entities.add(leftBounds);
        entities.add(rightBounds);
    }

    private void initBricks(){
        bricks.clear();
        int rows = 5;
        int columns = 16;
        double padding = ball.radius / 2;
        double borderPadding = ball.radius * 4;
        double yOffset = borderPadding * 1.75;
        double brickWidth = (width - borderPadding * 2 - (columns + 1) * padding) / columns;
        double brickHeight = brickWidth / 2.75;
        Material material = Material.createCustomMaterial(0, 1);
        for(int x = 0; x < columns; x++){
            for(int y = 0; y < rows; y++){
                double xPos = padding * (1 + x) + brickWidth * x + brickWidth / 2 + borderPadding;
                double yPos = padding * (1 + y)  + brickHeight * y + brickHeight / 2 + yOffset;
                Brick brick = new Brick(xPos, yPos, brickWidth, brickHeight);
                brick.setMass(1000);
                bricks.add(brick);
                entities.add(brick);
                controller.addShapeToCollisionDetector(new AABBShape
                        (brick.getX(), brick.getY(), brickWidth, brickHeight, brick, material), 0);
            }
        }
    }

    @Override
    public void update(double elapsedTime) {
        for(Entity entity: entities){
            entity.update(elapsedTime);
        }
        for (int i = 0; i < bricks.size(); i++){
            Brick brick = bricks.get(i);
            if(!brick.isAlive()){
                bricks.remove(i);
                entities.remove(brick);
                controller.removeChildrenFromCollisionDetector(brick);
            }
        }
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(new Color(30,110, 50));
        g.fillRect(0, 0, width, height);
        for(Entity entity: entities) {
            entity.draw(g);
        }

        drawText(g);
    }

    private void drawText(Graphics2D g) {
        g.setColor(Color.red);
        g.drawString("ball speed " + Math.sqrt(ball.getDX() * ball.getDX() + ball.getDY() * ball.getDY()), 25, 50);
        g.drawString("controlledEntity speed " + Math.sqrt(controlledEntity.getDX() * controlledEntity.getDX() + controlledEntity.getDY() * controlledEntity.getDY()), 25, 70);
        g.drawString("controlledEntity pos " + (int)controlledEntity.getX() + ", " + (int)controlledEntity.getY(), 25, 90);
    }

    @Override
    public void handleCollision(Collision collision, double collisionsPerMilli) {
        Physics.performCollision(collision);

        double damage = 50;
        if (collision.getA().getParentEntity() instanceof Brick && collision.getB().getParentEntity() == ball){
            ((Brick) collision.getA().getParentEntity()).doDamage(damage);
        } else if (collision.getB().getParentEntity() instanceof Brick && collision.getA().getParentEntity() == ball){
            ((Brick) collision.getB().getParentEntity()).doDamage(damage);
        }

        takeHistorySnapshot();
    }

    private void takeHistorySnapshot(){
        ArrayList<Entity> snapshot = new ArrayList<Entity>(entities.size());
        for(Entity entity: entities) {
            snapshot.add(new CircleEntity(entity, 1));
        }
        history[historyPosition] = snapshot;
        if(historySize < history.length){
            historySize ++;
        }
        historyPosition = (historyPosition + 1) % history.length;
    }

    private void revertToPrevHistorySnapshot(){
        if(historySize == 0){
            return;
        }
        if(historyPosition == 0){
            historyPosition = history.length - 1;
        }else{
            historyPosition--;
        }
        ArrayList<Entity> snapshot = history[historyPosition];
        for(int i = 0; i < entities.size(); i++) {
            Entity historyEntity = snapshot.get(i);
            Entity entity = entities.get(i);
            entity.setPosition(historyEntity.getX(), historyEntity.getY());
            entity.setVelocity(historyEntity.getDX(), historyEntity.getDY());
            entity.setAcceleration(historyEntity.getDDX(), historyEntity.getDDY());
        }
        historySize--;
    }

    @Override
    public void mouseMoved(double x, double y, double velocityX, double velocityY) {
        controlledEntity.setVelocityX(velocityX);
//        controlledEntity.addVelocityY(velocityY / 50);
    }

    private void setupInput(){
        controller.setContextBinding(contextType, InputCode.KEY_P, Action.PAUSE_GAME);
        bindAction(Action.PAUSE_GAME, new ActionHandler() {
            @Override
            public void startAction(int inputCode) {
            }

            @Override
            public void stopAction(int inputCode) {
                togglePause();
            }
        });

        controller.setContextBinding(contextType, InputCode.KEY_ESCAPE, Action.EXIT_GAME);
        bindAction(Action.EXIT_GAME, new ActionHandler() {
            @Override
            public void startAction(int inputCode) {
            }

            @Override
            public void stopAction(int inputCode) {
                init();
                controller.exitContext();
            }
        });

        controller.setContextBinding(contextType, InputCode.KEY_UP, Action.GAME_UP);
        bindAction(Action.GAME_UP, new ActionHandler() {
            @Override
            public void startAction(int inputCode) {
            }

            @Override
            public void stopAction(int inputCode) {
            }
        });

        controller.setContextBinding(contextType, InputCode.KEY_DOWN, Action.GAME_DOWN);
        bindAction(Action.GAME_DOWN, new ActionHandler() {
            @Override
            public void startAction(int inputCode) {
            }

            @Override
            public void stopAction(int inputCode) {
            }
        });

        controller.setContextBinding(contextType, InputCode.KEY_LEFT, Action.GAME_LEFT);
        bindAction(Action.GAME_LEFT, new ActionHandler() {
            @Override
            public void startAction(int inputCode) {
            }

            @Override
            public void stopAction(int inputCode) {
            }
        });

        controller.setContextBinding(contextType, InputCode.KEY_RIGHT, Action.GAME_RIGHT);
        bindAction(Action.GAME_RIGHT, new ActionHandler() {
            @Override
            public void startAction(int inputCode) {
            }

            @Override
            public void stopAction(int inputCode) {
            }
        });

        controller.setContextBinding(contextType, InputCode.KEY_Q, Action.GAME_UNDO);
        bindAction(Action.GAME_UNDO, new ActionHandler() {
            @Override
            public void startAction(int inputCode) {
            }

            @Override
            public void stopAction(int inputCode) {
                revertToPrevHistorySnapshot();
            }
        });
    }
}

package bricklets;

import gameengine.Context;
import gameengine.ContextType;
import gameengine.GameController;
import gameengine.effects.*;
import gameengine.effects.motions.*;
import gameengine.input.Action;
import gameengine.input.ActionHandler;
import gameengine.input.InputCode;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: davidrusu
 * Date: 24/11/12
 * Time: 9:55 AM
 * To change this template use File | Settings | File Templates.
 */
public class BrickBreaker extends Context {
    private ArrayList<Brick> bricks = new ArrayList<Brick>();
    private Paddle paddle;
    private ArrayList<CircleEntity> balls = new ArrayList<CircleEntity>();
    private AABBEntity topBounds, rightBounds, leftBounds, bottomBounds;
    private Group gravityGroup;

    private Entity controlledEntity;
    private double thrust = 0.001;
    private double xThrustMultiplier = 0;
    private double yThrustMultiplier = 0;
    private Random rand = new Random(1);
    private double initialPaddleY = height - 200;
    private double ballRadius = 15;
    private double ballMass = 100;
    private int lives = 100;

    public BrickBreaker(GameController controller){
        super(controller, ContextType.GAME, false, true);
        init();
    }

    private void init(){
        rand.setSeed(1);
        controller.clearCollisions(this);
        lives = 100;
        reset();

        controller.setCollisionPair(this, 0, 0);
        gravityGroup = new Group();
        gravityGroup.addEnvironmentMotionGenerator(new Gravity(0, 0.001));
        groups.add(gravityGroup);

        Material paddleMaterial = Material.createCustomMaterial(0, 2);
        paddle = new Paddle(width / 2, initialPaddleY, 300, 50, paddleMaterial);
        paddle.setMass(1000);
        entities.add(paddle);
        gravityGroup.addEntity(paddle);
        controller.addShapeToCollisionDetector(this, paddle.getPolygon(), 0);
//        controller.addShapeToCollisionDetector(new AABBShape(paddle.getX(), paddle.getY(), paddle.getWidth(), paddle.getHeight(), paddle, Material.createCustomMaterial(0, 2)), 0);

        CircleEntity ball = new CircleEntity(width / 2, height / 2, ballRadius);
        ball.setMass(ballMass);
        ball.setVelocity(0.1, 0.5);
        gravityGroup.addEntity(ball);
        controller.addShapeToCollisionDetector(this, new CircleShape(ball.getX(), ball.getY(), ball.getRadius(), ball, Material.createCustomMaterial(0, 1)), 0);
        entities.add(ball);
        balls.add(ball);

        initBricks();
        initBounding();

        controlledEntity = paddle;
        controlledEntity.setMotion(new MotionCompositor(
                new MouseMotion(),
                new VerticalAttractMotion(paddle.getY(), 0.001, 1, controlledEntity.getMass())));

        setupInput();
    }

    public void initBounding(){
        double borderThickness = 10;
        topBounds = new AABBEntity(width / 2, 0, width, borderThickness);
        bottomBounds = new AABBEntity(width / 2, height - 50, width, borderThickness);
        leftBounds = new AABBEntity(0, height / 2, borderThickness, height);
        rightBounds = new AABBEntity(width, height / 2, borderThickness, height);
        topBounds.setMass(Double.POSITIVE_INFINITY);
        bottomBounds.setMass(Double.POSITIVE_INFINITY);
        leftBounds.setMass(Double.POSITIVE_INFINITY);
        rightBounds.setMass(Double.POSITIVE_INFINITY);
        controller.setCollisionPair(this, 0, 1);
        Material material = Material.createCustomMaterial(0, 0.5);
        controller.addShapeToCollisionDetector(this, new AABBShape(
                topBounds.getX(), topBounds.getY(), topBounds.getWidth(), topBounds.getHeight(), topBounds, material), 1);
        controller.addShapeToCollisionDetector(this, new AABBShape(
                bottomBounds.getX(), bottomBounds.getY(), bottomBounds.getWidth(), bottomBounds.getHeight(), bottomBounds, material), 1);
        material = Material.createCustomMaterial(0, 1);
        controller.addShapeToCollisionDetector(this, new AABBShape(
                leftBounds.getX(), leftBounds.getY(), leftBounds.getWidth(), leftBounds.getHeight(), leftBounds, material), 1);
        controller.addShapeToCollisionDetector(this, new AABBShape(
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
        double padding = ballRadius / 2;
        double borderPadding = ballRadius * 4;
        double yOffset = borderPadding * 1.75;
        double brickWidth = (width - borderPadding * 2 - (columns + 1) * padding) / columns;
        double brickHeight = brickWidth / 2.75;
        Material material = Material.createCustomMaterial(0, 1);
        for(int x = 0; x < columns; x++){
            for(int y = 0; y < rows; y++){
                double xPos = padding * (1 + x) + brickWidth * x + brickWidth / 2 + borderPadding;
                double yPos = padding * (1 + y)  + brickHeight * y + brickHeight / 2 + yOffset;
                Brick brick = new Brick(xPos, yPos, brickWidth, brickHeight);
                brick.setMass(10);
                brick.setMotion(new AttractMotion(xPos,  yPos, 0.001, 0.3, brick.getMass()));
                bricks.add(brick);
                entities.add(brick);
                gravityGroup.addEntity(brick);
                controller.addShapeToCollisionDetector(this, new AABBShape
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
                controller.removeChildrenFromCollisionDetector(this, brick);
            }
        }
        if(bricks.size() == 0){
            init();
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
        g.drawString("Lives " + lives, 25, 70);
    }

    @Override
    public void handleCollision(Collision collision, double collisionsPerMilli) {
        Physics.performCollision(collision);

        double damage = 10;
        Entity a = collision.getA().getParentEntity();
        Entity b = collision.getB().getParentEntity();
        boolean isABall = balls.contains(a);
        boolean isBBall = balls.contains(b);
        if (a instanceof Brick && isBBall){
            ((Brick) a).doDamage(damage);
        } else if (b instanceof Brick && isABall){
            ((Brick) b).doDamage(damage);
        } else if (a == bottomBounds && isBBall || isABall && b == bottomBounds){
            if(lives > 0){
                lives--;
                CircleEntity ball;
                if(isBBall){
                    ball = (CircleEntity)b;
                } else{
                    ball = (CircleEntity)a;
                }
                controller.removeChildrenFromCollisionDetector(this, ball);
                entities.remove(ball);
                balls.remove(ball);


            } else {
                init();
            }
        }
    }

    private void setupInput(){
        controller.setContextBinding(contextType, InputCode.MOUSE_LEFT_BUTTON, Action.MOUSE_CLICK);
        bindAction(Action.MOUSE_CLICK, new ActionHandler() {
            @Override
            public void startAction(int inputCode) {
            }

            @Override
            public void stopAction(int inputCode) {
                CircleEntity newBall = new CircleEntity(paddle.getX(), paddle.getY() - paddle.getHeight() / 2 - 5, ballRadius);
                newBall.setVelocity(0, -1);
                newBall.setMass(ballMass);
                entities.add(newBall);
                balls.add(newBall);
                gravityGroup.addEntity(newBall);
                controller.addShapeToCollisionDetector(BrickBreaker.this, newBall.getShape(Material.createCustomMaterial(0, 1)), 0);
            }
        });

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
            }
        });
    }
}

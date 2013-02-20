package bricklets;

import gameengine.GameController;
import gameengine.collisiondetection.Collision;
import gameengine.collisiondetection.CollisionType;
import gameengine.collisiondetection.shapes.Shape;
import gameengine.context.Context;
import gameengine.context.ContextType;
import gameengine.entities.BoxEntity;
import gameengine.entities.CircleEntity;
import gameengine.entities.Entity;
import gameengine.input.Action;
import gameengine.input.ActionHandler;
import gameengine.input.InputCode;
import gameengine.motion.motions.AttractMotion;
import gameengine.motion.motions.MotionCompositor;
import gameengine.motion.motions.MouseMotion;
import gameengine.motion.motions.VerticalAttractMotion;
import gameengine.physics.Material;
import gameengine.physics.Physics;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * User: davidrusu
 * Date: 24/11/12
 * Time: 9:55 AM
 */
public class BrickBreaker extends Context implements ActionHandler {
    private ArrayList<Brick> bricks = new ArrayList<>();
    private Paddle paddle;
    private ArrayList<CircleEntity> balls = new ArrayList<>();
    private BoxEntity topBounds, rightBounds, leftBounds, bottomBounds;

    private Entity controlledEntity;
    private double thrust = 0.001;
    private double xThrustMultiplier = 0;
    private double yThrustMultiplier = 0;
    private Random rand = new Random(1);
    private double initialPaddleY = height - 200;
    private double ballRadius = 15;
    private double ballMass = 50;
    private int lives = 100;
    private double scale = 1;
    private Color bgColor = Color.black;

    public BrickBreaker(GameController controller) {
        super(controller, ContextType.GAME);
        init();
    }

    private void init() {
        rand.setSeed(1);
        controller.clearCollisions(this);
        lives = 100;
        reset();

        controller.setCollisionPair(
                this, CollisionType.DEFAULT, CollisionType.DEFAULT);

        paddle = new Paddle(width / 2, initialPaddleY, 300, 50);
        Shape paddleShape = paddle.getShape();
        paddleShape.setMass(100);
        paddleShape.setMaterial(Material.createMaterial(0, 1));
        paddleShape.setCollisionType(CollisionType.DEFAULT);
        controller.addEntityToCollisionDetector(this, paddle);

        entities.add(paddle);


//        CircleEntity ball = new CircleEntity(width / 2, height / 2, ballRadius);
//        Shape ballShape = ball.getShape();
//        ballShape.setMass(ballMass);
//        ballShape.setCollisionType(CollisionType.DEFAULT);
//        ball.setVelocity(0.1, 0.5);
//        controller.addEntityToCollisionDetector(this, ball);
//        balls.add(ball);
//
//        entities.add(ball);

        initBricks();
        initBounding();

        controlledEntity = paddle;
        controlledEntity.setMotion(new MotionCompositor(new MouseMotion(), new VerticalAttractMotion(paddle.getY(), 0.01, 0.3, controlledEntity.getShape().getMass())));

        setupInput();
    }

    public void initBounding() {
        double borderThickness = 10;
        topBounds = new BoxEntity(width / 2, 0, width, borderThickness);
        bottomBounds = new BoxEntity(width / 2, height - 50, width, borderThickness);
        leftBounds = new BoxEntity(0, height / 2, borderThickness, height);
        rightBounds = new BoxEntity(width, height / 2, borderThickness, height);

        topBounds.getShape().setMass(Double.POSITIVE_INFINITY);
        bottomBounds.getShape().setMass(Double.POSITIVE_INFINITY);
        leftBounds.getShape().setMass(Double.POSITIVE_INFINITY);
        rightBounds.getShape().setMass(Double.POSITIVE_INFINITY);

        topBounds.getShape().setCollisionType(CollisionType.WALL);
        bottomBounds.getShape().setCollisionType(CollisionType.WALL);
        leftBounds.getShape().setCollisionType(CollisionType.WALL);
        rightBounds.getShape().setCollisionType(CollisionType.WALL);
        controller.setCollisionPair(this, CollisionType.DEFAULT, CollisionType.WALL);

        Material material = Material.createMaterial(0, 1);
        topBounds.getShape().setMaterial(material);
        bottomBounds.getShape().setMaterial(material);
        leftBounds.getShape().setMaterial(material);
        rightBounds.getShape().setMaterial(material);

        controller.addEntityToCollisionDetector(this, topBounds);
        controller.addEntityToCollisionDetector(this, bottomBounds);
        controller.addEntityToCollisionDetector(this, leftBounds);
        controller.addEntityToCollisionDetector(this, rightBounds);

        entities.add(topBounds);
        entities.add(bottomBounds);
        entities.add(leftBounds);
        entities.add(rightBounds);
    }

    private void initBricks() {
        bricks.clear();
        int rows = 5;
        int columns = 20;
        double padding = ballRadius / 2;
        double borderPadding = 100;
        double yOffset = borderPadding * 1.75;
        double brickWidth = (width - borderPadding * 2 - (columns + 1) * padding) / columns;
        double brickHeight = brickWidth / 2.75;
        Material material = Material.createMaterial(0, 1);
        for (int x = 0; x < columns; x++) {
            for (int y = 0; y < rows; y++) {
                double xPos = padding * (1 + x) + brickWidth * x + brickWidth / 2 + borderPadding;
                double yPos = padding * (1 + y) + brickHeight * y + brickHeight / 2 + yOffset;
                Brick brick = new Brick(xPos, yPos, brickWidth, brickHeight);
                brick.getShape().setMass(20);
                brick.getShape().setCollisionType(CollisionType.DEFAULT);
                brick.setMotion(new AttractMotion(xPos, yPos, 0.001, 0.3, brick.getShape().getMass()));
                bricks.add(brick);
                entities.add(brick);
                controller.addEntityToCollisionDetector(this, brick);
            }
        }
    }

    @Override
    public void update(double elapsedTime) {
        for (int i = 0; i < bricks.size(); i++) {
            Brick brick = bricks.get(i);
            if (brick.isDead()) {
                bricks.remove(i);
                entities.remove(brick);
                brick.removeFromWorld();
            }
        }
//        if (bricks.size() == 0) {
//            init();
//        }
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(bgColor);
        g.fillRect(0, 0, width, height);

        double shiftX = width * (1 - scale) / 2;
        double shiftY = height * (1 - scale) / 2;
        g.translate((int) shiftX, (int) shiftY);
        g.scale(scale, scale);

//        controller.drawPartitions(g, bgColor);
        for (Entity entity : entities) {
            entity.draw(g);
        }
//        for (Entity entity : entities) {
//            entity.drawLineToPartition(g, Color.RED);
//        }

        g.scale(1 / scale, 1 / scale);
        drawText(g);
    }

    private void drawText(Graphics2D g) {
        g.setColor(Color.red);
        g.drawString("Lives " + lives, 25, 70);
    }

    @Override
    public void handleCollision(Collision collision) {
        Physics.performCollision(collision);
//        bgColor = new Color(
//                (int)(Math.random() * 255), (int)(Math.random() * 255), (int)(Math.random() * 255));
        double damage = 10;
        Entity a = collision.getA();
        Entity b = collision.getB();
        boolean isABall = balls.contains(a);
        boolean isBBall = balls.contains(b);
        if (a instanceof Brick && isBBall) {
            ((Brick) a).doDamage(damage);
        } else if (b instanceof Brick && isABall) {
            ((Brick) b).doDamage(damage);
        } else if (a == bottomBounds && isBBall || isABall && b == bottomBounds) {
            if (lives > 0) {
                lives--;
                CircleEntity ball;
                if (isBBall) {
                    ball = (CircleEntity) b;
                } else {
                    ball = (CircleEntity) a;
                }
                ball.removeFromWorld();
                entities.remove(ball);
                balls.remove(ball);


            } else {
                init();
            }
        }
    }

    private void setupInput() {
        controller.setContextBinding(contextType, InputCode.MOUSE_LEFT_BUTTON, Action.MOUSE_CLICK);
        controller.setContextBinding(contextType, InputCode.KEY_P, Action.PAUSE_GAME);
        controller.setContextBinding(contextType, InputCode.KEY_ESCAPE, Action.EXIT_GAME);
        controller.setContextBinding(contextType, InputCode.KEY_UP, Action.GAME_UP);
        controller.setContextBinding(contextType, InputCode.KEY_DOWN, Action.GAME_DOWN);
        controller.setContextBinding(contextType, InputCode.KEY_LEFT, Action.GAME_LEFT);
        controller.setContextBinding(contextType, InputCode.KEY_RIGHT, Action.GAME_RIGHT);
        controller.setContextBinding(contextType, InputCode.KEY_Q, Action.GAME_UNDO);
        controller.setContextBinding(contextType, InputCode.MOUSE_WHEEL_UP, Action.ZOOM_OUT);
        controller.setContextBinding(contextType, InputCode.MOUSE_WHEEL_DOWN, Action.ZOOM_IN);
    }

    @Override
    public void startAction(Action action, int inputCode) {
        double scaleAmount = 0.01;
        switch (action) {
            case MOUSE_CLICK:
                break;
            case PAUSE_GAME:
                break;
            case EXIT_GAME:
                break;
            case GAME_UP:
                break;
            case GAME_DOWN:
                break;
            case GAME_LEFT:
                break;
            case GAME_RIGHT:
                break;
            case GAME_UNDO:
                break;
            case ZOOM_IN:
                scale *= 1 - scaleAmount;
                break;
            case ZOOM_OUT:
                scale *= 1 + scaleAmount;
        }
    }

    @Override
    public void stopAction(Action action, int inputCode) {
        switch (action) {
            case MOUSE_CLICK:
                CircleEntity newBall = new CircleEntity(paddle.getX(), paddle.getY() - paddle.getHeight() / 2 - 5, ballRadius);
                newBall.setVelocity(paddle.getDX() * 5, -1);
                newBall.getShape().setMass(ballMass);
                newBall.getShape().setCollisionType(CollisionType.DEFAULT);
                entities.add(newBall);
                balls.add(newBall);
                controller.addEntityToCollisionDetector(this, newBall);
                break;
            case PAUSE_GAME:
                togglePause();
                break;
            case EXIT_GAME:
//                init();
                controller.exitContext();
                break;
            case GAME_UP:
                break;
            case GAME_DOWN:
                break;
            case GAME_LEFT:
                break;
            case GAME_RIGHT:
                break;
            case GAME_UNDO:
                break;
        }
    }
}

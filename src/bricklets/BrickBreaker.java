package bricklets;

import gameengine.GameController;
import gameengine.collisiondetection.Collision;
import gameengine.collisiondetection.CollisionType;
import gameengine.context.Context;
import gameengine.context.ContextType;
import gameengine.entities.BoxEntity;
import gameengine.entities.CircleEntity;
import gameengine.entities.Entity;
import gameengine.input.Action;
import gameengine.input.ActionHandler;
import gameengine.input.InputCode;
import gameengine.motion.environmentmotions.VelocityEnforcerWorldEffect;
import gameengine.motion.motions.AttractMotion;
import gameengine.motion.motions.MotionCompositor;
import gameengine.motion.motions.MouseMotion;
import gameengine.motion.motions.VerticalAttractMotion;
import gameengine.physics.Material;
import gameengine.physics.Physics;

import java.awt.*;
import java.util.Random;

/**
 * User: davidrusu
 * Date: 24/11/12
 * Time: 9:55 AM
 */
public class BrickBreaker extends Context implements ActionHandler {
    private int brickCount = 0;
    private Paddle paddle;
    private BoxEntity topBounds, rightBounds, leftBounds, bottomBounds;

    private Entity controlledEntity;
    private double thrust = 0.001;
    private double xThrustMultiplier = 0;
    private double yThrustMultiplier = 0;
    private Random rand = new Random(1);
    private double initialPaddleY = height - 200;
    private double ballRadius = 5;
    private double ballMass = 50;
    private int lives = 100;
    private double scale = 1;
    private Color bgColor = Color.black;
    private double currentTime = 0, lastTime = 0;
    private boolean shooting = false;
    private Material ballMaterial = Material.createMaterial(0, 1, 1);

    public BrickBreaker(GameController controller) {
        super(controller, ContextType.GAME);
        init();
    }

    private void init() {
        rand.setSeed(1);
        world.clearCollisions();
        lives = 100;
        reset();

        world.setCollisionGroup(CollisionType.DEFAULT, CollisionType.DEFAULT);
        world.setCollisionGroup(CollisionType.DEFAULT, CollisionType.BALL);
        world.setCollisionGroup(CollisionType.DEFAULT, CollisionType.WALL);
        world.setCollisionGroup(CollisionType.BALL, CollisionType.WALL);
        world.setCollisionGroup(CollisionType.BALL, CollisionType.BALL);

        VelocityEnforcerWorldEffect velocityEnforcer = new VelocityEnforcerWorldEffect(0.5, 0.8);
        velocityEnforcer.addCollisionType(CollisionType.BALL);
        world.addEnvironmentMotion(velocityEnforcer);

        paddle = new Paddle(width / 2, initialPaddleY, 300, 50);
        paddle.setMass(1000);
        paddle.setMaterial(Material.createMaterial(0, 1, 1));
        paddle.setCollisionType(CollisionType.DEFAULT);
        world.addEntity(paddle);

        initBricks();
        initBounding();

        controlledEntity = paddle;
        controlledEntity.setMotion(new MotionCompositor(new MouseMotion(),
                new VerticalAttractMotion(paddle.getY(), 0.01, 0.3, controlledEntity.getMass())));

        setupInput();
    }

    public void initBounding() {
        double borderThickness = 10;
        topBounds = new BoxEntity(width / 2, 0, width, borderThickness);
        bottomBounds = new BoxEntity(width / 2, height - 50, width, borderThickness);
        leftBounds = new BoxEntity(0, height / 2, borderThickness, height);
        rightBounds = new BoxEntity(width, height / 2, borderThickness, height);

        topBounds.setMass(Double.POSITIVE_INFINITY);
        bottomBounds.setMass(Double.POSITIVE_INFINITY);
        leftBounds.setMass(Double.POSITIVE_INFINITY);
        rightBounds.setMass(Double.POSITIVE_INFINITY);

        topBounds.setCollisionType(CollisionType.WALL);
        bottomBounds.setCollisionType(CollisionType.WALL);
        leftBounds.setCollisionType(CollisionType.WALL);
        rightBounds.setCollisionType(CollisionType.WALL);

        Material material = Material.createMaterial(0, 1, 1);
        topBounds.setMaterial(material);
        bottomBounds.setMaterial(material);
        leftBounds.setMaterial(material);
        rightBounds.setMaterial(material);

        world.addEntity(topBounds);
        world.addEntity(bottomBounds);
        world.addEntity(leftBounds);
        world.addEntity(rightBounds);
    }

    private void initBricks() {
        brickCount = 0;
        int rows = 10;
        int columns = 70;
        double padding = ballRadius * 2.5;
        double borderPadding = ballRadius * 5;
        double yOffset = borderPadding * 1.75;
        double brickWidth = (width - borderPadding * 2 - (columns + 1) * padding) / columns;
        double brickHeight = brickWidth;
        for (int x = 0; x < columns; x++) {
            for (int y = 0; y < rows; y++) {
                double xPos = padding * (1 + x) + brickWidth * x + brickWidth / 2 + borderPadding;
                double yPos = padding * (1 + y) + brickHeight * y + brickHeight / 2 + yOffset;
                Brick brick = new Brick(xPos, yPos, brickWidth, brickHeight);
                brick.setMass(20);
                brick.setCollisionType(CollisionType.DEFAULT);
                brick.setMotion(new AttractMotion(xPos, yPos, 0.001, 0.3, brick.getMass()));
                brickCount++;
                world.addEntity(brick);
            }
        }
    }

    @Override
    public void update(double elapsedTime) {
        if (shooting) {
            currentTime += elapsedTime;
            double timeBetweenBalls = 5;
            if (lastTime + timeBetweenBalls <= currentTime) {

                for (int i = 0; i < 3; i++) {
                    addBall(controlledEntity.getX(), controlledEntity.getY() - controlledEntity.getHeight(),
                            ballRadius, 10);
                }
//                addBall(pointer.getX() + ballRadius * 2 + 2, pointer.getY(), ballRadius);
//                addBall(pointer.getX() - ballRadius * 2- 2, pointer.getY(), ballRadius);
                lastTime = currentTime;// - (currentTime - lastTime + timeBetweenBalls);
            }
        }
        if (brickCount == 0) {
            init();
        }
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(bgColor);
        g.fillRect(0, 0, width, height);
        world.draw(this, g);
        drawText(g);
    }

    private void drawText(Graphics2D g) {
        g.setColor(Color.red);
        g.drawString("fps " + controller.getFrameRate(), 25, 50);
        g.drawString("Lives " + lives, 25, 70);
        g.drawString("entities " + world.getEntityCount(), 25, 90);
        g.drawString("bricks " + brickCount, 25, 110);
    }

    @Override
    public void handleCollision(Collision collision) {
        Physics.performCollision(collision);
//        bgColor = new Color(
//                (int)(Math.random() * 255), (int)(Math.random() * 255), (int)(Math.random() * 255));
        double damage = 30;
        Entity a = collision.getA();
        Entity b = collision.getB();
        boolean isABall = a instanceof CircleEntity;//balls.contains(a);
        boolean isBBall = b instanceof CircleEntity;//balls.contains(b);
        if (a instanceof Brick && isBBall) {
//            damage *= b.getMass();
            damageBrick((Brick) a, damage);
        } else if (b instanceof Brick && isABall) {
//            damage *= a.getMass();
            damageBrick((Brick) b, damage);
        } else if (a == bottomBounds && isBBall || isABall && b == bottomBounds) {
//            if (lives > 0) {
            lives--;
            CircleEntity ball;
            if (isBBall) {
                ball = (CircleEntity) b;
            } else {
                ball = (CircleEntity) a;
            }
            ball.removeFromWorld();
//            }
        }
    }

    private void damageBrick(Brick brick, double damage) {
        brick.doDamage(damage);
        if (brick.isDead()) {
            brick.removeFromWorld();
            brickCount--;
        }
    }

    private void setupInput() {
        controller.setContextBinding(contextType, InputCode.MOUSE_LEFT_BUTTON, Action.MOUSE_CLICK);
        controller.setContextBinding(contextType, InputCode.MOUSE_RIGHT_BUTTON, Action.RIGHT_CLICK);
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
                shooting = true;
                break;
            case RIGHT_CLICK:

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
                shooting = false;
//                addBall(paddle.getX(), paddle.getY() - paddle.getHeight() / 2 - 5, ballRadius, ballMass);
                break;
            case RIGHT_CLICK:

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

    private void addBall(double x, double y, double radius, double mass) {
        double speed = 1.5;
        CircleEntity entity = new CircleEntity(x, y, radius);
//        entity.setColor(new Color((float)Math.random(), (float)Math.random(), (float)Math.random()));
        double spread = (Math.sin(currentTime / 1000) + 1.5) * 0.2;
        entity.setVelocity((Math.random() - 0.5) * spread, -1);
        entity.setMass(mass);
//        entity.setVelocity((Math.random() - 0.5) * speed, (Math.random() - 0.5) * speed);
        entity.setMaterial(ballMaterial);
        entity.setCollisionType(CollisionType.BALL);
        world.addEntity(entity);
    }
}

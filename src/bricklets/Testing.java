package bricklets;

import gameengine.GameController;
import gameengine.collisiondetection.Collision;
import gameengine.collisiondetection.CollisionType;
import gameengine.context.Context;
import gameengine.context.ContextType;
import gameengine.entities.BoxEntity;
import gameengine.entities.CircleEntity;
import gameengine.entities.Entity;
import gameengine.entities.Pointer;
import gameengine.graphics.OvalGraphic;
import gameengine.input.Action;
import gameengine.input.ActionHandler;
import gameengine.input.InputCode;
import gameengine.physics.Material;
import gameengine.physics.Physics;

import java.awt.*;
import java.util.Random;

public class Testing extends Context implements ActionHandler {
    private Pointer pointer;
    private Random rand = new Random(0);
    private boolean attracting = false;
    private boolean panning = false;
    private Material ballMaterial = Material.createMaterial(0, 1, 1);
    private boolean shooting = false;
    private BoxEntity topBounds;
    private BoxEntity bottomBounds;
    private BoxEntity leftBounds;
    private BoxEntity rightBounds;

    public Testing(GameController controller) {
        super(controller, ContextType.GAME);
        init();
    }

    public void init() {
        // TODO each context should have its own world, also clearWorld() should not clear the collision pairs
        world.clearCollisions();
        world.setCollisionGroup(CollisionType.BALL, CollisionType.BALL);
        world.setCollisionGroup(CollisionType.BALL, CollisionType.DEFAULT);
        world.setCollisionGroup(CollisionType.WALL, CollisionType.DEFAULT);

//        GravityWorldEffect gravity = new GravityWorldEffect(0.001);
//        gravity.addCollisionType(CollisionType.BALL);
//        world.addEnvironmentMotion(gravity);
//
//        VelocityEnforcerWorldEffect velocityEnforcer = new VelocityEnforcerWorldEffect(0.3, 0.9);
//        velocityEnforcer.addCollisionType(CollisionType.BALL);
//        world.addEnvironmentMotion(velocityEnforcer);

        pointer = new Pointer(new OvalGraphic(15, 15, Color.RED), width / 2, height / 2);
//        pointer.getShape().setMass(1);
        world.addEntity(pointer);
//        double xOffset = 500;
//        double yOffset = 500;
//        double ballRadius = 10;
//        for (int i = 0; i < 10; i++) {
////            double ballX = xOffset + i * ballRadius * 2;
////            CircleEntity circle = new CircleEntity(ballX, yOffset, ballRadius);
////            circle.getShape().setMaterial(ballMaterial);
////            circle.getShape().setCollisionType(CollisionType.BALL);
//////            if (i == 5) {
//////                circle.getShape().setMass(10);
//////                circle.setColor(Color.DARK_GRAY);
//////            }
////            SpringMotion spring = new SpringMotion(ballX, yOffset, 200, 0.001, 0.2, 1);
////            circle.setMotion(spring);
////            controller.addEntityToCollisionDetector(this, circle);
////            entities.add(circle);
//        }
        initBounding();
        setupInput();
    }

    public void initBounding() {
        double borderThickness = 10;
        topBounds = new BoxEntity(width / 2, 0, width, borderThickness);
        bottomBounds = new BoxEntity(width / 2, height, width, borderThickness);
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
        world.setCollisionGroup(CollisionType.BALL, CollisionType.WALL);

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

    @Override
    public void update(double elapsedTime) {
        if (panning) {
            viewPort.setPosition(pointer.getX() - width / 2, pointer.getY() - height / 2);
        }

//        if (shooting) {
//            currentTime += elapsedTime;
//            double ballRadius = 3;
//            double timeBetweenBalls = 2;
//            if (lastTime + timeBetweenBalls <= currentTime) {
//
//                for (int i = 0; i < 2; i++) {
//                    addBall(pointer.getX(), pointer.getY(), ballRadius);
//                }
////                addBall(pointer.getX() + ballRadius * 2 + 2, pointer.getY(), ballRadius);
////                addBall(pointer.getX() - ballRadius * 2- 2, pointer.getY(), ballRadius);
//                lastTime = currentTime;// - (currentTime - lastTime + timeBetweenBalls);
//            }
//        }
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, width, height);
        world.draw(this, g);
        drawStats(g);
    }

    private void drawStats(Graphics2D g) {
        g.setColor(Color.red);
        g.drawString("fps: " + controller.getFrameRate(), 25, 25);
        g.drawString("ups: " + controller.getUpdateRate(), 25, 50);
        g.drawString("entities: " + world.getEntityCount(), 25, 75);
        g.drawString("scale: " + viewPort.getScale(), 25, 100);
    }

    @Override
    public void handleCollision(Collision collision) {
        Physics.performCollision(collision);
        Entity a = collision.getA();
        Entity b = collision.getB();

//        if (a == bottomBounds) {
//            b.removeFromWorld();
//        } else if (b == bottomBounds) {
//            a.removeFromWorld();
//        }
    }

    private void setupInput() {
        controller.setContextBinding(contextType, InputCode.KEY_SPACE, Action.TOGGLE_DRAWING);
        controller.setContextBinding(contextType, InputCode.KEY_ESCAPE, Action.EXIT_GAME);
        controller.setContextBinding(contextType, InputCode.MOUSE_LEFT_BUTTON, Action.MOUSE_CLICK);
        controller.setContextBinding(contextType, InputCode.MOUSE_WHEEL_UP, Action.ZOOM_OUT);
        controller.setContextBinding(contextType, InputCode.MOUSE_WHEEL_DOWN, Action.ZOOM_IN);
        controller.setContextBinding(contextType, InputCode.KEY_SHIFT, Action.PAN);
        controller.setContextBinding(contextType, InputCode.MOUSE_RIGHT_BUTTON, Action.ATTRACT);
    }

    @Override
    public void startAction(Action action, int inputCode) {
        double scaleAmount = 0.1;
        switch (action) {
            case EXIT_GAME:
                break;
            case MOUSE_CLICK:
                shooting = true;
                break;
            case ZOOM_IN:
                viewPort.scaleScale(1 - scaleAmount);
                break;
            case ZOOM_OUT:
                viewPort.scaleScale(1 + scaleAmount);
                break;
            case PAN:
                panning = true;
                break;
        }
    }

    @Override
    public void stopAction(Action action, int inputCode) {
        switch (action) {
            case TOGGLE_DRAWING:
                break;
            case EXIT_GAME:
                controller.exitContext();
                break;
            case MOUSE_CLICK:
                shooting = false;
                int ballSize = 2;
                double jitter = 0;
//                int xOffset = width / 6;
//                int yOffset = 50;
//                int width = 1;
//                int height = 1;
//                int diameter = ballSize * 2 + 10;
//                int rows = 1;
//                for (int x = 0; x < width; x++) {
//                    if (x % 2 == 0) {
//                        rows = x / 2;
//                    } else {
//                        rows = 1;
//                    }
//                    for (int y = 0; y < height; y++) {
//                        addBall((x * diameter) + xOffset, (y * diameter) + yOffset, ballSize);
//                    }
//                }
                for (int i = 0; i < 100; i++) {
                    addBall(width * 0.5 + (rand.nextDouble() - 0.5) * (width - 50),
                            height * 0.5 + (rand.nextDouble() - 0.5) * (height - 100), ballSize);
//                    addBall(pointer.getX() + (rand.nextDouble() - 0.5) * jitter,
//                            pointer.getY() + (rand.nextDouble() - 0.5) * jitter, ballSize);
                }
                break;
            case ATTRACT:
                attracting = !attracting;
                break;
            case PAN:
                panning = false;
                break;
        }
    }

    private void addBall(double x, double y, double radius) {
        double speed = 0.1;
        CircleEntity entity = new CircleEntity(x, y, radius);
//        entity.setMass(1);
        entity.setVelocity((Math.random() - 0.5) * speed, (Math.random() - 0.5) * speed);
        entity.setMaterial(ballMaterial);
        entity.setCollisionType(CollisionType.BALL);
        world.addEntity(entity);
    }
}

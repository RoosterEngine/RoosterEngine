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
import gameengine.motion.motions.AttractMotion;
import gameengine.motion.motions.Motion;
import gameengine.motion.motions.SpringMotion;
import gameengine.physics.Material;
import gameengine.physics.Physics;

import java.awt.*;
import java.util.Random;

public class Testing extends Context implements ActionHandler {
    private Pointer pointer;
    private AttractMotion attractMotion;
    private Random rand = new Random(0);
    private boolean drawing = true;
    private boolean attracting = false;
    private boolean panning = false;
    private Material ballMaterial = Material.createMaterial(0, 1);

    public Testing(GameController controller) {
        super(controller, ContextType.GAME);
        init();
    }

    public void init() {
        entities.clear();
        // TODO each context should have its own world, also clearWorld() should not clear the collision pairs
        controller.clearWorld(this);
        controller.setCollisionPair(this, CollisionType.BALL, CollisionType.BALL);
        controller.setCollisionPair(this, CollisionType.BALL, CollisionType.DEFAULT);
        controller.setCollisionPair(this, CollisionType.WALL, CollisionType.DEFAULT);

        pointer = new Pointer(new OvalGraphic(15, 15, Color.RED), width / 2, height / 2);
//        pointer.getShape().setMass(1);
        controller.addEntityToCollisionDetector(this, pointer);
        entities.add(pointer);
        attractMotion = new AttractMotion(pointer.getX(), pointer.getY(),
                0.00001, 0.1, 1);

        double xOffset = 500;
        double yOffset = 500;
        double ballRadius = 10;
        for (int i = 0; i < 10; i++) {
            double ballX = xOffset + i * ballRadius * 2;
            CircleEntity circle = new CircleEntity(ballX, yOffset, ballRadius);
            circle.getShape().setMaterial(ballMaterial);
            circle.getShape().setCollisionType(CollisionType.BALL);
            if (i == 5) {
                circle.getShape().setMass(10);
                circle.setColor(Color.DARK_GRAY);
            }
            SpringMotion spring = new SpringMotion(ballX, yOffset, 200, 0.001, 0.2, 1);
            circle.setMotion(spring);
            controller.addEntityToCollisionDetector(this, circle);
            entities.add(circle);
        }

        initBounding();
        setupInput();
        viewport.setX(width / 2);
        viewport.setY(height / 2);
    }

    public void initBounding() {
        double borderThickness = 10;
        BoxEntity topBounds = new BoxEntity(width / 2, 0, width, borderThickness);
        BoxEntity bottomBounds = new BoxEntity(width / 2, height, width, borderThickness);
        BoxEntity leftBounds = new BoxEntity(0, height / 2, borderThickness, height);
        BoxEntity rightBounds = new BoxEntity(width, height / 2, borderThickness, height);
        topBounds.getShape().setMass(Double.POSITIVE_INFINITY);
        bottomBounds.getShape().setMass(Double.POSITIVE_INFINITY);
        leftBounds.getShape().setMass(Double.POSITIVE_INFINITY);
        rightBounds.getShape().setMass(Double.POSITIVE_INFINITY);
        topBounds.getShape().setCollisionType(CollisionType.WALL);
        bottomBounds.getShape().setCollisionType(CollisionType.WALL);
        leftBounds.getShape().setCollisionType(CollisionType.WALL);
        rightBounds.getShape().setCollisionType(CollisionType.WALL);
        controller.setCollisionPair(this, CollisionType.BALL, CollisionType.WALL);

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

    @Override
    public void update(double elapsedTime) {
        if (panning) {
            viewport.setPosition(pointer.getX(), pointer.getY());
        }
        if (attracting) {
            attractMotion.setDestination(pointer.getX(), pointer.getY());
            double k = 0.00001;
            double d = 0.001;
            double targetD = 300;
            for (Entity entity : entities) {
                double attractStrength = k / entity.getShape().getMass();
                double deltaX = pointer.getX() - entity.getX();
                double deltaY = pointer.getY() - entity.getY();
                double velX = (Math.abs(deltaX) - targetD) * attractStrength * Math.signum(deltaX)- d * entity.getDX();
                double velY = (Math.abs(deltaY) - targetD) * attractStrength * Math.signum(deltaY)- d * entity.getDY();
                entity.addVelocity(velX * elapsedTime, velY * elapsedTime);
            }
        }
//
        double d = 0.99;
        double g = 0.001 * elapsedTime;
        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
            if (!(entity instanceof BoxEntity)) {
                entity.addVelocity(0, g);
            }
            entity.setVelocity(entity.getDX() * d, entity.getDY() * d);
        }
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, width, height);

        controller.drawWorld(this, g);

        if (drawing) {
            for (Entity entity : entities) {
                Motion motion = entity.getMotion();
                if (motion instanceof SpringMotion) {
                    ((SpringMotion)motion).draw(g, Color.red, entity);
                }
            }
//            controller.drawPartitions(g, Color.RED);
//            for (Entity entity : entities) {
//                entity.drawLineToPartition(g, new Color(93, 71, 57));
//            }
        }
        drawStats(g);
    }

    private void drawStats(Graphics2D g) {
        g.setColor(Color.red);
        g.drawString("fps: " + controller.getFrameRate(), 25, 25);
        g.drawString("ups: " + controller.getUpdateRate(), 25, 50);
        g.drawString("entities: " + entities.size(), 25, 75);
        g.drawString("scale: " + viewport.getScale(), 25, 100);
    }

    @Override
    public void handleCollision(Collision collision) {
        Physics.performCollision(collision);
    }

    private void setupInput() {
        controller.setContextBinding(contextType, InputCode.KEY_SPACE,
                Action.TOGGLE_DRAWING);
        controller.setContextBinding(contextType, InputCode.KEY_ESCAPE, Action.EXIT_GAME);
        controller.setContextBinding(contextType, InputCode.MOUSE_LEFT_BUTTON, Action.MOUSE_CLICK);
        controller.setContextBinding(contextType, InputCode.MOUSE_WHEEL_UP, Action.ZOOM_OUT);
        controller.setContextBinding(contextType, InputCode.MOUSE_WHEEL_DOWN, Action.ZOOM_IN);
        controller.setContextBinding(contextType, InputCode.KEY_SHIFT, Action.PAN);
        controller.setContextBinding(contextType, InputCode.MOUSE_RIGHT_BUTTON, Action.ATTRACT);
    }

    @Override
    public void startAction(Action action, int inputCode) {
        double scaleAmount = 0.01;
        switch (action) {
            case EXIT_GAME:
                break;
            case MOUSE_CLICK:
                break;
            case ZOOM_IN:
                viewport.scaleScale(1 - scaleAmount);
                break;
            case ZOOM_OUT:
                viewport.scaleScale(1 + scaleAmount);
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
                drawing = !drawing;
                break;
            case EXIT_GAME:
                controller.exitContext();
                break;
            case MOUSE_CLICK:
                int ballSize = 10;
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
                for (int i = 0; i < 1; i++) {
//                    addBall(width * 0.5 + (rand.nextDouble() - 0.5) * (width - 50),
//                            height * 0.5 + (rand.nextDouble() - 0.5) * (height - 100), ballSize);
                    addBall(pointer.getX() + (rand.nextDouble() - 0.5) * jitter,
                            pointer.getY() + (rand.nextDouble() - 0.5) * jitter, ballSize);
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
//        entity.setColor(new Color((float)Math.random(), (float)Math.random(), (float)Math.random()));
//        entity.setVelocity(0, 1);
        entity.getShape().setMass(1);
        entity.setVelocity((Math.random() - 0.5) * speed, (Math.random() - 0.5) * speed);
        entity.getShape().setMaterial(ballMaterial);
        entity.getShape().setCollisionType(CollisionType.BALL);
        controller.addEntityToCollisionDetector(this, entity);
        entities.add(entity);
    }
}

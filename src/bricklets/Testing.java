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
import gameengine.physics.Material;
import gameengine.physics.Physics;

import java.awt.*;
import java.util.Random;

public class Testing extends Context implements ActionHandler {
    private Pointer pointer;
    private AttractMotion attractMotion;
    private double scale = 1;
    private Color bgColor = Color.WHITE;
    private Random rand = new Random(0);
    private boolean drawing = true;

    public Testing(GameController controller) {
        super(controller, ContextType.GAME);
        init();
    }

    public void init() {
//        scale = 1;
//        if (!isPaused()) {
//            togglePause();
//        }
        entities.clear();
        controller.clearCollisions(this);
        controller.setCollisionPair(this, CollisionType.BALL, CollisionType.BALL);
        controller.setCollisionPair(this, CollisionType.BALL, CollisionType.DEFAULT);

        pointer = new Pointer(new OvalGraphic(10, 10, Color.RED), width / 4, height / 4);
        controller.addEntityToCollisionDetector(this, pointer);
        entities.add(pointer);
        attractMotion = new AttractMotion(pointer.getX(), pointer.getY(),
                0.00001, 0.1, 1);

        initBounding();

//        BoxEntity box = new BoxEntity(width / 2, height / 2, 25, height * 0.8);
//        box.getShape().setMass(Double.POSITIVE_INFINITY);
//        box.getShape().setMaterial(Material.createMaterial(0, 1));
//        box.getShape().setCollisionType(CollisionType.BALL);
//        entities.add(box);
//        controller.addEntityToCollisionDetector(this, box);

//        double radius = 10;
//        addBall(box.getX() - box.getWidth() / 2 - radius * 1.2, box.getY(), radius);
//        box = new BoxEntity(width / 4, height / 2, 100, 50);
//        boxMotion = new AttractMotion(width / 2, height / 2, 0.0001, 0.3, box.getShape().getMass());
//        box.setMotion(boxMotion);
//        entities.add(box);
//
//        Material material = Material.createMaterial(0.1, 0.001);
//        box.getShape().setMaterial(material);
//        box.getShape().setCollisionType(CollisionType.DEFAULT);
//        controller.addEntityToCollisionDetector(this, box);
//
//        BoxEntity box2 = new BoxEntity(width - width / 4, height / 2, 100, 50);
//        box2.getShape().setMass(10000);
//        entities.add(box2);
//
//        box2.getShape().setMaterial(material);
//        box2.getShape().setCollisionType(CollisionType.DEFAULT);
//        controller.addEntityToCollisionDetector(this, box2);

        setupInput();
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
//        attractMotion.setDestination(pointer.getX(), pointer.getY());
//        double k = 0.000001;
//        double d = 0.001;
//        double targetD = 500;
//        for (Entity entity : entities) {
//            double attractStrength = k / entity.getShape().getMass();
//            double deltaX = pointer.getX() - entity.getX();
//            double deltaY = pointer.getY() - entity.getY();
//            double velX = (Math.abs(deltaX) - targetD) * attractStrength * Math.signum(deltaX)- d * entity.getDX();
//            double velY = (Math.abs(deltaY) - targetD) * attractStrength * Math.signum(deltaY)- d * entity.getDY();
//            entity.addVelocity(velX * elapsedTime, velY * elapsedTime);
//        }
        double d = 0.999;
        double g = 0.001 * elapsedTime;
        for (Entity entity : entities) {
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

        if (drawing) {
            int shiftX = (int) (width * (1 - scale) / 2);
            int shiftY = (int) (height * (1 - scale) / 2);
            g.translate(shiftX, shiftY);
            g.scale(scale, scale);

            for (Entity entity : entities) {
                entity.draw(g);
            }
//            controller.drawPartitions(g, Color.RED);
//            for (Entity entity : entities) {
//                entity.drawLineToPartition(g, new Color(93, 71, 57));
//            }

            g.scale(1 / scale, 1 / scale);
            g.translate(-shiftX, -shiftY);
        }
        drawStats(g);
    }

    private void drawStats(Graphics2D g) {
        g.setColor(Color.red);
        g.drawString("fps: " + controller.getFrameRate(), 25, 25);
        g.drawString("ups: " + controller.getUpdateRate(), 25, 50);
        g.drawString("entities: " + entities.size(), 25, 75);
    }

    @Override
    public void handleCollision(Collision collision) {
        Physics.performCollision(collision);
//        double bright = 35;
//        bgColor = new Color((int) (Math.random() * bright), (int) (Math.random() * bright), (int) (Math.random() * bright));
    }

    private void setupInput() {
        controller.setContextBinding(contextType, InputCode.KEY_SPACE,
                Action.TOGGLE_DRAWING);
        controller.setContextBinding(contextType, InputCode.KEY_ESCAPE, Action.EXIT_GAME);
        controller.setContextBinding(contextType, InputCode.MOUSE_LEFT_BUTTON, Action.MOUSE_CLICK);
        controller.setContextBinding(contextType, InputCode.MOUSE_WHEEL_UP, Action.ZOOM_OUT);
        controller.setContextBinding(contextType, InputCode.MOUSE_WHEEL_DOWN, Action.ZOOM_IN);
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
                scale *= 1 - scaleAmount;
                break;
            case ZOOM_OUT:
                scale *= 1 + scaleAmount;
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
                double radius = 1;
                for (int i = 0; i < 200; i++) {
                    addBall(width * 0.5 + (rand.nextDouble() - 0.5) * (width - 50),
                            height * 0.5 + (rand.nextDouble() - 0.5) * (height - 100), 2);
//                    addBall(pointer.getX() + (rand.nextDouble() - 0.5) * radius,
//                            pointer.getY() + (rand.nextDouble() - 0.5) * radius, 2);
                }
                break;
        }
    }

    private void addBall(double x, double y, double radius) {
        double speed = 0.1;
        CircleEntity entity = new CircleEntity(x, y, radius);
        entity.setVelocity((Math.random() - 0.5) * speed, (Math.random() - 0.5) * speed);
        entity.getShape().setMaterial(Material.createMaterial(0, 1));
        entity.getShape().setCollisionType(CollisionType.BALL);
        controller.addEntityToCollisionDetector(this, entity);
        entities.add(entity);
//        entity.setMotion(attractMotion);
//        entity.setVelocity(5, 0);
    }
}

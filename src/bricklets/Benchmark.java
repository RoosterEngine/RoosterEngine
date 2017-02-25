package bricklets;

import gameengine.collisiondetection.Collision;
import gameengine.collisiondetection.EntityType;
import gameengine.context.Context;
import gameengine.core.GameController;
import gameengine.entities.Entity;
import gameengine.entities.Pointer;
import gameengine.graphics.RColor;
import gameengine.graphics.Renderer;
import gameengine.graphics.ScreenManager;
import gameengine.graphics.image.OvalGraphic;
import gameengine.input.InputCode;
import gameengine.physics.Material;
import gameengine.physics.Physics;

import java.util.Random;

public class Benchmark extends Context {

    private static final String EXIT = "Exit";
    private static final String INCREASE_BALL_COUNT = "Increase Ball Count";

    private Random rand = new Random(0);
    private Material ballMaterial = Material.createMaterial(0, 1, 1);
    private double currentTime = 0;
    private double lastTime = 0;
    private int balls = 0, maxBalls = 3000;
    private int width, height;

    public Benchmark(GameController controller) {
        super(controller);
        init();
    }

    public void init() {
        world.clear();
        world.setCollisionGroups(EntityType.BALL, EntityType.BALL, EntityType.WALL);
        balls = 0;

        Entity.setDefaultMaterial(Material.createMaterial(0, 1, 1));
        Entity.setDefaultEntityType(EntityType.STANDARD);

        ScreenManager screen = controller.getScreenManager();
        width = screen.getWidth();
        height = screen.getHeight();

        Pointer pointer = new Pointer(new OvalGraphic(15, 15, RColor.RED), width / 2, height / 2);
        pointer.setMass(1);
//        world.addEntity(pointer);
        initBounding();
        setupInput();
    }

    public void initBounding() {
        double borderThickness = 0.0000001;

        Entity.setDefaultMaterial(Material.createMaterial(0, 1, Double.POSITIVE_INFINITY));
        Entity.setDefaultEntityType(EntityType.WALL);
        BoxEntity topBounds = new BoxEntity(width / 2, 0, width, borderThickness);
        BoxEntity bottomBounds = new BoxEntity(width / 2, height, width, borderThickness);
        BoxEntity leftBounds = new BoxEntity(0, height / 2, borderThickness, height);
        BoxEntity rightBounds = new BoxEntity(width, height / 2, borderThickness, height);

        world.addEntity(topBounds);
        world.addEntity(bottomBounds);
        world.addEntity(leftBounds);
        world.addEntity(rightBounds);
    }

    @Override
    protected void updateContext(long gameTime, double mouseDeltaX, double mouseDeltaY, double
            mouseWheelRotation) {
        currentTime = gameTime;
        double timeBetweenBalls = 25;
        if (lastTime + timeBetweenBalls <= currentTime && balls < maxBalls) {
            int ballSize = 2;
            double halfWidth = width * 0.5;
            double halfHeight = height * 0.5;
            double xLength = width - 50;
            double yLength = height - 100;
            for (int i = 0; i < 100; i++) {
                addBall(halfWidth + (rand.nextDouble() - 0.5) * xLength, halfHeight + (rand
                        .nextDouble() - 0.5) * yLength, ballSize);
                balls++;
            }
            lastTime = currentTime;// - (currentTime - lastTime + timeBetweenBalls);
        }
    }

    @Override
    protected void renderContext(Renderer renderer, long gameTime) {
        renderer.setForegroundColor(RColor.BLACK);
        renderer.fillRect(width / 2, height / 2, width / 2, height / 2);
        world.draw(this, renderer);
        world.drawTree(renderer, RColor.RED);
        drawStats(renderer);
    }

    private void drawStats(Renderer renderer) {
        renderer.setForegroundColor(RColor.RED);
        renderer.drawString("fps: " + controller.getFrameRateCounter().getCurrentTickRate(), 25,
                25);
        renderer.drawString("balls: " + balls + " / " + maxBalls, 25, 75);
    }

    @Override
    public void handleCollision(Collision collision) {
        Physics.performCollision(collision);
    }

    private void setupInput() {
        mapInputAction(EXIT, InputCode.KEY_ESCAPE);
        mapInputAction(INCREASE_BALL_COUNT, InputCode.MOUSE_LEFT_BUTTON);

        mapActionStartedHandler(EXIT, () -> controller.exitContext());
        mapActionStartedHandler(INCREASE_BALL_COUNT, () -> {
            int ballSize = 2;
            double halfWidth = width * 0.5;
            double halfHeight = height * 0.5;
            double xLength = width - 50;
            double yLength = height - 100;
            for (int i = 0; i < 200; i++) {
                addBall(halfWidth + (rand.nextDouble() - 0.5) * xLength, halfHeight + (rand
                        .nextDouble() - 0.5) * yLength, ballSize);
                balls++;
            }
        });
    }

    private void addBall(double x, double y, double radius) {
        double speed = 0.1;
        CircleEntity entity = new CircleEntity(x, y, radius);
        entity.setMass(1);
        entity.setVelocity((Math.random() - 0.5) * speed, (Math.random() - 0.5) * speed);
        entity.setMaterial(ballMaterial);
        entity.setEntityType(EntityType.BALL);
        world.addEntity(entity);
    }
}

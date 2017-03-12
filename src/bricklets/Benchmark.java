package bricklets;

import Utilities.GameUtils;
import gameengine.collisiondetection.Collision;
import gameengine.collisiondetection.EntityType;
import gameengine.collisiondetection.shapes.Circle;
import gameengine.context.Context;
import gameengine.core.GameController;
import gameengine.entities.Entity;
import gameengine.entities.Pointer;
import gameengine.geometry.Vector2D;
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

    private static final Circle CIRLCLE = new Circle(2);

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

        Vector2D[] points = new Vector2D[5];
        points[0] = new Vector2D(0, height);
        points[1] = new Vector2D(0, 0);
        points[2] = new Vector2D(width, 0);
        points[3] = new Vector2D(width, height);
        points[4] = points[0];
        GameUtils.createWalls(world, EntityType.WALL, borderThickness, points);
    }

    @Override
    protected void updateContext(long gameTime, double mouseDeltaX, double mouseDeltaY, double
            mouseWheelRotation) {
        currentTime = gameTime;
        double timeBetweenBalls = 25;
        if (lastTime + timeBetweenBalls <= currentTime && balls < maxBalls) {
            double halfWidth = width * 0.5;
            double halfHeight = height * 0.5;
            double xLength = width - 50;
            double yLength = height - 100;
            for (int i = 0; i < 100; i++) {
                addBall(halfWidth + (rand.nextDouble() - 0.5) * xLength, halfHeight + (rand
                        .nextDouble() - 0.5) * yLength);
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
            double halfWidth = width * 0.5;
            double halfHeight = height * 0.5;
            double xLength = width - 50;
            double yLength = height - 100;
            for (int i = 0; i < 200; i++) {
                addBall(halfWidth + (rand.nextDouble() - 0.5) * xLength, halfHeight + (rand
                        .nextDouble() - 0.5) * yLength);
                balls++;
            }
        });
    }

    private void addBall(double x, double y) {
        double speed = 0.1;
        TestingEntity entity = new TestingEntity(x, y, CIRLCLE);
        entity.setMass(1);
        entity.setVelocity((Math.random() - 0.5) * speed, (Math.random() - 0.5) * speed);
        entity.setMaterial(ballMaterial);
        entity.setEntityType(EntityType.BALL);
        world.addEntity(entity);
    }
}

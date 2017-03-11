package bricklets;

import Utilities.GameUtils;
import gameengine.collisiondetection.Collision;
import gameengine.collisiondetection.EntityType;
import gameengine.collisiondetection.shapes.Rectangle;
import gameengine.context.Context;
import gameengine.core.GameController;
import gameengine.entities.Entity;
import gameengine.entities.RegionSensor;
import gameengine.geometry.Vector2D;
import gameengine.graphics.RColor;
import gameengine.graphics.Renderer;
import gameengine.graphics.ScreenManager;
import gameengine.input.InputCode;
import gameengine.motion.environmentmotions.VelocityEnforcerWorldEffect;
import gameengine.motion.motions.AttractMotion;
import gameengine.motion.motions.MotionCompositor;
import gameengine.motion.motions.MouseMotion;
import gameengine.motion.motions.VerticalAttractMotion;
import gameengine.physics.Material;
import gameengine.physics.Physics;

import java.util.Random;

/**
 * @author davidrusu
 */
public class BrickBreaker extends Context {
    private static final String SHOOT = "Shoot";
    private static final String EXIT = "Exit";

    private int width, height;
    private int brickCount = 0;
    private Paddle paddle;

    private Entity controlledEntity;
    private Random rand = new Random(1);
    private double initialPaddleY;
    private double ballRadius = 10;
    private int lives = 100;
    private long currentTime;

    private RepeatedAction shootAction = createRepeatedAction(50, () -> {
        addBall(controlledEntity.getX(), controlledEntity.getY() - controlledEntity.getHeight(),
                ballRadius, 50);
    });

    private Material ballMaterial = Material.createMaterial(0, 1, 1);

    public BrickBreaker(GameController controller) {
        super(controller);
        init();
    }

    private void init() {
        ScreenManager screen = controller.getScreenManager();
        width = screen.getWidth();
        height = screen.getHeight();
        initialPaddleY = height - 200;

        shootAction.pause();
        rand.setSeed(1);
        world.clear();
        lives = 100;
        world.setCollisionGroups(EntityType.PADDLE, EntityType.BALL);
        world.setCollisionGroups(EntityType.STANDARD, EntityType.STANDARD, EntityType.WALL,
                EntityType.BALL);
        world.setCollisionGroups(EntityType.BALL, EntityType.PADDLE, EntityType.BALL, EntityType
                .WALL);
        world.setCollisionGroups(EntityType.WALL, EntityType.PADDLE);

        VelocityEnforcerWorldEffect velocityEnforcer = new VelocityEnforcerWorldEffect(0.5, 0.6);
        velocityEnforcer.addCollisionType(EntityType.BALL);
        world.addEnvironmentMotion(velocityEnforcer);

        double paddleDensity = 0.001;
        Entity.setDefaultMaterial(Material.createMaterial(0, 1, paddleDensity));
        Entity.setDefaultEntityType(EntityType.PADDLE);
        paddle = new Paddle(width / 2, initialPaddleY, 300, 50);
        world.addEntity(paddle);

        initBricks();
        initBounding();

        controlledEntity = paddle;
        controlledEntity.setMotion(new MotionCompositor(new MouseMotion(), new
                VerticalAttractMotion(paddle.getY(), 0.005, 0.3, controlledEntity.getMass())));

        setupInput();
    }

    public void initBounding() {
        double borderThickness = 2;
        Entity.setDefaultEntityType(EntityType.WALL);
        Entity.setDefaultMaterial(Material.createMaterial(0, 1, Double.POSITIVE_INFINITY));

        Vector2D[] points = new Vector2D[4];
        points[0] = new Vector2D(0, height);
        points[1] = new Vector2D(0, 0);
        points[2] = new Vector2D(width, 0);
        points[3] = new Vector2D(width, height);
        GameUtils.createWalls(world, EntityType.WALL, borderThickness, points);

        Entity.setDefaultEntityType(EntityType.STANDARD);
        RegionSensor bottom = new RegionSensor(width / 2, height - 50, new Rectangle(width,
                borderThickness), entity -> {
            lives--;
            entity.removeFromWorld();
        }, null);
        world.addEntity(bottom);
    }

    private void initBricks() {
        brickCount = 0;
        int rows = 10;
        int columns = 70;
        double padding = ballRadius * 1.5;
        double borderPadding = ballRadius * 5;
        double yOffset = borderPadding * 1.75;
        double brickWidth = (width - borderPadding * 2 - (columns + 1) * padding) / columns;
        double brickHeight = brickWidth;
        Entity.setDefaultEntityType(EntityType.STANDARD);
        for (int x = 0; x < columns; x++) {
            for (int y = 0; y < rows; y++) {
                double xPos = padding * (1 + x) + brickWidth * x + brickWidth / 2 + borderPadding;
                double yPos = padding * (1 + y) + brickHeight * y + brickHeight / 2 + yOffset;
                Brick brick = new Brick(xPos, yPos, brickWidth, brickHeight);
//                brick.setShape(new Circle(xPos, yPos, brickWidth));
                brick.setMass(20);
                brick.setMotion(new AttractMotion(xPos, yPos, 0.0005, 0.3, brick.getMass()));
                brickCount++;
                world.addEntity(brick);
            }
        }

        //path experiment
        Vector2D[] path = new Vector2D[4];
        path[0] = new Vector2D(width / 10, height / 2);
        path[1] = new Vector2D(width / 2, height * 9 / 10);
        path[2] = new Vector2D(width * 9 / 10, height / 2);
        path[3] = new Vector2D(width / 2, height / 10);

        GameUtils.createEntityPath(world, EntityType.WALL, 0.01, 0.5, 50, true, path);

        //some walls
        Vector2D from = new Vector2D(500, 600);
        Vector2D to = new Vector2D(350, 450);
        GameUtils.createWall(world, EntityType.WALL, 8, from, to);

        from = new Vector2D(350, 700);
        to = new Vector2D(550, 650);
        GameUtils.createWall(world, EntityType.WALL, 8, from, to);
    }

    @Override
    protected void updateContext(long gameTime, double mouseDeltaX, double mouseDeltaY, double
            mouseWheelRotation) {
        currentTime = gameTime;
        if (brickCount == 0) {
            init();
        }
    }

    @Override
    protected void renderContext(Renderer renderer, long gameTime) {
//        renderer.setForegroundColor(bgColor);
//        renderer.fillRect(width / 2, height / 2, width / 2, height / 2);
        drawText(renderer);
    }

    private void drawText(Renderer renderer) {
        renderer.setForegroundColor(RColor.RED);

        renderer.drawStrings(25, 50, 20, "fps " + controller.getFrameRateCounter()
                .getCurrentTickRate(), "Lives " + lives, "entities " + world.getEntityCount(),
                "bricks " + brickCount);
    }

    @Override
    public void handleCollision(Collision collision) {
        Physics.performCollision(collision);
//        bgColor = new RColor(
//                (int)(Math.random() * 255), (int)(Math.random() * 255), (int)(Math.random() *
// 255));
        double damage = 30;
        Entity a = collision.getA();
        Entity b = collision.getB();
        boolean isABall = a instanceof CircleEntity;
        boolean isBBall = b instanceof CircleEntity;
        if (a instanceof Brick && isBBall) {
//            damage *= b.getMass();
            damageBrick((Brick) a, damage);
        } else if (b instanceof Brick && isABall) {
//            damage *= a.getMass();
            damageBrick((Brick) b, damage);
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
        mapInputAction(EXIT, InputCode.KEY_ESCAPE);
        mapInputAction(SHOOT, InputCode.MOUSE_LEFT_BUTTON);

        mapActionStartedHandler(EXIT, () -> controller.exitContext());
        mapActionStartedHandler(SHOOT, () -> shootAction.resume());
        mapActionStoppedHandler(SHOOT, () -> shootAction.pause());
    }

    private void addBall(double x, double y, double radius, double mass) {
        CircleEntity entity = new CircleEntity(x, y, radius);
        double spread = (Math.sin(currentTime / 1000000) + 1.5) * 2;
        entity.setVelocity((Math.random() - 0.5) * spread, -10);
        entity.setMass(mass);
        entity.setMaterial(ballMaterial);
        entity.setEntityType(EntityType.BALL);
        world.addEntity(entity);
    }
}

package bricklets;

import gameengine.collisiondetection.Collision;
import gameengine.collisiondetection.EntityType;
import gameengine.context.Context;
import gameengine.core.GameController;
import gameengine.entities.Entity;
import gameengine.graphics.MutableColor;
import gameengine.graphics.Renderer;
import gameengine.graphics.ScreenManager;
import gameengine.input.InputCode;
import gameengine.motion.motions.Motion;
import gameengine.motion.motions.MouseMotion;
import gameengine.motion.motions.NoMotion;
import gameengine.physics.Material;
import gameengine.physics.Physics;

import java.util.ArrayList;

/**
 * documentation
 *
 * @author davidrusu
 */
public class Test extends Context {
    private static final String EXIT = "Exit";
    private static final String CHANGE_CONTROL = "Change Control";

    private static final MutableColor BLACK = MutableColor.createBlackInstance();
    private static final MutableColor RED = MutableColor.createRedInstance();
    private static final MutableColor WHITE = MutableColor.createWhiteInstance();

    private ArrayList<TestingEntity> entities = new ArrayList<>();
    private int controlIndex = 0;
    private Entity controlledEntity;
    private Motion noMotion = new NoMotion();
    private Motion mouseMotion = new MouseMotion();
    private int width, height;

    /**
     * Constructs a Context
     *
     * @param controller The {@link GameController} controlling the game
     */
    public Test(GameController controller) {
        super(controller);
        init();
    }

    public void init() {
        ScreenManager screen = controller.getScreenManager();
        width = screen.getWidth();
        height = screen.getHeight();

        world.clear();
//        world.setCollisionGroups(EntityType.STANDARD, EntityType.STANDARD, EntityType.WALL);
        entities.clear();

        double centerX = width / 2;
        double centerY = height / 2;
        double length = 100;
        Entity.setDefaultMaterial(Material.createMaterial(0, 1, 1));
        Entity.setDefaultEntityType(EntityType.STANDARD);
        double currentX = length;
        BoxEntity box = new BoxEntity(currentX, centerY, length, length);
        currentX += length * 1.5;
        CircleEntity circle = new CircleEntity(currentX, centerY, length / 2);
        currentX += length * 1.5;
        Paddle poly = new Paddle(currentX, centerY, length / 2, length / 2);

        box.setMotion(noMotion);
        circle.setMotion(noMotion);
        poly.setMotion(noMotion);
        entities.add(box);
        entities.add(circle);
        entities.add(poly);
        world.addEntity(box);
        world.addEntity(circle);
        world.addEntity(poly);

        currentX += length * 1.5;
        box = new BoxEntity(currentX, centerY, length, length / 2);
        currentX += length * 1.5;
        circle = new CircleEntity(currentX, centerY, length / 2);
        currentX += length * 1.5;
        poly = new Paddle(currentX, centerY, length / 2, length / 2);

        box.setMotion(noMotion);
        circle.setMotion(noMotion);
        poly.setMotion(noMotion);
        entities.add(box);
        entities.add(circle);
        entities.add(poly);
        world.addEntity(box);
        world.addEntity(circle);
        world.addEntity(poly);

        controlledEntity = entities.get(0);
        controlledEntity.setMotion(mouseMotion);
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
        for (int i = 0; i < entities.size(); i++) {
            entities.get(i).setColor(WHITE);
        }
        for (int i = 0; i < entities.size(); i++) {
            TestingEntity a = entities.get(i);
            for (int j = i + 1; j < entities.size(); j++) {
                TestingEntity b = entities.get(j);
                if (a.getShape().isOverlappingShape(b.getShape())) {
                    a.setColor(RED);
                    b.setColor(RED);
                }
            }
        }
    }

    @Override
    protected void renderContext(Renderer renderer, long gameTime) {
        renderer.setForegroundColor(BLACK);
        renderer.fillRect(width / 2, height / 2, width / 2, height / 2);
        world.draw(this, renderer);
        drawStats(renderer);
        //To change body of implemented methods use File | Settings | File Templates.
    }

    private void drawStats(Renderer renderer) {
        renderer.setForegroundColor(RED);
        renderer.drawString("fps: " + controller.getFrameRateCounter().getCurrentTickRate(), 25,
                25);
        renderer.drawString("entities: " + world.getEntityCount(), 25, 75);
    }

    @Override
    public void handleCollision(Collision collision) {
        Physics.performCollision(collision);
    }

    private void setupInput() {
        mapInputAction(EXIT, InputCode.KEY_ESCAPE);
        mapInputAction(CHANGE_CONTROL, InputCode.KEY_LEFT);

        mapActionStartedHandler(EXIT, () -> controller.exitContext());

        mapActionStartedHandler(CHANGE_CONTROL, () -> {
            controlledEntity.setMotion(noMotion);
            controlIndex = (controlIndex + 1) % entities.size();
            controlledEntity = entities.get(controlIndex);
            controlledEntity.setMotion(mouseMotion);
        });
    }
}

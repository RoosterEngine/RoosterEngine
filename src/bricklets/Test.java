package bricklets;

import gameengine.collisiondetection.Collision;
import gameengine.collisiondetection.EntityType;
import gameengine.collisiondetection.shapes.Circle;
import gameengine.collisiondetection.shapes.Polygon;
import gameengine.collisiondetection.shapes.Rectangle;
import gameengine.context.Context;
import gameengine.core.GameController;
import gameengine.entities.Entity;
import gameengine.graphics.RColor;
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
        TestingEntity box = new TestingEntity(currentX, centerY, new Rectangle(length, length));
        currentX += length * 1.5;
        TestingEntity circle = new TestingEntity(currentX, centerY, new Circle(length / 2));
        currentX += length * 1.5;
        TestingEntity poly = new TestingEntity(currentX, centerY, Polygon.getRandConvexPolygon
                (10, 100, 3, 10));

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
        box = new TestingEntity(currentX, centerY, new Rectangle(length, length / 2));
        currentX += length * 1.5;
        circle = new TestingEntity(currentX, centerY, new Circle(length / 2));
        currentX += length * 1.5;
        poly = new TestingEntity(currentX, centerY, Polygon.getRandConvexPolygon(10, 100, 3, 10));

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

    @Override
    protected void updateContext(long gameTime, double mouseDeltaX, double mouseDeltaY, double
            mouseWheelRotation) {
        for (int i = 0; i < entities.size(); i++) {
            entities.get(i).setColor(RColor.WHITE);
        }
        for (int i = 0; i < entities.size(); i++) {
            TestingEntity a = entities.get(i);
            for (int j = i + 1; j < entities.size(); j++) {
                TestingEntity b = entities.get(j);
                if (a.getShape().isOverlappingShape(a, b)) {
                    a.setColor(RColor.RED);
                    b.setColor(RColor.RED);
                }
            }
        }
    }

    @Override
    protected void renderContext(Renderer renderer, long gameTime) {
        renderer.setForegroundColor(RColor.BLACK);
        renderer.fillRect(width / 2, height / 2, width / 2, height / 2);
        world.draw(this, renderer);
        drawStats(renderer);
        //To change body of implemented methods use File | Settings | File Templates.
    }

    private void drawStats(Renderer renderer) {
        renderer.setForegroundColor(RColor.RED);
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

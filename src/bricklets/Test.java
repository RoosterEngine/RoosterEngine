package bricklets;

import gameengine.GameController;
import gameengine.collisiondetection.Collision;
import gameengine.collisiondetection.EntityType;
import gameengine.context.Context;
import gameengine.context.ContextType;
import gameengine.entities.BoxEntity;
import gameengine.entities.CircleEntity;
import gameengine.entities.Entity;
import gameengine.input.Action;
import gameengine.input.InputCode;
import gameengine.motion.motions.Motion;
import gameengine.motion.motions.MouseMotion;
import gameengine.motion.motions.NoMotion;
import gameengine.physics.Material;
import gameengine.physics.Physics;

import java.awt.*;
import java.util.ArrayList;

/**
 * documentation
 * User: davidrusu
 * Date: 14/03/13
 * Time: 1:26 PM
 */
public class Test extends Context {
    private ArrayList<Entity> entities = new ArrayList<>();
    private int controlIndex = 0;
    private Entity controlledEntity;
    Motion noMotion = new NoMotion();
    Motion mouseMotion = new MouseMotion();

    /**
     * Constructs a Context
     *
     * @param controller The {@link gameengine.GameController} controlling the game
     */
    public Test(GameController controller) {
        super(controller, ContextType.GAME);
        init();
    }

    public void init() {
        world.clear();
        world.setCollisionGroups(EntityType.STANDARD, EntityType.STANDARD, EntityType.WALL);
        entities.clear();

        double centerX = width / 2;
        double centerY = height / 2;
        double length = 100;
        Entity.setDefaultMaterial(Material.createMaterial(0, 1, 1));
        Entity.setDefaultEntityType(EntityType.STANDARD);
        double currentX = length;
        BoxEntity box = new BoxEntity(currentX, centerY, length, length);
        currentX += length;
        CircleEntity circle = new CircleEntity(currentX, centerY, length / 2);
        currentX += length;
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
        controlledEntity = box;
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
    public void update(double elapsedTime) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, width, height);
        world.draw(this, g);
        drawStats(g);
        //To change body of implemented methods use File | Settings | File Templates.
    }

    private void drawStats(Graphics2D g) {
        g.setColor(Color.red);
        g.drawString("fps: " + controller.getFrameRate(), 25, 25);
        g.drawString("ups: " + controller.getUpdateRate(), 25, 50);
        g.drawString("entities: " + world.getEntityCount(), 25, 75);
    }

    @Override
    public void handleCollision(Collision collision) {
        Physics.performCollision(collision);
    }

    private void setupInput() {
        controller.setContextBinding(contextType, InputCode.KEY_ESCAPE, Action.EXIT_GAME);
        controller.setContextBinding(contextType, InputCode.MOUSE_LEFT_BUTTON, Action.MOUSE_CLICK);
        controller.setContextBinding(contextType, InputCode.MOUSE_WHEEL_UP, Action.ZOOM_OUT);
        controller.setContextBinding(contextType, InputCode.MOUSE_WHEEL_DOWN, Action.ZOOM_IN);
        controller.setContextBinding(contextType, InputCode.KEY_LEFT, Action.GAME_LEFT);
        controller.setContextBinding(contextType, InputCode.KEY_RIGHT, Action.GAME_RIGHT);

    }

    @Override
    public void startAction(Action action, int inputCode) {
        double scaleAmount = 0.1;
        switch (action) {
            case EXIT_GAME:
                break;
            case MOUSE_CLICK:
                break;
            case ZOOM_IN:
                viewPort.scaleScale(1 - scaleAmount);
                break;
            case ZOOM_OUT:
                viewPort.scaleScale(1 + scaleAmount);
                break;
        }
    }

    @Override
    public void stopAction(Action action, int inputCode) {
        switch (action) {
            case EXIT_GAME:
                controller.exitContext();
                break;
            case MOUSE_CLICK:
                break;
            case GAME_LEFT:
                controlledEntity.setMotion(noMotion);
                controlIndex = (controlIndex + 1) % entities.size();
                controlledEntity = entities.get(controlIndex);
                controlledEntity.setMotion(mouseMotion);
                break;
            case GAME_RIGHT:
                break;
        }
    }
}

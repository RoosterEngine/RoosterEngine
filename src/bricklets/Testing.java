package bricklets;

import gameengine.*;
import gameengine.motion.motions.AttractMotion;
import gameengine.input.Action;
import gameengine.input.ActionHandler;
import gameengine.input.InputCode;

import java.awt.Color;
import java.awt.Graphics2D;

public class Testing extends Context implements ActionHandler{
    private Pointer pointer;
    private AABBEntity box;
    private AttractMotion boxMotion;

    public Testing(GameController controller){
        super(controller, ContextType.GAME, false, true);
        init();
    }

    private void init(){
        entities.clear();
        controller.clearCollisions(this);
        controller.setCollisionPair(this, 0, 0);
        pointer = new Pointer(new OvalGraphic(10, 10, Color.RED), width / 2, height / 2);
        entities.add(pointer);

        box = new AABBEntity(width / 4, height / 2, 100, 50);
        boxMotion = new AttractMotion(width / 2, height / 2, 0.0001, 0.3, box.getMass());
        box.setMotion(boxMotion);
        entities.add(box);

        Material material = Material.createCustomMaterial(0.1, 0.001);
        controller.addShapeToCollisionDetector(this, new AABBShape(box.getX(), box.getY(), box.getWidth(), box.getHeight(), box, material), 0);

        AABBEntity box2 = new AABBEntity(width - width / 4, height / 2, 100, 50);
        box2.setMass(10000);
        entities.add(box2);

        controller.addShapeToCollisionDetector(this, new AABBShape(box2.getX(), box2.getY(), box2.getWidth(), box2.getHeight(), box2, material), 0);

        setupInput();
    }

    @Override
    public void update(double elapsedTime) {
        boxMotion.setDestination(box, pointer.getX(), pointer.getY());
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, width, height);
        for (Entity entity : entities) {
            entity.draw(g);
        }
    }

    @Override
    public void handleCollision(Collision collision, double collisionsPerMilli) {
        Physics.performCollision(collision);
    }

    private void setupInput(){
        controller.setContextBinding(contextType, InputCode.KEY_ESCAPE, Action.EXIT_GAME);
        controller.setContextBinding(contextType, InputCode.MOUSE_LEFT_BUTTON, Action.MOUSE_CLICK);
    }

    @Override
    public void startAction(Action action, int inputCode) {
        switch (action) {
            case EXIT_GAME:
                break;
            case MOUSE_CLICK:
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
        }
    }
}
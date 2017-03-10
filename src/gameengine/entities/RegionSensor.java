package gameengine.entities;

import Utilities.HashSet;
import gameengine.collisiondetection.shapes.Shape;
import gameengine.graphics.Renderer;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * A region sensor detects when entities enter the region.
 */
public class RegionSensor extends Entity {
    private HashSet<Entity> containedEntities = new HashSet<>();
    private Consumer<Entity> enteredRegionHandler;
    private Consumer<Entity> exitedRegionHandler;
    private Predicate<Entity> outOfBounds = entity -> {
        boolean exited = !entity.isInWorld() || !getShape().isOverlappingShape(entity.getShape());
        if (exited && exitedRegionHandler != null) {
            exitedRegionHandler.accept(entity);
        }
        return exited;
    };

    public RegionSensor(double x, double y, Shape shape, Consumer<Entity> enteredRegionHandler,
                        Consumer<Entity> exitedRegionHandler) {
        super(x, y, shape);
        this.enteredRegionHandler = enteredRegionHandler;
        this.exitedRegionHandler = exitedRegionHandler;
    }

    @Override
    public void update(double elapsedTime) {
        containedEntities.forEachConditionallyRemove(outOfBounds);
    }

    public void addEntity(Entity entity) {
        containedEntities.add(entity);
        if (enteredRegionHandler != null) {
            enteredRegionHandler.accept(entity);
        }
    }

    public boolean containsEntity(Entity entity) {
        return containedEntities.contains(entity);
    }

    public void forEach(Consumer<Entity> consumer) {
        containedEntities.forEach(consumer);
    }

    @Override
    public void draw(Renderer renderer) {
        getShape().draw(renderer);

        containedEntities.forEach((entity) -> {
            renderer.drawLine(x, y, entity.x, entity.y);
        });
    }
}

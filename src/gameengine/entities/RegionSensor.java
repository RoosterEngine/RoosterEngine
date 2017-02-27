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
    private Predicate<Entity> outOfBounds = entity -> {
        return !entity.isInWorld() || !getShape().isOverlappingShape(entity.getShape());
    };

    public RegionSensor(double x, double y, Shape shape) {
        super(x, y, shape);
    }

    @Override
    public void update(double elapsedTime) {
        containedEntities.forEachConditionallyRemove(outOfBounds);
    }

    public void addEntity(Entity entity) {
        containedEntities.add(entity);
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

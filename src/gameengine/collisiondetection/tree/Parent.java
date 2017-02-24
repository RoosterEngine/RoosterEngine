package gameengine.collisiondetection.tree;

import gameengine.entities.Entity;

/**
 * Interface for nodes in the spatial tree that have children.
 *
 * @author davidrusu
 */
public interface Parent {

    void childEntityUpdated(double timeToCheck, Entity entity);

    void relocateAndCheck(double timeToCheck, Entity entity);

    void relocate(Entity entity);

    void decrementEntityCount();

    void entityRemovedDuringCollision(double timeToCheck, Entity entity, double currentTime);
}

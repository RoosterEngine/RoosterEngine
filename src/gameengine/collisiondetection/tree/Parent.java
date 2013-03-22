package gameengine.collisiondetection.tree;

import gameengine.collisiondetection.Collision;
import gameengine.entities.Entity;

/**
 * Interface for nodes in the spatial tree that have children
 * <p/>
 * User: davidrusu
 * Date: 15/01/13
 * Time: 9:31 PM
 */
public interface Parent {

    void childEntityUpdated(double timeToCheck, Entity entity);

    void relocateAndCheck(double timeToCheck, Entity entity);

    void relocate(Entity entity);

    void decrementEntityCount();

    void entityRemovedDuringCollision(double timeToCheck, Entity entity, double currentTime);
}

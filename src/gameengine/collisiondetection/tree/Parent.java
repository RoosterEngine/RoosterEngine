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

    void childEntityUpdated(Collision temp, double timeToCheck, Entity entity,
                            CollisionList list);

    void relocateAndCheck(Collision temp, double timeToCheck, Entity entity,
                          CollisionList list);

    void relocate(Entity entity);

    void decrementEntityCount();

    void entityRemovedDuringCollision(Collision temp, double timeToCheck, Entity entity,
                                      double currentTime, CollisionList list);
}

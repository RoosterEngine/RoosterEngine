package gameengine.collisiondetection.tree;

import gameengine.collisiondetection.Collision;
import gameengine.entities.Entity;

/**
 * Interface for nodes in the spatial tree that have children
 *
 * User: davidrusu
 * Date: 15/01/13
 * Time: 9:31 PM
 */
public interface Parent {

    void childEntityUpdated(int[] collisionGroups, Collision temp, double timeToCheck, Entity entity,
                            CollisionList list);

    void relocateAndCheck(int[] collisionGroups, Collision temp, double timeToCheck, Entity entity,
                          CollisionList list);

    void relocate(Entity entity);

    void decrementEntityCount();

    void entityRemovedDuringCollision(int[] collisionGroups, Collision temp, double timeToCheck, Entity entity,
                                      CollisionList list);
}

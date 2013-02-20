package gameengine.collisiondetection.tree;

import gameengine.collisiondetection.Collision;
import gameengine.entities.Entity;

/**
 * documentation
 * User: davidrusu
 * Date: 15/01/13
 * Time: 9:31 PM
 */
public interface Parent {

    void relocateAndCheck(int[] collisionGroups, Collision temp, double timeToCheck, double currentTime, Entity entity,
                          CollisionList list);

    void relocate(Entity entity);

    void decrementEntityCount();
}

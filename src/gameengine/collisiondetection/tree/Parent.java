package gameengine.collisiondetection.tree;

import gameengine.entities.Entity;

/**
 * documentation
 * User: davidrusu
 * Date: 15/01/13
 * Time: 9:31 PM
 */
public interface Parent {

    void relocateLeft(Entity entity);

    void relocateRight(Entity entity);

    void relocateUp(Entity entity);

    void relocateDown(Entity entity);

    void decrementEntityCount();
}

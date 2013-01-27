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

    public void relocateLeft(Entity entity);

    public void relocateRight(Entity entity);

    public void relocateUp(Entity entity);

    public void relocateDown(Entity entity);

    public abstract void checkForCollisionWithTree(
            Tree tree, int[] collisionGroups, Collision temp, Collision result);

}

package gameengine.collisiondetection.tree;

import gameengine.collisiondetection.Collision;
import gameengine.collisiondetection.CollisionPair;
import gameengine.entities.Entity;

import java.util.ArrayList;

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
            Tree tree, ArrayList<CollisionPair> collisionPairs,
            Collision result);

}

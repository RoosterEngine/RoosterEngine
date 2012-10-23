package bricklets;

import java.awt.Color;
import java.util.ArrayList;

/**
 * 
 * @author davidrusu
 */
public class CollisionDetector {
    public static final int MAX_COLLISION_CATEGORIES = 10;
    private ArrayList<Shape>[] categories;
    private ArrayList<CollisionCategoryPair> collisionPairs = new ArrayList<CollisionCategoryPair>();
    
    /**
     * Constructs a CollisionDetector with the specified number of
     * collision categories.
     * Collision categories are lists of {@link Collidable}'s that all interact
     * with the same collision categories. When Items are added to the collision
     * detector the collision category number is also needed. To set which
     * categories collide with each other, the user must set collision pairs.
     * For example an enemy plane is added to the collision detector and the
     * collision category is 1, enemy bullets are added and the collision
     * categories are also 1, a player plane is added and the collision category
     * is 2.
     * Now we must define which collision categories will interact. We want the
     * enemy plane and bullets to collide with the player so we must set a
     * collision pair (1, 2) if we want enemy bullets to collide with each other
     * and enemy planes we must set the collision pair (1, 1)
     * 
     * @param numOfCollisionCategories must not exceed MAX_COLLISION_CATEGORIES or be less than 1
     */
    public CollisionDetector(int numOfCollisionCategories){
        if(numOfCollisionCategories > MAX_COLLISION_CATEGORIES || numOfCollisionCategories < 1){
            throw new IllegalArgumentException("numOfCollisions must not exceed MAX_COLLISION_CATEGORIES or be less than 1, numOfCollisionCategories: " + numOfCollisionCategories);
        }
        
        categories = new ArrayList[numOfCollisionCategories];
        for(int i = 0; i < numOfCollisionCategories; i++){
            categories[i] = new ArrayList();
        }
    }
    
    public void addShape(Shape shape, int collisionCategory){
        categories[collisionCategory].add(shape);
    }
    
    public boolean removeShape(Shape shape, int collisionCategory){
        for(Shape testShape: categories[collisionCategory]){
            if(testShape == shape){
                return categories[collisionCategory].remove(shape);
            }
        }
        return false;
    }
    
    public void setCollisionPair(int collisionCategoryA, int collisionCategoryB){
        int i = 0;
        boolean foundDuplicate = false;
        while(!foundDuplicate && i < collisionPairs.size()){
            CollisionCategoryPair pair = collisionPairs.get(i);
            if((pair.getA() == collisionCategoryA && pair.getB() == collisionCategoryB) || (pair.getB() == collisionCategoryA && pair.getA() == collisionCategoryB)){
                foundDuplicate = true;
            }
            i++;
        }
        if(!foundDuplicate){
            collisionPairs.add(new CollisionCategoryPair(collisionCategoryA, collisionCategoryB));
        }
    }
    
    public boolean removeCollisionPair(int collisionCategoryA, int collisionCategoryB){
        int i = 0;
        while(i < collisionPairs.size()){
            CollisionCategoryPair pair = collisionPairs.get(i);
            if((pair.getA() == collisionCategoryA && pair.getB() == collisionCategoryB) || (pair.getB() == collisionCategoryA && pair.getA() == collisionCategoryB)){
                collisionPairs.remove(pair);
                return true;
            }
            i++;
        }
        return false;
    }
    
    /**
     * Removes all {@link Collidable}'s that were added and collision pairs
     */
    public void clearCollisions(){
        for(ArrayList list: categories){
            list.clear();
        }
        collisionPairs.clear();
    }
    
    public Collision getNextCollision(double maxTime){
        Collision nextCollision = new Collision();
        double shortestTimeTillNextCollision = Shape.NO_COLLISION;
        for(CollisionCategoryPair pair: collisionPairs){
            ArrayList<Shape> listA = categories[pair.getA()];
            ArrayList<Shape> listB = categories[pair.getB()];
            
            for(Shape b: listB){
                b.update();
            }
            
            if(listA == listB){
                checkCollisionsInSingleList(listA, maxTime, nextCollision);
            }else{
                checkCollisionsInLists(listA, listB, maxTime, nextCollision);
            }
        }
        return nextCollision;
    }
    
    private void checkCollisionsInSingleList(ArrayList<Shape> list, double maxTime, Collision result){
        double shortestTimeTillNextCollision = Shape.NO_COLLISION;
        Collision testCollision = new Collision(); //TODO can be moved to the calling method and passed as an argument to save object creation
        for(int i = 0; i < list.size(); i++){
            Shape a = list.get(i);
            for(int j = i + 1; j < list.size(); j++){
                Shape b = list.get(j);
                getTimeToCollision(a, b, maxTime, testCollision);
                if(testCollision.getTimeToCollision() < shortestTimeTillNextCollision){
                    shortestTimeTillNextCollision = testCollision.getTimeToCollision();
                    result.set(testCollision);
                }
            }
        }
    }
    
    private void checkCollisionsInLists(ArrayList<Shape> listA, ArrayList<Shape> listB, double maxTime, Collision result){
        double shortestTimeTillNextCollision = Double.MAX_VALUE;
        Collision testCollision = new Collision(); //TODO can be moved to the calling method and passed as an argument to save object creation
        for(Shape a: listA){
            a.update();
            for(Shape b: listB){
                if(a != b){
                    getTimeToCollision(a, b, maxTime, testCollision);
                    if(testCollision.getTimeToCollision() < shortestTimeTillNextCollision){
                        shortestTimeTillNextCollision = testCollision.getTimeToCollision();
                        result.set(testCollision);
                    }
                }
            }
        }
    }
    
    private void getTimeToCollision(Shape a, Shape b, double maxTime, Collision result){
        if(a.getShapeType() == Shape.TYPE_CIRCLE && b.getShapeType() == Shape.TYPE_CIRCLE){
            Shape.collideCircleCircle(a, b, maxTime, result);
        }else if(a.getShapeType() == Shape.TYPE_POLYGON && b.getShapeType() == Shape.TYPE_POLYGON){
            Shape.collidePolyPoly((Polygon) a, (Polygon) b, maxTime, result);
        }
    }
}
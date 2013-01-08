package bricklets;

import gameengine.Context;

import java.util.ArrayList;

public class CollisionDetector {
    public static final int MAX_COLLISION_CATEGORIES = 10;
    private ArrayList<Shape>[] categories;
    private ArrayList<CollisionCategoryPair> collisionPairs = new ArrayList<>();
    private double[] collisionTimes = new double[16];
    private int back = 0, numCollision = 0;
    private double currentGameTime = 0;

    /**
     * Constructs a CollisionDetector with the specified number of
     * collision categories.
     * Collision categories are lists of {@link Shape}'s that all interact
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
     */
    public CollisionDetector() {
        categories = new ArrayList[MAX_COLLISION_CATEGORIES];
        for (int i = 0; i < categories.length; i++) {
            categories[i] = new ArrayList<>();
        }
    }

    public void addShape(Shape shape, int collisionCategory) {
        categories[collisionCategory].add(shape);
    }

    public boolean removeShape(Shape shape, int collisionCategory) {
        for (Shape testShape : categories[collisionCategory]) {
            if (testShape == shape) {
                return categories[collisionCategory].remove(shape);
            }
        }
        return false;
    }

    public void removeChildren(Entity parent) {
        for (CollisionCategoryPair pair : collisionPairs) {
            ArrayList<Shape> listA = categories[pair.getA()];
            ArrayList<Shape> listB = categories[pair.getB()];

            for (int i = 0; i < listA.size(); i++) {
                Shape shape = listA.get(i);
                if (shape.getParentEntity() == parent) {
                    listA.remove(shape);
                }
            }

            for (int i = 0; i < listB.size(); i++) {
                Shape shape = listB.get(i);
                if (shape.getParentEntity() == parent) {
                    listB.remove(shape);
                }
            }
        }
    }

    public void setCollisionPair(int collisionCategoryA, int collisionCategoryB) {
        int i = 0;
        boolean foundDuplicate = false;
        while (!foundDuplicate && i < collisionPairs.size()) {
            CollisionCategoryPair pair = collisionPairs.get(i);
            if ((pair.getA() == collisionCategoryA && pair.getB() == collisionCategoryB) || (pair.getB() == collisionCategoryA && pair.getA() == collisionCategoryB)) {
                foundDuplicate = true;
            }
            i++;
        }
        if (!foundDuplicate) {
            collisionPairs.add(new CollisionCategoryPair(collisionCategoryA, collisionCategoryB));
        }
    }

    public boolean removeCollisionPair(int collisionCategoryA, int collisionCategoryB) {
        int i = 0;
        while (i < collisionPairs.size()) {
            CollisionCategoryPair pair = collisionPairs.get(i);
            if ((pair.getA() == collisionCategoryA && pair.getB() == collisionCategoryB) || (pair.getB() == collisionCategoryA && pair.getA() == collisionCategoryB)) {
                collisionPairs.remove(pair);
                return true;
            }
            i++;
        }
        return false;
    }

    /**
     * Removes all {@link Shape}'s that were added and collision pairs
     */
    public void clearCollisions() {
        for (ArrayList list : categories) {
            list.clear();
        }
        collisionPairs.clear();
    }

    public Collision getNextCollision(double maxTime) {
        for (ArrayList<Shape> list : categories) {
            for (Shape shape : list) {
                shape.update();
            }
        }
        Collision nextCollision = new Collision();
        for (CollisionCategoryPair pair : collisionPairs) {
            ArrayList<Shape> listA = categories[pair.getA()];
            ArrayList<Shape> listB = categories[pair.getB()];

            if (listA == listB) {
                checkCollisionsInSingleList(listA, maxTime, nextCollision);
            } else {
                checkCollisionsInLists(listA, listB, maxTime, nextCollision);
            }
        }
        return nextCollision;
    }

    private void checkCollisionsInSingleList(ArrayList<Shape> list, double maxTime, Collision result) {
        double shortestTimeTillNextCollision = Shape.NO_COLLISION;
        Collision testCollision = new Collision(); //TODO can be moved to the calling method and passed as an argument to save object creation
        for (int i = 0; i < list.size(); i++) {
            Shape a = list.get(i);
            for (int j = i + 1; j < list.size(); j++) {
                Shape b = list.get(j);
                getTimeToCollision(a, b, maxTime, testCollision);
                if (testCollision.getTimeToCollision() < shortestTimeTillNextCollision) {
                    shortestTimeTillNextCollision = testCollision.getTimeToCollision();
                    result.set(testCollision);
                }
            }
        }
    }

    private void checkCollisionsInLists(ArrayList<Shape> listA, ArrayList<Shape> listB, double maxTime, Collision result) {
        double shortestTimeTillNextCollision = Double.MAX_VALUE;
        Collision testCollision = new Collision(); //TODO can be moved to the calling method and passed as an argument to save object creation
        for (Shape a : listA) {
            for (Shape b : listB) {
                if (a != b) {
                    getTimeToCollision(a, b, maxTime, testCollision);
                    if (testCollision.getTimeToCollision() < shortestTimeTillNextCollision) {
                        shortestTimeTillNextCollision = testCollision.getTimeToCollision();
                        result.set(testCollision);
                    }
                }
            }
        }
    }

    private void getTimeToCollision(Shape a, Shape b, double maxTime, Collision result) {
        if (a.getShapeType() == Shape.TYPE_CIRCLE && b.getShapeType() == Shape.TYPE_CIRCLE) {
            Shape.collideCircleCircle(a, b, maxTime, result);
        } else if (a.getShapeType() == Shape.TYPE_POLYGON && b.getShapeType() == Shape.TYPE_POLYGON) {
            Shape.collidePolyPoly((Polygon) a, (Polygon) b, maxTime, result);
        } else if (a.getShapeType() == Shape.TYPE_CIRCLE && b.getShapeType() == Shape.TYPE_POLYGON) {
            Shape.collideCirclePoly(a, (Polygon) b, maxTime, result);
        } else if (a.getShapeType() == Shape.TYPE_POLYGON && b.getShapeType() == Shape.TYPE_CIRCLE) {
            Shape.collideCirclePoly(b, (Polygon) a, maxTime, result);
        } else if (a.getShapeType() == Shape.TYPE_AA_BOUNDING_BOX && b.getShapeType() == Shape.TYPE_AA_BOUNDING_BOX) {
            Shape.collideAABBAABB((AABBShape) a, (AABBShape) b, maxTime, result);
        } else if (a.getShapeType() == Shape.TYPE_AA_BOUNDING_BOX && b.getShapeType() == Shape.TYPE_CIRCLE) {
            Shape.collideCircleAABB(b, (AABBShape) a, maxTime, result);
        } else if (a.getShapeType() == Shape.TYPE_CIRCLE && b.getShapeType() == Shape.TYPE_AA_BOUNDING_BOX) {
            Shape.collideCircleAABB(a, (AABBShape) b, maxTime, result);
        } else if (a.getShapeType() == Shape.TYPE_AA_BOUNDING_BOX && b.getShapeType() == Shape.TYPE_POLYGON) {
            Shape.collideAABBPoly((AABBShape) a, (Polygon) b, maxTime, result);
        } else if (a.getShapeType() == Shape.TYPE_POLYGON && b.getShapeType() == Shape.TYPE_AA_BOUNDING_BOX) {
            Shape.collideAABBPoly((AABBShape) b, (Polygon) a, maxTime, result);
        }
    }

    public void update(double elapsedTime, Context context) {
        double timeLeft = elapsedTime;

        context.updateMotions(elapsedTime);

        while (timeLeft > 0 && !context.isPaused()) {
            Collision collision = getNextCollision(timeLeft);
            double updateTime = Math.min(collision.getTimeToCollision(), timeLeft);
            currentGameTime += updateTime;
            context.updatePositions(updateTime);
            if (collision.getTimeToCollision() != Shape.NO_COLLISION && collision.getTimeToCollision() <= timeLeft) {
                collisionTimes[back] = currentGameTime;
                double collisionRate;
                if (numCollision >= collisionTimes.length - 1) {
                    int front = (back + 1) % collisionTimes.length;
                    double dt = collisionTimes[back] - collisionTimes[front];
                    back = front;
                    collisionRate = collisionTimes.length / dt;
                } else {
                    double dt = collisionTimes[back] - collisionTimes[0];
                    back = (back + 1) % collisionTimes.length;
                    collisionRate = collisionTimes.length / dt;
                }
                context.handleCollision(collision, collisionRate);
            }
            timeLeft -= updateTime;
        }
        context.update(elapsedTime);
    }
}
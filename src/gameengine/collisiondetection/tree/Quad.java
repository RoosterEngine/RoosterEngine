package gameengine.collisiondetection.tree;

import gameengine.collisiondetection.Collision;
import gameengine.collisiondetection.shapes.Shape;
import gameengine.entities.Entity;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayDeque;
import java.util.Iterator;

/**
 * documentation
 * User: davidrusu
 * Date: 15/01/13
 * Time: 9:30 PM
 */
public class Quad extends Tree implements Parent {
    private static ArrayDeque<Quad> recycledQuads = new ArrayDeque<>();
    private Tree topLeft;
    private Tree topRight;
    private Tree bottomLeft;
    private Tree bottomRight;

    private Quad(Parent parent, double centerX, double centerY,
                 double halfLength) {
        super(parent, centerX, centerY, halfLength);
        initQuads();
    }

    private Quad(Parent parent, double centerX, double centerY,
                 double halfLength, Tree topLeft, Tree topRight,
                 Tree bottomLeft, Tree bottomRight) {
        super(parent, centerX, centerY, halfLength);
        initQuads(topLeft, topRight, bottomLeft, bottomRight);
    }

    public static Quad getInstance(Parent parent,
                                   double centerX, double centerY,
                                   double halfLength) {
        if (recycledQuads.isEmpty()) {
            return new Quad(parent, centerX, centerY, halfLength);
        }
        Quad quad = recycledQuads.pop();
        quad.init(parent, centerX, centerY, halfLength);
        quad.initQuads();
        return quad;
    }

    public static Quad getInstance(Parent parent, double centerX,
                                   double centerY, double halfLength,
                                   Tree topLeft, Tree topRight,
                                   Tree bottomLeft, Tree bottomRight) {
        if (recycledQuads.isEmpty()) {
            return new Quad(parent, centerX, centerY, halfLength,
                    topLeft, topRight, bottomLeft, bottomRight);
        }
        Quad quad = recycledQuads.pop();
        quad.init(parent, centerX, centerY, halfLength);
        quad.initQuads(topLeft, topRight, bottomLeft, bottomRight);
        return quad;
    }

    private void initQuads(Tree topLeft, Tree topRight,
                           Tree bottomLeft, Tree bottomRight) {
        this.topLeft = topLeft;
        topLeft.parent = this;
        this.topRight = topRight;
        topRight.parent = this;
        this.bottomLeft = bottomLeft;
        bottomLeft.parent = this;
        this.bottomRight = bottomRight;
        bottomRight.parent = this;
    }

    private void initQuads() {
        double quartLength = halfLength / 2;
        double left = centerX - quartLength;
        double right = centerX + quartLength;
        double top = centerY - quartLength;
        double bottom = centerY + quartLength;

        topLeft = Leaf.getInstance(this, left, top, quartLength);
        topRight = Leaf.getInstance(this, right, top, quartLength);
        bottomLeft = Leaf.getInstance(this, left, bottom, quartLength);
        bottomRight = Leaf.getInstance(this, right, bottom, quartLength);
    }

    @Override
    public void addEntity(Entity entity) {
        // TODO ensure calculateBoundingBox is called in the spatialTree addEntity method
        Shape shape = entity.getShape();
        if (shape.getMaxCollisionX() < centerX) {
            // left
            if (shape.getMaxCollisionY() < centerY) {
                // top
                addToTopLeft(entity);
                entityCount++;
                return;
            } else if (shape.getMinCollisionY() > centerY) {
                // bottom
                addToBottomLeft(entity);
                entityCount++;
                return;
            }
        } else if (shape.getMinCollisionX() > centerX) {
            // right
            if (shape.getMaxCollisionY() < centerY) {
                // top
                addToTopRight(entity);
                entityCount++;
                return;
            } else if (shape.getMinCollisionY() > centerY) {
                // bottom
                addToBottomRight(entity);
                entityCount++;
                return;
            }
        }
        // stays here
        addToThis(entity);
        entityCount++;
    }

    private void addToBottomRight(Entity entity) {
        bottomRight.addEntity(entity);
    }

    private void addToTopRight(Entity entity) {
        topRight.addEntity(entity);
    }

    private void addToBottomLeft(Entity entity) {
        bottomLeft.addEntity(entity);
    }

    private void addToTopLeft(Entity entity) {
        topLeft.addEntity(entity);
    }

    private void addToThis(Entity entity) {
        entities.add(entity);
        entity.setPartition(this);
    }

    @Override
    public boolean removeEntity(Entity entity) {
        Shape shape = entity.getShape();
        boolean removed = false;
        if (shape.getMaxCollisionX() < centerX) {
            // left
            if (shape.getMaxCollisionY() < centerY) {
                // top
                removed = topLeft.removeEntity(entity);
            } else if (shape.getMinCollisionY() > centerY) {
                // bottom
                removed = bottomLeft.removeEntity(entity);
            }
        } else if (shape.getMinCollisionX() > centerX) {
            // right
            if (shape.getMaxCollisionY() < centerY) {
                // top
                removed = topRight.removeEntity(entity);
            } else if (shape.getMinCollisionY() > centerY) {
                // bottom
                removed = bottomRight.removeEntity(entity);
            }
        }
        if (!removed) {
            removed = entities.remove(entity);
        }
        if (removed) {
            entityCount--;
            entity.setPartition(null);
        }
        return removed;
    }

    @Override
    public void ensureEntitiesAreContained(double time) {
        Iterator<Entity> iterator = entities.iterator();
        while (iterator.hasNext()) {
            Entity entity = iterator.next();
            Shape shape = entity.getShape();
            shape.calculateBoundingBox(time);

            if (shape.getMaxCollisionX() < centerX) { // left
                if (shape.getMinCollisionX() > minX) { // horizontally contained
                    if (shape.getMaxCollisionY() < centerY) { // top
                        if (shape.getMinCollisionY() > minY) {
                            addToTopLeft(entity);
                            iterator.remove();
                        } else {
                            parent.relocateUp(entity);
                            postRelocateRemove(entity, iterator);
                        }
                    } else if (shape.getMinCollisionY() > centerY) { // bottom
                        if (shape.getMaxCollisionY() < maxY) {
                            addToBottomLeft(entity);
                            iterator.remove();
                        } else {
                            parent.relocateDown(entity);
                            postRelocateRemove(entity, iterator);
                        }
                    }
                } else {
                    parent.relocateLeft(entity);
                    postRelocateRemove(entity, iterator);
                }
            } else if (shape.getMinCollisionX() > centerX) { // right
                if (shape.getMaxCollisionX() < maxX) { // horizontally contained
                    if (shape.getMaxCollisionY() < centerY) { // top
                        if (shape.getMinCollisionY() > minY) {
                            addToTopRight(entity);
                            iterator.remove();
                        } else {
                            parent.relocateUp(entity);
                            postRelocateRemove(entity, iterator);
                        }
                    } else if (shape.getMinCollisionY() > centerY) { // bottom
                        if (shape.getMaxCollisionY() < maxY) {
                            addToBottomRight(entity);
                            iterator.remove();
                        } else {
                            parent.relocateDown(entity);
                            postRelocateRemove(entity, iterator);
                        }
                    }
                } else {
                    parent.relocateRight(entity);
                    postRelocateRemove(entity, iterator);
                }
            } else if (shape.getMinCollisionY() <= minY) {
                parent.relocateUp(entity);
                postRelocateRemove(entity, iterator);
            } else if (shape.getMaxCollisionY() >= maxY) {
                parent.relocateDown(entity);
                postRelocateRemove(entity, iterator);
            }
        }
        topLeft.ensureEntitiesAreContained(time);
        topRight.ensureEntitiesAreContained(time);
        bottomLeft.ensureEntitiesAreContained(time);
        bottomRight.ensureEntitiesAreContained(time);
    }

    private void postRelocateRemove(Entity entity, Iterator<Entity> iterator) {
        iterator.remove();
        entityCount--;
    }

    @Override
    public Tree tryResize() {
        if (entityCount == 0) {
            Leaf leaf = Leaf.getInstance(
                    parent, centerX, centerY, halfLength);
            recycle();
            return leaf;
        }
        topLeft = topLeft.tryResize();
        topRight = topRight.tryResize();
        bottomLeft = bottomLeft.tryResize();
        bottomRight = bottomRight.tryResize();
        return this;
    }

    @Override
    public void updateEntities(double elapsedTime) {
        topLeft.updateEntities(elapsedTime);
        topRight.updateEntities(elapsedTime);
        bottomLeft.updateEntities(elapsedTime);
        bottomRight.updateEntities(elapsedTime);
        for (Entity entity : entities) {
            entity.update(elapsedTime);
        }
    }

    @Override
    public void updateEntityPositions(double elapsedTime) {
        topLeft.updateEntityPositions(elapsedTime);
        topRight.updateEntityPositions(elapsedTime);
        bottomLeft.updateEntityPositions(elapsedTime);
        bottomRight.updateEntityPositions(elapsedTime);
        for (Entity entity : entities) {
            entity.updatePosition(elapsedTime);
        }
    }

    @Override
    public void updateEntityMotions(double elapsedTime) {
        topLeft.updateEntityMotions(elapsedTime);
        topRight.updateEntityMotions(elapsedTime);
        bottomLeft.updateEntityMotions(elapsedTime);
        bottomRight.updateEntityMotions(elapsedTime);
        for (Entity entity : entities) {
            entity.updateMotion(elapsedTime);
        }
    }

    @Override
    public void calcCollision(int[] collisionGroups, Collision temp, Collision result) {
        topLeft.calcCollision(collisionGroups, temp, result);
        topRight.calcCollision(collisionGroups, temp, result);
        bottomLeft.calcCollision(collisionGroups, temp, result);
        bottomRight.calcCollision(collisionGroups, temp, result);

        checkEntityCollisions(collisionGroups, temp, result);
    }

    @Override
    public void checkForCollisionWithTree(Tree tree, int[] collisionGroups,
                                          Collision temp, Collision result) {
        for (int i = 0; i < entities.size(); i++) {
            Shape shape1 = entities.get(i).getShape();
            for (int j = 0; j < tree.entities.size(); j++) {
                Shape shape2 = tree.entities.get(j).getShape();

                int bitmap = collisionGroups[shape1.getCollisionType()];
                if ((bitmap & 1 << shape2.getCollisionType()) != 0) {
                    Shape.collideShapes(
                            shape1, shape2, result.getCollisionTime(), temp);
                    if (temp.getCollisionTime() < result.getCollisionTime()) {
                        result.set(temp);
                    }
                }
            }
        }
        parent.checkForCollisionWithTree(tree, collisionGroups, temp, result);
    }

    @Override
    public void recycle() {
        topLeft.recycle();
        topRight.recycle();
        bottomLeft.recycle();
        bottomRight.recycle();
        clear();
        recycledQuads.push(this);
    }

    @Override
    public void draw(Graphics2D g, Color color) {
        Color deeperColor = color;
        topLeft.draw(g, deeperColor);
        topRight.draw(g, deeperColor);
        bottomLeft.draw(g, deeperColor);
        bottomRight.draw(g, deeperColor);
        g.setColor(Color.DARK_GRAY);
        g.drawLine((int) minX, (int) centerY, (int) maxX, (int) centerY);
        g.drawLine((int) centerX, (int) minY, (int) centerX, (int) maxY);
        g.setColor(color);
        int offset = 20;
        int size = offset * 2;
        g.fillRect((int) centerX - offset, (int) centerY - offset, size, size);
        g.setColor(Color.WHITE);

        String string = "" + entityCount;
        FontMetrics metrics = g.getFontMetrics();
        Rectangle2D rect = metrics.getStringBounds(string, g);

        g.drawString(string,
                (int) (centerX - rect.getWidth() / 2),
                (int) (centerY));
    }

    @Override
    public void clear() {
        topLeft.clear();
        topRight.clear();
        bottomLeft.clear();
        bottomRight.clear();
        super.clear();
    }

    @Override
    public void relocateLeft(Entity entity) {
        Shape shape = entity.getShape();
        if (shape.getMaxCollisionX() < centerX) {
            // left
            if (shape.getMinCollisionX() > minX) {
                // contained horizontally
                if (shape.getMaxCollisionY() < centerY) {
                    // top
                    if (shape.getMinCollisionY() > minY) {
                        // contained
                        addToTopLeft(entity);
                    } else {
                        parent.relocateUp(entity);
                        entityCount--;
                    }
                } else if (shape.getMinCollisionY() > centerY) {
                    // bottom
                    if (shape.getMaxCollisionY() < maxY) {
                        // contained
                        addToBottomLeft(entity);
                    } else {
                        parent.relocateDown(entity);
                        entityCount--;
                    }
                } else {
                    addToThis(entity);
                }
            } else {
                parent.relocateLeft(entity);
                entityCount--;
            }
        } else {
            addToThis(entity);
        }
    }

    @Override
    public void relocateRight(Entity entity) {
        Shape shape = entity.getShape();
        if (shape.getMinCollisionX() > centerX) {
            // right
            if (shape.getMaxCollisionX() < maxX) {
                // contained horizontally
                if (shape.getMaxCollisionY() < centerY) {
                    // top
                    if (shape.getMinCollisionY() > minY) {
                        // contained
                        addToTopRight(entity);
                    } else {
                        parent.relocateUp(entity);
                        entityCount--;
                    }
                } else if (shape.getMinCollisionY() > centerY) {
                    // bottom
                    if (shape.getMaxCollisionY() < maxY) {
                        // contained
                        addToBottomRight(entity);
                    } else {
                        parent.relocateDown(entity);
                        entityCount--;
                    }
                } else {
                    addToThis(entity);
                }
            } else {
                parent.relocateRight(entity);
                entityCount--;
            }
        } else {
            addToThis(entity);
        }
    }

    @Override
    public void relocateUp(Entity entity) {
        Shape shape = entity.getShape();
        if (shape.getMaxCollisionY() < centerY) {
            // up
            if (shape.getMinCollisionY() > minY) {
                // contained vertically
                if (shape.getMaxCollisionX() < centerX) {
                    // left
                    if (shape.getMinCollisionX() > minX) {
                        // contained
                        addToTopLeft(entity);
                    } else {
                        parent.relocateLeft(entity);
                        entityCount--;
                    }
                } else if (shape.getMinCollisionX() > centerX) {
                    // right
                    if (shape.getMaxCollisionX() < maxX) {
                        // contained
                        addToTopRight(entity);
                    } else {
                        parent.relocateRight(entity);
                        entityCount--;
                    }
                } else {
                    addToThis(entity);
                }
            } else {
                parent.relocateUp(entity);
                entityCount--;
            }
        } else {
            addToThis(entity);
        }
    }

    @Override
    public void relocateDown(Entity entity) {
        Shape shape = entity.getShape();
        if (shape.getMinCollisionY() > centerY) {
            // down
            if (shape.getMaxCollisionY() < maxY) {
                // contained vertically
                if (shape.getMaxCollisionX() < centerX) {
                    // left
                    if (shape.getMinCollisionX() > minX) {
                        // contained
                        addToBottomLeft(entity);
                    } else {
                        parent.relocateLeft(entity);
                        entityCount--;
                    }
                } else if (shape.getMinCollisionX() > centerX) {
                    // right
                    if (shape.getMaxCollisionX() < maxX) {
                        // contained
                        addToBottomRight(entity);
                    } else {
                        parent.relocateRight(entity);
                        entityCount--;
                    }
                } else {
                    addToThis(entity);
                }
            } else {
                parent.relocateDown(entity);
                entityCount--;
            }
        } else {
            addToThis(entity);
        }
    }
}

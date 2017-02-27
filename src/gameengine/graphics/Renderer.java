package gameengine.graphics;

import gameengine.geometry.Vector2D;

import java.awt.*;

/**
 * The Renderer is responsible for drawing on the current frame.
 */
public abstract class Renderer {
    /**
     * Sets the foreground color.
     *
     * @param color The foreground color
     */
    public final void setForegroundColor(RColor color) {
        setForegroundColor(color.getRed(), color.getGreen(), color.getBlue());
    }

    /**
     * Sets the foreground color.
     *
     * @param red   The red component
     * @param green The green component
     * @param blue  The blue component
     */
    public abstract void setForegroundColor(float red, float green, float blue);

    /**
     * Sets the background color.
     *
     * @param color The background color
     */
    public final void setBackgroundColor(RColor color) {
        setBackgroundColor(color.getRed(), color.getGreen(), color.getBlue());
    }

    /**
     * Sets the background color.
     *
     * @param red   The red component
     * @param green The green component
     * @param blue  The blue component
     */
    public abstract void setBackgroundColor(float red, float green, float blue);

    /**
     * Controls which elements are drawn in front of others.  Elements with a greater index are
     * drawn behind elements with a smaller index.
     *
     * @param index the Z index
     */
    public abstract void setZIndex(float index);

    /**
     * Scales the screen (eg. zoom).
     *
     * @param scale The scaling factor
     */
    public abstract void scale(double scale);

    /**
     * Sets the line width.
     *
     * @param width The line width
     */
    public abstract void setLineWidth(double width);

    /**
     * Rotates the screen about the specified point.
     *
     * @param radians The number of radians to rotate
     * @param about   The point to rotate about
     */
    public final void rotate(double radians, Vector2D about) {
        rotate(radians, about.getX(), about.getY());
    }

    /**
     * Rotates the screen about the specified point.
     *
     * @param radians The number of radians to rotate
     * @param aboutX  The X component of the point to rotate about
     * @param aboutY  The Y component of the point to rotate about
     */
    public abstract void rotate(double radians, double aboutX, double aboutY);

    public final void translate(Vector2D vec) {
        translate(vec.getX(), vec.getY());
    }

    /**
     * Translates the rendering by the provided offsets relative to the current position.
     *
     * @param dx The horizontal delta
     * @param dy The vertical delta
     */
    public abstract void translate(double dx, double dy);

    /**
     * Sets the size of points.
     *
     * @param size The point size
     */
    public abstract void setPointSize(double size);

    /**
     * Draws the specified string at the specified location.
     *
     * @param text     The string to draw
     * @param location The location
     */
    public final void drawString(String text, Vector2D location) {
        drawString(text, location.getX(), location.getY());
    }

    /**
     * Draws the strings in order using the specified offset and padding.
     *
     * @param xOffset The distance from the left side of the screen
     * @param yOffset The distance from the top of the screen for the first string
     * @param padding The vertical padding between strings
     * @param strings The strings to be drawn
     */
    public final void drawStrings(double xOffset, double yOffset, double padding, String...
            strings) {
        for (int i = 0; i < strings.length; i++) {
            drawString(strings[i], xOffset, yOffset);
            yOffset += padding;
        }
    }

    /**
     * Draws the specified string at the specified location.
     *
     * @param text The string to draw
     * @param x    The X component of the location
     * @param y    The Y component of the location
     */
    public abstract void drawString(String text, double x, double y);

    /**
     * @return The font metrics of the current font.
     */
    public abstract FontMetrics getFontMetrics();

    /**
     * Draws a point at the specified location.
     *
     * @param location The location where the point should be drawn
     */
    public final void drawPoint(Vector2D location) {
        drawPoint(location.getX(), location.getY());
    }

    /**
     * Draws a point at the specified location
     *
     * @param x The X component of the location
     * @param y The Y component of the location
     */
    public abstract void drawPoint(double x, double y);

    /**
     * Draws points at each of the specified locations in the array.
     *
     * @param points The locations where the points should be drawn
     */
    public abstract void drawPoints(Vector2D[] points);

    /**
     * Draws a line from point 1 to point 2.
     *
     * @param p1 point 1
     * @param p2 point 2
     */
    public final void drawLine(Vector2D p1, Vector2D p2) {
        drawLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());
    }

    /**
     * Draws a line from point 1 to point 2.
     *
     * @param x1 The X component of point 1
     * @param y1 The Y component of point 1
     * @param x2 The X component of point 2
     * @param y2 The Y component of point 2
     */
    public abstract void drawLine(double x1, double y1, double x2, double y2);

    /**
     * Draws a line strip connecting the points.
     *
     * @param points The points
     * @param offset The offset to be added to each point
     */
    public final void drawLineStrip(Vector2D[] points, Vector2D offset) {
        drawLineStrip(points, offset.getX(), offset.getY());
    }

    /**
     * Draws a line strip connecting the points.
     *
     * @param points  The points
     * @param xOffset The X component of the offset to be added to each point
     * @param yOffset The Y component of the offset to be added to each point
     */
    public abstract void drawLineStrip(Vector2D[] points, double xOffset, double yOffset);

    /**
     * Draws a rectangle.
     *
     * @param centerLocation The center of the rectangle
     * @param halfWidth      Half of the width
     * @param halfHeight     Half of the height
     */
    public final void drawRect(Vector2D centerLocation, double halfWidth, double halfHeight) {
        drawRect(centerLocation.getX(), centerLocation.getY(), halfWidth, halfHeight);
    }

    /**
     * Draws a rectangle.
     *
     * @param centerX    The X component of the center of the rectangle
     * @param centerY    The Y component of the center of the rectangle
     * @param halfWidth  Half of the width
     * @param halfHeight Half of the height
     */
    public abstract void drawRect(double centerX, double centerY, double halfWidth, double
            halfHeight);

    /**
     * Fills a rectangle.
     *
     * @param centerLocation The center location of the rectangle
     * @param halfWidth      Half of the width
     * @param halfHeight     Half of the height
     */
    public final void fillRect(Vector2D centerLocation, double halfWidth, double halfHeight) {
        fillRect(centerLocation.getX(), centerLocation.getY(), halfWidth, halfHeight);
    }

    /**
     * Fills a rectangle.
     *
     * @param centerX    The X component of the center location of the rectangle
     * @param centerY    The Y component of the center location of the rectangle
     * @param halfWidth  Half of the width
     * @param halfHeight Half of the heigth
     */
    public abstract void fillRect(double centerX, double centerY, double halfWidth, double
            halfHeight);

    /**
     * Draws a circle.
     *
     * @param centerLocation The center location
     * @param radius         The radius of the circle
     */
    public final void drawCircle(Vector2D centerLocation, double radius) {
        drawCircle(centerLocation.getX(), centerLocation.getY(), radius);
    }

    /**
     * Draws a circle.
     *
     * @param centerX The X component of the center location
     * @param centerY The Y component of the center location
     * @param radius  The radius of the circle
     */
    public abstract void drawCircle(double centerX, double centerY, double radius);

    /**
     * Fills a circle at the specified location.
     *
     * @param centerLocation The center location
     * @param radius         The radius of the circle
     */
    public final void fillCircle(Vector2D centerLocation, double radius) {
        fillCircle(centerLocation.getX(), centerLocation.getY(), radius);
    }

    /**
     * Fills a circle at the specified location.
     *
     * @param centerX The X component of the center location
     * @param centerY The Y component of the center location
     * @param radius  The radius of the circle
     */
    public abstract void fillCircle(double centerX, double centerY, double radius);

    /**
     * Draws an oval at the specified location.
     *
     * @param centerLocation The center location
     * @param width          The width of the oval
     * @param height         The height of the oval
     */
    public final void drawOval(Vector2D centerLocation, double width, double height) {
        drawOval(centerLocation.getX(), centerLocation.getY(), width, height);
    }

    /**
     * Draws an oval at the specified location.
     *
     * @param centerX The X component of the center location
     * @param centerY The Y component of the center location
     * @param width   The width of the oval
     * @param height  The height of the oval
     */
    public abstract void drawOval(double centerX, double centerY, double width, double height);

    /**
     * Fills an oval at the specified location.
     *
     * @param centerLocation The center location
     * @param width          The width of the oval
     * @param height         The height of the oval
     */
    public final void fillOval(Vector2D centerLocation, double width, double height) {
        fillOval(centerLocation.getX(), centerLocation.getY(), width, height);
    }

    /**
     * Fills an oval at the specified location.
     *
     * @param centerX The X component of the center location
     * @param centerY The Y component of the center location
     * @param width   The width of the oval
     * @param height  The height of the oval
     */
    public abstract void fillOval(double centerX, double centerY, double width, double height);

    /**
     * Draws a polygon.
     *
     * @param points The vertices of the polygon
     * @param offset The offset to be added to each point
     */
    public final void drawPolygon(Vector2D[] points, Vector2D offset) {
        drawPolygon(points, offset.getX(), offset.getY());
    }

    /**
     * Draws a polygon.
     *
     * @param points  The vertices of the polygon
     * @param offsetX The X component of the offset to be added to each point
     * @param offsetY The Y component of the offset to be added to each point
     */
    public abstract void drawPolygon(Vector2D[] points, double offsetX, double offsetY);

    /**
     * Fills a polygon.
     *
     * @param points The vertices of the polygon
     * @param offset The offset to be added to each point
     */
    public final void fillPolygon(Vector2D[] points, Vector2D offset) {
        fillPolygon(points, offset.getX(), offset.getY());
    }

    /**
     * Fills a polygon.
     *
     * @param points  The vertices of the polygon
     * @param offsetX The X component of the offset to be added to each point
     * @param offsetY The Y component of the offset to be added to each point
     */
    public abstract void fillPolygon(Vector2D[] points, double offsetX, double offsetY);
}

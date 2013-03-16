package gameengine.physics;

import gameengine.graphics.Graphic;
import gameengine.math.Vector2D;

/**
 * Created with IntelliJ IDEA.
 * User: Core2Quad
 * Date: 03/03/13
 * Time: 11:44 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ScreenRenderer {

    public void setForegroundColour(float red, float green, float blue);

    public void setBackgroundColour(float red, float green, float blue);

    public void setScale(double scale);

    public void setLineWidth(double width);

    public void setPointSize(double size);

    public void setScreenCenter(double centerX, double centerY);

    public void rotate(double degrees, double aboutX, double aboutY);

    public void translate(double x, double y);

    public void drawString(String s, double x, double y);

    public void drawPoint(double x, double y);

    public void drawPoints(Vector2D[] points);

    public void drawLine(double x1, double y1, double x2, double y2);

    public void drawLineStrip(Vector2D[] points);

    public void drawGraphic(Graphic graphic, double x, double y, double width, double height);

    public void drawRect(double x, double y, double width, double height);

    public void fillRect(double x, double y, double width, double height);

    //The circle looks smooth when numSegments = 1 + (int)(9 * Math.log(radius)) but log is a slow operation
    public void drawCircle(double x, double y, double radius, int numSegments);

    //The circle looks smooth when numSegments = 1 + (int)(9 * Math.log(radius)) but log is a slow operation
    public void fillCircle(double x, double y, double radius, int numSegments);

    public void drawPolygon(Vector2D[] points);

    public void fillPolygon(Vector2D[] points);
}

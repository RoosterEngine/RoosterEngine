package gameengine.entities;

import gameengine.collisiondetection.shapes.AABBShape;
import gameengine.collisiondetection.shapes.CircleShape;
import gameengine.graphics.Graphic;
import gameengine.graphics.SolidColorGraphic;

import java.awt.*;

/**
 * @author davidrusu
 */
public class BasicButton extends Entity {
    private String text;
    private boolean selected = false, isPressed = false;
    private Graphic upGraphic, pressedGraphic, selectedGraphic, currentGraphic;
    private double initialX, initialY;

    public BasicButton(String text) {
        this(text, new SolidColorGraphic(new Color(177, 70, 35), 0, 0),
                new SolidColorGraphic(new Color(62, 28, 51), 0, 0),
                new SolidColorGraphic(new Color(96, 39, 73), 0, 0));
    }

    public BasicButton(String text, Graphic upGraphic, Graphic downGraphic, Graphic selectedGraphic) {
        super(0, 0, 0, 0, new CircleShape(0, 0, 0));
        this.text = text;
        this.upGraphic = upGraphic;
        this.pressedGraphic = downGraphic;
        this.selectedGraphic = selectedGraphic;
        currentGraphic = upGraphic;
    }

    public void initialize(double x, double y, double width, double height) {
        resetMotion();
        this.x = x;
        this.y = y;
        initialX = x;
        initialY = y;
        setWidth(width);
        setHeight(height);
        upGraphic.resize((int) width, (int) height);
        pressedGraphic.resize((int) width, (int) height);
        selectedGraphic.resize((int) width, (int) height);
        setShape(new AABBShape(x, y, width, height));
    }

    public void select() {
        currentGraphic = selectedGraphic;
        currentGraphic.reset();
        selected = true;
    }

    public void deSelect() {
        currentGraphic = upGraphic;
        currentGraphic.reset();
        selected = false;
    }

    public boolean isPressed() {
        return isPressed;
    }

    public void setPressed() {
        currentGraphic = pressedGraphic;
        currentGraphic.reset();
        isPressed = true;
    }

    public void setUnpressed() {
        if (selected) {
            currentGraphic = selectedGraphic;
        } else {
            currentGraphic = upGraphic;
        }
        currentGraphic.reset();
        isPressed = false;
    }

    @Override
    public void update(double elapsedTime) {
        currentGraphic.update(elapsedTime);
    }

    public void reset() {
        resetMotion();
        x = initialX;
        y = initialY;
        dx = 0;
        dy = 0;
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public boolean contains(double x, double y) {
        double leftEdge = this.x - width / 2;
        double topEdge = this.y - height / 2;
        return x >= leftEdge && x <= leftEdge + width && y >= topEdge && y <= topEdge + height;
    }

    @Override
    public void draw(Graphics2D g) {
        currentGraphic.draw(g, (int) (x - width / 2), (int) (y - height / 2));
        FontMetrics metrics = g.getFontMetrics();
        int textWidth = metrics.stringWidth(text);
        int textHeight = metrics.getHeight();
        g.setColor(Color.WHITE);
        double xPadding = (width - textWidth) * 0.5;
        double yPadding = (height + textHeight / 2) * 0.5;
        g.drawString(text, (int) (x - width / 2 + xPadding), (int) (y - height / 2 + yPadding));
    }
}

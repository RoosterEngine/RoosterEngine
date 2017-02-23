package gameengine.entities;

import gameengine.collisiondetection.shapes.AABBShape;
import gameengine.collisiondetection.shapes.CircleShape;
import gameengine.graphics.MutableColor;
import gameengine.graphics.Renderer;
import gameengine.graphics.image.SolidColorGraphic;
import gameengine.physics.Material;

import java.awt.*;

/**
 * @author davidrusu
 */
public class BasicButton extends Entity {
    private final MutableColor WHITE = MutableColor.createWhiteInstance();

    private String text;
    private boolean selected = false, isPressed = false;
    private SolidColorGraphic upGraphic, pressedGraphic, selectedGraphic, currentGraphic;
    private double initialX, initialY;

    public BasicButton(String text) {
        this(text, new SolidColorGraphic(new MutableColor(177, 70, 35), 0, 0), new
                SolidColorGraphic(new MutableColor(62, 28, 51), 0, 0), new SolidColorGraphic(new
                MutableColor(96, 39, 73), 0, 0));
    }

    public BasicButton(String text, SolidColorGraphic upGraphic, SolidColorGraphic downGraphic,
                       SolidColorGraphic selectedGraphic) {
        super(0, 0, Material.getDefaultMaterial(), new CircleShape(0));
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
        upGraphic.resize((int) width, (int) height);
        pressedGraphic.resize((int) width, (int) height);
        selectedGraphic.resize((int) width, (int) height);
        setShape(new AABBShape(width, height));
//        updateMass();
        mass = 1;
    }

    public void select() {
        currentGraphic = selectedGraphic;
        selected = true;
    }

    public void deSelect() {
        currentGraphic = upGraphic;
        selected = false;
    }

    public boolean isPressed() {
        return isPressed;
    }

    public void setPressed() {
        currentGraphic = pressedGraphic;
        isPressed = true;
    }

    public void setUnpressed() {
        if (selected) {
            currentGraphic = selectedGraphic;
        } else {
            currentGraphic = upGraphic;
        }
        isPressed = false;
    }

    @Override
    public void update(double elapsedTime) {
    }

    public void reset() {
        resetMotion();
        x = initialX;
        y = initialY;
        dx = 0;
        dy = 0;
    }

    public boolean contains(double x, double y) {
        double width = getWidth();
        double height = getHeight();
        double leftEdge = this.x - width / 2;
        double topEdge = this.y - height / 2;
        return x >= leftEdge && x <= leftEdge + width && y >= topEdge && y <= topEdge + height;
    }

    @Override
    public void draw(Renderer renderer) {
        double width = getWidth();
        double height = getHeight();
        currentGraphic.draw(renderer, x, y);
        FontMetrics metrics = renderer.getFontMetrics();
        int textWidth = metrics.stringWidth(text);
        int textHeight = metrics.getHeight();
        renderer.setForegroundColor(WHITE);
        double xPadding = (width - textWidth) * 0.5;
        double yPadding = (height + textHeight / 2) * 0.5;
        renderer.drawString(text, x - width / 2 + xPadding, y - height / 2 + yPadding);
    }
}

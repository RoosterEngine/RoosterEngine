package gameengine;

import gameengine.effects.Effect;
import gameengine.effects.Effectable;
import gameengine.effects.NoEffect;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

/**
 *
 * @author davidrusu
 */
public class BasicButton implements Effectable{
    private String text;
    private double width, height, xPadding, yPadding;
    private double padding = 0.5;
    private boolean selected = false, isPressed = false;
    private Graphic upGraphic, pressedGraphic, selectedGraphic, currentGraphic;
    private Effect effect;
    
    public BasicButton(String text){
        this(text, new SolidColorGraphic(new Color(205, 179, 128), 0, 0),
                   new SolidColorGraphic(new Color(103, 54, 73), 0, 0),
                   new SolidColorGraphic(new Color(3, 101, 100), 0, 0));
    }
    
    public BasicButton(String text, Graphic upGraphic, Graphic downGraphic, Graphic selectedGraphic){
        this.text = text;
        this.upGraphic = upGraphic;
        this.pressedGraphic = downGraphic;
        this.selectedGraphic = selectedGraphic;
        currentGraphic = upGraphic;
        xPadding = (int)(width * padding);
        yPadding = (int)(height * padding);
        effect = new NoEffect(0, 0);
    }
    
    public void initialize(double x, double y, double width, double height){
        effect.reset(x, y);
        this.width = width;
        this.height = height;
        xPadding = (int)(width * padding);
        yPadding = (int)(height * padding);
        upGraphic.resize((int)width, (int)height);
        pressedGraphic.resize((int)width, (int)height);
        selectedGraphic.resize((int)width, (int)height);
        
    }
    
    public void setPosition(double x, double y){
        effect.reset(x, y);
    }
    
    public void select(){
        currentGraphic = selectedGraphic;
        currentGraphic.reset();
        selected = true;
    }
    
    public void deSelect(){
        currentGraphic = upGraphic;
        currentGraphic.reset();
        selected = false;
    }
    
    public boolean isPressed(){
        return isPressed;
    }
    
    public void setPressed(){
        currentGraphic = pressedGraphic;
        currentGraphic.reset();
        isPressed = true;
    }
    
    public void setUnpressed(){
        if(selected){
            currentGraphic = selectedGraphic;
        }else{
            currentGraphic = upGraphic;
        }
        currentGraphic.reset();
        isPressed = false;
    }
    
    public void update(double elapsedTime){
        currentGraphic.update(elapsedTime);
        effect.update(elapsedTime);
    }
    
    public void reset(){
        effect.reset();
    }

    @Override
    public double getX() {
        return effect.getX();
    }

    @Override
    public double getY() {
        return effect.getY();
    }

    @Override
    public double getWidth() {
        return width;
    }

    @Override
    public double getHeight() {
        return height;
    }

    @Override
    public Effect getCurrentEffect() {
        return effect;
    }

    @Override
    public void setEffect(Effect effect) {
        this.effect = effect;
    }
    
    public boolean contains(double x, double y){
        double leftEdge = effect.getX() - width / 2;
        double topEdge = effect.getY() - height / 2;
        return x >= leftEdge && x <= leftEdge + width && y >= topEdge && y <= topEdge + height;
    }
    
    public void draw(Graphics2D g){
        double x = effect.getX();
        double y = effect.getY();
        currentGraphic.draw(g, (int)(x - width / 2), (int)(y - height / 2));
        FontMetrics metrics = g.getFontMetrics();
        int textWidth = metrics.stringWidth(text);
        int textHeight = metrics.getHeight();
        g.setColor(Color.WHITE);
        double xPadding = (width - textWidth) * 0.5;
        double yPadding = (height + textHeight / 2) * 0.5;
        g.drawString(text, (int)(x - width / 2 + xPadding), (int)(y - height / 2 + yPadding));
    }
}
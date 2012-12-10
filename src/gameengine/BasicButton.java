package gameengine;

import gameengine.effects.StationaryEffect;
import gameengine.effects.PositionEffect;
import gameengine.effects.Effectable;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

/**
 *
 * @author davidrusu
 */
public class BasicButton implements Effectable{
    private String text;
    private double width, height;
    private double padding = 0.5;
    private boolean selected = false, isPressed = false;
    private Graphic upGraphic, pressedGraphic, selectedGraphic, currentGraphic;
    private PositionEffect positionEffect;
    
    public BasicButton(String text){
        this(text, new SolidColorGraphic(new Color(177,70,35), 0, 0),
                   new SolidColorGraphic(new Color(62,28,51), 0, 0),
                   new SolidColorGraphic(new Color(96,39,73), 0, 0));
    }
    
    public BasicButton(String text, Graphic upGraphic, Graphic downGraphic, Graphic selectedGraphic){
        this.text = text;
        this.upGraphic = upGraphic;
        this.pressedGraphic = downGraphic;
        this.selectedGraphic = selectedGraphic;
        currentGraphic = upGraphic;
        positionEffect = new StationaryEffect(0, 0);
    }
    
    public void initialize(double x, double y, double width, double height){
        positionEffect.reset(x, y);
        this.width = width;
        this.height = height;
        upGraphic.resize((int)width, (int)height);
        pressedGraphic.resize((int)width, (int)height);
        selectedGraphic.resize((int)width, (int)height);
        
    }
    
    public void setPosition(double x, double y){
        positionEffect.reset(x, y);
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
        positionEffect.update(elapsedTime);
    }
    
    public void reset(){
        positionEffect.reset();
    }

    @Override
    public double getX() {
        return positionEffect.getX();
    }

    @Override
    public double getY() {
        return positionEffect.getY();
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
    public PositionEffect getCurrentEffect() {
        return positionEffect;
    }

    @Override
    public void setPositionEffect(PositionEffect positionEffect) {
        this.positionEffect = positionEffect;
    }
    
    public boolean contains(double x, double y){
        double leftEdge = positionEffect.getX() - width / 2;
        double topEdge = positionEffect.getY() - height / 2;
        return x >= leftEdge && x <= leftEdge + width && y >= topEdge && y <= topEdge + height;
    }
    
    public void draw(Graphics2D g){
        double x = positionEffect.getX();
        double y = positionEffect.getY();
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
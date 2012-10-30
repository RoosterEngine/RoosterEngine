package gameengine;

import bricklets.WavyGraphic;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 *
 * @author davidrusu
 */
public class BasicButton {
    
    private String text;
    private int x, y, width, height, xPadding, yPadding;
    private double padding = 0.5;
    private boolean selected = false, isPressed = false;
    private Graphic upGraphic, pressedGraphic, selectedGraphic, currentGraphic;
    
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
    }
    
    public void setDimensions(int x, int y, int width, int height){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        
        xPadding = (int)(width * padding);
        yPadding = (int)(height * padding);
        
        upGraphic.resize(width, height);
        pressedGraphic.resize(width, height);
        selectedGraphic.resize(width, height);
        
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
    }
    
    public boolean contains(double x, double y){
        return x >= this.x && x <= this.x + width && y >= this.y && y <= this.y + this.height;
    }
    
    public void draw(Graphics2D g){
        currentGraphic.draw(g, x, y);
        g.setColor(Color.WHITE);
        g.drawString(text, x + xPadding, y + yPadding);
    }
}
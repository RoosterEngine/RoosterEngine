/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gameengine.effects;

import bricklets.Physics;
import gameengine.GameController;

/**
 *
 * @author davidrusu
 */
public class EffectFactory {
    
    private EffectFactory(){}
    
    /**
     * 
     * @param numElements
     * @param initialX
     * @param destinationX
     * @param initialY
     * @param speedMultiplier set 1 for normal speed, 0.5 for half speed etc..
     * @return 
     */
    public static void setCurtainEffect(Effectable[] effectables, double startX, double destinationX, double speedMultiplier){
        double k = 0.0002 * speedMultiplier;
        double dampening = 0.6;
        for(int i = 0; i < effectables.length; i++){
            k *= 0.7;
            Effectable effectable = effectables[i];
            effectable.setEffect(new HorizontalSlide(startX, effectable.getY(), destinationX, new SpringMotion(k, dampening)));
        }
    }
    
    
    
    public static void setIntersectingEffect(Effectable[] effectables, double destinationX, double width, double speedMultiplier){
        double k = 0.0001 * speedMultiplier;
        double dampening = 0.8;
        for(int i = 0; i < effectables.length; i++){
            double initialX;
            Effectable effectable = effectables[i];
            if(i % 2 == 0){ // coming in from right
                initialX = effectable.getWidth() / 2 + width;
            }else{ // coming in from left
                initialX = -effectable.getWidth() / 2;
            }
            effectable.setEffect(new HorizontalSlide(initialX, effectable.getY(), destinationX, new SpringMotion(k, dampening)));
        }
    }
    
    public static void setZipperEffect(Effectable[] effectables, double destinationX, double width, double speedMultiplier){
        double speed = 3.5 * speedMultiplier;
        for(int i = 0; i < effectables.length; i++){
            double initialX;
            Effectable effectable = effectables[i];
            if(i % 2 == 0){ // coming in from right
                initialX = effectable.getWidth() / 2 * (i + 1) * 0.5+ width;
            }else{ // coming in from left
                initialX = -effectable.getWidth() / 2 * (i + 1) * 0.5;
            }
            effectable.setEffect(new HorizontalSlide(initialX, effectable.getY(), destinationX, new LinearMotion(speed)));
        }
    }
    
    public static void setCurtainDropEffect(Effectable[] effectables, double speedMultiplier){
        double k = 0.0001 * speedMultiplier;
        double dampening = 0.5;
        double decay = 1;
        for(int i = 0; i < effectables.length; i++){
            Effectable effectable = effectables[i];
            double initialY = -effectable.getHeight() / 2;
            effectable.setEffect(new VerticalSlide(effectable.getX(), initialY, effectable.getY(), new SpringMotion(k, dampening)));
            k *= decay;
        }
    }
    
    public static void setSlideDownEffect(Effectable[] effectables, double speedMultiplier){
        double k = 0.00005 * speedMultiplier;
        double dampening = 0.5;
        double decay = 1;
        for(int i = 0; i < effectables.length; i++){
            Effectable effectable = effectables[effectables.length - i - 1];
            double initialY = -effectable.getHeight() / 2 * (i + 1) * 5;
            effectable.setEffect(new VerticalSlide(effectable.getX(), initialY, effectable.getY(), new SpringMotion(k, dampening)));
            k *= decay;
        }
    }
    
    /**
     * Assuming the position of the effectable is also the center of the effectable
     * @param destinationX
     * @param y
     * @param k
     * @param dampeningRatio
     * @param controller
     * @return 
     */
    public static void setSlideFromRightEffect(Effectable effectable, double destinationX, double speedMultiplier, GameController controller){
        effectable.setEffect(new HorizontalSlide(controller.getWidth() + effectable.getWidth() / 2, effectable.getY(), destinationX, new SpringMotion(0.00001 * speedMultiplier, 1)));
    }
    
//    public static Effect createSlideLeftEffect(double destinationX, double y, double k, double dampening, GameController controller){
//        
//    }
//    
//    public static Effect createSlideRightEffect(){
//        
//    }
//    
//    public static Effect createSlideUpEffect(){
//        
//    }
//    
//    public static Effect createSlideDownEffect(){
//        
//    }
//    
//    public static Effect createDirectionalSlideEffect(){
//        
//    }
}

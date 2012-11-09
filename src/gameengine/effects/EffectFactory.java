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
    public static void setCurtainEffect(Effectable[] effectables, double destinationX, double speedMultiplier){
        HorizontalSlide[] effects = new HorizontalSlide[effectables.length];
        double k = 0.0002 * speedMultiplier;
        double dampening = 0.6;
        for(int i = 0; i < effects.length; i++){
            k *= 0.7;
            Effectable effectable = effectables[i];
            effectable.setEffect(new HorizontalSlide(effectable.getX(), effectable.getY(), destinationX, new SpringMotion(k, dampening)));
//            effectable.setEffect(new HorizontalSlide(effectable.getX(), effectable.getY(), destinationX, new LinearMotion(1)));
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

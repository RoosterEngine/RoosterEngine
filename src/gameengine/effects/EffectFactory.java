package gameengine.effects;

import gameengine.GameController;

/**
 *
 * @author davidrusu
 */
public class EffectFactory {
    
    private EffectFactory(){}

    public static void setCurtainEffect(Effectable[] effectables, double startX, double destinationX,
                                        double speedMultiplier){
        double k = 0.0002 * speedMultiplier;
        double dampening = 0.6;
        for (Effectable effectable1 : effectables) {
            k *= 0.7;
            Effectable effectable = effectable1;
            effectable.setPositionEffect(new HorizontalSlide(startX, effectable.getY(), destinationX,
                    new SpringMotion(k, dampening)));
        }
    }

    public static void setIntersectingEffect(Effectable[] effectables, double destinationX, double width,
                                             double speedMultiplier){
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
            effectable.setPositionEffect(new HorizontalSlide(initialX, effectable.getY(), destinationX,
                    new SpringMotion(k, dampening)));
        }
    }
    
    public static void setZipperEffect(Effectable[] effectables, double destinationX, double width,
                                       double speedMultiplier){
        double speed = 3.5 * speedMultiplier;
        for(int i = 0; i < effectables.length; i++){
            double initialX;
            Effectable effectable = effectables[i];
            if(i % 2 == 0){ // coming in from right
                initialX = effectable.getWidth() / 2 * (i + 1) * 0.5+ width;
            }else{ // coming in from left
                initialX = -effectable.getWidth() / 2 * (i + 1) * 0.5;
            }
            effectable.setPositionEffect(new HorizontalSlide(initialX, effectable.getY(), destinationX,
                    new LinearMotion(speed)));
        }
    }
    
    public static void setCurtainDropEffect(Effectable[] effectables, double speedMultiplier){
        double k = 0.0001 * speedMultiplier;
        double dampening = 0.5;
        double decay = 1;
        for (Effectable effectable : effectables) {
            double initialY = -effectable.getHeight() / 2;
            effectable.setPositionEffect(new VerticalSlide(effectable.getX(), initialY, effectable.getY(),
                    new SpringMotion(k, dampening)));
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
            effectable.setPositionEffect(new VerticalSlide(effectable.getX(), initialY, effectable.getY(),
                    new SpringMotion(k, dampening)));
            k *= decay;
        }
    }

    public static void setSlideFromRightEffect(Effectable effectable, double destinationX,
                                               double speedMultiplier, GameController controller){
        effectable.setPositionEffect(
                new HorizontalSlide(controller.getWidth() + effectable.getWidth() / 2, effectable.getY(), destinationX,
                        new SpringMotion(0.00001 * speedMultiplier, 1)));
    }
}

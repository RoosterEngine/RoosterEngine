package gameengine.effects;

import bricklets.Entity;
import gameengine.GameController;

/**
 *
 * @author davidrusu
 */
public class EffectFactory {
    
    private EffectFactory(){}

    public static void setCurtainEffect(Entity[] entitys, double startX, double destinationX, double speedMultiplier){
        double k = 0.0002 * speedMultiplier;
        double dampening = 0.6;
        for (Entity entity : entitys) {
            k *= 0.7;
            entity.setPosition(startX, entity.getY());
            entity.setMotionEffect(new HorizontalSpring(entity, destinationX, new SpringMotion(k, dampening)));
        }
    }

    public static void setIntersectingEffect(Entity[] entitys, double destinationX, double width,
                                             double speedMultiplier){
        double k = 0.0001 * speedMultiplier;
        double dampening = 0.8;
        for(int i = 0; i < entitys.length; i++){
            double initialX;
            Entity entity = entitys[i];
            if(i % 2 == 0){ // coming in from right
                initialX = entity.getWidth() / 2 + width;
            }else{ // coming in from left
                initialX = -entity.getWidth() / 2;
            }
            entity.setPosition(initialX, entity.getY());
            entity.setMotionEffect(new HorizontalSpring(entity, destinationX, new SpringMotion(k, dampening)));
        }
    }
    
    public static void setZipperEffect(Entity[] entities, double destinationX, double width, double speedMultiplier){
        double speed = 3.5 * speedMultiplier;
        for(int i = 0; i < entities.length; i++){
            double initialX;
            Entity entity = entities[i];
            if(i % 2 == 0){ // coming in from right
                initialX = entity.getWidth() / 2 * (i + 1) + width;
            }else{ // coming in from left
                initialX = -entity.getWidth() / 2 * (i + 1) * 0.5 - width;
            }
            entity.setPosition(initialX, entity.getY());
            entity.setMotionEffect(new HorizontalSpring(entity, destinationX, new LinearMotion(speed)));
//            entity.setMotionEffect(new HorizontalSpring(entity, destinationX, new SpringMotion(0.0001, 0.7)));
        }
    }
    
    public static void setCurtainDropEffect(Entity[] entitys, double speedMultiplier){
        double k = 0.0001 * speedMultiplier;
        double dampening = 0.5;
        double decay = 1;
        for (Entity entity : entitys) {
            double initialY = -entity.getHeight() / 2;
            entity.setMotionEffect(new VerticalSpring(entity, entity.getY(), new SpringMotion(k, dampening)));
            entity.setPosition(entity.getX(), initialY);
            k *= decay;
        }
    }
    
    public static void setSlideDownEffect(Entity[] entitys, double speedMultiplier){
        double k = 0.00005 * speedMultiplier;
        double dampening = 0.5;
        double decay = 1;
        for(int i = 0; i < entitys.length; i++){
            Entity entity = entitys[entitys.length - i - 1];
            double initialY = -entity.getHeight() / 2 * (i + 1) * 5;
            entity.setMotionEffect(new VerticalSpring(entity, entity.getY(), new SpringMotion(k, dampening)));
            entity.setPosition(entity.getX(), initialY);
            k *= decay;
        }
    }

    public static void setSlideFromRightEffect(Entity entity, double destinationX,
                                               double speedMultiplier, GameController controller){
        entity.setMotionEffect(
                new HorizontalSpring(entity, destinationX, new SpringMotion(0.00001 * speedMultiplier, 1)));
        entity.setPosition(controller.getWidth() + entity.getWidth() / 2, entity.getY());
    }
}

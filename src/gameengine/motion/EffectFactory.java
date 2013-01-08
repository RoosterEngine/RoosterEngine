package gameengine.motion;

import bricklets.Entity;
import gameengine.motion.motions.HorizontalAttractMotion;

public class EffectFactory {

    private EffectFactory() {
    }

    public static void setCurtainEffect(Entity[] entitys, double startX, double destinationX, double speedMultiplier) {
        double k = 0.0002 * speedMultiplier;
        double dampening = 0.6;
        for (Entity entity : entitys) {
            k *= 0.7;
            entity.setPosition(startX, entity.getY());
            entity.setMotion(new HorizontalAttractMotion(destinationX, k, dampening, entity.getMass()));
        }
    }

    public static void setIntersectingEffect(Entity[] entitys, double destinationX, double width,
                                             double speedMultiplier) {
        double k = 0.0001 * speedMultiplier;
        double dampening = 0.8;
        for (int i = 0; i < entitys.length; i++) {
            double initialX;
            Entity entity = entitys[i];
            if (i % 2 == 0) { // coming in from right
                initialX = entity.getWidth() / 2 + width;
            } else { // coming in from left
                initialX = -entity.getWidth() / 2;
            }
            entity.setPosition(initialX, entity.getY());
            entity.setMotion(new HorizontalAttractMotion(destinationX, k, dampening, entity.getMass()));
        }
    }

    public static void setZipperEffect(Entity[] entities, double destinationX, double width, double speedMultiplier) {
        double speed = 3.5 * speedMultiplier;
        for (int i = 0; i < entities.length; i++) {
            double initialX;
            Entity entity = entities[i];
            if (i % 2 == 0) { // coming in from right
                initialX = entity.getWidth() / 2 * (i + 1) + width;
            } else { // coming in from left
                initialX = -entity.getWidth() / 2 * (i + 1) * 0.5 - width;
            }
            entity.setPosition(initialX, entity.getY());
            entity.setMotion(new HorizontalAttractMotion(destinationX, 0.0001 * speed, 0.3, entity.getMass()));
//            entity.setMotionEffect(new HorizontalSpringMotion(entity, destinationX, new SpringIntegrator(0.0001, 0.7)));
        }
    }
}

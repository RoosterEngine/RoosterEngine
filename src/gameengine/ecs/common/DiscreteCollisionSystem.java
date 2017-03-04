package gameengine.ecs.common;

import gameengine.ecs.Entity;
import gameengine.ecs.Scheduler;
import gameengine.ecs.System;

import java.util.List;
import java.util.function.BiConsumer;

import static Utilities.MakeJavaSuckLess.*;

public class DiscreteCollisionSystem implements System {
    private final BiConsumer<Entity, Entity> collisionHandler;

    public DiscreteCollisionSystem(BiConsumer<Entity, Entity> collisionHandler) {
        this.collisionHandler = collisionHandler;
    }

    public Class[] requiredComponents() {
        return array(PositionComponent.class, DiscreteCollisionComponent.class);
    }

    public Scheduler schedule() {
        // this should be changeable at runtime

        return Scheduler.every(100, Scheduler.MILLIS);

        // Some ideas for Scheduler api

        // return Scheduler.whenever(Event('shoot'))
        // return Scheduler.whenever(PositionSystem.schedule())
        // return PositionSystem.schedule().map((e) -> e.add(100, Scheduler.millis))
        // return Scheduler.beforeContextStart()
        // return Scheduler.beforeContextExit()
    }

    public void update(List<Entity> entities) {
        // this is where you do the stuff

        for (int i = 0; i < entities.size(); i++) {
            Entity a = entities.get(i);
            PositionComponent aPos = a.getComponent(PositionComponent.class);
            DiscreteCollisionComponent aCol = a.getComponent(DiscreteCollisionComponent.class);

            for (int j = i + 1; j < entities.size(); j++) {
                Entity b = entities.get(j);
                PositionComponent bPos = b.getComponent(PositionComponent.class);
                DiscreteCollisionComponent bCol = b.getComponent(DiscreteCollisionComponent.class);

                // do collision checks and w/e

                if (colliding(bCol, bCol)) {
                   collisionHandler.accept(a, b);
                }

            }
        }
    }

    public boolean colliding(DiscreteCollisionComponent a, DiscreteCollisionComponent b) {
        return false;
    }
}

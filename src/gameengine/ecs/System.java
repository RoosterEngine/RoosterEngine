package gameengine.ecs;

import java.util.List;

public interface System {

    Class[] requiredComponents();

    Scheduler schedule();

    void update(List<Entity> entities);
}

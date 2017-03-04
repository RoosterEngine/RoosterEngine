package gameengine.ecs;

import java.util.HashMap;
import java.util.Map;

public class Entity {
    private Map<Class, Component> componentMap = new HashMap<>();

    public Entity() {
    }

    public void addComponent(Component component) {
        componentMap.put(component.getClass(), component);
    }

    /**
     * Fetches the component data for this entity.
     * ie. If an entity has a PhysicalComponent, entity.getComponent(PhysicalComponent.class) will return the
     * physical component associated with this entity.
     */
    public <T extends Component> T getComponent(Class<T> componentClass) {
        Component component = componentMap.get(componentClass);
        if (component == null) throw new EntityMissingComponentException(componentClass);

        return (T) component;
    }
}

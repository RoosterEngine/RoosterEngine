package gameengine.ecs;

public class EntityMissingComponentException extends RuntimeException {
    public <T extends Component> EntityMissingComponentException(Class<T> componentClass) {
        super("Entity doesn't have component: " + componentClass.getSimpleName());
    }
}

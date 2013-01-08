package bricklets;

import gameengine.motion.EnvironmentMotionGenerator;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: davidrusu
 * Date: 12/12/12
 * Time: 9:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class Group {
    private ArrayList<Entity> entities = new ArrayList<>();
    private ArrayList<EnvironmentMotionGenerator> environmentMotionGenerators = new ArrayList<>();

    public Group() {
    }

    /**
     * Adds the specified {@link Entity} to the group
     *
     * @param entity the {@link Entity} to add to the group
     */
    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    /**
     * Adds the specified {@link EnvironmentMotionGenerator} to the group
     *
     * @param environmentMotionGenerator the {@link EnvironmentMotionGenerator} to add to the group
     */
    public void addEnvironmentMotionGenerator(EnvironmentMotionGenerator environmentMotionGenerator) {
        environmentMotionGenerators.add(environmentMotionGenerator);
    }

    /**
     * removes all {@link Entity}s from the group
     */
    public void clearEntities() {
        entities.clear();
    }

    /**
     * removes all {@link EnvironmentMotionGenerator}s from the group
     */
    public void clearEnvironmentMotionGenerators() {
        environmentMotionGenerators.clear();
    }

    /**
     * Removes all {@link Entity}s and {@link EnvironmentMotionGenerator}s from the group
     */
    public void clearAll() {
        clearEntities();
        clearEnvironmentMotionGenerators();
    }

    /**
     * Updates all {@link EnvironmentMotionGenerator}s and applies the motion generated to the members of the group
     *
     * @param elapsedTime the amount of time in milliseconds since the last update
     */
    public void updateEnvironmentMotionGenerator(double elapsedTime) {
        for (EnvironmentMotionGenerator environmentMotionGenerator : environmentMotionGenerators) {
            environmentMotionGenerator.update(elapsedTime);
        }

        for (Entity member : entities) {
            double xAmount = 0;
            double yAmount = 0;
            for (EnvironmentMotionGenerator environmentMotionGenerator : environmentMotionGenerators) {
                environmentMotionGenerator.update(member);
                xAmount += environmentMotionGenerator.getDeltaVelocityX();
                yAmount += environmentMotionGenerator.getDeltaVelocityY();
            }
            member.addVelocity(xAmount, yAmount);
        }
    }
}

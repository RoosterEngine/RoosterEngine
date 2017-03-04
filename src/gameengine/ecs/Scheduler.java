package gameengine.ecs;

public class Scheduler {
    public static final double NANOS = 1; // base unit is nanos
    public static final double MICRO = NANOS * 1e3;
    public static final double MILLIS = NANOS * 1e6;
    public static final double SECONDS = NANOS * 1e9;

    public static Scheduler every(double interval, double units) {
        // ie. Scheduler.every(10, Scheduler.SECONDS);

        // TODO: this hasn't been thought through yet
        return new Scheduler();
    }
}

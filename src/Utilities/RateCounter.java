package Utilities;

import gameengine.core.GameTimer;

/**
 * Used to measure how many times an action happens per second.
 */
public class RateCounter {
    private long totalTickCount = 0;
    private long startTime, lastTickTime;
    private LongQueue tickTimes = new LongQueue(60);

    /**
     * Creates a RateCounter instance.
     */
    public RateCounter() {
        reset();
    }

    public final void reset() {
        tickTimes.clear();
        startTime = System.nanoTime();
        lastTickTime = startTime;
        //add an artificial tick a sixtieth of a second in the past which will only affect the
        // current tick rate during the first second
        tickTimes.enqueue(System.nanoTime() - 16 * GameTimer.NANOS_PER_MILLI);
    }

    /**
     * Registers a new tick.
     */
    public void registerTick() {
        long now = System.nanoTime();
        lastTickTime = now;
        tickTimes.enqueue(now);
        long cutoff = now - GameTimer.NANOS_PER_SECOND;//only keep samples for the last second
        //keep at least 2 samples since the tick rate is calculated from the last tick time (not
        // the current time)
        while (tickTimes.size() > 2 && tickTimes.peek() < cutoff) {
            tickTimes.dequeue();
        }
        totalTickCount++;
    }

    /**
     * @return The average number of actions per second since the creation of this rate counter
     * instance.
     */
    public double getAverageTickRate() {
        return totalTickCount * GameTimer.NANOS_PER_SECOND / (System.nanoTime() - startTime);
    }

    /**
     * @return The # of ticks per second.  This ignores any time that has passed since the last tick
     * because another tick might be right around the corner so this reduces fluctuations in the
     * reported tick rate.
     */
    public double getCurrentTickRate() {
        //TODO: subtracting 1 makes the reported fps inline with the target but I'm not sure why
        double result = (tickTimes.size() - 1) * GameTimer.NANOS_PER_SECOND / (double)
                (lastTickTime - tickTimes.peek());
        //round to the nearest decimal point to avoid misleading accuracy assumptions
        return Math.floor(result * 10 + 0.5) / 10;
    }
}

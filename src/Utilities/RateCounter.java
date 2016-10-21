package Utilities;

/**
 * Used to measure how many times an action happens per second.
 * <p>
 * Created by Dan on 10/19/2016.
 */
public class RateCounter {
   private static final long NANOS_PER_MILLI = 1000000;
   private static final long NANOS_PER_SECOND = NANOS_PER_MILLI * 1000;
   private long totalTickCount;
   private long startTime, lastTickTime;
   private LongQueue tickTimes;

   /**
    * Creates a RateCounter instance.
    */
   public RateCounter() {
      startTime = System.currentTimeMillis();
      lastTickTime = startTime;
      tickTimes = new LongQueue(60);
      //add an artificial tick a sixtieth of a second in the past which will only affect the
      // current tick rate during the first second
      tickTimes.enqueue(System.nanoTime() - 16 * NANOS_PER_MILLI);
   }

   /**
    * Registers a new action.
    */
   public void registerTick() {
      long now = System.nanoTime();
      lastTickTime = now;
      tickTimes.enqueue(now);
      long cutoff = now - NANOS_PER_SECOND;//only keep samples for the last second
      //keep at least 2 samples since the tick rate is calculated from the last tick time (not
      // the current time)
      while (tickTimes.size() > 2 && tickTimes.peek() < cutoff) {
         tickTimes.dequeue();
      }
      totalTickCount++;
   }

   /**
    * @return The average number of actions per second since the creation of this rate counter
    * instance
    */
   public double getAverageTickRate() {
      return totalTickCount * 1000.0 / (System.currentTimeMillis() - startTime);
   }

   /**
    * @return The # of ticks per second.  This ignores any time that has passed
    * since the last tick because another tick might be right around the corner
    * so this reduces fluctuations in the reported tick rate.
    */
   public double getCurrentTickRate() {
      //the number of ticks that occurred in the last second.  Multiply by 1000 since the times
      //are in milliseconds
      return tickTimes.size() * NANOS_PER_SECOND / (double) (lastTickTime - tickTimes.peek());
   }
}

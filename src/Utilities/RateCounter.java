package Utilities;

/**
 * Used to measure how many times an action happens per second.
 * <p>
 * Created by Dan on 10/19/2016.
 */
public class RateCounter {
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
      tickTimes.enqueue(System.currentTimeMillis() - 16);
   }

   /**
    * Registers a new action.
    */
   public void registerTick() {
      long now = System.currentTimeMillis();
      lastTickTime = now;
      tickTimes.enqueue(now);
      long cutoff = now - 1000;//only keep samples for the last second
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
      return totalTickCount / ((double) (lastTickTime - startTime));
   }

   /**
    * @return The # of ticks per second.  This ignores any time that has passed
    * since the last tick because another tick might be right around the corner
    * so this reduces fluctuations in the reported tick rate.
    */
   public double getCurrentTickRate() {
      //the number of ticks that occurred in the last second.  Multiply by 1000 since the times
      //are in milliseconds
      return tickTimes.size() * 1000.0 / (lastTickTime - tickTimes.peek());
   }
}

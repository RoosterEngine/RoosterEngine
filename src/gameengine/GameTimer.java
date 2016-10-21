package gameengine;

/**
 * @author danrusu
 */
public class GameTimer implements Runnable {
   /**
    * Number of nanoseconds in a millisecond.
    */
   private static final long NANOS_PER_MILLI = 1000000;

   /**
    * Number of nanoseconds in a second.
    */
   private static final double NANOS_PER_SECOND = 1000 * NANOS_PER_MILLI;

   /**
    * The sleep precision.  Don't attempt to sleep less than this amount (2 milliseconds).
    */
   private static final long SLEEP_PRECISION = 2 * NANOS_PER_MILLI;

   /**
    * This will remain true as long as the game is running and then the app will stop.
    */
   private volatile boolean isRunning = true;

   /**
    * minimum delay between rendering so that it doesn't exceed the desired frame rate.
    */
   private final long desiredFrameTime;

   /**
    * maximum delay between rendering so that it's not slower than the minimum frame rate.
    */
   private final long maxFrameTime;

   /**
    * the amount of nanoseconds that the last sleep operation overslept by (based on what was
    * requested).
    */
   private long overSleep = 0;

   /**
    * The game controller.
    */
   private GameController gameController;

   /**
    * The frame rate is allowed to vary between minFrameRate and maxFrameRate.
    * If the frame rate drops below the min frame rate then time dilation is
    * introduced to slow down the game.
    *
    * @param desiredFrameRate The desired frame rate
    * @param minFrameRate     The minimum acceptable frame rate
    */
   public GameTimer(GameController gameController, int desiredFrameRate, int minFrameRate) {
      assert desiredFrameRate >= minFrameRate & minFrameRate > 0;

      this.gameController = gameController;
      desiredFrameTime = (long) (NANOS_PER_SECOND / desiredFrameRate);
      maxFrameTime = (long) (NANOS_PER_SECOND / minFrameRate);
   }

   /**
    * Stops the game loop, thus stopping the game
    */
   public void stop() {
      isRunning = false;
   }

   public void run() {
      try {
         enableHighResolutionTimer();
         gameLoop();
      } finally {
         gameController.cleanup();
      }
   }

   /**
    * The game loop.
    */
   private void gameLoop() {
      long lastNanoTime = System.nanoTime();

      while (isRunning) {
         syncFrameRate(lastNanoTime);
         long currentTime = System.nanoTime();

         //The time difference since the last iteration of the game loop
         long timeDelta = currentTime - lastNanoTime;
         lastNanoTime = currentTime;
         //Introduce time dilation if the frame is taking too long so that it
         //doesn't grow out of control
         timeDelta = Math.min(timeDelta, maxFrameTime);
         double timeDeltaMillis = ((double) timeDelta) / NANOS_PER_MILLI;
         gameController.updateMouseVelocity(timeDeltaMillis);
         gameController.updateMouseMovedHandler(timeDeltaMillis);
         gameController.update(timeDeltaMillis);
         gameController.handleEvents(currentTime);
         gameController.draw();
      }
   }

   /**
    * Synchronize the frame rate sleeping if the previous frame completed
    * faster than the desired frame time
    *
    * @param lastUpdateTime The time when the last frame started
    */
   private void syncFrameRate(long lastUpdateTime) {
      long currentTime = System.nanoTime();
      long endTime = lastUpdateTime + desiredFrameTime;

      //Threads don't sleep the exact amount of time that you ask them to so
      //ask for the adjusted amount knowing that it will sleep for the amount
      //of time you really want
      long timeLeft = endTime - currentTime - overSleep;
      if (timeLeft > 0) {
         sleep(timeLeft);
         //compare the time we slept (System.nanoTime() - currentTime)
         //against timeLeft because that's how much time we asked to sleep
         long delta = System.nanoTime() - currentTime - timeLeft;

         //use a weighted average to compute the new value of overSleep so
         //that our one-off jumps are not taken too seriously
         overSleep = Math.max(0, (long) (overSleep * 0.95 + delta * 0.05));
      } else {
         //the current frame lasted more than the desired frame time
         Thread.yield();
      }
   }

   /**
    * Sleep the specified number of nanoseconds
    *
    * @param nanoseconds The number of nanoseconds to sleep
    */
   private void sleep(long nanoseconds) {
      long start = System.nanoTime();

      //sleeping is not too precise so sleep a bit less time than what was
      //really requested and perform a few yields
      long milliseconds = (nanoseconds - SLEEP_PRECISION) / NANOS_PER_MILLI;
      if (milliseconds > 0) {
         try {
            Thread.sleep(milliseconds);
         } catch (InterruptedException e) {
            //ignore as we'll be yielding anyway
         }
      }
      //subtract 5 microseconds because Thread.yield() isn't free
      nanoseconds += start - 5000;
      while (System.nanoTime() < nanoseconds) {
         Thread.yield();
      }
   }

   /**
    * Windows hack to enable high resolution timer while app is running
    */
   private void enableHighResolutionTimer() {
      Thread thread = new Thread(() -> {
         try {
            Thread.sleep(Long.MAX_VALUE);
         } catch (Exception e) {
         }
      });
      thread.setDaemon(true);
      thread.start();
   }
}

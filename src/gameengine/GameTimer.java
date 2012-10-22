package gameengine;

/**
 *
 * @author danrusu
 */
public class GameTimer implements Runnable {

    private long startTime;
    private int numFrames = 0, numUpdates = 0;
    private int lastFrameIndex, lastUpdateIndex;
    private long[] frameTimes, updateTimes;
    
    private GameController gameController;
    private boolean isRunning = true;
    private long milliToNano = 1000000;	 //this is the number of nanoseconds in a millisecond (multiply milliseconds by this to convert to the number of nanoseconds)
    private long updateTime;
    private double updateTimeMS;       //update time in milliseconds as a double
    private long desiredFrameTime;	 //minimum delay between rendering so that it doesn't exceed the desired frame rate
    private long maxFrameTime;         //maximum delay between rendering so that it's not slower than the minimum frame rate
    private long sleepAccumulator = 0;   //small differences between frame times are accumulated so that frames are sincronized to avoid drifting
    private long overSleep = 0;        //the amount of nanoseconds that the last sleep operation overslept by (based on what was requested)

    /**
     * @param updateRate update game state this many times per second
     * (updateRate needs to be greater or equal to desiredFrameRate)
     * @param desiredFrameRate render to screen this many times per second
     * @param minFrameRate minimum allowable frame rate before the updateRate
     * slows down so it will appear to jump (game play doesn't slow down though)
     */
    public GameTimer(GameController gameController, int updateRate, int desiredFrameRate, int minFrameRate) {
        this.gameController = gameController;
        updateTime = 1000 * milliToNano / updateRate;
        updateTimeMS = 1000.0 / updateRate;
        desiredFrameTime = 1000 * milliToNano / desiredFrameRate;
        maxFrameTime = 1000 * milliToNano / minFrameRate;
        
        frameTimes = new long[desiredFrameRate];
        updateTimes = new long[updateRate];
    }

    /**
     * Stops the game loop, thus stopping the game
     */
    public void stop() {
        isRunning = false;
    }

    public void run() {
        try {
            init();
            gameLoop();
        } finally {
            gameController.resetCursor();
            gameController.restoreScreen();
        }
    }

    public void init() {
        // sets up the the frame and update times arrays so that the values stored are close to would be if the game was currently running
        long currentTime = System.nanoTime();
        long offset = 2000000000 / frameTimes.length; // starts half way between 0 fps and the desired fps
        for(int i = 0; i < frameTimes.length; i++){
            frameTimes[i] = currentTime - offset * (frameTimes.length - i);
        }
        offset = 2000000000 / updateTimes.length;
        for(int i = 0; i < updateTimes.length; i++){
            updateTimes[i] = currentTime - offset * (updateTimes.length - i);
        }
        startTime = System.nanoTime();
        isRunning = true;
    }

    public void gameLoop() {
        long currentTime = System.nanoTime();
        long lastRenderTime = currentTime;
        long lastUpdateTime;
        long gameTime = currentTime;

        while (isRunning) {
            currentTime = System.nanoTime();
            // should predict if it has enough time to do two updates in the condition of the while loop
            while (gameTime + updateTime < currentTime && System.nanoTime() < lastRenderTime + maxFrameTime) {
                gameTime += updateTime; //if need to update every 5ms but 15ms passed then update by 5ms 3 times
                gameController.handleEvents(gameTime);
                update(updateTimeMS);
            }
            currentTime = System.nanoTime();
            lastUpdateTime = currentTime;
            gameController.handleEvents(currentTime);
            update((currentTime - gameTime) / 1000000.0); //update the game based on how much time is left
            gameTime = currentTime;
            lastRenderTime = System.nanoTime();
            draw();
            syncFrameRate(lastUpdateTime);
        }
    }

    private void syncFrameRate(long lastUpdateTime) {
        long endTime = lastUpdateTime + desiredFrameTime;
        long currentTime = System.nanoTime();
        if (currentTime < endTime) {
            long timeLeft = endTime - currentTime - sleepAccumulator - overSleep;
            long timeMS = timeLeft / 1000000;
            if (timeMS > 0) {
                try {
                    Thread.sleep(timeMS, (int) (timeLeft - 1000000 * timeMS));
                    overSleep = System.nanoTime() - currentTime - timeLeft;
                } catch (InterruptedException e) {
                    overSleep = 0;
                }
            } else {
                Thread.yield();
            }
            sleepAccumulator += System.nanoTime() - endTime;
        } else //the current frame lasted more than the desired frame time
        {
            Thread.yield();
            sleepAccumulator += System.nanoTime() - endTime;
            sleepAccumulator = Math.min(desiredFrameTime * 5, sleepAccumulator); //it's falling behind so we don't want it to go really fast for more than a frame afterwards to try to catch up
        }
    }

    private void update(double elapsedTime) {
        numUpdates++;
        lastUpdateIndex = (lastUpdateIndex + 1) % updateTimes.length;
        updateTimes[lastUpdateIndex] = System.nanoTime();
        gameController.update(elapsedTime);
    }

    private void draw() {
        numFrames++;
        lastFrameIndex = (lastFrameIndex + 1) % frameTimes.length;
        frameTimes[lastFrameIndex] = System.nanoTime();
        gameController.draw();
    }

    public double getFrameRate() {
        return 1000000000.0 * frameTimes.length/(frameTimes[lastFrameIndex] - frameTimes[(lastFrameIndex + 1) % frameTimes.length]);
    }

    public double getUpdateRate() {
        return 1000000000.0 * updateTimes.length/(updateTimes[lastUpdateIndex] - updateTimes[(lastUpdateIndex + 1) % updateTimes.length]);
    }
    
    public double getAverageFrameRate(){
        return 1000000000.0 * numFrames / (System.nanoTime() - startTime);
    }

    public double getAverateUpdateRate(){
        return 1000000000.0 * numUpdates / (System.nanoTime() - startTime);
    }
}
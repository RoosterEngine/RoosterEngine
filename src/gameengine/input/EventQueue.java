package gameengine.input;

import gameengine.GameController;

/**
 * Events from the EDT thread are placed on this queue and handled from the game thread
 *
 * @author davidrusu
 */
public class EventQueue {

    private static final double GROWTH_RATE = 1.5;
    private static final int INIT_CAPACITY = 16, INIT_RECYCLE_CAPACITY = 16;
    private GameController gameController;
    private InputEvent[] queue;
    private int front = 0, size = 0;
    private InputEvent[][] recycledInstances = new InputEvent[2][INIT_RECYCLE_CAPACITY];
    private int[] numRecycled;
    private boolean clearQueue = false;
    
    public EventQueue(GameController gameController) {
        this.gameController = gameController;
        queue = new InputEvent[INIT_CAPACITY];
        numRecycled = new int[2];
        numRecycled[0] = 1;
        numRecycled[1] = 1;
        recycledInstances[InputEvent.PRESSED_EVENT][0] = new PressedEvent(0, 0);
        recycledInstances[InputEvent.RELEASED_EVENT][0] = new ReleasedEvent(0, 0);
    }

    /**
     * Add a {@link PressedEvent} to the queue
     *
     * @param inputCode the input code of the event, should be retrieved from
     * {@link InputCode}
     * @param eventTime the time this action was triggered
     */
    public synchronized void addPressedAction(int inputCode, long eventTime) {
        addRecycledInputAction(InputEvent.PRESSED_EVENT, inputCode, eventTime);
    }

    /**
     * Add a {@link ReleasedEvent} to the queue
     *
     * @param inputCode the input code of the event, should be retrieved from
     * {@link InputCode}
     * @param eventTime the time this action was triggered
     */
    public synchronized void addReleasedAction(int inputCode, long eventTime) {
        addRecycledInputAction(InputEvent.RELEASED_EVENT, inputCode, eventTime);
    }
    
    private void addRecycledInputAction(int actionType, int inputCode, long eventTime){
        InputEvent inputEvent;
        if(numRecycled[actionType] == 0){
            inputEvent = recycledInstances[actionType][0].createInstance(inputCode, eventTime);
        }else{
            numRecycled[actionType]--;
            inputEvent = recycledInstances[actionType][numRecycled[actionType]];
            inputEvent.setup(inputCode, eventTime);
        }
        if (size == queue.length) {
            expand();
        }
        queue[(front + size) % queue.length] = inputEvent;
        size++;
    }

    /**
     * Handles the events up to the specified time
     * 
     * @param cutOffTime the time to handle events upto
     */
    public synchronized void handleEvents(long cutOffTime) {
        while(size > 0 && queue[front].getEventTime() <= cutOffTime) {
            size--;
            queue[front].handleAction(gameController);
            recycleAction(queue[front]);
            front = (front + 1) % queue.length;
        }
    }
    
    /**
     * Returns the time in nanoseconds of the next event. Long.MAX_VALUE is returned if there are no events in queue
     * @return the time in nanoseconds of the next event. Long.MAX_VALUE is returned if there are no events in queue
     */
    public long getNextEventTime(){
        if(size == 0){
            return Long.MAX_VALUE;
        }
        return queue[front].getEventTime();
    }
    
    private void emptyQueue(){
        while(size > 0) {
            size--;
            recycleAction(queue[front]);
            front = (front + 1) % queue.length;
        }
        clearQueue = false;
    }

    //TODO figure out what was going on here
    public synchronized void clearQueue(){
        clearQueue = true;
    }
    
    private void recycleAction(InputEvent action){
        int type = action.getEventType();
        if(numRecycled[type] == recycledInstances[type].length){
            InputEvent[] temp = recycledInstances[type];
            recycledInstances[type] = new InputEvent[(int)(temp.length * GROWTH_RATE)];
            System.arraycopy(temp, 0, recycledInstances[type], 0, temp.length);
        }
        recycledInstances[type][numRecycled[type]] = action;
        numRecycled[type]++;
    }
    
    private void expand() {
        InputEvent[] temp = new InputEvent[(int) (queue.length * GROWTH_RATE)];
        int firstHalfLength = queue.length - front;
        System.arraycopy(queue, front, temp, 0, firstHalfLength);
        System.arraycopy(queue, 0, temp, firstHalfLength, front);
        queue = temp;
        front = 0;
    }
}

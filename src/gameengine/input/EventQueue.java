package gameengine.input;

/**
 * Events from the EDT thread are placed on this queue and handled from the game thread
 *
 * @author davidrusu
 */
public class EventQueue {

    private static final double GROWTH_RATE = 1.5;
    private static final int INIT_CAPACITY = 16, INIT_RECYCLE_CAPACITY = 16;
    private InputAction[] queue;
    private int front = 0, size = 0;
    private InputAction[][] recycledInstances = new InputAction[3][INIT_RECYCLE_CAPACITY];
    private int[] numRecycled;
    private boolean clearQueue = false;
    
    public EventQueue() {
        queue = new InputAction[INIT_CAPACITY];
        numRecycled = new int[2];
        numRecycled[0] = 1;
        numRecycled[1] = 1;
        recycledInstances[InputAction.PRESSED_ACTION][0] = new PressedAction(null, 0, 0);
        recycledInstances[InputAction.RELEASED_ACTION][0] = new ReleasedAction(null, 0, 0);
    }

    /**
     * Add a {@link PressedAction} to the queue
     *
     * @param handler the handler to be called when the handleAction method is
     * called
     * @param inputCode the input code of the event, should be retrieved from
     * {@link InputCode}
     * @param eventTime the time this action was triggered
     */
    public synchronized void addPressedAction(ActionHandler handler, int inputCode, long eventTime) {
        ((PressedAction)addRecycledInputAction(InputAction.PRESSED_ACTION)).setup(handler, inputCode, eventTime);
    }

    /**
     * Add a {@link ReleasedAction} to the queue
     *
     * @param handler the handler to be called when the handleAction method is
     * called
     * @param inputCode the input code of the event, should be retrieved from
     * {@link InputCode}
     * @param eventTime the time this action was triggered
     */
    public synchronized void addReleasedAction(ActionHandler handler, int inputCode, long eventTime) {
        ((ReleasedAction)addRecycledInputAction(InputAction.RELEASED_ACTION)).setup(handler, inputCode, eventTime);
    }
    
    private InputAction addRecycledInputAction(int actionType){
        InputAction action;
        if(numRecycled[actionType] == 0){
            action = recycledInstances[actionType][0].createInstance();
        }else{
            numRecycled[actionType]--;
            action = recycledInstances[actionType][numRecycled[actionType]];
        }
        if (size == queue.length) {
            expand();
        }
        queue[(front + size) % queue.length] = action;
        size++;
        return action;
    }

    /**
     * Handles the events up to the specified time
     * 
     * @param cutOffTime the time to handle events upto
     */
    public synchronized void handleEvents(long cutOffTime) {
        if(clearQueue){
            emptyQueue();
        }
        while(size > 0 && queue[front].getEventTime() <= cutOffTime) {
            size--;
            queue[front].handleAction();
            recycleAction(queue[front]);
            front = (front + 1) % queue.length;
        }
    }
    
    /**
     * Returns the time in nanoseconds of the next event. Long.MAX_VALUE is returned if there are no events in queue
     * @return 
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
    
    public synchronized void clearQueue(){
        clearQueue = true;
    }
    
    private void recycleAction(InputAction action){
        int type = action.getActionType();
        if(numRecycled[type] == recycledInstances[type].length){
            InputAction[] temp = recycledInstances[type];
            recycledInstances[type] = new InputAction[(int)(temp.length * GROWTH_RATE)];
            System.arraycopy(temp, 0, recycledInstances[type], 0, temp.length);
        }
        action.clearHandler();
        recycledInstances[type][numRecycled[type]] = action;
        numRecycled[type]++;
    }
    
    private void expand() {
        InputAction[] temp = new InputAction[(int) (queue.length * GROWTH_RATE)];
        int firstHalfLength = queue.length - front;
        System.arraycopy(queue, front, temp, 0, firstHalfLength);
        System.arraycopy(queue, 0, temp, firstHalfLength, front);
        queue = temp;
        front = 0;
    }
}
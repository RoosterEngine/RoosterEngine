package gameengine.effects;

/**
 *
 * @author davidrusu
 */
public class Slide implements Effect{
    private double x, y, dx, dy, destinationX, destinationY, k, dampeningFactor;
    private double initialX, initialY;
    public Slide(double initialX, double initialY, double destinationX, double destinationY, double k, double dampeningRatio){
        this.destinationX = destinationX;
        this.destinationY = destinationY;
        this.k = k;
        this.initialX = initialX;
        this.initialY = initialY;
        reset();
        dampeningFactor = dampeningRatio * 2 * Math.sqrt(k);
    }

    @Override
    public double getX(){
        return x;
    }
    
    @Override
    public double getY(){
        return y;
    }
    
    @Override
    public void reset(){
        x = initialX;
        y = initialY;
        dx = 0;
        dy = 0;
    }
    
    @Override
    public void reset(double x, double y){
        this.x = x;
        this.y = y;
        initialX = x;
        initialY = y;
        dx = 0;
        dy = 0;
    }
    
    @Override
    public void update(double elapsedTime) {
        dx += ((destinationX - x) * k - dampeningFactor * dx) * elapsedTime;
        dy += ((destinationY - y) * k - dampeningFactor * dy) * elapsedTime;
        x += dx * elapsedTime;
        y += dy * elapsedTime;
    }
}
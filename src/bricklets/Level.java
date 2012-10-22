package bricklets;

import java.util.ArrayList;

/**
 * Stores level data
 * @author davidrusu
 */
public class Level {
    private Paddle paddle;
    private ArrayList<Ball> balls;
    private ArrayList<Brick> bricks;
    
    
    public Level(Paddle paddle, ArrayList<Brick> bricks, ArrayList<Ball> balls){
        this.paddle = paddle;
        this.balls = balls;
        this.bricks = bricks;
    }
    
    public Paddle getPaddle(){
        return paddle;
    }
    
    public ArrayList<Ball> getBalls(){
        return balls;
    }
    
    public ArrayList<Brick> getBricks(){
        return bricks;
    }
}
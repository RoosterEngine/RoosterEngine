package bricklets;

import gameengine.Context;
import gameengine.ContextType;
import gameengine.GameController;
import gameengine.input.Action;
import gameengine.input.ActionHandler;
import gameengine.input.InputCode;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author davidrusu
 */
public class Game extends Context{
    private Level[] levels;
    private int currentLevel = -1;
    private int mouseX = 0, mouseY = 0;
    private int realMouseX = 0, realMouseY = 0;
    private CollisionDetector collisionDetector = new CollisionDetector(3);
    private ArrayList<Ball> balls;
    private ArrayList<Brick> bricks;
    private ArrayList<BoundingBox> boundingBoxes;
    private Paddle paddle;
    private Collidable mouseItem;
    
    //Physics junk
    private double timeToCollision = 0;
    private boolean paused = false;
    private double[] collisionTimes = new double[16];
    private double currentGameTime = 0, collisionRate = 0;
    private int collisionX, collisionY, back = 0, numCollision = 0;
    private ArrayList<Point2D> collisions = new ArrayList<Point2D>();
    private boolean rulerMode, dragging;
    private int startMouseX, startMouseY;
    private double rulerLength;
    private long seed = 0;
    
    //Panning around screen
    boolean panMode = false;
    int shiftX = 0, shiftY = 0;
    double scale = 1;
    private double timeScale = 1;
    
    public Game(GameController controller){
        super(controller, ContextType.GAME, false, true);
        bricks = new ArrayList<Brick>();
        balls = new ArrayList<Ball>();
        boundingBoxes = new ArrayList<BoundingBox>();
        setupLevels();
        nextLevel();
        setupInput();
    }
            
    @Override
    public void mouseMoved(int x, int y) {
        mouseX = x;
        mouseY = y;
        realMouseX += x;
        realMouseY += y;
        if(dragging){
            int dx = realMouseX - startMouseX;
            int dy = realMouseY - startMouseY;
            rulerLength = Math.sqrt(dx * dx + dy * dy);
        }else if(panMode){
            shiftX += x;
            shiftY += y;
        }else{
            mouseItem.addForce(mouseX / 10000.0, mouseY / 10000.0);
        }
    }
    
    @Override
    public void update(double elapsedTime) {
        double timeLeft = elapsedTime * timeScale;
        while(timeLeft > 0 && !paused){
            Collision collision = collisionDetector.getNextCollision(timeLeft);
            timeToCollision = collision.getTimeToCollision(); // used for display purposes only
            double updateTime = Math.min(collision.getTimeToCollision(), timeLeft);
            currentGameTime += updateTime;
            if(updateTime > 0){
                updatePositions(updateTime);
            }
            if(collision.getTimeToCollision() != Collider.NO_COLLISION && collision.getTimeToCollision() <= timeLeft){
                // for the first collisionTimes.length collisions, the collision rate will be inacurate;
                collisionTimes[back] = currentGameTime;
                int front = (back + 1) % collisionTimes.length;
                double dt = collisionTimes[back] - collisionTimes[front];
                back = front;
                collisionRate = collisionTimes.length / dt;
                
//                collisions.add(new Point2D.Double(collision.getA().getX(), collision.getA().getY()));// used for debugging
//                collisions.add(new Point2D.Double(collision.getB().getX(), collision.getB().getY()));// used for debugging
                handleCollision(collision, collisionRate);
//                paused = true;
                
                numCollision++;
            }
            timeLeft -= updateTime;
        }
    }
    
    private void updatePositions(double elapsedTime){
            updateBricks(elapsedTime);
            updateBalls(elapsedTime);
            updatePaddle(elapsedTime);
            updateBoundingBoxes(elapsedTime);
    }
    
    private void updateBoundingBoxes(double elapsedTime){
        double k = 0.00001;
        double bottomBorder = 100;
        for(BoundingBox box: boundingBoxes){
//            box.setColor(box.getColor().);
            box.update(elapsedTime);
        }
    }
    
    private void updateBricks(double elapsedTime){
        double k = 0.0001;
        double bottomBorder = 100;
        for(Brick brick: bricks){
            if(brick.getX() <= 0){
                brick.addForce(-brick.getX() * k, 0);
            }else if(brick.getX() >= width){
                brick.addForce((width - brick.getX()) * k, 0);
            }else if(brick.getY() <= 0){
                brick.addForce(0, -brick.getY() * k);
            }else if(brick.getY() >= height - bottomBorder){
                brick.addForce(0, (height - bottomBorder - brick.getY()) * k);
            }
            brick.update(elapsedTime);
        }
    }
    
    private void updateBalls(double elapsedTime){
        double k = 0.001;
        double borderY = 0;
//        balls.get(0).addForce(0, -Entity.g);
        for(Ball ball: balls){
            ball.update(elapsedTime);
            if(ball.getX() <= 0){
                ball.addForce(-ball.getX() * k, 0);
            }else if(ball.getX() >= width){
                ball.addForce((width - ball.getX()) * k, 0);
            }else if(ball.getY() <= 0){
                ball.addForce(0, -ball.getY() * k);
            }else if(ball.getY() >= height - borderY){
                ball.addForce(0, (height - borderY - ball.getY()) * k);
            }
//            if(ball.getX() <= 0 || ball.getX() >= width){
//                ball.setVelocityX(-ball.getVelocityX());
//            }else if(ball.getY() <= 0 || ball.getY() >= height){
//                ball.setVelocityY(-ball.getVelocityY());
//            }
        }
    }
    
    private void updatePaddle(double elapsedTime){
        double k = 0.001;
            if(paddle.getX() <= 0){
                paddle.addForce(-paddle.getX() * k, 0);
            }else if(paddle.getX() >= width){
                paddle.addForce((width - paddle.getX()) * k, 0);
            }else if(paddle.getY() <= 0){
                paddle.addForce(0, -paddle.getY() * k);
            }else if(paddle.getY() >= height){
                paddle.addForce(0, (height - paddle.getY()) * k);
            }
        paddle.update(elapsedTime);
    }
    
    private void handleCollision(Collision collision, double collisionsPerMilli){
//        Collidable a = collision.getA();
//        Collidable b = collision.getB();
//        a.setColor(Color.RED);
//        b.setColor(Color.RED);
//        Physics.performCollision(a, b, 1, collisionsPerMilli, collision.getCollisionNormal());
    }

    private void drawCollisionPoints(Graphics2D g){
        double width = 2, height = 2;
        g.setColor(Color.MAGENTA);
        for(Point2D point: collisions){
            g.fillRect((int)(point.getX() - width / 2), (int)(point.getY() - height / 2), (int)width, (int)height);
        }
    }
    @Override
    public void draw(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, width, height);
        
        g.translate(shiftX, shiftY);
        g.scale(scale, scale);
        for(Ball ball: balls){;
            ball.draw(g);
        }
        for(Brick brick: bricks){
            brick.draw(g);
        }
        for(BoundingBox box: boundingBoxes){
            box.draw(g);
        }
        if(rulerMode){
            g.setColor(Color.WHITE);
            g.drawLine(startMouseX, startMouseY, realMouseX, realMouseY);
            g.drawString(rulerLength + "", realMouseX, realMouseY);
        }
//        paddle.draw(g);
        drawCollisionPoints(g);
        g.scale(1 / scale, 1 / scale);
        g.translate(-shiftX, -shiftY);
        
        g.setColor(Color.LIGHT_GRAY);
        g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        g.drawString("          fps: " + controller.getFrameRate(), 50, 50);
        g.drawString("          ups: " + controller.getUpdateRate(), 50, 70);
        g.drawString("nextCollision: " + timeToCollision / 1000, 50, 90);
        g.drawString("    collision: " + collisionX + " " + collisionY, 50, 110);
        g.drawString("collisionRate: " + collisionRate, 50, 130);
        g.drawString("   collisions: " + numCollision, 50, 150);
        g.drawString(" num of balls: " + balls.size(), 50, 170);
        g.drawString("mouseItem pos: " + mouseItem.getPosition().getX() + ", " + mouseItem.getPosition().getY(), 50, 190);
//        for(Ball ball: balls){
//            ball.setColor(Color.ORANGE);
//        }
    }
    
    public void setSeed(long seed){
        this.seed = seed;
    }
    
    private void setupLevels(){
        ArrayList<Brick> bricks = new ArrayList<Brick>();
        int brickWidth = 10;
        int brickHeight = 10;
        int padding = 100;
        int rows = 3;
        int columns = 1;
        int borderX = (width - columns * (brickWidth + padding)) / 2;
        int borderY = 300;
        double offsetX = borderX + brickWidth / 2.0;
        double offsetY = borderY + brickHeight / 2.0;
        for(int y = 0; y < rows; y++){
            for(int x = 0; x < columns; x++){
                bricks.add(new Brick(this, x * (brickWidth + padding) + offsetX, y * (brickHeight + padding) + offsetY, brickWidth, brickHeight));
//                bricks.add(new Brick(controller, x * (brickWidth + padding) + offsetX + y * 2, Math.sin(x / 10.0) * 50 + offsetY + y * (brickHeight + padding) + x, brickWidth, brickHeight));
            }
        }
        
        Paddle paddle = new Paddle(this, 50, 50, 50, 10);
        levels = new Level[1];
        levels[0] = new Level(paddle, bricks, getBalls());
    }
    
    private ArrayList<Ball> getBalls(){
        ArrayList<Ball> balls = new ArrayList<Ball>();
        int radius = 10;
        int padding = 9;
        int rows = 10;
        int columns = 10;
        int borderX = (width - columns * (radius * 2 + padding)) / 2;
        int borderY = 50;
        double offsetX = borderX + radius * 2 / 2.0;
        double offsetY = borderY + radius * 2 / 2.0;
        balls.add(new Ball(this, offsetX + 50, 800, 0, -0.5, 1, 15, Color.PINK));
        for(int y = 0; y < rows; y++){
            for(int x = 0; x < columns; x++){
                balls.add(new Ball(this, x * (radius * 2 + padding) + offsetX, y * (radius * 2 + padding) + offsetY, radius, 0.1));
//                balls.add(new Ball(this, Math.random() * width, Math.random() * height, radius, 0.1));
            }
        }
        return balls;
    }
    
    private void loadLevel(Level level){
        Polygon.setRandomSeed(seed);
        balls.clear();
        bricks.clear();
        boundingBoxes.clear();
        collisionDetector.clearCollisions();
        
        ballMode(level);
//        brickMode(level);
//        boundingBoxMode(level);
        paddle = new Paddle(level.getPaddle());
//        collisions.addCollidable(paddle, 2);
        
        collisionDetector.setCollisionPair(0, 0);
        collisionDetector.setCollisionPair(0, 1);
        collisionDetector.setCollisionPair(0, 2);
        collisionDetector.setCollisionPair(2, 1);
        collisionDetector.setCollisionPair(1, 1);
        
    }
    
    private void boundingBoxMode(Level level){
        int width = 50;
        int height = 100;
        int padding = 1;
        int rows = 30;
        int columns = 1;
        int borderX = 500;//(this.width - columns * (width + padding)) / 2;
        int borderY = -3500;
        double offsetX = borderX + width / 2.0;
        double offsetY = borderY + height / 2.0;
        for(int y = 0; y < rows; y++){
            for(int x = 0; x < columns; x++){
                BoundingBox box = new BoundingBox(this, x * (width + padding) + offsetX, y * (height + padding) + offsetY, width, height, false, true);
//                BoundingBox box = new BoundingBox(this, Math.random() * this.width, Math.random() * this.height, width, height);
                boundingBoxes.add(box);
                collisionDetector.addCollidable(box, 1);
            }
        }
//        double boxWidth = 10;
//        double boxHeight = 10;
//        for(int i = 0; i < 3; i++){
//            BoundingBox box = new BoundingBox(this, width / 2, 50 + i * boxHeight * 1.1, boxWidth, boxHeight);
//            boundingBoxes.add(box);
//            collisions.addCollidable(box, 1);
//        }
        
//        BoundingBox box = new BoundingBox(this, this.width / 2, this.height / 2, 10, 10);
//        boundingBoxes.add(box);
//        collisions.addCollidable(box, 1);
        BoundingBox box = new BoundingBox(this, this.width / 2, this.height - 100, this.width, 10, true, false);
        box.setMass(10000);
        boundingBoxes.add(box);
        collisionDetector.addCollidable(box, 1);
        box = new BoundingBox(this, this.width / 2, -5500, this.width, 10, true, false);
        box.setMass(10000);
        boundingBoxes.add(box);
        collisionDetector.addCollidable(box, 1);
        box = new BoundingBox(this, 10, this.height / 2 - 1500, 10, this.height * 6, true, false);
        box.setMass(10000);
        boundingBoxes.add(box);
        collisionDetector.addCollidable(box, 1);
        box = new BoundingBox(this, this.width - 10, this.height / 2 - 1500, 10, this.height * 6, true, false);
        box.setMass(10000);
        boundingBoxes.add(box);
        collisionDetector.addCollidable(box, 1);
        mouseItem = boundingBoxes.get(0);
    }
    
    private void brickMode(Level level){
        for(Brick brick: level.getBricks()){
            Brick newBrick = new Brick(brick);
            bricks.add(newBrick);
            collisionDetector.addCollidable(newBrick, 1);
        }
        mouseItem = bricks.get(0);
    }
    
    private void ballMode(Level level){
        for(Ball ball: level.getBalls()){
            Ball newBall = new Ball(ball);
            balls.add(newBall);
            collisionDetector.addCollidable(newBall, 0);
        }
        mouseItem = balls.get(0);
    }
    
    private void nextLevel(){
        currentLevel++;
        loadLevel(levels[currentLevel]);
    }
    
    private void setupInput(){
        
        controller.setContextBinding(contextType, InputCode.KEY_ESCAPE, Action.EXIT_GAME);
        bindAction(Action.EXIT_GAME, new ActionHandler() {
            @Override
            public void startAction(int inputCode) {
            }

            @Override
            public void stopAction(int inputCode) {
                loadLevel(levels[currentLevel]);
                controller.exitContext();
            }
        });
        
        final double thrust = 0.05;
        controller.setContextBinding(contextType, InputCode.KEY_LEFT, Action.GAME_LEFT);
        bindAction(Action.GAME_LEFT, new ActionHandler() {
            @Override
            public void startAction(int inputCode) {
                mouseItem.addForce(-thrust, 0);
            }

            @Override
            public void stopAction(int inputCode) {
            }
        });
        
        controller.setContextBinding(contextType, InputCode.KEY_RIGHT, Action.GAME_RIGHT);
        bindAction(Action.GAME_RIGHT, new ActionHandler() {
            @Override
            public void startAction(int inputCode) {
                mouseItem.addForce(thrust, 0);
            }

            @Override
            public void stopAction(int inputCode) {
            }
        });
        
        final double timeSpeedIncrement = 0.05;
        
        controller.setContextBinding(contextType, InputCode.KEY_UP, Action.GAME_UP);
        bindAction(Action.GAME_UP, new ActionHandler() {
            @Override
            public void startAction(int inputCode) {
//                mouseItem.addForce(0, -thrust);
                timeScale += timeSpeedIncrement;
            }

            @Override
            public void stopAction(int inputCode) {
            }
        });
        
        controller.setContextBinding(contextType, InputCode.KEY_DOWN, Action.GAME_DOWN);
        bindAction(Action.GAME_DOWN, new ActionHandler() {
            @Override
            public void startAction(int inputCode) {
//                mouseItem.addForce(0, thrust);
                if(timeScale - timeSpeedIncrement > 0){
                    timeScale -= timeSpeedIncrement;
                }
            }

            @Override
            public void stopAction(int inputCode) {
            }
        });
        
        controller.setContextBinding(contextType, InputCode.KEY_P, Action.PAUSE_GAME);
        bindAction(Action.PAUSE_GAME, new ActionHandler() {
            @Override
            public void startAction(int inputCode) {
//                rulerMode = !rulerMode;
//                dragging = !dragging;
                paused = !paused;
            }

            @Override
            public void stopAction(int inputCode) {
            }
        });
        
        controller.setContextBinding(contextType, InputCode.KEY_R, Action.RESTART_GAME);
        bindAction(Action.RESTART_GAME, new ActionHandler() {

            @Override
            public void startAction(int inputCode) {
            }

            @Override
            public void stopAction(int inputCode) {
                loadLevel(levels[currentLevel]);
            }
        });
        
        controller.setContextBinding(contextType, InputCode.MOUSE_LEFT_BUTTON, Action.MENU_MOUSE_SELECT);
        bindAction(Action.MENU_MOUSE_SELECT, new ActionHandler() {

            @Override
            public void startAction(int inputCode) {
                startMouseX = realMouseX;
                startMouseY = realMouseY;
            }

            @Override
            public void stopAction(int inputCode) {
//                dragging = false;
            }
        });
        
        controller.setContextBinding(contextType, InputCode.KEY_SHIFT, Action.PAN_GAME);
        bindAction(Action.PAN_GAME, new ActionHandler() {

            @Override
            public void startAction(int inputCode) {
                panMode = true;
            }

            @Override
            public void stopAction(int inputCode) {
                panMode = false;
            }
        });
        
        final double zoom = 0.04;
        controller.setContextBinding(contextType, InputCode.MOUSE_WHEEL_DOWN, Action.ZOOM_IN_GAME);
        bindAction(Action.ZOOM_IN_GAME, new ActionHandler() {
            @Override
            public void startAction(int inputCode) {
                scale -= zoom;
            }

            @Override
            public void stopAction(int inputCode) {
            }
        });
        
        controller.setContextBinding(contextType, InputCode.MOUSE_WHEEL_UP, Action.ZOOM_OUT_GAME);
        bindAction(Action.ZOOM_OUT_GAME, new ActionHandler() {
            @Override
            public void startAction(int inputCode) {
                scale += zoom;
            }

            @Override
            public void stopAction(int inputCode) {
            }
        });
    }
}
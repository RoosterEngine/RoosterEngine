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
import java.util.ArrayList;

/**
 *
 * @author davidrusu
 */
public class Game extends Context{
    private double mouseX = 0, mouseY = 0;
    private double realMouseX = 0, realMouseY = 0;
    private CollisionDetector collisionDetector = new CollisionDetector(3);
    private ArrayList<CircleEntity> circles = new ArrayList<CircleEntity>();
    private ArrayList<PolygonEntity> polygons = new ArrayList<PolygonEntity>();
    private ArrayList<AABBEntity> aabBoxs = new ArrayList<AABBEntity>();
    private Entity mouseItem;
    
    //Physics junk
    private double timeToCollision = 0;
    private boolean paused = false;
    private double[] collisionTimes = new double[16];
    private double currentGameTime = 0, collisionRate = 0;
    private int collisionX, collisionY, back = 0, numCollision = 0;
    private boolean rulerMode, dragging;
    private double startMouseX, startMouseY;
    private double rulerLength;
    private long seed = 0;
    
    //Panning around screen
    private boolean panMode = false;
    private double scale = 0.5;
    private int shiftX = (int)(width * scale * scale), shiftY = (int)(height * scale * scale);
    private double timeScale = 1;

    
    public Game(GameController controller){
        super(controller, ContextType.GAME, false, true);
        load();
        setupInput();
    }
            
    @Override
    public void mouseMoved(double x, double y, double velocityX, double velocityY) {
        mouseX += velocityX / scale;
        mouseY += velocityY / scale;
        realMouseX = mouseX;
        realMouseY = mouseY;
        if(rulerMode){
            double dx = realMouseX - startMouseX;
            double dy = realMouseY - startMouseY;
            rulerLength = Math.sqrt(dx * dx + dy * dy);
        }else if(panMode){
            shiftX += x;
            shiftY += y;
        }else{
            mouseItem.setVelocity(velocityX, velocityY);
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
            if(collision.getTimeToCollision() != Shape.NO_COLLISION && collision.getTimeToCollision() <= timeLeft){
                // for the first collisionTimes.length collisions, the collision rate will be inacurate;
                collisionTimes[back] = currentGameTime;
                int front = (back + 1) % collisionTimes.length;
                double dt = collisionTimes[back] - collisionTimes[front];
                back = front;
                collisionRate = collisionTimes.length / dt;
                handleCollision(collision, collisionRate);
//                paused = true;
                numCollision++;
            }
            timeLeft -= updateTime;
        }
    }
    
    private void updatePositions(double elapsedTime){
            updateCircles(elapsedTime);
            updatePolygons(elapsedTime);
            updateBoxes(elapsedTime);
    }
    
    private void updateBoxes(double elapsedTime){
        for(AABBEntity box: aabBoxs){
            box.update(elapsedTime);
        }
    }
    
    private void updatePolygons(double elapsedTime){
        for(PolygonEntity polygon: polygons){
            polygon.update(elapsedTime);
        }
    }
    
    private void updateCircles(double elapsedTime){
        double k = 0.000005;
        double d = 0.0005;
        for(CircleEntity circle: circles){
            circle.update(elapsedTime);
//            circle.addForce((mouseX - circle.getX()) * k * elapsedTime - d * circle.getDX(), (mouseY - circle.getY()) * k * elapsedTime - d * circle.getDY());
        }
    }
    private void handleCollision(Collision collision, double collisionsPerMilli){
        Physics.performCollision(collision, 0.7, 0.1, collisionsPerMilli);
    }

    @Override
    public void draw(Graphics2D g) {
        g.clearRect(0, 0, 300, 200);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        g.translate(shiftX, shiftY);
        g.scale(scale, scale);
        for(PolygonEntity polygon: polygons){
            polygon.draw(g);
        }
        for(CircleEntity circle: circles){
            circle.draw(g);
        }
        for(AABBEntity box: aabBoxs){
            box.draw(g);
        }
        if(rulerMode){
            g.setColor(Color.BLACK);
            g.drawLine((int)startMouseX, (int)startMouseY, (int)realMouseX, (int)realMouseY);
            g.drawString(rulerLength + "", (int)realMouseX, (int)realMouseY);
        }
        g.setColor(Color.RED);
//        int r = 50;
//        g.fillRect((int)(mouseX - r), (int)(mouseY - r), r + r, r + r);
        g.scale(1 / scale, 1 / scale);
        g.translate(-shiftX, -shiftY);
        drawStats(g);
    }
    
    private void drawStats(Graphics2D g){
        g.setColor(Color.BLACK);
        g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        g.drawString("          fps: " + controller.getFrameRate(), 50, 50);
        g.drawString("          ups: " + controller.getUpdateRate(), 50, 70);
        g.drawString("nextCollision: " + timeToCollision / 1000, 50, 90);
        g.drawString("    collision: " + collisionX + " " + collisionY, 50, 110);
        g.drawString("collisionRate: " + collisionRate, 50, 130);
        g.drawString("   collisions: " + numCollision, 50, 150);
        g.drawString(" num of shapes: " + (circles.size() + polygons.size() + aabBoxs.size()), 50, 170);
        g.drawString("mouseItem pos: " + mouseItem.getX() + ", " + mouseItem.getY(), 50, 190);
        g.drawString("mouseItem velX: " + mouseItem.getDX(), 50, 210);
        g.drawString("mouseItem velY: " + Math.abs(mouseItem.getDY()), 50, 240);
    }
    
    public void setSeed(long seed){
        this.seed = seed;
    }
    
    private void load(){
        Polygon.setRandomSeed(seed);
        circles.clear();
        polygons.clear();
        aabBoxs.clear();
        collisionDetector.clearCollisions();
        AABBMode();
        polygonMode();
//        ballMode();
        collisionDetector.setCollisionPair(0, 0);
        mouseItem = aabBoxs.get(0);
        mouseItem.setMass(0.0001);
        mouseItem.setColor(Color.DARK_GRAY);
//        polygons.get(2).setMass(10000000D);
    }
    
    private void AABBMode(){
        double width = 100;
        double height = 100;
        double padding = 700;
        int rows = 2;
        int columns = 1;
        double borderX = this.width  / 2;
        double borderY = this.height / 2;
        double  offsetX = width/2 + borderX;
        double offsetY = height/2 + borderY;
        for(int y = 0; y < rows; y++){
            for(int x = 0; x < columns; x++){
                double xPos = offsetX;//x * (width + padding) + offsetX;
                double yPos = offsetY;//y * (height + padding) + offsetY;
                AABBEntity box = new AABBEntity(xPos, yPos, width, height);
                aabBoxs.add(box);
                AABBShape aabb = new AABBShape(xPos, yPos, width, height, box);
                collisionDetector.addShape(aabb, 0);
            }
        }
    }
    
    private void polygonMode(){
        double minRadius = 300;
        double maxRadius = 500;
        int minPoints = 4;
        int maxPoints = 4;
        int padding = 5;
        int rows = 2;
        int columns = 1;
        double borderX = (width - columns * (maxRadius * 2 + padding)) / 2;
        double borderY = (height - rows * (maxRadius * 2 + padding)) / 2;
//        double offsetX = borderX + maxRadius;
//        double offsetY = borderY + maxRadius;
        double offsetX = width / 2;
        double offsetY = height / 2;
        Polygon.setRandomSeed(104523);
        for(int y = 0; y < rows; y++){
            for(int x = 0; x < columns; x++){
//                double xPos = x * (maxRadius * 2 + padding) + offsetX;
//                double yPos = y * (maxRadius * 2 + padding) + offsetY;
                double xPos = offsetX;
                double yPos = offsetY;
                PolygonEntity polygon = new PolygonEntity(xPos, yPos, minRadius, maxRadius, minPoints, maxPoints);
                polygons.add(polygon);
                collisionDetector.addShape(polygon.getPolygonShape(), 0);
            }
        }
    }
    
    private void ballMode(){
        double radius = 200;
        int padding = 0;
        int rows = 1;
        int columns = 1;
        double borderX = (width - columns * (radius * 2 + padding)) / 2;
        int borderY = 0;
//        double offsetX = borderX + radius;
//        double offsetY = borderY - radius;
        double offsetX = width / 2;
        double offsetY = height / 2;
        for(int y = 0; y < rows; y++){
            for(int x = 0; x < columns; x++){
//                double xPos = x * (radius * 2 + padding) + offsetX;
//                double yPos = Math.sin(x) * 10 + y * (radius * 2 + padding) + offsetY;
                double xPos = offsetX;
                double yPos = offsetY;
                CircleEntity circle = new CircleEntity(xPos, yPos, radius);
                circles.add(circle);
                CircleShape circleShape = new CircleShape(xPos, yPos, 0, 0, radius, circle);
                collisionDetector.addShape(circleShape, 0);
            }
        }
    }
    
    private void setupInput(){
        
        controller.setContextBinding(contextType, InputCode.KEY_ESCAPE, Action.EXIT_GAME);
        bindAction(Action.EXIT_GAME, new ActionHandler() {
            @Override
            public void startAction(int inputCode) {
            }

            @Override
            public void stopAction(int inputCode) {
                load();
                controller.exitContext();
            }
        });
        
        final double thrust = 0.01;
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
                mouseItem.addForce(0, -thrust);
//                timeScale += timeSpeedIncrement;
            }

            @Override
            public void stopAction(int inputCode) {
            }
        });
        
        controller.setContextBinding(contextType, InputCode.KEY_DOWN, Action.GAME_DOWN);
        bindAction(Action.GAME_DOWN, new ActionHandler() {
            @Override
            public void startAction(int inputCode) {
                mouseItem.addForce(0, thrust);
//                if(timeScale - timeSpeedIncrement > 0){
//                    timeScale -= timeSpeedIncrement;
//                }
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
                load();
                paused = false;
            }
        });
        
        controller.setContextBinding(contextType, InputCode.MOUSE_LEFT_BUTTON, Action.MENU_MOUSE_SELECT);
        bindAction(Action.MENU_MOUSE_SELECT, new ActionHandler() {

            @Override
            public void startAction(int inputCode) {
                startMouseX = realMouseX;
                startMouseY = realMouseY;
                dragging = true;
            }

            @Override
            public void stopAction(int inputCode) {
                dragging = false;
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
                if(scale < zoom){
                    scale = zoom;
                }
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

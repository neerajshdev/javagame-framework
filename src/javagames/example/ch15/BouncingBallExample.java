package javagames.example.ch15;

import javagames.util.Matrix3x3f;
import javagames.util.SimpleFramework;
import javagames.util.Utility;
import javagames.util.Vector2f;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Random;

public class BouncingBallExample extends SimpleFramework {
    class Ball{
        Vector2f position;
        Vector2f velocity;
        float radius;
        Color color;
    }

    private Ball[] balls;
    private int numOFBalls = 5;
    private final int MAX_BALLS = 500;
    private final int MIN_BALLS = 2;
    private final int MULTIPLE = 2;

    private float worldRight = appWorldWidth/2;
    private float worldLeft = -appWorldWidth/2;
    private float worldTop = appWorldHeight/2;
    private float worldBottom = -appWorldHeight/2;
    private final Random random = new Random();


    BouncingBallExample() {
        appWidth = 600;
        appHeight = 600;
        appBackground = Color.white;
        appMaintainRatio = true;
    }

    @Override
    protected void initialize() {
        super.initialize();
        createBalls();
    }

    private void createBalls() {
        balls = new Ball[numOFBalls];
        for (int i = 0; i < balls.length; i++) {
            Ball ball = new Ball();
            ball.radius = random.nextFloat() * 0.20f;
            ball.position = new Vector2f(worldLeft + ball.radius,worldTop - ball.radius);
            ball.color = new Color(random.nextInt());
            ball.velocity = new Vector2f(random.nextFloat()* 0.5f, random.nextFloat() * -0.5f);
            balls[i] = ball;
        }
    }

    @Override
    protected void processInput(float t) {
        super.processInput(t);
        if(keyboard.keyDownOnce(KeyEvent.VK_SPACE)) {
            createBalls();
        }
        if(keyboard.keyDownOnce(KeyEvent.VK_UP)) {
            numOFBalls *= MULTIPLE;
            if (numOFBalls > MAX_BALLS) {
                numOFBalls = MAX_BALLS;
            }
            createBalls();
        }

        if(keyboard.keyDownOnce(KeyEvent.VK_DOWN)) {
            numOFBalls /= MULTIPLE;
            if (numOFBalls < MIN_BALLS) {
                numOFBalls = MIN_BALLS;
            }
            createBalls();
        }
    }

    @Override
    protected void updateObjects(float t) {
        super.updateObjects(t);
        for (Ball ball : balls) {
            ball.position = ball.position.add(ball.velocity.mul(t));
            float r = ball.radius;
            if (ball.position.x - r < worldLeft) {
                ball.position.x = worldLeft + r;
                ball.velocity.x *= -1;
            } else if (ball.position.x + r > worldRight) {
                ball.position.x = worldRight - r;
                ball.velocity.x *= -1;
            } else if (ball.position.y + r > worldTop) {
                ball.position.y = worldTop - r;
                ball.velocity.y *= -1;
            } else if (ball.position.y - r < worldBottom) {
                ball.position.y = worldBottom + r;
                ball.velocity.y *= -1;
            }
        }
    }

    @Override
    protected void render(Graphics g) {
//        Utility.drawString(g, textPos, 30,
//                "Balls: " + numOFBalls,
//                "Hit UP & DOWN button to increase or decrease"
//        );
        for (Ball ball : balls) {
            drawBalls((Graphics2D) g, ball);
        }
        super.render(g);
    }



    private void drawBalls(Graphics2D g, Ball ball) {
//        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setColor(ball.color);
        Matrix3x3f viewport = getViewportTransform();
        //circle center
        Vector2f c = ball.position;
        float r = ball.radius;
        Vector2f topLeft = new Vector2f(c.x - r, c.y + r);
        Vector2f bottomRight = new Vector2f(c.x + r, c.y - r);
        // transform the points into viewport
        topLeft = viewport.mul(topLeft);
        bottomRight = viewport.mul(bottomRight);
        int circleX = (int) topLeft.x;
        int circleY = (int) topLeft.y;
        int circleW = (int)(bottomRight.x - topLeft.x);
        int circleH = (int)(bottomRight.y - topLeft.y);
        g.fillOval(circleX, circleY, circleW,circleH);
    }

    @Override
    protected void terminate() {
        super.terminate();
    }

    public static void main(String[] args) {
        launchApp(new BouncingBallExample());
    }
}

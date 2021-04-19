package javagames.example.ch4;

import javagames.util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.util.concurrent.locks.ReentrantLock;

public class CannonExample extends JFrame implements Runnable {

    private final float WORLD_WIDTH = 5f;
    private final float WORLD_HEIGHT = 5f;

    private volatile boolean running;
    private Thread gameThread;
    private BufferStrategy bs;
    private RelativeMouseInput mouse;
    private KeyboardInput keyboard;
    private FrameRate frameRate;
    private Canvas canvas;


    private float x, y;
    private Vector2f[] cannon;
    private Vector2f[] cannonCpy;

    private boolean shoot;
    private Vector2f bullet;
    private Vector2f bulletCpy;

    private Vector2f velocity;
    private float v;
    private float cannonRot, w;  // cannonRot will be increase or decrease by wt





    public void createAndShowGUI() {
        canvas = new Canvas();
        canvas.setBackground(Color.white);
        canvas.setSize(600, 500);
        canvas.setIgnoreRepaint(true);
        setIgnoreRepaint(true);
        getContentPane().add(canvas);
        pack();
        setTitle("Animation");
        setVisible(true);
        canvas.requestFocus();

        canvas.createBufferStrategy(2);
        bs = canvas.getBufferStrategy();

        // add mouse listener
        mouse = new RelativeMouseInput(canvas);
        canvas.addMouseListener(mouse);
        canvas.addMouseMotionListener(mouse);
        canvas.addMouseWheelListener(mouse);

        // add keyboard listener
        keyboard = new KeyboardInput();
        canvas.addKeyListener(keyboard);

        // create and start gameThread
        gameThread = new Thread(this);
        gameThread.setName("game thread");
        gameThread.start();
    }

    private void sleep() {
        try {
            Thread.sleep(0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void init() {
        frameRate = new FrameRate();
        frameRate.initialize();
        v = 2;

        // cannon model vertices
        cannon = new Vector2f[] {
                new Vector2f(-0.5f,0.125f),
                new Vector2f(0.5f,0.125f),
                new Vector2f(0.5f,-0.125f),
                new Vector2f(-0.5f,-0.125f)
        };
        cannonCpy = new Vector2f[cannon.length];

        velocity = new Vector2f();
        cannonRot = 0f;
        w = (float) Math.PI / 2;
    }

    @Override
    public void run() {
        running = true;
        init();
        long ct, lt = System.nanoTime(), dt;
        while (running) {
            ct = System.nanoTime();
            dt = ct - lt;
            loop((float) (dt / 1.0e9));
            lt = ct;
            sleep();
        }
    }

    // t : time in seconds
    private void loop(float t) {
        processInput(t);
        gameUpdate(t);
        renderFrame();
    }

    private void processInput(float t) {
        keyboard.poll();
        mouse.poll();

        if (keyboard.keyDown(KeyEvent.VK_W)) {
            v += 2 * t;
        }
        if (keyboard.keyDown(KeyEvent.VK_S)) {
            v -= 2 * t;
        }

        if (keyboard.keyDown(KeyEvent.VK_A)) {
            cannonRot += w * t;
        }

        if (keyboard.keyDown(KeyEvent.VK_D)) {
            cannonRot -= w * t;
        }

        if (keyboard.keyDown(KeyEvent.VK_UP)) {
            y += t;
        }
        if (keyboard.keyDown(KeyEvent.VK_DOWN)) {
            y -= t;
        }
        if (keyboard.keyDown(KeyEvent.VK_LEFT)) {
            x -= t;
        }
        if (keyboard.keyDown(KeyEvent.VK_RIGHT)) {
            x += t;
        }

        if (keyboard.keyDownOnce(KeyEvent.VK_SPACE)) {
            shoot = true;
        }
    }

    private void gameUpdate(float t) {
        Matrix3x3f cannonMat = Matrix3x3f.rotate(cannonRot)
                .mul(Matrix3x3f.scale(0.75f, 0.75f))
                .mul(Matrix3x3f.translate(x,y));

        for (int i = 0;  i < cannonCpy.length; i++) {
            cannonCpy[i] = new Vector2f(cannonMat.mul(cannon[i]));
        }

        if (bullet != null) {
            if (bullet.x > 5 || bullet.y > 5) {
                bullet = null;
            } else
            {
                velocity.y = velocity.y - 9.8f * t;
                bullet.x = bullet.x + velocity.x * t;
                bullet.y = bullet.y + velocity.y * t;
            }
        }

        if (shoot) {
            bullet = cannonMat.mul(new Vector2f(.5f, 0));
            velocity.x = (float) (v * Math.cos(cannonRot));
            velocity.y = (float) (v * Math.sin(cannonRot));
//            bullet = new Vector2f(0.5f, 0);

            shoot = false;
        }
    }

    private void renderFrame() {
        do {
            do {
                Graphics g = null;
                try {
                    g = bs.getDrawGraphics();
                    g.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
                    render(g);
                } finally {
                    if (g != null) {
                        g.dispose();
                    }
                }
            } while (bs.contentsRestored());
            bs.show();
        } while (bs.contentsLost());
    }
    /*  Render the frame
     *  */
    private void render(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

        // draw frameRate
        g2d.setColor(Color.black);
        frameRate.calculate();
        g2d.drawString(frameRate.getFps(), 20, 20);
        g2d.drawString(String.format("Bullet speed: %.2f", v), 20 , 40);
        g2d.drawString( "Press W to increase the cannon power", 20 , 60);
        g2d.drawString( "Press S to decrease the cannon power", 20 , 80);
        g2d.drawString( "USE up, down, left, right keys to move the cannon", 20 , 100);
        g2d.drawString( "Press A and D to rotate", 20 , 120);
        g2d.drawString( "Press SPACE to shoot", 20 , 140);


        float sx = (canvas.getWidth() - 1 ) / WORLD_WIDTH;
        float sy = (canvas.getHeight() - 1) / WORLD_HEIGHT;

        float tx = (float) canvas.getWidth() / 2;
        float ty = (float) canvas.getHeight() / 2;

        Matrix3x3f viewport = Matrix3x3f.scale(sx, -sy).mul(Matrix3x3f.translate(tx, ty));


        for (int i = 0; i < cannonCpy.length; i ++) {
            cannonCpy[i] = viewport.mul(cannonCpy[i]);
        }
        // draw the cannon
        g2d.setColor(new Color(164,116,73));
        drawPolygon(g2d, cannonCpy);

        // draw the bullet
        if (bullet != null) {
            Vector2f b = viewport.mul(bullet);
            g2d.setColor(Color.blue);
            g2d.fillRect((int )b.x,(int) b.y, 10 , 8);
        }

    }

    private void drawPolygon(Graphics g, Vector2f[] p) {
        Vector2f lastPoint = p[p.length - 1];

        for (int i = 0; i < p.length; i ++) {
            Vector2f point = p[i];
            g.drawLine((int) lastPoint.x, (int)lastPoint.y, (int)point.x, (int)point.y );
            lastPoint = point;
        }
    }


    public void shutDown() {
        running = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.exit(0);
    }

    public static void main(String[] args) {
        CannonExample app = new CannonExample();
        app.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                app.shutDown();
            }
        });

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                app.createAndShowGUI();
            }
        });
    }
}

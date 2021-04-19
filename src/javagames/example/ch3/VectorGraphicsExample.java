package javagames.example.ch3;

import javagames.util.FrameRate;
import javagames.util.KeyboardInput;
import javagames.util.RelativeMouseInput;
import javagames.util.Vector2f;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;

public class VectorGraphicsExample extends JFrame implements Runnable {

    private final double TIME_PER_FRAME = 20.0;

    private BufferStrategy bs;
    private FrameRate frameRate;
    private Thread gameThread;
    private Canvas canvas;
    private KeyboardInput keyboard;
    private RelativeMouseInput mouse;
    private volatile boolean running;

    private Vector2f[] polygon;
    private Vector2f[] world;

    private float tx, ty;
    private float tr = 0;

    private boolean up, down, left, right, rotQ, rotE;


    VectorGraphicsExample() {
        frameRate = new FrameRate();

        polygon = new Vector2f[] {new Vector2f(-10, 8)
                , new Vector2f(0, 0)
                , new Vector2f(-10, -8)
                , new Vector2f(10, 0)};

        world = new Vector2f[polygon.length];
    }

    public void createAndShowGUI() {
        canvas = new Canvas();
        canvas.setSize(800, 600 );
        canvas.setIgnoreRepaint(true);
        canvas.setBackground(Color.BLACK);
        getContentPane().add(canvas);

        setIgnoreRepaint(true);
        pack();

        tx = (float) canvas.getWidth() /  2;
        ty = (float) canvas.getHeight() / 2;

        setTitle("Vector graphics example");
        setVisible(true);

        canvas.requestFocus();

        //add key event listener
        keyboard = new KeyboardInput();
        canvas.addKeyListener(keyboard);

        // add mouse listener
        mouse = new RelativeMouseInput(canvas);
        addMouseListener(mouse);
        addMouseMotionListener(mouse);
        addMouseWheelListener(mouse);

        canvas.createBufferStrategy(2);
        bs = canvas.getBufferStrategy();

        gameThread = new Thread(this);
        gameThread.setName("gameThread");
        gameThread.start();

    }

    public void run() {

        running = true;
        frameRate.initialize();
        double delta = 0;
        double lastTime = System.currentTimeMillis();
        double currentTime;
        double diff;

        while (running) {

            currentTime = System.currentTimeMillis();
            diff = currentTime - lastTime;
            delta += diff;

            if (delta > TIME_PER_FRAME) {
               gameLoop(delta);
               delta -= TIME_PER_FRAME;
            }

            lastTime = currentTime;
        }
    }

    private void gameLoop(double delta) {
        processEvents();
        update(delta);
        render();
    }

    private void processEvents() {
        mouse.poll();
        keyboard.poll();

        up = keyboard.keyDown(KeyEvent.VK_W);
        down = keyboard.keyDown(KeyEvent.VK_S);
        left = keyboard.keyDown(KeyEvent.VK_A);
        right = keyboard.keyDown(KeyEvent.VK_D);

        rotQ = keyboard.keyDown(KeyEvent.VK_Q);
        rotE = keyboard.keyDown(KeyEvent.VK_E);

    }

    private void update(double deltaTime) {

        // copy data to world
        for (int i = 0; i < polygon.length; i ++) {
            world[i] = new Vector2f(polygon[i]);
        }

        // convert the delta time in seconds
        deltaTime *= 1e-3;


        float speed = 50f;
        double dm = speed * deltaTime;

        float w = 3f;
        double dr = w * deltaTime;


        if (up) {
            ty -= dm;
        }
        if (down) {
            ty += dm;
        }

        if (left) {
            tx -= dm;
        }

        if (right) {
            tx += dm;
        }

        // rot
        if (rotQ) {
            tr -= dr;
        }

        if (rotE) {
            tr += dr;
        }

        // apply transformation
        for (Vector2f vector2f : world) {
//            vector2f.rotate(tr);
//            vector2f.translate(tx, ty);
        }
    }

    private void render() {
        do {
            do {
                Graphics g = null;
                try {
                    g = bs.getDrawGraphics();
                    g.clearRect(0, 0, getWidth(), getHeight());
                    drawOnBackFrame(g);
                } finally {
                    if (g != null) {
                        g.dispose();
                    }
                }
            } while (bs.contentsRestored());
            bs.show();
        }while (bs.contentsLost());
    }

    private void drawOnBackFrame(Graphics g) {
        g.setColor(Color.green);

        frameRate.calculate();
        g.drawString(frameRate.getFps(), 20, 30);

        Vector2f lastPoint = world[world.length - 1];
        Vector2f point = null;

        for (int i = 0; i < world.length; i ++) {
            point = world[i];
            g.drawLine((int) lastPoint.x, (int) lastPoint.y, (int) point.x, (int) point.y);

            lastPoint = point;
        }
    }



    /* called from event dispatch thread */
    private void quit() {

        running = false;

        /* edt waits for gameThread to stop */
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /* quit the app now */
        System.exit(0);
    }


    public static void main(String[] args) {

        VectorGraphicsExample app = new VectorGraphicsExample();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                app.createAndShowGUI();
            }
        });


        app.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                app.quit();
            }
        });
    }

}

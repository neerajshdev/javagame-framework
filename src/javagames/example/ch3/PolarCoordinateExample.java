package javagames.example.ch3;


import javagames.util.FrameRate;
import javagames.util.KeyboardInput;
import javagames.util.RelativeMouseInput;
import javagames.util.Vector2f;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;


public class PolarCoordinateExample extends JFrame implements Runnable {

    private static final int SCREEN_W = 640;
    private static final int SCREEN_H = 480;

    private FrameRate frameRate;
    private BufferStrategy bs;
    private volatile boolean running;
    private Thread gameThread;
    private RelativeMouseInput mouse;
    private KeyboardInput keyboard;

    private Vector2f mousePos;
    private Vector2f location;
    private double radius;
    private double angle;

    private Vector2f[] polar;
    private Vector2f[] world;

    public PolarCoordinateExample() {

    }

    protected void createAndShowGUI() {
        Canvas canvas = new Canvas();
        canvas.setSize(SCREEN_W, SCREEN_H);
        canvas.setBackground(Color.BLACK);
        canvas.setIgnoreRepaint(true);
        getContentPane().add(canvas);
        setTitle("Polar Coordinate Example");
        setIgnoreRepaint(true);
        pack();
        // Add key listeners
        keyboard = new KeyboardInput();
        canvas.addKeyListener(keyboard);
        // Add mouse listeners
        // For full screen : mouse = new RelativeMouseInput( this );
        mouse = new RelativeMouseInput(canvas);
        canvas.addMouseListener(mouse);
        canvas.addMouseMotionListener(mouse);
        canvas.addMouseWheelListener(mouse);
        setVisible(true);
        canvas.createBufferStrategy(2);
        bs = canvas.getBufferStrategy();
        canvas.requestFocus();
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void run() {
        running = true;
        initialize();
        while (running) {
            gameLoop();
        }
    }

    private void gameLoop() {
        processInput();
        renderFrame();
        sleep(10L);
    }

    private void renderFrame() {
        do {
            do {
                Graphics g = null;
                try {
                    g = bs.getDrawGraphics();
                    g.clearRect(0, 0, getWidth(), getHeight());
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

    private void render(Graphics g) {

        g.setFont(new Font( "Courier New", Font.BOLD, 24 ));
        g.setColor(Color.green);

        frameRate.calculate();
        g.drawString(frameRate.getFps(), 20, 20);
        g.drawString("(" + mousePos.x + " ," + mousePos.y + ")", 20, 50);

        // draw the coordinates
        g.setColor(Color.gray);
        for (int i = 0; i < world.length; i += 2) {
            Vector2f a = world[i];
            Vector2f b = world[i + 1];
            g.drawLine((int) a.x, (int) a.y, (int) b.x, (int) b.y);
        }

        //draw the radius
        g.drawLine((int) location.x, (int ) location.y, (int) mousePos.x, (int) mousePos.y);

        //draw the arc
        g.drawArc((int)(location.x - radius), (int)(location.y - radius), (int)(2 * radius), (int) (2 * radius) , 0,(int) angle);
        g.setColor(Color.green);
        g.drawString((int)radius + ", " + (int) angle, (int) mousePos.x, (int) mousePos.y);

    }

    private void sleep(long sleep) {
        try {
            Thread.sleep(sleep);
        } catch (InterruptedException ex) {
        }
    }

    private void initialize() {
        frameRate = new FrameRate();
        frameRate.initialize();
        location = new Vector2f((float) SCREEN_W / 2, (float) SCREEN_H / 2);
        polar = new Vector2f[] {
                new Vector2f((float) -SCREEN_W / 2, 0),
                new Vector2f((float) SCREEN_W / 2, 0),
                new Vector2f(0, (float) -SCREEN_H / 2),
                new Vector2f(0,(float) SCREEN_H / 2 )
        };
    }

    private void processInput() {
        keyboard.poll();
        mouse.poll();

        Point p = mouse.getPosition();
        mousePos = new Vector2f(p.x , p.y);

        world = new Vector2f[polar.length];
//        // copy data
//        for (int i = 0; i < polar.length; i++) {
//            world[i] = new Vector2f(polar[i]);
//        }
//
//        for (Vector2f d : world) {
//            d.translate(location.x, location.y);
//        }
//
//        Vector2f v = mousePos.minus(location);
//        radius = v.magnitude();
//        angle = -Math.toDegrees(Math.atan2(v.y, v.x));

    }



    protected void onWindowClosing() {
        try {
            running = false;
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    public static void main(String[] args) {
        final PolarCoordinateExample app = new PolarCoordinateExample();
        app.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                app.onWindowClosing();
            }
        });
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                app.createAndShowGUI();
            }
        });
    }
}
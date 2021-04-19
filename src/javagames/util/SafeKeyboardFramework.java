package javagames.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;

public class SafeKeyboardFramework extends JFrame implements Runnable {

    private Thread gameThread;
    private BufferStrategy bs;
    private volatile boolean running;

    protected Canvas canvas;
    protected RelativeMouseInput mouse;
    protected SafeKeyboardInput keyboard;
    protected FrameRate frameRate;

    protected Color appBackground = Color.BLACK;
    protected Color appBorder = Color.LIGHT_GRAY;
    protected Color appFPSColor = Color.GREEN;
    protected Font appFont = new Font( "Courier New", Font.PLAIN, 14 );
    protected Font appFPSFont = new Font( "Courier New", Font.BOLD, 20 );
    protected String appTitle = "TBD-Title";
    protected float appBorderScale = 0.8f;
    protected int appWidth = 640;
    protected int appHeight = 480;
    protected float appWorldWidth = 2.0f;
    protected float appWorldHeight = 2.0f;
    protected long appSleep = 10L;
    protected boolean appMaintainRatio = false;
    protected int textPos = 0;


    public void createAndShowGUI() {
        canvas = new Canvas();
        canvas.setBackground(appBackground);
        canvas.setIgnoreRepaint(true);

        getContentPane().add(canvas);

        setTitle(appTitle);
        setLocationByPlatform(true);
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
        keyboard = new SafeKeyboardInput();
        canvas.addKeyListener(keyboard);

        if (appMaintainRatio) {
            setSize(appWidth, appHeight);
            getContentPane().setBackground(appBorder);
            setLayout(null);
            getContentPane().addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    onComponentResized();
                }
            });
        } else {
            canvas.setSize(appWidth, appHeight);
            pack();
        }

        // create and start gameThread
        gameThread = new Thread(this);
        gameThread.setName("game thread");
        gameThread.start();
    }

    private void onComponentResized() {
        Dimension size = getContentPane().getSize();
        int vw = (int)(size.width * appBorderScale);
        int vh = (int)(size.height * appBorderScale);
        int vx = (size.width - vw) / 2;
        int vy = (size.height - vh) / 2;
        int newW = vw;
        int newH = (int)(vw * appWorldHeight / appWorldWidth);
        if( newH > vh ) {
            newW = (int)(vh * appWorldWidth / appWorldHeight);
            newH = vh;
        }
// center
        vx += (vw - newW) / 2;
        vy += (vh - newH) / 2;
        canvas.setLocation( vx, vy );
        canvas.setSize( newW, newH );
    }

    private void sleep(Long st) {
        try {
            Thread.sleep(st);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        long ct, lt = System.nanoTime(), et;
        initialize();
        running = true;
        while (running) {
            ct = System.nanoTime();
            et = ct - lt;  // elapsed time
            gameLoop((float) (et / 1E9));
            lt = ct;
            sleep(appSleep);
        }
        terminate();
    }

    protected void initialize () {
        frameRate = new FrameRate();
        frameRate.initialize();
    }

    private void gameLoop(float t) {
        processInput(t);
        updateObjects(t);
        renderFrame();
    }

    protected void processInput(float t) {
        mouse.poll();
        keyboard.poll();
    }

    protected void updateObjects(float t) {
        // should be overridden
    }

    protected void render(Graphics g) {
        g.setFont(appFPSFont);
        // draw frame rate
        g.setColor(appFPSColor);
        frameRate.calculate();
        textPos = Utility.drawString(g, 20, 0, frameRate.getFps());
    }

    private void renderFrame() {
        do{
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
            }while (bs.contentsRestored());
            bs.show();
        }while (bs.contentsLost());
    }

    protected void terminate() {
        // should be overridden from the sub class
    }

    protected Matrix3x3f getViewportTransform() {
        return Utility.createViewport(appWorldWidth, appWorldHeight
                , canvas.getWidth() - 1, canvas.getHeight() - 1);
    }

    protected Matrix3x3f getReverseViewportTransform() {
        return Utility.createReverseViewport(appWorldWidth, appWorldHeight
                , canvas.getWidth(), canvas.getHeight());
    }

    protected Vector2f getWorldMousePos() {
        Matrix3x3f screenToWorld = getReverseViewportTransform();
        Point p = mouse.getPosition();
        return screenToWorld.mul(new Vector2f(p.x, p.y));
    }

    protected Vector2f getRelativeWorldMousePos() {
        float sx = appWorldWidth / (canvas.getWidth() - 1);
        float sy = appWorldHeight / (canvas.getHeight() - 1);
        Matrix3x3f world = Matrix3x3f.scale(sx, -sy);
        Point p = mouse.getPosition();
        return world.mul(new Vector2f(p.x, p.y));
    }


    protected void onWindowClosing() {
        running = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    protected static void launchApp(final SafeKeyboardFramework app) {
        app.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                app.onWindowClosing();
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


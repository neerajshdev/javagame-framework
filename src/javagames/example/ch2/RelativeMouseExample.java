package javagames.example.ch2;


import javagames.util.FrameRate;
import javagames.util.KeyboardInput;
import javagames.util.RelativeMouseInput;


import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;

public class RelativeMouseExample extends JFrame implements Runnable {
    private FrameRate frameRate;
    private KeyboardInput keyboardInput;
    private RelativeMouseInput mouseInput;
    private BufferStrategy bs;
    private Thread gameThread;
    private boolean running;
    private Canvas canvas;

    private Point point = new Point(0, 0);
    private boolean cursorDisable;

    private final int SQUARE = 25;

    public RelativeMouseExample() {
        frameRate = new FrameRate();
    }

    public void createAndShowGui() {
        canvas = new Canvas();
        canvas.setIgnoreRepaint(true);
        canvas.setBackground(Color.BLACK);
        canvas.setSize(800, 600);
        getContentPane().add(canvas);
        setIgnoreRepaint(true);
        pack();
        setVisible(true);

        keyboardInput = new KeyboardInput();
        canvas.addKeyListener(keyboardInput);

        mouseInput = new RelativeMouseInput(canvas);
        canvas.addMouseListener(mouseInput);
        canvas.addMouseMotionListener(mouseInput);
        canvas.addMouseWheelListener(mouseInput);

        canvas.createBufferStrategy(2);
        bs = canvas.getBufferStrategy();

        canvas.requestFocus();

        gameThread = new Thread(this);
        gameThread.setName("gameThread");

        gameThread.start();

    }

    public void run() {
        running = true;
        frameRate.initialize();
        while (running) {
            processInput();
            renderFrame();
            sleep(10L);
        }
    }

    private void processInput() {
        mouseInput.poll();
        keyboardInput.poll();

        Point mousePos = mouseInput.getPosition();

        if (mouseInput.isRelative()) {
            point.x += mousePos.x;
            point.y += mousePos.y;
        } else {
            point.x = mousePos.x;
            point.y = mousePos.y;
        }

        // wrap Square to screen
        if (point.x + SQUARE < 0) {
            point.x = canvas.getWidth() - 1;
        } else if (point.x > canvas.getWidth() - 1) {
            point.x = -25;
        }

        if (point.y + SQUARE < 0) {
            point.y = canvas.getHeight() - 1;
        } else if (point.y > canvas.getHeight() - 1) {
            point.y = -25;
        }

        // press space to toggle Relative
        if (keyboardInput.keyDownOnce(KeyEvent.VK_SPACE)) {
            mouseInput.setRelative(!mouseInput.isRelative());
        }

        // press space to disable or enable the default mouse cursor
        if (keyboardInput.keyDownOnce(KeyEvent.VK_C)) {
            cursorDisable = !cursorDisable;
            if (cursorDisable) {
                disableCursor();
            } else {
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        }
    }

    private void renderFrame() {
        do {
            do {
                Graphics g = null;
                try {
                    g = bs.getDrawGraphics();
                    g.clearRect(0,0,getWidth(), getHeight());
                    render(g);
                }
                finally {
                    if (g != null) {
                        g.dispose();
                    }
                }
            }while (bs.contentsRestored());
            bs.show();
        }while (bs.contentsLost());
    }

    private void render(Graphics g) {
        g.setColor(Color.green);
        g.drawRect(point.x, point.y, SQUARE, SQUARE);
        frameRate.calculate();
        g.drawString(frameRate.getFps(), 20, 20);
        g.drawString("Relative: " + mouseInput.isRelative(), 20, 35);
        g.drawString("Press space to switch mouse mode", 20, 50);
        g.drawString("Press c to toggle relative", 20, 70);
        g.drawString(point.toString(), 20, 85);
        g.drawString("Frame width and height: " + getWidth()+ ", " + getHeight(), 20, 100);
        g.drawString("Canvas width and height: " + canvas.getWidth()+ ", " + canvas.getHeight(), 20, 120);
    }

    private void sleep(long sleepTime) {
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // disable cursor
    private void disableCursor() {
        Toolkit tk = Toolkit.getDefaultToolkit();
        Image image = tk.createImage("");
        Cursor cursor = tk.createCustomCursor(image, new Point(0, 0),"custom cursor");
        setCursor(cursor);
    }

    // call on edt
    private void shutDown() {
        running = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    public static void main(String[] args) {
        RelativeMouseExample app = new RelativeMouseExample();
        app.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                app.shutDown();
            }
        });

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                app.createAndShowGui();
            }
        });
    }

}

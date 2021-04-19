package javagames.example.ch1;

import javagames.util.FrameRate;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;

public class FullScrMode extends JFrame implements Runnable {
    // necessary fields
    private final FrameRate frameRate;
    private DisplayMode currentDisplayMode;
    private GraphicsDevice graphicsDevice;
    private BufferStrategy bs;
    private Thread gameThread;
    private volatile boolean running;

    // Constructor
    FullScrMode() {
        frameRate = new FrameRate();
    }

    private void createAndShowGui() {
        setIgnoreRepaint(true);
        setBackground(Color.BLACK);
        setUndecorated(true);

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        graphicsDevice = ge.getDefaultScreenDevice();
        currentDisplayMode = graphicsDevice.getDisplayMode();
        if (!graphicsDevice.isFullScreenSupported()) {
            System.err.println("Error: Full screen is not supported");
            System.exit(0);
        }

        graphicsDevice.setFullScreenWindow(this);
        graphicsDevice.setDisplayMode(getDisplayMode());

        createBufferStrategy(2);
        bs = getBufferStrategy();

        gameThread = new Thread(this);
        gameThread.setName("GameThread");
        gameThread.start();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
                    shutDown();
            }
        });
    }

    private void shutDown() {
        System.out.println("Stopping...");
        running = false;
        try {
            gameThread.join();
            graphicsDevice.setDisplayMode(currentDisplayMode);
            graphicsDevice.setFullScreenWindow(null);
            System.out.println("comeback to current display mode");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Stopped.");
        System.exit(0);
    }

    private DisplayMode getDisplayMode() {
        return new DisplayMode(800, 600, 32, DisplayMode.REFRESH_RATE_UNKNOWN);
    }

    public void run() {
        running = true;
        frameRate.initialize();
        while (running) {
            gameLoop();
        }
    }

    private void gameLoop() {
        do {
            do {
                Graphics g = null;
                try{
                    g = bs.getDrawGraphics();
                    g.clearRect(0, 0, getWidth(), getHeight());
                    render(g);
                }finally {
                    if (g != null)
                        g.dispose();
                }
            }while (contentsRestored());
            bs.show();
        }while (contentsLost());
    }
    private boolean contentsRestored() {
        if (bs.contentsRestored()) {
            System.out.println("Contents Restored");
            return true;
        }
        return false;
    }
    private boolean contentsLost() {
        if (bs.contentsLost()) {
            System.out.println("Contents lost");
            return true;
        }
        return false;
    }

    // render single frame 
    private void render(Graphics g) {
        frameRate.calculate();
        g.setColor(Color.red);
        g.drawString(frameRate.getFps(), 50, 50);
        g.drawString("Press EXC to exit !!" ,50, 80);
    }



    // entry point for  the execution
    public static void main(String[] args) {
        FullScrMode app = new FullScrMode();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                app.createAndShowGui();
            }
        });
    }

}

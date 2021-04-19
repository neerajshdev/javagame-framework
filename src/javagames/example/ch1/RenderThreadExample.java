package javagames.example.ch1;

import javagames.util.FrameRate;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;

public class RenderThreadExample extends JFrame implements Runnable {
    private volatile boolean running;
    private Thread gameThread;
    private FrameRate fps;
    private BufferStrategy bs;

    // create the gui and start the game thread
    public void createAndShowGui() {
        Canvas canvas = new Canvas();
        canvas.setBackground(Color.black);
        canvas.setIgnoreRepaint(true);
        canvas.setSize(300, 300);
        add(canvas);

        setTitle("Active rendering");
        setIgnoreRepaint(true);
        pack();
        setVisible(true);

        canvas.createBufferStrategy(2);
        bs = canvas.getBufferStrategy();

        gameThread = new Thread(this);
        gameThread.setName("gameThread");
        gameThread.start();
    }

    public void run() {
        running = true;
        fps = new FrameRate();
        fps.initialize();
        while (running) {
            gameLoop();
        }
    }

    private void gameLoop() {
        do{
            do {
                Graphics g;
//                try {
                contentsLost();
                    g = bs.getDrawGraphics();
                    g.clearRect(0,0, getWidth(), getHeight());
                    render(g);
//                }finally {
                g.dispose();
                //                }

            }while (contentsRest());
            bs.show();
        } while (contentsLost());
    }

    private boolean contentsRest() {
        if (bs.contentsRestored()) {
            System.out.println("Contents were restored");
            return true;
        }
        return false;
    }

    private boolean contentsLost() {
        if (bs.contentsLost()) {
            System.out.println("Contents were lost");
            return true;
        }
        return false;
    }

    private void render(Graphics g) {
        fps.calculate();
        g.setColor(Color.white);
        g.drawString(fps.getFps(), 10, 20);
    }

    private void onWindowClosing() {
        try {
            System.out.println("Stopping the thread....");
            running = false;
            gameThread.join();
            System.out.println("Stopped..");

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    public static void main(String[] args) {
        RenderThreadExample app = new RenderThreadExample();
        app.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
              app.onWindowClosing();
            }
        });
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                app.createAndShowGui();
                System.out.println(Thread.currentThread().getName());
            }
        });
    }

}

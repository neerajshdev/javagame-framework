package javagames.example.ch2;

import javagames.util.KeyboardInput;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class SimpleKeyboardTest extends JFrame implements Runnable {

    //Fields
    private volatile boolean running;
    private KeyboardInput keys;
    private Thread gameThread;
    private boolean space;

    public SimpleKeyboardTest() {
        keys = new KeyboardInput();
    }

    public void createAndShowGui() {
        setTitle("Simple keyboard test");
        setSize(300, 300);
        setVisible(true);

        addKeyListener(keys);
        //game thread
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void run() {
        running = true;
        while (running) {
            gameLoop();
        }
    }

    private void gameLoop() {
        if (keys.keyDown(KeyEvent.VK_SPACE)) {
            if (!space) {
                System.out.println("VK_SPACE");
            }
            space = true;
        } else {
            space = false;
        }

        if (keys.keyDown(KeyEvent.VK_UP)) {
            System.out.println("VK_UP");
        }
        if (keys.keyDown(KeyEvent.VK_DOWN)) {
            System.out.println("VK_DOWN");
        }
        if (keys.keyDown(KeyEvent.VK_LEFT)) {
            System.out.println("VK_LEFT");
        }
        if (keys.keyDown(KeyEvent.VK_RIGHT)) {
            System.out.println("VK_RIGHT");
        }

        try{
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void shutDown() {
        running = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        System.exit(0);

    }


    // Entry point
    public static void main(String[] args) {
        SimpleKeyboardTest app = new SimpleKeyboardTest();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                app.createAndShowGui();
            }
        });

        app.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                app.shutDown();
            }
        });

        System.out.println("The end of main thread");
    }
}

package javagames.example.ch2;

import javagames.util.FrameRate;
import javagames.util.KeyboardInput;
import javagames.util.SimpleMouseInput;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;

public class SimpleMouseExample extends JFrame implements Runnable {
    private Thread gameThread;
    private volatile boolean running;
    private FrameRate frameRate;
    private KeyboardInput keyboardInput;
    private SimpleMouseInput mouseInput;
    private BufferStrategy bs;

    private final Color[] COLORS = {Color.RED, Color.GREEN, Color.YELLOW, Color.BLUE };
    private int colorIndex;

    private boolean drawingLine;
    private ArrayList<Point> lines = new ArrayList<>();


    public SimpleMouseExample() {
        frameRate = new FrameRate();
        keyboardInput = new KeyboardInput();
        mouseInput = new SimpleMouseInput();
    }

    public void createAndShowGui() {
        setTitle("Simple Mouse Example");
        Canvas canvas = new Canvas();
        canvas.setSize(600, 500);
        canvas.setBackground(Color.BLACK);
        canvas.setIgnoreRepaint(true);
        getContentPane().add(canvas);
        setIgnoreRepaint(true);
        pack();
        setVisible(true);

        
        canvas.addMouseListener(mouseInput);
        canvas.addMouseMotionListener(mouseInput);
        canvas.addMouseWheelListener(mouseInput);

        canvas.addKeyListener(keyboardInput);

        canvas.createBufferStrategy(2);
        bs = canvas.getBufferStrategy();
        canvas.requestFocus();

        gameThread = new Thread(this);
        gameThread.setName("GAME_THREAD");
        gameThread.start();
    }

    private void sleep(long sometime) {
        try {
            Thread.sleep(sometime);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        running = true;
        frameRate.initialize();
        while (running) {
           processInput();
           renderFrame();
           sleep(10);
        }
    }

   private void processInput() {
        keyboardInput.poll();
        mouseInput.poll();

        if (keyboardInput.keyDownOnce(KeyEvent.VK_SPACE)) {
            System.out.println("VK_SPACE");
        }

        if (mouseInput.buttonDownOnce(MouseEvent.BUTTON1)) {
            drawingLine = true;
        }

        if (mouseInput.buttonDown(MouseEvent.BUTTON1)) {
            lines.add(mouseInput.getMousePos());
        } else {
            lines.add(null);
            drawingLine = false;
        }

        if (keyboardInput.keyDownOnce(KeyEvent.VK_C)) {
            lines.clear();
        }

   }

   private void renderFrame() {
        do{
            do {
                Graphics g = null;
                try {
                    g = bs.getDrawGraphics();
                    g.clearRect(0,0, getWidth(), getHeight());
                    render(g);
                }finally {
                    if (g != null)
                    g.dispose();
                }
            } while (bs.contentsRestored());
            bs.show();
        }while (bs.contentsLost());
   }

   private void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

        colorIndex += mouseInput.getPolledNotches();
        Color color = COLORS[Math.abs(colorIndex % COLORS.length)];
        g2d.setColor(color);

        frameRate.calculate();

       g2d.drawString( frameRate.getFps(), 30, 30 );
       g2d.drawString( "Use mouse to draw lines", 30, 45 );
       g2d.drawString( "Press C to clear lines", 30, 60 );
       g2d.drawString( "Mouse Wheel cycles colors", 30, 75 );
       g2d.drawString( mouseInput.getMousePos().toString(), 30, 90 );

       // draw the lines
       for (int i = 0; i < lines.size() - 1; i++) {
           Point p1 = lines.get(i);
           Point p2 = lines.get(i + 1);

           if (p1 != null && p2 != null) {
               g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
           }
       }
   }


    protected void shutDown() {
        running = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.exit(0);
    }

    public static void main(String[] args) {
        SimpleMouseExample app = new SimpleMouseExample();
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

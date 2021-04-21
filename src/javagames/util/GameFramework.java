
package javagames.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;

public abstract class GameFramework extends JFrame implements Runnable {

      private Thread gameThread;
      private BufferStrategy bs;
      private volatile boolean running;
      /**
       * viewport width: vw
       * viewport height: vh
       * topLeft vx, vy
       */
      protected int vw, vh, vx, vy;

      protected Canvas canvas;
      protected RelativeMouseInput mouse;
      protected KeyboardInput keyboard;
      protected FrameRate frameRate;
      protected boolean appCursorDisable;
      /**
       * application properties
       */
      protected Color appBackground = Color.BLACK;
      protected Color appBorder = Color.LIGHT_GRAY;
      protected Color appFPSColor = Color.GREEN;
      protected Font appFont = new Font( "Courier New", Font.PLAIN, 14 );
      protected Font appFPSFont = new Font( "Courier New", Font.BOLD, 10 );
      protected String appTitle = "Game";
      protected float appBorderScale = 0.8f;
      protected int appWidth = 640;
      protected int appHeight = 480;
      protected float appWorldWidth = 2.0f;
      protected float appWorldHeight = 2.0f;
      protected long appSleep = 10L;
      protected boolean appMaintainRatio = false;
      protected int textPos = 0;
      protected Color textColor;


      public GameFramework() {

      }

      protected abstract void createFramework();
      protected abstract void renderFrame(Graphics g);
      protected abstract int getScreenWidth();
      protected abstract int getScreenHeight();

      protected void createAndShowGUI() {
            createFramework();
            if (appCursorDisable) {
                  disableCursor();
            }
            gameThread = new Thread(this);
            gameThread.start();
      }

      protected void setupViewport() {
            int vw = (int) (getScreenWidth() * appBorderScale);
            int vh = (int) (getScreenHeight() * appBorderScale);
            vx = (getScreenWidth() - vw) / 2;
            vy = (getScreenHeight() - vh) / 2;
            //maintain the ratio
            float ratio = appWorldWidth / appWorldHeight;
            this.vw = vw;
            this.vh = (int) (vw / ratio);
            if (this.vh > vh) {
                  this.vh = vh;
                  this.vw = (int) (ratio * vh);
            }
            //recenter
            vx += (int) ((vw - this.vw) / 2);
            vy += (int) ((vh - this.vh) / 2);
      }

      private void disableCursor() {
            Toolkit tk = Toolkit.getDefaultToolkit();
            Image image = tk.createImage("");
            Point point = new Point(0, 0);
            String name = "CanBeAnything";
            Cursor cursor = tk.createCustomCursor(image, point, name );
            setCursor(cursor);
      }

      @Override
      public void run() {
            initialize();
            running = true;
            long lastTime = System.nanoTime();
            long currentTime = System.nanoTime();
            while (running) {
                  long elapsedTime = currentTime - lastTime;
                  gameLoop((float) (elapsedTime * 1E-9));
                  lastTime = currentTime;
            }
            terminate();
      }


      private void renderFrame() {
            do {
                  do {
                        Graphics g = null;
                        try {
                              g = bs.getDrawGraphics();
                              renderFrame(g);
                        } finally {
                              if (g != null) {
                                    g.dispose();
                              }
                        }
                  } while (bs.contentsRestored());
                  bs.show();
            } while (bs.contentsLost());
      }

      private void gameLoop(float dt) {
            processInput(dt);
            updateObjects(dt);
            renderFrame();
            sleep(appSleep);
      }



      /*
       * start game loop
       */
      protected void initialize() {
            frameRate = new FrameRate();
            frameRate.initialize();
      }

      protected void processInput(float dt) {
            mouse.poll();
            keyboard.poll();
      }

      protected void updateObjects(float dt) {

      }

      protected void render(Graphics g) {
            g.setFont(appFPSFont);
            g.setColor(appFPSColor);
            frameRate.calculate();
            Utility.drawString(g, textPos, 10, frameRate.getFps());
      }

      protected void terminate() {

      }

      /* end of the game loop
       */

      protected void setupInput(Component component) {
            keyboard = new KeyboardInput();
            component.addKeyListener(keyboard);
            mouse = new RelativeMouseInput(component);
            component.addMouseListener(mouse);
            component.addMouseMotionListener(mouse);
            component.addMouseWheelListener(mouse);
      }

      protected void createBufferStrategy( Canvas component ) {
            component.createBufferStrategy(2);
            bs = component.getBufferStrategy();
      }

      protected void createBufferStrategy( Window component ) {
            component.createBufferStrategy(2);
            bs = component.getBufferStrategy();
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

      private void sleep(Long st) {
            try {
                  Thread.sleep(st);
            } catch (InterruptedException e) {
                  e.printStackTrace();
            }
      }

      private void shutDown() {
            if(Thread.currentThread() != gameThread) {
                  running = false;
                  try {
                        gameThread.join();
                        onShutDown();
                  } catch (InterruptedException e) {
                        e.printStackTrace();
                  }
            } else {
                  SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                              shutDown();
                        }
                  });
            }
      }

      protected void onShutDown() {

      }

      protected static void launchApp(final GameFramework app) {
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

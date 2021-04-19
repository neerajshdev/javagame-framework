package javagames.example.ch10;

import javagames.util.SimpleFramework;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.util.Random;

public class ImageSpeedTest extends SimpleFramework {

    private boolean bufferedImage;
    private boolean realTimeRendering;
    private BufferedImage bImg;
    private VolatileImage vImg;
    private GraphicsConfiguration gc;
    private Random random;


    public ImageSpeedTest() {
        appTitle = "Image speed Test";
        appWidth = 640;
        appHeight = 640;
        appSleep = 0L;
    }



    @Override
    protected void initialize() {
        super.initialize();
        random = new Random();
        bufferedImage = true;
        realTimeRendering = true;
        GraphicsDevice gd = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getDefaultScreenDevice();
        gc = gd.getDefaultConfiguration();
        createBuffImg();
        createVolatileImg();
    }

    private void createBuffImg() {
        bImg = gc.createCompatibleImage(getWidth(), getHeight());
    }

    private void createVolatileImg() {
        if (vImg != null) {
            vImg.flush();
            vImg = null;
        }
        vImg = gc.createCompatibleVolatileImage(getWidth(), getHeight());
    }


    @Override
    protected void processInput(float t) {
        super.processInput(t);

        if (keyboard.keyDownOnce(KeyEvent.VK_R)) {
            realTimeRendering = !realTimeRendering;
        }

        if (keyboard.keyDownOnce(KeyEvent.VK_B)) {
            bufferedImage = !bufferedImage;
        }
    }

    @Override
    protected void updateObjects(float t) {
        super.updateObjects(t);

        if (realTimeRendering) {
            if (bufferedImage) {
                renderBuffImg();
            } else {
                renderVolatileImg();
            }
        }
    }

    private void renderBuffImg() {
        Graphics2D g = bImg.createGraphics();
        g.setColor(new Color(random.nextInt()));
        g.fillRect(0,0, getWidth(), getHeight());
        g.dispose();
    }

    private void renderVolatileImg() {
        Graphics2D g = vImg.createGraphics();
        g.setColor(new Color(random.nextInt()));
        g.fillRect(0,0, getWidth(), getHeight());
        g.dispose();
    }

    @Override
    protected void render(Graphics g) {

        if (bufferedImage) {
            drawBuffImg(g);
        } else {
            drawVolatileImg(g);
        }

        super.render(g);
        g.drawString("(R)ealtime rendering: " + realTimeRendering, 10, 40);
        g.drawString("(B)ufferedImage: " + bufferedImage, 10, 60);
    }


    private void drawBuffImg(Graphics g) {
        g.drawImage(bImg, 0 ,0, null);
    }

    private void drawVolatileImg(Graphics g) {
        do {
            int returnCode = vImg.validate(gc);
            if (returnCode == VolatileImage.IMAGE_RESTORED) {
                renderVolatileImg();
            } else if (returnCode == VolatileImage.IMAGE_INCOMPATIBLE) {
                createVolatileImg();
                renderVolatileImg();
            }

            g.drawImage(vImg, 0, 0,null);

        } while (vImg.contentsLost());
    }


    public static void main(String[] args) {
        launchApp(new ImageSpeedTest());
    }
}


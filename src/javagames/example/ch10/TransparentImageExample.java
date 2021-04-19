package javagames.example.ch10;

import javagames.util.SimpleFramework;

import java.awt.*;
import java.awt.image.BufferedImage;

public class TransparentImageExample extends SimpleFramework {

    private BufferedImage transparentImg;
    private float shift;
    private float ribbonHeight;

    public TransparentImageExample() {
        appTitle = "transparent image example";
        appWidth = 300;
        appHeight = 400;
        appBackground = Color.darkGray;
    }

    @Override
    protected void initialize() {
        super.initialize();
        transparentImg = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = transparentImg.createGraphics();

        int sw = transparentImg.getWidth() / 8;
        int sh = transparentImg.getHeight() / 8;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if((row + col) % 2 == 0) {
                    g.setColor(Color.red);
                    g.fillRect(col * sw, row * sh, sw, sh);
                }

            }
        }

        g.dispose();
    }

    @Override
    protected void updateObjects(float t) {
        super.updateObjects(t);
        ribbonHeight = canvas.getHeight() / 5;
        shift += ribbonHeight * t;
        if (shift > ribbonHeight) {
            shift -= ribbonHeight;
        }
    }

    @Override
    protected void render(Graphics g) {
        super.render(g);

        int locX = 0, locY;
        for (int i = -1; i < 5; i++) {
            locY = i * (int) ribbonHeight + (int) shift;
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(locX, locY, canvas.getWidth(), (int) ribbonHeight / 2);
        }

        locX = canvas.getWidth() / 2 - transparentImg.getWidth()/2;
        locY = canvas.getHeight() / 2 - transparentImg.getHeight()/2;
        g.drawImage(transparentImg, locX, locY, null );
    }

    public static void main(String[] args) {
        launchApp(new TransparentImageExample());
    }
}

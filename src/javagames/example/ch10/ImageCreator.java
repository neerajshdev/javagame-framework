package javagames.example.ch10;

import javagames.util.SimpleFramework;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class ImageCreator extends SimpleFramework {

    public ImageCreator () {
        appBackground = Color.darkGray;
        appTitle  = "Image creator";
        appWidth = 640;
        appHeight = 640;
    }

    private static final int IMG_WIDTH = 256;
    private static final int IMG_HEIGHT = 256;
    private Random random = new Random();
    private BufferedImage sprite;
    private String loadFile;
    private final int SQUARES = 8;

    @Override
    protected void initialize() {
        super.initialize();
    }

    @Override
    protected void processInput(float t) {
        super.processInput(t);

        if (keyboard.keyDownOnce(KeyEvent.VK_NUMPAD1)) {
            createFile("jpg", "image-creator.jpg");
        }

        if (keyboard.keyDownOnce(KeyEvent.VK_NUMPAD2)) {
            createFile("gif", "image-creator.gif");
        }
    }

    @Override
    protected void updateObjects(float t) {
        super.updateObjects(t);
    }

    @Override
    protected void render(Graphics g) {
        super.render(g);
        g.drawString("(1) save JPG", 20, 40);
        g.drawString("(2) save GIF", 20, 60);
        g.drawString("(3) save BMP", 20, 80);
        g.drawString("(4) save PNG", 20, 100);

        g.drawString("(5) load JPG", 20, 120);
        g.drawString("(6) load GIF", 20, 140);
        g.drawString("(7) load BMP", 20, 160);
        g.drawString("(8) load PNG", 20, 180);

        if (sprite != null) {
            int x = canvas.getWidth() / 2;
            int y = canvas.getHeight() / 2;
            x = x - IMG_WIDTH / 2;
            y = y - IMG_HEIGHT / 2;
            g.drawImage(sprite, x, y, null );
        }

    }


    private void createFile(String type, String fileName) {
        try {
            sprite = createCustomImg();
            File file = new File(fileName);
            if (!ImageIO.write(sprite, type, file)) {
                throw new IOException("no " + type + " image writer found");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private BufferedImage createCustomImg() {
        BufferedImage image = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        int dx = image.getWidth() / SQUARES;
        int dy = image.getHeight() / SQUARES;

        for (int i = 0; i < SQUARES; i++) {
            for (int j = 0; j < SQUARES; j++) {
                g.setColor(new Color(random.nextInt()));
                g.fillRect(dx * i, dy * j, dx, dy);
            }
        }

        g.setColor(Color.black);
        g.drawRect(0, 0, IMG_WIDTH - 1, IMG_HEIGHT - 1);
        g.dispose();

        return image;
    }

    public static void main(String[] args) {
        launchApp(new ImageCreator());
    }
}

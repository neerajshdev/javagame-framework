package javagames.example.ch10;

import javagames.util.SimpleFramework;
import javagames.util.Vector2f;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public class FlyingSpriteExample extends SimpleFramework {

    private final int IMG_WIDTH = 256;
    private final int IMG_HEIGHT = 256;
    private BufferedImage sprite;

    private boolean antiAliasing;
    private boolean transparent;
    private boolean greenBorder;

    private enum RotationMethod {
         AffineTransform, AffineTransformOp, TexturePaint
    }

    private enum Interpolation {
        nearestNeighbor, bilinear, bicubic
    }

    private RotationMethod rotationMethod;
    private Interpolation interpolation;


    public FlyingSpriteExample() {
        appTitle = "Flying sprites example";
        appBackground = Color.darkGray;
        appWidth = 640;
        appHeight = 640;
        appSleep = 0L;
    }


    private FlyingSprite[] flyingSprites;

    @Override
    protected void initialize() {
        super.initialize();

        flyingSprites = new FlyingSprite[] {
                new FlyingSprite(new Vector2f(), 0,
                        Vector2f.polar((float)Math.toRadians(90), 0.12f),
                        (float)Math.toRadians(15)),

                new FlyingSprite(new Vector2f(-0.54f, 0.15f), (float)-Math.toRadians(-45),
                        Vector2f.polar((float)Math.toRadians(-90), 0.05f),
                        (float)Math.toRadians(10)),

                new FlyingSprite(new Vector2f(0.54f, 0.45f), (float)-Math.toRadians(90),
                        Vector2f.polar((float)Math.toRadians(2), 0.025f),
                        (float)Math.toRadians(10)),

                new FlyingSprite(new Vector2f(0.34f, 0.45f), (float)-Math.toRadians(50),
                        Vector2f.polar((float)Math.toRadians(80), 0.11f),
                        (float)Math.toRadians(10))
        };

        rotationMethod = RotationMethod.AffineTransform;
        interpolation = Interpolation.nearestNeighbor;
        transparent = false;
        greenBorder = false;
        antiAliasing = false;

        createSprite();
    }

    private void createSprite() {
        sprite = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        createCheckerBoard();
        if (transparent) {
            addTransparent();
        }
        if (greenBorder) {
            addGreenBorder();
        }
    }

    private void createCheckerBoard() {
        Graphics2D g2d = sprite.createGraphics();
        int dx = IMG_WIDTH / 8;
        int dy = IMG_HEIGHT / 8;
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if ((row + col) % 2 == 0) {
                    g2d.setColor(Color.BLACK);
                } else {
                    g2d.setColor(Color.WHITE);
                }
                g2d.fillRect(col * dx, row * dy, dx, dy);
            }
        }
        g2d.dispose();
    }

    private void addTransparent() {
        BufferedImage newSprite = new BufferedImage(IMG_WIDTH + 8, IMG_HEIGHT + 8, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = newSprite.createGraphics();
        g2d.drawImage(sprite, 4, 4, null);
        g2d.dispose();
        sprite = newSprite;
    }

    private void addGreenBorder() {
        Graphics2D g2d = sprite.createGraphics();
        g2d.setColor(Color.GREEN);
        g2d.drawRect(0, 0, sprite.getWidth()-1, sprite.getHeight()-1);
        g2d.dispose();
    }

    @Override
    protected void processInput(float t) {
        super.processInput(t);

        if (keyboard.keyDownOnce(KeyEvent.VK_A)) {
            antiAliasing = !antiAliasing;
        }

        if (keyboard.keyDownOnce(KeyEvent.VK_T)) {
            transparent = !transparent;
            createSprite();
        }

        if (keyboard.keyDownOnce(KeyEvent.VK_G)) {
           greenBorder = !greenBorder;
           createSprite();
        }

        if (keyboard.keyDownOnce(KeyEvent.VK_I)) {
           Interpolation[] values = Interpolation.values();
           int index = (interpolation.ordinal() + 1) % values.length;
           interpolation = values[index];
        }

        if (keyboard.keyDownOnce(KeyEvent.VK_R)) {
            RotationMethod[] values = RotationMethod.values();
            int index = (rotationMethod.ordinal() + 1) % values.length;
            rotationMethod = values[index];
        }
    }

    @Override
    protected void updateObjects(float t) {
        super.updateObjects(t);

        for (int i = 0; i < flyingSprites.length; i ++) {
            FlyingSprite flyingSprite = flyingSprites[i];
            Vector2f pos = flyingSprite.position.add(flyingSprite.velocity.mul(t));

            if (pos.x > appWorldWidth / 2) {
                pos.x -= appWorldWidth;
            }
            if (pos.x < -appWorldWidth / 2) {
                pos.x += appWorldWidth;
            }

            if (pos.y > appWorldHeight / 2) {
                pos.y -= appWorldHeight;
            }
            if (pos.y < -appWorldHeight / 2) {
                pos.y += appWorldHeight;
            }

            flyingSprite.angle += t * flyingSprite.angularVelocity;
            flyingSprite.position = pos;
        }
    }


    private void setAntialiasing(Graphics2D g2d){
        if (antiAliasing) {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }else {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        }
    }
    private void setInterpolation(Graphics2D g2d){
        switch (interpolation) {
            case nearestNeighbor: g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR); break;
            case bilinear: g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR); break;
            case bicubic: g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC); break;
        }
    }

    @Override
    protected void render(Graphics g) {

        setAntialiasing((Graphics2D) g);
        setInterpolation((Graphics2D) g);

        switch (rotationMethod) {
            case AffineTransform:
                doAffineTransform((Graphics2D) g);
                break;
            case AffineTransformOp:
                doAffineTransformOp((Graphics2D) g);
                break;
            case TexturePaint:
                doTexturePaint((Graphics2D) g);
                break;

        }

        super.render(g);
        g.drawString("(A) Antialiasing: " + antiAliasing, 20, 35);
        g.drawString("(I) Interpolation: " + interpolation, 20, 50);
        g.drawString("(R) Rotation method: " + rotationMethod, 20, 65);
        g.drawString("(T) Transparent Border: " + transparent, 20, 80);
        g.drawString("(G) Green Border: " + greenBorder, 20, 95);

    }

    private AffineTransform createAffineTransform(Vector2f pos, float angle) {
        Vector2f screen = getViewportTransform().mul(pos);

        AffineTransform transform = AffineTransform.getTranslateInstance(screen.x, screen.y);
        transform.rotate(angle);
        transform.translate(-sprite.getWidth() / 2, -sprite.getHeight() / 2);

        return  transform;
    }

    private void doAffineTransform(Graphics2D g2d) {
        for (int i = 0; i < flyingSprites.length; i++) {
            AffineTransform transform = createAffineTransform(flyingSprites[i].position, flyingSprites[i].angle);
            BufferedImage destImg = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics2D = destImg.createGraphics();
            graphics2D.drawImage(sprite, 0, 0, destImg.getWidth(), destImg.getHeight(), null);
            graphics2D.dispose();
            g2d.drawImage(destImg, transform, null);
        }
    }


    private AffineTransformOp createAffineTransformOP(Vector2f pos, float angle) {
        AffineTransform transform = createAffineTransform(pos, angle);
        switch (interpolation) {
            case nearestNeighbor: return new AffineTransformOp(transform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
            case bilinear: return new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
            case bicubic: return new AffineTransformOp(transform, AffineTransformOp.TYPE_BICUBIC);
        }
        return null;
    }

    private void doAffineTransformOp(Graphics2D g2d) {
       for (int i = 0; i < flyingSprites.length; i++) {
           FlyingSprite flyingSprite = flyingSprites[i];
           Vector2f pos = flyingSprite.position;
           float angle = flyingSprite.angle;
           AffineTransformOp op = createAffineTransformOP(pos, angle);
           BufferedImage img = op.filter(sprite, null);
           g2d.drawImage(img,0,0, null);
       }
    }

    private void doTexturePaint(Graphics2D g2d) {
        for (int i = 0; i < flyingSprites.length; i++) {
            FlyingSprite flyingSprite = flyingSprites[i];
            Vector2f pos = flyingSprite.position;
            float angle = flyingSprite.angle;
            AffineTransform transform = createAffineTransform(pos, angle);
            g2d.setTransform(transform);
            Rectangle2D anchor  = new Rectangle2D.Float(0, 0, sprite.getWidth() -1, sprite.getHeight()-1);
            TexturePaint paint = new TexturePaint(sprite, anchor);
            g2d.setPaint(paint);
            g2d.fillRect(0,0, sprite.getWidth()-1, sprite.getHeight()-1);
        }

        g2d.setTransform(new AffineTransform());
    }

    public static void main(String[] args) {
        launchApp(new FlyingSpriteExample());
    }
}

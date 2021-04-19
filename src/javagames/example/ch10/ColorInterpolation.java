package javagames.example.ch10;

import javagames.util.SimpleFramework;

import java.awt.*;
import java.awt.image.*;

public class ColorInterpolation extends SimpleFramework {


    private BufferedImage sprite;
    private int[] pixels;
    private int[] clear;

    @Override
    protected void initialize() {
        super.initialize();
        sprite = new BufferedImage(400, 400, BufferedImage.TYPE_INT_ARGB);
        WritableRaster raster = sprite.getRaster();
        pixels = ((DataBufferInt) raster.getDataBuffer()).getData();
        clear = new int[pixels.length];
    }


    @Override
    protected void updateObjects(float t) {
        super.updateObjects(t);
        createColorSquare();
    }

    @Override
    protected void render(Graphics g) {
        super.render(g);
        int posX = canvas.getWidth() / 2 - sprite.getWidth() / 2;
        int posY = canvas.getHeight() / 2 - sprite.getHeight() / 2;
        g.drawImage(sprite, posX, posY, null);
    }

    private void createColorSquare() {
        // top-left
        float tlr = 255f;
        float tlg = 0;
        float tlb = 0;

        // top-right
        float trr = 0;
        float trg = 255f;
        float trb = 0;

        // bottom-right
        float brr = 0;
        float brg = 0;
        float brb = 0;

        // bottom-left
        float blr = 0;
        float blg = 0;
        float blb = 255f;

        int w = sprite.getWidth();
        float w0 = 0;
        float w1 = w -1;
        float w1w0 = w1 - w0;

        int h = sprite.getHeight();
        float h0 =  0;
        float h1 = h - 1;
        float h1h0 = h1 - h0;

        System.arraycopy(clear, 0, pixels, 0, pixels.length);

        for (int row = 0; row < h; row++) {
            // left pixel
            float lr = tlr + (row - h0) * (blr - tlr) / h1h0;
            float lg = tlg + (row - h0) * (blg - tlg) / h1h0;
            float lb = tlb + (row - h0) * (blb - tlb) / h1h0;

            // right pixel
            float rr = trr + (row - h0) * (brr - trr) / h1h0;
            float rg = trg + (row - h0) * (brg - trg) / h1h0;
            float rb = trb + (row - h0) * (brb - trb) / h1h0;

            for (int col = 0; col < sprite.getWidth(); col++) {
                int r = (int) ( lr + (col - w0) * (rr - lr) / w1w0);
                int g = (int) ( lg + (col - w0) * (rg - lg) / w1w0);
                int b = (int) ( lb + (col - w0) * (rb - lb) / w1w0);

                int color = 0xFF << 24 | r << 16 | g << 8 | b;
                int index = row * w + col;
                pixels[index] = color;
            }
        }
    }

    public static void main(String[] args) {
        launchApp(new ColorInterpolation());
    }
}

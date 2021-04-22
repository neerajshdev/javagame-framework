package javagames.util;

import java.awt.*;
import java.awt.image.VolatileImage;

public class FullScreenFramework extends GameFramework {
    private final int BIT_DEPTH = 32;
    private DisplayMode currentDisplayMode;
    private GraphicsConfiguration gc;
    private VolatileImage vi;

    @Override
    protected void createFramework() {
        setUndecorated(true);
        setIgnoreRepaint(true);
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        gc = gd.getDefaultConfiguration();
        if (!gd.isFullScreenSupported()) {
            System.err.println("ERROR: fullScreen not supported!");
            System.exit(0);
        }
        gd.setFullScreenWindow(this);
        if (appMaintainRatio) {
            setBackground(appBorder);
            setupViewport(appWidth, appHeight);
            createVolatileImage();
        } else {
            setBackground(appBackground);
        }
        currentDisplayMode = gd.getDisplayMode();
        gd.setDisplayMode(new DisplayMode(appWidth, appHeight, BIT_DEPTH, DisplayMode.REFRESH_RATE_UNKNOWN));
        createBufferStrategy(this);
        setupInput(this);
    }

    private void createVolatileImage() {
        if (vi != null) {
            vi.flush();
            vi = null;
        }
        vi = gc.createCompatibleVolatileImage(getScreenWidth(), getScreenHeight());
    }

    private void renderVolatileImage(Graphics g) {
        do {
             int returnCode = vi.validate(gc);
             if (returnCode == VolatileImage.IMAGE_INCOMPATIBLE) {
                 createVolatileImage();
             }
             Graphics gVI = null;
             try {
                 gVI = vi.createGraphics();
                 gVI.clearRect(0, 0, getScreenWidth(), getScreenHeight());
                 render(gVI);
                 g.drawImage(vi, vx, vy, null);
             } finally {
                 if (gVI != null) {
                     gVI.dispose();
                 }
             }

        }while (vi.contentsLost());
    }

    @Override
    protected void renderFrame(Graphics g) {
        if (appMaintainRatio) {
            g.clearRect(0, 0, getWidth(), getHeight());
            renderVolatileImage(g);
        } else {
            g.clearRect(0, 0, getScreenWidth(), getScreenHeight());
            render(g);
        }
    }

    @Override
    protected int getScreenWidth() {
        return appMaintainRatio ? vw : getWidth();
    }

    @Override
    protected int getScreenHeight() {
        return appMaintainRatio ? vh : getHeight();
    }

    @Override
    protected void onShutDown() {
        super.onShutDown();
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        gd.setDisplayMode(currentDisplayMode);
    }
}

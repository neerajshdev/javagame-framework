package javagames.util;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class WindowFramework extends GameFramework{

    private Canvas canvas;

    @Override
    protected void createFramework() {
        canvas = new Canvas();
        canvas.setBackground(appBackground);
        canvas.setIgnoreRepaint(true);
        setIgnoreRepaint(true);
        getContentPane().add(canvas);
        setLocationByPlatform(true);

        if (appMaintainRatio) {
            setSize(appWidth, appHeight);
            setBackground(appBorder);
            setLayout(null);
            addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    onComponentResized(e);
                }
            });
        } else {
            canvas.setSize(appWidth, appHeight);
            pack();
        }

        setTitle(appTitle);
        setVisible(true);
        createBufferStrategy(canvas);
        setupInput(canvas);
        canvas.requestFocus();
    }

    private void onComponentResized(ComponentEvent e) {
       Dimension size =  e.getComponent().getSize();
       setupViewport(size.width, size.height);
       canvas.setSize(vw, vh);
       canvas.setLocation(vx, vy);
    }

    @Override
    protected void renderFrame(Graphics g) {
        g.clearRect(0, 0, getScreenWidth(), getScreenHeight());
        render(g);
    }

    @Override
    protected int getScreenWidth() {
        return canvas.getWidth();
    }

    @Override
    protected int getScreenHeight() {
        return canvas.getHeight();
    }
}

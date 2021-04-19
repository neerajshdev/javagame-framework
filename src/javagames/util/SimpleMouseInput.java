package javagames.util;

import java.awt.*;
import java.awt.event.*;

public class SimpleMouseInput implements MouseListener, MouseMotionListener, MouseWheelListener {


    private static final int BUTTON_COUNT = 3;

    private Point mousePos;
    private Point currentPos;
    private final boolean[] mouse;
    private final int[] polled;
    private int notches;
    private int polledNotches;

    public SimpleMouseInput() {
        mousePos = new Point(0,0);
        currentPos = new Point(0, 0);
        mouse = new boolean[BUTTON_COUNT];
        polled = new int[BUTTON_COUNT];
    }

    public synchronized void poll() {
        mousePos = currentPos;
        polledNotches = notches;
        notches = 0;
        for (int i = 0; i < mouse.length; i++) {
            if (mouse[i]) {
                polled[i]++;
            } else {
                polled[i] = 0;
            }
        }
    }

    public Point getMousePos() {
        return mousePos;
    }

    public int getPolledNotches() {
        return polledNotches;
    }

    public boolean buttonDown(int button) {
        return polled[button - 1] > 0;
    }

    public boolean buttonDownOnce(int button) {
        return polled[button - 1] == 1;
    }

    @Override
    public synchronized void mousePressed(MouseEvent e) {
        int button = e.getButton() -1;
        mouse[button] = true;
    }

    @Override
    public synchronized void mouseReleased(MouseEvent e) {
        int button = e.getButton() -1;
        mouse[button] = false;
    }

    @Override
    public synchronized void mouseEntered(MouseEvent e) {
        System.out.println("mouse entered and calling mouseMoved");
        mouseMoved(e);
    }

    @Override
    public synchronized void mouseExited(MouseEvent e) {
        System.out.println("mouse exited and calling mouseMoved");
        mouseMoved(e);

    }

    @Override
    public synchronized void mouseClicked(MouseEvent e) {
        System.out.println("mouse clicked");
        // ignore
    }

    @Override
    public synchronized void mouseDragged(MouseEvent e) {
        System.out.println("mouse dragged and calling mouseMoved");
        mouseMoved(e);

    }

    @Override
    public synchronized void mouseMoved(MouseEvent e) {
        System.out.println("mouse moved");
        currentPos = e.getPoint();
    }

    @Override
    public synchronized void mouseWheelMoved(MouseWheelEvent e) {
        notches += e.getWheelRotation();
    }
}

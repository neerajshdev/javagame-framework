package javagames.util;


import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyboardInput implements KeyListener {
    private boolean[] keys;
    private int[] polled;

    public KeyboardInput() {
        keys = new boolean[256]; // this is for the event thread
        polled = new int[256]; // this is for the game thread
    }

    public boolean keyDown( int keyCode ) {
        return polled[keyCode] > 0;
    }

    public boolean keyDownOnce( int keyCode ) {
        return polled[keyCode] == 1;
    }

    public synchronized void poll() {
        for (  int i = 0;  i < keys.length;  i++  )  {
            if ( keys[i] )  {
                polled[i]++;
            } else {
                polled[i] = 0;
            }
        }
    }

    @Override
    public synchronized void keyPressed( KeyEvent e ) {
        int keyCode = e.getKeyCode();
        if ( keyCode >= 0 && keyCode < keys.length ) {
            keys[keyCode] = true;
        }
    }

    @Override
    public synchronized void keyReleased( KeyEvent e ) {
        int keyCode = e.getKeyCode();
        if ( keyCode >= 0 && keyCode < keys.length ) {
            keys[ keyCode ] = false;
        }
    }

    @Override
    public synchronized void keyTyped( KeyEvent e ) {
        // no need
    }
}

package javagames.util;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;

public class SafeKeyboardInput implements KeyListener {

    enum EventType {
        PRESSED,
        TYPED,
        RELEASED
    }

    class Event {
        public KeyEvent event;
        public EventType type;
        public Event( KeyEvent event, EventType type) {
            this.event = event;
            this.type = type;
        }
    }
    private int[] polled;
    private Event event = null;
    private LinkedList<Event> eventThread = new LinkedList<>();
    private LinkedList<Event> gameThread = new LinkedList<>();

    public SafeKeyboardInput() {
        polled = new int[256]; // this is for the game thread
    }

    public boolean keyDown( int keyCode ) {
        return  keyCode == event.event.getKeyCode() &&  polled[ keyCode ] > 0;
    }

    public boolean keyDownOnce( int keyCode ) {
        return  keyCode == event.event.getKeyCode() &&  polled[ keyCode ] == 1;
    }

    public Character getKeyTyped() {
        if ( event.type != EventType.TYPED ) {
            return null;
        } else {
            return event.event.getKeyChar();
        }
    }

    // This method should be used in a
    // while loop
    public boolean processEvent() {
        event = gameThread.poll();
        if ( event != null ) {
            int keyCode = event.event.getKeyCode();
            if ( keyCode >= 0 && keyCode < polled.length ) {
                if ( event.type == EventType.PRESSED ) {
                    polled[ keyCode ] ++;
                } else if ( event.type == EventType.RELEASED ) {
                    polled[ keyCode ] = 0;
                }
            }
        }
        return event != null;
    }

    // Before processing the events
    // it is important to call this method.
    public synchronized void poll() {
        LinkedList<Event> temp = eventThread;
        gameThread = eventThread;
        eventThread = temp;
    }


    // These three methods are called on event thread.
    @Override
    public synchronized void keyPressed( KeyEvent e ) {
        eventThread.add( new Event( e, EventType.PRESSED ) );
    }

    @Override
    public synchronized void keyReleased( KeyEvent e ) {
        eventThread.add( new Event( e, EventType.RELEASED ) );
    }

    @Override
    public synchronized void keyTyped( KeyEvent e ) {
        eventThread.add( new Event( e, EventType.TYPED ) );
    }
}

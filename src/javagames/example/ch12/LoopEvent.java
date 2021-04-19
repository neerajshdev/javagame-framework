package javagames.example.ch12;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class LoopEvent implements Runnable {

    private BlockingHardware hardware;
    private BlockingQueue<Event> queue;
    private Thread consumer;
    private State currentState;
    private int ms, slices;

    private enum Event {
        FIRE, RESTART, DONE;
    };

    private enum State {
        WAITING, RUNNING;
    }

    public LoopEvent(int ms, int slices) {
        this.ms = ms;
        this.slices = slices;
    }

    public void initialize() {
        hardware = new BlockingHardware("Loop Event");
        currentState = State.WAITING;
        queue = new LinkedBlockingQueue<>();
        hardware.addListener(getListener());
        consumer = new Thread(this);
        consumer.start();
    }

    public void fire( ) {
        try {
            queue.put(Event.FIRE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void done() {
        try {
            queue.put(Event.DONE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void shutDown() {
        Thread temp = consumer;
        consumer = null;
        try {
            queue.put(Event.DONE);
            temp.join(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Loop shutdown");
    }

    @Override
    public void run() {
        while (Thread.currentThread() == consumer) {
            try {
                processEvent(queue.take());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void processEvent(Event event) {
        System.out.println( "Got " + event + " event" );
        if (currentState == State.WAITING) {
            if (event == Event.FIRE) {
                hardware.turnOn();
                hardware.start(ms, slices);
                currentState = State.RUNNING;
            }
        } else if (currentState == State.RUNNING) {
            if (event == Event.DONE) {
                hardware.stop();
                hardware.turnOFF();
                currentState = State.WAITING;
            } else if (event == Event.RESTART) {
                hardware.start(ms, slices);
            }
        }
    }

    private BlockingHardwareListener getListener() {
        return new BlockingHardwareListener() {
            @Override
            public void taskFinished() {
                try {
                    queue.put(Event.RESTART);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
    }
}

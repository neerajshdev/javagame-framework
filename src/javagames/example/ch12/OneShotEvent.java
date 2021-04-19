package javagames.example.ch12;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class OneShotEvent implements Runnable{

    private BlockingHardware hardware;
    private BlockingQueue<Event> queue;
    private volatile Thread consumer;
    private State currentState;
    private int ms, slices;

    private enum Event {
        FIRE, DONE;
    };

    private enum State {
        WAITING, RUNNING;
    }

    public OneShotEvent(int ms, int slices) {
        this.ms = ms;
        this.slices = slices;
    }

    public void initialize() {
        hardware = new BlockingHardware("OneShotEvent");
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
//            queue.put(Event.DONE);
            temp.join(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("One shot shutdown");
    }

    @Override
    public void run() {
        while (Thread.currentThread() == consumer) {
            try {
                processEvent(queue.take());
                System.out.println("there");
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
                hardware.turnOFF();
                currentState = State.WAITING;
            }
        }
    }

    private BlockingHardwareListener getListener() {
        BlockingHardwareListener listener = new BlockingHardwareListener() {
            @Override
            public void taskFinished() {
                try {
                    queue.put(Event.DONE);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        return listener;
    }
}

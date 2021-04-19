package javagames.example.ch12;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class RestartEvent implements Runnable{
    private BlockingHardware hardware;
    private Thread consumer;
    private BlockingQueue<Event> queue;
    private State currentState;
    private int ms, slices;

    private enum Event {
        FIRE,
        DONE;
    };

    private enum State {
        WAITING,
        RUNNING,
        RESTART;
    };

    public RestartEvent(int ms, int slices) {
        this.ms = ms;
        this.slices = slices;
    }

    public void initialize() {
        hardware = new BlockingHardware("Restart Event");
        hardware.addListener(getListener());
        currentState = State.WAITING;
        queue = new LinkedBlockingQueue();
        consumer = new Thread(this);
        consumer.start();
    }


    public void fire() {
        try {
            queue.put(Event.FIRE);
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
            System.out.println("Restart event stopped");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
            if (event == Event.FIRE) {
                hardware.stop();
                currentState = State.RESTART;
            } else if (event == Event.DONE) {
                hardware.turnOFF();
                currentState = State.WAITING;
            }
        } else if (currentState == State.RESTART) {
            if (event == Event.DONE) {
                hardware.start(ms, slices);
                currentState = State.RUNNING;
            }
        }
    }

    private BlockingHardwareListener getListener() {
        return new BlockingHardwareListener() {
            @Override
            public void taskFinished() {
                try {
                    queue.put(Event.DONE);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
    }
}

package javagames.example.ch12;

import java.util.*;

public class FakeHardware {

    private static final int SLEEP_MIN = 100;
    private static final int SLEEP_MAX = 700;

    private volatile boolean on = false;
    private volatile boolean running = false;
    private String name;
    private List<FakeHardwareListener> listeners = Collections.synchronizedList(new ArrayList<>());

    enum FakeHardwareEvent {
        ON, OFF, START, STOP;
    }

    public boolean addListener(FakeHardwareListener listener) {
        return listeners.add(listener);
    }

    public FakeHardware(String name) {
        this.name = name;
    }

    private void sleep() {
        int sleepTime = new Random().nextInt(
                SLEEP_MAX - SLEEP_MIN + 1
        ) + SLEEP_MIN;
        sleep(sleepTime);
    }

    private void sleep( int sleepTime) {
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean isOn() {
        return on;
    }

    public boolean isRunning() {
        return running;
    }

    public void turnOn() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                sleep();
                setOn();
            }
        }).start();
    }

    public void turnOff() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                sleep();
                setOff();
            }
        }).start();
    }

    public void start(int timeMS, int slices) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                sleep();
                setStart(timeMS, slices);
            }
        }).start();
    }

    public void stop() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                sleep();
                setStop();
            }
        }).start();
    }

    private synchronized void setOn() {
        if (!on) {
            on = true;
            fireEvent(FakeHardwareEvent.ON);
        }
    }

    private synchronized void setOff() {
        if (on) {
            on = false;
            fireEvent(FakeHardwareEvent.OFF);
        }
    }

    private void setStart(int timeMS, int slices) {
        synchronized (this) {
            if (on && !running ) {
                running = true;
                fireEvent(FakeHardwareEvent.START);
            }
        }
        if (running) {
            runTask(timeMS, slices);
            running = false;
            fireEvent(FakeHardwareEvent.STOP);
        }

    }

    private synchronized void setStop() {
        if (running) {
            running = false;
        }
    }



    private void fireEvent(FakeHardwareEvent event) {
        for (FakeHardwareListener listener : listeners) {
            listener.event(this, event);
        }
    }

    private void runTask(int timeMS, int slices) {
        int sleep = timeMS / slices;
        for (int i = 0; i < slices; i++) {
            if (!(running && on) ) {
                return;
            }
            System.out.println( name + "[" + (i+1) + "/" + slices + "]" );
            sleep(sleep);
        }
    }

    
}

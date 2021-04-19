package javagames.example.ch12;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BlockingHardware {


    private Lock lock = new ReentrantLock();
    private Condition cond = lock.newCondition();
    private FakeHardware hardware;

    private volatile boolean on;
    private volatile boolean started;

    private List<BlockingHardwareListener> listeners = Collections.synchronizedList(new ArrayList<>());

    public BlockingHardware(String name) {
        hardware = new FakeHardware(name);
        hardware.addListener(
                new FakeHardwareListener() {
                    @Override
                    public void event(FakeHardware source, FakeHardware.FakeHardwareEvent event) {
                        handleHardwareEvent(source, event);
                    }
                }
        );
    }

    public boolean addListener( BlockingHardwareListener listener) {
        return listeners.add(listener);
    }

    // this will block until hardware is on.
    public void turnOn() {
        try {
            lock.lock();
            hardware.turnOn();
            while (!on) {
                cond.await();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        finally {
            lock.unlock();
        }
    }

    public void turnOFF() {
        try {
            lock.lock();
            hardware.turnOff();
            while (on) {
                cond.await();
            }

            System.out.println("Turned OFF");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        finally {
            lock.unlock();
        }
    }

    public void start(int ms, int slices) {
        try {
            lock.lock();
            hardware.start(ms, slices);
            while (!started) {
                cond.await();
            }
            System.out.println("hardware is started");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        finally {
            lock.unlock();
        }
    }

    public void stop() {
        try {
            lock.lock();
            hardware.stop();
            while (started) {
                cond.await();
            }
            System.out.println("hardware is stopped");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }

    private void handleHardwareEvent(FakeHardware source, FakeHardware.FakeHardwareEvent event) {
        boolean wasStarted = started;

        try {
            lock.lock();
            switch (event) {
                case ON: on = true; break;
                case OFF: on = false; break;
                case START: started = true; break;
                case STOP : started = false;
            }
            cond.signalAll();

        } finally {
            lock.unlock();
        }

        if (wasStarted && !started) {
            fireTaskFinished();
        }

    }

    private void fireTaskFinished() {
        synchronized (listeners) {
            for (BlockingHardwareListener listener : listeners) {
                listener.taskFinished();
            }
         }
    }

}

package javagames.sound;

import javax.sound.sampled.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class AudioStream implements LineListener {

    public static final int LOOP_CONTINUOUSLY  = -1;
    protected  boolean open = false;
    protected  boolean started = false;
    protected final Lock lock = new ReentrantLock();
    protected final Condition cond = lock.newCondition();
    protected byte[] soundData;
    private boolean stoppedBeforeComplete;
    private FloatControl gain;
    private FloatControl pan;
    private final List<BlockingAudioListener> listeners = Collections.synchronizedList(new ArrayList<>());



    public AudioStream(byte[] soundData) {
        this.soundData = soundData;
    }

    public abstract void open();
    public abstract void close();
    public abstract void start();
    public void stop() {
        stoppedBeforeComplete = true;
    }
    public abstract void restart();
    public abstract void loop(int count);

    public boolean addListener(BlockingAudioListener listener) {
        return listeners.add(listener);
    }

    protected void fireTaskFinished() {
        synchronized (listeners) {
            for (BlockingAudioListener listener : listeners) {
                listener.audioFinished();
            }
        }
    }

    @Override
    public void update(LineEvent event) {
        boolean wasStarted = started;
        LineEvent.Type type = event.getType();
        lock.lock();
        try {
            if (type == LineEvent.Type.OPEN) {
                open = true;
            } else if (type == LineEvent.Type.CLOSE) {
                open = false;
            } else if (type == LineEvent.Type.START) {
                started = true;
            } else if (type == LineEvent.Type.STOP) {
                started = false;
            }
            cond.signalAll();
        } finally {
            lock.unlock();
        }

        if (wasStarted && !started) {
            if (!stoppedBeforeComplete) {
                fireTaskFinished();
            } else {
                stoppedBeforeComplete = false;
            }

        }
    }

    protected void createControl(Line line) {
        if (line.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            gain = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
        }
        if (line.isControlSupported(FloatControl.Type.PAN)) {
            pan = (FloatControl) line.getControl(FloatControl.Type.PAN);
        }
    }

    protected void clearControl() {
        gain = null;
        pan = null;
    }

    protected boolean hasGainControl() {
        return gain != null;
    }

    protected boolean hasPanControl() {
        return pan != null;
    }


    public void setGainTo( float value) {
        if (hasGainControl()) gain.setValue(value);
    }

    public float getGainValue() {
        return hasGainControl() ? gain.getValue() : 0.0f;
    }

    public float getMinimum() {
        return hasGainControl() ? gain.getMinimum() : 0.0f;
    }

    public float getMaximum() {
        return hasGainControl() ? gain.getMaximum() : 0.0f;
    }

    public void setPanTo( float value) {
        if (hasPanControl()) pan.setValue(value);
    }
    public float getPanValue() {
        return hasPanControl() ? pan.getValue() : 0.0f;
    }

    public float getPrecision() {
        return hasPanControl() ? pan.getPrecision() : 0.0f;
    }
}

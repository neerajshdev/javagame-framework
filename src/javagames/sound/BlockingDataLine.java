package javagames.sound;

public class BlockingDataLine extends AudioStream {

    private AudioDataLine stream;

    public BlockingDataLine(byte[] soundData) {
       super(soundData);
    }


    @Override
    public void open() {
        lock.lock();
        try {
            stream = new AudioDataLine(soundData);
            stream.initialize();
            stream.addListener(this);
            stream.open();
            while (!open) {
                cond.await();
            }
            createControl(stream.getLine());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void close() {
        lock.lock();
        try {
            stream.close();
            while (open) {
                cond.await();
            }
            clearControl();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void start() {
        lock.lock();
        try {
            stream.start();
            while (!started) {
                cond.await();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }


    @Override
    public void stop() {
        lock.lock();
        try {
            stream.stop();
            while (started) {
                cond.await();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void restart() {
        stream.reset();
    }

    @Override
    public void loop(int count) {
        lock.lock();
        try {
            stream.loop(count);
            while (!started) {
                cond.await();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}

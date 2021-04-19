package javagames.sound;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class BlockingClip extends AudioStream {

    private Clip clip;
    private AudioInputStream ais;

    public BlockingClip(byte[] soundData) {
        super(soundData);
    }

    @Override
    public void open() {
        lock.lock();
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(soundData);
            ais = AudioSystem.getAudioInputStream(in);
            clip =  AudioSystem.getClip();
            clip.addLineListener(this);
            clip.open(ais);
            while (!open) {
                cond.await();
            }
            createControl(clip);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
            throw  new SoundException(ex.getMessage(), ex);
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
            clip.close();
            ais.close();
            while (open) {
                cond.await();
            }
            clip = null;
            clearControl();
        } catch (InterruptedException | IOException e) {
          e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void start() {
        lock.lock();
        try {
            clip.flush();
            clip.setFramePosition(0);
            clip.start();
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
        super.stop();
        lock.lock();
        try {
            clip.stop();
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
      stop();
      start();
    }

    @Override
    public void loop(int count) {
        lock.lock();
        try {
            clip.flush();
            clip.setFramePosition(0);
            clip.loop(count);
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

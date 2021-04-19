package javagames.sound;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AudioDataLine implements Runnable{

    private static final  int BUFFER_SIZE_MS = 50;
    private final byte[] rawData;
    private byte[] soundData;
    private Thread writer;
    private SourceDataLine dataLine;
    private final List<LineListener> listeners = Collections.synchronizedList(new ArrayList<>());
    private int bufferSize;
    private int loopCount;
    private boolean restart;
    private AudioFormat audioFormat;

    public AudioDataLine(byte[] rawData) {
        this.rawData = rawData;
    }

    public void initialize() {
        try{
            ByteArrayInputStream in = new ByteArrayInputStream(rawData);
            AudioInputStream ais = AudioSystem.getAudioInputStream(in);
            audioFormat = ais.getFormat();
            bufferSize = computeBufferSize(BUFFER_SIZE_MS);
            soundData = readSoundData(ais);
        }catch (IOException ex) {
            ex.printStackTrace();
        } catch (UnsupportedAudioFileException ex ) {
            throw new SoundException(ex.getMessage(), ex);
        }
    }

    private int computeBufferSize(int millisecond) {
        float sampleRate = audioFormat.getSampleRate();
        int channels =  audioFormat.getChannels();
        int bitSize =  audioFormat.getSampleSizeInBits();

        if (
                sampleRate == AudioSystem.NOT_SPECIFIED
                || channels == AudioSystem.NOT_SPECIFIED
                || bitSize == AudioSystem.NOT_SPECIFIED
        ) {
            System.out.println("buffer size: " + AudioSystem.NOT_SPECIFIED );
            return -1;
        }

        // no of samples in $(millisecond) ms
        float samples = sampleRate * millisecond / 1000.0f;
        while (samples != Math.floor(samples)) {
            millisecond++;
            samples = sampleRate * millisecond / 1000.0f;
        }
        // total samples size in bytes =
        // ( total samples * size of one sample in bits * no of channels ) / 8
        return  (int) (samples * channels * bitSize / 8);
    }

    private byte[] readSoundData(AudioInputStream ais) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int chunk = audioFormat.getFrameSize();
            byte[] buff = new byte[chunk];
            while (ais.read(buff) != -1) {
                out.write(buff);
            }
            ais.close();
            return out.toByteArray();
        }catch (IOException ex ) {
            ex.printStackTrace();
            return null;
        }
    }

    public void addListener( LineListener listener ) {
        listeners.add(listener);
    }

    public void open() {
        try {
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
            dataLine = (SourceDataLine)AudioSystem.getLine(info);
            synchronized (listeners) {
                for (LineListener listener : listeners) {
                    dataLine.addLineListener(listener);
                }
            }
            dataLine.open(audioFormat, bufferSize);
        } catch (LineUnavailableException ex) {
            throw new SoundException(ex.getMessage(), ex);
        }
    }

    public void close() {
        dataLine.close();
    }

    public void start() {
        loopCount = 0;
        dataLine.flush();
        dataLine.start();
        writer = new Thread(this);
        writer.start();
    }

    public void stop() {
        if (writer != null ) {
            Thread temp = writer;
            writer = null;
            try {
                temp.join(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void reset() {
        restart = true;
    }

    public void loop(int loopCount) {
        this.loopCount = loopCount;
        dataLine.flush();
        dataLine.start();
        writer = new Thread(this);
        writer.start();
    }

    public Line getLine() {
        return dataLine;
    }

    @Override
    public void run() {
        try {
            while (true) {
                int written = 0;
                int length = bufferSize == -1 ? dataLine.getBufferSize() : bufferSize;
                while ( written < soundData.length ) {
                    if (Thread.currentThread() != writer) {
                        loopCount = 0;
                        break;
                    } else if ( restart ) {
                        restart = false;
                        if (loopCount != AudioStream.LOOP_CONTINUOUSLY) {
                            loopCount++;
                        }
                        break;
                    }
                    // write chunk to line buffer
                    int bytesLeft = soundData.length - written;
                    int toWrite = bytesLeft > 2 * length ? length : bytesLeft;
                    written += dataLine.write(soundData, written, toWrite);
                }
                if (loopCount == 0) {
                    break;
                } else if (loopCount != AudioStream.LOOP_CONTINUOUSLY) {
                    loopCount--;
                }
            }
        } finally {
            dataLine.drain();
            dataLine.stop();
        }



    }
}

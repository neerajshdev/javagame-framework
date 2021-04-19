package javagames.sound;


import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SoundEvent implements Runnable {

    public static final String SHUT_DOWN = "shutdown";
    public static final String STATE_WAITING = "waiting";
    public static final String STATE_RUNNING = "running";
    // Trigger
    public static final String EVENT_FIRE = "fire";
    public static final String EVENT_DONE = "done";
    // sound complete
    public static final String EVENT_FINISHED = "finished";


    protected AudioStream audio;
    protected BlockingQueue<String> queue;

    protected String currentState;

    public SoundEvent( AudioStream audio ) {
        this.audio = audio;
        initialize();
    }

    private void initialize() {
        currentState = STATE_WAITING;
        audio.addListener(getListener());
        queue = new LinkedBlockingQueue<>();
        Thread consumer = new Thread(this);
        consumer.setName("soundEvent");
        consumer.start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                String event = queue.take();
                if (event.equals(SHUT_DOWN)) {
                    if (currentState.equals(STATE_RUNNING)) {
                        audio.stop();
                        audio.close();
                    }
                    break;
                }
                processEvent(event);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    protected void processEvent(String event) {
        System.out.println("Got event: " + event);
    }


    protected void put(String event) {
        try {
            queue.put(event);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    public void shutDown() {
        put(SHUT_DOWN);
    }

    public AudioStream getAudio() {
        return audio;
    }

    // Get called when the audio has been
    // played completely.
    protected void onAudioFinished() {

    }

    public BlockingAudioListener getListener() {
        return new BlockingAudioListener() {
            @Override
            public void audioFinished() {
                onAudioFinished();
            }
        };
    }
}

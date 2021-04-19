package javagames.sound;

public class LoopEvent extends SoundEvent{

    public LoopEvent(AudioStream audio) {
        super(audio);
    }

    public void fire() {
        put(EVENT_FIRE);
    }

    public void done() {
        put(EVENT_DONE);
    }


    @Override
    protected void processEvent(String event) {
        super.processEvent(event);
        if (currentState == STATE_WAITING) {
            if (event == EVENT_FIRE) {
                audio.open();
                audio.loop(AudioStream.LOOP_CONTINUOUSLY);
                currentState = STATE_RUNNING;
            }
        } else if (currentState == STATE_RUNNING) {
            if (event == EVENT_DONE) {
                audio.stop();
                audio.close();
                currentState = STATE_WAITING;
            }
        }
    }

}

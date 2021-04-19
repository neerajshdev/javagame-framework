package javagames.sound;

public class OneShotEvent extends SoundEvent {

    public OneShotEvent(AudioStream audio) {
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
                audio.start();
                currentState = STATE_RUNNING;
            }
        } else if (currentState == STATE_RUNNING) {
            if (event == EVENT_DONE) {
                audio.stop();
                audio.close();
                currentState = STATE_WAITING;
            } else if (event == EVENT_FINISHED) {
                audio.close();
                currentState = STATE_WAITING;
            }
        }
    }

    // Get called when the audio has been
    // played completely and got stopped.
    @Override
    protected void onAudioFinished() {
        put(EVENT_FINISHED);
    }
}

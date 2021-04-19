package javagames.sound;

public class RestartEvent extends SoundEvent {

    public RestartEvent(AudioStream audio) {
        super(audio);
    }

    public void fire() {
        put(EVENT_FIRE);
    }



    @Override
    protected void processEvent(String event) {
        super.processEvent(event);
        if (currentState.equals(STATE_WAITING)) {
            if (event.equals(EVENT_FIRE)) {
                audio.open();
                audio.start();
                currentState = STATE_RUNNING;
            }
        } else if (currentState.equals(STATE_RUNNING)) {
            if (event.equals(EVENT_FIRE)) {
                audio.restart();
            } else if (event.equals(EVENT_FINISHED)) {
                audio.close();
                currentState = STATE_WAITING;
            }
        }
        System.out.println("Current State: " + currentState);
    }


    @Override
    protected void onAudioFinished() {
        put(EVENT_FINISHED);
    }
}

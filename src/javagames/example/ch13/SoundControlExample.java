package javagames.example.ch13;

import javagames.sound.BlockingClip;
import javagames.sound.BlockingDataLine;
import javagames.sound.LoopEvent;
import javagames.util.ResourceLoader;
import javagames.util.SimpleFramework;
import javagames.util.Utility;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;

public class SoundControlExample extends SimpleFramework {
    private byte[] rewSound;
    private BlockingClip clip;
    private LoopEvent loopClip;
    private BlockingDataLine stream;
    private LoopEvent loopStream;

    public SoundControlExample() {
        appWidth = 600;
        appHeight = 600;
        appFPSColor = Color.MAGENTA;
        appBackground = Color.ORANGE;
        textColor = Color.BLACK;
    }

    @Override
    protected void initialize() {
        super.initialize();
        InputStream in = ResourceLoader.load(
                SoundPlayerExample.class,
                null,
                "/assets/sound/ELECTRONIC_computer_beep_09.wav"
        );
        try {
            rewSound = in.readAllBytes();
            clip = new BlockingClip(rewSound);
            loopClip = new LoopEvent(clip);
            stream = new BlockingDataLine(rewSound);
            loopStream = new LoopEvent(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void processInput(float t) {
        super.processInput(t);

        if (keyboard.keyDownOnce(KeyEvent.VK_1)) {
            loopClip.fire();
        }

        if (keyboard.keyDownOnce(KeyEvent.VK_2)) {
            loopClip.done();
        }

        if (keyboard.keyDownOnce(KeyEvent.VK_3)) {
            loopStream.fire();
        }

        if (keyboard.keyDownOnce(KeyEvent.VK_4)) {
            loopStream.done();
        }
        if (keyboard.keyDownOnce(KeyEvent.VK_5)) {
            float currentGain = clip.getGainValue();
            currentGain++;
            if (currentGain <= clip.getMaximum()) {
                clip.setGainTo(currentGain);
            }
        }
        if (keyboard.keyDownOnce(KeyEvent.VK_6)) {
            float currentGain = clip.getGainValue();
            currentGain--;
            if (currentGain >= clip.getMinimum()) {
                clip.setGainTo(currentGain);
            }
        }
        if (keyboard.keyDownOnce(KeyEvent.VK_7)) {
           float pan =  clip.getPanValue();
           pan -= 0.2f;
           if (pan < -1) pan = -1;
           clip.setPanTo(pan);
        }

        if (keyboard.keyDownOnce(KeyEvent.VK_8)) {
            float pan =  clip.getPanValue();
            pan += 0.2f;
            if (pan > 1) pan = 1;
            clip.setPanTo(pan);
        }

        if (keyboard.keyDownOnce(KeyEvent.VK_9)) {
            float currentGain = stream.getGainValue();
            currentGain++;
            if (currentGain <= stream.getMaximum()) {
                stream.setGainTo(currentGain);
            }
        }
        if (keyboard.keyDownOnce(KeyEvent.VK_0)) {
            float currentGain = stream.getGainValue();
            currentGain--;
            if (currentGain >= stream.getMinimum()) {
                stream.setGainTo(currentGain);
            }
        }
        if (keyboard.keyDownOnce(KeyEvent.VK_Q)) {
            float pan =  stream.getPanValue();
            pan -= 0.2f;
            if (pan < -1) pan = -1;
            stream.setPanTo(pan);
        }

        if (keyboard.keyDownOnce(KeyEvent.VK_W)) {
            float pan =  stream.getPanValue();
            pan += 0.2f;
            if (pan > 1) pan = 1;
            stream.setPanTo(pan);
        }

    }

    @Override
    protected void render(Graphics g) {
        super.render(g);
        Utility.drawString(g, 20, textPos, "Clip gain " + clip.getGainValue(),
                "Clip pan " + clip.getPanValue(),
                "Stream gain " + stream.getGainValue(),
                "Stream pan " + stream.getPanValue(),
                "",
                "(1) Start loop (clip)",
                "(2) Stop loop (clip)",
                "(3) Start loop (stream)",
                "(4) Stop loop (stream)",
                "",
                "***** Clip Control *****",
                "(5) Raise gain",
                "(6) Lower gain",
                "(7) pan left",
                "(8) pan right", "",

                "***** Stream Control *****",

                "(9) Raise gain",
                "(0) Lower gain",
                "(q) pan left",
                "(w) pan right"

        );
    }

    @Override
    protected void terminate() {
        super.terminate();
        loopClip.shutDown();
        loopStream.shutDown();
    }

    public static void main(String[] args) {
        launchApp(new SoundControlExample());
    }
}

package javagames.example.ch12;

import javagames.util.SimpleFramework;
import javagames.util.Utility;

import java.awt.*;
import java.awt.event.KeyEvent;

public class MultiThreadEventExample extends SimpleFramework {

    private OneShotEvent oneShotEvent;
    private LoopEvent loopEvent;
    private RestartEvent restartEvent;

    public MultiThreadEventExample() {
        appBackground = Color.white;
        appWidth = 640;
        appHeight = 640;
    }

    @Override
    protected void initialize() {
        super.initialize();
        oneShotEvent = new OneShotEvent(5000, 10);
        oneShotEvent.initialize();
        loopEvent = new LoopEvent(3000, 4);
        loopEvent.initialize();
        restartEvent = new RestartEvent(5000, 10);
        restartEvent.initialize();
    }

    @Override
    protected void processInput(float t) {
        super.processInput(t);
        if (keyboard.keyDownOnce(KeyEvent.VK_1)) {
            oneShotEvent.fire();
        }
        if (keyboard.keyDownOnce(KeyEvent.VK_2)) {
            oneShotEvent.done();
        }
        if (keyboard.keyDownOnce(KeyEvent.VK_3)) {
            loopEvent.fire();
        }
        if (keyboard.keyDownOnce(KeyEvent.VK_4)) {
            loopEvent.done();
        }
        if (keyboard.keyDownOnce(KeyEvent.VK_5)) {
            restartEvent.fire();
        }
    }

    @Override
    protected void updateObjects(float t) {
        super.updateObjects(t);
    }

    @Override
    protected void render(Graphics g) {
        super.render(g);
        g.setColor(new Color(0, 0, 0, 0.5f));
        Utility.drawString(g, 20, textPos, "1) Oneshot.fire()",
                "2) OneShot.done()",
                "3) LoopEvent.fire()",
                "4) LoopEvent.done()",
                "5) RestartEvent.fire()"
                );
    }

    @Override
    protected void terminate() {
        super.terminate();
        oneShotEvent.shutDown();
        loopEvent.shutDown();
        restartEvent.shutDown();
    }

    public static void main(String[] args) {
        launchApp(new MultiThreadEventExample());
    }
}

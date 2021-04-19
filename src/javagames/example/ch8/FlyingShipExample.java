package javagames.example.ch8;

import javagames.sound.BlockingClip;
import javagames.sound.LoopEvent;
import javagames.sound.RestartEvent;
import javagames.util.Matrix3x3f;
import javagames.util.ResourceLoader;
import javagames.util.SimpleFramework;
import javagames.util.Vector2f;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class FlyingShipExample extends SimpleFramework {

    PolygonWrapper polygonWrapper;
    PrototypeShip ship;
    List<PrototypeBullet> bullets;
    private RestartEvent fire;

    private void drawBullets(Graphics g) {
        for (int i = 0; i < bullets.size(); i++) {
            PrototypeBullet prototypeBullet = bullets.get(i);
            Vector2f pos = prototypeBullet.getPosition();
            if (polygonWrapper.hasLeftWorld(pos)) {
                bullets.remove(i);
                i--;
            } else {
                prototypeBullet.draw((Graphics2D) g, getViewportTransform());
            }
        }
    }


    @Override
    protected void initialize() {
        InputStream in = ResourceLoader.load(FlyingShipExample.class, null, "/assets/sound/WEAPON_scifi_fire_02.wav");
        try {
            fire = new RestartEvent(new BlockingClip(in.readAllBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        appSleep = 0;
        super.initialize();
        polygonWrapper = new PolygonWrapper(appWorldWidth, appWorldHeight);
        ship = new PrototypeShip(new Vector2f(), 0, polygonWrapper);
        bullets = new ArrayList<>();
    }

    @Override
    protected void processInput(float t) {
        super.processInput(t);

        ship.setThrust(keyboard.keyDown(KeyEvent.VK_SPACE));

        if (keyboard.keyDownOnce(KeyEvent.VK_UP)) {
            bullets.add(ship.launchBullet());
            fire.fire();
        }

        if (keyboard.keyDown(KeyEvent.VK_LEFT))
            ship.rotateLeft(t);

        if (keyboard.keyDown(KeyEvent.VK_RIGHT))
            ship.rotateRight(t);

    }

    @Override
    protected void updateObjects(float t) {
        super.updateObjects(t);
        ship.update(t);
        bullets.forEach(new Consumer<PrototypeBullet>() {
            @Override
            public void accept(PrototypeBullet prototypeBullet) {
                prototypeBullet.update(t);
            }
        });
    }

    @Override
    protected void render(Graphics g) {
        super.render(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        ship.draw((Graphics2D) g, getViewportTransform());
        drawBullets(g);
    }

    public static void main(String[] args) {
        FlyingShipExample app = new FlyingShipExample();
        launchApp(app);
    }
}

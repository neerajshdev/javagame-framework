package javagames.example.ch8;

import javagames.util.Matrix3x3f;
import javagames.util.SimpleFramework;
import javagames.util.Vector2f;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javagames.example.ch8.PrototypeAsteroid.Size;

public class RandomAsteroidExample extends SimpleFramework {

    private List<PrototypeAsteroid> asteroids;
    private Random rand;
    private PrototypeAsteroidFactory asteroidFactory;
    private final float hw = appWorldWidth / 2;
    private final float hh = appWorldHeight / 2;

    public RandomAsteroidExample() {
        appBackground = Color.white;
        appTitle = "Random Asteroid example";
        appFPSColor = Color.red;
    }

    @Override
    protected void initialize() {
        super.initialize();
        asteroids = new ArrayList<>();
        rand = new Random();
        asteroidFactory = new PrototypeAsteroidFactory(new PolygonWrapper(appWorldWidth, appWorldHeight));
        createAsteroids();
    }

    @Override
    protected void processInput(float t) {
        super.processInput(t);

        if (keyboard.keyDownOnce(KeyEvent.VK_R)) {
            asteroids.clear();
            createAsteroids();
        }
    }

    @Override
    protected void updateObjects(float t) {
        super.updateObjects(t);

        for (PrototypeAsteroid asteroid : asteroids) {
            asteroid.update(t);
        }
    }

    @Override
    protected void render(Graphics g) {
        g.drawString("Press R to respawn", 20, 35);
        Matrix3x3f view = getViewportTransform();

        for (PrototypeAsteroid asteroid : asteroids) {
            asteroid.draw((Graphics2D) g, view);
        }

        super.render(g);
        g.drawString("Press R to respawn", 20, 35);
    }

    private void createAsteroids() {
        for (int i = 0; i < 42; i++) {
            asteroids.add(getRandomAsteroid());
        }
    }

    private PrototypeAsteroid getRandomAsteroid() {
        // get the random position
        float x = rand.nextFloat() * hw;
        float y = rand.nextFloat() * hh;
        x = rand.nextBoolean() ? -x : x;
        y = rand.nextBoolean() ? -y : y;
        Size[] sizes = Size.values();
        return createRandomAsteroid(new Vector2f(x, y), sizes[rand.nextInt(sizes.length)]);
    }

    private PrototypeAsteroid createRandomAsteroid(Vector2f pos, PrototypeAsteroid.Size randomSize) {
        PrototypeAsteroid asteroid = null;
        switch (randomSize) {
            case Large:
               asteroid =  asteroidFactory.createLargeAsteroid(pos);
               break;
            case Medium:
                asteroid =  asteroidFactory.createMediumAsteroid(pos);
                break;
            case Small:
                asteroid = asteroidFactory.createSmallAsteroid(pos);
        }
        return asteroid;
    }

    public static void main(String[] args) {
        RandomAsteroidExample app = new RandomAsteroidExample();
        launchApp(app);
    }
}

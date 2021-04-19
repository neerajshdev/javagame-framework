package javagames.example.ch15;

import javagames.util.Matrix3x3f;
import javagames.util.SimpleFramework;
import javagames.util.Utility;
import javagames.util.Vector2f;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Random;

public class BouncingPoints extends SimpleFramework {
    public BouncingPoints() {
        appWidth = 600;
        appHeight = 600;
        appBackground = Color.white;
        appFPSColor = Color.BLACK;
    }

    Vector2f[] polygonModel;
    Vector2f[] points;
    Vector2f[] velocities;
    int totalPoints = 1;
    int MAX_POINTS = 300;
    int MIN_POINTS = 2;

    @Override
    protected void initialize() {
        super.initialize();
        polygonModel = new Vector2f[] {
                new Vector2f(-0.753125f, 0.550000f),
                new Vector2f(0.525000f, 0.920833f),
                new Vector2f(0.603125f, 0.675000f),
                new Vector2f(0.012500f, 0.633333f),
                new Vector2f(0.603125f, 0.537500f),
                new Vector2f(0.790625f, -0.670833f),
                new Vector2f(0.165625f, -0.791667f),
                new Vector2f(0.521875f, 0.391667f),
                new Vector2f(0.025000f, 0.520833f),
                new Vector2f(0.056250f, -0.812500f),
                new Vector2f(-0.775000f, -0.862500f),
                new Vector2f(-0.200000f, -0.229167f),
                new Vector2f(-0.906250f, 0.258333f)
        };

        createPoints();
    }

    private void createPoints() {
        Random random = new Random();
        points = new Vector2f[totalPoints];
        velocities = new Vector2f[totalPoints];
        for (int i = 0; i < points.length; i++) {
            points[i] = new Vector2f();
            float direction = (float) (random.nextFloat() * 2 * Math.PI);
            velocities[i] = Vector2f.polar(direction, random.nextFloat() + 1);
        }
    }

    @Override
    protected void processInput(float t) {
        super.processInput(t);
        if (keyboard.keyDownOnce(KeyEvent.VK_UP)) {
            totalPoints *= 2;
            if (totalPoints > MAX_POINTS) {
                totalPoints = MAX_POINTS;
            }
            createPoints();
        }
        if (keyboard.keyDownOnce(KeyEvent.VK_DOWN)) {
            totalPoints /= 2;
            if (totalPoints < MIN_POINTS) {
                totalPoints = MIN_POINTS;
            }
            createPoints();
        }
    }

    @Override
    protected void updateObjects(float t) {
        super.updateObjects(t);
        for (int i = 0; i < points.length; i++) {
            Vector2f point = points[i];
            Vector2f vel = velocities[i];
            Vector2f newPoint =  point.add(vel.mul(t));
            Vector2f s = polygonModel[polygonModel.length - 1];
            Vector2f reflection = null;
            for (Vector2f e : polygonModel) {
                if (hitPolygon(point, newPoint, s, e)) {
                    reflection = reflect(vel, e.sub(s));
                    break;
                }
                s = e;
            }
            if (reflection != null) {
                velocities[i] = reflection;
            } else {
                points[i] = newPoint;
            }
        }
    }

    private boolean hitPolygon(Vector2f p1, Vector2f p2, Vector2f a, Vector2f b ) {
        Vector2f ab = p2.sub(p1);
        Vector2f cd = b.sub(a);
        Vector2f ca = p1.sub(a);
        Vector2f abP = ab.perp();
        Vector2f cdP = cd.perp();
        float n1 = cdP.dot(ca);
        float n2 = abP.dot(ca);
        float d = abP.dot(cd);
        float t1 = n1 / d;
        float t2 = n2 / d;
        return (!(t1 > 1) && !(t1 < 0)) && (!(t2 > 1) && !(t2 < 0));
    }

    private float distance(Vector2f p, Vector2f a, Vector2f b) {
        Vector2f ab = b.sub(a);
        Vector2f bp = p.sub(b);
        Vector2f n = ab.perp().norm();
         return n.dot(bp);
    }

    private Vector2f reflect(Vector2f v, Vector2f surface) {
        Vector2f n = surface.perp().norm();
        Vector2f vn = n.mul(v.dot(n));
        Vector2f vp = v.sub(vn);
        return vp.sub(vn);
    }

    @Override
    protected void render(Graphics g) {
        super.render(g);
        Matrix3x3f view = getViewportTransform();
        // draw polygon
        Vector2f[] poly = Utility.transform(polygonModel, view);
        Utility.drawPolygon((Graphics2D) g, poly);
        // draw points
        for (int i = 0; i < points.length; i++) {
            Vector2f point = view.mul(points[i]);
            int X = (int)point.x;
            int Y = (int)point.y;
            g.fillOval(X, Y, 4, 4);
        }
    }

    public static void main(String[] args) {
        launchApp(new BouncingPoints());
    }
}

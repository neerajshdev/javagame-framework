package javagames.example.ch15;

import javagames.util.Matrix3x3f;
import javagames.util.SimpleFramework;
import javagames.util.Utility;
import javagames.util.Vector2f;

import java.awt.*;

public class Reflection extends SimpleFramework {

    private Vector2f[] line1;
    private Vector2f[] line2;
    private Vector2f reflection;
    private Vector2f pointOfCollision;

    public Reflection() {
        appWidth = 600;
        appHeight = 600;
        appBackground = Color.gray;
        appFPSColor = Color.magenta;
    }

    @Override
    protected void initialize() {
        super.initialize();
        line1 = new Vector2f[] {
                new Vector2f(0, 0), new Vector2f(getWorldMousePos())
        };

        line2 = new Vector2f[] {
                new Vector2f(0.5f, -0.0011f),  new Vector2f(-0.5f, -0.5f)
        };
    }

    @Override
    protected void processInput(float t) {
        super.processInput(t);
    }

    @Override
    protected void updateObjects(float t) {
        super.updateObjects(t);
        line1[1] = getWorldMousePos();
        if ((pointOfCollision = Utility.lineLineCollide(line1[0], line1[1], line2[0], line2[1])) != null) {
            Vector2f vector = line1[1].sub(pointOfCollision);
            Vector2f surface = line2[1].sub(line2[0]);
            reflection = reflection(vector, surface);
        }
    }

    private Vector2f reflection(Vector2f v, Vector2f surface) {
        Vector2f n = surface.perp().norm();
        Vector2f vn = n.mul(v.dot(n));
        Vector2f vp = v.sub(vn);
        return vp.sub(vn);
    }



    @Override
    protected void render(Graphics g) {
        super.render(g);
        Matrix3x3f view = getViewportTransform();

        //draw line1
        g.setColor(Color.BLUE);
        Vector2f point = view.mul(line1[0]);
        int startX = (int) point.x;
        int startY = (int) point.y;
        point = view.mul(line1[1]);
        int endX = (int) point.x;
        int endY = (int) point.y;
        g.drawLine(startX, startY, endX, endY);

        // draw line2
        g.setColor(Color.BLACK);
        point = view.mul(line2[0]);
        startX = (int) point.x;
        startY = (int) point.y;
        point = view.mul(line2[1]);
        endX = (int) point.x;
        endY = (int) point.y;
        g.drawLine(startX, startY, endX, endY);

        // draw reflection
        if (pointOfCollision != null && reflection != null) {
            g.setColor(Color.CYAN);
            point = view.mul(pointOfCollision);
            startX = (int) point.x;
            startY = (int) point.y;
            point = view.mul(pointOfCollision.add(reflection));
            endX = (int) point.x;
            endY = (int) point.y;
            g.drawLine(startX, startY, endX, endY);
        }
    }



    public static void main(String[] args) {
        launchApp(new Reflection());
    }
}

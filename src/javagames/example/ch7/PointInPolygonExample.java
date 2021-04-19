package javagames.example.ch7;

import javagames.util.Matrix3x3f;
import javagames.util.SimpleFramework;
import javagames.util.Utility;
import javagames.util.Vector2f;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PointInPolygonExample extends SimpleFramework {

    public PointInPolygonExample() {
        appTitle = "Point in polygon example";
    }
    private final int MAX_POINTS = 10000;
    private List<Vector2f> poly;
    private List<Vector2f> polyCpy;
    private List<Vector2f> inside;
    private List<Vector2f> outside;
    private boolean selected;
    private boolean winding;


    @Override
    protected void initialize() {
        super.initialize();
        poly = new ArrayList<>();
        polyCpy = new ArrayList<>();
        inside = new ArrayList<>();
        outside = new ArrayList<>();
    }

    @Override
    protected void processInput(float t) {
        super.processInput(t);
        if (mouse.buttonDownOnce(MouseEvent.BUTTON1)) {
            Vector2f p = getWorldMousePos();
            poly.add(p);
        }

        if (mouse.buttonDownOnce(MouseEvent.BUTTON3)) {
            poly.clear();
            System.out.println("clr");
        }

        if (keyboard.keyDownOnce(KeyEvent.VK_SPACE)) {
            winding = !winding;
        }

    }

    @Override
    protected void updateObjects(float t) {
        super.updateObjects(t);

        Random rand = new Random();
        inside.clear();
        outside.clear();
        for (int i = 0; i < MAX_POINTS; i ++) {
            float x = rand.nextFloat() * 2 -1;
            float y = rand.nextFloat() * 2 -1;
            Vector2f randomP = new Vector2f(x, y);
            if (pointInPolygon(randomP, poly, winding)) {
                inside.add(randomP);
            } else {
                outside.add(randomP);
            }
        }

        Vector2f mousePoint = getWorldMousePos();
        selected = pointInPolygon(mousePoint, poly, winding);
    }

    private boolean pointInPolygon(Vector2f point, List<Vector2f> poly, boolean winding) {
        int inside = 0;
        if (poly.size() > 2) {
            Vector2f start = poly.get(poly.size() - 1);
            boolean startAbove = start.y >= point.y;

            for(int i = 0; i < poly.size(); i ++) {
                Vector2f end = poly.get(i);
                boolean endAbove = end.y >= point.y;

                if (startAbove != endAbove) {
                    float m = (end.y - start.y) / (end.x - start.x);
                    float x = (point.y - start.y) / m + start.x;
                    if (x >= point.x) {
                        if (winding) {
                            inside += startAbove? 1 : -1;
                        } else {
                            inside = (inside == 0) ? 1 : 0;
                        }
                    }
                }

                start = end;
                startAbove = endAbove;
            }
        }
        return inside != 0;
    }

    @Override
    protected void render(Graphics g) {
        super.render(g);
        Matrix3x3f view = getViewportTransform();


        //draw inside blue
        g.setColor(Color.blue);
        for (Vector2f vector : inside) {
            Vector2f point = view.mul(vector);
            g.drawRect((int) point.x, (int) point.y, 1, 1);
        }

        //draw outside red
        g.setColor(Color.red);
        for (Vector2f vector : outside) {
            Vector2f point = view.mul(vector);
            g.drawRect((int) point.x, (int) point.y, 1, 1);
        }

        // draw polygon
        if (poly.size() > 2 ) {
            g.setColor(selected? Color.green: Color.blue);
            for (int i = 0; i < poly.size(); i ++) {
                polyCpy.add(view.mul(poly.get(i)));
            }
            Utility.drawPolygon((Graphics2D) g, polyCpy);
            polyCpy.clear();
        }

    }

    public static void main(String[] args) {
        launchApp(new PointInPolygonExample());
    }
}

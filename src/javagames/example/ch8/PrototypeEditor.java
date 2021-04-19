package javagames.example.ch8;

import javagames.util.Matrix3x3f;
import javagames.util.SimpleFramework;
import javagames.util.Utility;
import javagames.util.Vector2f;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class PrototypeEditor extends SimpleFramework {

    public PrototypeEditor() {
        appBackground = Color.white;
        appTitle = "prototype editor 1.0";
        appFPSColor = Color.black;
    }


    private List<Vector2f> v;
    private boolean closed;

    @Override
    protected void initialize() {
        super.initialize();
        closed = false;
        v = new ArrayList<>();
    }

    @Override
    protected void processInput(float t) {
        super.processInput(t);

        // left mouse button
        if (mouse.buttonDownOnce(MouseEvent.BUTTON1)) {
            Vector2f mousePos = getWorldMousePos();
            v.add(mousePos);
        }

        // right mouse button
        if (mouse.buttonDownOnce(MouseEvent.BUTTON3)) {
            v.remove(v.size() - 1);
        }

        // Q button from keyboard
        if (keyboard.keyDownOnce(KeyEvent.VK_Q)) {
            closed = !closed;
        }

        if (keyboard.keyDownOnce(KeyEvent.VK_C)) {
            v.clear();
        }

        // space button from keyboard
        if (keyboard.keyDownOnce(KeyEvent.VK_SPACE)) {
            spitOutCode();
        }
    }

    @Override
    protected void updateObjects(float t) {
        super.updateObjects(t);
    }

    @Override
    protected void render(Graphics g) {
        super.render(g);

        g.setColor(Color.blue);
        drawAxis(g);

        g.setColor(Color.black);
        Vector2f[] viewPoint = transform(v, getViewportTransform());

        if (!closed && v.size() > 1) {
            drawLines(g, viewPoint);
        }

        if (closed && v.size() > 2) {
            Utility.drawPolygon((Graphics2D) g, viewPoint);
        }

        if (v.size() == 1) {
            drawPoint(g, viewPoint[0]);
        }


    }

    private void drawAxis(Graphics g) {
        float halfW = appWorldWidth / 2;
        float halfH = appWorldHeight / 2;
        Vector2f h1 = new Vector2f(- halfW , 0);
        Vector2f h2 = new Vector2f(halfW , 0);
        Vector2f v1 = new Vector2f(0 , -halfH);
        Vector2f v2 = new Vector2f(0 , halfH);

        Matrix3x3f view = getViewportTransform();
        h1 = view.mul(h1);
        h2 = view.mul(h2);
        v1 = view.mul(v1);
        v2 = view.mul(v2);

        g.drawLine((int) h1.x, (int) h1.y, (int) h2.x, (int) h2.y);
        g.drawLine((int) v1.x, (int) v1.y, (int) v2.x, (int) v2.y);
    }

    private void drawPoint(Graphics g, Vector2f point) {
        g.drawRect((int) point.x, (int) point.y, 1,1);
    }

    private void drawLines(Graphics g, Vector2f[] points) {
        for (int i = 0; i < points.length - 1; i++) {
            Vector2f p1 = points[i];
            Vector2f p2 = points[i + 1];
            g.drawLine((int) p1.x, (int) p1.y, (int) p2.x, (int) p2.y);
        }

        Vector2f p1 = points[points.length - 1];
        Point p2 = mouse.getPosition();
        g.drawLine((int) p1.x, (int) p1.y, p2.x, p2.y);
    }

    public static Vector2f[] transform(List<Vector2f> vList, Matrix3x3f mat) {
        Vector2f[] transPoly = new Vector2f[vList.size()];
        for (int i = 0 ; i < vList.size(); i++) {
            transPoly[i] = mat.mul(vList.get(i));
        }

        return transPoly;
    }

    private void spitOutCode() {
        StringBuilder sb = new StringBuilder();
        sb.append("Vector2f[] v = new Vector2f[] { \n\t\t");

        for (int i = 0; i < v.size(); i++) {
            Vector2f v = this.v.get(i);
            sb.append(String.format("new Vector2f(%ff, %ff)", v.x, v.y));
            if (i != this.v.size() - 1) {
                sb.append(",\n\t\t");
            }
        }

        sb.append("\n}");
        System.out.println(sb.toString());
    }

    public static void main(String[] args) {
        PrototypeEditor app = new PrototypeEditor();
        launchApp(app);
    }

}

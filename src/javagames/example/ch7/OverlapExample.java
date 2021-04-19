package javagames.example.ch7;

import javagames.util.Matrix3x3f;
import javagames.util.SimpleFramework;
import javagames.util.Vector2f;

import java.awt.*;
import java.awt.event.MouseEvent;

public class OverlapExample extends SimpleFramework {


    private AABB aabb0;
    private AABB aabb1;
    private Circle circle;

    private Vector2f mousePos;
    private Vector2f mouseDelta;
    private boolean drag;

    private class AABB {
        Vector2f min;
        Vector2f max;
        public boolean intersect = false;
        public boolean selected = false;
        public boolean drag = false;

        public AABB(Vector2f min, Vector2f max) {
            this.min = min;
            this.max = max;
        }

        public void translate(float tx, float ty) {
            Matrix3x3f trans = Matrix3x3f.translate(tx, ty);
            min = trans.mul(min);
            max = trans.mul(max);
        }
    }

    private class Circle {
        public float r;
        public Vector2f c;
        public boolean intersect = false;
        public boolean drag = false;
        public boolean selected = false;

        public Circle(Vector2f c, float r) {
            this.c = c;
            this.r = r;
        }


    }

    public OverlapExample() {
        appTitle = "Overlap Example";
        appBackground = Color.WHITE;
        appFPSColor = Color.orange;
    }

    public static void main(String[] args) {
        OverlapExample app = new OverlapExample();
        launchApp(app);
    }

    @Override
    protected void initialize() {
        super.initialize();
        aabb0 = new AABB(new Vector2f(-0.2f, -0.2f), new Vector2f(0.2f, 0.2f));
        aabb1 = new AABB(new Vector2f(-0.3f, -0.2f), new Vector2f(0.3f, 0.2f));
//        circle = new Circle();
        mousePos = new Vector2f();
        drag = false;
    }

    @Override
    protected void processInput(float t) {
        super.processInput(t);

        Vector2f temp = getWorldMousePos();
        mouseDelta = temp.sub(mousePos);
        mousePos = temp;
    }

    @Override
    protected void updateObjects(float t) {
        super.updateObjects(t);

        boolean buttonDown = mouse.buttonDown(MouseEvent.BUTTON1);
        boolean buttonDownOnce = mouse.buttonDownOnce(MouseEvent.BUTTON1);
        aabb0.selected = pointInAABB(mousePos, aabb0.min, aabb0.max);
        aabb1.selected = pointInAABB(mousePos, aabb1.min, aabb1.max);

        if (aabb0.selected && buttonDownOnce) {
            aabb0.drag = true;
        }

        if (aabb1.selected && buttonDownOnce) {
            aabb1.drag = true;
        }

        if (aabb0.drag) {
            aabb0.translate(mouseDelta.x, mouseDelta.y);
        }

        if (aabb1.drag) {
            aabb1.translate(mouseDelta.x, mouseDelta.y);
        }

        if (!buttonDown) {
            aabb1.drag = false;
            aabb0.drag = false;
        }

        if (intersectAABB(aabb0.min, aabb0.max, aabb1.min, aabb1.max)) {
            aabb1.intersect = true;
            aabb0.intersect = true;
        } else {
            aabb1.intersect = false;
            aabb0.intersect = false;
        }


    }

    @Override
    protected void render(Graphics g) {
        super.render(g);

        g.setColor(aabb0.intersect ? Color.BLACK : Color.blue);
        drawAABB(aabb0.min, aabb0.max, g);
        g.setColor(aabb1.intersect ? Color.black : Color.blue);
        drawAABB(aabb1.min, aabb1.max, g);


    }

    private void drawAABB(Vector2f min, Vector2f max, Graphics g) {
        Matrix3x3f view = getViewportTransform();

        Vector2f topLeft = new Vector2f(min.x, max.y);
        topLeft = view.mul(topLeft);

        Vector2f bottomRight = new Vector2f(max.x, min.y);
        bottomRight = view.mul(bottomRight);

        int rectX = (int) topLeft.x;
        int rectY = (int) topLeft.y;
        int width = (int) (bottomRight.x - topLeft.x);
        int height = (int) (bottomRight.y - topLeft.y);

        g.drawRect(rectX, rectY, width, height);
    }

    private void drawCircle(Vector2f c, float r, Graphics g) {
        Matrix3x3f view = getViewportTransform();
        Vector2f topLeft = new Vector2f(c.x - r, c.y + r);
        topLeft = view.mul(topLeft);

        Vector2f bottomRight = new Vector2f(c.x + r, c.y - r);
        bottomRight = view.mul(bottomRight);

        int ovalX = (int) topLeft.x;
        int ovalY = (int) topLeft.y;
        int ovalW = (int) (bottomRight.x - topLeft.x);
        int ovalH = (int) (bottomRight.y - topLeft.y);

        g.drawOval(ovalX, ovalY, ovalW, ovalH);
    }

    private boolean pointInAABB(Vector2f point, Vector2f min, Vector2f max) {
        return point.x > min.x && point.x < max.x &&
                point.y > min.y && point.y < max.y;
    }

    private boolean intersectAABB(Vector2f minA, Vector2f maxA, Vector2f minB, Vector2f maxB) {
        if (maxA.x < minB.x || minA.x > maxB.x) return false;
        if (maxA.y < minB.y || minA.y > maxB.y) return false;
        return true;
    }

}

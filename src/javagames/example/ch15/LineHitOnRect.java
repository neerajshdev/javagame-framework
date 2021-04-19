package javagames.example.ch15;

import javagames.util.Matrix3x3f;
import javagames.util.SimpleFramework;
import javagames.util.Utility;
import javagames.util.Vector2f;

import java.awt.*;
import java.awt.event.MouseEvent;

public class LineHitOnRect extends SimpleFramework {

    // rectangle under test
    private Vector2f[] rect;
    private Vector2f rectPos;
    private float rot;
    private float angle;
    private Vector2f[] worldRect;
    //line start rect
    private Vector2f[] startRect;
    private Vector2f sRectPos;
    private Vector2f[] worldSRect;
    //line end rect
    private Vector2f[] endRect;
    private Vector2f eRectPos;
    private Vector2f[] worldERect;
    private Vector2f mousePos;
    Vector2f collisionPoint;



    public LineHitOnRect() {
        appWidth = 600;
        appHeight = 600;
        appBackground = Color.white;
        appFPSColor = Color.BLACK;
    }

    @Override
    protected void initialize() {
        super.initialize();
        rectPos = new Vector2f(0, 0);
        rot = (float) Math.toRadians(50);
        rect = createRect(0.45f, 0.55f);
        worldRect = rect;
        //Line start rect
        startRect = createRect(0.022f,0.022f );
        sRectPos = new Vector2f(-0.5f, -0.5f);
        worldSRect = startRect;
        //Line end rect
        endRect = startRect.clone();
        eRectPos = new Vector2f(0.5f, -0.5f);
        worldERect = endRect;
        mousePos = new Vector2f();
    }

    private Vector2f[] createRect(float rectWidth, float rectHeight) {
         return new Vector2f[] {
                new Vector2f(-rectWidth/2, rectHeight/2),
                new Vector2f(rectWidth/2, rectHeight/2),
                new Vector2f(rectWidth/2, -rectHeight/2),
                new Vector2f(-rectWidth/2, -rectHeight/2)
        };
    }

    @Override
    protected void processInput(float t) {
        super.processInput(t);
        Vector2f currentMousePos = getWorldMousePos();
        if (Utility.pointInPolygon(mousePos, worldSRect, false)) {
            if (mouse.buttonDown(MouseEvent.BUTTON1)) { // rect is hold
                Vector2f move = currentMousePos.sub(mousePos);
                sRectPos = sRectPos.add(move);
            }
        } else if (Utility.pointInPolygon(mousePos, worldERect, false)) {
            if (mouse.buttonDown(MouseEvent.BUTTON1)) { // rect is hold
                Vector2f move = currentMousePos.sub(mousePos);
                eRectPos = eRectPos.add(move);
            }
        } else if (Utility.pointInPolygon(mousePos, worldRect, false)) {
            if (mouse.buttonDown(MouseEvent.BUTTON1)) { // rect is hold
                Vector2f move = currentMousePos.sub(mousePos);
                rectPos = rectPos.add(move);
            }
        }
        mousePos = currentMousePos;
    }

    @Override
    protected void updateObjects(float t) {
        super.updateObjects(t);
        angle += rot * t;
        worldRect = Utility.transform(rect, Matrix3x3f.rotate(angle).mul(Matrix3x3f.translate(rectPos)));
        worldSRect = Utility.transform(startRect, Matrix3x3f.translate(sRectPos));
        worldERect = Utility.transform(endRect, Matrix3x3f.translate(eRectPos));
        // if line hit the rectangle then calculate the point of touch.
        if ( ( collisionPoint = Utility.lineRectCollide(sRectPos, eRectPos,worldRect ) ) != null ) {
            String title = String.format("collision point (%.2f, %.2f)", collisionPoint.x, collisionPoint.y);
            setTitle(title);
        } else {
            setTitle("collision point: (_, _)" );
        }
    }

    @Override
    protected void render(Graphics g) {
        g.setColor(Color.red);
        Matrix3x3f view = getViewportTransform();
        //draw rect under test
        Vector2f[] poly = Utility.transform(worldRect, view);
        Utility.drawPolygon((Graphics2D) g,poly);
        //draw line start rect
        g.setColor(Color.MAGENTA);
        poly = Utility.transform(worldSRect, view);
        Utility.drawPolygon((Graphics2D) g,poly);
        //draw line end rect
        g.setColor(Color.BLUE);
        poly = Utility.transform(worldERect, view);
        Utility.drawPolygon((Graphics2D) g,poly);
        //draw line
        g.setColor(Color.orange);
        Vector2f p = view.mul(sRectPos);
        int startX = (int)p.x;
        int startY = (int)p.y;
        p = view.mul(eRectPos);
        int endX = (int) p.x;
        int endY = (int) p.y;
        g.drawLine(startX, startY, endX, endY);
        g.setColor(Color.BLACK);
        Utility.drawString(g, startX, startY, "start");
        Utility.drawString(g, endX, endY, "end");
        // RENDER COLLISION POINT
        if (collisionPoint != null) {
            g.setColor(Color.BLUE);
            Vector2f cpLeft = collisionPoint.sub(new Vector2f(0.022f, 0f));
            Vector2f cpRight = collisionPoint.add(new Vector2f(0.022f, 0f));
            cpLeft = view.mul(cpLeft);
            cpRight = view.mul(cpRight);
            g.drawLine((int) cpLeft.x, (int) cpLeft.y, (int) cpRight.x, (int) cpRight.y);
            Vector2f cpUP = collisionPoint.add(new Vector2f(0f, 0.022f));
            Vector2f cpDown = collisionPoint.sub(new Vector2f(0f, 0.022f));
            cpUP = view.mul(cpUP);
            cpDown = view.mul(cpDown);
            g.drawLine((int) cpUP.x,(int) cpUP.y,(int) cpDown.x, (int) cpDown.y);
        }
        super.render(g);
    }

    @Override
    protected void terminate() {
        super.terminate();
    }

    public static void main(String[] args) {
        launchApp(new LineHitOnRect());
    }
}

package javagames.example.ch15;

import javagames.util.Matrix3x3f;
import javagames.util.SimpleFramework;
import javagames.util.Utility;
import javagames.util.Vector2f;

import java.awt.*;
import java.awt.event.MouseEvent;

public class LineToLineCollision extends SimpleFramework {

    //line1 start rect
    private Vector2f[] line1startRect;
    private Vector2f line1StartPos;
    private Vector2f[] line1worldStartRect;
    //line1 end rect
    private Vector2f[] line1EndRect;
    private Vector2f line1EndPos;
    private Vector2f[] line1worldEndRect;
    //line 2 start rect
    private Vector2f[] line2startRect;
    private Vector2f line2StartPos;
    private Vector2f[] line2worldStartRect;
    //line1 end rect
    private Vector2f[] line2EndRect;
    private Vector2f line2EndPos;
    private Vector2f[] line2worldEndRect;
    private Vector2f mousePos;
    Vector2f collisionPoint;



    public LineToLineCollision() {
        appWidth = 600;
        appHeight = 600;
        appBackground = Color.white;
        appFPSColor = Color.BLACK;
    }

    @Override
    protected void initialize() {
        super.initialize();
        //Line1 start rect
        line1startRect = createRect(0.022f,0.022f );
        line1StartPos = new Vector2f(-0.5f, -0.5f);
        line1worldStartRect = line1startRect;
        //Line1 end rect
        line1EndRect = line1startRect.clone();
        line1EndPos = new Vector2f(0.5f, -0.5f);
        line1worldEndRect = line1EndRect;
        //line2 start rect
        line2startRect = createRect(0.022f,0.022f );
        line2StartPos = new Vector2f(0.5f, 0.5f );
        line2worldStartRect = line1startRect;
        mousePos = new Vector2f();
        // line2 end rect
        line2EndRect = line2startRect.clone();
        line2EndPos = new Vector2f(0.6f, 0.9f);
        line2worldEndRect = line2EndRect;

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
        if (Utility.pointInPolygon(mousePos, line1worldStartRect, false)) {
            if (mouse.buttonDown(MouseEvent.BUTTON1)) { // rect is hold
                Vector2f move = currentMousePos.sub(mousePos);
                line1StartPos = line1StartPos.add(move);
            }
        } else if (Utility.pointInPolygon(mousePos, line1worldEndRect, false)) {
            if (mouse.buttonDown(MouseEvent.BUTTON1)) { // rect is hold
                Vector2f move = currentMousePos.sub(mousePos);
                line1EndPos = line1EndPos.add(move);
            }
        } else if (Utility.pointInPolygon(mousePos, line2worldStartRect, false)) {
            if (mouse.buttonDown(MouseEvent.BUTTON1)) { // rect is hold
                Vector2f move = currentMousePos.sub(mousePos);
                line2StartPos = line2StartPos.add(move);
            }
        } else if (Utility.pointInPolygon(mousePos, line2worldEndRect, false)) {
            if (mouse.buttonDown(MouseEvent.BUTTON1)) { // rect is hold
                Vector2f move = currentMousePos.sub(mousePos);
                line2EndPos = line2EndPos.add(move);
            }
        }
        mousePos = currentMousePos;
    }

    @Override
    protected void updateObjects(float t) {
        super.updateObjects(t);
        line1worldStartRect = Utility.transform(line1startRect, Matrix3x3f.translate(line1StartPos));
        line1worldEndRect = Utility.transform(line1EndRect, Matrix3x3f.translate(line1EndPos));
        line2worldStartRect = Utility.transform(line2startRect, Matrix3x3f.translate(line2StartPos));
        line2worldEndRect = Utility.transform(line2EndRect, Matrix3x3f.translate(line2EndPos));
        // if line hit the rectangle then calculate the point of touch.
        if ( ( collisionPoint = Utility.lineLineCollide(line1StartPos, line1EndPos, line2StartPos, line2EndPos ) ) != null ) {
            String title = String.format("collision point (%.2f, %.2f)", collisionPoint.x, collisionPoint.y);
            setTitle(title);
        } else {
            setTitle("collision point: (_, _)" );
        }
    }

    @Override
    protected void render(Graphics g) {
        Matrix3x3f view = getViewportTransform();
        //draw line1 start rect
        g.setColor(Color.BLUE);
        Vector2f[] poly = Utility.transform(line1worldStartRect, view);
        Utility.drawPolygon((Graphics2D) g,poly);
        //draw line1 end rect
        poly = Utility.transform(line1worldEndRect, view);
        Utility.drawPolygon((Graphics2D) g,poly);
        //draw line1
        drawLine(g, view.mul(line1StartPos), view.mul(line1EndPos));
        g.setColor(Color.magenta);
        //draw line2 start rect
        poly =  Utility.transform(line2worldStartRect, view);
        Utility.drawPolygon((Graphics2D) g,poly);
        //draw line2 end rect
        poly =  Utility.transform(line2worldEndRect, view);
        Utility.drawPolygon((Graphics2D) g,poly);
        //draw line2
        drawLine(g, view.mul(line2StartPos), view.mul(line2EndPos));
        // RENDER COLLISION POINT
        if (collisionPoint != null) {
            g.setColor(Color.red);
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

    private void drawLine(Graphics g, Vector2f start , Vector2f end) {
        int startX = (int)start.x;
        int startY = (int)start.y;
        int endX = (int) end.x;
        int endY = (int) end.y;
        g.drawLine(startX, startY, endX, endY);
        g.setColor(Color.black);
        Utility.drawString(g, startX, startY, "start");
        Utility.drawString(g, endX, endY, "end");
    }

    @Override
    protected void terminate() {
        super.terminate();
    }

    public static void main(String[] args) {
        launchApp(new LineToLineCollision());
    }
}

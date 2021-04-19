package javagames.example.ch8;

import javagames.util.Matrix3x3f;
import javagames.util.SimpleFramework;
import javagames.util.Utility;
import javagames.util.Vector2f;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class ScreenWrapExample extends SimpleFramework {
    public ScreenWrapExample() {
        appWidth = 800;
        appHeight = 600;
        appBackground = Color.WHITE;
        appBorderScale = 0.89f;
        appBorder = Color.gray;
        appTitle = "Wrapping example";
        appMaintainRatio = true;
        appFPSColor = Color.blue;
        appSleep = 10;
    }


    private Vector2f[] poly;
    private Vector2f pos;
    private List<Vector2f[]> renderList;

    private PolygonWrapper wrapper;



    @Override
    protected void initialize() {
        super.initialize();

        poly = new Vector2f[] {
                new Vector2f(-0.153125f, 0.225000f),
                new Vector2f(-0.134375f, 0.245833f),
                new Vector2f(-0.115625f, 0.270833f),
                new Vector2f(-0.093750f, 0.279167f),
                new Vector2f(-0.087500f, 0.283333f),
                new Vector2f(-0.071875f, 0.287500f),
                new Vector2f(-0.059375f, 0.287500f),
                new Vector2f(-0.037500f, 0.287500f),
                new Vector2f(-0.021875f, 0.287500f),
                new Vector2f(0.009375f, 0.295833f),
                new Vector2f(0.025000f, 0.295833f),
                new Vector2f(0.034375f, 0.304167f),
                new Vector2f(0.043750f, 0.304167f),
                new Vector2f(0.062500f, 0.291667f),
                new Vector2f(0.084375f, 0.283333f),
                new Vector2f(0.100000f, 0.275000f),
                new Vector2f(0.100000f, 0.275000f),
                new Vector2f(0.118750f, 0.266667f),
                new Vector2f(0.121875f, 0.262500f),
                new Vector2f(0.143750f, 0.254167f),
                new Vector2f(0.150000f, 0.250000f),
                new Vector2f(0.165625f, 0.237500f),
                new Vector2f(0.171875f, 0.229167f),
                new Vector2f(0.178125f, 0.212500f),
                new Vector2f(0.187500f, 0.187500f),
                new Vector2f(0.187500f, 0.170833f),
                new Vector2f(0.187500f, 0.158333f),
                new Vector2f(0.184375f, 0.137500f),
                new Vector2f(0.178125f, 0.116667f),
                new Vector2f(0.171875f, 0.108333f),
                new Vector2f(0.171875f, 0.104167f),
                new Vector2f(0.168750f, 0.100000f),
                new Vector2f(0.165625f, 0.091667f),
                new Vector2f(0.165625f, 0.083333f),
                new Vector2f(0.165625f, 0.070833f),
                new Vector2f(0.171875f, 0.062500f),
                new Vector2f(0.178125f, 0.054167f),
                new Vector2f(0.184375f, 0.050000f),
                new Vector2f(0.190625f, 0.033333f),
                new Vector2f(0.190625f, -0.016667f),
                new Vector2f(0.190625f, -0.037500f),
                new Vector2f(0.190625f, -0.041667f),
                new Vector2f(0.184375f, -0.050000f),
                new Vector2f(0.184375f, -0.058333f),
                new Vector2f(0.181250f, -0.062500f),
                new Vector2f(0.181250f, -0.079167f),
                new Vector2f(0.181250f, -0.087500f),
                new Vector2f(0.178125f, -0.108333f),
                new Vector2f(0.175000f, -0.120833f),
                new Vector2f(0.171875f, -0.133333f),
                new Vector2f(0.171875f, -0.170833f),
                new Vector2f(0.171875f, -0.187500f),
                new Vector2f(0.171875f, -0.191667f),
                new Vector2f(0.165625f, -0.200000f),
                new Vector2f(0.165625f, -0.200000f),
                new Vector2f(0.162500f, -0.216667f),
                new Vector2f(0.162500f, -0.233333f),
                new Vector2f(0.162500f, -0.245833f),
                new Vector2f(0.121875f, -0.250000f),
                new Vector2f(0.121875f, -0.250000f),
                new Vector2f(0.121875f, -0.254167f),
                new Vector2f(0.134375f, -0.279167f),
                new Vector2f(0.140625f, -0.295833f),
                new Vector2f(0.134375f, -0.308333f),
                new Vector2f(0.109375f, -0.320833f),
                new Vector2f(0.093750f, -0.325000f),
                new Vector2f(0.078125f, -0.329167f),
                new Vector2f(0.053125f, -0.325000f),
                new Vector2f(0.025000f, -0.316667f),
                new Vector2f(-0.012500f, -0.329167f),
                new Vector2f(-0.087500f, -0.312500f),
                new Vector2f(-0.093750f, -0.291667f),
                new Vector2f(-0.100000f, -0.258333f),
                new Vector2f(-0.100000f, -0.233333f),
                new Vector2f(-0.100000f, -0.179167f),
                new Vector2f(-0.081250f, -0.104167f),
                new Vector2f(-0.012500f, -0.020833f),
                new Vector2f(-0.028125f, -0.041667f),
                new Vector2f(-0.100000f, -0.070833f),
                new Vector2f(-0.137500f, -0.083333f),
                new Vector2f(-0.171875f, -0.091667f),
                new Vector2f(-0.212500f, -0.062500f),
                new Vector2f(-0.178125f, -0.025000f),
                new Vector2f(-0.125000f, 0.008333f),
                new Vector2f(-0.109375f, 0.037500f),
                new Vector2f(-0.146875f, 0.062500f),
                new Vector2f(-0.240625f, 0.179167f),
                new Vector2f(-0.225000f, 0.204167f),
                new Vector2f(-0.159375f, 0.250000f)

    };

        renderList = new ArrayList<>();

        pos = new Vector2f();
        mouse.setRelative(false);
        wrapper = new PolygonWrapper(appWorldWidth, appWorldHeight);
    }

    @Override
    protected void processInput(float t) {
        super.processInput(t);

        if (mouse.isRelative()) {
            pos = pos.add(getRelativeWorldMousePos());
        }

        if (keyboard.keyDownOnce(KeyEvent.VK_SPACE)) {
            mouse.setRelative(!mouse.isRelative());
        }
    }

    @Override
    protected void updateObjects(float t) {
        super.updateObjects(t);

        renderList.clear();
        renderList.add(Utility.transform(poly, Matrix3x3f.translate(pos)));
        wrapper.wrapPosition(pos);
        wrapper.wrapPolygon(renderList.get(0), renderList);
    }

    @Override
    protected void render(Graphics g) {
        super.render(g);

        for (Vector2f[] poly : renderList) {
            Vector2f[] scrPoly = Utility.transform(poly, getViewportTransform());
            g.setColor(Color.gray);
            Utility.fillPolygon((Graphics2D) g, scrPoly);
            g.setColor(Color.black);
            Utility.drawPolygon((Graphics2D) g, scrPoly);
        }
    }


    public static void main(String[] args) {
        ScreenWrapExample app = new ScreenWrapExample();
        launchApp(app);
    }
}

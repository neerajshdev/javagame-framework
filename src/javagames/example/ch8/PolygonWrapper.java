package javagames.example.ch8;

import javagames.util.Matrix3x3f;
import javagames.util.Utility;
import javagames.util.Vector2f;

import java.util.List;

public class PolygonWrapper {
    private final float MAX = Float.MAX_VALUE;

    private Vector2f worldMin, worldMax;
    private float worldWidth, worldHeight;

    public PolygonWrapper(float worldWidth, float worldHeight) {
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        worldMax = new Vector2f(worldWidth/2, worldHeight/2);
        worldMin = worldMax.inv();
    }

    public void wrapPosition(Vector2f pos) {
        if (pos.x < worldMin.x) {
            pos.x += worldWidth;
        }

        if (pos.x > worldMax.x) {
            pos.x -= worldWidth;
        }

        if (pos.y < worldMin.y) {
            pos.y += worldHeight;
        }

        if (pos.y > worldMax.y) {
            pos.y -= worldHeight;
        }
    }

    public boolean hasLeftWorld( Vector2f position ) {
        return position.x < worldMin.x || position.x > worldMax.x ||
                position.y < worldMin.y || position.y > worldMax.y;
    }

    public void wrapPolygon(Vector2f[] poly, List<Vector2f[]> renderList ) {
        Vector2f min = getMin(poly);
        Vector2f max = getMax(poly);

        boolean north = max.y > worldMax.y;
        boolean south = min.y < worldMin.y;
        boolean east  = max.x > worldMax.x;
        boolean west  = min.x < worldMin.x;

        if (north) {
            renderList.add(Utility.transform(poly, Matrix3x3f.translate(0, -worldHeight)));
        }

        if (south) {
            renderList.add(Utility.transform(poly, Matrix3x3f.translate(0, worldHeight)));
        }

        if (east) {
            renderList.add(Utility.transform(poly, Matrix3x3f.translate(-worldWidth, 0)));
        }

        if (west) {
            renderList.add(Utility.transform(poly, Matrix3x3f.translate(worldWidth, 0)));
        }

        if (north && east) {
            renderList.add(Utility.transform(poly, Matrix3x3f.translate(-worldWidth, -worldHeight)));
        }

        if (north && west) {
            renderList.add(Utility.transform(poly, Matrix3x3f.translate(worldWidth, -worldHeight)));
        }

        if (south && west) {
            renderList.add(Utility.transform(poly, Matrix3x3f.translate(worldWidth, worldHeight)));
        }

        if (south && east) {
            renderList.add(Utility.transform(poly, Matrix3x3f.translate(-worldWidth, worldHeight)));
        }
    }

    private Vector2f getMin(Vector2f[] poly) {
        Vector2f min = new Vector2f(MAX, MAX);
        for (Vector2f p : poly) {
            min.x = Math.min(min.x, p.x);
            min.y = Math.min(min.y, p.y);
        }

        return min;
    }

    private Vector2f getMax(Vector2f[] poly) {
        Vector2f max = new Vector2f(-MAX, -MAX);
        for (Vector2f p : poly) {
            max.x = Math.max(max.x, p.x);
            max.y = Math.max(max.y, p.y);
        }

        return max;
    }
}

package javagames.example.ch8;

import javagames.util.Matrix3x3f;
import javagames.util.Utility;
import javagames.util.Vector2f;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PrototypeAsteroid {


    public enum Size{
        Large,
        Medium,
        Small;
    }

    private PolygonWrapper wrapper;
    private List<Vector2f[]> renderList;
    private Vector2f[] poly;
    private Vector2f pos;
    private Size size;
    private Vector2f vel;
    private float rot;
    private float rotVel;


    public PrototypeAsteroid(PolygonWrapper wrapper) {
        this.wrapper = wrapper;
        renderList = new ArrayList();
        // set the random velocity
        vel = getRandomVelocity();
        rotVel = getRandomRotationDelta();
    }

    private float getRandomFloat(float min, float max) {
        return new Random().nextFloat() * (max - min) + min;
    }

    private float getRandomRadians(int minDegree, int maxDegree) {
        int rand = new Random().nextInt(maxDegree - minDegree + 1) + minDegree;
        return (float) Math.toRadians(rand);
    }

    private float getRandomRotationDelta() {
        float radians = getRandomRadians(5, 45);
        return new Random().nextBoolean() ? radians : -radians;
    }

    private Vector2f getRandomVelocity(){
        float angle = getRandomRadians(0,360);
        float r = getRandomFloat(0.06f, 0.3f);
        return Vector2f.polar(angle, r);
    }

    public void setPolygon(Vector2f[] poly) {
        this.poly = poly;
    }

    public Vector2f[] getPolygon() {
        return poly;
    }

    public void setSize(Size size){
        this.size = size;
    }

    public Size getSize() {
        return size;
    }

    public void setPosition(Vector2f pos){
        this.pos = pos;
    }

    public Vector2f getPosition() {
        return pos;
    }


    public void update(float t){
        pos = pos.add(vel.mul(t));
        rot = rot + rotVel * t;
        wrapper.wrapPosition(pos);
        // transform the poly in world
        Vector2f[] world = Utility.transform(
                poly,
                Matrix3x3f.rotate(rot).mul(Matrix3x3f.translate(pos))
        );

        renderList.clear();
        renderList.add(world);

        wrapper.wrapPolygon(world, renderList);
    }


    public void draw(Graphics2D g, Matrix3x3f view){

        for(Vector2f[] toRender: renderList) {
            Vector2f[] poly = Utility.transform(toRender, view);
            g.setColor(Color.LIGHT_GRAY);
            Utility.fillPolygon((Graphics2D)g, poly);
            g.setColor(Color.BLACK);
            Utility.drawPolygon(g, poly);
        }
    }

    public boolean contains(Vector2f point) {
        for (Vector2f[]poly : renderList) {
            if (pointInPolygon(point, poly)) {
                return  true;
            }
        }
        return false;
    }

    private boolean pointInPolygon(Vector2f point, Vector2f[] poly) {
        boolean inside = false;
        if (poly.length > 2) {
            Vector2f start = poly[poly.length - 1];
            boolean startAbove = start.y >= point.y;

            for (int i = 0; i < poly.length; i ++) {
                Vector2f end = poly[i];
                boolean endAbove = end.y >= point.y;
                if (startAbove != endAbove) {
                    float m = (end.y - start.y) / (end.x - start.x);
                    float x = (point.y - start.y) / m + start.x;
                    if (x >= point.x) {
                        inside = !inside;
                    }
                }
                start = end;
                startAbove = endAbove;
            }
        }
        return inside;
    }
}

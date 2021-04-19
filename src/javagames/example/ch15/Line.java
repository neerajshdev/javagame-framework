package javagames.example.ch15;

import javagames.util.Matrix3x3f;
import javagames.util.Vector2f;

import java.awt.*;

public class Line {
    private Vector2f[] points;
    private float velocity;

    public Line( float velocity, Vector2f... points) {
        this.points = points;
        this.velocity = velocity;
    }

    public Vector2f[] getPoints() {
        return points;
    }

    public void setPoints(Vector2f[] points) {
        this.points = points;
    }

    public void update(float dt) {
        Vector2f velocity = points[1].sub(points[0]).norm().mul(this.velocity);
        Vector2f displacement = velocity.mul(dt);
        for (int i = 0; i < 2; i++) {
            points[i] = points[i].add(displacement);
        }
    }

    public void draw(Graphics g, Matrix3x3f view) {
        Vector2f point = view.mul(this.points[0]);
        int startX = (int) point.x;
        int startY = (int) point.y;
        point = view.mul(this.points[1]);
        int endX = (int) point.x;
        int endY = (int) point.y;
        g.drawLine(startX, startY, endX, endY);
    }
}

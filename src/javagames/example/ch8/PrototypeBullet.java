package javagames.example.ch8;

import javagames.util.Matrix3x3f;
import javagames.util.Vector2f;

import java.awt.*;

public class PrototypeBullet {
    private Vector2f velocity;
    private Vector2f position;
    private Color color;
    private float radius;

    // angle: required for direction in which it moves forward
    //position: from where is it launch?
    public PrototypeBullet(Vector2f position, float angle) {
        this.position = position;
        velocity = Vector2f.polar(angle, 1.0f);
        color = Color.GREEN;
        radius = 0.006f;
    }


    public Vector2f getPosition() {
        return position;
    }

    public void update(float t) {
        position = position.add(velocity.mul(t));
    }

    public void draw(Graphics2D g, Matrix3x3f view) {
        g.setColor(color);

        Vector2f topLeft = new Vector2f(position.x - radius, position.y + radius);
        topLeft = view.mul(topLeft);

        Vector2f bottomRight = new Vector2f(position.x + radius, position.y - radius);
        bottomRight = view.mul(bottomRight);

        int rectX = (int) topLeft.x;
        int rectY = (int) topLeft.y;
        int rectW = (int) (bottomRight.x - topLeft.x);
        int rectH = (int) (bottomRight.y - topLeft.y);

        g.fillOval(rectX, rectY, rectW, rectH);
    }
 }

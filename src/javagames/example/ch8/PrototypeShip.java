package javagames.example.ch8;

import javagames.util.Matrix3x3f;
import javagames.util.Utility;
import javagames.util.Vector2f;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class PrototypeShip {
    private PolygonWrapper wrapper;
    private List<Vector2f[]> renderList;
    private Vector2f[] ship;
    private Vector2f position;
    private float angle;
    private float acceleration;
    private Vector2f velocity;
    private float maxVelocity;
    private float currAcc;

    private boolean damaged;
    private float rotationDelta;


    public PrototypeShip(Vector2f position, float angle, PolygonWrapper wrapper) {
       construct(position, angle, wrapper);
    }

    public PrototypeShip(PolygonWrapper wrapper) {
        construct(new Vector2f(), 0, wrapper);
    }

    private void construct(Vector2f position, float angle, PolygonWrapper wrapper) {
        velocity = new Vector2f();
        acceleration = 1f;
        maxVelocity = 0.5f;
        rotationDelta = (float) Math.toRadians(100);
        this.position = position;
        this.angle = angle;
        this.wrapper = wrapper;

        // model ship
        ship = new Vector2f[] {
                new Vector2f( 0.0325f, 0.0f ),
                new Vector2f( -0.0325f, -0.0325f ),
                new Vector2f( 0.0f, 0.0f ),
                new Vector2f( -0.0325f, 0.0325f ),
        };

        renderList = new ArrayList<>();
    }

    public PrototypeBullet launchBullet() {
        Vector2f pos = this.position.add(Vector2f.polar(angle, 0.0325f));
        return new PrototypeBullet(pos, angle);
    }

    public boolean isTouching(PrototypeAsteroid asteroid) {
        for (Vector2f[] poly : renderList) {
            for (Vector2f v  : poly) {
                 if (asteroid.contains(v)) return true;
            }
        }
        return false;
    }

    public void rotateLeft(float dt) {
        angle += rotationDelta * dt;
    }

    public void rotateRight(float dt) {
        angle -= rotationDelta * dt;
    }

    public void setThrust(boolean thrust) {
        currAcc = thrust ? acceleration : 0;
    }

    public boolean isDamaged() {
        return damaged;
    }

    public void setDamage(boolean damage) {
        damaged = damage;
    }

    public void update(float dt) {
        updatePosition(dt);
        Vector2f[] world = Utility.transform(ship, Matrix3x3f.rotate(angle).mul(Matrix3x3f.translate(position)));
        renderList.clear();
        renderList.add(world);
        wrapper.wrapPosition(position);
        wrapper.wrapPolygon(world, renderList);
    }

    private void updatePosition(float dt) {
        Vector2f accel = Vector2f.polar(angle, currAcc);
        velocity = velocity.add(accel.mul(dt));

        float scale = Math.min(maxVelocity / velocity.len(), 1);
        velocity = velocity.mul(scale);

        float maxSpeed = 1 - 0.25f * dt;
        velocity = velocity.mul(maxSpeed);

        position = position.add(velocity.mul(dt));
    }

    public void draw(Graphics2D g, Matrix3x3f view) {
        for (Vector2f[] ship : renderList) {
            Vector2f[] poly = Utility.transform(ship, view);
            g.setColor(Color.gray);
            Utility.drawPolygon(g, poly );
            g.setColor(Color.ORANGE);
            Utility.fillPolygon(g, poly );
        }
    }

}

package javagames.example.ch10;

import javagames.util.Vector2f;

public class FlyingSprite {
    public Vector2f position;
    public float angle;
    public Vector2f velocity;
    public float angularVelocity;

    public FlyingSprite(Vector2f position, float angle, Vector2f velocity, float angularVelocity) {
        this.position = position;
        this.angle = angle;
        this.velocity = velocity;
        this.angularVelocity = angularVelocity;
    }
}

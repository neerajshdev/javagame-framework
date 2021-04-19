package javagames.util;

public class Vector2f {

    public float x, y, w;

    @Override
    public String toString() {
        return String.format("%.2f, %.2f", x, y);
    }

    /**
     * Constructors
     */
    public Vector2f() {
        x = y = 0.0f;
        w = 1.0f;
    }
    public Vector2f(float x, float y) {
        this.x = x;
        this.y = y;
        this.w = 1.0f;
    }
    public Vector2f(Vector2f v) {
        this.x = v.x;
        this.y = v.y;
        this.w = v.w;
    }
    public Vector2f(float x, float y, float w) {
        this.x = x;
        this.y = y;
        this.w = w;
    }



    public float len() {
        return (float) Math.sqrt(x * x + y * y);
    }

    public float lenSqr() {
        return x * x + y * y;
    }

    public Vector2f sub(Vector2f v) {
        return new Vector2f(x - v.x, y - v.y);
    }

    public Vector2f add(Vector2f other) {
        return new Vector2f(x + other.x, y + other.y);
    }

    public Vector2f inv() {
        return new Vector2f(-x, -y);
    }

    public float dot(Vector2f other) {
        return x * other.x + y * other.y;
    }

    public Vector2f mul(float scalar) {
        return new Vector2f(x * scalar, y * scalar );
    }

    public Vector2f div(float scalar) {
        return new Vector2f(x / scalar, y / scalar );
    }

    public Vector2f norm() {
        return  div(len());
    }

    public Vector2f perp() {
        return new Vector2f(y, -x);
    }

    public float angle() {
        return (float) Math.atan2(y, x);
    }

    public static Vector2f polar(float angle, float r) {
        return new Vector2f(
                (r * (float) Math.cos(angle)),
                (r * (float) Math.sin(angle))
        );
    }

}

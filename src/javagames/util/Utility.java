package javagames.util;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.List;

public class Utility {

    // world to screen
    public static Matrix3x3f createViewport(float worldWidth, float worldHeight,
                                            float screenWidth, float screenHeight) {

        return Matrix3x3f.scale(screenWidth / worldWidth, -screenHeight / worldHeight)
                .mul(Matrix3x3f.translate(screenWidth / 2, screenHeight / 2));
    }

    // screen to world
    public static Matrix3x3f createReverseViewport(float worldWidth, float worldHeight,
                                                   float screenWidth, float screenHeight) {

        return Matrix3x3f.translate(-screenWidth / 2, -screenHeight / 2)
                .mul(Matrix3x3f.scale(worldWidth / screenWidth, - worldHeight / screenHeight));
    }

    public static void drawPolygon(Graphics2D g, Vector2f[] poly ) {
        Vector2f s = poly[poly.length - 1];
        for (int i = 0; i < poly.length; i++) {
            Vector2f p = poly[i];
            g.drawLine((int) s.x, (int) s.y, (int) p.x, (int) p.y);
            s = p;
        }
    }

    public static void drawPolygon(Graphics2D g, List<Vector2f> poly) {
        Vector2f s = poly.get(poly.size() - 1);
        for (int i = 0; i < poly.size(); i++) {
            Vector2f p = poly.get(i);
            g.drawLine((int) s.x, (int) s.y, (int) p.x, (int) p.y);
            s = p;
        }
    }

    // transform polygon with given matrix
    public static Vector2f[] transform(Vector2f[] poly, Matrix3x3f mat) {
        Vector2f[] transPoly = new Vector2f[poly.length];
        for (int i = 0 ; i < poly.length; i++) {
            transPoly[i] = mat.mul(poly[i]);
        }

        return transPoly;
    }


    public static void fillPolygon(Graphics2D g, Vector2f[] poly) {
        Polygon polygon = new Polygon();
        for (Vector2f point : poly) {
            polygon.addPoint((int) point.x, (int) point.y);
        }
        g.fillPolygon(polygon);
    }




    public static int drawString(Graphics g, int x, int y, List<String> strings) {
        return drawString(g, x, y, strings.toArray(new String[0]));
    }

    public static int drawString(Graphics g, int x, int y, String... strings) {
        FontMetrics fm = g.getFontMetrics();
        for (String str: strings) {
            g.drawString(str, x, y + fm.getAscent());
            y += fm.getHeight();
        }
        return y;
    }

    public static boolean pointInPolygon(Vector2f point, Vector2f[] poly, boolean winding) {
        return pointInPolygon(point, Arrays.asList(poly), winding);
    }
    public static boolean pointInPolygon(Vector2f point, List<Vector2f> poly, boolean winding) {
        int inside = 0;
        if (poly.size() > 2) {
            Vector2f start = poly.get(poly.size() - 1);
            boolean startAbove = start.y >= point.y;

            for(int i = 0; i < poly.size(); i ++) {
                Vector2f end = poly.get(i);
                boolean endAbove = end.y >= point.y;

                if (startAbove != endAbove) {
                    float m = (end.y - start.y) / (end.x - start.x);
                    float x = (point.y - start.y) / m + start.x;
                    if (x >= point.x) {
                        if (winding) {
                            inside += startAbove? 1 : -1;
                        } else {
                            inside = (inside == 0) ? 1 : 0;
                        }
                    }
                }

                start = end;
                startAbove = endAbove;
            }
        }
        return inside != 0;
    }


    /**
     *
     * @param start start point of the line
     * @param end end point of the line
     * @param rect 4 points of rect represent by Vector2f[]
     * @return
     * point of collision(Vector2f) if collision detected otherwise a null
     */
    public static Vector2f lineRectCollide(Vector2f start, Vector2f end, Vector2f[] rect) {
        if (start == null || end == null || rect == null) {
            throw  new RuntimeException(
                    "One parameter is null"
            );
        }
        if (rect.length != 4) {
            throw new NotARectangleException (
                    "Only the 4 points can form a rectangle"
            );
        }
        // Algorithm
        float largestMin = -Float.MAX_VALUE;
        float smallestMax = Float.MAX_VALUE;
        Vector2f d = end.sub(start);
        for (int i = 0; i < 2; i++) {
            Vector2f n = rect[i].sub(rect[i+1]);
            n = n.norm();
            float a0 = n.dot(rect[i].sub(start));
            float a1 = n.dot(rect[i+1].sub(start));
            float f = n.dot(d);
            if (Math.abs(f) > 0) {
                float t0 = a0/f;
                float t1 = a1/f;
                if (t0 > t1) {
                    float swap = t0;
                    t0 = t1;
                    t1 = swap;
                }
                largestMin = Math.max(largestMin, t0);
                smallestMax = Math.min(smallestMax, t1);
            } else if (a0 * a1 > 0) {
                return  null;
            }
        }
        if (largestMin > smallestMax) {
            // collision is not possible
            return null;
        } else if (largestMin >= 0) {
            if (largestMin <= 1) {
                return start.add(d.mul(largestMin));
            }
        } else if (smallestMax >= 0) {
            if (smallestMax <= 1) {
                return start.add(d.mul(smallestMax));
            }
        }
        return null;
    }


    /**
     *
     * @param center center of the circle
     * @param radius radius of the circle
     * @param start  start point of the line
     * @param end end point of the line
     * @return (Vector2f instance) point of collision if exists otherwise null
     */
    public static Vector2f lineCircleCollide(Vector2f center, float radius, Vector2f start, Vector2f end) {
        Vector2f d = end.sub(start);
        Vector2f oc = start.sub(center);
        float a = d.dot(d);
        float b = d.dot(oc);
        float c = oc.dot(oc) - radius * radius;
        float bb = b * b;
        float ac = a * c;
        if (bb < ac) {
            // because bb - ac will produce an imaginary number
            return null;
        }
        float root = (float) Math.sqrt(bb - ac);
        float n0 = (-b - root);
        if (n0 > 0 && n0 <= a) {
            float t = n0 / a;
            return start.add(d.mul(t));
        } else  {
            float n1 = (-b + root);
            if (n1 > 0 && n1 <= a) {
                float t = n1 / a;
                return start.add(d.mul(t));
            }
        }
        return null;
    }

    /**
     *
     * @param start1 start point of the line 1
     * @param end1 end point of the line 1
     * @param start2 start point of the line 2
     * @param end2 end point of the line 2
     * @return point of collision through line1
     * */
    public static Vector2f lineLineCollide(Vector2f start1, Vector2f end1, Vector2f start2, Vector2f end2) {
        Vector2f ab = end1.sub(start1);
        Vector2f cd = end2.sub(start2);
        Vector2f ca = start1.sub(start2);
        Vector2f abP = ab.perp();
        Vector2f cdP = cd.perp();
        float n1 = cdP.dot(ca);
        float n2 = abP.dot(ca);
        float d = abP.dot(cd);
        float t1 = n1 / d;
        float t2 = n2 / d;
        if ((t1 > 1 || t1 < 0) || (t2 > 1 || t2 < 0)) return null;
        return start1.add(ab.mul(t1));
    }

    /**
     *
     * @param center center of the circle
     * @param radius  radius of the circle
     * @param g  Component's Graphics object
     * @param view Transformation matrix (Matrix3x3)
     *
     * @apiNote draws the circle in screen space
     */
    public static void drawCircle(Vector2f center, float radius, Graphics g, Matrix3x3f view) {
        Vector2f topLeft = new Vector2f(center.x - radius, center.y + radius);
        topLeft = view.mul(topLeft);
        Vector2f bottomRight = new Vector2f(center.x + radius, center.y - radius);
        bottomRight = view.mul(bottomRight);
        int ovalX = (int) topLeft.x;
        int ovalY = (int) topLeft.y;
        int ovalW = (int) (bottomRight.x - topLeft.x);
        int ovalH = (int) (bottomRight.y - topLeft.y);
        g.drawOval(ovalX, ovalY, ovalW, ovalH);
    }


    /**
     * @param center Center of the circle
     * @param radius Radius of the circle
     * @param point Point under test
     * @return true if point is inside the circle otherwise false
     */
    public static boolean pointInCircle(Vector2f center, float radius, Vector2f point) {
        Vector2f d = point.sub(center);
        float len = d.lenSqr();
        radius = radius * radius;
        return len <= radius;
    }
}

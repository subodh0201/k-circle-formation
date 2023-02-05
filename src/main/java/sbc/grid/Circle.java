package sbc.grid;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Circle represents a circle on a grid defined
 * using Bresenham's circle drawing algorithm
 */
public class Circle {
    public final Point center;
    public final int radius;
    private final Set<Point> pointsOnCircle;

    public Circle(Point center, int radius) {
        this.center = center;
        this.radius = radius;
        this.pointsOnCircle = Collections.unmodifiableSet(findPointsOnCircle(center, radius));
    }

    public Set<Point> getPointsOnCircle() {
        return pointsOnCircle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Circle circle = (Circle) o;
        return radius == circle.radius && Objects.equals(center, circle.center);
    }

    @Override
    public int hashCode() {
        return Objects.hash(center, radius);
    }

    @Override
    public String toString() {
        return "{" + center +
                ", " + radius +
                "}";
    }

    public static Set<Point> findPointsOnCircle(Point center, int radius) {
        Set<Point> points = new HashSet<>();

        int y = radius;
        int x = 0;
        int delta = 3 - 2 * radius;

        while (y >= x) {
            addPointAndReflect(x, y, center, points);

            x++;
            if (delta < 0) {
                delta = delta + 4 * x + 6;
            } else {
                y--;
                delta = delta + 4 * (x - y) + 10;
            }
        }

        return points;
    }

    private static void addPointAndReflect(int x, int y, Point center, Set<Point> points) {
        points.add(new Point(center.x + x, center.y + y));
        points.add(new Point(center.x + x, center.y - y));
        points.add(new Point(center.x - x, center.y + y));
        points.add(new Point(center.x - x, center.y - y));

        points.add(new Point(center.x + y, center.y + x));
        points.add(new Point(center.x + y, center.y - x));
        points.add(new Point(center.x - y, center.y + x));
        points.add(new Point(center.x - y, center.y - x));
    }
}

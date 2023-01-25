package sbc.grid;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CircleTest {
    private static final Point center = new Point(0, 0);
    private static final int radius = 8;
    private static final int[][] points =
            {{0, 8},          {0, -8},
            {1, 8}, {-1, 8}, {-1, -8}, {1, -8},
            {2, 8}, {-2, 8}, {-2, -8}, {2, -8},
            {3, 7}, {-3, 7}, {-3, -7}, {3, -7},
            {4, 6}, {-4, 6}, {-4, -6}, {4, -6},
            {5, 5}, {-5, 5}, {-5, -5}, {5, -5},
            {6, 4}, {-6, 4}, {-6, -4}, {6, -4},
            {7, 3}, {-7, 3}, {-7, -3}, {7, -3},
            {8, 2}, {-8, 2}, {-8, -2}, {8, -2},
            {8, 1}, {-8, 1}, {-8, -1}, {8, -1},
            {8, 0}, {-8, 0},                 };

    private static final Circle circle = new Circle(center, radius);

    @Test
    void getPointsOnCircle() {
        assertEquals(circle.radius, radius);
        assertEquals(circle.center, center);

        Set<Point> pointsOnCircle = circle.getPointsOnCircle();
        assertEquals(pointsOnCircle.size(), points.length);
        for (int[] point : points) {
            assertTrue(pointsOnCircle.contains(new Point(point[0], point[1])));
        }
    }
}
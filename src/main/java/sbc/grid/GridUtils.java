package sbc.grid;

import sbc.grid.robot.Direction;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class GridUtils {
    public static final Point POINT_U = new Point(0, 1);
    public static final Point POINT_D = new Point(0, -1);
    public static final Point POINT_L = new Point(-1, 0);
    public static final Point POINT_R = new Point(1, 0);

    private GridUtils() {}

    public static Point move(Point point, Direction dir) {
        return point.add(directionToPoint(dir));
    }

    public static Point directionToPoint(Direction dir) {
        switch (dir) {
            case U: return POINT_U;
            case D: return POINT_D;
            case L: return POINT_L;
            case R: return POINT_R;
            default: return new Point(0, 0);    // Missing return statement without this?!
        }
    }

    public static Point centerOfMass(Iterable<Point> points) {
        int x = 0, y = 0, n = 0;
        for (Point point : points) {
            x += point.x;
            y += point.y;
            n++;
        }
        return n == 0 ? new Point(0, 0) :  new Point(x / n, y / n);
    }

    public static List<Point> shiftOrigin(List<Point> points, Point origin) {
        return points.stream().map(p -> p.subtract(origin)).collect(Collectors.toList());
    }

    public static<C> boolean unique(List<C> list) {
        return list.size() == new HashSet<>(list).size();
    }

    public static boolean overLappingCircles(Iterable<Circle> circles) {
        HashSet<Point> points = new HashSet<>();
        for (Circle circle : circles) {
            for (Point point : circle.getPointsOnCircle()) {
                if (!points.add(point))
                    return false;
            }
        }
        return true;
    }
}

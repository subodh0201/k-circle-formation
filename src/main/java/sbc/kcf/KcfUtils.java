package sbc.kcf;

import sbc.grid.Circle;
import sbc.grid.Point;
import sbc.grid.robot.Direction;
import sbc.grid.robot.Path;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class KcfUtils {
    private KcfUtils() {}

    public static Point inversePoint(Point point) {
        return point == null ? null : new Point(-point.x, point.y);
    }

    public static List<Point> inversePointList(List<Point> points) {
        return points == null ? null :
                points.stream().map(KcfUtils::inversePoint).collect(Collectors.toList());
    }

    public static Circle inverseCircle(Circle circle) {
        return  circle == null ? null : new Circle(inversePoint(circle.center), circle.radius);
    }

    public static List<Circle> inverseCircleList(List<Circle> circles) {
        return circles == null ? null : circles.stream().map(KcfUtils::inverseCircle).collect(Collectors.toList());
    }

    public static Direction inverseDirection(Direction direction) {
        if (direction == Direction.L) return Direction.R;
        if (direction == Direction.R) return Direction.L;
        return direction;
    }

    public static List<Direction> inverseDirectionList(List<Direction> directionList) {
        return directionList == null ? null :
                directionList.stream().map(KcfUtils::inverseDirection).collect(Collectors.toList());
    }

    public static Path inversePath(Path path) {
        return path == null ? null :
                new Path(inversePoint(path.getStart()), inverseDirectionList(path.getDirectionList()));
    }

    public static List<Boolean> getRandomBooleanList(int size) {
        List<Boolean> list = new ArrayList<>();
        while (size-- > 0)
            list.add(Math.random() < 0.5);
        return list;
    }
}

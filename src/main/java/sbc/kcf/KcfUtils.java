package sbc.kcf;

import sbc.grid.Point;
import sbc.grid.robot.Direction;
import sbc.grid.robot.Path;

import java.util.List;
import java.util.stream.Collectors;

public class KcfUtils {
    private KcfUtils() {}

    public static Point inversePoint(Point point) {
        return point == null ? null : new Point(-point.x, point.y);
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
}

package sbc.kcf;

import sbc.grid.Point;
import sbc.grid.robot.Direction;
import sbc.grid.robot.Path;

import java.util.List;
import java.util.stream.Collectors;

public class KcfUtils {
    private KcfUtils() {}

    public static Point inverse(Point point) {
        return point == null ? null : new Point(-point.x, point.y);
    }

    public static Direction inverse(Direction direction) {
        if (direction == Direction.L) return Direction.R;
        if (direction == Direction.R) return Direction.L;
        return direction;
    }

    public static List<Direction> inverse(List<Direction> directionList) {
        return directionList.stream().map(KcfUtils::inverse).collect(Collectors.toList());
    }

    public static Path inverse(Path path) {
        return new Path(inverse(path.getStart()), inverse(path.getDirectionList()));
    }
}

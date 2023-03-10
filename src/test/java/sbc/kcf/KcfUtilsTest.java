package sbc.kcf;

import org.junit.jupiter.api.Test;
import sbc.grid.Point;
import sbc.grid.robot.Direction;
import sbc.grid.robot.Path;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class KcfUtilsTest {

    private final Point start = new Point(7, 3);

    private final List<Direction> directionList = List.of(
            Direction.U, Direction.U, Direction.U, Direction.D, Direction.R, Direction.U, Direction.D,
            Direction.R, Direction.R, Direction.L, Direction.U, Direction.U, Direction.L, Direction.L
    );

    private final Path path = new Path(start, directionList);

    private final List<Point> pointList = List.of(
            new Point(5, 7), new Point(0, 0), new Point(-3, 4), new Point(0, 1), new Point(0, -10),
            new Point(-5, -7), new Point(21310, 110), new Point(-332131, 23124), new Point(123, 2132131)
    );

    @Test
    void testInversePoint() {
        Point point = new Point(5, 6);
        Point inversePoint = KcfUtils.inversePoint(point);
        assertEquals(-point.x, inversePoint.x);
        assertEquals(point.y, inversePoint.y);

    }

    @Test
    void inversePointList() {
        assertNull(KcfUtils.inversePointList(null));
        List<Point> invPointList = KcfUtils.inversePointList(pointList);
        assertEquals(pointList.size(), invPointList.size());
        for (int i = 0; i < pointList.size(); i++) {
            assertEquals(-pointList.get(i).x, invPointList.get(i).x);
            assertEquals(pointList.get(i).y, invPointList.get(i).y);
        }
    }

    @Test
    void inverseCircle() {
    }

    @Test
    void inverseCircleList() {
    }

    @Test
    void testInverseDirection() {
        assertNull(KcfUtils.inverseDirection(null));
        assertEquals(Direction.U, KcfUtils.inverseDirection(Direction.U));
        assertEquals(Direction.D, KcfUtils.inverseDirection(Direction.D));
        assertEquals(Direction.L, KcfUtils.inverseDirection(Direction.R));
        assertEquals(Direction.R, KcfUtils.inverseDirection(Direction.L));
    }

    @Test
    void testInverseDirectionList() {
        assertNull(KcfUtils.inverseDirectionList(null));
        List<Direction> invDirectionList = KcfUtils.inverseDirectionList(directionList);
        assertEquals(directionList, invDirectionList.stream().map(KcfUtils::inverseDirection)
                .collect(Collectors.toList()));
    }

    @Test
    void testInversePath() {
        assertNull(KcfUtils.inversePath(null));
        Path invPath = KcfUtils.inversePath(path);
        assertEquals(KcfUtils.inversePoint(path.getStart()), invPath.getStart());
        assertEquals(path.getDirectionList(), invPath.getDirectionList().stream().map(KcfUtils::inverseDirection)
                .collect(Collectors.toList()));
    }
}
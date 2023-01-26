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

    Path path = new Path(start, directionList);

    @Test
    void testInversePoint() {
        assertNull(KcfUtils.inversePoint(null));
        Point point = new Point(5, 6);
        Point inversePoint = KcfUtils.inversePoint(point);
        assertEquals(-point.x, inversePoint.x);
        assertEquals(point.y, inversePoint.y);

    }

    @Test
    void testInverseDirectionTest() {
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
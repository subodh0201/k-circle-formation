package sbc.grid.robot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sbc.grid.GridUtils;
import sbc.grid.Point;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RobotTest {
    private static final Point START = new Point(6, 3);
    private static final List<Direction> directionList = List.of(
            Direction.L, Direction.L, Direction.L,
            Direction.U,
            Direction.R,
            Direction.D, Direction.D, Direction.D, Direction.D,
            Direction.L
    );


    private Robot<List<Direction>> robot;

    @BeforeEach
    void beforeEach() {
        robot = new Robot<>(START, RobotTest::compute);
    }

    @Test
    void LCM() {
        Point position = START;

        assertEquals(position, robot.getPosition());
        assertFalse(robot.canMove());
        assertFalse(robot.move());
        assertEquals(position, robot.getPosition());

        robot.lookAndCompute(directionList);
        for (Direction dir : directionList) {
            assertTrue(robot.canMove());
            assertTrue(robot.move());
            position = GridUtils.move(position, dir);
            assertEquals(position, robot.getPosition());
        }

        assertFalse(robot.canMove());
        assertFalse(robot.move());
        assertEquals(position, robot.getPosition());

        robot.lookAndCompute(directionList);
        for (Direction dir : directionList) {
            assertTrue(robot.canMove());
            assertTrue(robot.move());
            position = GridUtils.move(position, dir);
            assertEquals(position, robot.getPosition());
        }


    }

    @Test
    void getCurrentPath() {
        robot.lookAndCompute(directionList);
        Path path = robot.getCurrentPath();
        assertEquals(START, path.getStart());
        assertEquals(directionList, path.getDirectionList());
    }

    private static List<Direction> compute(List<Direction> directionList) {
        return directionList;
    }
}
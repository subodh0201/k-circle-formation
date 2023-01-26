package sbc.grid.robot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sbc.grid.GridUtils;
import sbc.grid.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class RobotTest {

    private final Point start = new Point(7, 3);

    private final List<Direction> directionList = List.of(
            Direction.U, Direction.U, Direction.U, Direction.D, Direction.R, Direction.U, Direction.D,
            Direction.R, Direction.R, Direction.L, Direction.U, Direction.U, Direction.L, Direction.L
    );

    private final Algorithm<List<Direction>, List<Direction>> algorithm = arg -> arg;

    private Robot<List<Direction>> robot;

    @BeforeEach
    void beforeEach() {
        robot = new Robot<>(start, algorithm);
    }

    @Test
    void getPosition() {
        Point position = start;
        assertEquals(position, robot.getPosition());

        robot.lookAndCompute(directionList);
        assertEquals(position, robot.getPosition());
        while (robot.canMove()) {
            position = GridUtils.move(position, robot.getNextMove());
            robot.move();
            assertEquals(position, robot.getPosition());
        }
        robot.move();
        assertEquals(position, robot.getPosition());

        robot.lookAndCompute(directionList);
        assertEquals(position, robot.getPosition());
        while (robot.canMove()) {
            position = GridUtils.move(position, robot.getNextMove());
            robot.move();
            assertEquals(position, robot.getPosition());
        }
        robot.move();
        assertEquals(position, robot.getPosition());
    }

    @Test
    void getAlgorithm() {
        assertEquals(algorithm, robot.getAlgorithm());
    }

    @Test
    void getCurrentPath() {
        assertNull(robot.getCurrentPath());

        Path path = new Path(robot.getPosition(), directionList);
        robot.lookAndCompute(directionList);
        assertEquals(path, robot.getCurrentPath());

        path = new Path(robot.getPosition(), directionList);
        robot.lookAndCompute(directionList);
        assertEquals(path, robot.getCurrentPath());
    }

    @Test
    void lookAndCompute() {
        Path path = new Path(robot.getPosition(), directionList);
        assertEquals(path, robot.lookAndCompute(directionList));
    }

    @Test
    void canMove() {
        assertFalse(robot.canMove());
        robot.lookAndCompute(directionList);
        assertTrue(robot.canMove());
        robot.lookAndCompute(new ArrayList<>());
        assertFalse(robot.canMove());
    }

    @Test
    void move() {
        assertFalse(robot.move());
        robot.lookAndCompute(directionList);
        while (robot.canMove()) {
            assertTrue(robot.move());
        }
        assertFalse(robot.move());
    }

    @Test
    void getPathIteratorIndex() {
        assertEquals(-1, robot.getPathIteratorIndex());
        robot.lookAndCompute(directionList);
        int index = 0;
        while (robot.canMove()) {
            assertEquals(index, robot.getPathIteratorIndex());
            robot.move();
            index++;
        }
        assertEquals(index, robot.getPathIteratorIndex());
    }


    @Test
    void getNextMove() {
        assertThrows(NoSuchElementException.class, () -> robot.getNextMove());
        robot.lookAndCompute(directionList);
        for (Direction direction : directionList) {
            assertEquals(direction, robot.getNextMove());
            robot.move();
        }
        assertThrows(NoSuchElementException.class, () -> robot.getNextMove());
    }
}
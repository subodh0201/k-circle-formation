package sbc.grid;

import org.junit.jupiter.api.Test;
import sbc.grid.robot.Direction;

import static org.junit.jupiter.api.Assertions.*;

class GridUtilsTest {

    @Test
    void move() {
        Point point = new Point(2, 3);
        Point u = new Point(2, 4);
        Point d = new Point(2, 2);
        Point l = new Point(1, 3);
        Point r = new Point(3, 3);
        assertEquals(u, GridUtils.move(point, Direction.U));
        assertEquals(d, GridUtils.move(point, Direction.D));
        assertEquals(l, GridUtils.move(point, Direction.L));
        assertEquals(r, GridUtils.move(point, Direction.R));
    }

    @Test
    void directionToPoint() {
        assertEquals(new Point(0, 1), GridUtils.directionToPoint(Direction.U));
        assertEquals(new Point(0, -1), GridUtils.directionToPoint(Direction.D));
        assertEquals(new Point(-1, 0), GridUtils.directionToPoint(Direction.L));
        assertEquals(new Point(1, 0), GridUtils.directionToPoint(Direction.R));

    }
}
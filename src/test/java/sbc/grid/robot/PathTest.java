package sbc.grid.robot;

import org.junit.jupiter.api.Test;
import sbc.grid.Point;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class PathTest {

    private final Point start = new Point(7, 3);

    private final List<Direction> directionList = List.of(
        Direction.U, Direction.U, Direction.U, Direction.D, Direction.R, Direction.U, Direction.D,
        Direction.R, Direction.R, Direction.L, Direction.U, Direction.U, Direction.L, Direction.L
    );

    private final Path path = new Path(start, directionList);

    @Test
    void getStart() {
        assertEquals(start, path.getStart());
    }

    @Test
    void getDirectionList() {
        assertEquals(directionList, path.getDirectionList());
    }

    @Test
    void pathIterator() {
        Path.PathIterator pathIterator = path.pathIterator();
        for (int i = 0; i < directionList.size(); i++) {
            assertEquals(i, pathIterator.getIndex());
            assertTrue(pathIterator.hasNext());
            assertEquals(directionList.get(i), pathIterator.peekNext());
            assertEquals(directionList.get(i), pathIterator.next());
        }
        assertEquals(directionList.size(), pathIterator.getIndex());
        assertFalse(pathIterator.hasNext());
        assertThrows(NoSuchElementException.class, pathIterator::next);
    }
}
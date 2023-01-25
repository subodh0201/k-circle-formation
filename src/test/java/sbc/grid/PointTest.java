package sbc.grid;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PointTest {

    @Test
    void add() {
        Point a = new Point(5, 7);
        Point b = new Point(9, 8);

        Point sum = a.add(b);
        assertEquals(sum.x, 5+9);
        assertEquals(sum.y, 7+8);

        assertEquals(sum, b.add(a));
    }


    @Test
    void subtract() {
        Point a = new Point(5, 7);
        Point b = new Point(9, 8);

        Point diff = a.subtract(b);
        assertEquals(diff.x, 5-9);
        assertEquals(diff.y, 7-8);
    }

}
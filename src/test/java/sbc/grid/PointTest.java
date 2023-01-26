package sbc.grid;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PointTest {

    @Test
    void add() {
        Point a = new Point(5, 7);
        Point b = new Point(9, 8);

        Point sum = a.add(b);
        assertEquals(5+9, sum.x);
        assertEquals(7+8, sum.y);

        assertEquals(sum, b.add(a));
    }


    @Test
    void subtract() {
        Point a = new Point(5, 7);
        Point b = new Point(9, 8);

        Point diff = a.subtract(b);
        assertEquals(5-9, diff.x);
        assertEquals(7-8, diff.y);
    }

}
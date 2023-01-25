package sbc.grid;

import sbc.grid.robot.Direction;

public class GridUtils {
    public static final Point POINT_U = new Point(0, 1);
    public static final Point POINT_D = new Point(0, -1);
    public static final Point POINT_L = new Point(-1, 0);
    public static final Point POINT_R = new Point(1, 0);

    private GridUtils() {}

    public static Point move(Point point, Direction dir) {
        return point.add(directionToPoint(dir));
    }

    public static Point directionToPoint(Direction dir) {
        switch (dir) {
            case U: return POINT_U;
            case D: return POINT_D;
            case L: return POINT_L;
            case R: return POINT_R;
            default: return new Point(0, 0);    // Missing return statement without this?!
        }
    }
}

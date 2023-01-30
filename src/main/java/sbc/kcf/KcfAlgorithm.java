package sbc.kcf;

import sbc.grid.robot.Direction;

import java.util.Collections;
import java.util.List;

public class KcfAlgorithm {

    private static final List<Direction> directionList = List.of(
            Direction.U, Direction.U, Direction.U, Direction.U, Direction.U,
            Direction.L, Direction.L, Direction.L, Direction.L, Direction.L,
            Direction.D, Direction.D, Direction.D, Direction.D, Direction.D,
            Direction.R, Direction.R, Direction.R, Direction.R, Direction.R
    );

    public static List<Direction> ULDR(KcfConfig config) {
        return Math.random() < 0.5 ? directionList : Collections.emptyList();
    }

}

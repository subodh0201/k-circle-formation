package sbc.kcf;

import sbc.grid.Point;
import sbc.grid.robot.Algorithm;
import sbc.grid.robot.Direction;
import sbc.grid.robot.Path;
import sbc.grid.robot.Robot;

import java.util.List;

public class KcfRobot<C extends Invertible<C>> {
    private final Robot<C> robot;
    private final boolean xAxisAlignment;

    public KcfRobot(Point position, Algorithm<List<Direction>, C> algorithm, boolean xAxisAlignment) {
        this.xAxisAlignment = xAxisAlignment;
        this.robot = new Robot<>(transform(position), algorithm);
    }

    public Robot<C> getRobot() {
        return robot;
    }

    public boolean isxAxisAlignment() {
        return xAxisAlignment;
    }

    public Point getPosition() {
        return transform(robot.getPosition());
    }

    public Algorithm<List<Direction>, C> getAlgorithm() {
        return robot.getAlgorithm();
    }

    public Path getCurrentPath() {
        return transform(robot.getCurrentPath());
    }

    public Path lookAndCompute(C config) {
        return transform(robot.lookAndCompute(transform(config)));
    }

    public boolean canMove() {
        return robot.canMove();
    }

    public boolean move() {
        return robot.move();
    }

    public Direction getNextMove() {
        return transform(robot.getNextMove());
    }

    public int getPathIteratorIndex() {
        return robot.getPathIteratorIndex();
    }

    public Point transform(Point point) {
        return xAxisAlignment ? point : KcfUtils.inversePoint(point);
    }

    public Direction transform(Direction direction) {
        return xAxisAlignment ? direction : KcfUtils.inverseDirection(direction);
    }

    public Path transform(Path path) {
        return xAxisAlignment ? path : KcfUtils.inversePath(path);
    }

    public C transform(C config) {
        return xAxisAlignment ? config : config.inverse();
    }

    @Override
    public String toString() {
        return "KcfRobot{" + robot +
                ", " + xAxisAlignment +
                '}';
    }
}

package sbc.kcf;

import sbc.grid.Circle;
import sbc.grid.Point;

import java.util.Collections;
import java.util.List;

public class KcfConfig implements Invertible<KcfConfig> {

    private final List<Point> robots;
    private final List<Circle> circles;
    private final Point position;
    private final KcfConfig inverse;

    public KcfConfig(List<Point> robots, List<Circle> circles, Point position) {
        this.robots = Collections.unmodifiableList(robots);
        this.circles = Collections.unmodifiableList(circles);
        this.position = position;
        this.inverse = new KcfConfig(KcfUtils.inversePointList(robots),
                KcfUtils.inverseCircleList(circles), KcfUtils.inversePoint(position), this);
    }

    public KcfConfig(List<Point> robots, List<Circle> circles, Point position, KcfConfig inverse) {
        this.robots = Collections.unmodifiableList(robots);
        this.circles = Collections.unmodifiableList(circles);
        this.position = position;
        this.inverse = inverse;
    }


    @Override
    public KcfConfig inverse() {
        return inverse;
    }

    public List<Point> getRobots() {
        return robots;
    }

    public List<Circle> getCircles() {
        return circles;
    }

    public Point getPosition() {
        return position;
    }
}

package sbc.kcf;

import sbc.grid.Circle;
import sbc.grid.Point;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class KcfCircle {
    private final Circle circle;
    private final Set<Point> robotsOnCircle;
    private final int saturation;

    public KcfCircle(Circle circle, List<Point> R, int k) {
        this.circle = circle;
        HashSet<Point> robots = new HashSet<>();
        for (Point r : R) {
            if (circle.getPointsOnCircle().contains(r))
                robots.add(r);
        }
        robotsOnCircle = Set.copyOf(robots);
        this.saturation = robotsOnCircle.size() - k;
    }

    public Circle getCircle() {
        return circle;
    }

    public Set<Point> getRobotsOnCircle() {
        return robotsOnCircle;
    }

    public int getSaturation() {
        return saturation;
    }
}

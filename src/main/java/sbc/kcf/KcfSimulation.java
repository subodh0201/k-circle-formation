package sbc.kcf;

import sbc.grid.Circle;
import sbc.grid.GridUtils;
import sbc.grid.Point;
import sbc.grid.robot.Algorithm;
import sbc.grid.robot.Direction;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class KcfSimulation {
    private final List<KcfRobot<KcfConfig>> robots;
    private final int n;
    private final List<Circle> circles;
    private final List<Circle> invCircles;
    private final int m;
    private final int k;
    private KcfState state;
    private int round;
    private KcfPhase phase;
    private final Point origin;
    private List<Point> robotPositions;
    private List<Point> invRobotPositions;


    public KcfSimulation(
            List<Point> robotPositions, Algorithm<List<Direction>, KcfConfig> algorithm,
            List<Point> centers, int radius
    ) {
        // check nulls
        if (robotPositions == null) throw new IllegalArgumentException("robotPositions is null");
        if (centers == null) throw new IllegalArgumentException("centers is null");
        if (algorithm == null) throw new IllegalArgumentException("algorithm is null");

        // check radius
        if (radius < 1) throw new IllegalArgumentException("radius < 1");

        n = robotPositions.size();
        m = centers.size();

        // check n and m > 0
        if (n == 0) throw new IllegalArgumentException("n is 0");
        if (m == 0) throw new IllegalArgumentException("m is 0");

        k = n / m;

        // is n divisible by m?
        if (n % m != 0) throw new IllegalArgumentException("n is not divisible by m");

        // are circles big enough?
        if (new Circle(new Point(0, 0), radius).getPointsOnCircle().size() < k)
            throw new IllegalArgumentException("circles cannot fit k robots");

        // check robots and centers are unique
        if (!GridUtils.unique(robotPositions))
            throw new IllegalArgumentException("robot positions not unique");
        if (!GridUtils.unique(centers))
            throw new IllegalArgumentException("centers not unique");

        // shift origin to center of mass of centers
        origin = GridUtils.centerOfMass(centers);
        robotPositions = robotPositions.stream().map(p -> p.subtract(origin)).collect(Collectors.toUnmodifiableList());
        centers = centers.stream().map(p -> p.subtract(origin)).collect(Collectors.toUnmodifiableList());

        this.robotPositions = robotPositions;
        this.invRobotPositions = Collections.unmodifiableList(KcfUtils.inversePointList(this.robotPositions));

        // create circle list
        circles = centers.stream().map(c -> new Circle(c, radius)).collect(Collectors.toUnmodifiableList());
        invCircles = Collections.unmodifiableList(KcfUtils.inverseCircleList(circles));

        // check overlapping circles
        if (!GridUtils.overLappingCircles(circles))
            throw new IllegalArgumentException("Overlapping circles");

        // create robot list
        robots = robotPositions.stream().map(p -> new KcfRobot<>(p, algorithm, Math.random() < 0.5))
                .collect(Collectors.toUnmodifiableList());

        round = 0;
        phase = KcfPhase.LOOK;
        state = error() ? KcfState.ERROR : unsolvable() ? KcfState.UNSOLVABLE
                : solved() ? KcfState.SOLVED : KcfState.SOLVING;
    }

    public List<KcfRobot<KcfConfig>> getRobots() {
        return robots;
    }

    public int getN() {
        return n;
    }

    public List<Circle> getCircles() {
        return circles;
    }

    public int getM() {
        return m;
    }

    public int getK() {
        return k;
    }

    public KcfState getState() {
        return state;
    }

    public int getRound() {
        return round;
    }

    public KcfPhase getPhase() {
        return phase;
    }

    public Point getOrigin() {
        return origin;
    }

    public List<Point> getRobotPositions() {
        return robotPositions;
    }

    public List<Circle> getInvCircles() {
        return invCircles;
    }

    public List<Point> getInvRobotPositions() {
        return invRobotPositions;
    }

    public KcfConfig getCurrentConfig(Point position) {
        return new KcfConfig(robotPositions, invRobotPositions, circles, invCircles, position);
    }

    public boolean step() {
        if (this.state == KcfState.SOLVING) {
            switch (this.phase) {
                case LOOK: look(); break;
                case COMPUTE: compute(); break;
                case MOVE: move(); break;
                default: throw new IllegalStateException("Phase null");
            }
            return true;
        }
        return false;
    }

    private void look() {
        updateRobotPositions();
        state = error() ? KcfState.ERROR : solved() ? KcfState.SOLVED : KcfState.SOLVING;
        if (state == KcfState.SOLVING)
            phase = KcfPhase.COMPUTE;
    }

    private void compute() {
        for (KcfRobot<KcfConfig> robot : robots) {
            robot.lookAndCompute(getCurrentConfig(robot.getPosition()));
        }
        this.phase = KcfPhase.MOVE;
    }

    private void move() {
        boolean movable = false;
        for (KcfRobot<KcfConfig> robot : robots) {
            if (robot.canMove()) {
                movable = true;
                break;
            }
        }
        if (!movable) {
            this.round++;
            this.phase = KcfPhase.LOOK;
        } else  {
            for (KcfRobot<KcfConfig> robot : robots) {
                if (robot.canMove())
                    robot.move();
            }
            updateRobotPositions();
        }
    }

    private void updateRobotPositions() {
        this.robotPositions = robots.stream().map(KcfRobot::getPosition).collect(Collectors.toUnmodifiableList());
        this.invRobotPositions = Collections.unmodifiableList(KcfUtils.inversePointList(this.robotPositions));
    }

    public boolean unsolvable() {
        return false;
    }

    public boolean solved() {
        if (!GridUtils.unique(robotPositions)) return false;
        int[] counts = new int[m];
        for (KcfRobot<KcfConfig> robot : robots) {
            for (int i = 0; i < m; i++) {
                if (circles.get(i).getPointsOnCircle().contains(robot.getPosition())) {
                    counts[i]++;
                    break;
                }
            }
        }
        for (int count : counts)
            if (count != k) return false;
        return true;
    }

    public boolean error() {
        return !GridUtils.unique(robotPositions);
    }
}

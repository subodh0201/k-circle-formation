package sbc.kcf;

import sbc.grid.Circle;
import sbc.grid.GridUtils;
import sbc.grid.Point;
import sbc.grid.robot.Algorithm;
import sbc.grid.robot.Direction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class KcfSimulation {
    private final KcfSetup kcfSetup;

    private final List<KcfRobot<KcfConfig>> robots;
    private List<Point> robotPositions;
    private List<Point> invRobotPositions;

    private final List<Circle> circles;
    private final List<Circle> invCircles;

    private KcfState state;
    private int round;
    private KcfPhase phase;

    public KcfSimulation(KcfSetup setup, Algorithm<List<Direction>, KcfConfig> algorithm) {
        this.kcfSetup = setup;
        this.robotPositions = kcfSetup.getRobotPositions();
        this.invRobotPositions = Collections.unmodifiableList(KcfUtils.inversePointList(this.robotPositions));
        this.circles = kcfSetup.getCircles();
        this.invCircles = Collections.unmodifiableList(KcfUtils.inverseCircleList(this.circles));

        // create robot list
        List<KcfRobot<KcfConfig>> robotList = new ArrayList<>();
        for (int i = 0; i < robotPositions.size(); i++) {
            robotList.add(new KcfRobot<>(robotPositions.get(i), algorithm, kcfSetup.getXAxisAlignments().get(i)));
        }
        robots = Collections.unmodifiableList(robotList);

        round = 0;
        phase = KcfPhase.LOOK;
        state = error() ? KcfState.ERROR : unsolvable() ? KcfState.UNSOLVABLE
                : solved() ? KcfState.SOLVED : KcfState.SOLVING;
    }

    public KcfSetup getKcfSetup() {
        return kcfSetup;
    }

    public List<KcfRobot<KcfConfig>> getRobots() {
        return robots;
    }

    public List<Point> getRobotPositions() {
        return robotPositions;
    }

    public List<Point> getInvRobotPositions() {
        return invRobotPositions;
    }

    public List<Circle> getCircles() {
        return circles;
    }

    public List<Circle> getInvCircles() {
        return invCircles;
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

    private KcfConfig getCurrentConfig(Point position) {
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
            if (error()) state = KcfState.ERROR;
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
        int[] counts = new int[kcfSetup.getM()];
        for (KcfRobot<KcfConfig> robot : robots) {
            for (int i = 0; i < kcfSetup.getM(); i++) {
                if (circles.get(i).getPointsOnCircle().contains(robot.getPosition())) {
                    counts[i]++;
                    break;
                }
            }
        }
        for (int count : counts)
            if (count != kcfSetup.getK()) return false;
        return true;
    }

    public boolean error() {
        return !GridUtils.unique(robotPositions);
    }
}

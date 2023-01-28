package sbc.grid.robot;

import sbc.grid.GridUtils;
import sbc.grid.Point;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Robot represents a robot on a 2D grid that follows a look-compute-move
 * (LCM) cycle.
 * - Look corresponds to sensing the configuration of the environment.
 *   Type param C represents the configuration data type.
 * - Compute corresponds to computing a path the robot will take in the
 *   move phase. This is done using an algorithm.
 *   The algorithm receives configuration C as argument and returns a list
 *   of direction.
 * - Move corresponds to moving along the path set during the compute phase
 * Look and compute is executed in a single call to the method
 * lookAndCompute(C config). This methods sets up the path that the robot
 * will take and calls to move, moves the robot along the path.
 * Once the robot reaches the destination calls to move  don't move the robot
 * and returns false.
 * @param <C> Data type that the algorithm accepts as argument for compute.
 */
public class Robot<C> {
    private Point position;
    private final Algorithm<List<Direction>, C> algorithm;
    private Path currentPath;
    private Path.PathIterator currentPathIterator;

    public Robot(Point position, Algorithm<List<Direction>, C> algorithm) {
        this.position = position;
        this.algorithm = algorithm;
    }

    public Point getPosition() {
        return position;
    }

    public Algorithm<List<Direction>, C> getAlgorithm() {
        return algorithm;
    }

    public Path getCurrentPath() {
        return currentPath;
    }

    public Path lookAndCompute(C config) {
        currentPath = new Path(position, algorithm.compute(config));
        currentPathIterator = currentPath.pathIterator();
        return currentPath;
    }

    public boolean canMove() {
        return currentPathIterator != null && currentPathIterator.hasNext();
    }

    public boolean move() {
        if (!canMove()) return false;
        move(currentPathIterator.next());
        return true;
    }

    private void move(Direction dir) {
        position = GridUtils.move(position, dir);
    }

    public Direction getNextMove() {
        if (!canMove())
            throw new NoSuchElementException("no move found");
        return currentPathIterator.peekNext();
    }

    public int getPathIteratorIndex() {
        return currentPathIterator == null ? -1 : currentPathIterator.getIndex();
    }

    @Override
    public String toString() {
        return "Robot{" + position +
                ", " + currentPath +
                ", " + (currentPathIterator == null? "null" : currentPathIterator.getIndex()) +
                '}';
    }
}

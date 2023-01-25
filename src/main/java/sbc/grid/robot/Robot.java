package sbc.grid.robot;

import sbc.grid.GridUtils;
import sbc.grid.Point;

import java.util.List;

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

    public void lookAndCompute(C config) {
        currentPath = new Path(position, algorithm.compute(config));
        currentPathIterator = currentPath.pathIterator();
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

    private int getPathIteratorIndex() {
        return currentPathIterator == null ? -1 : currentPathIterator.getIndex();
    }
}

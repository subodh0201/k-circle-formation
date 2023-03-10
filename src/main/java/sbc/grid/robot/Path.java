package sbc.grid.robot;

import sbc.grid.GridUtils;
import sbc.grid.Point;

import java.util.*;

public class Path implements Iterable<Direction> {
    private final Point start;
    private final List<Direction> directionList;
    private final List<Point> pointList;

    public Path(Point start, List<Direction> directionList) {
        this.start = start;
        this.directionList = Collections.unmodifiableList(directionList);

        List<Point> points = new ArrayList<>();
        points.add(start);
        for (Direction d : directionList) {
            points.add(GridUtils.move(points.get(points.size() - 1), d));
        }
        this.pointList = Collections.unmodifiableList(points);

    }

    public Point getStart() {
        return start;
    }

    public List<Direction> getDirectionList() {
        return directionList;
    }

    public List<Point> getPointList() {
        return pointList;
    }

    @Override
    public Iterator<Direction> iterator() {
        return new PathIterator();
    }

    public PathIterator pathIterator() {
        return new PathIterator();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Path path = (Path) o;
        return Objects.equals(start, path.start) && Objects.equals(directionList, path.directionList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, directionList);
    }

    @Override
    public String toString() {
        return "Path{" + start +
                ", " + directionList +
                '}';
    }

    public class PathIterator implements Iterator<Direction> {

        private int index = 0;

        @Override
        public boolean hasNext() {
            return index < directionList.size();
        }

        @Override
        public Direction next() {
            if (!hasNext())
                throw new NoSuchElementException("nothing left");
            return directionList.get(index++);
        }

        public int getIndex() {
            return index;
        }

        public Direction peekNext() {
            if (!hasNext())
                throw new NoSuchElementException("nothing left");
            return directionList.get(index);
        }
    }
}

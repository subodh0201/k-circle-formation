package sbc.grid.robot;

import sbc.grid.Point;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class Path implements Iterable<Direction> {
    private final Point start;
    private final List<Direction> directionList;

    public Path(Point start, List<Direction> directionList) {
        this.start = start;
        this.directionList = Collections.unmodifiableList(directionList);
    }

    public Point getStart() {
        return start;
    }

    public List<Direction> getDirectionList() {
        return directionList;
    }

    @Override
    public Iterator<Direction> iterator() {
        return new PathIterator();
    }

    public PathIterator pathIterator() {
        return new PathIterator();
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
    }
}

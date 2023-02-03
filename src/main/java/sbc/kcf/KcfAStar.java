package sbc.kcf;

import sbc.grid.GridUtils;
import sbc.grid.Point;
import sbc.grid.robot.Direction;

import java.util.*;


public class KcfAStar {
    private static final int[][] nbrs = {{1,0}, {0,-1}, {-1,0}, {0, 1}};


    private final Point dest;
    private List<Direction> directionList = null;

    public KcfAStar(Point start, Point destination, KcfConfig config) {
        this.dest = destination;
        Set<Point> obstacles = new HashSet<>(config.getRobots());
        Set<Point> visited = new HashSet<>();
        obstacles.remove(start);
        if (obstacles.contains(destination)) {
            throw new IllegalStateException("Destination " + destination + " blocked by a robot");
        }
        PriorityQueue<Node> pq = new PriorityQueue<>();
        pq.add(new Node(start, null));
        while (!pq.isEmpty()) {
            Node n = pq.remove();
            if (visited.contains(n.point)) continue;
            visited.add(n.point);
            if (n.point.equals(destination)) {
                extractPath(n);
                break;
            }
            for (int[] nbr : nbrs) {
                Point p = new Point(n.point.x + nbr[0], n.point.y + nbr[1]);
                if (obstacles.contains(p) || visited.contains(p)) continue;
                pq.add(new Node(p, n));
            }
        }
    }

    private void extractPath(Node n) {
        directionList = new ArrayList<>();
        List<Point> points = new ArrayList<>();
        while (n != null) {
            points.add(n.point);
            n = n.parent;
        }
        for (int i = points.size() - 2; i >= 0; i--) {
            directionList.add(GridUtils.pointToDirection(points.get(i).subtract(points.get(i+1))));
        }
        directionList = Collections.unmodifiableList(directionList);
    }

    public List<Direction> getDirectionList() {
        return directionList;
    }

    private class Node implements Comparable<Node> {
        Point point;
        int distance;
        Node parent;

        Node(Point point, Node parent) {
            this.point = point;
            distance = 1 + (parent == null ? -1 : parent.distance);
            this.parent = parent;
        }

        int cost() {
            return distance + manhattan(point, dest);
        }

        @Override
        public int compareTo(Node that) {
            return Integer.compare(this.cost(), that.cost());
        }
    }

    private int manhattan(Point a, Point b) {
        return Math.abs(b.x - a.x) + Math.abs(b.y - a.y);
    }
 }

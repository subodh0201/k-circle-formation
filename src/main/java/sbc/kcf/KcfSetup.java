package sbc.kcf;

import sbc.grid.Circle;
import sbc.grid.GridUtils;
import sbc.grid.Point;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

public class KcfSetup {
    private final List<Point> robotPositions;
    private final List<Boolean> xAxisAlignments;
    private final List<Circle> circles;
    private final int n;
    private final int m;
    private final int k;
    private final Point origin;

    public KcfSetup(
            List<Point> robotPositions,
            List<Boolean> xAxisAlignments,
            List<Circle> circles
    ) {
        if (robotPositions == null) throw new InvalidSetupException("robotPositions is null");
        if (xAxisAlignments == null) throw new InvalidSetupException("xAxisAlignments is null");
        if (circles == null) throw new InvalidSetupException("circles is null");
        if (robotPositions.size() != xAxisAlignments.size())
            throw new InvalidSetupException("robotPositions and xAxisAlignments size mismatch");

        n = robotPositions.size();
        m = circles.size();

        if (n == 0) throw new InvalidSetupException("n is 0");
        if (m == 0) throw new InvalidSetupException("m is 0");
        if (n % m != 0) throw new InvalidSetupException("n is not divisible by m");

        k = n / m;

        // check circles are big enough
        for (Circle c : circles)
            if (c.getPointsOnCircle().size() < k)
                throw new InvalidSetupException("circle cannot fit k robots");

        List<Point> centers = circles.stream().map(c -> c.center).collect(Collectors.toList());

        // check robots and centers are unique
        if (!GridUtils.unique(robotPositions))
            throw new InvalidSetupException("robot positions not unique");
        if (!GridUtils.unique(centers))
            throw new InvalidSetupException("centers not unique");

        // check overlapping circles
        if (!GridUtils.overLappingCircles(circles))
            throw new InvalidSetupException("Overlapping circles");

        // shift origin to center of mass of centers
        origin = GridUtils.centerOfMass(centers);
        robotPositions = robotPositions.stream().map(p -> p.subtract(origin))
                .collect(Collectors.toUnmodifiableList());
        circles = circles.stream().map(c -> new Circle(c.center.subtract(origin), c.radius))
                .collect(Collectors.toUnmodifiableList());

        this.robotPositions = robotPositions;
        this.circles = circles;
        this.xAxisAlignments =Collections.unmodifiableList(xAxisAlignments);
    }

    public List<Point> getRobotPositions() {
        return robotPositions;
    }

    public List<Boolean> getXAxisAlignments() {
        return xAxisAlignments;
    }

    public List<Circle> getCircles() {
        return circles;
    }

    public int getN() {
        return n;
    }

    public int getM() {
        return m;
    }

    public int getK() {
        return k;
    }

    public Point getOrigin() {
        return origin;
    }

    public static KcfSetup readFromFile(File file) throws FileNotFoundException {
        try (Scanner in = new Scanner(file)) {
            List<Point> rp = new ArrayList<>();
            List<Boolean> al = new ArrayList<>();
            List<Circle> c = new ArrayList<>();
            int n = Integer.parseInt(in.nextLine());
            while (n-- > 0) {
                String line = in.nextLine();
                String[] split = line.split(" ");
                rp.add(new Point(Integer.parseInt(split[0]), Integer.parseInt(split[1])));
                if (split.length > 2) 
                    al.add(Integer.parseInt(split[2]) == 1);
                else al.add(Math.random() < 0.5);
            }
            int m = Integer.parseInt(in.nextLine());
            while (m-- > 0) {
                c.add(new Circle(new Point(in.nextInt(), in.nextInt()), in.nextInt()));
            }
            return new KcfSetup(rp, al, c);
        }
    }
}

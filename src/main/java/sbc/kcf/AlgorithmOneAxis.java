package sbc.kcf;

import sbc.grid.Point;
import sbc.grid.robot.Algorithm;
import sbc.grid.robot.Direction;

import java.util.*;
import java.util.stream.Collectors;

public class AlgorithmOneAxis implements Algorithm<List<Direction>, KcfConfig> {

    @Override
    public List<Direction> compute(KcfConfig arg) {
        return new AlgorithmOneAxisSolver(arg).getDirectionList();
    }

    private static class AlgorithmOneAxisSolver {
        private final KcfConfig config;
        private List<Direction> directionList = new ArrayList<>();

        public List<Point> R;
        public Set<Point> setR;
        public List<Point> F;
        public Set<Point> setF;
        private final int k;
        public final List<KcfCircle> kcfCircles;
        public KcfHalfPlanes kcfHalfPlanes;
        private final int balance;
        private final Set<Point> candidates;

        private ConfigType configType;

        private AgreementOneAxisResult agreementOneAxisResult;

        private KcfCircle target1, target2;
        private Point candidate1, candidate2;


        public AlgorithmOneAxisSolver(KcfConfig config) {
            this.config = config;
            this.R = config.getRobots();
            this.setR = Set.copyOf(R);
            this.F = config.getCircles().stream().map(circle -> circle.center)
                    .collect(Collectors.toUnmodifiableList());
            this.setF = Set.copyOf(F);
            this.k = R.size() / F.size();
            this.kcfCircles = config.getCircles().stream().map(circle -> new KcfCircle(circle, R, k))
                    .collect(Collectors.toUnmodifiableList());
            this.kcfHalfPlanes = new KcfHalfPlanes(R, kcfCircles);
            this.balance = balance(R);

            Set<Point> _candidates = new HashSet<>(R);
            for (KcfCircle circle : kcfCircles) {
                if (circle.getSaturation() == 0)
                    _candidates.removeAll(circle.getRobotsOnCircle());
            }
            this.candidates = Set.copyOf(_candidates);

            setConfigType();
            System.out.println("Config Type: " + configType);

            if (isFinalState(kcfCircles, k) || isUnsolvable(configType, k)) {
                setDirectionList();
                return;
            }

            algorithmOneAxis();
            setDirectionList();
        }

        public KcfConfig getConfig() {
            return config;
        }

        public List<Direction> getDirectionList() {
            return directionList;
        }

        private void setDirectionList() {
            this.directionList = Collections.unmodifiableList(this.directionList);
        }

        private void setConfigType() {
            if (!symmetricAboutYAxis(F)) configType = ConfigType.I1;
            else if (!symmetricAboutYAxis(R)) configType = ConfigType.I2;
            else if (kcfHalfPlanes.getR_Axis().size() != 0) configType = ConfigType.I3;
            else if (kcfHalfPlanes.getF_Axis().size() == 0) configType = ConfigType.I4;
            else configType = ConfigType.I5;
        }

        private Point CR(Point p) {
            if (agreementOneAxisResult == AgreementOneAxisResult.ALIGNED) return p;
            else if (agreementOneAxisResult == AgreementOneAxisResult.MISALIGNED) return KcfUtils.inversePoint(p);
            else return new Point(Math.abs(p.x), p.y);
        }


        private void algorithmOneAxis() {
            agreementOneAxis();
            System.out.println("AgreementOneAxis: " + agreementOneAxisResult);
            if (agreementOneAxisResult == AgreementOneAxisResult.CHANGE_TO_UNBALANCED) {
                changeToUnbalanced();
            }
            else if (agreementOneAxisResult == AgreementOneAxisResult.ALIGNED
                    || agreementOneAxisResult == AgreementOneAxisResult.MISALIGNED) {
                tFPSWithAxisA();
                cRSWithAxisAgreement();
                if (config.getPosition().equals(candidate1)) {
                    moveTo(target1);
                }
            } else {
                if (allSaturated(kcfHalfPlanes.getF_HL1()) && allSaturated(kcfHalfPlanes.getF_HL2())) {
                    tFPSWithoutAAFy();
                    cRSWithoutAAFy();
                } else {
                    tFPSWithoutAA();
                    cRSWithoutAA();
                }
                if (config.getPosition().equals(candidate1))
                    moveTo(target1);
                if (config.getPosition().equals(candidate2))
                    moveTo(target1);
            }
        }

        private void agreementOneAxis() {
            switch(configType) {
                case I1: {
                    PriorityQueue<Point> pqF = new PriorityQueue<>(
                            (u, v) -> u.y != v.y ? Integer.compare(v.y, u.y) : Integer.compare(u.x, v.x)
                    );
                    pqF.addAll(F);
                    while (!pqF.isEmpty()) {
                        Point p = pqF.remove();
                        if (p.x == 0) continue;
                        if (!setF.contains(KcfUtils.inversePoint(p))) {
                            if (p.x > 0) this.agreementOneAxisResult = AgreementOneAxisResult.ALIGNED;
                            else this.agreementOneAxisResult = AgreementOneAxisResult.MISALIGNED;
                            return;
                        }
                    }
                    return;
                }
                case I2: {
                    if (balance != 0) {
                        if (balance > 0) agreementOneAxisResult = AgreementOneAxisResult.ALIGNED;
                        else agreementOneAxisResult = AgreementOneAxisResult.MISALIGNED;
                    } else {
                        // TODO
                        if (!hasUnsaturated(kcfHalfPlanes.getF_HL1())) {
                            agreementOneAxisResult = AgreementOneAxisResult.ALIGNED;
                        } else if (!hasUnsaturated(kcfHalfPlanes.getF_HL2())) {
                            agreementOneAxisResult = AgreementOneAxisResult.MISALIGNED;
                        } else if (kcfHalfPlanes.getR_Axis().size() > 0)
                                agreementOneAxisResult = AgreementOneAxisResult.CHANGE_TO_UNBALANCED;
                        else if (k % 2 == 0 && kcfHalfPlanes.getF_Axis().size() > 0) {
                            // TODO
                            agreementOneAxisResult = AgreementOneAxisResult.CANNOT_AGREE;
                        } else {
                            agreementOneAxisResult = AgreementOneAxisResult.CANNOT_AGREE;
                        }

                    }
                    return;
                }
                case I3: {
                    this.agreementOneAxisResult = AgreementOneAxisResult.CHANGE_TO_UNBALANCED;
                    return;
                }
                case I4:
                case I5: {
                    this.agreementOneAxisResult = AgreementOneAxisResult.CANNOT_AGREE;
                }
            }
        }



        // Changed!
        private void changeToUnbalanced() {
            Point topMost = kcfHalfPlanes.getR_Axis().get(0);
            for (Point r : kcfHalfPlanes.getR_Axis())
                if (r.y > topMost.y) topMost = r;
            int targetY = topMost.y;
            if (kcfHalfPlanes.getR_HL1().size() > 0) {
                Point topMostInHL1 = kcfHalfPlanes.getR_HL1().get(0);
                for (Point r : kcfHalfPlanes.getR_HL1())
                    if (r.y > topMostInHL1.y) topMostInHL1 = r;
                targetY = topMostInHL1.y + 2;
            }
            if (config.getPosition().equals(topMost))
                directionList = getPath(topMost, new Point(1, targetY));
        }



        // TargetFPSelection with Axis Agreement
        private void tFPSWithAxisA() {
            target1 = getHighestCRUnsaturatedCircle(kcfCircles);
        }

        // TargetFPSelection without Axis Agreement with unsaturated fixed points only on y-axis
        private void tFPSWithoutAAFy() {
            target1 = getHighestCRUnsaturatedCircle(kcfHalfPlanes.getF_Axis());
        }

        // TargetFPSelection without Axis Agreement with unsaturated fixed points in both half planes
        private void tFPSWithoutAA() {
            target1 = getHighestCRUnsaturatedCircle(kcfHalfPlanes.getF_HL1());
            target2 = getHighestCRUnsaturatedCircle(kcfHalfPlanes.getF_HL2());
        }




        // Candidate selection with axis agreement
        private void cRSWithAxisAgreement() {
            Point f = target1.getCircle().center;
            Set<Point> candidates = new HashSet<>(this.candidates);
            candidates.removeAll(target1.getRobotsOnCircle());
            candidate1 = findCandidate(f, candidates);
        }


        // Candidate selection with axis agreement and target on y-axis
        private void cRSWithoutAAFy() {
            Point f = target1.getCircle().center;
            Set<Point> candidates = new HashSet<>(this.candidates);
            candidates.removeAll(target1.getRobotsOnCircle());
            Point candidate = findCandidate(f, candidates);
            if (!candidates.contains(KcfUtils.inversePoint(candidate))) {
                candidate1 = candidate;
            } else {
                // if symmetric configuration
                if (configType == ConfigType.I3 || configType == ConfigType.I4 || configType == ConfigType.I5) {
                    candidate1 = candidate;
                    candidate2 = KcfUtils.inversePoint(candidate);
                } else {
                    Point hcr = null;
                    for (Point r : R ) {
                        if (r.x == 0) continue;
                        if (setR.contains(KcfUtils.inversePoint(r))) continue;
                        if (hcr == null) hcr = r;
                        else if (CR(r).compareTo(CR(hcr)) > 0)
                            hcr = r;
                    }
                    if (hcr.x * candidate.x > 0) candidate1 = candidate;
                    else candidate1 = KcfUtils.inversePoint(candidate);
                }
            }
        }


        private void cRSWithoutAA() {
            // HL1
            Set<Point> hl1Candidates = new HashSet<>(candidates);
            kcfHalfPlanes.getR_HL2().forEach(hl1Candidates::remove);
            kcfHalfPlanes.getR_Axis().forEach(hl1Candidates::remove);
            hl1Candidates.removeAll(target1.getRobotsOnCircle());
            candidate1 = findCandidate(target1.getCircle().center, hl1Candidates);

            // HL2
            Set<Point> hl2Candidates = new HashSet<>(candidates);
            kcfHalfPlanes.getR_HL1().forEach(hl1Candidates::remove);
            kcfHalfPlanes.getR_Axis().forEach(hl1Candidates::remove);
            hl1Candidates.removeAll(target2.getRobotsOnCircle());
            candidate2 = findCandidate(target2.getCircle().center, hl2Candidates);

        }


        private Point findCandidate(Point f, Iterable<Point> candidates) {
            Point candidate = null;
            for (Point r : candidates) {
                if (candidate == null)
                    candidate = r;
                else {
                    if (f.distance(r) < f.distance(candidate))
                        candidate = r;
                    else if (f.distance(r) == f.distance(candidate)) {
                        if (CR(r).compareTo(CR(candidate)) > 0)
                            candidate = r;
                    }
                }
            }
            return candidate;
        }


        private void moveTo(KcfCircle circle) {
            System.out.println("Target: " + target1.getCircle().center);
            System.out.println("Candidate: " + candidate1);


            Point pos = config.getPosition();
            Set<Point> targets = new HashSet<>(circle.getCircle().getPointsOnCircle());
            targets.removeAll(circle.getRobotsOnCircle());

            Point targetPoint = null;
            for (Point target : targets) {
                if (targetPoint == null)
                    targetPoint = target;
                else if (target.distance(pos) < targetPoint.distance(pos))
                    targetPoint = target;
            }

            directionList = getPath(pos, targetPoint);

            System.out.println("Path: " + directionList);
        }

        private  List<Direction> getPath(Point src, Point dest) {
            return new KcfAStar(src, dest, config).getDirectionList();
        }


        private KcfCircle getHighestCRUnsaturatedCircle(List<KcfCircle> circles) {
            PriorityQueue<KcfCircle> pqC = new PriorityQueue<>(
                    (u, v) -> CR(v.getCircle().center).compareTo(CR(u.getCircle().center))
            );
            pqC.addAll(circles);
            while (!pqC.isEmpty()) {
                KcfCircle circle = pqC.remove();
                if (circle.getSaturation() < 0) {
                    return circle;
                }
            }
            return null;
        }

        private Point getHighestCRPoint(List<Point> points) {
            if (points.isEmpty()) return null;
            Point hcr = points.get(0);
            for (Point point : points) {
                if (CR(point).compareTo(CR(hcr)) > 0) {
                    hcr = point;
                }
            }
            return hcr;
        }

        private boolean allSaturated(List<KcfCircle> circles) {
            for (KcfCircle circle : circles)
                if (circle.getSaturation() != 0)
                    return false;
            return true;
        }

        private boolean hasUnsaturated(List<KcfCircle> circles) {
            for (KcfCircle circle : circles)
                if (circle.getSaturation() < 0)
                    return true;
            return false;
        }


    }

    enum ConfigType {
        I1, I2, I3, I4, I5
    }

    enum AgreementOneAxisResult {
        ALIGNED, MISALIGNED, CANNOT_AGREE, CHANGE_TO_UNBALANCED
    }

    public static boolean symmetricAboutYAxis(List<Point> points) {
        HashSet<Point> set = new HashSet<>();
        for (Point point : points) {
            if (point.x == 0) continue;
            if (set.contains(KcfUtils.inversePoint(point))) {
                set.remove(KcfUtils.inversePoint(point));
            } else set.add(point);
        }
        return set.isEmpty();
    }


    public static boolean isFinalState(List<KcfCircle> circles, int k) {
        for (KcfCircle circle : circles) {
            if (circle.getRobotsOnCircle().size() != k)
                return false;
        }
        System.out.println("Final State");
        return true;
    }

    public static boolean isUnsolvable(ConfigType configType, int k) {
        return configType == ConfigType.I5 && k % 2 == 1;
    }

    public static int balance(List<Point> R) {
        int count = 0;
        for (Point r : R) {
            if (r.x > 0) count++;
            if (r.x < 0) count--;
        }
        return count;
    }
}

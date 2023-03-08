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
        private List<Direction> directionList = Collections.emptyList();

        private final List<Point> R;
        private final Set<Point> setR;
        private final List<Point> F;
        private final Set<Point> setF;
        private final int k;
        private final List<KcfCircle> kcfCircles;
        private final KcfHalfPlanes kcfHalfPlanes;
        private final int balance;
        private final ConfigType configType;
        private final Set<Point> candidates;


        // Used by algorithmOneAxis()
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
            this.balance = kcfHalfPlanes.getR_HL1().size() - kcfHalfPlanes.getR_HL2().size();
            this.configType = ConfigType.getConfigType(R, F, kcfHalfPlanes);
            this.candidates = Set.copyOf(getCandidates(R, kcfCircles));


            System.out.print(config.getPosition() + ": " + configType + " balance=" + balance);

            if (isFinalState(kcfCircles, k)) {
                System.out.println(" SOLVED");
            } else if (isUnsolvable(configType, k)) {
                System.out.println(" UNSOLVABLE");
            } else {
                algorithmOneAxis();
                System.out.print(" " + agreementOneAxisResult);
                System.out.print(" t1=" + target1);
                System.out.print(" t2=" + target2);
                System.out.print(" c1=" + candidate1);
                System.out.print(" c2=" + candidate2);
                System.out.println(" " + directionList);
            }
        }

        public List<Direction> getDirectionList() {
            return directionList;
        }

        private void setDirectionList(List<Direction> directionList) {
            this.directionList = Collections.unmodifiableList(directionList);
        }

        private Point CR(Point p) {
            if (agreementOneAxisResult == AgreementOneAxisResult.ALIGNED) return p;
            else if (agreementOneAxisResult == AgreementOneAxisResult.MISALIGNED) return KcfUtils.inversePoint(p);
            else return new Point(Math.abs(p.x), p.y);
        }


        private void algorithmOneAxis() {
            agreementOneAxis();
            if (agreementOneAxisResult == AgreementOneAxisResult.CHANGE_TO_UNBALANCED) {
                changeToUnbalanced();
            }
            else if (agreementOneAxisResult == AgreementOneAxisResult.ALIGNED
                    || agreementOneAxisResult == AgreementOneAxisResult.MISALIGNED) {
                KcfCircle target = tFPSWithAxisAgreement();
                Point candidate = cRSWithAxisAgreement(target);
                if (config.getPosition().equals(candidate)) {
                    moveTo(target);
                }
                target1 = target;
                candidate1 = candidate;
            }
            else {
                if (allSaturated(kcfHalfPlanes.getF_HL1()) && allSaturated(kcfHalfPlanes.getF_HL2())) {
                    KcfCircle target = tFPSWithoutAAFy();
                    cRSWithoutAAFy(target);
                    if (config.getPosition().equals(candidate1))
                        moveTo(target);
                    if (config.getPosition().equals(candidate2))
                        moveTo(target);
                    target1 = target;
                } else {
                    tFPSWithoutAA();
                    cRSWithoutAA();
                    if (config.getPosition().equals(candidate1))
                        moveTo(target1);
                    if (config.getPosition().equals(candidate2))
                        moveTo(target2);
                }
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
                        boolean allSaturatedH1 = AlgorithmOneAxis.allSaturated(kcfHalfPlanes.getF_HL1());
                        boolean allSaturatedH2 = AlgorithmOneAxis.allSaturated(kcfHalfPlanes.getF_HL2());
                        if (allSaturatedH1 && !allSaturatedH2) {
                            agreementOneAxisResult = AgreementOneAxisResult.ALIGNED;
                        } else if (!allSaturatedH1 && allSaturatedH2) {
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

        // Moving top most robot on y-axis to HL1
        private void changeToUnbalanced() {
            Point topMostRobotAxis = topMostPoint(kcfHalfPlanes.getR_Axis());

            if (topMostRobotAxis == null)
                throw new IllegalStateException("No robots on y-axis");
            int targetY = topMostRobotAxis.y;

            Point topMostRobotHL1 = topMostPoint(kcfHalfPlanes.getR_HL1());
            if (topMostRobotHL1 != null)
                targetY = Math.max(targetY, topMostRobotHL1.y);

            targetY += (int) (2 + getLambda(config.getPosition()));

            if (config.getPosition().equals(topMostRobotAxis))
                setDirectionList(getPath(topMostRobotAxis, new Point(1, targetY)));
        }

        private double getLambda(Point cur) {
            double max = 0;
            for (Point f : F) {
                for (Point r : R) {
                    if (r.equals(cur)) continue;
                    max = Math.max(max, f.distance(r));
                }
            }
            return max;
        }



        // TargetFPSelection with Axis Agreement
        private KcfCircle tFPSWithAxisAgreement() {
            KcfCircle target = getTargetCircle(kcfCircles);
            if (target == null)
                throw new IllegalStateException("Could not find a target circle!");
            return target;
        }


        // TargetFPSelection without Axis Agreement with unsaturated fixed points only on y-axis
        private KcfCircle tFPSWithoutAAFy() {
            KcfCircle target = getTargetCircle(kcfHalfPlanes.getF_Axis());
            if (target == null)
                throw new IllegalStateException("Could not find a target circle on axis");
            return target;
        }


        // TargetFPSelection without Axis Agreement with unsaturated fixed points in both half planes
        private void tFPSWithoutAA() {
            target1 = getTargetCircle(kcfHalfPlanes.getF_HL1());
            if (target1 == null)
                throw new IllegalStateException("Could not find a target circle in HL1");
            target2 = getTargetCircle(kcfHalfPlanes.getF_HL2());
            if (target2 == null)
                throw new IllegalStateException("Could not find a target circle in HL2");
        }




        // Candidate selection with axis agreement
        private Point cRSWithAxisAgreement(KcfCircle target) {
            Point f = target.getCircle().center;

            Set<Point> candidates = new HashSet<>(this.candidates);
            candidates.removeAll(target.getRobotsOnCircle());

            Point candidate = findCandidate(f, candidates);
            if (candidate == null)
                throw new IllegalStateException("Could not find candidate");
            return candidate;
        }


        // Candidate selection with axis agreement and target on y-axis
        private void cRSWithoutAAFy(KcfCircle target) {
            Point f = target.getCircle().center;

            Set<Point> candidates = new HashSet<>(this.candidates);
            candidates.removeAll(target.getRobotsOnCircle());

            Point candidate = findCandidate(f, candidates);
            if (candidate == null)
                throw new IllegalStateException("Could not find candidate");

            if (!candidates.contains(KcfUtils.inversePoint(candidate))) {
                candidate1 = candidate;
            }
            else {
                // if symmetric configuration
                if (!asymmetricAboutYAxis(R)) {
                    candidate1 = candidate;
                    candidate2 = KcfUtils.inversePoint(candidate);
                }
                else {
                    Point hcr = null;

                    for (Point r : R ) {
                        if (r.x == 0) continue;
                        if (setR.contains(KcfUtils.inversePoint(r))) continue;
                        if (hcr == null) hcr = r;
                        else if (CR(r).compareTo(CR(hcr)) > 0)
                            hcr = r;
                    }

                    if (hcr == null)
                        throw new IllegalStateException("Could not find an asymmetric robot");

                    if (hcr.x * candidate.x > 0) candidate1 = candidate;
                    else candidate1 = KcfUtils.inversePoint(candidate);
                }
            }
        }


        private void cRSWithoutAA() {
            // HL1
            Set<Point> HL1Candidates = new HashSet<>(candidates);
            kcfHalfPlanes.getR_HL2().forEach(HL1Candidates::remove);
            kcfHalfPlanes.getR_Axis().forEach(HL1Candidates::remove);
            HL1Candidates.removeAll(target1.getRobotsOnCircle());
            candidate1 = findCandidate(target1.getCircle().center, HL1Candidates);

            // HL2
            Set<Point> HL2Candidates = new HashSet<>(candidates);
            kcfHalfPlanes.getR_HL1().forEach(HL2Candidates::remove);
            kcfHalfPlanes.getR_Axis().forEach(HL2Candidates::remove);
            HL2Candidates.removeAll(target2.getRobotsOnCircle());
            candidate2 = findCandidate(target2.getCircle().center, HL2Candidates);

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
            Point pos = config.getPosition();
            Set<Point> points = new HashSet<>(circle.getCircle().getPointsOnCircle());
            points.removeAll(circle.getRobotsOnCircle());

            Point target = null;
            for (Point point : points) {
                if (target == null) target = point;
                else if (point.distance(pos) < target.distance(pos))
                    target = point;
            }

            if (target == null)
                throw new IllegalStateException("Could not find an open spot on the circle");

            setDirectionList(getPath(pos, target));
        }

        private  List<Direction> getPath(Point start, Point dest) {
            return new KcfAStar(start, dest, config).getDirectionList();
        }


        // Finds the unsaturated circle with the highest configuration rank
        private KcfCircle getTargetCircle(List<KcfCircle> circles) {
            KcfCircle target = null;
            for (KcfCircle circle : circles) {
                if (circle.getSaturation() < 0) {
                    if (target == null ) target = circle;
                    else if (CR(circle.getCircle().center).compareTo(CR(target.getCircle().center)) > 0)
                        target = circle;
                }
            }
            return target;
        }
    }

    enum ConfigType {
        I1, I2, I3, I4, I5;

        public static ConfigType getConfigType(List<Point> R, List<Point> F, KcfHalfPlanes kcfHalfPlanes) {
            if (asymmetricAboutYAxis(F)) return ConfigType.I1;
            if (asymmetricAboutYAxis(R)) return ConfigType.I2;
            if (kcfHalfPlanes.getR_Axis().size() != 0) return ConfigType.I3;
            if (kcfHalfPlanes.getF_Axis().size() == 0) return ConfigType.I4;
            return ConfigType.I5;
        }
    }

    enum AgreementOneAxisResult {
        ALIGNED, MISALIGNED, CANNOT_AGREE, CHANGE_TO_UNBALANCED
    }




    public static boolean isFinalState(List<KcfCircle> circles, int k) {
        for (KcfCircle circle : circles) {
            if (circle.getRobotsOnCircle().size() != k)
                return false;
        }
        return true;
    }

    public static boolean isUnsolvable(ConfigType configType, int k) {
        return configType == ConfigType.I5 && k % 2 == 1;
    }



    // CLEANUP
    public static Set<Point> getCandidates(List<Point> R, List<KcfCircle> kcfCircles) {
        Set<Point> candidates = new HashSet<>(R);
        for (KcfCircle circle : kcfCircles) {
            if (circle.getSaturation() == 0)
                candidates.removeAll(circle.getRobotsOnCircle());
        }
        return candidates;
    }

    public static boolean allSaturated(List<KcfCircle> circles) {
        for (KcfCircle circle : circles)
            if (circle.getSaturation() < 0)
                return false;
        return true;
    }

    public static boolean asymmetricAboutYAxis(List<Point> points) {
        HashSet<Point> set = new HashSet<>();
        for (Point point : points) {
            if (point.x == 0) continue;
            if (set.contains(KcfUtils.inversePoint(point))) {
                set.remove(KcfUtils.inversePoint(point));
            } else set.add(point);
        }
        return !set.isEmpty();
    }

    public static Point topMostPoint(List<Point> points) {
        if (points.isEmpty()) return null;
        Point topMost = points.get(0);
        for (Point point : points) {
            if (point.y > topMost.y) topMost = point;
        }
        return topMost;
    }
}

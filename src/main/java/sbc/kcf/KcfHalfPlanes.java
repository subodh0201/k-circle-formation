package sbc.kcf;

import sbc.grid.Point;

import java.util.List;
import java.util.stream.Collectors;

public class KcfHalfPlanes {
    private final List<Point> R_HL1;
    private final List<Point> R_HL2;
    private final List<Point> R_Axis;

    private final List<KcfCircle> F_HL1;
    private final List<KcfCircle> F_HL2;
    private final List<KcfCircle> F_Axis;

    public KcfHalfPlanes(List<Point> R, List<KcfCircle> F) {
        R_HL1 = R.stream().filter(r -> r.x > 0).collect(Collectors.toUnmodifiableList());
        R_HL2 = R.stream().filter(r -> r.x < 0).collect(Collectors.toUnmodifiableList());
        R_Axis = R.stream().filter(r -> r.x == 0).collect(Collectors.toUnmodifiableList());

        F_HL1 = F.stream().filter(c -> c.getCircle().center.x > 0).collect(Collectors.toUnmodifiableList());
        F_HL2 = F.stream().filter(c -> c.getCircle().center.x < 0).collect(Collectors.toUnmodifiableList());
        F_Axis = F.stream().filter(c -> c.getCircle().center.x == 0).collect(Collectors.toUnmodifiableList());
    }

    public List<Point> getR_HL1() {
        return R_HL1;
    }

    public List<Point> getR_HL2() {
        return R_HL2;
    }

    public List<Point> getR_Axis() {
        return R_Axis;
    }

    public List<KcfCircle> getF_HL1() {
        return F_HL1;
    }

    public List<KcfCircle> getF_HL2() {
        return F_HL2;
    }

    public List<KcfCircle> getF_Axis() {
        return F_Axis;
    }
}

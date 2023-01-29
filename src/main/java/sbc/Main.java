package sbc;

import sbc.grid.Point;
import sbc.gui.App;
import sbc.kcf.KcfAlgorithm;
import sbc.kcf.KcfSimulation;

import javax.swing.*;
import java.util.List;

public class Main {

    private static final List<Point> robotPosition = List.of(
            new Point(5, 5), new Point(-4, 2), new Point(6, -3),
            new Point(-2, -2), new Point(0, 3), new Point(-4, 0)
    );

    private static final List<Point> centers = List.of(new Point(6, 6), new Point(-6, -6));

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::startApp);
    }

    public static void startApp() {
        App app = new App("K Circle Formation");
        app.addSimulation(new KcfSimulation(robotPosition, KcfAlgorithm::ULDR, centers, 3));
    }
}
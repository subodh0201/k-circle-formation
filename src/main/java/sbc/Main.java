package sbc;

import sbc.grid.Point;
import sbc.kcf.KcfAlgorithm;
import sbc.kcf.KcfSimulation;
import sbc.kcf.KcfState;

import java.util.List;

public class Main {

    private static final List<Point> robotPosition = List.of(
            new Point(5, 5), new Point(-4, 2), new Point(6, -3),
            new Point(-2, -2), new Point(0, 3), new Point(-4, 0)
    );

    private static final List<Point> centers = List.of(new Point(6, 6), new Point(-6, -6));


    public static void main(String[] args) {
        KcfSimulation simulation = new KcfSimulation(robotPosition, KcfAlgorithm::ULDR, centers, 3);
        int limit = 100;
        while (simulation.getState() == KcfState.SOLVING &&  limit-- > 0) {
            simulation.step();
            System.out.println("Round: " + simulation.getRound() + ", phase: " + simulation.getPhase());
            System.out.println(simulation.getRobots());
        }
    }
}
package sbc.kcf;

import sbc.grid.Point;
import sbc.grid.Circle;
import sbc.gui.GridEntity;
import sbc.gui.GridViewPort;

import java.awt.*;

public class KcfSimulationRenderer implements GridEntity {
    private final Color centerColor = Color.ORANGE;
    private final Color circleColor = Color.GREEN;
    private final Color robotColor = Color.RED;
    private final Color pathColor = Color.GRAY;

    private final KcfSimulation simulation;
    private int delay;
    private int framesSinceLastUpdate;

    public KcfSimulationRenderer(KcfSimulation simulation) {
        this.simulation = simulation;
        this.delay = 10;
        this.framesSinceLastUpdate = 10;
    }

    public void setDelay(int delay) {
        this.delay = Math.max(1, delay);
    }

    @Override
    public boolean update() {
        if (framesSinceLastUpdate < delay) {
            framesSinceLastUpdate++;
            return false;
        }
        framesSinceLastUpdate = 0;
        return simulation.step();
    }

    @Override
    public void render(Graphics2D graphics2D, GridViewPort gridViewPort) {
        renderCircles(graphics2D, gridViewPort);
        renderRobots(graphics2D, gridViewPort);
    }

    private void renderCircles(Graphics2D g, GridViewPort v) {
        for (Circle c : simulation.getCircles()) {
            g.setColor(centerColor);
            renderTile(g, v, c.center.x, c.center.y);
            g.setColor(circleColor);
            for (Point p : c.getPointsOnCircle()) {
                renderTile(g, v, p.x, p.y);
            }
        }
    }

    private void renderRobots(Graphics2D g, GridViewPort v) {
        g.setColor(robotColor);
        for (KcfRobot<KcfConfig> robot : simulation.getRobots()) {
            renderCircleOnTile(g, v, robot.getPosition().x, robot.getPosition().y);
        }
    }

    private void renderTile(Graphics2D g, GridViewPort v, int x, int y) {
        g.fillRect(v.gridToScreenX(x), v.gridToScreenY(y), v.tileSize(), v.tileSize());
    }

    private void renderCircleOnTile(Graphics2D g, GridViewPort v, int x, int y) {
        g.fillOval(v.gridToScreenX(x), v.gridToScreenY(y), v.tileSize(), v.tileSize());
    }
}

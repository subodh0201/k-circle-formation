package sbc.kcf;

import sbc.grid.Point;
import sbc.grid.Circle;
import sbc.grid.robot.Path;
import sbc.gui.GridEntity;
import sbc.gui.GridViewPort;

import java.awt.*;
import java.util.HashSet;

public class KcfSimulationRenderer implements GridEntity {
    private final Color centerColor = Color.ORANGE;
    private final Color circleColor = Color.GREEN;
    private final Color robotColor = Color.BLUE;
    private final Color misalignedRobotColor = Color.RED;
    private final Color pathColor = Color.gray;
    private final Color pathStart = Color.GREEN;
    private final Color pathEnd = Color.BLUE;
    private final Color errorTile = Color.RED;

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
        renderPaths(graphics2D, gridViewPort);
        if (simulation.getState() == KcfState.ERROR)
            renderError(graphics2D, gridViewPort);
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

    private void renderError(Graphics2D g, GridViewPort v) {
        g.setColor(errorTile);
        HashSet<Point> robots = new HashSet<>();
        for (Point p : simulation.getRobotPositions()) {
            if (!robots.add(p)) {
                renderTile(g, v, p.x, p.y);
            }
        }
    }

    private void renderRobots(Graphics2D g, GridViewPort v) {
        for (KcfRobot<KcfConfig> robot : simulation.getRobots()) {
            if (robot.isxAxisAlignment()) g.setColor(robotColor);
            else g.setColor(misalignedRobotColor);
            renderCircleOnTile(g, v, robot.getPosition().x, robot.getPosition().y);
        }
    }

    private void renderPaths(Graphics2D g, GridViewPort v) {
        for (KcfRobot<KcfConfig> robot : simulation.getRobots()) {
            renderPath(g, v, robot.getCurrentPath());
        }
    }

    private void renderPath(Graphics2D g, GridViewPort v, Path path) {
        if (path == null || path.getPointList().size() == 0) return;
        g.setColor(pathColor);
        for (Point p : path.getPointList()) {
            renderTileOutline(g, v, p.x, p.y);
        }
        g.setColor(pathStart);
        renderTileOutline(g, v, path.getPointList().get(0).x, path.getPointList().get(0).y);
        g.setColor(pathEnd);
        renderTileOutline(g, v, path.getPointList().get(path.getPointList().size() - 1).x,
                path.getPointList().get(path.getPointList().size() - 1).y);
    }

    private void renderTile(Graphics2D g, GridViewPort v, int x, int y) {
        g.fillRect(v.gridToScreenX(x), v.gridToScreenY(y), v.tileSize(), v.tileSize());
    }

    private void renderCircleOnTile(Graphics2D g, GridViewPort v, int x, int y) {
        g.fillOval(v.gridToScreenX(x), v.gridToScreenY(y), v.tileSize(), v.tileSize());
    }

    private void renderTileOutline(Graphics2D g, GridViewPort v, int x, int y) {
        g.drawRect(v.gridToScreenX(x), v.gridToScreenY(y), v.tileSize(), v.tileSize());
    }
}

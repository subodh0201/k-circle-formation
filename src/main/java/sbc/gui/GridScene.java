package sbc.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GridScene extends JPanel {

    private final DrawLoop drawLoop;
    private final GridViewPort gridViewPort;

    private final EventQueue eventQueue = new EventQueue();
    private final List<GridEntity> entities = Collections.synchronizedList(new ArrayList<>());

    public GridScene() {
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);

        this.drawLoop = new DrawLoop() {
            @Override
            protected void update() { GridScene.this.update(); }

            @Override
            protected void render() { GridScene.this.repaint(); }
        };

        // Event listeners
        this.addMouseMotionListener(new ZoomPanHandler());
        this.addMouseWheelListener(new ZoomPanHandler());
        this.addComponentListener(new ResizeHandler());


        this.gridViewPort = new GridViewPort(this.getWidth(), this.getHeight(), 16, 0, 0,
                this.getWidth() / 2, this.getHeight() / 2);
    }

    public void addEntity(GridEntity e) {
        entities.add(e);
    }

    public void start() {
        this.drawLoop.start();
    }

    public void pause() {
        this.drawLoop.pause();
    }

    public void resume() {
        this.drawLoop.resume();
    }

    private void update() {
        // consume all pending events
        int sz = eventQueue.size();
        while (sz-- > 0)
            eventQueue.dequeue().consume();

        // update all entities
        for (GridEntity e : entities)
            e.update();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2D = (Graphics2D) g;
        for (GridEntity e : entities)
            e.render(g2D, gridViewPort);
        g2D.dispose();
    }

    // Allows zooming (scroll) and panning (drag) using the mouse
    private class ZoomPanHandler implements MouseMotionListener, MouseWheelListener {
        private Point cursor;

        @Override
        public void mouseDragged(MouseEvent e) {
            if (cursor != null) {
                int diffX = e.getPoint().x - cursor.x, diffY = e.getPoint().y - cursor.y;
                eventQueue.enqueue(() -> gridViewPort.addOffset(diffX, diffY));
                cursor = e.getPoint();
            }
            cursor = e.getPoint();
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            cursor = null;
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            eventQueue.enqueue(() -> gridViewPort.zoom(-e.getWheelRotation()));
        }
    }

    private class ResizeHandler implements ComponentListener {

        @Override
        public void componentResized(ComponentEvent e) {
            gridViewPort.setWidth(getWidth());
            gridViewPort.setHeight(getHeight());
        }

        @Override
        public void componentMoved(ComponentEvent e) {}

        @Override
        public void componentShown(ComponentEvent e) {}

        @Override
        public void componentHidden(ComponentEvent e) {}
    }
}

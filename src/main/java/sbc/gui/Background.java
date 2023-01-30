package sbc.gui;

import java.awt.*;

public class Background implements GridEntity {
    public final Color oddTiles;
    public final Color evenTiles;
    public final Color axisBorders;

    public Background() {
        this(Color.WHITE, new Color(245, 245, 245), new Color(0, 220, 220));
    }

    public Background(Color oddTiles, Color evenTiles, Color axisBorders) {
        this.oddTiles = oddTiles;
        this.evenTiles = evenTiles;
        this.axisBorders = axisBorders;
    }

    @Override
    public boolean update() {
        // nothing to update
        return false;
    }

    @Override
    public void render(Graphics2D g, GridViewPort v) {
        for (int tileY = 0; tileY < v.tileCountY(); tileY++) {
            for (int tileX = 0; tileX < v.tileCountX(); tileX++) {
                boolean even = (v.tileToGridX(tileX) + v.tileToGridY(tileY)) % 2 == 0;
                g.setColor(even ? evenTiles : oddTiles);
                g.fillRect(v.tileToScreenX(tileX), v.tileToScreenY(tileY), v.tileSize(), v.tileSize());
            }
        }

        g.setColor(axisBorders);
        // y-axis
        if (v.isVisibleGridX(0)) {
            for (int tileY = 0; tileY < v.tileCountY(); tileY++) {
                g.drawRect(v.gridToScreenX(0), v.tileToScreenY(tileY), v.tileSize(), v.tileSize());
            }
        }
        // x-axis
        if (v.isVisibleGridY(0)) {
            for (int tileX = 0; tileX < v.tileCountX(); tileX++) {
                g.drawRect(v.tileToScreenX(tileX), v.gridToScreenY(0), v.tileSize(), v.tileSize());
            }
        }
    }
}

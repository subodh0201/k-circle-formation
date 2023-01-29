package sbc.gui;

import java.awt.*;

public interface GridEntity {
    void update();
    void render(Graphics2D graphics2D, GridViewPort gridViewPort);
}

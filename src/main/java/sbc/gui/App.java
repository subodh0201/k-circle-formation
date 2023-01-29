package sbc.gui;

import javax.swing.*;
import java.awt.*;

public class App extends JFrame {

    private final GridScene gridScene;

    public App(String title) {
        super(title);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setMinimumSize(new Dimension(512, 512));
        this.setBackground(Color.CYAN);

        this.gridScene = new GridScene();
        this.add(gridScene);
        this.setVisible(true);
    }

    public void setGridEntity(GridEntity gridEntity) {
        this.gridScene.setEntity(gridEntity);
    }
}

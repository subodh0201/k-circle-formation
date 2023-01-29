package sbc.gui;

import javax.swing.*;
import java.awt.*;

public class App extends JFrame {

    private final GridScene gridScene;
    private final ControlPanel controlPanel;

    public App(String title) {
        super(title);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setMinimumSize(new Dimension(512, 512));
        this.setBackground(Color.CYAN);
        this.setLayout(new BorderLayout());

        this.gridScene = new GridScene();
        this.add(gridScene, BorderLayout.CENTER);

        this.controlPanel = new ControlPanel(gridScene);
        this.add(controlPanel, BorderLayout.SOUTH);

        this.setVisible(true);
    }
}

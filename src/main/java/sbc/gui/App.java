package sbc.gui;

import sbc.kcf.KcfAlgorithm;

import javax.swing.*;
import java.awt.*;

public class App extends JFrame {

    private final GridScene gridScene;
    private final KcfSimulationControl controlPanel;

    public App(String title) {
        super(title);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setMinimumSize(new Dimension(800, 600));
        this.setBackground(Color.CYAN);
        this.setLayout(new BorderLayout());

        this.gridScene = new GridScene();
        this.add(gridScene, BorderLayout.CENTER);

        this.controlPanel = new KcfSimulationControl(gridScene, KcfAlgorithm::ULDR);
        this.add(controlPanel, BorderLayout.SOUTH);

        this.setVisible(true);
    }
}

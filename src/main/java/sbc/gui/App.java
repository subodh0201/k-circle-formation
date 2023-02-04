package sbc.gui;

import sbc.kcf.AlgorithmOneAxis;

import javax.swing.*;
import java.awt.*;

public class App extends JFrame {

    public App(String title) {
        super(title);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setMinimumSize(new Dimension(800, 600));
        this.setBackground(Color.CYAN);
        this.setLayout(new BorderLayout());

        GridScene gridScene = new GridScene();
        this.add(gridScene, BorderLayout.CENTER);

        KcfSimulationControl controlPanel = new KcfSimulationControl(gridScene, new AlgorithmOneAxis());
        this.add(controlPanel, BorderLayout.SOUTH);

        this.setVisible(true);
    }
}

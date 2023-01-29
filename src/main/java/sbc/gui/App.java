package sbc.gui;

import sbc.kcf.KcfSimulation;
import sbc.kcf.KcfSimulationRenderer;

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

    public void addSimulation(KcfSimulation  kcfSimulation) {
        this.gridScene.addEntity(new Background());
        this.gridScene.addEntity(new KcfSimulationRenderer(kcfSimulation));
        this.gridScene.start();
    }
}

package sbc.gui;

import sbc.kcf.KcfAlgorithm;
import sbc.kcf.KcfSetup;
import sbc.kcf.KcfSimulation;
import sbc.kcf.KcfSimulationRenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ControlPanel extends JPanel implements ActionListener {
    private final JButton selectFileButton;
    private final JButton simulateButton;
    private final JFileChooser fileChooser;
    private final JLabel statusLabel;
    private KcfSetup kcfSetup;

    private final GridScene gridScene;


    public ControlPanel(GridScene gridScene) {
        selectFileButton = new JButton("Select File");
        fileChooser = new JFileChooser();
        statusLabel = new JLabel("Select a file");
        simulateButton = new JButton("Simulate");
        simulateButton.setEnabled(false);

        this.gridScene = gridScene;

        this.setLayout(new FlowLayout());
        this.add(selectFileButton);
        this.add(statusLabel);
        this.add(simulateButton);

        selectFileButton.addActionListener(this);
        simulateButton.addActionListener(this);

    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == selectFileButton) {
            int returnVal = fileChooser.showOpenDialog(ControlPanel.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                try {
                    this.kcfSetup = KcfSetup.readFromFile(fileChooser.getSelectedFile());
                    statusLabel.setText(fileChooser.getSelectedFile().getName());
                    simulateButton.setEnabled(true);
                } catch (Exception ex) {
                    statusLabel.setText("Could not read file");
                }
            }
        } else if (e.getSource() == simulateButton) {
            gridScene.setEntity(new KcfSimulationRenderer(new KcfSimulation(kcfSetup, KcfAlgorithm::ULDR)));
        }
    }
}

package sbc.gui;

import sbc.grid.robot.Algorithm;
import sbc.grid.robot.Direction;
import sbc.kcf.*;

import java.util.List;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class KcfSimulationControl extends JPanel implements ActionListener, GridEntity, ChangeListener {
    private static final int DELAY_MIN = 1;
    private static final int DELAY_MAX = 30;
    private static final int DELAY_INIT = 15;

    private final JButton selectFileButton;
    private final JFileChooser fileChooser;
    private final JLabel fileLabel;
    private final JLabel statusLabel;
    private final JButton resetBtn;
    private final JButton playBtn;
    private final JButton pauseBtn;
    private final JSlider delaySlider;

    private boolean paused;

    private KcfSetup kcfSetup;
    private KcfSimulationRenderer kcfSimulationRenderer;

    private final Algorithm<List<Direction>, KcfConfig> algorithm;


    public KcfSimulationControl(GridScene gridScene, Algorithm<List<Direction>, KcfConfig> algorithm) {
        this.algorithm = algorithm;
        selectFileButton = new JButton("Load File");
        fileChooser = new JFileChooser();
        fileLabel = new JLabel("No file selected");
        statusLabel = new JLabel("Load a File");
        resetBtn = new JButton("Reset");
        playBtn = new JButton("Play");
        pauseBtn = new JButton("Pause");
        delaySlider = new JSlider(JSlider.HORIZONTAL, DELAY_MIN, DELAY_MAX, DELAY_INIT);
        delaySlider.setMajorTickSpacing(5);
        delaySlider.setMinorTickSpacing(1);


        selectFileButton.addActionListener(this);
        resetBtn.addActionListener(this);
        playBtn.addActionListener(this);
        pauseBtn.addActionListener(this);
        delaySlider.addChangeListener(this);


        this.setLayout(new FlowLayout());
        this.add(selectFileButton);
        this.add(fileLabel);
        this.add(resetBtn);
        this.add(playBtn);
        this.add(pauseBtn);
        this.add(delaySlider);
        this.add(statusLabel);


        resetBtn.setEnabled(false);
        playBtn.setEnabled(false);
        pauseBtn.setEnabled(false);

        paused = true;

        gridScene.setEntity(this);
    }



    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == selectFileButton) {
            int returnVal = fileChooser.showOpenDialog(KcfSimulationControl.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                try {
                    this.kcfSetup = KcfSetup.readFromFile(fileChooser.getSelectedFile());
                    newSetUpLoaded(fileChooser.getSelectedFile().getName());
                } catch (Exception ex) {
                    statusLabel.setText("Could not read file");
                }
            }
        } else if (e.getSource() == playBtn) {
            playBtnClickHandler();
        } else if (e.getSource() == pauseBtn) {
            pauseBtnClickHandler();
        } else if (e.getSource() == resetBtn) {
            resetBtnClickHandler();
        }

    }

    private void newSetUpLoaded(String name) {
        paused = true;

        resetBtn.setEnabled(true);
        playBtn.setEnabled(true);
        pauseBtn.setEnabled(false);

        fileLabel.setText(name);

        setKcfSimulationRenderer();
    }


    private void playBtnClickHandler() {
        playBtn.setEnabled(false);
        pauseBtn.setEnabled(true);
        paused = false;
        statusLabel.setText("Simulating");
    }

    private void pauseBtnClickHandler() {
        playBtn.setEnabled(true);
        pauseBtn.setEnabled(false);
        paused = true;
        statusLabel.setText("Paused");
    }

    private void resetBtnClickHandler() {
        pauseBtnClickHandler();
        setKcfSimulationRenderer();
    }

    private void setKcfSimulationRenderer() {
        kcfSimulationRenderer = new KcfSimulationRenderer(new KcfSimulation(kcfSetup, algorithm));
        kcfSimulationRenderer.setDelay(delaySlider.getValue());
        statusLabel.setText("Ready for simulation");
    }

    @Override
    public boolean update() {
        if (paused || kcfSimulationRenderer == null) return false;
        return kcfSimulationRenderer.update();
    }

    @Override
    public void render(Graphics2D graphics2D, GridViewPort gridViewPort) {
        if (kcfSimulationRenderer == null) return;
        kcfSimulationRenderer.render(graphics2D, gridViewPort);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == delaySlider) {
            if (kcfSimulationRenderer != null)
                kcfSimulationRenderer.setDelay(delaySlider.getValue());
        }
    }
}

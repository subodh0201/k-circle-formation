package sbc.gui;

import javax.swing.*;
import java.awt.*;

public class App extends JFrame {

    public App(String title) {
        super(title);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setMinimumSize(new Dimension(512, 512));
        this.setBackground(Color.CYAN);
        this.setVisible(true);
    }
}

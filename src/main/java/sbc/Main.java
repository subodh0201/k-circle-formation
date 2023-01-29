package sbc;

import sbc.gui.App;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new App("K Circle Formation"));
    }
}
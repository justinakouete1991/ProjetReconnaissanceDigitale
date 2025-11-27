package org.example;

import org.example.ui.MainWindow;

import javax.swing.*;

public class Main {
    MainWindow appMainWindow = new MainWindow();

    public void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            appMainWindow.setVisible(true);
        });
    }
}

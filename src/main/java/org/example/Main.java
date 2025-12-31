package org.example;

import org.example.core.FingerprintProcessor;
import org.example.ui.MainWindow;

import javax.swing.*;

public class Main {
    MainWindow appMainWindow = new MainWindow();
    FingerprintProcessor fingerprintProcessor = new FingerprintProcessor(appMainWindow);

    public void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            appMainWindow.storeFingerprintProcessor(fingerprintProcessor);
            appMainWindow.setVisible(true);
        });
    }
}

package org.example.ui;

import ij.ImagePlus;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class MainWindow extends JFrame {

    private final MainPanel mainPanel = new MainPanel(new GridLayout(1, 2, 10, 0));
    private final TopPanel topPanel = new TopPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
    private final LogsArea logsArea = mainPanel.getLogsArea();

    /**
     * Constructeur
     */

    public MainWindow() {
        setTitle("FingerRecognition Interface");
        setLayout(new BorderLayout(5, 5));
        getContentPane().setBackground(Color.WHITE);

        // =============== PANNEAU SUPÉRIEUR ===============
        topPanel.setBackground(Color.WHITE);

        // =============== PANNEAU PRINCIPAL ===============
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // =============== ASSEMBLAGE ===============
        add(topPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);

        // =============== CONFIGURATION FENÊTRE ===============
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) (screenSize.width * 0.85);
        int height = (int) (screenSize.height * 0.90);

        setSize(width, height);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        logsArea.addLog("Application initialisée.");
    }

    /* Fonctions privées */

    public LogsArea getLogsArea() {
        return logsArea;
    }

    public void enableWindow() {
        topPanel.enableTopPanel();
        mainPanel.enableMainPanel();
    }
}

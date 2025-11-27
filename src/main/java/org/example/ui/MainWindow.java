package org.example.ui;

import ij.ImagePlus;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class MainWindow extends JFrame {

    MainPanel mainPanel = new MainPanel(new GridLayout(1, 2, 10, 0));

    /*private final ImagePanel imagePanel1 = new ImagePanel("Empreinte 1 - Non chargée");

    // =============== COMPOSANTS UI ===============
    private final ImagePanel imagePanel2 = new ImagePanel("Empreinte 2 - Non chargée");
    private final JButton loadBtn1 = new JButton("Charger Empreinte 1");
    private final JButton loadBtn2 = new JButton("Charger Empreinte 2");

    private final JTextArea logArea = new JTextArea(6, 50);
    // =============== DONNÉES IMAGES ===============
    private final BufferedImage img1 = null;
    private final BufferedImage img2 = null;
    private final ImagePlus imp1 = null;
    private final ImagePlus imp2 = null;
    private final String filePath1 = "";
    private final String filePath2 = "";
    private final boolean imagesProcessed = false;*/

    /**
     * Constructeur
     */

    public MainWindow() {
        setTitle("FingerRecognition Interface");
        setLayout(new BorderLayout(5, 5));
        getContentPane().setBackground(Color.WHITE);

        // =============== PANNEAU SUPÉRIEUR ===============
        TopPanel topPanel = new TopPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
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

        addLog("Application initialisée. Chargez deux empreintes pour effectuer la vérification.");
    }

    /* Fonctions privées */

    private void addLog(String message) {
        LogsArea logsArea = mainPanel.getLogsArea();
        logsArea.append(message + "\n");
        logsArea.setCaretPosition(logsArea.getDocument().getLength());
    }
}

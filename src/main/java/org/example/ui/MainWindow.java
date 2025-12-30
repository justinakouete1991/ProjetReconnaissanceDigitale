package org.example.ui;

import ij.ImagePlus;
import org.example.core.FingerprintProcessor;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class MainWindow extends JFrame {

    private final MainPanel mainPanel = new MainPanel(new GridLayout(1, 2, 10, 0));
    private final TopPanel topPanel = new TopPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
    private final LogsArea logsArea = mainPanel.getLogsArea();
    private FingerprintProcessor fingerprintProcessor;

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

        // =============== GESTIONNAIRES D'ÉVÉNEMENTS ===============
        setupEventHandlers();

        // =============== CONFIGURATION FENÊTRE ===============
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) (screenSize.width * 0.85);
        int height = (int) (screenSize.height * 0.90);

        setSize(width, height);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    /* Fonctions privées */

    public LogsArea getLogsArea() {
        return logsArea;
    }

    public void enableWindow() {
        topPanel.enableTopPanel();
        mainPanel.enableMainPanel();
    }

    public void storeFingerprintProcessor(FingerprintProcessor fingerprintProcessor){
        this.fingerprintProcessor = fingerprintProcessor;
    }

    /**
     * Configure les gestionnaires d'événements
     */
    private void setupEventHandlers(){
        JButton[] loadBtns = mainPanel.getMainPanelLoadBtns();
        loadBtns[0].addActionListener(e -> loadImage(1));
        loadBtns[1].addActionListener(e -> loadImage(2));
        topPanel.getVerificationBtn().addActionListener(e -> {
            String[] mainPanelImagesPaths = mainPanel.getMainPanelImagesPaths();
            for (String mainPanelImage : mainPanelImagesPaths) {
                if (mainPanelImage == null) {
                    JOptionPane.showMessageDialog(this,
                            "Sélectionnez deux empreintes avant de procéder à leur comparaison",
                            "Erreur", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
            fingerprintProcessor.processVerification(mainPanel.getMainPanelImages(), mainPanelImagesPaths, mainPanel.getImagePanels());
        });
    }

    public void loadImage(int imageNum){
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Sélectionner l'empreinte " + imageNum);
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Images (PNG, JPG, BMP, TIFF)", "png", "jpg", "jpeg", "bmp", "tif", "tiff"
        ));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
            File selectedFile = fc.getSelectedFile();
            //BufferedImage img = javax.imageio.ImageIO.read(selectedFile);
            //ImagePlus imp = new ImagePlus(selectedFile.getAbsolutePath(), img);
            String exceptionMessage = mainPanel.setImagesPanelImage(imageNum, selectedFile);
            if(exceptionMessage != null){
                // Il y a eu une erreur
                JOptionPane.showMessageDialog(this,
                        "Erreur lors du chargement : " + exceptionMessage,
                        "Erreur", JOptionPane.ERROR_MESSAGE
                );
                logsArea.addLog("Echec du chargement de l'empreinte " + imageNum);
            }else{
                // Pas d'erreur
                logsArea.addLog("Empreinte " + imageNum + " chargée : " + selectedFile.getName());
                //logsArea.addLog("Dimensions : " + );
            }
        }
    }
}

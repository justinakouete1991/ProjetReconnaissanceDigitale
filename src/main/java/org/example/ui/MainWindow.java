package org.example.ui;

import org.example.core.FingerprintProcessor;
import org.example.core.StepByStepProcessor;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class MainWindow extends JFrame {

    private final MainPanel mainPanel = new MainPanel(new GridLayout(1, 2, 10, 0));
    private final TopPanel topPanel = new TopPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
    private final ProcessingControlPanel controlPanel = new ProcessingControlPanel();

    private final LogsArea logsArea = mainPanel.getLogsArea();
    private FingerprintProcessor fingerprintProcessor;
    private StepByStepProcessor stepProcessor;

    /**
     * Constructeur
     */
    public MainWindow() {
        setTitle("FingerRecognition Interface - Prétraitement Étape par Étape");
        setLayout(new BorderLayout(5, 5));
        getContentPane().setBackground(Color.WHITE);

        // =============== PANNEAU SUPÉRIEUR ===============
        topPanel.setBackground(Color.WHITE);

        // =============== PANNEAU PRINCIPAL ===============
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // =============== PANNEAU DE CONTRÔLE ===============
        JScrollPane controlScroll = new JScrollPane(controlPanel);
        controlScroll.setPreferredSize(new Dimension(380, 0));
        controlScroll.setBorder(BorderFactory.createEmptyBorder());
        controlScroll.getVerticalScrollBar().setUnitIncrement(16);

        // =============== ASSEMBLAGE ===============
        add(topPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
        add(controlScroll, BorderLayout.EAST);

        // =============== GESTIONNAIRES D'ÉVÉNEMENTS ===============
        setupEventHandlers();

        // =============== CONFIGURATION FENÊTRE ===============
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) (screenSize.width * 0.90);
        int height = (int) (screenSize.height * 0.90);

        setSize(width, height);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        logsArea.addLog("═══════════════════════════════════════");
        logsArea.addLog("  FingerRecognition Interface v2.0");
        logsArea.addLog("  Mode : Prétraitement étape par étape");
        logsArea.addLog("═══════════════════════════════════════");
        logsArea.addLog("Application initialisée.");
    }

    /* Fonctions publiques */

    public LogsArea getLogsArea() {
        return logsArea;
    }

    public void enableWindow() {
        topPanel.enableTopPanel();
        mainPanel.enableMainPanel();
        controlPanel.enable();
        logsArea.addLog("Interface activée. Chargez deux empreintes pour commencer.");
    }

    public void storeFingerprintProcessor(FingerprintProcessor fingerprintProcessor) {
        this.fingerprintProcessor = fingerprintProcessor;
        this.stepProcessor = new StepByStepProcessor(logsArea);
    }

    /**
     * Configure les gestionnaires d'événements
     */
    private void setupEventHandlers() {
        // Boutons de chargement d'images
        JButton[] loadBtns = mainPanel.getMainPanelLoadBtns();
        loadBtns[0].addActionListener(e -> loadImage(1));
        loadBtns[1].addActionListener(e -> loadImage(2));

        // Bouton de comparaison (traitement complet classique)
        topPanel.getVerificationBtn().addActionListener(e -> {
            if (!checkImagesLoaded()) return;
            fingerprintProcessor.processVerification(
                    mainPanel.getMainPanelImages(),
                    mainPanel.getMainPanelImagesPaths(),
                    mainPanel.getImagePanels()
            );
        });

        // ========== BOUTONS DES ÉTAPES ==========

        controlPanel.getBtn1_Grayscale().addActionListener(e -> {
            int fingerprint = getSelectedFingerprint();
            if (fingerprint == 0) {
                stepProcessor.step1_ConvertToGrayscale(1);
                stepProcessor.step1_ConvertToGrayscale(2);
                controlPanel.completeStep(1);
            } else {
                stepProcessor.step1_ConvertToGrayscale(fingerprint);
                controlPanel.completeStep(1);
            }
        });

        controlPanel.getBtn2_Contrast().addActionListener(e -> {
            int fingerprint = getSelectedFingerprint();
            if (fingerprint == 0) {
                stepProcessor.step2_EnhanceContrast(1);
                stepProcessor.step2_EnhanceContrast(2);
                controlPanel.completeStep(2);
            } else {
                stepProcessor.step2_EnhanceContrast(fingerprint);
                controlPanel.completeStep(2);
            }
        });

        controlPanel.getBtn3_Normalization().addActionListener(e -> {
            int fingerprint = getSelectedFingerprint();
            if (fingerprint == 0) {
                stepProcessor.step3_Normalize(1);
                stepProcessor.step3_Normalize(2);
                controlPanel.completeStep(3);
            } else {
                stepProcessor.step3_Normalize(fingerprint);
                controlPanel.completeStep(3);
            }
        });

        controlPanel.getBtn4_Segmentation().addActionListener(e -> {
            int fingerprint = getSelectedFingerprint();
            if (fingerprint == 0) {
                stepProcessor.step4_Segment(1);
                stepProcessor.step4_Segment(2);
                controlPanel.completeStep(4);
            } else {
                stepProcessor.step4_Segment(fingerprint);
                controlPanel.completeStep(4);
            }
        });

        controlPanel.getBtn5_Orientation().addActionListener(e -> {
            int fingerprint = getSelectedFingerprint();
            if (fingerprint == 0) {
                stepProcessor.step5_EstimateOrientation(1);
                stepProcessor.step5_EstimateOrientation(2);
                controlPanel.completeStep(5);
            } else {
                stepProcessor.step5_EstimateOrientation(fingerprint);
                controlPanel.completeStep(5);
            }
        });

        controlPanel.getBtn6_Frequency().addActionListener(e -> {
            int fingerprint = getSelectedFingerprint();
            if (fingerprint == 0) {
                stepProcessor.step6_EstimateFrequency(1);
                stepProcessor.step6_EstimateFrequency(2);
                controlPanel.completeStep(6);
            } else {
                stepProcessor.step6_EstimateFrequency(fingerprint);
                controlPanel.completeStep(6);
            }
        });

        controlPanel.getBtn7_Gabor().addActionListener(e -> {
            int fingerprint = getSelectedFingerprint();
            if (fingerprint == 0) {
                stepProcessor.step7_ApplyGaborFilter(1);
                stepProcessor.step7_ApplyGaborFilter(2);
                controlPanel.completeStep(7);
            } else {
                stepProcessor.step7_ApplyGaborFilter(fingerprint);
                controlPanel.completeStep(7);
            }
        });

        controlPanel.getBtn8_Binarization().addActionListener(e -> {
            int fingerprint = getSelectedFingerprint();
            if (fingerprint == 0) {
                stepProcessor.step8_Binarize(1);
                stepProcessor.step8_Binarize(2);
                controlPanel.completeStep(8);
            } else {
                stepProcessor.step8_Binarize(fingerprint);
                controlPanel.completeStep(8);
            }
        });

        controlPanel.getBtn9_Thinning().addActionListener(e -> {
            int fingerprint = getSelectedFingerprint();
            if (fingerprint == 0) {
                stepProcessor.step9_Thin(1);
                stepProcessor.step9_Thin(2);
                controlPanel.completeStep(9);
            } else {
                stepProcessor.step9_Thin(fingerprint);
                controlPanel.completeStep(9);
            }
        });

        // ========== BOUTONS DE CONTRÔLE GLOBAUX ==========

        controlPanel.getBtnNextStep().addActionListener(e -> {
            int fingerprint = getSelectedFingerprint();
            if (fingerprint == 0) {
                stepProcessor.runNextStep(1);
                stepProcessor.runNextStep(2);
            } else {
                stepProcessor.runNextStep(fingerprint);
            }

            // Mise à jour de l'UI
            int currentStep = stepProcessor.getCurrentStep(fingerprint == 0 ? 1 : fingerprint);
            controlPanel.completeStep(currentStep);
        });

        controlPanel.getBtnRunAll().addActionListener(e -> {
            if (!checkImagesLoaded()) return;

            int fingerprint = getSelectedFingerprint();
            if (fingerprint == 0) {
                stepProcessor.runAllSteps(1);
                stepProcessor.runAllSteps(2);
            } else {
                stepProcessor.runAllSteps(fingerprint);
            }

            controlPanel.completeStep(9);
        });

        controlPanel.getBtnReset().addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(
                    this,
                    "Réinitialiser le prétraitement ?\nToutes les étapes seront annulées.",
                    "Confirmation",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (result == JOptionPane.YES_OPTION) {
                int fingerprint = getSelectedFingerprint();
                if (fingerprint == 0) {
                    stepProcessor.resetFingerprint(1);
                    stepProcessor.resetFingerprint(2);
                } else {
                    stepProcessor.resetFingerprint(fingerprint);
                }

                controlPanel.resetSteps();
                logsArea.addLog("\n══════════════════════════════════════");
                logsArea.addLog("Prétraitement réinitialisé");
                logsArea.addLog("══════════════════════════════════════\n");
            }
        });
    }

    /**
     * Charge une image d'empreinte
     */
    private void loadImage(int imageNum) {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Sélectionner l'empreinte " + imageNum);
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Images (PNG, JPG, BMP, TIFF)", "png", "jpg", "jpeg", "bmp", "tif", "tiff"
        ));

        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fc.getSelectedFile();
            String exceptionMessage = mainPanel.setImagesPanelImage(imageNum, selectedFile);

            if (exceptionMessage != null) {
                JOptionPane.showMessageDialog(
                        this,
                        "Erreur lors du chargement : " + exceptionMessage,
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE
                );
                logsArea.addLog("❌ Échec du chargement de l'empreinte " + imageNum);
            } else {
                logsArea.addLog("✓ Empreinte " + imageNum + " chargée : " + selectedFile.getName());

                // Initialiser le processeur étape par étape
                stepProcessor.initializeFingerprint(
                        imageNum,
                        mainPanel.getMainPanelImages()[imageNum - 1],
                        mainPanel.getMainPanelImagesPaths()[imageNum - 1],
                        mainPanel.getImagePanels()[imageNum - 1]
                );

                // Réinitialiser le panneau de contrôle si on recharge une image
                controlPanel.resetSteps();
            }
        }
    }

    /**
     * Vérifie que les deux images sont chargées
     */
    private boolean checkImagesLoaded() {
        String[] paths = mainPanel.getMainPanelImagesPaths();
        for (String path : paths) {
            if (path == null) {
                JOptionPane.showMessageDialog(
                        this,
                        "Veuillez charger les deux empreintes avant de continuer.",
                        "Images manquantes",
                        JOptionPane.WARNING_MESSAGE
                );
                return false;
            }
        }
        return true;
    }

    /**
     * Récupère l'empreinte sélectionnée dans le combo
     * @return 1 pour empreinte 1, 2 pour empreinte 2, 0 pour les deux
     */
    private int getSelectedFingerprint() {
        int selectedIndex = controlPanel.getFingerprintSelector().getSelectedIndex();

        if (selectedIndex == 0) return 1;      // Empreinte 1
        else if (selectedIndex == 1) return 2; // Empreinte 2
        else return 0;                         // Les deux
    }
}
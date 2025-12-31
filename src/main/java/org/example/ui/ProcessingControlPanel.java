package org.example.ui;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * Panneau de contrôle pour l'exécution étape par étape du prétraitement
 */
public class ProcessingControlPanel extends JPanel {
    
    // Boutons pour chaque étape
    private JButton btn1_Grayscale;
    private JButton btn2_Contrast;
    private JButton btn3_Normalization;
    private JButton btn4_Segmentation;
    private JButton btn5_Orientation;
    private JButton btn6_Frequency;
    private JButton btn7_Gabor;
    private JButton btn8_Binarization;
    private JButton btn9_Thinning;
    
    // Boutons de contrôle globaux
    private JButton btnRunAll;
    private JButton btnReset;
    private JButton btnNextStep;
    
    // Indicateur de l'étape courante
    private JLabel currentStepLabel;
    private int currentStep = 0;
    
    // ComboBox pour choisir l'empreinte à traiter
    private JComboBox<String> fingerprintSelector;
    
    public ProcessingControlPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(70, 130, 180), 2),
            "Contrôle du Prétraitement",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14),
            new Color(70, 130, 180)
        ));
        
        // Panneau supérieur : Sélection et info
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);
        
        // Panneau central : Boutons des étapes
        JPanel stepsPanel = createStepsPanel();
        add(stepsPanel, BorderLayout.CENTER);
        
        // Panneau inférieur : Contrôles globaux
        JPanel controlPanel = createControlPanel();
        add(controlPanel, BorderLayout.SOUTH);
        
        // Désactiver tous les boutons au départ
        disableAllButtons();
    }
    
    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panel.setBackground(Color.WHITE);
        
        JLabel selectLabel = new JLabel("Traiter :");
        selectLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        fingerprintSelector = new JComboBox<>(new String[]{
            "Empreinte 1",
            "Empreinte 2",
            "Les deux empreintes"
        });
        fingerprintSelector.setPreferredSize(new Dimension(180, 30));
        fingerprintSelector.setEnabled(false);
        
        currentStepLabel = new JLabel("Étape : 0/9");
        currentStepLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        currentStepLabel.setForeground(new Color(70, 130, 180));
        
        panel.add(selectLabel);
        panel.add(fingerprintSelector);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(currentStepLabel);
        
        return panel;
    }
    
    private JPanel createStepsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        Font buttonFont = new Font("Segoe UI", Font.PLAIN, 11);
        Dimension buttonSize = new Dimension(320, 32);
        
        // Création de tous les boutons d'étapes
        btn1_Grayscale = createStepButton("1. Conversion en niveaux de gris", buttonFont, buttonSize);
        btn2_Contrast = createStepButton("2. Amélioration du contraste", buttonFont, buttonSize);
        btn3_Normalization = createStepButton("3. Normalisation", buttonFont, buttonSize);
        btn4_Segmentation = createStepButton("4. Segmentation", buttonFont, buttonSize);
        btn5_Orientation = createStepButton("5. Estimation d'orientation", buttonFont, buttonSize);
        btn6_Frequency = createStepButton("6. Estimation de fréquence", buttonFont, buttonSize);
        btn7_Gabor = createStepButton("7. Filtrage de Gabor", buttonFont, buttonSize);
        btn8_Binarization = createStepButton("8. Binarisation", buttonFont, buttonSize);
        btn9_Thinning = createStepButton("9. Amincissement (Squelettisation)", buttonFont, buttonSize);
        
        // Ajout avec espacement
        panel.add(btn1_Grayscale);
        panel.add(Box.createVerticalStrut(5));
        panel.add(btn2_Contrast);
        panel.add(Box.createVerticalStrut(5));
        panel.add(btn3_Normalization);
        panel.add(Box.createVerticalStrut(5));
        panel.add(btn4_Segmentation);
        panel.add(Box.createVerticalStrut(5));
        panel.add(btn5_Orientation);
        panel.add(Box.createVerticalStrut(5));
        panel.add(btn6_Frequency);
        panel.add(Box.createVerticalStrut(5));
        panel.add(btn7_Gabor);
        panel.add(Box.createVerticalStrut(5));
        panel.add(btn8_Binarization);
        panel.add(Box.createVerticalStrut(5));
        panel.add(btn9_Thinning);
        
        return panel;
    }
    
    private JButton createStepButton(String text, Font font, Dimension size) {
        JButton button = new JButton(text);
        button.setFont(font);
        button.setPreferredSize(size);
        button.setMaximumSize(size);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setBackground(new Color(245, 245, 245));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return button;
    }
    
    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setBackground(Color.WHITE);
        
        Font controlFont = new Font("Segoe UI", Font.BOLD, 12);
        
        btnNextStep = new JButton("▶ Étape Suivante");
        btnNextStep.setFont(controlFont);
        btnNextStep.setBackground(new Color(70, 180, 130));
        btnNextStep.setForeground(Color.WHITE);
        btnNextStep.setFocusPainted(false);
        btnNextStep.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        
        btnRunAll = new JButton("▶▶ Tout Exécuter");
        btnRunAll.setFont(controlFont);
        btnRunAll.setBackground(new Color(70, 130, 180));
        btnRunAll.setForeground(Color.WHITE);
        btnRunAll.setFocusPainted(false);
        btnRunAll.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        
        btnReset = new JButton("↺ Réinitialiser");
        btnReset.setFont(controlFont);
        btnReset.setBackground(new Color(220, 100, 100));
        btnReset.setForeground(Color.WHITE);
        btnReset.setFocusPainted(false);
        btnReset.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        
        panel.add(btnNextStep);
        panel.add(btnRunAll);
        panel.add(btnReset);
        
        return panel;
    }
    
    /**
     * Active le panneau de contrôle
     */
    public void enable() {
        fingerprintSelector.setEnabled(true);
        btnRunAll.setEnabled(true);
        btnReset.setEnabled(true);
        btnNextStep.setEnabled(true);
        btn1_Grayscale.setEnabled(true);
        updateStepIndicator();
    }
    
    /**
     * Désactive tous les boutons
     */
    public void disableAllButtons() {
        fingerprintSelector.setEnabled(false);
        btn1_Grayscale.setEnabled(false);
        btn2_Contrast.setEnabled(false);
        btn3_Normalization.setEnabled(false);
        btn4_Segmentation.setEnabled(false);
        btn5_Orientation.setEnabled(false);
        btn6_Frequency.setEnabled(false);
        btn7_Gabor.setEnabled(false);
        btn8_Binarization.setEnabled(false);
        btn9_Thinning.setEnabled(false);
        btnRunAll.setEnabled(false);
        btnReset.setEnabled(false);
        btnNextStep.setEnabled(false);
    }
    
    /**
     * Marque une étape comme complétée et active la suivante
     */
    public void completeStep(int stepNumber) {
        currentStep = stepNumber;
        
        // Marquer l'étape complétée en vert
        JButton completedButton = getButtonForStep(stepNumber);
        if (completedButton != null) {
            completedButton.setBackground(new Color(180, 230, 180));
            completedButton.setEnabled(true); // Permettre de rejouer l'étape
        }
        
        // Activer le bouton de l'étape suivante
        if (stepNumber < 9) {
            JButton nextButton = getButtonForStep(stepNumber + 1);
            if (nextButton != null) {
                nextButton.setEnabled(true);
                nextButton.setBackground(new Color(255, 250, 205)); // Jaune clair
            }
        }
        
        updateStepIndicator();
    }
    
    /**
     * Réinitialise toutes les étapes
     */
    public void resetSteps() {
        currentStep = 0;
        
        JButton[] allButtons = {btn1_Grayscale, btn2_Contrast, btn3_Normalization,
                                btn4_Segmentation, btn5_Orientation, btn6_Frequency,
                                btn7_Gabor, btn8_Binarization, btn9_Thinning};
        
        for (JButton btn : allButtons) {
            btn.setBackground(new Color(245, 245, 245));
            btn.setEnabled(false);
        }
        
        btn1_Grayscale.setEnabled(true);
        btn1_Grayscale.setBackground(new Color(255, 250, 205));
        
        updateStepIndicator();
    }
    
    /**
     * Met à jour l'indicateur d'étape courante
     */
    private void updateStepIndicator() {
        currentStepLabel.setText("Étape : " + currentStep + "/9");
        
        if (currentStep == 9) {
            currentStepLabel.setForeground(new Color(50, 180, 50));
            currentStepLabel.setText("✓ Prétraitement terminé (9/9)");
        } else if (currentStep > 0) {
            currentStepLabel.setForeground(new Color(70, 130, 180));
        } else {
            currentStepLabel.setForeground(Color.GRAY);
        }
    }
    
    /**
     * Récupère le bouton correspondant à une étape
     */
    private JButton getButtonForStep(int step) {
        switch (step) {
            case 1: return btn1_Grayscale;
            case 2: return btn2_Contrast;
            case 3: return btn3_Normalization;
            case 4: return btn4_Segmentation;
            case 5: return btn5_Orientation;
            case 6: return btn6_Frequency;
            case 7: return btn7_Gabor;
            case 8: return btn8_Binarization;
            case 9: return btn9_Thinning;
            default: return null;
        }
    }
    
    // ========== GETTERS ==========
    
    public JButton getBtn1_Grayscale() { return btn1_Grayscale; }
    public JButton getBtn2_Contrast() { return btn2_Contrast; }
    public JButton getBtn3_Normalization() { return btn3_Normalization; }
    public JButton getBtn4_Segmentation() { return btn4_Segmentation; }
    public JButton getBtn5_Orientation() { return btn5_Orientation; }
    public JButton getBtn6_Frequency() { return btn6_Frequency; }
    public JButton getBtn7_Gabor() { return btn7_Gabor; }
    public JButton getBtn8_Binarization() { return btn8_Binarization; }
    public JButton getBtn9_Thinning() { return btn9_Thinning; }
    
    public JButton getBtnRunAll() { return btnRunAll; }
    public JButton getBtnReset() { return btnReset; }
    public JButton getBtnNextStep() { return btnNextStep; }
    
    public JComboBox<String> getFingerprintSelector() { return fingerprintSelector; }
    
    public int getCurrentStep() { return currentStep; }
}

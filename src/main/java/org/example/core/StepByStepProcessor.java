package org.example.core;

import ij.ImagePlus;
import ij.process.ImageProcessor;
import org.example.preprocessing.*;
import org.example.ui.ImagePanel;
import org.example.ui.LogsArea;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * Gestionnaire pour l'exécution étape par étape du prétraitement
 * Conserve l'état entre chaque étape
 */
public class StepByStepProcessor {
    
    private LogsArea logsArea;
    
    // États intermédiaires pour chaque empreinte
    private Map<Integer, ProcessingState> fingerprintStates;
    
    // Paramètres du pipeline
    private static final double NORMALIZATION_MEAN = 128.0;
    private static final double NORMALIZATION_VARIANCE = 2500.0;
    private static final int SEGMENTATION_BLOCK_SIZE = 16;
    private static final int ORIENTATION_BLOCK_SIZE = 16;
    private static final int FREQUENCY_BLOCK_SIZE = 16;
    private static final double GABOR_SIGMA_X = 2.0;
    private static final double GABOR_SIGMA_Y = 2.0;
    private static final int ADAPTIVE_BINARIZATION_BLOCK_SIZE = 32;
    
    /**
     * Classe interne pour stocker l'état du traitement
     */
    private static class ProcessingState {
        BufferedImage originalImage;
        String imagePath;
        ImagePanel imagePanel;
        
        // Images intermédiaires
        ImagePlus currentImage;
        ImagePlus normalized;
        ImagePlus segmented;
        ImagePlus oriented;
        ImagePlus frequencies;
        ImagePlus gaborFiltered;
        ImagePlus binarized;
        
        int currentStep = 0;
        
        ProcessingState(BufferedImage img, String path, ImagePanel panel) {
            this.originalImage = img;
            this.imagePath = path;
            this.imagePanel = panel;
            this.currentImage = new ImagePlus(path, img);
        }
    }
    
    public StepByStepProcessor(LogsArea logsArea) {
        this.logsArea = logsArea;
        this.fingerprintStates = new HashMap<>();
    }
    
    /**
     * Initialise le traitement pour une empreinte
     */
    public void initializeFingerprint(int fingerprintNumber, BufferedImage image, 
                                     String imagePath, ImagePanel imagePanel) {
        ProcessingState state = new ProcessingState(image, imagePath, imagePanel);
        fingerprintStates.put(fingerprintNumber, state);
        logsArea.addLog("Empreinte " + fingerprintNumber + " chargée et prête pour le prétraitement");
    }
    
    /**
     * Réinitialise une empreinte à son état initial
     */
    public void resetFingerprint(int fingerprintNumber) {
        ProcessingState state = fingerprintStates.get(fingerprintNumber);
        if (state == null) return;
        
        state.currentImage = new ImagePlus(state.imagePath, state.originalImage);
        state.normalized = null;
        state.segmented = null;
        state.oriented = null;
        state.frequencies = null;
        state.gaborFiltered = null;
        state.binarized = null;
        state.currentStep = 0;
        
        state.imagePanel.showProcessedImage(state.originalImage);
        logsArea.addLog("Empreinte " + fingerprintNumber + " réinitialisée");
    }
    
    /**
     * Exécute l'étape 1 : Conversion en niveaux de gris
     */
    public void step1_ConvertToGrayscale(int fingerprintNumber) {
        ProcessingState state = fingerprintStates.get(fingerprintNumber);
        if (state == null) {
            logsArea.addLog("❌ Erreur : Empreinte " + fingerprintNumber + " non initialisée");
            return;
        }
        
        try {
            logsArea.addLog("\n[Empreinte " + fingerprintNumber + "] Étape 1/9 : Conversion en niveaux de gris");
            
            ImageProcessor ip = state.currentImage.getProcessor();
            
            if (state.currentImage.getBitDepth() != 8) {
                ip = ip.convertToByte(true);
                state.currentImage.setProcessor(ip);
                state.imagePanel.showProcessedImage(state.currentImage.getBufferedImage());
                logsArea.addLog("✓ Conversion effectuée (8-bit)");
            } else {
                logsArea.addLog("✓ Image déjà en 8-bit, aucune conversion nécessaire");
            }
            
            state.currentStep = 1;
            
        } catch (Exception e) {
            logsArea.addLog("❌ Erreur étape 1 : " + e.getMessage());
        }
    }
    
    /**
     * Exécute l'étape 2 : Amélioration du contraste
     */
    public void step2_EnhanceContrast(int fingerprintNumber) {
        ProcessingState state = fingerprintStates.get(fingerprintNumber);
        if (state == null || state.currentStep < 1) {
            logsArea.addLog("❌ Erreur : Exécutez d'abord l'étape 1");
            return;
        }
        
        try {
            logsArea.addLog("\n[Empreinte " + fingerprintNumber + "] Étape 2/9 : Amélioration du contraste");
            
            ImageProcessor ip = state.currentImage.getProcessor();
            ip.setMinAndMax(0, 255);
            ip.resetMinAndMax();
            
            state.imagePanel.showProcessedImage(state.currentImage.getBufferedImage());
            logsArea.addLog("✓ Contraste amélioré (normalisation histogramme)");
            
            state.currentStep = 2;
            
        } catch (Exception e) {
            logsArea.addLog("❌ Erreur étape 2 : " + e.getMessage());
        }
    }
    
    /**
     * Exécute l'étape 3 : Normalisation
     */
    public void step3_Normalize(int fingerprintNumber) {
        ProcessingState state = fingerprintStates.get(fingerprintNumber);
        if (state == null || state.currentStep < 2) {
            logsArea.addLog("❌ Erreur : Exécutez d'abord les étapes précédentes");
            return;
        }
        
        try {
            logsArea.addLog("\n[Empreinte " + fingerprintNumber + "] Étape 3/9 : Normalisation");
            logsArea.addLog("  Paramètres : moyenne=" + NORMALIZATION_MEAN + ", variance=" + NORMALIZATION_VARIANCE);
            
            Normalization normalization = new Normalization(NORMALIZATION_MEAN, NORMALIZATION_VARIANCE);
            state.normalized = normalization.normalize(state.currentImage);
            state.currentImage = state.normalized;
            
            state.imagePanel.showProcessedImage(state.normalized.getBufferedImage());
            logsArea.addLog("✓ Normalisation effectuée");
            
            state.currentStep = 3;
            
        } catch (Exception e) {
            logsArea.addLog("❌ Erreur étape 3 : " + e.getMessage());
        }
    }
    
    /**
     * Exécute l'étape 4 : Segmentation
     */
    public void step4_Segment(int fingerprintNumber) {
        ProcessingState state = fingerprintStates.get(fingerprintNumber);
        if (state == null || state.currentStep < 3) {
            logsArea.addLog("❌ Erreur : Exécutez d'abord les étapes précédentes");
            return;
        }
        
        try {
            logsArea.addLog("\n[Empreinte " + fingerprintNumber + "] Étape 4/9 : Segmentation");
            logsArea.addLog("  Taille de bloc : " + SEGMENTATION_BLOCK_SIZE + " pixels");
            
            Segmentation segmentation = new Segmentation(SEGMENTATION_BLOCK_SIZE);
            state.segmented = segmentation.segment(state.normalized);
            
            // On affiche le masque de segmentation temporairement
            state.imagePanel.showProcessedImage(state.segmented.getBufferedImage());
            logsArea.addLog("✓ Segmentation effectuée (masque de qualité créé)");
            logsArea.addLog("  Note : Ce masque identifie les zones exploitables de l'empreinte");
            
            state.currentStep = 4;
            
        } catch (Exception e) {
            logsArea.addLog("❌ Erreur étape 4 : " + e.getMessage());
        }
    }
    
    /**
     * Exécute l'étape 5 : Estimation d'orientation
     */
    public void step5_EstimateOrientation(int fingerprintNumber) {
        ProcessingState state = fingerprintStates.get(fingerprintNumber);
        if (state == null || state.currentStep < 4) {
            logsArea.addLog("❌ Erreur : Exécutez d'abord les étapes précédentes");
            return;
        }
        
        try {
            logsArea.addLog("\n[Empreinte " + fingerprintNumber + "] Étape 5/9 : Estimation d'orientation");
            logsArea.addLog("  Taille de bloc : " + ORIENTATION_BLOCK_SIZE + " pixels");
            
            OrientationEstimation orientationEstimator = new OrientationEstimation(ORIENTATION_BLOCK_SIZE);
            state.oriented = orientationEstimator.estimateOrientation(state.normalized);
            
            // Affichage du champ d'orientation
            state.imagePanel.showProcessedImage(state.oriented.getBufferedImage());
            logsArea.addLog("✓ Champ d'orientation calculé");
            logsArea.addLog("  Note : Visualisation de la direction des crêtes");
            
            state.currentStep = 5;
            
        } catch (Exception e) {
            logsArea.addLog("❌ Erreur étape 5 : " + e.getMessage());
        }
    }
    
    /**
     * Exécute l'étape 6 : Estimation de fréquence
     */
    public void step6_EstimateFrequency(int fingerprintNumber) {
        ProcessingState state = fingerprintStates.get(fingerprintNumber);
        if (state == null || state.currentStep < 5) {
            logsArea.addLog("❌ Erreur : Exécutez d'abord les étapes précédentes");
            return;
        }
        
        try {
            logsArea.addLog("\n[Empreinte " + fingerprintNumber + "] Étape 6/9 : Estimation de fréquence");
            logsArea.addLog("  Taille de bloc : " + FREQUENCY_BLOCK_SIZE + " pixels");
            
            FrequencyEstimation frequencyEstimator = new FrequencyEstimation(FREQUENCY_BLOCK_SIZE);
            state.frequencies = frequencyEstimator.estimateFrequency(state.normalized, state.oriented);
            
            // Affichage de la carte de fréquences
            state.imagePanel.showProcessedImage(state.frequencies.getBufferedImage());
            logsArea.addLog("✓ Fréquences locales estimées");
            logsArea.addLog("  Note : Carte de densité des crêtes");
            
            state.currentStep = 6;
            
        } catch (Exception e) {
            logsArea.addLog("❌ Erreur étape 6 : " + e.getMessage());
        }
    }
    
    /**
     * Exécute l'étape 7 : Filtrage de Gabor
     */
    public void step7_ApplyGaborFilter(int fingerprintNumber) {
        ProcessingState state = fingerprintStates.get(fingerprintNumber);
        if (state == null || state.currentStep < 6) {
            logsArea.addLog("❌ Erreur : Exécutez d'abord les étapes précédentes");
            return;
        }
        
        try {
            logsArea.addLog("\n[Empreinte " + fingerprintNumber + "] Étape 7/9 : Filtrage de Gabor");
            logsArea.addLog("  Paramètres : σx=" + GABOR_SIGMA_X + ", σy=" + GABOR_SIGMA_Y);
            
            GaborFiltering gaborFilter = new GaborFiltering(GABOR_SIGMA_X, GABOR_SIGMA_Y);
            state.gaborFiltered = gaborFilter.filter(state.normalized, state.oriented, state.frequencies);
            state.currentImage = state.gaborFiltered;
            
            state.imagePanel.showProcessedImage(state.gaborFiltered.getBufferedImage());
            logsArea.addLog("✓ Filtrage de Gabor appliqué");
            logsArea.addLog("  Note : Les crêtes sont maintenant bien définies");
            
            state.currentStep = 7;
            
        } catch (Exception e) {
            logsArea.addLog("❌ Erreur étape 7 : " + e.getMessage());
        }
    }
    
    /**
     * Exécute l'étape 8 : Binarisation
     */
    public void step8_Binarize(int fingerprintNumber) {
        ProcessingState state = fingerprintStates.get(fingerprintNumber);
        if (state == null || state.currentStep < 7) {
            logsArea.addLog("❌ Erreur : Exécutez d'abord les étapes précédentes");
            return;
        }
        
        try {
            logsArea.addLog("\n[Empreinte " + fingerprintNumber + "] Étape 8/9 : Binarisation");
            
            Binarization binarizer = new Binarization();
            
            // Choix entre binarisation globale ou adaptative
            boolean useAdaptive = shouldUseAdaptiveBinarization(state.gaborFiltered);
            
            if (useAdaptive) {
                logsArea.addLog("  Mode : Binarisation adaptative (blocs de " + ADAPTIVE_BINARIZATION_BLOCK_SIZE + "px)");
                state.binarized = binarizer.adaptiveBinarize(state.gaborFiltered, ADAPTIVE_BINARIZATION_BLOCK_SIZE);
            } else {
                logsArea.addLog("  Mode : Binarisation globale (méthode d'Otsu)");
                state.binarized = binarizer.binarize(state.gaborFiltered);
            }
            
            state.currentImage = state.binarized;
            state.imagePanel.showProcessedImage(state.binarized.getBufferedImage());
            
            // Statistiques
            int[] stats = calculateBinaryStats(state.binarized);
            logsArea.addLog("✓ Binarisation effectuée");
            logsArea.addLog("  Pixels noirs (crêtes) : " + stats[0] + " (" + stats[2] + "%)");
            logsArea.addLog("  Pixels blancs (vallées) : " + stats[1] + " (" + (100-stats[2]) + "%)");
            
            state.currentStep = 8;
            
        } catch (Exception e) {
            logsArea.addLog("❌ Erreur étape 8 : " + e.getMessage());
        }
    }
    
    /**
     * Exécute l'étape 9 : Amincissement (Squelettisation)
     */
    public void step9_Thin(int fingerprintNumber) {
        ProcessingState state = fingerprintStates.get(fingerprintNumber);
        if (state == null || state.currentStep < 8) {
            logsArea.addLog("❌ Erreur : Exécutez d'abord les étapes précédentes");
            return;
        }
        
        try {
            logsArea.addLog("\n[Empreinte " + fingerprintNumber + "] Étape 9/9 : Amincissement");
            logsArea.addLog("  Algorithme : Zhang-Suen");
            
            Thinning thinner = new Thinning();
            ImagePlus thinned = thinner.thin(state.binarized);
            state.currentImage = thinned;
            
            state.imagePanel.showProcessedImage(thinned.getBufferedImage());
            
            logsArea.addLog("✓ Amincissement effectué (squelette extrait)");
            logsArea.addLog("════════════════════════════════════════");
            logsArea.addLog("✓✓ PRÉTRAITEMENT TERMINÉ POUR L'EMPREINTE " + fingerprintNumber);
            logsArea.addLog("════════════════════════════════════════");
            
            state.currentStep = 9;
            
        } catch (Exception e) {
            logsArea.addLog("❌ Erreur étape 9 : " + e.getMessage());
        }
    }
    
    /**
     * Exécute toutes les étapes d'un coup
     */
    public void runAllSteps(int fingerprintNumber) {
        logsArea.addLog("\n╔════════════════════════════════════════╗");
        logsArea.addLog("║  EXÉCUTION COMPLÈTE - EMPREINTE " + fingerprintNumber + "     ║");
        logsArea.addLog("╚════════════════════════════════════════╝");
        
        step1_ConvertToGrayscale(fingerprintNumber);
        step2_EnhanceContrast(fingerprintNumber);
        step3_Normalize(fingerprintNumber);
        step4_Segment(fingerprintNumber);
        step5_EstimateOrientation(fingerprintNumber);
        step6_EstimateFrequency(fingerprintNumber);
        step7_ApplyGaborFilter(fingerprintNumber);
        step8_Binarize(fingerprintNumber);
        step9_Thin(fingerprintNumber);
    }
    
    /**
     * Exécute l'étape suivante
     */
    public void runNextStep(int fingerprintNumber) {
        ProcessingState state = fingerprintStates.get(fingerprintNumber);
        if (state == null) {
            logsArea.addLog("❌ Erreur : Empreinte " + fingerprintNumber + " non initialisée");
            return;
        }
        
        int nextStep = state.currentStep + 1;
        
        switch (nextStep) {
            case 1: step1_ConvertToGrayscale(fingerprintNumber); break;
            case 2: step2_EnhanceContrast(fingerprintNumber); break;
            case 3: step3_Normalize(fingerprintNumber); break;
            case 4: step4_Segment(fingerprintNumber); break;
            case 5: step5_EstimateOrientation(fingerprintNumber); break;
            case 6: step6_EstimateFrequency(fingerprintNumber); break;
            case 7: step7_ApplyGaborFilter(fingerprintNumber); break;
            case 8: step8_Binarize(fingerprintNumber); break;
            case 9: step9_Thin(fingerprintNumber); break;
            default:
                logsArea.addLog("Toutes les étapes ont déjà été exécutées");
        }
    }
    
    // ========== MÉTHODES UTILITAIRES ==========
    
    private boolean shouldUseAdaptiveBinarization(ImagePlus img) {
        ImageProcessor ip = img.getProcessor();
        int width = ip.getWidth();
        int height = ip.getHeight();
        
        double sum = 0, sumSq = 0;
        int totalPixels = width * height;
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double pixel = ip.getPixel(x, y);
                sum += pixel;
                sumSq += pixel * pixel;
            }
        }
        
        double mean = sum / totalPixels;
        double variance = (sumSq / totalPixels) - (mean * mean);
        
        return variance > 3000;
    }
    
    private int[] calculateBinaryStats(ImagePlus img) {
        ImageProcessor ip = img.getProcessor();
        int width = ip.getWidth();
        int height = ip.getHeight();
        
        int blackPixels = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (ip.getPixel(x, y) < 128) blackPixels++;
            }
        }
        
        int whitePixels = width * height - blackPixels;
        int blackPercent = (int)((double)blackPixels / (width * height) * 100);
        
        return new int[]{blackPixels, whitePixels, blackPercent};
    }
    
    public int getCurrentStep(int fingerprintNumber) {
        ProcessingState state = fingerprintStates.get(fingerprintNumber);
        return (state != null) ? state.currentStep : 0;
    }
}

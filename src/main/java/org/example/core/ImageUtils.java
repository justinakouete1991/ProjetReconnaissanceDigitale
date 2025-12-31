package org.example.core;

import ij.ImagePlus;
import ij.process.ImageProcessor;
import org.example.preprocessing.*;
import org.example.ui.ImagePanel;
import org.example.ui.LogsArea;

import java.awt.image.BufferedImage;

/**
 * Gestionnaire centralisé du prétraitement des empreintes digitales
 * Orchestre les différentes étapes de traitement de manière modulaire
 */
public class ImageUtils {
    private LogsArea logsArea;

    // Paramètres du pipeline (configurables)
    private static final double NORMALIZATION_MEAN = 128.0;
    private static final double NORMALIZATION_VARIANCE = 2500.0;
    private static final int SEGMENTATION_BLOCK_SIZE = 16;
    private static final int ORIENTATION_BLOCK_SIZE = 16;
    private static final int FREQUENCY_BLOCK_SIZE = 16;
    private static final double GABOR_SIGMA_X = 2.0;
    private static final double GABOR_SIGMA_Y = 2.0;
    private static final int ADAPTIVE_BINARIZATION_BLOCK_SIZE = 32;

    public ImageUtils(LogsArea logsArea) {
        this.logsArea = logsArea;
    }

    /**
     * Pipeline complet de prétraitement d'une empreinte digitale
     * @param fingerprint Image brute
     * @param fingerprintPath Chemin du fichier
     * @param imagePanel Panel d'affichage
     */
    public void preProcessFingerprint(BufferedImage fingerprint, String fingerprintPath, ImagePanel imagePanel) {
        try {
            ImagePlus impFingerprint = new ImagePlus(fingerprintPath, fingerprint);
            ImageProcessor ipFingerprint = impFingerprint.getProcessor();

            logsArea.addLog("═══════════════════════════════════════");

            // ========== ÉTAPE 1 : Conversion en niveaux de gris ==========
            if (impFingerprint.getBitDepth() != 8) {
                logsArea.addLog("[1/8] Conversion en 8-bit...");
                ipFingerprint = ipFingerprint.convertToByte(true);
                impFingerprint.setProcessor(ipFingerprint);
                imagePanel.showProcessedImage(impFingerprint.getBufferedImage());
                pauseForVisualization(300);
            } else {
                logsArea.addLog("[1/8] Image déjà en 8-bit ✓");
            }

            // ========== ÉTAPE 2 : Amélioration du contraste ==========
            logsArea.addLog("[2/8] Amélioration du contraste...");
            ipFingerprint.setMinAndMax(0, 255);
            ipFingerprint.resetMinAndMax();
            imagePanel.showProcessedImage(impFingerprint.getBufferedImage());
            pauseForVisualization(300);

            // ========== ÉTAPE 3 : Normalisation ==========
            logsArea.addLog("[3/8] Normalisation (moyenne: " + NORMALIZATION_MEAN + ", variance: " + NORMALIZATION_VARIANCE + ")...");
            Normalization normalization = new Normalization(NORMALIZATION_MEAN, NORMALIZATION_VARIANCE);
            ImagePlus normalized = normalization.normalize(impFingerprint);
            imagePanel.showProcessedImage(normalized.getBufferedImage());
            pauseForVisualization(400);

            // ========== ÉTAPE 4 : Segmentation ==========
            logsArea.addLog("[4/8] Segmentation (taille bloc: " + SEGMENTATION_BLOCK_SIZE + "px)...");
            Segmentation segmentation = new Segmentation(SEGMENTATION_BLOCK_SIZE);
            ImagePlus segmented = segmentation.segment(normalized);
            // On n'affiche pas la segmentation, c'est juste un masque interne
            logsArea.addLog("    → Zones récupérables identifiées");

            // ========== ÉTAPE 5 : Estimation d'orientation ==========
            logsArea.addLog("[5/8] Estimation d'orientation des crêtes...");
            OrientationEstimation orientationEstimator = new OrientationEstimation(ORIENTATION_BLOCK_SIZE);
            ImagePlus oriented = orientationEstimator.estimateOrientation(normalized);
            logsArea.addLog("    → Champ d'orientation calculé");

            // ========== ÉTAPE 6 : Estimation de fréquence ==========
            logsArea.addLog("[6/8] Estimation de fréquence des crêtes...");
            FrequencyEstimation frequencyEstimator = new FrequencyEstimation(FREQUENCY_BLOCK_SIZE);
            ImagePlus frequencies = frequencyEstimator.estimateFrequency(normalized, oriented);
            logsArea.addLog("    → Fréquences locales estimées");

            // ========== ÉTAPE 7 : Filtrage de Gabor ==========
            logsArea.addLog("[7/8] Filtrage de Gabor (amélioration des crêtes)...");
            GaborFiltering gaborFilter = new GaborFiltering(GABOR_SIGMA_X, GABOR_SIGMA_Y);
            ImagePlus gaborFiltered = gaborFilter.filter(normalized, oriented, frequencies);
            imagePanel.showProcessedImage(gaborFiltered.getBufferedImage());
            pauseForVisualization(500);

            // ========== ÉTAPE 8 : Binarisation ==========
            logsArea.addLog("[8/8] Binarisation adaptative (Otsu)...");
            Binarization binarizer = new Binarization();
            ImagePlus binarized;

            // Choix entre binarisation globale ou adaptative
            boolean useAdaptiveBinarization = shouldUseAdaptiveBinarization(gaborFiltered);
            if (useAdaptiveBinarization) {
                logsArea.addLog("    → Mode adaptatif (blocs de " + ADAPTIVE_BINARIZATION_BLOCK_SIZE + "px)");
                binarized = binarizer.adaptiveBinarize(gaborFiltered, ADAPTIVE_BINARIZATION_BLOCK_SIZE);
            } else {
                logsArea.addLog("    → Mode global (Otsu)");
                binarized = binarizer.binarize(gaborFiltered);
            }
            imagePanel.showProcessedImage(binarized.getBufferedImage());
            pauseForVisualization(500);

            // ========== ÉTAPE 9 : Amincissement (squelettisation) ==========
            logsArea.addLog("[BONUS] Amincissement des crêtes...");
            Thinning thinner = new Thinning();
            ImagePlus thinned = thinner.thin(binarized);
            imagePanel.showProcessedImage(thinned.getBufferedImage());

            // ========== RÉSUMÉ ==========
            logsArea.addLog("═══════════════════════════════════════");
            logsArea.addLog("✓ Prétraitement terminé avec succès");
            displayImageStatistics(thinned);
            logsArea.addLog("═══════════════════════════════════════");

        } catch (Exception e) {
            logsArea.addLog("✗ ERREUR lors du prétraitement : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Détermine si la binarisation adaptative est nécessaire
     * (basée sur la variance globale de l'image)
     */
    private boolean shouldUseAdaptiveBinarization(ImagePlus img) {
        ImageProcessor ip = img.getProcessor();
        int width = ip.getWidth();
        int height = ip.getHeight();

        // Calcul de la variance globale
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

        // Si variance > 3000, l'image a des zones très contrastées → binarisation adaptative
        return variance > 3000;
    }

    /**
     * Affiche des statistiques sur l'image traitée
     */
    private void displayImageStatistics(ImagePlus img) {
        ImageProcessor ip = img.getProcessor();
        int width = ip.getWidth();
        int height = ip.getHeight();

        int blackPixels = 0, whitePixels = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (ip.getPixel(x, y) < 128) {
                    blackPixels++;
                } else {
                    whitePixels++;
                }
            }
        }

        double ridgeDensity = (double) whitePixels / (width * height) * 100;
        logsArea.addLog(String.format("    Densité de crêtes : %.1f%%", ridgeDensity));
        logsArea.addLog(String.format("    Dimensions : %d × %d px", width, height));
    }

    /**
     * Pause pour permettre la visualisation progressive
     * (optionnel, peut être retiré pour un traitement instantané)
     */
    private void pauseForVisualization(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Version simplifiée du prétraitement (sans visualisation intermédiaire)
     * Utile pour traiter plusieurs empreintes rapidement
     */
    public ImagePlus preProcessFingerprintQuick(BufferedImage fingerprint, String fingerprintPath) {
        ImagePlus impFingerprint = new ImagePlus(fingerprintPath, fingerprint);

        // Conversion en 8-bit
        if (impFingerprint.getBitDepth() != 8) {
            ImageProcessor ip = impFingerprint.getProcessor().convertToByte(true);
            impFingerprint.setProcessor(ip);
        }

        // Pipeline complet sans affichage
        Normalization normalization = new Normalization(NORMALIZATION_MEAN, NORMALIZATION_VARIANCE);
        ImagePlus normalized = normalization.normalize(impFingerprint);

        OrientationEstimation orientationEstimator = new OrientationEstimation(ORIENTATION_BLOCK_SIZE);
        ImagePlus oriented = orientationEstimator.estimateOrientation(normalized);

        FrequencyEstimation frequencyEstimator = new FrequencyEstimation(FREQUENCY_BLOCK_SIZE);
        ImagePlus frequencies = frequencyEstimator.estimateFrequency(normalized, oriented);

        GaborFiltering gaborFilter = new GaborFiltering(GABOR_SIGMA_X, GABOR_SIGMA_Y);
        ImagePlus gaborFiltered = gaborFilter.filter(normalized, oriented, frequencies);

        Binarization binarizer = new Binarization();
        ImagePlus binarized = binarizer.binarize(gaborFiltered);

        Thinning thinner = new Thinning();
        return thinner.thin(binarized);
    }
}
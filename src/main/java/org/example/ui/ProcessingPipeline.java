package org.example.ui;

import preprocessing.*;
import ij.ImagePlus;
import ij.process.ImageProcessor;
import javax.swing.JTextArea;

public class ProcessingPipeline {

    public static ImagePlus process(ImagePlus input, JTextArea logArea, String mode) {
        if (input == null) {
            logArea.append("❌ Erreur : Image d'entrée nulle\n");
            return null;
        }

        ImagePlus result = null;

        try {
            if ("Pipeline Complet".equals(mode)) {
                result = fullPipeline(input, logArea);
            } else if ("Étape par Étape".equals(mode)) {
                result = stepByStepPipeline(input, logArea);
            } else if ("Test de Validation".equals(mode)) {
                result = testPipeline(input, logArea);
            } else {
                result = fullPipeline(input, logArea);
            }

            logArea.append("✅ Traitement terminé avec succès\n");

        } catch (Exception e) {
            logArea.append("❌ ERREUR dans le pipeline: " + e.getMessage() + "\n");
            e.printStackTrace();
        }

        return result;
    }

    private static ImagePlus fullPipeline(ImagePlus input, JTextArea logArea) {
        logArea.append("1. Normalisation...\n");
        Normalization normalization = new Normalization(128, 2500);
        ImagePlus normalized = normalization.normalize(input);

        logArea.append("2. Segmentation...\n");
        Segmentation segmentation = new Segmentation(16);
        ImagePlus segmented = segmentation.segment(normalized);

        logArea.append("3. Estimation d'orientation...\n");
        OrientationEstimation orientation = new OrientationEstimation(16);
        ImagePlus oriented = orientation.estimateOrientation(normalized);

        logArea.append("4. Estimation de fréquence...\n");
        FrequencyEstimation frequency = new FrequencyEstimation(16);
        ImagePlus frequencies = frequency.estimateFrequency(normalized, oriented);

        logArea.append("5. Filtrage Gabor...\n");
        GaborFiltering gabor = new GaborFiltering(2.0, 2.0);
        ImagePlus gaborFiltered = gabor.filter(normalized, oriented, frequencies);

        logArea.append("6. Binarisation (Otsu)...\n");
        Binarization binarization = new Binarization();
        ImagePlus binarized = binarization.binarize(gaborFiltered);

        logArea.append("7. Amincissement...\n");
        Thinning thinning = new Thinning();
        ImagePlus thinned = thinning.thin(binarized);

        return thinned;
    }

    private static ImagePlus stepByStepPipeline(ImagePlus input, JTextArea logArea) {
        // Cette version permet de voir chaque étape intermédiaire
        logArea.append("Mode étape par étape activé\n");

        logArea.append("Étape 1/7: Normalisation...\n");
        Normalization normalization = new Normalization(128, 2500);
        ImagePlus normalized = normalization.normalize(input);

        logArea.append("Étape 2/7: Segmentation...\n");
        Segmentation segmentation = new Segmentation(16);
        ImagePlus segmented = segmentation.segment(normalized);

        logArea.append("Étape 3/7: Estimation d'orientation...\n");
        OrientationEstimation orientation = new OrientationEstimation(16);
        ImagePlus oriented = orientation.estimateOrientation(normalized);

        logArea.append("Étape 4/7: Estimation de fréquence...\n");
        FrequencyEstimation frequency = new FrequencyEstimation(16);
        ImagePlus frequencies = frequency.estimateFrequency(normalized, oriented);

        logArea.append("Étape 5/7: Filtrage Gabor...\n");
        GaborFiltering gabor = new GaborFiltering(2.0, 2.0);
        ImagePlus gaborFiltered = gabor.filter(normalized, oriented, frequencies);

        logArea.append("Étape 6/7: Binarisation...\n");
        Binarization binarization = new Binarization();
        ImagePlus binarized = binarization.binarize(gaborFiltered);

        logArea.append("Étape 7/7: Amincissement...\n");
        Thinning thinning = new Thinning();
        ImagePlus thinned = thinning.thin(binarized);

        return thinned;
    }

    private static ImagePlus testPipeline(ImagePlus input, JTextArea logArea) {
        logArea.append("Mode test de validation activé\n");

        // Test simple : binarisation directe pour vérifier le chargement
        logArea.append("Test: Binarisation simple...\n");
        Binarization binarization = new Binarization();
        ImagePlus binarized = binarization.binarize(input);

        // Vérifier le résultat
        ImageProcessor ip = binarized.getProcessor();
        int blackPixels = 0, whitePixels = 0;

        for (int y = 0; y < ip.getHeight(); y++) {
            for (int x = 0; x < ip.getWidth(); x++) {
                if (ip.getPixel(x, y) < 128) blackPixels++;
                else whitePixels++;
            }
        }

        logArea.append(String.format("Résultat test: %d pixels noirs, %d pixels blancs\n",
                blackPixels, whitePixels));

        if (blackPixels == 0 || whitePixels == 0) {
            logArea.append("⚠️ Avertissement: L'image semble uniforme\n");
        } else {
            logArea.append("✅ Test de validation réussi\n");
        }

        return binarized;
    }
}
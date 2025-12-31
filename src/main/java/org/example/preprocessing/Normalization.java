package org.example.preprocessing;

import ij.ImagePlus;
import ij.process.ImageProcessor;

public class Normalization {
    private double targetMean;
    private double targetVariance;

    public Normalization(double targetMean, double targetVariance) {
        this.targetMean = targetMean;
        this.targetVariance = targetVariance;
    }

    public ImagePlus normalize(ImagePlus input) {
        ImageProcessor ip = input.getProcessor();
        int width = ip.getWidth();
        int height = ip.getHeight();

        // Calculer la moyenne et l'écart-type
        double sum = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                sum += ip.getPixel(x, y);
            }
        }
        double mean = sum / (width * height);

        double sumSq = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double diff = ip.getPixel(x, y) - mean;
                sumSq += diff * diff;
            }
        }
        double variance = sumSq / (width * height);
        double std = Math.sqrt(variance);

        // Éviter la division par zéro
        if (std < 1.0) std = 1.0;

        // Normaliser avec des paramètres plus adaptés
        ImageProcessor result = ip.duplicate();
        double targetStd = Math.sqrt(targetVariance);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double pixel = ip.getPixel(x, y);
                double normalized = targetMean + (pixel - mean) * (targetStd / std);

                // Limiter aux valeurs valides
                normalized = Math.max(0, Math.min(255, normalized));
                result.putPixel(x, y, (int) normalized);
            }
        }

        return new ImagePlus("Normalized", result);
    }
}
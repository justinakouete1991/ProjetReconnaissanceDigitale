package org.example.preprocessing;

import ij.ImagePlus;
import ij.process.ImageProcessor;

public class FrequencyEstimation {
    private int blockSize;

    public FrequencyEstimation(int blockSize) {
        this.blockSize = blockSize;
    }

    public ImagePlus estimateFrequency(ImagePlus input, ImagePlus orientation) {
        ImageProcessor ip = input.getProcessor();
        ImageProcessor op = orientation.getProcessor();
        int width = ip.getWidth();
        int height = ip.getHeight();

        ImageProcessor frequency = ip.createProcessor(width, height);

        // Pour les empreintes digitales, la fréquence est généralement autour de 1/8 à 1/12 pixel
        double defaultFrequency = 1.0 / 10.0; // 10 pixels entre les crêtes

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Estimation simple basée sur le contraste local
                double localFrequency = estimateLocalFrequency(ip, x, y);

                if (localFrequency <= 0) {
                    localFrequency = defaultFrequency;
                }

                // Convertir en valeur d'image (0-255)
                int freqValue = (int) (localFrequency * 100 * 255);
                freqValue = Math.max(0, Math.min(255, freqValue));
                frequency.putPixel(x, y, freqValue);
            }
        }

        return new ImagePlus("Frequency", frequency);
    }

    private double estimateLocalFrequency(ImageProcessor ip, int x, int y) {
        int windowSize = 16;
        int halfWindow = windowSize / 2;

        if (x < halfWindow || x >= ip.getWidth() - halfWindow ||
                y < halfWindow || y >= ip.getHeight() - halfWindow) {
            return 0.1; // Fréquence par défaut
        }

        // Compter les passages par zéro dans une ligne horizontale
        int zeroCrossings = 0;
        double prevValue = ip.getPixel(x - halfWindow, y) - 128;

        for (int dx = -halfWindow + 1; dx <= halfWindow; dx++) {
            double currentValue = ip.getPixel(x + dx, y) - 128;
            if (prevValue * currentValue < 0) {
                zeroCrossings++;
            }
            prevValue = currentValue;
        }

        // Nombre de crêtes = passages par zéro / 2
        double ridges = zeroCrossings / 2.0;

        // Fréquence = nombre de crêtes / largeur de la fenêtre
        return ridges / windowSize;
    }
}
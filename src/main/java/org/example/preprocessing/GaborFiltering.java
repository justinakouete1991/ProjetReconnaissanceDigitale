package org.example.preprocessing;

import ij.ImagePlus;
import ij.process.ImageProcessor;

public class GaborFiltering {
    private double sigmaX;
    private double sigmaY;

    public GaborFiltering(double sigmaX, double sigmaY) {
        this.sigmaX = sigmaX;
        this.sigmaY = sigmaY;
    }

    public ImagePlus filter(ImagePlus input, ImagePlus orientation, ImagePlus frequency) {
        ImageProcessor ip = input.getProcessor();
        ImageProcessor op = orientation.getProcessor();
        ImageProcessor fp = frequency.getProcessor();
        int width = ip.getWidth();
        int height = ip.getHeight();

        ImageProcessor result = ip.createProcessor(width, height);
        int filterSize = 11;
        int halfSize = filterSize / 2;

        // Paramètres par défaut si les images sont nulles
        if (op == null) {
            op = ip.duplicate();
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    op.putPixel(x, y, 128); // Orientation par défaut
                }
            }
        }

        if (fp == null) {
            fp = ip.duplicate();
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    fp.putPixel(x, y, 25); // Fréquence par défaut (0.1 * 255)
                }
            }
        }

        for (int y = halfSize; y < height - halfSize; y++) {
            for (int x = halfSize; x < width - halfSize; x++) {
                // Convertir l'orientation de [0,255] à [-π/2, π/2]
                double orientationAngle = (op.getPixel(x, y) * Math.PI / 255.0) - (Math.PI / 2.0);

                // Convertir la fréquence de [0,255] à une valeur réelle
                double freqValue = fp.getPixel(x, y) / 255.0;
                double localFreq = 0.05 + freqValue * 0.15; // Entre 0.05 et 0.2

                double sum = 0;
                double weightSum = 0;

                // Appliquer le filtre Gabor
                for (int ky = -halfSize; ky <= halfSize; ky++) {
                    for (int kx = -halfSize; kx <= halfSize; kx++) {
                        // Coordonnées tournées selon l'orientation
                        double xPrime = kx * Math.cos(orientationAngle) + ky * Math.sin(orientationAngle);
                        double yPrime = -kx * Math.sin(orientationAngle) + ky * Math.cos(orientationAngle);

                        // Composante gaussienne
                        double gaussian = Math.exp(-0.5 * (
                                (xPrime * xPrime) / (sigmaX * sigmaX) +
                                        (yPrime * yPrime) / (sigmaY * sigmaY)
                        ));

                        // Composante sinusoïdale
                        double sinusoid = Math.cos(2.0 * Math.PI * localFreq * xPrime);

                        double gabor = gaussian * sinusoid;

                        int pixelX = x + kx;
                        int pixelY = y + ky;

                        if (pixelX >= 0 && pixelX < width && pixelY >= 0 && pixelY < height) {
                            double pixelValue = ip.getPixel(pixelX, pixelY);
                            sum += gabor * pixelValue;
                            weightSum += Math.abs(gabor);
                        }
                    }
                }

                // Normaliser et stocker le résultat
                if (weightSum > 0) {
                    double filteredValue = sum / weightSum;
                    // Renforcer le contraste
                    filteredValue = 128 + (filteredValue - 128) * 1.5;
                    filteredValue = Math.max(0, Math.min(255, filteredValue));
                    result.putPixel(x, y, (int) filteredValue);
                } else {
                    result.putPixel(x, y, ip.getPixel(x, y));
                }
            }
        }

        // Remplir les bords
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (result.getPixel(x, y) == 0) {
                    result.putPixel(x, y, ip.getPixel(x, y));
                }
            }
        }

        return new ImagePlus("Gabor Filtered", result);
    }
}
package org.example.preprocessing;

import ij.ImagePlus;
import ij.process.ImageProcessor;

public class Binarization {

    public ImagePlus binarize(ImagePlus input) {
        ImageProcessor ip = input.getProcessor().duplicate();
        int width = ip.getWidth();
        int height = ip.getHeight();

        // Calcul de l'histogramme
        int[] histogram = new int[256];
        int totalPixels = width * height;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = ip.getPixel(x, y);
                histogram[pixel]++;
            }
        }

        // Calcul du seuil optimal avec la méthode d'Otsu
        double sum = 0;
        for (int i = 0; i < 256; i++) {
            sum += i * histogram[i];
        }

        double sumB = 0;
        int wB = 0;
        int wF;
        double maxVariance = 0;
        int threshold = 128;

        for (int i = 0; i < 256; i++) {
            wB += histogram[i];
            if (wB == 0) continue;

            wF = totalPixels - wB;
            if (wF == 0) break;

            sumB += i * histogram[i];

            double mB = sumB / wB;
            double mF = (sum - sumB) / wF;

            // Variance entre les classes
            double varianceBetween = wB * wF * (mB - mF) * (mB - mF);

            if (varianceBetween > maxVariance) {
                maxVariance = varianceBetween;
                threshold = i;
            }
        }

        // Ajuster le seuil pour les empreintes digitales
        threshold = Math.max(50, Math.min(200, threshold));

        // Appliquer la binarisation
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = ip.getPixel(x, y);
                // Inverser: crêtes sombres -> 0 (noir), vallées claires -> 255 (blanc)
                ip.putPixel(x, y, pixel < threshold ? 0 : 255);
            }
        }

        // Post-traitement: éliminer le bruit avec un filtre médian
        ip.medianFilter();

        return new ImagePlus("Binarized", ip);
    }

    // Méthode alternative avec seuil adaptatif local
    public ImagePlus adaptiveBinarize(ImagePlus input, int blockSize) {
        ImageProcessor ip = input.getProcessor().duplicate();
        int width = ip.getWidth();
        int height = ip.getHeight();

        ImageProcessor result = ip.createProcessor(width, height);

        for (int y = 0; y < height; y += blockSize) {
            for (int x = 0; x < width; x += blockSize) {
                // Calculer le seuil local pour chaque bloc
                int localThreshold = computeLocalThreshold(ip, x, y, blockSize);

                // Appliquer le seuil au bloc
                for (int dy = 0; dy < blockSize && (y + dy) < height; dy++) {
                    for (int dx = 0; dx < blockSize && (x + dx) < width; dx++) {
                        int pixel = ip.getPixel(x + dx, y + dy);
                        result.putPixel(x + dx, y + dy, pixel < localThreshold ? 0 : 255);
                    }
                }
            }
        }

        return new ImagePlus("Adaptive Binarized", result);
    }

    private int computeLocalThreshold(ImageProcessor ip, int startX, int startY, int size) {
        int sum = 0;
        int count = 0;

        int endX = Math.min(startX + size, ip.getWidth());
        int endY = Math.min(startY + size, ip.getHeight());

        for (int y = startY; y < endY; y++) {
            for (int x = startX; x < endX; x++) {
                sum += ip.getPixel(x, y);
                count++;
            }
        }

        if (count == 0) return 128;

        return (sum / count) - 10; // Léger offset pour mieux capturer les crêtes
    }
}
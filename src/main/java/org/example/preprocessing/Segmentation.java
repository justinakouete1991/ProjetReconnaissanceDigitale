package org.example.preprocessing;

import ij.ImagePlus;
import ij.process.ImageProcessor;

public class Segmentation {
    private int blockSize;

    public Segmentation(int blockSize) {
        this.blockSize = blockSize;
    }

    public ImagePlus segment(ImagePlus input) {
        ImageProcessor ip = input.getProcessor();
        int width = ip.getWidth();
        int height = ip.getHeight();

        ImageProcessor mask = ip.createProcessor(width, height);

        for (int y = 0; y < height; y += blockSize) {
            for (int x = 0; x < width; x += blockSize) {
                // Calculer la variance locale
                double variance = computeBlockVariance(ip, x, y, blockSize);

                // Seuils adaptatifs
                int maskValue;
                if (variance > 50) {
                    maskValue = 255;  // Récupérable - haute qualité
                } else if (variance > 20) {
                    maskValue = 180;  // Partiellement récupérable - qualité moyenne
                } else if (variance > 5) {
                    maskValue = 100;  // Faible qualité
                } else {
                    maskValue = 0;    // Non récupérable
                }

                // Remplir le bloc avec la valeur du masque
                for (int dy = 0; dy < blockSize && (y + dy) < height; dy++) {
                    for (int dx = 0; dx < blockSize && (x + dx) < width; dx++) {
                        mask.putPixel(x + dx, y + dy, maskValue);
                    }
                }
            }
        }

        // Appliquer un lissage pour éliminer les artefacts de bloc
        mask.medianFilter();

        return new ImagePlus("Segmentation Mask", mask);
    }

    private double computeBlockVariance(ImageProcessor ip, int startX, int startY, int size) {
        double sum = 0;
        double sumSq = 0;
        int count = 0;

        int endX = Math.min(startX + size, ip.getWidth());
        int endY = Math.min(startY + size, ip.getHeight());

        for (int y = startY; y < endY; y++) {
            for (int x = startX; x < endX; x++) {
                double pixel = ip.getPixel(x, y);
                sum += pixel;
                sumSq += pixel * pixel;
                count++;
            }
        }

        if (count <= 1) return 0;

        double mean = sum / count;
        return (sumSq / count) - (mean * mean);
    }
}
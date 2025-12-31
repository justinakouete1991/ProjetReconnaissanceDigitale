package org.example.preprocessing;

import ij.ImagePlus;
import ij.process.ImageProcessor;

public class OrientationEstimation {
    private int blockSize;

    public OrientationEstimation(int blockSize) {
        this.blockSize = blockSize;
    }

    public ImagePlus estimateOrientation(ImagePlus input) {
        ImageProcessor ip = input.getProcessor();
        int width = ip.getWidth();
        int height = ip.getHeight();

        ImageProcessor orientation = ip.createProcessor(width, height);

        for (int y = blockSize/2; y < height - blockSize/2; y += blockSize) {
            for (int x = blockSize/2; x < width - blockSize/2; x += blockSize) {
                double orientationAngle = computeBlockOrientation(ip, x, y, blockSize);

                // Normaliser l'orientation entre 0 et 255 pour l'affichage
                int orientationValue = (int) ((orientationAngle + Math.PI/2) * 255 / Math.PI);
                orientationValue = Math.max(0, Math.min(255, orientationValue));

                // Remplir le bloc avec l'orientation calculée
                for (int dy = -blockSize/2; dy <= blockSize/2; dy++) {
                    for (int dx = -blockSize/2; dx <= blockSize/2; dx++) {
                        int px = x + dx;
                        int py = y + dy;
                        if (px >= 0 && px < width && py >= 0 && py < height) {
                            orientation.putPixel(px, py, orientationValue);
                        }
                    }
                }
            }
        }

        // Appliquer un lissage pour obtenir un champ d'orientation continu
        orientation.blurGaussian(2.0);

        return new ImagePlus("Orientation", orientation);
    }

    private double computeBlockOrientation(ImageProcessor ip, int centerX, int centerY, int size) {
        double gx = 0, gy = 0;
        int halfSize = size / 2;

        for (int y = centerY - halfSize; y <= centerY + halfSize; y++) {
            for (int x = centerX - halfSize; x <= centerX + halfSize; x++) {
                if (x > 0 && x < ip.getWidth()-1 && y > 0 && y < ip.getHeight()-1) {
                    // Calcul des gradients avec Sobel
                    double dx = (ip.getPixel(x+1, y-1) + 2*ip.getPixel(x+1, y) + ip.getPixel(x+1, y+1))
                            - (ip.getPixel(x-1, y-1) + 2*ip.getPixel(x-1, y) + ip.getPixel(x-1, y+1));

                    double dy = (ip.getPixel(x-1, y+1) + 2*ip.getPixel(x, y+1) + ip.getPixel(x+1, y+1))
                            - (ip.getPixel(x-1, y-1) + 2*ip.getPixel(x, y-1) + ip.getPixel(x+1, y-1));

                    gx += 2 * dx * dy;
                    gy += dx * dx - dy * dy;
                }
            }
        }

        // Calcul de l'orientation (éviter la division par zéro)
        if (Math.abs(gx) < 0.001 && Math.abs(gy) < 0.001) {
            return 0.0;
        }

        return 0.5 * Math.atan2(gx, gy);
    }
}
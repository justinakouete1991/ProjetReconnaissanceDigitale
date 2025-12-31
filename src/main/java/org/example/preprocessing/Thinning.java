package org.example.preprocessing;

import ij.ImagePlus;
import ij.process.ImageProcessor;

public class Thinning {

    public ImagePlus thin(ImagePlus input) {
        ImageProcessor ip = input.getProcessor().duplicate();
        int width = ip.getWidth();
        int height = ip.getHeight();

        // Convertir en binaire si nécessaire (0 = noir, 255 = blanc)
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = ip.getPixel(x, y);
                ip.putPixel(x, y, pixel < 128 ? 0 : 255);
            }
        }

        boolean changed;
        int iterations = 0;
        int maxIterations = 100;

        do {
            changed = false;
            boolean[][] toDelete = new boolean[width][height];

            // Première sous-itération
            for (int y = 1; y < height - 1; y++) {
                for (int x = 1; x < width - 1; x++) {
                    if (ip.getPixel(x, y) == 0) { // Pixel noir (crête)
                        if (shouldDeleteFirstPass(ip, x, y)) {
                            toDelete[x][y] = true;
                            changed = true;
                        }
                    }
                }
            }

            // Supprimer les pixels marqués
            for (int y = 1; y < height - 1; y++) {
                for (int x = 1; x < width - 1; x++) {
                    if (toDelete[x][y]) {
                        ip.putPixel(x, y, 255); // Blanc = supprimé
                    }
                }
            }

            // Réinitialiser
            toDelete = new boolean[width][height];

            // Deuxième sous-itération
            for (int y = 1; y < height - 1; y++) {
                for (int x = 1; x < width - 1; x++) {
                    if (ip.getPixel(x, y) == 0) { // Pixel noir (crête)
                        if (shouldDeleteSecondPass(ip, x, y)) {
                            toDelete[x][y] = true;
                            changed = true;
                        }
                    }
                }
            }

            // Supprimer les pixels marqués
            for (int y = 1; y < height - 1; y++) {
                for (int x = 1; x < width - 1; x++) {
                    if (toDelete[x][y]) {
                        ip.putPixel(x, y, 255); // Blanc = supprimé
                    }
                }
            }

            iterations++;
        } while (changed && iterations < maxIterations);

        // Inverser pour avoir les crêtes en blanc sur fond noir
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = ip.getPixel(x, y);
                ip.putPixel(x, y, pixel == 0 ? 255 : 0);
            }
        }

        return new ImagePlus("Thinned", ip);
    }

    private int[] getNeighbors(ImageProcessor ip, int x, int y) {
        int[] neighbors = new int[8];
        // P2, P3, P4, P5, P6, P7, P8, P9 dans l'ordre horaire
        neighbors[0] = (ip.getPixel(x, y-1) == 0) ? 1 : 0;     // P2
        neighbors[1] = (ip.getPixel(x+1, y-1) == 0) ? 1 : 0;   // P3
        neighbors[2] = (ip.getPixel(x+1, y) == 0) ? 1 : 0;     // P4
        neighbors[3] = (ip.getPixel(x+1, y+1) == 0) ? 1 : 0;   // P5
        neighbors[4] = (ip.getPixel(x, y+1) == 0) ? 1 : 0;     // P6
        neighbors[5] = (ip.getPixel(x-1, y+1) == 0) ? 1 : 0;   // P7
        neighbors[6] = (ip.getPixel(x-1, y) == 0) ? 1 : 0;     // P8
        neighbors[7] = (ip.getPixel(x-1, y-1) == 0) ? 1 : 0;   // P9

        return neighbors;
    }

    private boolean shouldDeleteFirstPass(ImageProcessor ip, int x, int y) {
        int[] P = getNeighbors(ip, x, y);

        // Condition 1: 2 ≤ B(P1) ≤ 6
        int B = 0;
        for (int i = 0; i < 8; i++) B += P[i];
        if (B < 2 || B > 6) return false;

        // Condition 2: A(P1) = 1
        int A = 0;
        for (int i = 0; i < 8; i++) {
            if (P[i] == 0 && P[(i+1)%8] == 1) A++;
        }
        if (A != 1) return false;

        // Condition 3: P2 * P4 * P6 = 0
        if (P[0] * P[2] * P[4] != 0) return false;

        // Condition 4: P4 * P6 * P8 = 0
        if (P[2] * P[4] * P[6] != 0) return false;

        return true;
    }

    private boolean shouldDeleteSecondPass(ImageProcessor ip, int x, int y) {
        int[] P = getNeighbors(ip, x, y);

        // Condition 1: 2 ≤ B(P1) ≤ 6
        int B = 0;
        for (int i = 0; i < 8; i++) B += P[i];
        if (B < 2 || B > 6) return false;

        // Condition 2: A(P1) = 1
        int A = 0;
        for (int i = 0; i < 8; i++) {
            if (P[i] == 0 && P[(i+1)%8] == 1) A++;
        }
        if (A != 1) return false;

        // Condition 3: P2 * P4 * P8 = 0
        if (P[0] * P[2] * P[6] != 0) return false;

        // Condition 4: P2 * P6 * P8 = 0
        if (P[0] * P[4] * P[6] != 0) return false;

        return true;
    }
}
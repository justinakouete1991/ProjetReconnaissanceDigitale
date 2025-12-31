package org.example.utils;

import ij.ImagePlus;
import ij.process.ImageProcessor;
import java.awt.Image;
import javax.swing.ImageIcon;

public class ImageUtils {

    public static ImageIcon createScaledIcon(ImagePlus img, int width, int height) {
        if (img == null) return null;
        Image image = img.getImage();
        Image scaled = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    public static double calculateMean(ImageProcessor ip) {
        int width = ip.getWidth();
        int height = ip.getHeight();
        double sum = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                sum += ip.getPixel(x, y);
            }
        }

        return sum / (width * height);
    }

    public static double calculateVariance(ImageProcessor ip, double mean) {
        int width = ip.getWidth();
        int height = ip.getHeight();
        double sumSq = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double diff = ip.getPixel(x, y) - mean;
                sumSq += diff * diff;
            }
        }

        return sumSq / (width * height);
    }

    public static ImagePlus convertToGrayScale(ImagePlus input) {
        ImageProcessor ip = input.getProcessor();
        if (ip.getBitDepth() == 8) {
            return input; // Déjà en niveaux de gris
        }

        ImageProcessor gray = ip.convertToByte(true);
        return new ImagePlus("Grayscale", gray);
    }

    public static ImagePlus resizeImage(ImagePlus input, int newWidth, int newHeight) {
        ImageProcessor ip = input.getProcessor();
        ip.setInterpolationMethod(ImageProcessor.BILINEAR);
        ip = ip.resize(newWidth, newHeight);
        return new ImagePlus("Resized", ip);
    }

    public static void displayImageInfo(ImagePlus img, String title) {
        System.out.println("\n=== " + title + " ===");
        System.out.println("Dimensions: " + img.getWidth() + " x " + img.getHeight());
        System.out.println("Type: " + img.getBitDepth() + " bits");
        System.out.println("Titre: " + img.getTitle());
    }
}
package org.example.core;

import ij.ImagePlus;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import org.example.ui.ImagePanel;
import org.example.ui.ImagesPanel;
import org.example.ui.LogsArea;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageProducer;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ImageUtils {
    LogsArea logsArea;
    FingerprintProcessor fingerprintProcessor;
    int maxSize = 512;

    public ImageUtils(FingerprintProcessor fingerprintProcessor1) {
        fingerprintProcessor = fingerprintProcessor1;
        logsArea = fingerprintProcessor.getLogsArea();
    }

    public void preProcessFingerprint(BufferedImage fingerprint, String fingerprintPath, ImagePanel imagePanel) {
        ImagePlus impFingerprint = new ImagePlus(fingerprintPath, fingerprint);
        ImageProcessor ipFingerprint = impFingerprint.getProcessor();

        // 1ère étape : Redimensionnement des empreintes
        logsArea.addLog("Redimensionnement de l'empreinte");
        ipFingerprint = resizeImage(ipFingerprint);

        // 2ème étape : Conversion en niveaux de gris si l'empreinte ne l'est déjà pas
        if (impFingerprint.getBitDepth() != 8) {
            logsArea.addLog("Conversion en 8-bit...");
            ipFingerprint = ipFingerprint.convertToByte(true);
            impFingerprint.setProcessor(ipFingerprint);
            imagePanel.showProcessedImage(impFingerprint.getBufferedImage());
        }

        // 2ème étape : Amélioration du contraste
        logsArea.addLog("Amélioration du contraste");
        ipFingerprint.setMinAndMax(0, 255);
        ipFingerprint.resetMinAndMax();
        imagePanel.showProcessedImage(impFingerprint.getBufferedImage());

    }

    public ImageProcessor resizeImage(ImageProcessor ipFingerprint) {
        //ImagePlus impFingerprint = new ImagePlus(fingerprintPath, fingerprint);
        //ImageProcessor ipFingerprint = impFingerprint.getProcessor();

        int w = ipFingerprint.getWidth();
        int h = ipFingerprint.getHeight();
        int newW = w;
        int newH = h;

        if (w > maxSize || h > maxSize) {
            // L'une des deux dimensions est plus grande que la taille maximale
            double scale = Math.min(
                    (double) maxSize / w,
                    (double) maxSize / h
            );
            newW = (int) Math.round(w * scale);
            newH = (int) Math.round(h * scale);
        }

        //(new ImagePlus("resized", ipFingerprint)).show();
        // System.out.println("Ancienne largeur : " + w + ", Ancienne hauteur : " + h + "Nouvelle largeur : " + newW + ", Nouvelle hauteur : " + newH);
        ipFingerprint.setInterpolationMethod(ImageProcessor.BICUBIC);
        ipFingerprint = ipFingerprint.resize(newW, newH);

        // Padding
        ImageProcessor padded = ipFingerprint.createProcessor(maxSize, maxSize);
        padded.setColor(Color.WHITE); //fond blanc
        padded.fill();

        int x = (maxSize - newW) / 2;
        int y = (maxSize - newH) / 2;
        padded.insert(ipFingerprint, x, y);

        return padded;
    }

    public void exportMatrix(String imagePath, String outputPath) {
        ImagePlus imp = new ImagePlus(imagePath);
        ImageProcessor ip = imp.getProcessor();
        int width = ip.getWidth();
        int height = ip.getHeight();

        try{
            ByteBuffer bb = ByteBuffer.allocate(8 * width * height);
            bb.order(ByteOrder.LITTLE_ENDIAN);
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    double val = ip.getPixelValue(x, y);
//                    dos.writeDouble(val);
                    bb.putDouble(val);
                }
            }
            Files.write(Paths.get(outputPath), bb.array());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

//        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(outputPath))) {
//
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
    }

    public ImageProcessor convertToByte(ImageProcessor ip) {
        return ip.convertToByte(true);
    }

    public ImagePlus importBinaryImage(String binaryFilePath) {
        //double[][] data = new double[maxSize][maxSize];
//        int compteur = 0;
//        try (DataInputStream dis = new DataInputStream(new FileInputStream(binaryFilePath))) {
//            for (int y = 0; y < maxSize; y++) {
//                for (int x = 0; x < maxSize; x++) {
//                    compteur++;
//                    data[y][x] = dis.readDouble();
//                }
//            }
//        } catch (IOException e) {
//            logsArea.addLog("C'est ici que se trouve le problème");
//            //throw new RuntimeException(e);
//            System.out.println(compteur);
//        }
        double[] data = fingerprintProcessor.fastBinaryFileReader(binaryFilePath);
        float[] pixels = new float[maxSize * maxSize];
        int compteur = 0;
        // Conversion double → float
        for (int y = 0; y < maxSize; y++) {
            for (int x = 0; x < maxSize; x++) {
                pixels[y * maxSize + x] = (float) data[compteur];
                compteur++;
            }
        }

        FloatProcessor fp = new FloatProcessor(maxSize, maxSize, pixels);
        return new ImagePlus("Image reconstruite", fp);
    }
}

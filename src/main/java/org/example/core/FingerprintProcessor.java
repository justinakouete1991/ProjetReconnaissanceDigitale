package org.example.core;

import ij.IJ;
import ij.ImagePlus;
import ij.process.ImageProcessor;
import org.example.ui.ImagePanel;
import org.example.ui.LogsArea;
import org.example.ui.MainWindow;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.foreign.PaddingLayout;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FingerprintProcessor {
    LogsArea logsArea;
    MainWindow appMainWindow;
    ImageUtils imageUtils;

    public FingerprintProcessor(MainWindow appMainWindow) {
        this.appMainWindow = appMainWindow;
        this.logsArea = appMainWindow.getLogsArea();
        this.imageUtils = new ImageUtils(this);
        // Initialisation de MATLAB et activation de l'interface si tout est ok
        logsArea.addLog("Application en cours d'initialisation...");
        initializeMATLAB();
    }

    public void initializeMATLAB() {

//        try{
////            BufferedImage image1 = javax.imageio.ImageIO.read(
////                    new File(getProjectRoot() + "\\matlab\\test\\image_1.jpg")
////            );
//
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }


        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    String testImage = getProjectRoot() + "\\matlab\\test\\image_test.jpg";
                    String testProcessedImage = getProjectRoot() + "\\matlab\\test\\image_test_2.jpg";
                    String binaryInputPath = getProjectRoot() + "\\matlab\\test\\input.bin";

                    ImagePlus img = IJ.openImage(testImage);
                    ImageProcessor ip = img.getProcessor();
                    ImageProcessor processedIp = imageUtils.convertToByte(ip);
                    processedIp = imageUtils.resizeImage(processedIp);
                    ImagePlus processedImg = new ImagePlus("resized", processedIp);
                    IJ.save(processedImg, testProcessedImage);

                    imageUtils.exportMatrix(testProcessedImage, binaryInputPath);

                    String exePath = getProjectRoot() + "\\matlab\\test\\Test_connexion.exe";
                    ProcessBuilder pb = new ProcessBuilder(exePath);
                    pb.redirectErrorStream(true);
                    Process p = pb.start();

                    //String binaryOutputFile = getProjectRoot() + "\\matlab\\test\\output.bin";
                    // fastBinaryFileReader(binaryOutputFile);
                    // imageUtils.importMatrix(binaryOutputFile);
                    //ImagePlus outputImage = imageUtils.importBinaryImage(binaryOutputFile);
                    //outputImage.show();

                    //Lire la sortie de la console
                    BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    String line;
                    while ((line = br.readLine()) != null) {
                        logsArea.addLog("[MATLAB] " + line);
                    }
                    int exitCode = p.waitFor();
                    logsArea.addLog("[MATLAB] Code de sortie : " + exitCode);
                    logsArea.addLog("### Application initialisée avec succès.");
                    logsArea.addLog("Chargez à présent deux empreintes pour les comparer");
                    // Activation de l'interface
                    appMainWindow.enableWindow();
                } catch (Exception e) {
                    logsArea.addLog("[MATLAB] " + e.getMessage() + "\n[MATLAB] Connexion échouée");
                }
                return null;
            }
        };
        worker.execute();
    }

    public String getProjectRoot() {
        Path path = Paths.get("");
        return path.toAbsolutePath().toString();
    }

    public void processVerification(BufferedImage[] fingerprints, String[] fingerprintsPaths, ImagePanel[] imagePanels) {
        logsArea.addLog("\n========== PRÉ-TRAITEMENT DES EMPREINTES ==========");
        for (int i = 0; i < fingerprints.length; i++) {
            logsArea.addLog("\n========== OPÉRATIONS MENÉES SUR L'EMPREINTE " + (i + 1) + " ==========");
            BufferedImage fingerprint = fingerprints[i];
            String fingerprintPath = fingerprintsPaths[i];
            ImagePanel imagePanel = imagePanels[i];
            imageUtils.preProcessFingerprint(fingerprint, fingerprintPath, imagePanel);
        }


    }

    public double[] fastBinaryFileReader(String binaryFilePath) {
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(binaryFilePath));
            ByteBuffer bb = ByteBuffer.wrap(bytes);
            bb.order(ByteOrder.LITTLE_ENDIAN);

            DoubleBuffer db = bb.asDoubleBuffer();
            double[] data = new double[db.remaining()];
            db.get(data);

//            for (double v : data){
//                System.out.println(v);
//            }
//            System.out.println(data[data.length - 1]);
            return data;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public LogsArea getLogsArea() {
        return logsArea;
    }
}

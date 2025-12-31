package org.example.core;

import ij.ImagePlus;
import org.example.ui.ImagePanel;
import org.example.ui.LogsArea;
import org.example.ui.MainWindow;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.foreign.PaddingLayout;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FingerprintProcessor {
    LogsArea logsArea;
    MainWindow appMainWindow;
    ImageUtils imageUtils;

    public FingerprintProcessor(MainWindow appMainWindow) {
        this.appMainWindow = appMainWindow;
        this.logsArea = appMainWindow.getLogsArea();
        this.imageUtils = new ImageUtils(logsArea);
        // Initialisation de MATLAB et activation de l'interface si tout est ok
        initializeMATLAB();
    }

    public void initializeMATLAB() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    logsArea.addLog("[MATLAB] Patientez un instant, MATLAB se prépare...");
                    String exePath = getProjectRoot() + "\\matlab\\test_connexion.exe";
                    ProcessBuilder pb = new ProcessBuilder(exePath);
                    pb.redirectErrorStream(true);
                    Process p = pb.start();

                    //Lire la console MATLAB Runtime
                    BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    String line;
                    while ((line = br.readLine()) != null) {
                        int exitCode = p.waitFor();
                        logsArea.addLog(line + ". Code de sortie : " + exitCode);
                    }
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

    public void processVerification(BufferedImage[] fingerprints, String[] fingerprintsPaths, ImagePanel[] imagePanels){
        logsArea.addLog("\n========== PRÉ-TRAITEMENT DES EMPREINTES ==========");
        for (int i = 0; i < fingerprints.length; i++) {
            logsArea.addLog("========== OPÉRATIONS MENÉES SUR L'EMPREINTE " + (i + 1) + " ==========");
            BufferedImage fingerprint = fingerprints[i];
            String fingerprintPath = fingerprintsPaths[i];
            ImagePanel imagePanel = imagePanels[i];
            imageUtils.preProcessFingerprint(fingerprint, fingerprintPath, imagePanel);
        }


    }
}

package org.example.core;

import org.example.ui.LogsArea;
import org.example.ui.MainWindow;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FingerprintProcessor {
    LogsArea logsArea;
    //    JButton processBtn, matlabBtn;
    boolean matlabInitialized = false;
    MainWindow appMainWindow;

    public FingerprintProcessor(MainWindow appMainWindow) {
        this.appMainWindow = appMainWindow;
        logsArea = appMainWindow.getLogsArea();
//        processBtn = appMainWindow.getProcessBtn();
//        matlabBtn = appMainWindow.getMatlabBtn();

        // Initialisation de MATLAB
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

                    logsArea.addLog("Chargez à présent deux empreintes pour effectuer la vérification");
                    appMainWindow.setButtonsTrue();
                } catch (Exception e) {
                    logsArea.addLog("[MATLAB] " + e.getMessage() + "\n[MATLAB] Connexion échouée");
                }
                return null;
            }
        };
        worker.execute();
    }

    public String getProjectRoot(){
       Path path = Paths.get("");
       return path.toAbsolutePath().toString();
    }

}

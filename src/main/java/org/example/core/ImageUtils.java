package org.example.core;

import ij.ImagePlus;
import ij.process.ImageProcessor;
import org.example.ui.ImagePanel;
import org.example.ui.ImagesPanel;
import org.example.ui.LogsArea;

import java.awt.image.BufferedImage;
import java.awt.image.ImageProducer;

public class ImageUtils {
    LogsArea logsArea;
    public ImageUtils(LogsArea logsArea1){
        logsArea = logsArea1;
    }
    public void preProcessFingerprint(BufferedImage fingerprint, String fingerprintPath, ImagePanel imagePanel){
        ImagePlus impFingerprint = new ImagePlus(fingerprintPath, fingerprint);
        ImageProcessor ipFingerprint = impFingerprint.getProcessor();

        // 1ère étape : Conversion en niveaux de gris si l'empreinte ne l'est déjà pas
        if (impFingerprint.getBitDepth() != 8){
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
}

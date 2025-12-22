package org.example.ui;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class MainPanel extends JPanel {
    private final LogsArea logsArea = new LogsArea(6, 11);
    private final ImagesPanel imagesPanel = new ImagesPanel(new GridLayout(2, 1, 0, 10));;

    public MainPanel(LayoutManager layout){
        super(layout);
        // Colonne de gauche : Panneau des images
        JPanel leftColumn = new JPanel(new BorderLayout(5, 5));
        leftColumn.setBackground(Color.WHITE);

        imagesPanel.setBackground(Color.WHITE);
        leftColumn.add(imagesPanel, BorderLayout.CENTER);

        // Colonne de droite : Logs
        JPanel rightColumn = new JPanel(new BorderLayout());
        rightColumn.setBackground(Color.WHITE);

        JScrollPane logScroll = new JScrollPane(logsArea);
        logScroll.setBackground(Color.WHITE);
        logScroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2),
                "Journal d'activité",
                javax.swing.border.TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14),
                Color.BLACK
        ));

        rightColumn.add(logScroll, BorderLayout.CENTER);

        // Assemblage
        add(leftColumn);
        add(rightColumn);
    }

    /* Fonctions utilitaires */
    public LogsArea getLogsArea(){
        return logsArea;
    }

    public void enableMainPanel(){
        imagesPanel.enableImagesPanel();
    }

    public JButton[] getMainPanelLoadBtns(){
        return new JButton[]{imagesPanel.getLoadBtn1(), imagesPanel.getLoadBtn2()};
    }

    public String setImagesPanelImage(int imgNum, File pathToImage){
        return imagesPanel.setImagePanelImage(imgNum, pathToImage);
    }

    public BufferedImage[] getMainPanelImages(){
        return imagesPanel.getImages();
    }

    public String[] getMainPanelImagesPaths(){
        return imagesPanel.getImagesPaths();
    }
    public ImagePanel[] getImagePanels(){
        return imagesPanel.getImagePanels();
    }
}

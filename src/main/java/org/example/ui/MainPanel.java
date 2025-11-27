package org.example.ui;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class MainPanel extends JPanel {
    private final LogsArea logsArea = new LogsArea(6, 11);

    public MainPanel(LayoutManager layout){
        super(layout);
        // Colonne de gauche : Panneau des images
        JPanel leftColumn = new JPanel(new BorderLayout(5, 5));
        leftColumn.setBackground(Color.WHITE);
        ImagesPanel imagesPanel = new ImagesPanel(new GridLayout(2, 1, 0, 10));
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
}

package org.example.ui;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class ImagesPanel extends JPanel {
    private ImagePanel imagePanel1 = new ImagePanel("Empreinte 1 - Non chargée");
    private ImagePanel imagePanel2 = new ImagePanel("Empreinte 2 - Non chargée");
    private JButton loadBtn1 = new JButton("Charger Empreinte 1");
    private JButton loadBtn2 = new JButton("Charger Empreinte 2");

    public ImagesPanel(LayoutManager layout) {
        super(layout);
        setBackground(Color.WHITE);

        // Panel Image 1
        JPanel img1Panel = new JPanel(new BorderLayout(5, 5));
        img1Panel.setBackground(Color.WHITE);
        img1Panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2),
                "Image 1",
                javax.swing.border.TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14),
                Color.BLACK
        ));
        img1Panel.add(imagePanel1, BorderLayout.CENTER);

        JPanel img1BtnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        img1BtnPanel.setBackground(Color.WHITE);
        loadBtn1.setFont(new Font("Segoe UI", Font.BOLD, 12));
        loadBtn1.setEnabled(false);
        img1BtnPanel.add(loadBtn1);
        img1Panel.add(img1BtnPanel, BorderLayout.SOUTH);

        // Panel Image 2
        JPanel img2Panel = new JPanel(new BorderLayout(5, 5));
        img2Panel.setBackground(Color.WHITE);
        img2Panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2),
                "Image 2",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14),
                Color.BLACK));
        img2Panel.add(imagePanel2, BorderLayout.CENTER);

        JPanel img2BtnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        img2BtnPanel.setBackground(Color.WHITE);
        loadBtn2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        loadBtn2.setEnabled(false);
        img2BtnPanel.add(loadBtn2);
        img2Panel.add(img2BtnPanel, BorderLayout.SOUTH);

        // Assemblage

        add(img1Panel);
        add(img2Panel);
    }

    public void enableImagesPanel(){
        loadBtn1.setEnabled(true);
        loadBtn2.setEnabled(true);
    }
}

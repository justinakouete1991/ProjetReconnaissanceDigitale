package org.example.ui;

import javax.swing.*;
import java.awt.*;

public class TopPanel extends JPanel {
    Font btnFont = new Font("Segoe UI", Font.BOLD, 12);
    JLabel formatLabel = new JLabel("Format d'export:");
    private JButton processBtn = new JButton("Traiter les Empreintes");
    private JButton matlabBtn = new JButton("Analyser avec MATLAB");
    private JComboBox<String> formatCombo = new JComboBox<>(new String[]{"TIFF", "PNG", "JPEG", "BMP"});
    private JComboBox<String> exportCombo = new JComboBox<>(new String[]{"Exporter les Deux", "Exporter Empreinte 1", "Exporter Empreinte 2"});

    public TopPanel(LayoutManager layout) {
        super(layout);
        processBtn.setFont(btnFont);
        matlabBtn.setFont(btnFont);
        formatLabel.setForeground(Color.WHITE);
        formatLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        add(processBtn);
        add(matlabBtn);
        add(Box.createHorizontalStrut(20));
        add(formatLabel);
        add(formatCombo);
        add(Box.createHorizontalStrut(20));
        add(exportCombo);

        processBtn.setEnabled(false);
        matlabBtn.setEnabled(false);
    }

    public JButton getProcessBtn() {
        return processBtn;
    }

    public JButton getMatlabBtn() {
        return matlabBtn;
    }
}

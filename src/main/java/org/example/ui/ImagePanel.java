package org.example.ui;

import org.example.core.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImagePanel extends JPanel {
    private BufferedImage image, processedImage;
    private String imagePath, text;
    //private String text;

    public ImagePanel(String initialText) {
        this.text = initialText;
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
    }

    public String setImage(File pathToImage) {
        try {
            image = javax.imageio.ImageIO.read(pathToImage);
            imagePath = pathToImage.getAbsolutePath();
            text = null;
            repaint();
            return null;
        } catch (IOException e) {
            //throw new RuntimeException(e);
            return e.getMessage();
        }
    }

    public BufferedImage getImage(){
        return image;
    }

    public void setText(String text) {
        this.text = text;
        this.image = null;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        BufferedImage imageToUse = image;
        if (processedImage != null) imageToUse = processedImage;

        if (imageToUse != null) {
            int panelWidth = getWidth();
            int panelHeight = getHeight();
            int imgWidth = imageToUse.getWidth();
            int imgHeight = imageToUse.getHeight();

            double scaleX = (double) panelWidth / imgWidth;
            double scaleY = (double) panelHeight / imgHeight;
            double scale = Math.min(scaleX, scaleY) * 0.95;

            int scaledWidth = (int) (imgWidth * scale);
            int scaledHeight = (int) (imgHeight * scale);

            int x = (panelWidth - scaledWidth) / 2;
            int y = (panelHeight - scaledHeight) / 2;

            g2d.drawImage(imageToUse, x, y, scaledWidth, scaledHeight, null);

            g2d.setColor(new Color(0, 0, 0, 150));
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 12));
            String dimensionText = imgWidth + " × " + imgHeight + " px";
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(dimensionText);
            g2d.fillRect(panelWidth - textWidth - 15, panelHeight - 25, textWidth + 10, 20);
            g2d.setColor(Color.WHITE);
            g2d.drawString(dimensionText, panelWidth - textWidth - 10, panelHeight - 10);

        } else if (text != null) {
            g2d.setColor(Color.GRAY);
            g2d.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            int textHeight = fm.getHeight();
            g2d.drawString(text, (getWidth() - textWidth) / 2, (getHeight() + textHeight / 2) / 2);
        }
    }

    public String getImagePath(){
        return imagePath;
    }

    public void showProcessedImage(BufferedImage processedImage){
        this.processedImage = processedImage;
        repaint();
    }

//    public void showResizedImage(){
//        processedImage = resizedImage;
//        repaint();
//    }
}

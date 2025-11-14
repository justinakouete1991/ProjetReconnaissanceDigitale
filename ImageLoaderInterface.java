import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class ImageLoaderInterface extends JFrame {
    private JLabel imageLabel1;
    private JLabel imageLabel2;
    private JTextArea resultArea;
    private BufferedImage image1;
    private BufferedImage image2;

    public ImageLoaderInterface() {
        setTitle("Interface de Chargement d'Images");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);

        // Panel principal avec GridLayout (1 ligne, 2 colonnes)
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Colonne 1 : Zone de chargement des images (2 lignes)
        JPanel leftColumn = new JPanel(new GridLayout(2, 1, 0, 10));

        // Ligne 1 - Image 1
        JPanel imagePanel1 = createImagePanel("Image 1", 1);
        leftColumn.add(imagePanel1);

        // Ligne 2 - Image 2
        JPanel imagePanel2 = createImagePanel("Image 2", 2);
        leftColumn.add(imagePanel2);

        // Colonne 2 : Zone des résultats
        JPanel rightColumn = new JPanel(new BorderLayout());
        rightColumn.setBorder(BorderFactory.createTitledBorder("Résultats"));

        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(resultArea);
        rightColumn.add(scrollPane, BorderLayout.CENTER);

        // Bouton pour traiter les images
        JButton processButton = new JButton("Traiter les Images");
        processButton.addActionListener(e -> processImages());
        rightColumn.add(processButton, BorderLayout.SOUTH);

        // Ajout des colonnes au panel principal
        mainPanel.add(leftColumn);
        mainPanel.add(rightColumn);

        add(mainPanel);
    }

    private JPanel createImagePanel(String title, int imageNumber) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder(title));

        // Zone d'affichage de l'image
        JLabel imageLabel = new JLabel("Aucune image chargée", SwingConstants.CENTER);
        imageLabel.setPreferredSize(new Dimension(400, 200));
        imageLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        if (imageNumber == 1) {
            imageLabel1 = imageLabel;
        } else {
            imageLabel2 = imageLabel;
        }

        // Bouton de chargement
        JButton loadButton = new JButton("Charger " + title);
        loadButton.addActionListener(e -> loadImage(imageNumber));

        panel.add(imageLabel, BorderLayout.CENTER);
        panel.add(loadButton, BorderLayout.SOUTH);

        return panel;
    }

    private void loadImage(int imageNumber) {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Images (JPG, PNG, GIF)", "jpg", "jpeg", "png", "gif");
        fileChooser.setFileFilter(filter);

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                BufferedImage img = ImageIO.read(selectedFile);

                if (imageNumber == 1) {
                    image1 = img;
                    displayImage(imageLabel1, img);
                } else {
                    image2 = img;
                    displayImage(imageLabel2, img);
                }

                resultArea.append("Image " + imageNumber + " chargée: " +
                        selectedFile.getName() + "\n");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Erreur lors du chargement de l'image: " + ex.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void displayImage(JLabel label, BufferedImage img) {
        // Redimensionner l'image pour l'adapter au label
        Image scaledImg = img.getScaledInstance(
                label.getWidth(), label.getHeight(), Image.SCALE_SMOOTH);
        label.setIcon(new ImageIcon(scaledImg));
        label.setText("");
    }

    private void processImages() {
        if (image1 == null || image2 == null) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez charger les deux images avant de traiter.",
                    "Attention", JOptionPane.WARNING_MESSAGE);
            return;
        }

        resultArea.append("\n=== Traitement des images ===\n");
        resultArea.append("Image 1: " + image1.getWidth() + "x" + image1.getHeight() + " pixels\n");
        resultArea.append("Image 2: " + image2.getWidth() + "x" + image2.getHeight() + " pixels\n");
        resultArea.append("Traitement effectué avec succès!\n");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ImageLoaderInterface frame = new ImageLoaderInterface();
            frame.setVisible(true);
   });
}
}

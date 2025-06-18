import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CollectionPanel extends JPanel {
    private JTabbedPane tabs;
    private JPanel imagesGridPanel;
    private JButton uploadButton;

    public CollectionPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(35, 35, 35));

        tabs = new JTabbedPane();
        tabs.setBackground(new Color(35, 35, 35));
        tabs.setForeground(Color.WHITE);

        // --- MODIFICATION START ---
        // Increase the font size for the tabs to make them bigger
        Font currentFont = tabs.getFont();
        Font newFont = currentFont.deriveFont(currentFont.getSize() + 4f); // Increase font size by 4 points
        tabs.setFont(newFont);
        // --- MODIFICATION END ---

        tabs.addTab("Animals", createTabWithThumbnails("Animal"));
        tabs.addTab("Flowers", createTabWithThumbnails("Flower"));
        tabs.addTab("Images", createImagesTabContent());

        add(tabs, BorderLayout.CENTER);
    }

    private JScrollPane createTabWithThumbnails(String type) {
        // Adjusted hgap and vgap from 10, 10 to 5, 5 for better spacing
        JPanel gridPanel = new JPanel(new WrapLayout(FlowLayout.LEFT, 5, 5)); //
        gridPanel.setBackground(new Color(45, 45, 45));
        gridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        loadImagesToGrid(gridPanel, type);

        JScrollPane scrollPane = new JScrollPane(gridPanel,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        return scrollPane;
    }

    private JPanel createImagesTabContent() {
        JPanel imagesTabContentPanel = new JPanel(new BorderLayout());
        imagesTabContentPanel.setBackground(new Color(45, 45, 45));

        // Adjusted hgap and vgap from 10, 10 to 5, 5 for better spacing
        imagesGridPanel = new JPanel(new WrapLayout(FlowLayout.LEFT, 5, 5)); //
        imagesGridPanel.setBackground(new Color(45, 45, 45));
        imagesGridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        loadImagesToGrid(imagesGridPanel, "Image");

        JScrollPane scrollPane = new JScrollPane(imagesGridPanel,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        imagesTabContentPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel uploadButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        uploadButtonPanel.setBackground(new Color(45, 45, 45));
        uploadButtonPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        uploadButton = new JButton("Upload New Image");
        uploadButton.setBackground(new Color(50, 150, 200));
        uploadButton.setForeground(Color.WHITE);
        uploadButton.setFocusPainted(false);
        uploadButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        uploadButton.setFont(new Font("Arial", Font.BOLD, 14));

        uploadButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                uploadButton.setBackground(new Color(70, 170, 220));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                uploadButton.setBackground(new Color(50, 150, 200));
            }
        });

        uploadButton.addActionListener(e -> {
            File newImage = ImageUploader.uploadImage(this);
            if (newImage != null) {
                loadImagesToGrid(imagesGridPanel, "Image");
                JOptionPane.showMessageDialog(this, "Image uploaded successfully!");
            }
        });

        uploadButtonPanel.add(uploadButton);
        imagesTabContentPanel.add(uploadButtonPanel, BorderLayout.SOUTH);

        return imagesTabContentPanel;
    }


    private void loadImagesToGrid(JPanel gridPanel, String type) {
        gridPanel.removeAll();

        File[] files = getFilesForTab(type);
        if (files != null) {
            for (File imgFile : files) {
                gridPanel.add(new ImageThumbnail(imgFile.getPath()));
            }
        }
        gridPanel.revalidate();
        gridPanel.repaint();
    }

    private File[] getFilesForTab(String type) {
        String folderPath = type.equals("Image")
                ? "assets/images/"
                : "assets/" + type.toLowerCase() + "s/";

        File folder = new File(folderPath);
        return folder.listFiles(this::isImageFile);
    }

    private boolean isImageFile(File dir, String name) {
        String lower = name.toLowerCase();
        return lower.endsWith(".png") || lower.endsWith(".jpg") || lower.endsWith(".jpeg");
    }

    private void refreshImagesTab() {
        loadImagesToGrid(imagesGridPanel, "Image");
    }
}
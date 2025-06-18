import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CollectionPanel extends JPanel {
    private JTabbedPane tabs;
    private JPanel imagesGridPanel; // To allow refreshing just the images tab grid
    private JButton uploadButton; // Reference to the upload button

    public CollectionPanel() {
        // Set background of the entire CollectionPanel
        setLayout(new BorderLayout());
        setBackground(new Color(35, 35, 35)); // Darker background to be easier on eyes

        tabs = new JTabbedPane();
        tabs.setBackground(new Color(35, 35, 35)); // Background for tabs itself
        tabs.setForeground(Color.WHITE); // Text color for tab titles

        // Create grids for each tab
        tabs.addTab("Animals", createTabWithThumbnails("Animal")); // Use a general method for tabs
        tabs.addTab("Flowers", createTabWithThumbnails("Flower")); // Use a general method for tabs

        // For Images tab, create a special panel that includes the upload button at the bottom
        tabs.addTab("Images", createImagesTabContent());

        add(tabs, BorderLayout.CENTER);
        // The upload button is no longer added directly to the CollectionPanel's SOUTH.
        // It is now part of the 'Images' tab content.
    }

    // Helper method to create a tab content panel containing only thumbnails
    private JScrollPane createTabWithThumbnails(String type) {
        JPanel gridPanel = new JPanel(new WrapLayout(FlowLayout.LEFT, 10, 10)); // Use WrapLayout for dynamic wrapping into columns
        gridPanel.setBackground(new Color(45, 45, 45)); // Match canvas control panel background
        gridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding

        loadImagesToGrid(gridPanel, type);

        JScrollPane scrollPane = new JScrollPane(gridPanel, // Scroll pane for the grid
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // Remove default scroll pane border

        return scrollPane;
    }

    // Method to create the specific content panel for the "Images" tab, including the upload button
    private JPanel createImagesTabContent() {
        JPanel imagesTabContentPanel = new JPanel(new BorderLayout());
        imagesTabContentPanel.setBackground(new Color(45, 45, 45)); // Background for the tab content itself

        // Create the grid panel for images
        imagesGridPanel = new JPanel(new WrapLayout(FlowLayout.LEFT, 10, 10));
        imagesGridPanel.setBackground(new Color(45, 45, 45)); // Match canvas control panel background
        imagesGridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding
        loadImagesToGrid(imagesGridPanel, "Image"); // Load images into this grid

        JScrollPane scrollPane = new JScrollPane(imagesGridPanel,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        imagesTabContentPanel.add(scrollPane, BorderLayout.CENTER); // Add scroll pane to center

        // Create and add the upload button panel to the SOUTH of THIS tab content panel
        JPanel uploadButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Center the button
        uploadButtonPanel.setBackground(new Color(45, 45, 45)); // Match control panel background
        uploadButtonPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // Padding

        uploadButton = new JButton("Upload New Image");
        uploadButton.setBackground(new Color(50, 150, 200)); // A distinct but complementary color
        uploadButton.setForeground(Color.WHITE);
        uploadButton.setFocusPainted(false);
        uploadButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        uploadButton.setFont(new Font("Arial", Font.BOLD, 14)); // Make text clear

        // Add hover effect for consistency
        uploadButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                uploadButton.setBackground(new Color(70, 170, 220));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                uploadButton.setBackground(new Color(50, 150, 200));
            }
        });

        uploadButton.addActionListener(e -> {
            File newImage = ImageUploader.uploadImage(this); // 'this' refers to CollectionPanel, which is a Component
            if (newImage != null) {
                loadImagesToGrid(imagesGridPanel, "Image");
                JOptionPane.showMessageDialog(this, "Image uploaded successfully!");
            }
        });

        uploadButtonPanel.add(uploadButton);
        imagesTabContentPanel.add(uploadButtonPanel, BorderLayout.SOUTH); // Add to the SOUTH of the Images tab content

        return imagesTabContentPanel;
    }


    private void loadImagesToGrid(JPanel gridPanel, String type) {
        gridPanel.removeAll(); // Clear existing components before loading new ones

        File[] files = getFilesForTab(type);
        if (files != null) {
            for (File imgFile : files) {
                gridPanel.add(new ImageThumbnail(imgFile.getPath())); // ImageThumbnail sets its own size
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

    // The refreshImagesTab method is now implicitly handled by calling loadImagesToGrid directly.
    private void refreshImagesTab() {
        loadImagesToGrid(imagesGridPanel, "Image");
    }
}
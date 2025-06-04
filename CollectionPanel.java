import javax.swing.*;
import java.awt.*;
import java.io.File;

public class CollectionPanel extends JPanel {
    private JTabbedPane tabs;

    public CollectionPanel() {
        setLayout(new BorderLayout());

        tabs = new JTabbedPane();
        tabs.add("Animals", createImageGrid("Animal"));
        tabs.add("Flowers", createImageGrid("Flower"));
        tabs.add("Images", createImageGrid("Image"));

        add(tabs, BorderLayout.CENTER);
    }

    private JScrollPane createImageGrid(String type) {
        JPanel gridPanel = new JPanel(new GridLayout(0, 3, 5, 5));

        if (type.equals("Image")) {
            // Upload button at index 0
            JButton uploadBtn = new JButton("Upload");
            uploadBtn.addActionListener(e -> {
                File newImage = ImageUploader.uploadImage(this);
                if (newImage != null) {
                    refreshImagesTab(); // Refresh if upload successful
                }
            });
            gridPanel.add(uploadBtn);

            // Load all images from assets/images/
            File[] files = new File("assets/images/").listFiles((dir, name) -> {
                String lower = name.toLowerCase();
                return lower.endsWith(".png") || lower.endsWith(".jpg") || lower.endsWith(".jpeg");
            });

            if (files != null) {
                for (File imgFile : files) {
                    gridPanel.add(new ImageThumbnail(imgFile.getPath()));
                }
            }

        } else {
            // Your original 9-button logic
            for (int i = 0; i < 9; i++) {
                JButton button = new ImageThumbnail(type + " " + (i + 1));
                gridPanel.add(button);
            }
        }

        return new JScrollPane(gridPanel);
    }

    private void refreshImagesTab() {
        tabs.setComponentAt(2, createImageGrid("Image"));
        revalidate();
        repaint();
    }
}

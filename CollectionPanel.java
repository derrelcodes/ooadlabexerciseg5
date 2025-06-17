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
        // Custom panel to force preferred height for scrolling
        JPanel gridPanel = new JPanel(new WrapLayout(FlowLayout.LEFT, 10, 10)) {
            @Override
            public Dimension getPreferredSize() {
                int count = getComponentCount();
                int rows = (int) Math.ceil(count / 3.0);
                int height = rows * 120; // 100px + padding
                return new Dimension(350, height);
            }
        };

        gridPanel.setBackground(Color.WHITE);
        gridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        if (type.equals("Image")) {
            // Upload Button
            JButton uploadBtn = new JButton("Upload");
            uploadBtn.setPreferredSize(new Dimension(100, 100));
            uploadBtn.addActionListener(e -> {
                File newImage = ImageUploader.uploadImage(this);
                if (newImage != null) refreshImagesTab();
            });
            gridPanel.add(uploadBtn);

            // Load images from assets/images/
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
            // Static placeholders (replace with real animal/flower image paths)
            for (int i = 0; i < 9; i++) {
                gridPanel.add(new ImageThumbnail("assets/placeholder.png"));
            }
        }

        JScrollPane scrollPane = new JScrollPane(gridPanel,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        return scrollPane;
    }

    private void refreshImagesTab() {
        tabs.setComponentAt(2, createImageGrid("Image"));
        revalidate();
        repaint();
    }
}

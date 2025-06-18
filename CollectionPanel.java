import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CollectionPanel extends JPanel {
    private JTabbedPane tabs;

    public CollectionPanel() {
        setLayout(new BorderLayout());

        tabs = new JTabbedPane();
        tabs.add("Animals", createScrollableGrid("Animal"));
        tabs.add("Flowers", createScrollableGrid("Flower"));
        tabs.add("Images", createScrollableGrid("Image"));

        add(tabs, BorderLayout.CENTER);
    }

    private JScrollPane createScrollableGrid(String type) {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(Color.WHITE);

        JPanel row = createRowPanel();
        int columnCount = 0;
        int maxColumns = 2;

        List<JComponent> components = new ArrayList<>();

        // Add Upload button first for Images tab only
        if (type.equals("Image")) {
            JButton uploadBtn = new JButton("Upload");
            uploadBtn.setPreferredSize(new Dimension(100, 100));
            uploadBtn.addActionListener(e -> {
                File newImage = ImageUploader.uploadImage(this);
                if (newImage != null) refreshImagesTab();
            });
            components.add(uploadBtn);
        }

        // Load images based on tab type
        File[] files = getFilesForTab(type);
        if (files != null) {
            for (File imgFile : files) {
                components.add(new ImageThumbnail(imgFile.getPath()));
            }
        }

        // Distribute thumbnails in rows of 2
        for (JComponent comp : components) {
            row.add(wrapThumbnail(comp));
            columnCount++;
            if (columnCount == maxColumns) {
                container.add(row);
                row = createRowPanel();
                columnCount = 0;
            }
        }

        // Add final incomplete row if needed
        if (columnCount > 0) {
            row.add(Box.createHorizontalStrut(110)); // filler for alignment
            container.add(row);
        }

        JScrollPane scrollPane = new JScrollPane(container,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        return scrollPane;
    }

    private File[] getFilesForTab(String type) {
        String folderPath = type.equals("Image")
                ? "assets/images/"
                : "assets/" + type.toLowerCase() + "s/";

        File folder = new File(folderPath);
        return folder.listFiles(this::isImageFile);
    }

    private JPanel createRowPanel() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        row.setBackground(Color.WHITE);
        return row;
    }

    private JPanel wrapThumbnail(JComponent comp) {
        JPanel wrapper = new JPanel();
        wrapper.setPreferredSize(new Dimension(100, 100));
        wrapper.setMaximumSize(new Dimension(100, 100));
        wrapper.setMinimumSize(new Dimension(100, 100));
        wrapper.setLayout(new BorderLayout());
        wrapper.add(comp, BorderLayout.CENTER);
        wrapper.setOpaque(false);
        return wrapper;
    }

    private boolean isImageFile(File dir, String name) {
        String lower = name.toLowerCase();
        return lower.endsWith(".png") || lower.endsWith(".jpg") || lower.endsWith(".jpeg");
    }

    private void refreshImagesTab() {
        tabs.setComponentAt(2, createScrollableGrid("Image"));
        revalidate();
        repaint();
    }
}

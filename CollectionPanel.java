import javax.swing.*;
import java.awt.*;

public class CollectionPanel extends JPanel {
    public CollectionPanel() {
        setLayout(new BorderLayout());

        JTabbedPane tabs = new JTabbedPane();
        tabs.add("Animals", createImageGrid("Animal"));
        tabs.add("Flowers", createImageGrid("Flower"));
        tabs.add("Images", createImageGrid("Image"));

        add(tabs, BorderLayout.CENTER);
    }

    private JScrollPane createImageGrid(String type) {
        JPanel gridPanel = new JPanel(new GridLayout(0, 3, 5, 5));
        for (int i = 0; i < 9; i++) {
            JButton button;
            if (type.equals("Image") && i == 0) {
                button = new JButton("Upload");
            } else {
                button = new ImageThumbnail(type + " " + (i + 1));
            }
            gridPanel.add(button);
        }
        return new JScrollPane(gridPanel);
    }
}

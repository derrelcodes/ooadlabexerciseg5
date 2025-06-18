import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class LeftCanvasControls {

    public static JPanel createTopPanel(LeftCanvas canvas) {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(45, 45, 45));
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JLabel titleLabel = new JLabel("Composition Canvas", JLabel.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        buttonPanel.setBackground(new Color(45, 45, 45));

        JButton rotateButton = createToolButton("Rotate", "rotate.png", e -> canvas.rotateCanvas());
        JButton saveButton = createToolButton("Save", "save.png", e -> canvas.saveCanvasAsImage());

        buttonPanel.add(rotateButton);
        buttonPanel.add(saveButton);

        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(buttonPanel, BorderLayout.CENTER);

        return topPanel;
    }

    public static JPanel createBottomPanel(LeftCanvas canvas) {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(45, 45, 45));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JButton clearButton = new JButton("Clear Canvas");
        clearButton.setBackground(new Color(220, 53, 69));
        clearButton.setForeground(Color.WHITE);
        clearButton.setFocusPainted(false);
        clearButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        clearButton.addActionListener(e -> canvas.clearCanvas());

        bottomPanel.add(Box.createHorizontalStrut(10), BorderLayout.WEST);  // spacer
        bottomPanel.add(clearButton, BorderLayout.EAST);

        return bottomPanel;
    }

    private static JButton createToolButton(String tooltip, String iconFileName, ActionListener listener) {
        JButton button = new JButton();
        button.setToolTipText(tooltip);
        button.setPreferredSize(new Dimension(56, 56));
        button.setBackground(new Color(70, 70, 70));
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        button.setFocusPainted(false);

        boolean iconLoaded = false;
        String[] possiblePaths = {
            iconFileName,
            "icons/" + iconFileName,
            "src/icons/" + iconFileName,
            System.getProperty("user.dir") + "/icons/" + iconFileName
        };

        for (String path : possiblePaths) {
            try {
                File iconFile = new File(path);
                if (iconFile.exists()) {
                    ImageIcon icon = new ImageIcon(path);
                    Image img = icon.getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH);
                    button.setIcon(new ImageIcon(img));
                    iconLoaded = true;
                    break;
                }
            } catch (Exception ignored) {}
        }

        if (!iconLoaded) {
            button.setText(tooltip);
            button.setForeground(Color.WHITE);
        }

        button.addActionListener(listener);
        return button;
    }
}

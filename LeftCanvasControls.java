import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class LeftCanvasControls {
    public static JPanel createButtonPanel(LeftCanvas canvas) {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        buttonPanel.setBackground(new Color(45, 45, 45));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Create buttons
        JButton rotateBtn = createIconButton("RotateAll.png");
        JButton deleteBtn = createIconButton("Delete.png");
        JButton saveBtn = createIconButton("Save.png");
        JButton flipVerticalBtn = createIconButton("FlipVertical.png");
        JButton flipHorizontalBtn = createIconButton("FlipHorizontal.png");

        // Set tooltips for hover text
        rotateBtn.setToolTipText("Rotate Canvas");
        deleteBtn.setToolTipText("Delete Selected Image");
        saveBtn.setToolTipText("Save Composition");
        flipVerticalBtn.setToolTipText("Flip Selected Image Vertically");
        flipHorizontalBtn.setToolTipText("Flip Selected Image Horizontally");

        // Apply action listeners
        rotateBtn.addActionListener((ActionEvent e) -> canvas.rotateCanvas());
        deleteBtn.addActionListener((ActionEvent e) -> canvas.deleteSelectedItem());
        saveBtn.addActionListener((ActionEvent e) -> canvas.saveCanvasAsImage());
        flipVerticalBtn.addActionListener(e -> canvas.flipSelectedVertical());
        flipHorizontalBtn.addActionListener(e -> canvas.flipSelectedHorizontal());

        // Add buttons directly to the panel
        buttonPanel.add(rotateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(saveBtn);
        buttonPanel.add(flipVerticalBtn);
        buttonPanel.add(flipHorizontalBtn);

        return buttonPanel;
    }

    private static JButton createIconButton(String iconPath) {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(56, 56));
        button.setBackground(new Color(70, 70, 70));
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        button.setFocusPainted(false);
        button.setMargin(new Insets(4, 4, 4, 4));

        boolean iconLoaded = false;
        String[] possiblePaths = {
            "icons/" + iconPath,
            "src/icons/" + iconPath,
            "resources/" + iconPath,
            iconPath,
            System.getProperty("user.dir") + "/" + iconPath
        };

        for (String path : possiblePaths) {
            try {
                File iconFile = new File(path);
                if (iconFile.exists()) {
                    ImageIcon icon = new ImageIcon(path);
                    if (icon.getIconWidth() > 0) {
                        Image img = icon.getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH);
                        button.setIcon(new ImageIcon(img));
                        iconLoaded = true;
                        break;
                    }
                }
            } catch (Exception ignored) {}
        }

        if (!iconLoaded) {
            button.setText("?");
            button.setForeground(Color.WHITE);
            button.setFont(new Font("Arial", Font.BOLD, 16));
            System.out.println("Icon not found for " + iconPath + ", using text fallback");
        }

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(90, 90, 90));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(70, 70, 70));
            }
        });
        return button;
    }

    public static JPanel createBottomPanel(LeftCanvas canvas) {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(45, 45, 45));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JButton clearCompositionBtn = new JButton("Clear Composition");
        clearCompositionBtn.setBackground(new Color(220, 53, 69));
        clearCompositionBtn.setForeground(Color.WHITE);
        clearCompositionBtn.setFocusPainted(false);
        clearCompositionBtn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        clearCompositionBtn.setFont(new Font("Arial", Font.BOLD, 14));

        clearCompositionBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                clearCompositionBtn.setBackground(new Color(255, 80, 90));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                clearCompositionBtn.setBackground(new Color(220, 53, 69));
            }
        });

        clearCompositionBtn.addActionListener((ActionEvent e) -> {
            int confirm = JOptionPane.showConfirmDialog(null,
                    "Are you sure you want to clear the composition? This action cannot be undone.",
                    "Clear Composition",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE
            );
            if (confirm == JOptionPane.YES_OPTION) {
                canvas.clearCanvas();
            }
        });

        bottomPanel.add(clearCompositionBtn, BorderLayout.EAST);
        return bottomPanel;
    }
}
// derrelcodes/ooadlabexercisegeg5/ooadlabexerciseg5-main/LeftCanvasControls.java
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class LeftCanvasControls {
    public static JPanel createButtonPanel(LeftCanvas canvas) {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonPanel.setBackground(new Color(45, 45, 45));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Create buttons (Animal and Flower buttons removed)
        JButton composeBtn = createIconButton("icons/Compose.png", "Compose");
        JButton rotateBtn = createIconButton("icons/RotateAll.png", "Rotate");
        JButton saveBtn = createIconButton("icons/Save.png", "Save");
        JButton flipVerticalBtn = createIconButton("icons/FlipVertical.png", "FlipV");
        JButton flipHorizontalBtn = createIconButton("icons/FlipHorizontal.png", "FlipH");
        JButton deleteBtn = createIconButton("icons/Delete.png", "Delete");

        // Set tooltips for hover text
        composeBtn.setToolTipText("Compose Canvas");
        rotateBtn.setToolTipText("Rotate All Items");
        saveBtn.setToolTipText("Save Composition");
        flipVerticalBtn.setToolTipText("Flip Selected Item Vertically");
        flipHorizontalBtn.setToolTipText("Flip Selected Item Horizontally");
        deleteBtn.setToolTipText("Delete Selected Item");

        // Apply action listeners
        composeBtn.addActionListener(e -> canvas.composeCanvas());
        rotateBtn.addActionListener(e -> canvas.rotateCanvas());
        saveBtn.addActionListener(e -> canvas.saveCanvasAsImage());
        flipVerticalBtn.addActionListener(e -> canvas.flipSelectedVertical());
        flipHorizontalBtn.addActionListener(e -> canvas.flipSelectedHorizontal());
        deleteBtn.addActionListener(e -> canvas.deleteSelectedItem());

        // Add buttons to the panel
        buttonPanel.add(composeBtn);
        buttonPanel.add(rotateBtn);
        buttonPanel.add(saveBtn);
        buttonPanel.add(flipVerticalBtn);
        buttonPanel.add(flipHorizontalBtn);
        buttonPanel.add(deleteBtn);

        return buttonPanel;
    }

    private static JButton createIconButton(String iconPath, String fallbackText) {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(50, 50));
        button.setBackground(new Color(70, 70, 70));
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        button.setFocusPainted(false);
        button.setMargin(new Insets(4, 4, 4, 4));

        try {
            File iconFile = new File(iconPath);
            if (iconFile.exists()) {
                ImageIcon icon = new ImageIcon(iconPath);
                Image img = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
                button.setIcon(new ImageIcon(img));
            } else {
                throw new Exception("Icon not found");
            }
        } catch (Exception e) {
            button.setText(fallbackText);
            button.setForeground(Color.WHITE);
            button.setFont(new Font("Arial", Font.BOLD, 10));
            System.err.println("Icon not found for " + iconPath + ", using text fallback.");
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
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                canvas.clearCanvas();
            }
        });

        bottomPanel.add(clearCompositionBtn, BorderLayout.EAST);
        return bottomPanel;
    }
}
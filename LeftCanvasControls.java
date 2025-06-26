// derrelcodes/ooadlabexerciseg5/ooadlabexerciseg5-main/LeftCanvasControls.java
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class LeftCanvasControls {
    private static LeftCanvas canvas;
    private static CollectionPanel collectionPanel;

    public static JPanel createButtonPanel(LeftCanvas canvasRef, CollectionPanel collectionPanelRef) {
        canvas = canvasRef;
        collectionPanel = collectionPanelRef;

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonPanel.setBackground(new Color(45, 45, 45));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // MODIFICATION: Using direct icon paths
        JButton composeBtn = createIconButton("icons/Compose.png", "Compose");
        JButton rotateBtn = createIconButton("icons/RotateAll.png", "Rotate");
        JButton saveBtn = createIconButton("icons/Save.png", "Save");
        JButton flipVerticalBtn = createIconButton("icons/FlipVertical.png", "FlipV");
        JButton flipHorizontalBtn = createIconButton("icons/FlipHorizontal.png", "FlipH");
        JButton deleteBtn = createIconButton("icons/Delete.png", "Delete");

        composeBtn.setToolTipText("Compose Canvas");
        rotateBtn.setToolTipText("Rotate All Items");
        saveBtn.setToolTipText("Save Composition");
        flipVerticalBtn.setToolTipText("Flip Selected Item Vertically");
        flipHorizontalBtn.setToolTipText("Flip Selected Item Horizontally");
        deleteBtn.setToolTipText("Delete Selected Item");

        composeBtn.addActionListener(e -> canvas.composeCanvas());
        rotateBtn.addActionListener(e -> canvas.rotateCanvas());
        saveBtn.addActionListener(e -> showSaveOptions());
        flipVerticalBtn.addActionListener(e -> canvas.flipSelectedVertical());
        flipHorizontalBtn.addActionListener(e -> canvas.flipSelectedHorizontal());
        deleteBtn.addActionListener(e -> canvas.deleteSelectedItem());

        buttonPanel.add(composeBtn);
        buttonPanel.add(rotateBtn);
        buttonPanel.add(saveBtn);
        buttonPanel.add(flipVerticalBtn);
        buttonPanel.add(flipHorizontalBtn);
        buttonPanel.add(deleteBtn);

        return buttonPanel;
    }

    private static void showSaveOptions() {
        if (canvas == null) return;
        Object[] options = {"Save to Library", "Save to File...", "Cancel"};
        int choice = JOptionPane.showOptionDialog(canvas, "Where would you like to save this composition?",
                "Save Composition", JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (choice == JOptionPane.YES_OPTION) {
            canvas.saveToLibrary(collectionPanel);
        } else if (choice == JOptionPane.NO_OPTION) {
            canvas.saveToFile();
        }
    }

    // MODIFICATION: Removed path guessing
    private static JButton createIconButton(String iconPath, String fallbackText) {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(50, 50));
        button.setBackground(new Color(70, 70, 70));
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        button.setFocusPainted(false);
        button.setMargin(new Insets(4, 4, 4, 4));

        ImageIcon icon = new ImageIcon(iconPath);
        if (icon.getIconWidth() > 0) {
            Image img = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            button.setIcon(new ImageIcon(img));
        } else {
            button.setText(fallbackText);
            button.setForeground(Color.WHITE);
            button.setFont(new Font("Arial", Font.BOLD, 10));
            System.err.println("Icon not found: " + iconPath);
        }

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { button.setBackground(new Color(90, 90, 90)); }
            public void mouseExited(java.awt.event.MouseEvent evt) { button.setBackground(new Color(70, 70, 70)); }
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
        clearCompositionBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(canvas, "Are you sure you want to clear the composition?",
                    "Clear Composition", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                canvas.clearCanvas();
            }
        });
        bottomPanel.add(clearCompositionBtn, BorderLayout.EAST);
        return bottomPanel;
    }
}
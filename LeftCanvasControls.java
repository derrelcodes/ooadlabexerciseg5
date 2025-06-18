import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File; // Import File for icon loading

public class LeftCanvasControls {
    public static JPanel createButtonPanel(LeftCanvas canvas) {
        // Change to FlowLayout.CENTER for central alignment and consistent gaps
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5)); // Modified for uniform gaps and center alignment
        buttonPanel.setBackground(new Color(45, 45, 45)); //
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); //

        // Create buttons using the new icon/label helpers
        JButton rotateBtn = createIconButton("Rotate.png"); // Assuming Rotate.png exists
        JButton deleteBtn = createIconButton("Delete.png"); // Assuming Delete.png exists
        JButton saveBtn = createIconButton("Save.png");     // Assuming Save.png exists
        JButton flipVerticalBtn = createIconButton("FlipVertical.png"); // Assuming FlipVertical.png exists
        JButton flipHorizontalBtn = createIconButton("FlipHorizontal.png"); // Assuming FlipHorizontal.png exists

        rotateBtn.setToolTipText("Rotate Canvas"); //
        deleteBtn.setToolTipText("Delete Selected Image");
        saveBtn.setToolTipText("Save Composition"); //
        flipVerticalBtn.setToolTipText("Flip Selected Image Vertically");
        flipHorizontalBtn.setToolTipText("Flip Selected Image Horizontally");

        // Apply action listeners
        rotateBtn.addActionListener((ActionEvent e) -> canvas.rotateCanvas()); //

        // Action for the new Delete button
        deleteBtn.addActionListener((ActionEvent e) -> {
            canvas.deleteSelectedItem(); // Calls the new method in LeftCanvas
        });

        saveBtn.addActionListener((ActionEvent e) -> canvas.saveCanvasAsImage()); //

        flipVerticalBtn.addActionListener(e -> canvas.flipSelectedVertical());
        flipHorizontalBtn.addActionListener(e -> canvas.flipSelectedHorizontal());

        // Add wrapped buttons to the panel
        buttonPanel.add(wrapButtonWithLabel(rotateBtn, "Rotate"));
        buttonPanel.add(wrapButtonWithLabel(deleteBtn, "Delete")); // Replaced Refresh with Delete
        buttonPanel.add(wrapButtonWithLabel(saveBtn, "Save"));
        buttonPanel.add(wrapButtonWithLabel(flipVerticalBtn, "Flip Vertical"));
        buttonPanel.add(wrapButtonWithLabel(flipHorizontalBtn, "Flip Horizontal"));

        return buttonPanel;
    }

    // Helper method to create an icon-only button with common styling
    private static JButton createIconButton(String iconPath) {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(56, 56)); // Consistent size
        button.setBackground(new Color(70, 70, 70)); // Consistent background
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        button.setFocusPainted(false);
        button.setMargin(new Insets(4, 4, 4, 4));

        boolean iconLoaded = false;
        String[] possiblePaths = {
            "icons/" + iconPath, // Prioritize "icons/" folder
            "src/icons/" + iconPath,
            "resources/" + iconPath,
            iconPath, // Fallback to direct path
            System.getProperty("user.dir") + "/" + iconPath
        };

        for (String path : possiblePaths) {
            try {
                File iconFile = new File(path);
                if (iconFile.exists()) {
                    ImageIcon icon = new ImageIcon(path);
                    if (icon.getIconWidth() > 0) {
                        Image img = icon.getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH); // Slightly smaller icon
                        button.setIcon(new ImageIcon(img));
                        iconLoaded = true;
                        break;
                    }
                }
            } catch (Exception ignored) {}
        }

        if (!iconLoaded) {
            // Placeholder text if no icon, the actual label will be below via wrapButtonWithLabel
            button.setText("?"); // Use '?' as a placeholder if icon is not found.
            button.setForeground(Color.WHITE);
            button.setFont(new Font("Arial", Font.BOLD, 16));
            System.out.println("Icon not found for " + iconPath + ", using text fallback");
        }

        // Add hover effect
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

    // Helper method to wrap a button with a text label below it (copied from RightCanvasControls)
    private static JPanel wrapButtonWithLabel(JButton button, String labelText) {
        JPanel wrapperPanel = new JPanel();
        wrapperPanel.setLayout(new BoxLayout(wrapperPanel, BoxLayout.Y_AXIS)); // Vertical arrangement
        wrapperPanel.setBackground(new Color(45, 45, 45)); // Match top panel background
        wrapperPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2)); // Small padding around wrapper

        JLabel label = new JLabel(labelText, SwingConstants.CENTER); // Label for the button name
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.PLAIN, 10)); // Smaller font for label
        label.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the label

        button.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the button

        wrapperPanel.add(button);
        wrapperPanel.add(label);

        // Set a fixed preferred size to the wrapper panel for uniform button unit sizing
        wrapperPanel.setPreferredSize(new Dimension(70, 80)); // Consistent size with RightCanvasControls
        wrapperPanel.setMaximumSize(new Dimension(70, 80)); // Ensure it doesn't grow beyond this

        return wrapperPanel;
    }

    public static JPanel createBottomPanel(LeftCanvas canvas) {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(45, 45, 45)); //
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); //

        JButton clearCompositionBtn = new JButton("Clear Composition"); //
        clearCompositionBtn.setBackground(new Color(220, 53, 69)); //
        clearCompositionBtn.setForeground(Color.WHITE); //
        clearCompositionBtn.setFocusPainted(false); //
        clearCompositionBtn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); //
        clearCompositionBtn.setFont(new Font("Arial", Font.BOLD, 14)); //

        clearCompositionBtn.addMouseListener(new java.awt.event.MouseAdapter() { //
            public void mouseEntered(java.awt.event.MouseEvent evt) { //
                clearCompositionBtn.setBackground(new Color(255, 80, 90)); //
            }
            public void mouseExited(java.awt.event.MouseEvent evt) { //
                clearCompositionBtn.setBackground(new Color(220, 53, 69)); //
            }
        });

        clearCompositionBtn.addActionListener((ActionEvent e) -> { //
            int confirm = JOptionPane.showConfirmDialog(null, //
                    "Are you sure you want to clear the composition? This action cannot be undone.", //
                    "Clear Composition", //
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE //
            );
            if (confirm == JOptionPane.YES_OPTION) { //
                canvas.clearCanvas(); //
            }
        });

        bottomPanel.add(clearCompositionBtn, BorderLayout.EAST); //

        return bottomPanel;
    }
}
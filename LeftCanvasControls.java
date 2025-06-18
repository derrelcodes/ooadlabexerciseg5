import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LeftCanvasControls {
    public static JPanel createButtonPanel(LeftCanvas canvas) {
        // Change to FlowLayout.LEFT for consistency with RightCanvasControls
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        // Change background color to match RightCanvasControls
        buttonPanel.setBackground(new Color(45, 45, 45));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Add padding for the panel

        JButton rotateBtn = new JButton("Rotate");
        rotateBtn.setToolTipText("Rotate Canvas"); // Add tool tip
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setToolTipText("Clear Canvas"); // Add tool tip
        JButton saveBtn = new JButton("Save");
        saveBtn.setToolTipText("Save Composition"); // Add tool tip

        // Apply consistent button styling
        styleButton(rotateBtn);
        styleButton(refreshBtn);
        styleButton(saveBtn);

        rotateBtn.addActionListener((ActionEvent e) -> canvas.rotateCanvas());

        refreshBtn.addActionListener((ActionEvent e) -> {
            int confirm = JOptionPane.showConfirmDialog(null,
                    "Are you sure you want to clear the canvas? This action cannot be undone.", // More descriptive message
                    "Clear Canvas",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE
            );
            if (confirm == JOptionPane.YES_OPTION) {
                canvas.clearCanvas();
            }
        });

        saveBtn.addActionListener((ActionEvent e) -> canvas.saveCanvasAsImage());

        buttonPanel.add(rotateBtn);
        buttonPanel.add(Box.createHorizontalStrut(10)); // Add strut for spacing consistency
        buttonPanel.add(refreshBtn);
        buttonPanel.add(Box.createHorizontalStrut(10)); // Add strut for spacing consistency
        buttonPanel.add(saveBtn);

        return buttonPanel;
    }

    // Helper method to style buttons consistently with RightCanvasControls.createToolButton
    private static void styleButton(JButton button) {
        button.setPreferredSize(new Dimension(56, 56)); // Consistent size
        button.setBackground(new Color(70, 70, 70)); // Consistent background
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        button.setFocusPainted(false);
        button.setMargin(new Insets(4, 4, 4, 4)); // Consistent padding
        button.setForeground(Color.WHITE); // Consistent text color (fallback in RightCanvasControls)
        button.setFont(new Font("Arial", Font.BOLD, 12)); // Adjust font size to fit text

        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(90, 90, 90));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(70, 70, 70));
            }
        });
    }
}
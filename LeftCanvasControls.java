import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LeftCanvasControls {
    public static JPanel createButtonPanel(LeftCanvas canvas) {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(new Color(245, 245, 245));

        JButton rotateBtn = new JButton("Rotate");
        rotateBtn.setPreferredSize(new Dimension(90, 30));
        rotateBtn.addActionListener((ActionEvent e) -> canvas.rotateCanvas());

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setPreferredSize(new Dimension(90, 30));
        refreshBtn.addActionListener((ActionEvent e) -> {
            int confirm = JOptionPane.showConfirmDialog(null,
                    "Clear the entire canvas?", "Confirm",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                canvas.clearCanvas();
            }
        });

        JButton saveBtn = new JButton("Save");
        saveBtn.setPreferredSize(new Dimension(90, 30));
        saveBtn.addActionListener((ActionEvent e) -> canvas.saveCanvasAsImage());

        buttonPanel.add(rotateBtn);
        buttonPanel.add(refreshBtn);
        buttonPanel.add(saveBtn);

        return buttonPanel;
    }
}

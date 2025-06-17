import javax.swing.*;
import java.awt.*;

public class LeftCanvasControls {

    public static JPanel createButtonPanel(LeftCanvas canvas) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton rotateBtn = new JButton("Rotate");
        JButton refreshBtn = new JButton("Refresh");
        JButton saveBtn = new JButton("Save");

        // Dummy actions — replace with actual canvas logic if needed
        rotateBtn.addActionListener(e -> {
            System.out.println("Rotate button clicked");
            // canvas.rotateSelectedItem(); // if implemented
        });

        refreshBtn.addActionListener(e -> {
            System.out.println("Refresh button clicked");
            canvas.repaint();
        });

        saveBtn.addActionListener(e -> {
            System.out.println("Save button clicked");
            // canvas.saveCanvasAsImage(); // if implemented
        });

        panel.add(rotateBtn);
        panel.add(refreshBtn);
        panel.add(saveBtn);

        return panel;
    }
}

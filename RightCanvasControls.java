import javax.swing.*;
import java.awt.*;

public class RightCanvasControls {

    public static JPanel createTopPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4));
        panel.add(createIconButton("pen.png"));
        panel.add(createIconButton("color.png"));
        panel.add(createIconButton("size.png"));
        panel.add(createIconButton("eraser.png"));
        return panel;
    }

    public static JPanel createBottomPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2));
        panel.add(createIconButton("save_drawing.png"));
        panel.add(createIconButton("refresh_drawing.png"));
        return panel;
    }

    private static JButton createIconButton(String filename) {
        ImageIcon icon = new ImageIcon("assets/icons/" + filename);
        Image scaled = icon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
        return new JButton(new ImageIcon(scaled));
    }
}

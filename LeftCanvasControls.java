import javax.swing.*;
import java.awt.*;

public class LeftCanvasControls extends JPanel {
    public LeftCanvasControls() {
        setLayout(new GridLayout(1, 3));

        add(createIconButton("rotate.png"));
        add(createIconButton("refresh.png"));
        add(createIconButton("save.png"));
    }

    private JButton createIconButton(String filename) {
        ImageIcon icon = new ImageIcon("assets/icons/" + filename);
        Image scaled = icon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
        return new JButton(new ImageIcon(scaled));
    }
}

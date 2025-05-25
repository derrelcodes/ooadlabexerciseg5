import javax.swing.*;
import java.awt.*;

public class DrawingStudioPro {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame());
    }
}

// Bundled MainFrame class
class MainFrame extends JFrame {
    public MainFrame() {
        setTitle("Drawing Studio Pro");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Split into 3 sections: Left Canvas | Right Canvas | Collection Panel
        JPanel centerPanel = new JPanel(new GridLayout(1, 3));

        // Left side with canvas and bottom controls
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(new LeftCanvas(), BorderLayout.CENTER);
        leftPanel.add(new LeftCanvasControls(), BorderLayout.SOUTH);

        // Right side with top and bottom toolbar
        JPanel rightPanel = new JPanel(new BorderLayout());
RightCanvas rightCanvas = new RightCanvas(); // Create canvas instance first

// Pass the canvas to the controls
rightPanel.add(RightCanvasControls.createTopPanel(rightCanvas), BorderLayout.NORTH);
rightPanel.add(rightCanvas, BorderLayout.CENTER);
rightPanel.add(RightCanvasControls.createBottomPanel(), BorderLayout.SOUTH);

        // Add all to the center
        centerPanel.add(leftPanel);
        centerPanel.add(rightPanel);
        centerPanel.add(new CollectionPanel());

        add(centerPanel, BorderLayout.CENTER);
        setVisible(true);
    }
}

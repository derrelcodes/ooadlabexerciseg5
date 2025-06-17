import javax.swing.*;
import java.awt.*;

public class DrawingStudioPro {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame());
    }

    static class MainFrame extends JFrame {
        public MainFrame() {
            setTitle("Drawing Studio Pro");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLayout(new BorderLayout());

            // ===== LEFT: Collection Panel (Media)
            JPanel mediaPanel = new JPanel(new BorderLayout());
            mediaPanel.setPreferredSize(new Dimension(300, 0)); // Full height, fixed width
            mediaPanel.add(new CollectionPanel(), BorderLayout.CENTER);

            // ===== CENTER: Canvas Panel (Left + Right Canvas)
            JPanel canvasPanel = new JPanel(new GridLayout(1, 2));

            // LEFT CANVAS with control buttons below
            JPanel leftPanel = new JPanel(new BorderLayout());
            LeftCanvas leftCanvas = new LeftCanvas();
            leftPanel.add(leftCanvas, BorderLayout.CENTER);
            leftPanel.add(LeftCanvasControls.createButtonPanel(leftCanvas), BorderLayout.SOUTH);

            // RIGHT CANVAS with tools on top/bottom
            JPanel rightPanel = new JPanel(new BorderLayout());
            RightCanvas rightCanvas = new RightCanvas();
            rightPanel.add(RightCanvasControls.createTopPanel(rightCanvas), BorderLayout.NORTH);
            rightPanel.add(rightCanvas, BorderLayout.CENTER);
            rightPanel.add(RightCanvasControls.createBottomPanel(), BorderLayout.SOUTH);

            // Add both canvases to the center panel
            canvasPanel.add(leftPanel);
            canvasPanel.add(rightPanel);

            // ===== Assemble full UI
            add(mediaPanel, BorderLayout.WEST);     // Media (Collection) panel on the left
            add(canvasPanel, BorderLayout.CENTER);  // Canvases on the right

            // Final frame setup
            setSize(1200, 700);
            setLocationRelativeTo(null); // Center the window
            setVisible(true);
        }
    }
}

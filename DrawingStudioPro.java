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
            mediaPanel.setPreferredSize(new Dimension(300, 0));
            mediaPanel.add(new CollectionPanel(), BorderLayout.CENTER);

            // ===== CENTER: Canvas Panel (Left + Right Canvas)
            JPanel canvasPanel = new JPanel(new GridLayout(1, 2));

            // LEFT PANEL: "Composition"
            LeftCanvas leftCanvas = new LeftCanvas();
            JPanel leftPanel = new JPanel(new BorderLayout());

            JLabel leftLabel = new JLabel("Composition", SwingConstants.CENTER);
            leftLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
            leftPanel.add(leftLabel, BorderLayout.NORTH);
            leftPanel.add(leftCanvas, BorderLayout.CENTER);
            leftPanel.add(LeftCanvasControls.createButtonPanel(leftCanvas), BorderLayout.SOUTH);

            // RIGHT PANEL: "Drawing Pad"
            RightCanvas rightCanvas = new RightCanvas();
            JPanel rightPanel = new JPanel(new BorderLayout());

            // Drawing Pad Title Label
            JLabel rightLabel = new JLabel("Drawing Pad", SwingConstants.CENTER);
            rightLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
            rightLabel.setForeground(Color.WHITE);
            rightLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

            // Wrap title + toolbar in one top panel
            JPanel topRightPanel = new JPanel(new BorderLayout());
            topRightPanel.setBackground(Color.DARK_GRAY);
            topRightPanel.add(rightLabel, BorderLayout.NORTH);
            topRightPanel.add(RightCanvasControls.createTopPanel(rightCanvas), BorderLayout.CENTER);

            rightPanel.add(topRightPanel, BorderLayout.NORTH);
            rightPanel.add(rightCanvas, BorderLayout.CENTER);
            rightPanel.add(RightCanvasControls.createBottomPanel(), BorderLayout.SOUTH);

            // Combine both panels
            canvasPanel.add(leftPanel);
            canvasPanel.add(rightPanel);

            // ===== Assemble Main Layout
            add(mediaPanel, BorderLayout.WEST);
            add(canvasPanel, BorderLayout.CENTER);

            setSize(1200, 700);
            setLocationRelativeTo(null);
            setVisible(true);
        }
    }
}

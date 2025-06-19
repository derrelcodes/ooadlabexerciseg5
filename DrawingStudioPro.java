// In DrawingStudioPro.java

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
            mediaPanel.setPreferredSize(new Dimension(270, 0));
            mediaPanel.add(new CollectionPanel(), BorderLayout.CENTER);

            // ===== Canvas Panels (Left + Right Canvas)
            JSplitPane canvasSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
            canvasSplitPane.setDividerSize(8);
            canvasSplitPane.setContinuousLayout(true);
            canvasSplitPane.setResizeWeight(0.5);

            // LEFT PANEL: "Composition"
            LeftCanvas leftCanvas = new LeftCanvas();
            leftCanvas.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 5));

            JPanel leftPanel = new JPanel(new BorderLayout());

            JLabel leftLabel = new JLabel("Composition Canvas", SwingConstants.CENTER);
            leftLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
            leftLabel.setForeground(Color.WHITE);
            leftLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

            JPanel topLeftPanel = new JPanel(new BorderLayout());
            topLeftPanel.setBackground(new Color(45, 45, 45));
            JPanel leftControls = LeftCanvasControls.createButtonPanel(leftCanvas); // Get controls
            topLeftPanel.add(leftLabel, BorderLayout.NORTH);
            topLeftPanel.add(leftControls, BorderLayout.CENTER); // Add controls

            leftPanel.add(topLeftPanel, BorderLayout.NORTH);
            leftPanel.add(leftCanvas, BorderLayout.CENTER);
            leftPanel.add(LeftCanvasControls.createBottomPanel(leftCanvas), BorderLayout.SOUTH);

            // RIGHT PANEL: "Drawing Pad"
            RightCanvas rightCanvas = new RightCanvas();
            rightCanvas.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 5));

            JPanel rightPanel = new JPanel(new BorderLayout());

            JLabel rightLabel = new JLabel("Drawing Pad", SwingConstants.CENTER);
            rightLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
            rightLabel.setForeground(Color.WHITE);
            rightLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

            JPanel topRightPanel = new JPanel(new BorderLayout());
            topRightPanel.setBackground(new Color(45, 45, 45));
            JPanel rightControls = RightCanvasControls.createTopPanel(rightCanvas); // Get controls
            topRightPanel.add(rightLabel, BorderLayout.NORTH);
            topRightPanel.add(rightControls, BorderLayout.CENTER); // Add controls

            rightPanel.add(topRightPanel, BorderLayout.NORTH);
            rightPanel.add(rightCanvas, BorderLayout.CENTER);
            rightPanel.add(RightCanvasControls.createBottomPanel(), BorderLayout.SOUTH);

            // Add the left and right panels to the JSplitPane
            canvasSplitPane.setLeftComponent(leftPanel);
            canvasSplitPane.setRightComponent(rightPanel);

            // ===== Assemble Main Layout
            add(mediaPanel, BorderLayout.WEST);
            add(canvasSplitPane, BorderLayout.CENTER);

            // Make the window open in full-screen mode
            setExtendedState(JFrame.MAXIMIZED_BOTH);
            setSize(1200, 700); // Fallback size
            setLocationRelativeTo(null);
            setVisible(true);

            // Set divider location and minimum panel sizes after frame is visible
            SwingUtilities.invokeLater(() -> {
                // Limit slider movement by setting minimum panel sizes
                leftPanel.setMinimumSize(new Dimension(leftControls.getPreferredSize().width + 30, 0));
                rightPanel.setMinimumSize(new Dimension(rightControls.getPreferredSize().width + 30, 0));
                // Set the divider to be exactly in the middle on startup
                canvasSplitPane.setDividerLocation(0.5);
            });
        }
    }
}
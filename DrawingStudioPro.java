// DrawingStudioPro.java (Main class and main frame layout)
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class DrawingStudioPro {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame());
    }
}

// Bundled MainFrame class
class MainFrame extends JFrame {
    private JPanel collectionWrapper;
    private boolean collectionVisible = true;

    public MainFrame() {
        setTitle("Drawing Studio Pro");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Wrapper panel for left side controls and toggle
        JPanel leftSidebar = new JPanel();
        leftSidebar.setLayout(new BorderLayout());

        // Toggle button to show/hide collection panel
        JButton toggleCollectionBtn = new JButton("☰ Media");
        toggleCollectionBtn.addActionListener(e -> {
            collectionVisible = !collectionVisible;
            collectionWrapper.setVisible(collectionVisible);
            revalidate();
        });
        leftSidebar.add(toggleCollectionBtn, BorderLayout.NORTH);

        // Wrapped collection panel with half-height
        collectionWrapper = new JPanel(new BorderLayout());
        CollectionPanel collectionPanel = new CollectionPanel();
        collectionWrapper.add(collectionPanel, BorderLayout.NORTH);
        collectionWrapper.setPreferredSize(new Dimension(250, 300)); // half height-ish
        leftSidebar.add(collectionWrapper, BorderLayout.CENTER);

        // Central canvas area (Left + Right)
        JPanel canvasPanel = new JPanel(new GridLayout(1, 2));

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(new LeftCanvas(), BorderLayout.CENTER);
        leftPanel.add(new LeftCanvasControls(), BorderLayout.SOUTH);

        JPanel rightPanel = new JPanel(new BorderLayout());
        RightCanvas rightCanvas = new RightCanvas(); // create it once and reuse
        rightPanel.add(RightCanvasControls.createTopPanel(rightCanvas), BorderLayout.NORTH);
        rightPanel.add(rightCanvas, BorderLayout.CENTER);
        rightPanel.add(RightCanvasControls.createBottomPanel(), BorderLayout.SOUTH);


        canvasPanel.add(leftPanel);
        canvasPanel.add(rightPanel);

        // Add panels to frame
        add(leftSidebar, BorderLayout.WEST);
        add(canvasPanel, BorderLayout.CENTER);

        setVisible(true);
    }
}
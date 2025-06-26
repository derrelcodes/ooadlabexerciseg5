// derrelcodes/ooadlabexerciseg5/ooadlabexerciseg5-main/RightCanvasControls.java
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class RightCanvasControls {
    private static RightCanvas canvas;
    private static CollectionPanel collectionPanel;
    private static JButton penButton, eraserButton, colorButton, penSizeButton, saveButton;

    public static JPanel createTopPanel(RightCanvas canvasRef, CollectionPanel collectionPanelRef) {
        canvas = canvasRef;
        collectionPanel = collectionPanelRef;

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        topPanel.setBackground(new Color(45, 45, 45));
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // MODIFICATION: Using direct icon paths
        penButton = createIconButton("icons/Pen.png", "Pen");
        eraserButton = createIconButton("icons/Eraser.png", "Erase");
        colorButton = createIconButton("icons/Pen Colour.png", "Color");
        penSizeButton = createIconButton("icons/Pen Size.png", "Size");
        saveButton = createIconButton("icons/Save.png", "Save");

        penButton.setToolTipText("Pen");
        eraserButton.setToolTipText("Eraser");
        colorButton.setToolTipText("Color Picker");
        penSizeButton.setToolTipText("Pen Size");
        saveButton.setToolTipText("Save Drawing");

        penButton.addActionListener(e -> { canvas.setEraser(false); updateButtonStates(); });
        eraserButton.addActionListener(e -> { canvas.setEraser(true); updateButtonStates(); });
        colorButton.addActionListener(e -> showSimpleColorPicker());
        penSizeButton.addActionListener(e -> showPenSizePicker());
        saveButton.addActionListener(e -> saveDrawing());

        topPanel.add(penButton);
        topPanel.add(eraserButton);
        topPanel.add(colorButton);
        topPanel.add(penSizeButton);
        topPanel.add(saveButton);

        updateButtonStates();
        return topPanel;
    }
    
    // MODIFICATION: Removed path guessing
    private static JButton createIconButton(String iconPath, String fallbackText) {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(56, 56));
        button.setBackground(new Color(70, 70, 70));
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        button.setFocusPainted(false);

        ImageIcon icon = new ImageIcon(iconPath);
        if (icon.getIconWidth() > 0) {
            button.setIcon(new ImageIcon(icon.getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH)));
        } else {
            button.setText(fallbackText);
            button.setForeground(Color.WHITE);
            System.err.println("Icon not found: " + iconPath);
        }

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { button.setBackground(new Color(90, 90, 90)); }
            public void mouseExited(java.awt.event.MouseEvent evt) { updateButtonStates(); }
        });
        return button;
    }
    
    // ... other methods remain the same
    private static void showSimpleColorPicker() {
        if (canvas != null) {
            JDialog colorDialog = new JDialog((Frame) null, "Choose Color", true);
            colorDialog.setLayout(new BorderLayout());
            colorDialog.setSize(480, 300);
            colorDialog.setLocationRelativeTo(null);
            colorDialog.setResizable(false);
            JPanel colorGrid = new JPanel(new GridLayout(6, 8, 2, 2));
            colorGrid.setBackground(Color.WHITE);
            colorGrid.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            Color[] colors = { Color.BLACK, Color.DARK_GRAY, Color.GRAY, Color.LIGHT_GRAY, Color.WHITE, new Color(128, 0, 0), Color.RED, new Color(255, 128, 128), new Color(0, 128, 0), Color.GREEN, new Color(128, 255, 128), new Color(0, 255, 0), new Color(0, 128, 128), Color.CYAN, new Color(128, 255, 255), new Color(0, 255, 255), new Color(0, 0, 128), Color.BLUE, new Color(128, 128, 255), new Color(0, 0, 255), new Color(128, 0, 128), Color.MAGENTA, new Color(255, 128, 255), new Color(255, 0, 255), new Color(128, 128, 0), Color.YELLOW, new Color(255, 255, 128), new Color(255, 255, 0), new Color(128, 64, 0), Color.ORANGE, new Color(255, 200, 128), new Color(255, 165, 0), new Color(160, 82, 45), new Color(210, 180, 140), new Color(244, 164, 96), new Color(222, 184, 135), Color.PINK, new Color(255, 192, 203), new Color(255, 20, 147), new Color(199, 21, 133), new Color(75, 0, 130), new Color(148, 0, 211), new Color(138, 43, 226), new Color(153, 50, 204), new Color(0, 100, 0), new Color(34, 139, 34), new Color(50, 205, 50), new Color(124, 252, 0) };
            for (Color color : colors) {
                JButton colorButton = new JButton();
                colorButton.setBackground(color);
                colorButton.setPreferredSize(new Dimension(50, 35));
                colorButton.setBorder(BorderFactory.createRaisedBevelBorder());
                colorButton.setOpaque(true);
                colorButton.addActionListener(e -> { canvas.setDrawingColor(color); colorDialog.dispose(); });
                colorButton.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseEntered(java.awt.event.MouseEvent evt) { colorButton.setBorder(BorderFactory.createLoweredBevelBorder()); }
                    public void mouseExited(java.awt.event.MouseEvent evt) { colorButton.setBorder(BorderFactory.createRaisedBevelBorder()); }
                });
                colorGrid.add(colorButton);
            }
            JLabel titleLabel = new JLabel("Select a Color", SwingConstants.CENTER);
            titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
            titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
            colorDialog.add(titleLabel, BorderLayout.NORTH);
            colorDialog.add(colorGrid, BorderLayout.CENTER);
            colorDialog.setVisible(true);
        }
    }
    private static void showPenSizePicker() {
        if (canvas != null) {
            String[] sizes = {"1", "2", "3", "5", "8", "10", "15", "20", "25", "30"};
            String selectedSize = (String) JOptionPane.showInputDialog(null, "Select Pen Size:", "Pen Size", JOptionPane.QUESTION_MESSAGE, null, sizes, String.valueOf(canvas.getStrokeSize()));
            if (selectedSize != null) { try { canvas.setStrokeSize(Integer.parseInt(selectedSize)); } catch (NumberFormatException e) { JOptionPane.showMessageDialog(null, "Invalid size!", "Error", JOptionPane.ERROR_MESSAGE); } }
        }
    }
    private static void saveDrawing() {
        if (canvas == null) return;
        Object[] options = {"Save to Library", "Save to File...", "Cancel"};
        int choice = JOptionPane.showOptionDialog(null, "Where would you like to save this drawing?", "Save Drawing", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (choice == JOptionPane.YES_OPTION) { saveToLibrary(); } else if (choice == JOptionPane.NO_OPTION) { saveToFile(); }
    }
    private static void saveToLibrary() {
        File assetsDir = new File("assets/images");
        if (!assetsDir.exists()) assetsDir.mkdirs();
        String fileName = "drawing_" + UUID.randomUUID().toString().substring(0, 8) + ".png";
        File fileToSave = new File(assetsDir, fileName);
        try {
            ImageIO.write(canvas.getCanvasImage(), "PNG", fileToSave);
            JOptionPane.showMessageDialog(null, "Drawing saved to library!", "Save Complete", JOptionPane.INFORMATION_MESSAGE);
            if (collectionPanel != null) collectionPanel.refreshImagesTab();
        } catch (IOException e) { JOptionPane.showMessageDialog(null, "Error saving to library: " + e.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE); }
    }
    private static void saveToFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Drawing to File");
        fileChooser.setFileFilter(new FileNameExtensionFilter("PNG Images", "png"));
        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getName().toLowerCase().endsWith(".png")) { fileToSave = new File(fileToSave.getAbsolutePath() + ".png"); }
            try {
                ImageIO.write(canvas.getCanvasImage(), "PNG", fileToSave);
                JOptionPane.showMessageDialog(null, "Drawing saved successfully!", "Save Complete", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) { JOptionPane.showMessageDialog(null, "Error saving drawing: " + e.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE); }
        }
    }
    public static JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(45, 45, 45));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        JButton clearButton = new JButton("Clear Drawing");
        clearButton.setBackground(new Color(220, 53, 69));
        clearButton.setForeground(Color.WHITE);
        clearButton.addActionListener(e -> clearCanvas());
        bottomPanel.add(clearButton, BorderLayout.EAST);
        return bottomPanel;
    }
    private static void clearCanvas() {
        if (canvas != null) {
            int result = JOptionPane.showConfirmDialog(null, "Are you sure you want to clear the drawing?", "Clear Drawing", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (result == JOptionPane.YES_OPTION) { canvas.clearCanvas(); }
        }
    }
    private static void updateButtonStates() {
        if (canvas != null && penButton != null && eraserButton != null) {
            penButton.setBackground(!canvas.isEraser() ? new Color(100, 150, 100) : new Color(70, 70, 70));
            eraserButton.setBackground(canvas.isEraser() ? new Color(100, 150, 100) : new Color(70, 70, 70));
        }
    }
}
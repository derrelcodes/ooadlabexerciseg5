import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class RightCanvasControls {
    private static RightCanvas canvas;
    private static JButton penButton, eraserButton, colorButton, sizeButton, saveButton;
    private static JLabel statusLabel;

    public static JPanel createTopPanel(RightCanvas canvasRef) {
        canvas = canvasRef;
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(new Color(45, 45, 45));
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        penButton = createToolButton("Pen", "Pen.png", e -> {
            if (canvas != null) {
                canvas.setEraser(false);
                updateButtonStates();
                updateStatus("Pen tool selected");
            }
        });

        eraserButton = createToolButton("Eraser", "Eraser.png", e -> {
            if (canvas != null) {
                canvas.setEraser(true);
                updateButtonStates();
                updateStatus("Eraser tool selected");
            }
        });

        colorButton = createToolButton("Color", "Pen Colour.png", e -> showSimpleColorPicker());

        sizeButton = createToolButton("Size", "Pen Size.png", e -> showSizePicker());

        saveButton = createToolButton("Save", "Save.png", e -> saveDrawing());

        topPanel.add(penButton);
        topPanel.add(eraserButton);
        topPanel.add(Box.createHorizontalStrut(10));
        topPanel.add(colorButton);
        topPanel.add(sizeButton);
        topPanel.add(Box.createHorizontalStrut(10));
        topPanel.add(saveButton);

        return topPanel;
    }

    public static JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(45, 45, 45));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        statusLabel = new JLabel("Ready - Select a tool to start drawing");
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        JButton clearButton = new JButton("Clear Canvas");
        clearButton.setBackground(new Color(220, 53, 69));
        clearButton.setForeground(Color.WHITE);
        clearButton.setFocusPainted(false);
        clearButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        clearButton.addActionListener(e -> clearCanvas());

        bottomPanel.add(statusLabel, BorderLayout.WEST);
        bottomPanel.add(clearButton, BorderLayout.EAST);

        return bottomPanel;
    }

    private static JButton createToolButton(String tooltip, String iconPath, ActionListener listener) {
        JButton button = new JButton();
        button.setToolTipText(tooltip);
        button.setPreferredSize(new Dimension(56, 56));
        button.setBackground(new Color(70, 70, 70));
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        button.setFocusPainted(false);
        button.setMargin(new Insets(4, 4, 4, 4)); // Internal padding

        boolean iconLoaded = false;
        String[] possiblePaths = {
            iconPath,
            "icons/" + iconPath,
            "src/icons/" + iconPath,
            "resources/" + iconPath,
            System.getProperty("user.dir") + "/" + iconPath
        };

        for (String path : possiblePaths) {
            try {
                File iconFile = new File(path);
                if (iconFile.exists()) {
                    ImageIcon icon = new ImageIcon(path);
                    if (icon.getIconWidth() > 0) {
                        Image img = icon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
                        button.setIcon(new ImageIcon(img));
                        iconLoaded = true;
                        break;
                    }
                }
            } catch (Exception ignored) {}
        }

        if (!iconLoaded) {
            button.setText(tooltip.substring(0, 1));
            button.setForeground(Color.WHITE);
            button.setFont(new Font("Arial", Font.BOLD, 16));
            System.out.println("Icon not found for " + tooltip + ", using text fallback");
        }

        button.addActionListener(listener);

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(90, 90, 90));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                if ((button == penButton && !canvas.isEraser()) ||
                    (button == eraserButton && canvas.isEraser())) {
                    button.setBackground(new Color(100, 150, 100)); // active
                } else {
                    button.setBackground(new Color(70, 70, 70)); // default
                }
            }
        });

        return button;
    }
    
    private static void showSimpleColorPicker() {
        if (canvas != null) {
            // Create a simple color picker dialog with 48 basic colors
            JDialog colorDialog = new JDialog((Frame) null, "Choose Color", true);
            colorDialog.setLayout(new BorderLayout());
            colorDialog.setSize(480, 300);
            colorDialog.setLocationRelativeTo(null);
            colorDialog.setResizable(false);
            
            // Create color grid panel
            JPanel colorGrid = new JPanel(new GridLayout(6, 8, 2, 2));
            colorGrid.setBackground(Color.WHITE);
            colorGrid.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            // Define 48 basic colors
            Color[] colors = {
                // Row 1 - Basic colors
                Color.BLACK, Color.DARK_GRAY, Color.GRAY, Color.LIGHT_GRAY, 
                Color.WHITE, new Color(128, 0, 0), Color.RED, new Color(255, 128, 128),
                
                // Row 2 - Greens
                new Color(0, 128, 0), Color.GREEN, new Color(128, 255, 128), new Color(0, 255, 0),
                new Color(0, 128, 128), Color.CYAN, new Color(128, 255, 255), new Color(0, 255, 255),
                
                // Row 3 - Blues
                new Color(0, 0, 128), Color.BLUE, new Color(128, 128, 255), new Color(0, 0, 255),
                new Color(128, 0, 128), Color.MAGENTA, new Color(255, 128, 255), new Color(255, 0, 255),
                
                // Row 4 - Yellows/Oranges
                new Color(128, 128, 0), Color.YELLOW, new Color(255, 255, 128), new Color(255, 255, 0),
                new Color(128, 64, 0), Color.ORANGE, new Color(255, 200, 128), new Color(255, 165, 0),
                
                // Row 5 - Browns/Pinks
                new Color(160, 82, 45), new Color(210, 180, 140), new Color(244, 164, 96), new Color(222, 184, 135),
                Color.PINK, new Color(255, 192, 203), new Color(255, 20, 147), new Color(199, 21, 133),
                
                // Row 6 - Additional colors
                new Color(75, 0, 130), new Color(148, 0, 211), new Color(138, 43, 226), new Color(153, 50, 204),
                new Color(0, 100, 0), new Color(34, 139, 34), new Color(50, 205, 50), new Color(124, 252, 0)
            };
            
            for (Color color : colors) {
                JButton colorButton = new JButton();
                colorButton.setBackground(color);
                colorButton.setPreferredSize(new Dimension(50, 35));
                colorButton.setBorder(BorderFactory.createRaisedBevelBorder());
                colorButton.setOpaque(true);
                
                colorButton.addActionListener(e -> {
                    canvas.setDrawingColor(color);
                    updateStatus("Color changed to: " + getColorName(color));
                    colorDialog.dispose();
                });
                
                // Add hover effect
                colorButton.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseEntered(java.awt.event.MouseEvent evt) {
                        colorButton.setBorder(BorderFactory.createLoweredBevelBorder());
                    }
                    public void mouseExited(java.awt.event.MouseEvent evt) {
                        colorButton.setBorder(BorderFactory.createRaisedBevelBorder());
                    }
                });
                
                colorGrid.add(colorButton);
            }
            
            // Add title label
            JLabel titleLabel = new JLabel("Select a Color", JLabel.CENTER);
            titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
            titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
            
            colorDialog.add(titleLabel, BorderLayout.NORTH);
            colorDialog.add(colorGrid, BorderLayout.CENTER);
            
            colorDialog.setVisible(true);
        }
    }
    
    private static void showSizePicker() {
        if (canvas != null) {
            String[] sizes = {"1", "2", "3", "5", "8", "10", "15", "20", "25", "30"};
            String selectedSize = (String) JOptionPane.showInputDialog(
                null,
                "Select stroke size:",
                "Stroke Size",
                JOptionPane.QUESTION_MESSAGE,
                null,
                sizes,
                String.valueOf(canvas.getStrokeSize())
            );
            
            if (selectedSize != null) {
                try {
                    int size = Integer.parseInt(selectedSize);
                    canvas.setStrokeSize(size);
                    updateStatus("Stroke size changed to: " + size + "px");
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Invalid size value!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    private static void saveDrawing() {
        if (canvas != null) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save Drawing");
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PNG Images", "png"));
            
            int userSelection = fileChooser.showSaveDialog(null);
            
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                
                // Ensure .png extension
                if (!fileToSave.getName().toLowerCase().endsWith(".png")) {
                    fileToSave = new File(fileToSave.getAbsolutePath() + ".png");
                }
                
                try {
                    ImageIO.write(canvas.getCanvasImage(), "PNG", fileToSave);
                    updateStatus("Drawing saved to: " + fileToSave.getName());
                    JOptionPane.showMessageDialog(null, "Drawing saved successfully!", "Save Complete", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException e) {
                    updateStatus("Error saving drawing!");
                    JOptionPane.showMessageDialog(null, "Error saving drawing: " + e.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    private static void clearCanvas() {
        if (canvas != null) {
            int result = JOptionPane.showConfirmDialog(
                null,
                "Are you sure you want to clear the canvas? This action cannot be undone.",
                "Clear Canvas",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            
            if (result == JOptionPane.YES_OPTION) {
                canvas.clearCanvas();
                updateStatus("Canvas cleared");
            }
        }
    }
    
    private static void updateButtonStates() {
        if (canvas != null) {
            if (canvas.isEraser()) {
                eraserButton.setBackground(new Color(100, 150, 100));
                penButton.setBackground(new Color(70, 70, 70));
            } else {
                penButton.setBackground(new Color(100, 150, 100));
                eraserButton.setBackground(new Color(70, 70, 70));
            }
        }
    }
    
    private static void updateStatus(String message) {
        if (statusLabel != null) {
            statusLabel.setText(message);
        }
    }
    
    private static String getColorName(Color color) {
        if (color.equals(Color.BLACK)) return "Black";
        if (color.equals(Color.WHITE)) return "White";
        if (color.equals(Color.RED)) return "Red";
        if (color.equals(Color.GREEN)) return "Green";
        if (color.equals(Color.BLUE)) return "Blue";
        if (color.equals(Color.YELLOW)) return "Yellow";
        if (color.equals(Color.ORANGE)) return "Orange";
        if (color.equals(Color.PINK)) return "Pink";
        if (color.equals(Color.CYAN)) return "Cyan";
        if (color.equals(Color.MAGENTA)) return "Magenta";
        return "RGB(" + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + ")";
    }
    
    // Method to set the canvas reference (call this from MainFrame)
    public static void setCanvas(RightCanvas canvasRef) {
        canvas = canvasRef;
        updateButtonStates();
    }
}
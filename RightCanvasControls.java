import java.awt.*;
import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class RightCanvasControls {
    private static RightCanvas canvas;
    private static JButton penButton, eraserButton, colorButton, penSizeButton, saveButton;

    public static JPanel createTopPanel(RightCanvas canvasRef) {
        canvas = canvasRef;
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        topPanel.setBackground(new Color(45, 45, 45));
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        penButton = createIconButton("Pen.png");
        eraserButton = createIconButton("Eraser.png");
        colorButton = createIconButton("Pen Colour.png");
        penSizeButton = createIconButton("Pen Size.png");
        saveButton = createIconButton("Save.png");

        // Set tooltips for hover text
        penButton.setToolTipText("Pen");
        eraserButton.setToolTipText("Eraser");
        colorButton.setToolTipText("Color");
        penSizeButton.setToolTipText("Pen Size");
        saveButton.setToolTipText("Save");

        penButton.addActionListener(e -> {
            if (canvas != null) {
                canvas.setEraser(false);
                updateButtonStates();
            }
        });

        eraserButton.addActionListener(e -> {
            if (canvas != null) {
                canvas.setEraser(true);
                updateButtonStates();
            }
        });

        colorButton.addActionListener(e -> showSimpleColorPicker());
        penSizeButton.addActionListener(e -> showPenSizePicker());
        saveButton.addActionListener(e -> saveDrawing());

        // Add buttons directly to the panel
        topPanel.add(penButton);
        topPanel.add(eraserButton);
        topPanel.add(colorButton);
        topPanel.add(penSizeButton);
        topPanel.add(saveButton);

        return topPanel;
    }

    public static JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(45, 45, 45));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JButton clearButton = new JButton("Clear Drawing");
        clearButton.setBackground(new Color(220, 53, 69));
        clearButton.setForeground(Color.WHITE);
        clearButton.setFocusPainted(false);
        clearButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        clearButton.setFont(new Font("Arial", Font.BOLD, 14));

        clearButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                clearButton.setBackground(new Color(255, 80, 90));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                clearButton.setBackground(new Color(220, 53, 69));
            }
        });

        clearButton.addActionListener(e -> clearCanvas());
        bottomPanel.add(clearButton, BorderLayout.EAST);
        return bottomPanel;
    }

    private static JButton createIconButton(String iconPath) {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(56, 56));
        button.setBackground(new Color(70, 70, 70));
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        button.setFocusPainted(false);
        button.setMargin(new Insets(4, 4, 4, 4));

        boolean iconLoaded = false;
        String[] possiblePaths = {
            "icons/" + iconPath,
            "src/icons/" + iconPath,
            "resources/" + iconPath,
            iconPath,
            System.getProperty("user.dir") + "/" + iconPath
        };

        for (String path : possiblePaths) {
            try {
                File iconFile = new File(path);
                if (iconFile.exists()) {
                    ImageIcon icon = new ImageIcon(path);
                    if (icon.getIconWidth() > 0) {
                        Image img = icon.getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH);
                        button.setIcon(new ImageIcon(img));
                        iconLoaded = true;
                        break;
                    }
                }
            } catch (Exception ignored) {}
        }

        if (!iconLoaded) {
            button.setText("?");
            button.setForeground(Color.WHITE);
            button.setFont(new Font("Arial", Font.BOLD, 16));
            System.out.println("Icon not found for " + iconPath + ", using text fallback");
        }

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(90, 90, 90));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if ((button == penButton && !canvas.isEraser()) ||
                    (button == eraserButton && canvas.isEraser())) {
                    button.setBackground(new Color(100, 150, 100));
                } else {
                    button.setBackground(new Color(70, 70, 70));
                }
            }
        });
        return button;
    }

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

            Color[] colors = {
                Color.BLACK, Color.DARK_GRAY, Color.GRAY, Color.LIGHT_GRAY,
                Color.WHITE, new Color(128, 0, 0), Color.RED, new Color(255, 128, 128),
                new Color(0, 128, 0), Color.GREEN, new Color(128, 255, 128), new Color(0, 255, 0),
                new Color(0, 128, 128), Color.CYAN, new Color(128, 255, 255), new Color(0, 255, 255),
                new Color(0, 0, 128), Color.BLUE, new Color(128, 128, 255), new Color(0, 0, 255),
                new Color(128, 0, 128), Color.MAGENTA, new Color(255, 128, 255), new Color(255, 0, 255),
                new Color(128, 128, 0), Color.YELLOW, new Color(255, 255, 128), new Color(255, 255, 0),
                new Color(128, 64, 0), Color.ORANGE, new Color(255, 200, 128), new Color(255, 165, 0),
                new Color(160, 82, 45), new Color(210, 180, 140), new Color(244, 164, 96), new Color(222, 184, 135),
                Color.PINK, new Color(255, 192, 203), new Color(255, 20, 147), new Color(199, 21, 133),
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
                    colorDialog.dispose();
                });

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
            String selectedSize = (String) JOptionPane.showInputDialog(
                null,
                "Select Pen Size:",
                "Pen Size",
                JOptionPane.QUESTION_MESSAGE,
                null,
                sizes,
                String.valueOf(canvas.getStrokeSize())
            );

            if (selectedSize != null) {
                try {
                    int size = Integer.parseInt(selectedSize);
                    canvas.setStrokeSize(size);
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
                if (!fileToSave.getName().toLowerCase().endsWith(".png")) {
                    fileToSave = new File(fileToSave.getAbsolutePath() + ".png");
                }

                try {
                    ImageIO.write(canvas.getCanvasImage(), "PNG", fileToSave);
                    JOptionPane.showMessageDialog(null, "Drawing saved successfully!", "Save Complete", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null, "Error saving drawing: " + e.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private static void clearCanvas() {
        if (canvas != null) {
            int result = JOptionPane.showConfirmDialog(
                null,
                "Are you sure you want to clear the drawing? This action cannot be undone.",
                "Clear Drawing",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            if (result == JOptionPane.YES_OPTION) {
                canvas.clearCanvas();
            }
        }
    }

    private static void updateButtonStates() {
        if (canvas != null) {
            if (penButton != null && eraserButton != null) {
                if (canvas.isEraser()) {
                    eraserButton.setBackground(new Color(100, 150, 100));
                    penButton.setBackground(new Color(70, 70, 70));
                } else {
                    penButton.setBackground(new Color(100, 150, 100));
                    eraserButton.setBackground(new Color(70, 70, 70));
                }
            }
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

    public static void setCanvas(RightCanvas canvasRef) {
        canvas = canvasRef;
        updateButtonStates();
    }
}
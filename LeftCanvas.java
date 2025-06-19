import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LeftCanvas extends JPanel {
    private java.util.List<CanvasItem> items = new ArrayList<>();
    private CanvasItem selectedItem = null;
    private Point lastMousePoint = null;
    private String mode = "";

    public LeftCanvas() {
        setBackground(Color.WHITE);
        setTransferHandler(new ImageDropHandler());

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                lastMousePoint = e.getPoint();

                CanvasItem clickedItem = null;
                String newMode = "";

                for (int i = items.size() - 1; i >= 0; i--) {
                    CanvasItem item = items.get(i);
                    if (item.onRotateHandle(e.getX(), e.getY())) {
                        clickedItem = item;
                        newMode = "rotate";
                        break;
                    } else {
                        String handleType = item.getResizeHandle(e.getX(), e.getY());
                        if (handleType != null) {
                            clickedItem = item;
                            newMode = "resize-" + handleType;
                            break;
                        }
                    }
                    if (item.contains(e.getX(), e.getY())) {
                        clickedItem = item;
                        newMode = "move";
                        break;
                    }
                }

                if (selectedItem != null && selectedItem != clickedItem) {
                    selectedItem.setSelected(false);
                }

                selectedItem = clickedItem;
                mode = newMode;

                if (selectedItem != null) {
                    selectedItem.setSelected(true);
                    if (items.indexOf(selectedItem) < items.size() - 1) {
                        items.remove(selectedItem);
                        items.add(selectedItem);
                    }
                }
                repaint();
            }

            public void mouseReleased(MouseEvent e) {
                mode = "";
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (selectedItem != null && lastMousePoint != null) {
                    int dx = e.getX() - lastMousePoint.x;
                    int dy = e.getY() - lastMousePoint.y;

                    if (mode.startsWith("resize-")) {
                        String handleType = mode.substring(mode.indexOf('-') + 1);
                        selectedItem.resize(handleType, dx, dy);
                    } else {
                        switch (mode) {
                            case "move":
                                // Get current and canvas dimensions
                                int currentX = selectedItem.getX();
                                int currentY = selectedItem.getY();
                                int itemWidth = selectedItem.getWidth();
                                int itemHeight = selectedItem.getHeight();
                                int canvasWidth = getWidth();
                                int canvasHeight = getHeight();

                                // Calculate potential new position
                                int newX = currentX + dx;
                                int newY = currentY + dy;

                                // Clamp the position within the canvas boundaries
                                newX = Math.max(0, newX); // Prevent moving past left edge
                                newY = Math.max(0, newY); // Prevent moving past top edge
                                newX = Math.min(newX, canvasWidth - itemWidth); // Prevent moving past right edge
                                newY = Math.min(newY, canvasHeight - itemHeight); // Prevent moving past bottom edge

                                // Apply the new, validated position
                                selectedItem.setPosition(newX, newY);
                                break;
                            case "rotate":
                                selectedItem.rotate(dx);
                                break;
                        }
                    }

                    lastMousePoint = e.getPoint();
                    repaint();
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (CanvasItem item : items) {
            item.draw(g);
        }
    }

    private class ImageDropHandler extends TransferHandler {
        @Override
        public boolean canImport(TransferSupport support) {
            return support.isDataFlavorSupported(DataFlavor.stringFlavor);
        }

        @Override
        public boolean importData(TransferSupport support) {
            if (!canImport(support)) return false;

            try {
                String imagePath = (String) support.getTransferable().getTransferData(DataFlavor.stringFlavor);
                Point dropPoint = support.getDropLocation().getDropPoint();

                if (selectedItem != null) {
                    selectedItem.setSelected(false);
                }

                CanvasItem newItem = new CanvasItem(imagePath, dropPoint.x, dropPoint.y);
                items.add(newItem);
                selectedItem = newItem;
                selectedItem.setSelected(true);

                repaint();
                return true;
            } catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }
        }
    }

    // ====== Public methods used by control buttons ======

    public void clearCanvas() {
        items.clear();
        selectedItem = null;
        repaint();
    }

    public void rotateCanvas() {
        for (CanvasItem item : items) {
            item.rotate(90);
        }
        repaint();
    }

    public void flipSelectedVertical() {
        if (selectedItem != null) {
            selectedItem.flipVertical();
            repaint();
        } else {
            JOptionPane.showMessageDialog(this, "Please select an image to flip vertically.", "No Image Selected", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void flipSelectedHorizontal() {
        if (selectedItem != null) {
            selectedItem.flipHorizontal();
            repaint();
        } else {
            JOptionPane.showMessageDialog(this, "Please select an image to flip horizontally.", "No Image Selected", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void deleteSelectedItem() {
        if (selectedItem != null) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete the selected item? This action cannot be undone.",
                    "Delete Item",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE
            );
            if (confirm == JOptionPane.YES_OPTION) {
                items.remove(selectedItem);
                selectedItem = null;
                repaint();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an image to delete.", "No Image Selected", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void saveCanvasAsImage() {
        BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        paint(g2);
        g2.dispose();

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Composition");
        int option = fileChooser.showSaveDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".png")) {
                file = new File(file.getAbsolutePath() + ".png");
            }
            try {
                ImageIO.write(image, "png", file);
                JOptionPane.showMessageDialog(this, "Composition saved successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to save: " + ex.getMessage());
            }
        }
    }
}
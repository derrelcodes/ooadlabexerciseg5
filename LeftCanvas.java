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
    private String mode = ""; // Stores "move", "rotate", or "resize-TL", "resize-BR" etc.

    public LeftCanvas() {
        setBackground(Color.WHITE); //
        setTransferHandler(new ImageDropHandler()); //

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { //
                lastMousePoint = e.getPoint(); //

                CanvasItem clickedItem = null; //
                String newMode = ""; //

                // Iterate through items in reverse to find the topmost item clicked
                for (int i = items.size() - 1; i >= 0; i--) { //
                    CanvasItem item = items.get(i); //
                    if (item.onRotateHandle(e.getX(), e.getY())) { //
                        clickedItem = item; //
                        newMode = "rotate"; //
                        break; //
                    } else { //
                        String handleType = item.getResizeHandle(e.getX(), e.getY()); // Get specific handle type
                        if (handleType != null) { //
                            clickedItem = item; //
                            newMode = "resize-" + handleType; // e.g., "resize-TL", "resize-BR"
                            break; //
                        }
                    }
                    if (item.contains(e.getX(), e.getY())) { //
                        clickedItem = item; //
                        newMode = "move"; //
                        break; //
                    }
                }

                // Handle selection state and layering
                if (selectedItem != null && selectedItem != clickedItem) { //
                    selectedItem.setSelected(false); // Deselect the old item if a new one is clicked
                }

                selectedItem = clickedItem; // Set the new selected item
                mode = newMode; // Set the interaction mode

                if (selectedItem != null) { //
                    selectedItem.setSelected(true); // Mark the new item as selected
                    // Bring the selected item to the top of the drawing order (Z-order)
                    if (items.indexOf(selectedItem) < items.size() - 1) { //
                        items.remove(selectedItem); //
                        items.add(selectedItem); // Add to end of list
                    }
                }

                repaint(); // Repaint to show/hide handles and update layering
            }

            public void mouseReleased(MouseEvent e) { //
                // Keep selectedItem as is; handles should remain visible after dragging.
                // Only reset the drag mode.
                mode = ""; //
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) { //
                if (selectedItem != null && lastMousePoint != null) { //
                    int dx = e.getX() - lastMousePoint.x; //
                    int dy = e.getY() - lastMousePoint.y; //

                    if (mode.startsWith("resize-")) { // Check if mode starts with "resize-"
                        String handleType = mode.substring(mode.indexOf('-') + 1); // Extract handle type (e.g., "TL")
                        selectedItem.resize(handleType, dx, dy); // Call new resize method
                    } else { //
                        switch (mode) { //
                            case "move": //
                                selectedItem.move(dx, dy); //
                                break; //
                            case "rotate": //
                                selectedItem.rotate(dx); //
                                break; //
                        }
                    }

                    lastMousePoint = e.getPoint(); //
                    repaint(); //
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) { //
        super.paintComponent(g); //
        for (CanvasItem item : items) { //
            item.draw(g); //
        }
    }

    private class ImageDropHandler extends TransferHandler { //
        @Override
        public boolean canImport(TransferSupport support) { //
            return support.isDataFlavorSupported(DataFlavor.stringFlavor); //
        }

        @Override
        public boolean importData(TransferSupport support) { //
            if (!canImport(support)) return false; //

            try { //
                String imagePath = (String) support.getTransferable().getTransferData(DataFlavor.stringFlavor); //
                Point dropPoint = support.getDropLocation().getDropPoint(); //

                // Deselect any previously selected item
                if (selectedItem != null) { //
                    selectedItem.setSelected(false); //
                }

                CanvasItem newItem = new CanvasItem(imagePath, dropPoint.x, dropPoint.y); //
                items.add(newItem); // Add to end for layering
                selectedItem = newItem; // Select the newly dropped item
                selectedItem.setSelected(true); // Set its selected state to true

                repaint(); //
                return true; //
            } catch (Exception ex) { //
                ex.printStackTrace(); //
                return false; //
            }
        }
    }

    // ====== Public methods used by control buttons ======

    // Clears all items
    public void clearCanvas() { //
        items.clear(); //
        selectedItem = null; // Also clear selection
        repaint(); //
    }

    // Rotates all items
    public void rotateCanvas() { //
        for (CanvasItem item : items) { //
            item.rotate(90); //
        }
        repaint(); //
    }

    public void flipSelectedVertical() { //
        if (selectedItem != null) { //
            selectedItem.flipVertical(); //
            repaint(); //
        } else { //
            JOptionPane.showMessageDialog(this, "Please select an image to flip vertically.", "No Image Selected", JOptionPane.INFORMATION_MESSAGE); //
        }
    }

    public void flipSelectedHorizontal() { //
        if (selectedItem != null) { //
            selectedItem.flipHorizontal(); //
            repaint(); //
        } else { //
            JOptionPane.showMessageDialog(this, "Please select an image to flip horizontally.", "No Image Selected", JOptionPane.INFORMATION_MESSAGE); //
        }
    }

    // New method: Deletes the currently selected item
    public void deleteSelectedItem() {
        if (selectedItem != null) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete the selected item? This action cannot be undone.",
                    "Delete Item",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE
            );
            if (confirm == JOptionPane.YES_OPTION) {
                items.remove(selectedItem);
                selectedItem = null; // Deselect the item after deletion
                repaint();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an image to delete.", "No Image Selected", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Saves the current canvas as a PNG image
    public void saveCanvasAsImage() { //
        BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB); //
        Graphics2D g2 = image.createGraphics(); //
        paint(g2); //
        g2.dispose(); //

        JFileChooser fileChooser = new JFileChooser(); //
        fileChooser.setDialogTitle("Save Composition"); //
        int option = fileChooser.showSaveDialog(this); //
        if (option == JFileChooser.APPROVE_OPTION) { //
            File file = fileChooser.getSelectedFile(); //
            if (!file.getName().toLowerCase().endsWith(".png")) { //
                file = new File(file.getAbsolutePath() + ".png"); //
            }
            try { //
                ImageIO.write(image, "png", file); //
                JOptionPane.showMessageDialog(this, "Composition saved successfully!"); //
            } catch (Exception ex) { //
                JOptionPane.showMessageDialog(this, "Failed to save: " + ex.getMessage()); //
            }
        }
    }
}
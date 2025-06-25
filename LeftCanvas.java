// derrelcodes/ooadlabexerciseg5/ooadlabexerciseg5-main/LeftCanvas.java
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LeftCanvas extends JPanel {
    private final List<CreationItem> items = new ArrayList<>();
    private CreationItem selectedItem = null;
    private Point lastMousePoint = null;
    private String mode = ""; // e.g., "move", "rotate", "resize-TL"

    public LeftCanvas() {
        setBackground(Color.WHITE);
        setTransferHandler(new ImageDropHandler());

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                lastMousePoint = e.getPoint();
                CreationItem clickedItem = null;
                String newMode = "";

                // Iterate backwards to select the top-most item
                for (int i = items.size() - 1; i >= 0; i--) {
                    CreationItem item = items.get(i);
                    if (item.onRotateHandle(e.getX(), e.getY())) {
                        clickedItem = item;
                        newMode = "rotate";
                        break;
                    }
                    String handleType = item.getResizeHandle(e.getX(), e.getY());
                    if (handleType != null) {
                        clickedItem = item;
                        newMode = "resize-" + handleType;
                        break;
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
                    // Bring to front
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
                                if (selectedItem.canTranspose()) {
                                    int newX = Math.max(0, Math.min(selectedItem.getX() + dx, getWidth() - selectedItem.getWidth()));
                                    int newY = Math.max(0, Math.min(selectedItem.getY() + dy, getHeight() - selectedItem.getHeight()));
                                    selectedItem.setPosition(newX, newY);
                                }
                                break;
                            case "rotate":
                                Point center = new Point(selectedItem.getX() + selectedItem.getWidth() / 2, selectedItem.getY() + selectedItem.getHeight() / 2);
                                double angle1 = Math.atan2(lastMousePoint.y - center.y, lastMousePoint.x - center.x);
                                double angle2 = Math.atan2(e.getY() - center.y, e.getX() - center.x);
                                selectedItem.rotate(Math.toDegrees(angle2 - angle1));
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
        for (CreationItem item : items) {
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
                Transferable t = support.getTransferable();
                String data = (String) t.getTransferData(DataFlavor.stringFlavor);
                Point dropPoint = support.getDropLocation().getDropPoint();

                // Data is "TYPE:PATH"
                String[] parts = data.split(":", 2);
                if (parts.length != 2) return false;

                String itemType = parts[0];
                String imagePath = parts[1];

                if (selectedItem != null) {
                    selectedItem.setSelected(false);
                }

                CreationItem newItem = CreationFactory.createItem(itemType, imagePath, dropPoint.x, dropPoint.y);
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
    
    public void addImageFromPath(String type, String path) {
        if (selectedItem != null) {
            selectedItem.setSelected(false);
        }
        CreationItem newItem = CreationFactory.createItem(type, path, 50, 50);
        items.add(newItem);
        selectedItem = newItem;
        selectedItem.setSelected(true);
        repaint();
    }

    public void rotateCanvas() {
        for (CreationItem item : items) {
            item.rotate(90);
        }
        repaint();
    }

    public void flipSelectedVertical() {
        if (selectedItem != null && selectedItem.canFlip()) {
            selectedItem.flipVertical();
            repaint();
        } else {
            JOptionPane.showMessageDialog(this, "The selected item cannot be flipped.", "Action Not Allowed", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void flipSelectedHorizontal() {
        if (selectedItem != null && selectedItem.canFlip()) {
            selectedItem.flipHorizontal();
            repaint();
        } else {
            JOptionPane.showMessageDialog(this, "The selected item cannot be flipped.", "Action Not Allowed", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    public void composeCanvas() {
        if (items.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Canvas is empty. Add items to compose.", "Cannot Compose", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "This will merge all items into a single, new image. This action cannot be undone.\nContinue?",
                "Compose Canvas",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm != JOptionPane.YES_OPTION) return;

        // Create an image of the current canvas
        BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        
        // Temporarily deselect item to render without handles
        if (selectedItem != null) selectedItem.setSelected(false);
        this.paint(g2);
        if (selectedItem != null) selectedItem.setSelected(true); // Restore selection state
        g2.dispose();

        try {
            // Save to a temporary file
            File tempDir = new File("assets/temp");
            if (!tempDir.exists()) tempDir.mkdirs();
            String tempFileName = "composed_" + UUID.randomUUID().toString() + ".png";
            File tempFile = new File(tempDir, tempFileName);
            ImageIO.write(image, "png", tempFile);

            // Clear canvas and add the new composed image
            clearCanvas();
            CreationItem composedItem = CreationFactory.createItem("Image", tempFile.getAbsolutePath(), 0, 0);
            
            // Set size to fit canvas
            composedItem.width = getWidth();
            composedItem.height = getHeight();

            items.add(composedItem);
            selectedItem = composedItem;
            selectedItem.setSelected(true);
            
            repaint();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to compose canvas: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void deleteSelectedItem() {
        if (selectedItem != null) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete the selected item?", "Delete Item",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                items.remove(selectedItem);
                selectedItem = null;
                repaint();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an item to delete.", "No Item Selected", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void saveCanvasAsImage() {
        BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        if(selectedItem != null) selectedItem.setSelected(false);
        paint(g2);
        if(selectedItem != null) selectedItem.setSelected(true);
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
                JOptionPane.showMessageDialog(this, "Failed to save: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
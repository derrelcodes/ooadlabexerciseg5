// derrelcodes/ooadlabexerciseg5/ooadlabexerciseg5-main/LeftCanvas.java
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
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
    private String mode = "";

    public LeftCanvas() {
        setBackground(Color.WHITE);
        setTransferHandler(new ImageDropHandler());

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                lastMousePoint = e.getPoint();
                CreationItem clickedItem = null;
                String newMode = "";
                for (int i = items.size() - 1; i >= 0; i--) {
                    CreationItem item = items.get(i);
                    if (item.onRotateHandle(e.getX(), e.getY())) {
                        clickedItem = item; newMode = "rotate"; break;
                    }
                    String handleType = item.getResizeHandle(e.getX(), e.getY());
                    if (handleType != null) {
                        clickedItem = item; newMode = "resize-" + handleType; break;
                    }
                    if (item.contains(e.getX(), e.getY())) {
                        clickedItem = item; newMode = "move"; break;
                    }
                }
                if (selectedItem != null && selectedItem != clickedItem) selectedItem.setSelected(false);
                selectedItem = clickedItem;
                mode = newMode;
                if (selectedItem != null) {
                    selectedItem.setSelected(true);
                    
                    // MODIFICATION: Bring to front ONLY if it's not a background layer
                    if (!selectedItem.isBackgroundLayer() && items.indexOf(selectedItem) < items.size() - 1) {
                        items.remove(selectedItem);
                        items.add(selectedItem);
                    }
                }
                repaint();
            }
            public void mouseReleased(MouseEvent e) { mode = ""; }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (selectedItem != null && lastMousePoint != null) {
                    int dx = e.getX() - lastMousePoint.x; int dy = e.getY() - lastMousePoint.y;
                    if (mode.startsWith("resize-")) {
                        selectedItem.resize(mode.substring(mode.indexOf('-') + 1), dx, dy);
                    } else if (mode.equals("move") && selectedItem.canTranspose()) {
                        int newX = Math.max(0, Math.min(selectedItem.getX() + dx, getWidth() - selectedItem.getWidth()));
                        int newY = Math.max(0, Math.min(selectedItem.getY() + dy, getHeight() - selectedItem.getHeight()));
                        selectedItem.setPosition(newX, newY);
                    } else if (mode.equals("rotate")) {
                        Point center = new Point(selectedItem.getX() + selectedItem.getWidth() / 2, selectedItem.getY() + selectedItem.getHeight() / 2);
                        double angle1 = Math.atan2(lastMousePoint.y - center.y, lastMousePoint.x - center.x);
                        double angle2 = Math.atan2(e.getY() - center.y, e.getX() - center.x);
                        selectedItem.rotate(Math.toDegrees(angle2 - angle1));
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
        public boolean canImport(TransferSupport support) { return support.isDataFlavorSupported(DataFlavor.stringFlavor); }
        public boolean importData(TransferSupport support) {
            if (!canImport(support)) return false;
            try {
                String data = (String) support.getTransferable().getTransferData(DataFlavor.stringFlavor);
                Point dropPoint = support.getDropLocation().getDropPoint();
                String[] parts = data.split(":", 2);
                if (parts.length != 2) return false;
                if (selectedItem != null) selectedItem.setSelected(false);
                CreationItem newItem = CreationFactory.createItem(parts[0], parts[1], dropPoint.x, dropPoint.y);
                items.add(newItem);
                selectedItem = newItem;
                selectedItem.setSelected(true);
                repaint();
                return true;
            } catch (Exception ex) { return false; }
        }
    }

    public void clearCanvas() { items.clear(); selectedItem = null; repaint(); }
    public void rotateCanvas() { for (CreationItem item : items) item.rotate(90); repaint(); }
    public void flipSelectedVertical() { if (selectedItem != null && selectedItem.canFlip()) { selectedItem.flipVertical(); repaint(); } }
    public void flipSelectedHorizontal() { if (selectedItem != null && selectedItem.canFlip()) { selectedItem.flipHorizontal(); repaint(); } }
    
    public void deleteSelectedItem() {
        if (selectedItem != null) {
            if(selectedItem.isBackgroundLayer()){
                JOptionPane.showMessageDialog(this, "The composed background layer cannot be deleted.", "Action Not Allowed", JOptionPane.WARNING_MESSAGE);
                return;
            }
            items.remove(selectedItem);
            selectedItem = null;
            repaint();
        }
    }

    public void composeCanvas() {
        if (items.isEmpty()) { JOptionPane.showMessageDialog(this, "Canvas is empty.", "Cannot Compose", JOptionPane.WARNING_MESSAGE); return; }
        int confirm = JOptionPane.showConfirmDialog(this, "This will merge all items into a single background image.", "Compose Canvas", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        BufferedImage image = getCanvasAsImage();
        try {
            File tempDir = new File("assets/temp"); if (!tempDir.exists()) tempDir.mkdirs();
            File tempFile = new File(tempDir, "composed_" + UUID.randomUUID() + ".png");
            ImageIO.write(image, "png", tempFile);
            clearCanvas();
            CreationItem composedItem = CreationFactory.createItem("Image", tempFile.getAbsolutePath(), 0, 0);
            // ImageIcon(String) blocks until fully loaded, so the pixels are already
            // in memory here and the temp file is no longer needed on disk.
            tempFile.delete();

            // MODIFICATION: Mark the new item as a composition layer
            if (composedItem instanceof CustomImageItem) {
                ((CustomImageItem) composedItem).setAsCompositionLayer(true);
            }

            composedItem.width = getWidth(); composedItem.height = getHeight();
            items.add(composedItem);
            selectedItem = composedItem;
            selectedItem.setSelected(true);
            repaint();
        } catch (IOException e) { JOptionPane.showMessageDialog(this, "Failed to compose canvas: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); }
    }

    private BufferedImage getCanvasAsImage() {
        BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        boolean wasSelected = (selectedItem != null);
        if (wasSelected) selectedItem.setSelected(false);
        paint(g2);
        if (wasSelected) selectedItem.setSelected(true);
        g2.dispose();
        return image;
    }

    public void saveToLibrary(CollectionPanel collectionPanel) {
        File assetsDir = new File("assets/images");
        if (!assetsDir.exists()) assetsDir.mkdirs();
        String fileName = "composition_" + UUID.randomUUID().toString().substring(0, 8) + ".png";
        File fileToSave = new File(assetsDir, fileName);
        try {
            ImageIO.write(getCanvasAsImage(), "PNG", fileToSave);
            JOptionPane.showMessageDialog(this, "Composition saved to library successfully!", "Save Complete", JOptionPane.INFORMATION_MESSAGE);
            if (collectionPanel != null) {
                collectionPanel.refreshImagesTab();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving to library: " + e.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void saveToFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Composition to File");
        fileChooser.setFileFilter(new FileNameExtensionFilter("PNG Images", "png"));
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getName().toLowerCase().endsWith(".png")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".png");
            }
            try {
                ImageIO.write(getCanvasAsImage(), "png", fileToSave);
                JOptionPane.showMessageDialog(this, "Composition saved successfully!", "Save Complete", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to save: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
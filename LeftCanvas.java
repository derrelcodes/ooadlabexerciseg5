import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

public class LeftCanvas extends JPanel {
    private java.util.List<CanvasItem> items = new ArrayList<>();
    private CanvasItem selectedItem = null;
    private Point lastMousePoint = null;
    private String mode = "";

    public LeftCanvas() {
        setBackground(Color.LIGHT_GRAY);
        setTransferHandler(new ImageDropHandler());

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                lastMousePoint = e.getPoint();
                for (CanvasItem item : items) {
                    if (item.onRotateHandle(e.getX(), e.getY())) {
                        selectedItem = item;
                        mode = "rotate";
                        return;
                    } else if (item.onResizeHandle(e.getX(), e.getY())) {
                        selectedItem = item;
                        mode = "resize";
                        return;
                    } else if (item.contains(e.getX(), e.getY())) {
                        selectedItem = item;
                        mode = "move";
                        return;
                    }
                }
                selectedItem = null;
                mode = "";
            }

            public void mouseReleased(MouseEvent e) {
                selectedItem = null;
                mode = "";
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (selectedItem != null && lastMousePoint != null) {
                    int dx = e.getX() - lastMousePoint.x;
                    int dy = e.getY() - lastMousePoint.y;

                    switch (mode) {
                        case "move":
                            selectedItem.move(dx, dy);
                            break;
                        case "resize":
                            selectedItem.resize(dx, dy);
                            break;
                        case "rotate":
                            selectedItem.rotate(dx);
                            break;
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
                items.add(new CanvasItem(imagePath, dropPoint.x, dropPoint.y));
                repaint();
                return true;
            } catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }
        }
    }

    // ====== Public methods used by control buttons ======

    // Clears all items
    public void clearCanvas() {
        items.clear();
        repaint();
    }

    // Rotates all items
    public void rotateCanvas() {
        for (CanvasItem item : items) {
            item.rotate(90);
        }
        repaint();
    }

    // Saves the current canvas as a PNG image
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

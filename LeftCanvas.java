import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.io.File;
import java.util.ArrayList;

public class LeftCanvas extends JPanel {
    private java.util.List<CanvasItem> items = new ArrayList<>();

    public LeftCanvas() {
        setBackground(Color.LIGHT_GRAY);

        // Enable drop
        setTransferHandler(new ImageDropHandler());

        // Optional: repaint on resize
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                repaint();
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

    // Handles dropped image
    private class ImageDropHandler extends TransferHandler {
        @Override
        public boolean canImport(TransferHandler.TransferSupport support) {
            return support.isDataFlavorSupported(DataFlavor.stringFlavor);
        }

        @Override
        public boolean importData(TransferHandler.TransferSupport support) {
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
}

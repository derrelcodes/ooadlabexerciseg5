import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;

public class ImageThumbnail extends JButton {
    private String imagePath;

    public ImageThumbnail(String imagePath) {
        this.imagePath = imagePath;

        ImageIcon icon = new ImageIcon(imagePath);
        Image scaled = icon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
        setIcon(new ImageIcon(scaled));
        setPreferredSize(new Dimension(100, 100));
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);

        // Set custom TransferHandler
        setTransferHandler(new TransferHandler() {
            @Override
            protected Transferable createTransferable(JComponent c) {
                return new StringSelection(imagePath);
            }

            @Override
            public int getSourceActions(JComponent c) {
                return COPY;
            }
        });

        // Trigger drag on mouse press
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                JComponent comp = (JComponent) e.getSource();
                TransferHandler handler = comp.getTransferHandler();
                handler.exportAsDrag(comp, e, TransferHandler.COPY);
            }
        });
    }

    public String getImagePath() {
        return imagePath;
    }
}

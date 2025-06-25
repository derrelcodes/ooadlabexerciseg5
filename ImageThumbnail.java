// derrelcodes/ooadlabexerciseg5/ooadlabexerciseg5-main/ImageThumbnail.java
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ImageThumbnail extends JButton {
    private final String imagePath;
    private final String itemType;

    public ImageThumbnail(String imagePath, String itemType) {
        this.imagePath = imagePath;
        this.itemType = itemType;

        ImageIcon icon = new ImageIcon(imagePath);
        Image scaled = icon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
        setIcon(new ImageIcon(scaled));
        setPreferredSize(new Dimension(100, 100));
        setMaximumSize(new Dimension(100, 100));
        setMinimumSize(new Dimension(100, 100));
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);

        setTransferHandler(new TransferHandler() {
            @Override
            protected Transferable createTransferable(JComponent c) {
                // We now transfer a formatted string: "TYPE:PATH"
                return new StringSelection(itemType + ":" + imagePath);
            }

            @Override
            public int getSourceActions(JComponent c) {
                return COPY;
            }
        });

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                JComponent comp = (JComponent) e.getSource();
                TransferHandler handler = comp.getTransferHandler();
                handler.exportAsDrag(comp, e, TransferHandler.COPY);
            }
        });
    }
}
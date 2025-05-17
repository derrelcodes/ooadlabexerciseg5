import javax.swing.*;
import java.awt.*;

public class ImageThumbnail extends JButton {
    public ImageThumbnail(String label) {
        super(label);
        setPreferredSize(new Dimension(50, 50));
    }
}

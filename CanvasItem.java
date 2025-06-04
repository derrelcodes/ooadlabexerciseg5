import java.awt.*;
import javax.swing.*;

public class CanvasItem {
    private Image image;
    private int x, y;

    public CanvasItem(String imagePath, int x, int y) {
        ImageIcon icon = new ImageIcon(imagePath);
        this.image = icon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
        this.x = x;
        this.y = y;
    }

    public void draw(Graphics g) {
        g.drawImage(image, x, y, null);
    }
}

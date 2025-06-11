import java.awt.*;
import java.awt.geom.AffineTransform;
import javax.swing.*;

public class CanvasItem {
    private Image image;
    private int x, y, width, height;
    private double rotationAngle;

    private static final int HANDLE_SIZE = 10;

    public CanvasItem(String imagePath, int x, int y) {
        ImageIcon icon = new ImageIcon(imagePath);
        this.image = icon.getImage();
        this.x = x;
        this.y = y;
        this.width = 100;
        this.height = 100;
        this.rotationAngle = 0;
    }

    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        AffineTransform old = g2.getTransform();

        g2.translate(x + width / 2, y + height / 2);
        g2.rotate(Math.toRadians(rotationAngle));
        g2.drawImage(image, -width / 2, -height / 2, width, height, null);

        // Draw handles
        g2.setColor(Color.BLUE);
        g2.fillRect(-width / 2 - HANDLE_SIZE / 2, -height / 2 - HANDLE_SIZE / 2, HANDLE_SIZE, HANDLE_SIZE); // top-left
        g2.fillRect(width / 2 - HANDLE_SIZE / 2, -height / 2 - HANDLE_SIZE / 2, HANDLE_SIZE, HANDLE_SIZE);  // top-right
        g2.fillRect(-width / 2 - HANDLE_SIZE / 2, height / 2 - HANDLE_SIZE / 2, HANDLE_SIZE, HANDLE_SIZE);  // bottom-left
        g2.fillRect(width / 2 - HANDLE_SIZE / 2, height / 2 - HANDLE_SIZE / 2, HANDLE_SIZE, HANDLE_SIZE);   // bottom-right

        // Rotation handle (above top-center)
        g2.setColor(Color.RED);
        g2.fillOval(-10, -height / 2 - 30, 20, 20);

        g2.setTransform(old);
    }

    public boolean contains(int mx, int my) {
        Rectangle bounds = new Rectangle(x, y, width, height);
        return bounds.contains(mx, my);
    }

    public boolean onRotateHandle(int mx, int my) {
        return new Rectangle(x + width / 2 - 10, y - 30, 20, 20).contains(mx, my);
    }

    public boolean onResizeHandle(int mx, int my) {
        Rectangle[] corners = {
            new Rectangle(x - 5, y - 5, 10, 10), // top-left
            new Rectangle(x + width - 5, y - 5, 10, 10), // top-right
            new Rectangle(x - 5, y + height - 5, 10, 10), // bottom-left
            new Rectangle(x + width - 5, y + height - 5, 10, 10)  // bottom-right
        };
        for (Rectangle r : corners) {
            if (r.contains(mx, my)) return true;
        }
        return false;
    }

    public void move(int dx, int dy) {
        x += dx;
        y += dy;
    }

    public void resize(int dx, int dy) {
        width = Math.max(20, width + dx);
        height = Math.max(20, height + dy);
    }

    public void rotate(int deltaAngle) {
        rotationAngle += deltaAngle;
    }
}

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import javax.swing.ImageIcon;

public class CanvasItem {
    private Image image;
    private int x, y, width, height;
    private double rotationAngle;
    private boolean isSelected;

    private static final int HANDLE_SIZE = 10;

    public CanvasItem(String imagePath, int x, int y) {
        ImageIcon icon = new ImageIcon(imagePath);
        this.image = icon.getImage();
        this.x = x;
        this.y = y;
        this.width = 100;
        this.height = 100;
        this.rotationAngle = 0;
        this.isSelected = false;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        this.isSelected = selected;
    }

    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        AffineTransform old = g2.getTransform();

        Object oldAntiAliasing = g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.translate(x + width / 2, y + height / 2);
        g2.rotate(Math.toRadians(rotationAngle));
        g2.drawImage(image, -width / 2, -height / 2, width, height, null);

        if (isSelected) {
            g2.setColor(Color.CYAN);
            g2.setStroke(new BasicStroke(2));
            g2.drawRect(-width / 2, -height / 2, width, height);

            g2.setColor(Color.BLUE);
            g2.fillRect(-width / 2 - HANDLE_SIZE / 2, -height / 2 - HANDLE_SIZE / 2, HANDLE_SIZE, HANDLE_SIZE);
            g2.fillRect(width / 2 - HANDLE_SIZE / 2, -height / 2 - HANDLE_SIZE / 2, HANDLE_SIZE, HANDLE_SIZE);
            g2.fillRect(-width / 2 - HANDLE_SIZE / 2, height / 2 - HANDLE_SIZE / 2, HANDLE_SIZE, HANDLE_SIZE);
            g2.fillRect(width / 2 - HANDLE_SIZE / 2, height / 2 - HANDLE_SIZE / 2, HANDLE_SIZE, HANDLE_SIZE);

            g2.setColor(Color.RED);
            g2.fillOval(-10, -height / 2 - 30, 20, 20);
        }

        g2.setTransform(old);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldAntiAliasing);
    }

    public boolean contains(int mx, int my) {
        try {
            AffineTransform inverseTransform = new AffineTransform();
            inverseTransform.translate(x + width / 2, y + height / 2);
            inverseTransform.rotate(Math.toRadians(rotationAngle));
            inverseTransform.invert();

            Point2D transformedMouse = inverseTransform.transform(new Point(mx, my), null);

            return new Rectangle(-width / 2, -height / 2, width, height).contains(transformedMouse);

        } catch (java.awt.geom.NoninvertibleTransformException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean onRotateHandle(int mx, int my) {
        try {
            AffineTransform inverseTransform = new AffineTransform();
            inverseTransform.translate(x + width / 2, y + height / 2);
            inverseTransform.rotate(Math.toRadians(rotationAngle));
            inverseTransform.invert();

            Point2D transformedMouse = inverseTransform.transform(new Point(mx, my), null);

            return new Rectangle(-10, -height / 2 - 30, 20, 20).contains(transformedMouse);

        } catch (java.awt.geom.NoninvertibleTransformException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getResizeHandle(int mx, int my) {
        try {
            AffineTransform inverseTransform = new AffineTransform();
            inverseTransform.translate(x + width / 2, y + height / 2);
            inverseTransform.rotate(Math.toRadians(rotationAngle));
            inverseTransform.invert();

            Point2D transformedMouse = inverseTransform.transform(new Point(mx, my), null);

            // Check against local coordinates of resize handles
            Rectangle tlHandle = new Rectangle(-width / 2 - HANDLE_SIZE / 2, -height / 2 - HANDLE_SIZE / 2, HANDLE_SIZE, HANDLE_SIZE);
            Rectangle trHandle = new Rectangle(width / 2 - HANDLE_SIZE / 2, -height / 2 - HANDLE_SIZE / 2, HANDLE_SIZE, HANDLE_SIZE);
            Rectangle blHandle = new Rectangle(-width / 2 - HANDLE_SIZE / 2, height / 2 - HANDLE_SIZE / 2, HANDLE_SIZE, HANDLE_SIZE);
            Rectangle brHandle = new Rectangle(width / 2 - HANDLE_SIZE / 2, height / 2 - HANDLE_SIZE / 2, HANDLE_SIZE, HANDLE_SIZE);

            if (tlHandle.contains(transformedMouse)) return "TL";
            if (trHandle.contains(transformedMouse)) return "TR";
            if (blHandle.contains(transformedMouse)) return "BL";
            if (brHandle.contains(transformedMouse)) return "BR";

            return null;
        } catch (java.awt.geom.NoninvertibleTransformException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void move(int dx, int dy) {
        x += dx;
        y += dy;
    }

    public void resize(String handleType, int dx, int dy) {
        final int MIN_SIZE = 20;

        switch (handleType) {
            case "BR":
                width = Math.max(MIN_SIZE, width + dx);
                height = Math.max(MIN_SIZE, height + dy);
                break;
            case "TL":
                int newWidthTL = Math.max(MIN_SIZE, width - dx);
                int newHeightTL = Math.max(MIN_SIZE, height - dy);
                x += (width - newWidthTL);
                y += (height - newHeightTL);
                width = newWidthTL;
                height = newHeightTL;
                break;
            case "TR":
                int newWidthTR = Math.max(MIN_SIZE, width + dx);
                int newHeightTR = Math.max(MIN_SIZE, height - dy);
                y += (height - newHeightTR);
                width = newWidthTR;
                height = newHeightTR;
                break;
            case "BL":
                int newWidthBL = Math.max(MIN_SIZE, width - dx);
                int newHeightBL = Math.max(MIN_SIZE, height + dy);
                x += (width - newWidthBL);
                width = newWidthBL;
                height = newHeightBL;
                break;
            default:
                break;
        }
    }

    public void rotate(int deltaAngle) {
        rotationAngle += deltaAngle;
    }
}
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import javax.swing.ImageIcon;

public class CanvasItem {
    private Image image;
    private int x, y, width, height;
    private double rotationAngle;
    private boolean isSelected;
    private boolean flipHorizontal;
    private boolean flipVertical;

    private static final int HANDLE_SIZE = 10;
    
    // --- MODIFICATION START ---
    // Load the rotation icon statically so it's only loaded once for all items.
    private static Image rotateIcon;
    private static final int ICON_SIZE = 24; // Define a size for the icon

    static {
        try {
            // Load the icon from the specified path
            rotateIcon = new ImageIcon("Icons/RotateImage.png").getImage();
        } catch (Exception e) {
            rotateIcon = null; // Handle case where icon is not found
            System.err.println("Error: Could not load 'icons/RotateImage.png'");
        }
    }
    // --- MODIFICATION END ---

    public CanvasItem(String imagePath, int x, int y) {
        ImageIcon icon = new ImageIcon(imagePath);
        this.image = icon.getImage();
        this.x = x;
        this.y = y;
        this.width = 100;
        this.height = 100;
        this.rotationAngle = 0;
        this.isSelected = false;
        this.flipHorizontal = false;
        this.flipVertical = false;
    }

    // --- Getters and Setters ---
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public boolean isSelected() { return isSelected; }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public void setSelected(boolean selected) {
        this.isSelected = selected;
    }
    // -------------------------

    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        AffineTransform old = g2.getTransform();

        Object oldAntiAliasing = g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.translate(x + width / 2, y + height / 2);
        g2.rotate(Math.toRadians(rotationAngle));

        int drawX = -width / 2;
        int drawY = -height / 2;

        if (flipHorizontal) {
            g2.scale(-1, 1);
        }
        if (flipVertical) {
            g2.scale(1, -1);
        }

        g2.drawImage(image, drawX, drawY, width, height, null);

        if (isSelected) {
            g2.setColor(Color.CYAN);
            g2.setStroke(new BasicStroke(2));
            g2.drawRect(-width / 2, -height / 2, width, height);

            g2.setColor(Color.BLUE);
            g2.fillRect(-width / 2 - HANDLE_SIZE / 2, -height / 2 - HANDLE_SIZE / 2, HANDLE_SIZE, HANDLE_SIZE);
            g2.fillRect(width / 2 - HANDLE_SIZE / 2, -height / 2 - HANDLE_SIZE / 2, HANDLE_SIZE, HANDLE_SIZE);
            g2.fillRect(-width / 2 - HANDLE_SIZE / 2, height / 2 - HANDLE_SIZE / 2, HANDLE_SIZE, HANDLE_SIZE);
            g2.fillRect(width / 2 - HANDLE_SIZE / 2, height / 2 - HANDLE_SIZE / 2, HANDLE_SIZE, HANDLE_SIZE);

            // --- MODIFICATION START ---
            // Draw the icon instead of the red circle
            if (rotateIcon != null) {
                // The y-position is calculated to place the icon above the item's top edge
                g2.drawImage(rotateIcon, -ICON_SIZE / 2, -height / 2 - (ICON_SIZE + 10), ICON_SIZE, ICON_SIZE, null);
            } else {
                // Fallback to the red circle if the icon failed to load
                g2.setColor(Color.RED);
                g2.fillOval(-10, -height / 2 - 30, 20, 20);
            }
            // --- MODIFICATION END ---
        }

        g2.setTransform(old);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldAntiAliasing);
    }

    public boolean contains(int mx, int my) {
        try {
            AffineTransform currentTransform = new AffineTransform();
            currentTransform.translate(x + width / 2, y + height / 2);
            currentTransform.rotate(Math.toRadians(rotationAngle));
            if (flipHorizontal) currentTransform.scale(-1, 1);
            if (flipVertical) currentTransform.scale(1, -1);

            AffineTransform inverseTransform = currentTransform.createInverse();
            Point2D transformedMouse = inverseTransform.transform(new Point(mx, my), null);

            return new Rectangle(-width / 2, -height / 2, width, height).contains(transformedMouse);

        } catch (java.awt.geom.NoninvertibleTransformException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean onRotateHandle(int mx, int my) {
        try {
            AffineTransform currentTransform = new AffineTransform();
            currentTransform.translate(x + width / 2, y + height / 2);
            currentTransform.rotate(Math.toRadians(rotationAngle));
            if (flipHorizontal) currentTransform.scale(-1, 1);
            if (flipVertical) currentTransform.scale(1, -1);

            AffineTransform inverseTransform = currentTransform.createInverse();
            Point2D transformedMouse = inverseTransform.transform(new Point(mx, my), null);
            
            // --- MODIFICATION START ---
            // Update the hit detection rectangle to match the icon's position and size
            int handleY = -height / 2 - (ICON_SIZE + 10);
            if (rotateIcon == null) {
                // Fallback to the old circle dimensions if icon is not present
                 return new Rectangle(-10, -height / 2 - 30, 20, 20).contains(transformedMouse);
            }
            return new Rectangle(-ICON_SIZE / 2, handleY, ICON_SIZE, ICON_SIZE).contains(transformedMouse);
            // --- MODIFICATION END ---

        } catch (java.awt.geom.NoninvertibleTransformException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getResizeHandle(int mx, int my) {
        try {
            AffineTransform currentTransform = new AffineTransform();
            currentTransform.translate(x + width / 2, y + height / 2);
            currentTransform.rotate(Math.toRadians(rotationAngle));
            if (flipHorizontal) currentTransform.scale(-1, 1);
            if (flipVertical) currentTransform.scale(1, -1);

            AffineTransform inverseTransform = currentTransform.createInverse();
            Point2D transformedMouse = inverseTransform.transform(new Point(mx, my), null);

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
    
    public void resize(String handleType, int dx, int dy) {
        final int MIN_SIZE = 20;

        switch (handleType) {
            case "BR":
                width = Math.max(MIN_SIZE, width + (flipHorizontal ? -dx : dx));
                height = Math.max(MIN_SIZE, height + (flipVertical ? -dy : dy));
                break;
            case "TL":
                int newWidthTL = Math.max(MIN_SIZE, width - (flipHorizontal ? -dx : dx));
                int newHeightTL = Math.max(MIN_SIZE, height - (flipVertical ? -dy : dy));
                x += (width - newWidthTL);
                y += (height - newHeightTL);
                width = newWidthTL;
                height = newHeightTL;
                break;
            case "TR":
                int newWidthTR = Math.max(MIN_SIZE, width + (flipHorizontal ? -dx : dx));
                int newHeightTR = Math.max(MIN_SIZE, height - (flipVertical ? -dy : dy));
                y += (height - newHeightTR);
                width = newWidthTR;
                height = newHeightTR;
                break;
            case "BL":
                int newWidthBL = Math.max(MIN_SIZE, width - (flipHorizontal ? -dx : dx));
                int newHeightBL = Math.max(MIN_SIZE, height + (flipVertical ? -dy : dy));
                x += (width - newWidthBL);
                width = newWidthBL;
                height = newHeightBL;
                break;
        }
    }

    public void rotate(int deltaAngle) {
        rotationAngle += deltaAngle;
    }
    
    public void flipVertical() {
        this.flipVertical = !this.flipVertical;
    }

    public void flipHorizontal() {
        this.flipHorizontal = !this.flipHorizontal;
    }
}
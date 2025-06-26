// derrelcodes/ooadlabexerciseg5/ooadlabexerciseg5-main/CreationItem.java
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

public abstract class CreationItem {
    protected Image image;
    protected int x, y, width, height;
    protected double rotationAngle;
    protected boolean isSelected;
    protected boolean flipHorizontal;
    protected boolean flipVertical;

    private static final int HANDLE_SIZE = 10;
    private static final Image rotateIcon = new ImageIcon("icons/RotateImage.png").getImage();
    private static final int ICON_SIZE = 24;

    public CreationItem(String imagePath, int x, int y) {
        this.image = new ImageIcon(imagePath).getImage();
        this.x = x;
        this.y = y;
        this.width = 100;
        this.height = 100;
        this.rotationAngle = 0;
        this.isSelected = false;
        this.flipHorizontal = false;
        this.flipVertical = false;
    }

    // --- Abstract methods for capabilities ---
    public abstract boolean canFlip();
    public abstract boolean canScale();
    public abstract boolean canTranspose();

    // NEW METHOD: To check if the item is a locked background layer
    public boolean isBackgroundLayer() {
        return false;
    }

    // --- Common Drawing and Interaction Logic ---
    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        AffineTransform oldTransform = g2.getTransform();
        g2.translate(x + width / 2.0, y + height / 2.0);
        g2.rotate(Math.toRadians(rotationAngle));

        AffineTransform flipTransform = new AffineTransform();
        if (flipHorizontal) flipTransform.scale(-1, 1);
        if (flipVertical) flipTransform.scale(1, -1);
        g2.transform(flipTransform);

        g2.drawImage(image, -width / 2, -height / 2, width, height, null);

        if (isSelected) {
            g2.setTransform(oldTransform);
            g2.translate(x + width / 2.0, y + height / 2.0);
            g2.rotate(Math.toRadians(rotationAngle));

            g2.setColor(Color.CYAN);
            g2.setStroke(new BasicStroke(2));
            g2.drawRect(-width / 2, -height / 2, width, height);

            if (canScale()) {
                g2.setColor(Color.BLUE);
                g2.fillRect(-width / 2 - HANDLE_SIZE / 2, -height / 2 - HANDLE_SIZE / 2, HANDLE_SIZE, HANDLE_SIZE);
                g2.fillRect(width / 2 - HANDLE_SIZE / 2, -height / 2 - HANDLE_SIZE / 2, HANDLE_SIZE, HANDLE_SIZE);
                g2.fillRect(-width / 2 - HANDLE_SIZE / 2, height / 2 - HANDLE_SIZE / 2, HANDLE_SIZE, HANDLE_SIZE);
                g2.fillRect(width / 2 - HANDLE_SIZE / 2, height / 2 - HANDLE_SIZE / 2, HANDLE_SIZE, HANDLE_SIZE);
            }

            if (rotateIcon != null) {
                g2.drawImage(rotateIcon, -ICON_SIZE / 2, -height / 2 - (ICON_SIZE + 10), ICON_SIZE, ICON_SIZE, null);
            } else {
                g2.setColor(Color.RED);
                g2.fillOval(-10, -height / 2 - 30, 20, 20);
            }
        }
        g2.dispose();
    }

    private AffineTransform getInverseTransform() throws NoninvertibleTransformException {
        AffineTransform currentTransform = new AffineTransform();
        currentTransform.translate(x + width / 2.0, y + height / 2.0);
        currentTransform.rotate(Math.toRadians(rotationAngle));
        if (flipHorizontal) currentTransform.scale(-1, 1);
        if (flipVertical) currentTransform.scale(1, -1);
        return currentTransform.createInverse();
    }

    public boolean contains(int mx, int my) {
        try {
            Point2D transformedMouse = getInverseTransform().transform(new Point(mx, my), null);
            return new Rectangle(-width / 2, -height / 2, width, height).contains(transformedMouse);
        } catch (NoninvertibleTransformException e) {
            return false;
        }
    }

    public boolean onRotateHandle(int mx, int my) {
        if (!isSelected) return false;
        try {
            AffineTransform inverseNoFlip = new AffineTransform();
            inverseNoFlip.translate(x + width / 2.0, y + height / 2.0);
            inverseNoFlip.rotate(Math.toRadians(rotationAngle));
            Point2D transformedMouse = inverseNoFlip.createInverse().transform(new Point(mx, my), null);
            int handleY = -height / 2 - (ICON_SIZE + 10);
            return new Rectangle(-ICON_SIZE / 2, handleY, ICON_SIZE, ICON_SIZE).contains(transformedMouse);
        } catch (NoninvertibleTransformException e) {
            return false;
        }
    }

    public String getResizeHandle(int mx, int my) {
        if (!isSelected || !canScale()) return null;
        try {
            Point2D transformedMouse = getInverseTransform().transform(new Point(mx, my), null);
            if (new Rectangle(-width / 2 - HANDLE_SIZE / 2, -height / 2 - HANDLE_SIZE / 2, HANDLE_SIZE, HANDLE_SIZE).contains(transformedMouse)) return "TL";
            if (new Rectangle(width / 2 - HANDLE_SIZE / 2, -height / 2 - HANDLE_SIZE / 2, HANDLE_SIZE, HANDLE_SIZE).contains(transformedMouse)) return "TR";
            if (new Rectangle(-width / 2 - HANDLE_SIZE / 2, height / 2 - HANDLE_SIZE / 2, HANDLE_SIZE, HANDLE_SIZE).contains(transformedMouse)) return "BL";
            if (new Rectangle(width / 2 - HANDLE_SIZE / 2, height / 2 - HANDLE_SIZE / 2, HANDLE_SIZE, HANDLE_SIZE).contains(transformedMouse)) return "BR";
            return null;
        } catch (NoninvertibleTransformException e) {
            return null;
        }
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public void setPosition(int x, int y) {
        if (canTranspose()) {
            this.x = x;
            this.y = y;
        }
    }

    public void setSelected(boolean selected) { this.isSelected = selected; }
    public void rotate(double deltaAngle) { this.rotationAngle += deltaAngle; }

    public void flipVertical() { if (canFlip()) this.flipVertical = !this.flipVertical; }
    public void flipHorizontal() { if (canFlip()) this.flipHorizontal = !this.flipHorizontal; }

    public void resize(String handleType, int dx, int dy) {
        if (!canScale()) return;
        final int MIN_SIZE = 20;
        double angleRad = Math.toRadians(rotationAngle);
        double cosA = Math.cos(-angleRad);
        double sinA = Math.sin(-angleRad);
        int rotatedDx = (int) (dx * cosA - dy * sinA);
        int rotatedDy = (int) (dx * sinA + dy * cosA);

        switch (handleType) {
            case "BR":
                width = Math.max(MIN_SIZE, width + (flipHorizontal ? -rotatedDx : rotatedDx));
                height = Math.max(MIN_SIZE, height + (flipVertical ? -rotatedDy : rotatedDy));
                break;
            case "TL":
                int newWidthTL = Math.max(MIN_SIZE, width - (flipHorizontal ? -rotatedDx : rotatedDx));
                int newHeightTL = Math.max(MIN_SIZE, height - (flipVertical ? -rotatedDy : rotatedDy));
                x += (width - newWidthTL); y += (height - newHeightTL);
                width = newWidthTL; height = newHeightTL;
                break;
            case "TR":
                int newWidthTR = Math.max(MIN_SIZE, width + (flipHorizontal ? -rotatedDx : rotatedDx));
                int newHeightTR = Math.max(MIN_SIZE, height - (flipVertical ? -rotatedDy : rotatedDy));
                y += (height - newHeightTR);
                width = newWidthTR; height = newHeightTR;
                break;
            case "BL":
                int newWidthBL = Math.max(MIN_SIZE, width - (flipHorizontal ? -rotatedDx : rotatedDx));
                int newHeightBL = Math.max(MIN_SIZE, height + (flipVertical ? -rotatedDy : rotatedDy));
                x += (width - newWidthBL);
                width = newWidthBL; height = newHeightBL;
                break;
        }
    }
}
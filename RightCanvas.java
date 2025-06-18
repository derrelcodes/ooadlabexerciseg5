import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.io.File;

public class RightCanvas extends JPanel {
    private BufferedImage canvas;
    private Graphics2D g2d;
    private int prevX, prevY;
    private boolean drawing = false;

    // Drawing properties
    private Color currentColor = Color.BLACK;
    private int strokeSize = 3; // Pen size
    private int eraserSize = 5; // NEW FIELD: Default eraser size

    private boolean isEraser = false;

    // Custom cursors
    private Cursor penCursor;
    private Cursor eraserCursor;

    // Store drawing paths for undo functionality
    private List<DrawingPath> paths = new ArrayList<>();
    private DrawingPath currentPath;

    public RightCanvas() {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(400, 500));

        // Initialize canvas
        initCanvas();

        // Initialize custom cursors
        initCursors();

        // Set default cursor to pen
        setCursor(penCursor);

        // Add mouse listeners for drawing
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                startDrawing(e.getX(), e.getY());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                stopDrawing();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (drawing) {
                    draw(e.getX(), e.getY());
                }
            }
        });

        // Add component listener to handle resizing
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizeCanvas();
            }
        });
    }

    private void initCanvas() {
        int currentWidth = getWidth() > 0 ? getWidth() : getPreferredSize().width;
        int currentHeight = getHeight() > 0 ? getHeight() : getPreferredSize().height;
        canvas = new BufferedImage(currentWidth, currentHeight, BufferedImage.TYPE_INT_ARGB);
        g2d = canvas.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, currentWidth, currentHeight);
    }

    private void resizeCanvas() {
        int newWidth = getWidth();
        int newHeight = getHeight();

        if (newWidth > 0 && newHeight > 0 && (canvas.getWidth() != newWidth || canvas.getHeight() != newHeight)) {
            BufferedImage newCanvas = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D newG2d = newCanvas.createGraphics();
            newG2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            newG2d.setColor(Color.WHITE);
            newG2d.fillRect(0, 0, newWidth, newHeight);

            if (canvas != null) {
                newG2d.drawImage(canvas, 0, 0, null);
            }

            if (g2d != null) {
                g2d.dispose();
            }
            canvas = newCanvas;
            g2d = newG2d;

            redrawAllPaths();
            repaint();
        }
    }

    private void redrawAllPaths() {
        g2d.setComposite(AlphaComposite.Clear);
        g2d.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        g2d.setComposite(AlphaComposite.SrcOver);
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        for (DrawingPath path : paths) {
            // Use path's color and stroke size when redrawing
            g2d.setColor(path.getColor());
            g2d.setStroke(new BasicStroke(path.getStrokeSize(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            if (path.isEraser()) {
                g2d.setComposite(AlphaComposite.Clear);
            } else {
                g2d.setComposite(AlphaComposite.SrcOver);
            }

            List<Point> points = path.getPoints();
            if (points.size() > 1) {
                for (int i = 0; i < points.size() - 1; i++) {
                    Point p1 = points.get(i);
                    Point p2 = points.get(i + 1);
                    g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
                }
            } else if (points.size() == 1) {
                Point p = points.get(0);
                g2d.drawLine(p.x, p.y, p.x, p.y);
            }
        }
        g2d.setComposite(AlphaComposite.SrcOver);
    }

    private void initCursors() {
        try {
            String[] possiblePaths = {"icons/", "src/icons/", "resources/", ""};
            Image penImage = null;
            Image eraserImage = null;

            for (String path : possiblePaths) {
                try {
                    File penFile = new File(path + "Pen.png");
                    if (penFile.exists()) {
                        penImage = new ImageIcon(path + "Pen.png").getImage();
                        break;
                    }
                } catch (Exception e) {}
            }

            for (String path : possiblePaths) {
                try {
                    File eraserFile = new File(path + "Eraser.png");
                    if (eraserFile.exists()) {
                        eraserImage = new ImageIcon(path + "Eraser.png").getImage();
                        break;
                    }
                } catch (Exception e) {}
            }

            if (penImage != null) {
                penCursor = Toolkit.getDefaultToolkit().createCustomCursor(
                    penImage.getScaledInstance(32, 32, Image.SCALE_SMOOTH),
                    new Point(2, 30), "Pen Cursor");
            } else {
                penCursor = new Cursor(Cursor.CROSSHAIR_CURSOR);
            }

            if (eraserImage != null) {
                eraserCursor = Toolkit.getDefaultToolkit().createCustomCursor(
                    eraserImage.getScaledInstance(32, 32, Image.SCALE_SMOOTH),
                    new Point(16, 16), "Eraser Cursor");
            } else {
                eraserCursor = new Cursor(Cursor.CROSSHAIR_CURSOR);
            }

        } catch (Exception e) {
            penCursor = new Cursor(Cursor.CROSSHAIR_CURSOR);
            eraserCursor = new Cursor(Cursor.CROSSHAIR_CURSOR);
        }
    }

    private void startDrawing(int x, int y) {
        drawing = true;
        prevX = x;
        prevY = y;

        // Pass the correct active stroke size to the DrawingPath
        currentPath = new DrawingPath(currentColor, isEraser ? eraserSize : strokeSize, isEraser);
        currentPath.addPoint(x, y);
    }

    private void draw(int x, int y) {
        if (g2d != null) {
            // Use the correct stroke size based on the current tool
            g2d.setStroke(new BasicStroke(isEraser ? eraserSize : strokeSize, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            if (isEraser) {
                g2d.setComposite(AlphaComposite.Clear);
            } else {
                g2d.setComposite(AlphaComposite.SrcOver);
                g2d.setColor(currentColor);
            }

            g2d.drawLine(prevX, prevY, x, y);

            currentPath.addPoint(x, y);

            prevX = x;
            prevY = y;
            repaint();
        }
    }

    private void stopDrawing() {
        drawing = false;
        if (currentPath != null) {
            paths.add(currentPath);
            currentPath = null;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (canvas == null || canvas.getWidth() != getWidth() || canvas.getHeight() != getHeight()) {
            initCanvas();
            redrawAllPaths();
        }

        if (canvas != null) {
            g.drawImage(canvas, 0, 0, this);
        }
    }

    // Public methods for controls
    public void setDrawingColor(Color color) {
        this.currentColor = color;
        this.isEraser = false; // Selecting color implies pen tool
        setCursor(penCursor);
    }

    public void setStrokeSize(int size) { // Sets pen size
        this.strokeSize = size;
    }

    // NEW METHOD: Sets eraser size
    public void setEraserSize(int size) {
        this.eraserSize = size;
    }

    public void setEraser(boolean eraser) {
        this.isEraser = eraser;
        setCursor(eraser ? eraserCursor : penCursor);
    }

    public boolean isEraser() {
        return isEraser;
    }

    public Color getCurrentColor() {
        return currentColor;
    }

    public int getStrokeSize() { // Returns pen size
        return strokeSize;
    }

    // NEW METHOD: Returns eraser size
    public int getEraserSize() {
        return eraserSize;
    }

    // NEW METHOD: Returns the currently active stroke size (either pen or eraser)
    public int getActiveStrokeSize() {
        return isEraser ? eraserSize : strokeSize;
    }

    public BufferedImage getCanvasImage() {
        return canvas;
    }

    public void clearCanvas() {
        if (g2d != null) {
            g2d.setComposite(AlphaComposite.Clear);
            g2d.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
            g2d.setComposite(AlphaComposite.SrcOver);
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
            paths.clear();
            repaint();
        }
    }

    // Inner class to store drawing paths for potential undo functionality
    private static class DrawingPath {
        private Color color;
        private int strokeSize;
        private boolean isEraser;
        private List<Point> points;

        public DrawingPath(Color color, int strokeSize, boolean isEraser) {
            this.color = color;
            this.strokeSize = strokeSize;
            this.isEraser = isEraser;
            this.points = new ArrayList<>();
        }

        public void addPoint(int x, int y) {
            points.add(new Point(x, y));
        }

        public Color getColor() { return color; }
        public int getStrokeSize() { return strokeSize; }
        public boolean isEraser() { return isEraser; }
        public List<Point> getPoints() { return points; }
    }
}
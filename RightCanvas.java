// derrelcodes/ooadlabexercisegG5/ooadlabexerciseg5-version-reqs/RightCanvas.java
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
    private Color currentColor = Color.BLACK;
    private int strokeSize = 3;
    private static final int ERASER_SIZE = 20;
    private boolean isEraser = false;
    private Cursor penCursor;
    private Cursor eraserCursor;
    private List<DrawingPath> paths = new ArrayList<>();
    private DrawingPath currentPath;

    public RightCanvas() {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(400, 500));
        initCanvas();
        initCursors();
        setCursor(penCursor);
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { startDrawing(e.getX(), e.getY()); }
            public void mouseReleased(MouseEvent e) { stopDrawing(); }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) { if (drawing) { draw(e.getX(), e.getY()); } }
        });
        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) { resizeCanvas(); }
        });
    }

    private void initCanvas() {
        int w = getWidth() > 0 ? getWidth() : 800;
        int h = getHeight() > 0 ? getHeight() : 600;
        if (canvas == null || canvas.getWidth() != w || canvas.getHeight() != h) {
            BufferedImage newCanvas = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D newG2d = newCanvas.createGraphics();
            newG2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            newG2d.setColor(Color.WHITE);
            newG2d.fillRect(0, 0, w, h);
            if (canvas != null) {
                newG2d.drawImage(canvas, 0, 0, null);
            }
            Graphics2D oldG2d = g2d;
            canvas = newCanvas;
            g2d = newG2d;
            if (oldG2d != null) oldG2d.dispose();
            redrawAllPaths();
        }
    }

    private void resizeCanvas() {
        initCanvas();
    }

    private void redrawAllPaths() {
        // The call to initCanvas() was removed from here to fix the recursion bug.
        // The canvas is now cleared directly before redrawing the paths.
        if (g2d != null) {
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, getWidth(), getHeight());

            for (DrawingPath path : paths) {
                g2d.setColor(path.getColor());
                g2d.setStroke(new BasicStroke(path.isEraser() ? ERASER_SIZE : path.getStrokeSize(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                if (path.isEraser()) {
                    g2d.setComposite(AlphaComposite.Clear);
                } else {
                    g2d.setComposite(AlphaComposite.SrcOver);
                }
                List<Point> points = path.getPoints();
                for (int i = 0; i < points.size() - 1; i++) {
                    Point p1 = points.get(i);
                    Point p2 = points.get(i + 1);
                    g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
                }
            }
            g2d.setComposite(AlphaComposite.SrcOver);
        }
        repaint();
    }
    
    // MODIFICATION: Removed path guessing
    private void initCursors() {
        try {
            Toolkit tk = Toolkit.getDefaultToolkit();
            ImageIcon penIcon = new ImageIcon("icons/Pen.png");
            ImageIcon eraserIcon = new ImageIcon("icons/Eraser.png");

            if (penIcon.getIconWidth() > 0) {
                penCursor = tk.createCustomCursor(penIcon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH), new Point(2, 30), "Pen Cursor");
            } else {
                penCursor = new Cursor(Cursor.CROSSHAIR_CURSOR);
            }

            if (eraserIcon.getIconWidth() > 0) {
                eraserCursor = tk.createCustomCursor(eraserIcon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH), new Point(16, 16), "Eraser Cursor");
            } else {
                eraserCursor = new Cursor(Cursor.DEFAULT_CURSOR);
            }
        } catch (Exception e) {
            System.err.println("Could not load custom cursors: " + e.getMessage());
            penCursor = new Cursor(Cursor.CROSSHAIR_CURSOR);
            eraserCursor = new Cursor(Cursor.DEFAULT_CURSOR);
        }
    }

    private void startDrawing(int x, int y) {
        drawing = true;
        prevX = x;
        prevY = y;
        currentPath = new DrawingPath(isEraser ? getBackground() : currentColor, isEraser ? ERASER_SIZE : strokeSize, isEraser);
        currentPath.addPoint(x, y);
    }

    private void draw(int x, int y) {
        if (g2d != null) {
            g2d.setStroke(new BasicStroke(isEraser ? ERASER_SIZE : strokeSize, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            if (isEraser) {
                g2d.setComposite(AlphaComposite.Clear);
                g2d.setColor(new Color(255, 255, 255, 0)); // Transparent color for eraser
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
        if (currentPath != null && currentPath.getPoints().size() > 1) {
            paths.add(currentPath);
        }
        currentPath = null;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (canvas == null) { initCanvas(); }
        g.drawImage(canvas, 0, 0, this);
    }

    public void setDrawingColor(Color color) { this.currentColor = color; this.isEraser = false; setCursor(penCursor); }
    public void setStrokeSize(int size) { this.strokeSize = size; }
    public void setEraser(boolean eraser) { this.isEraser = eraser; setCursor(eraser ? eraserCursor : penCursor); }
    public boolean isEraser() { return isEraser; }
    public Color getCurrentColor() { return currentColor; }
    public int getStrokeSize() { return strokeSize; }
    public BufferedImage getCanvasImage() { return canvas; }
    
    public void clearCanvas() {
        paths.clear();
        if (g2d != null) {
            g2d.setComposite(AlphaComposite.SrcOver);
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
        repaint();
    }

    private static class DrawingPath {
        private Color color;
        private int strokeSize;
        private boolean isEraser;
        private List<Point> points;
        public DrawingPath(Color color, int strokeSize, boolean isEraser) {
            this.color = color; this.strokeSize = strokeSize; this.isEraser = isEraser; this.points = new ArrayList<>();
        }
        public void addPoint(int x, int y) { points.add(new Point(x, y)); }
        public Color getColor() { return color; }
        public int getStrokeSize() { return strokeSize; }
        public boolean isEraser() { return isEraser; }
        public List<Point> getPoints() { return points; }
    }
}
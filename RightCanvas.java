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
    private int strokeSize = 3;
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
    }
    
    private void initCanvas() {
        int width = getPreferredSize().width;
        int height = getPreferredSize().height;
        canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        g2d = canvas.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);
    }
    
    private void initCursors() {
        try {
            // Try to load custom cursor images
            String[] possiblePaths = {"icons/", "src/icons/", "resources/", ""};
            Image penImage = null;
            Image eraserImage = null;
            
            // Try to load pen cursor
            for (String path : possiblePaths) {
                try {
                    File penFile = new File(path + "Pen.png");
                    if (penFile.exists()) {
                        penImage = new ImageIcon(path + "Pen.png").getImage();
                        break;
                    }
                } catch (Exception e) {
                    // Continue to next path
                }
            }
            
            // Try to load eraser cursor
            for (String path : possiblePaths) {
                try {
                    File eraserFile = new File(path + "Eraser.png");
                    if (eraserFile.exists()) {
                        eraserImage = new ImageIcon(path + "Eraser.png").getImage();
                        break;
                    }
                } catch (Exception e) {
                    // Continue to next path
                }
            }
            
            // Create custom cursors if images are found
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
            // Fallback to default cursors
            penCursor = new Cursor(Cursor.CROSSHAIR_CURSOR);
            eraserCursor = new Cursor(Cursor.CROSSHAIR_CURSOR);
        }
    }
    
    private void startDrawing(int x, int y) {
        drawing = true;
        prevX = x;
        prevY = y;
        
        // Create new drawing path
        currentPath = new DrawingPath(currentColor, strokeSize, isEraser);
        currentPath.addPoint(x, y);
    }
    
    private void draw(int x, int y) {
        if (g2d != null) {
            g2d.setStroke(new BasicStroke(strokeSize, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            
            if (isEraser) {
                g2d.setComposite(AlphaComposite.Clear);
            } else {
                g2d.setComposite(AlphaComposite.SrcOver);
                g2d.setColor(currentColor);
            }
            
            g2d.drawLine(prevX, prevY, x, y);
            
            // Add point to current path
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
        if (canvas != null) {
            g.drawImage(canvas, 0, 0, this);
        }
    }
    
    // Public methods for controls
    public void setDrawingColor(Color color) {
        this.currentColor = color;
        this.isEraser = false;
        // Update cursor when switching to pen
        setCursor(penCursor);
    }
    
    public void setStrokeSize(int size) {
        this.strokeSize = size;
    }
    
    public void setEraser(boolean eraser) {
        this.isEraser = eraser;
        // Update cursor based on tool
        setCursor(eraser ? eraserCursor : penCursor);
    }
    
    public boolean isEraser() {
        return isEraser;
    }
    
    public Color getCurrentColor() {
        return currentColor;
    }
    
    public int getStrokeSize() {
        return strokeSize;
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
        
        // Getters for potential future use
        public Color getColor() { return color; }
        public int getStrokeSize() { return strokeSize; }
        public boolean isEraser() { return isEraser; }
        public List<Point> getPoints() { return points; }
    }
}
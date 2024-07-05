/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package volgyerdo.value.ui;

/**
 *
 * @author zsolt
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

public class ScatterPlotPanel extends JPanel {

    private List<DataSeries> dataSeriesList;
    private double minX, maxX, minY, maxY;
    private double scaleX;
    private double scaleY;
    private boolean dataSeriesSet = false;
    private int margin = 50;

    private double zoomFactor = 1.0;
    private double offsetX = 0, offsetY = 0;
    private int panStep = 10;

    private int prevX, prevY;

    private Color bgColor = Color.WHITE;
    private Color axisColor = Color.BLACK;
    private Color textColor = Color.BLACK;
    private Color gridColor = new Color(200, 200, 200, 200);
    
    public ScatterPlotPanel() {
        this.dataSeriesList = new ArrayList<>();
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (dataSeriesSet) {
//                    calculateMinMaxAndScale();
                    repaint();
                }
            }
        });

        JPanel buttonPanel = new JPanel();
        JButton zoomInButton = new JButton("Zoom In");
        JButton zoomOutButton = new JButton("Zoom Out");
        JButton resetButton = new JButton("Reset");

        zoomInButton.setToolTipText("Zoom in (Shortcut: +)");
        zoomOutButton.setToolTipText("Zoom out (Shortcut: -)");
        resetButton.setToolTipText("Reset view (Shortcut: R)");

        zoomInButton.addActionListener(e -> {
            zoomIn(getWidth() / 2, getHeight() / 2);
        });

        zoomOutButton.addActionListener(e -> {
            zoomOut(getWidth() / 2, getHeight() / 2);
        });

        resetButton.addActionListener(e -> {
            resetView();
        });

        buttonPanel.add(zoomInButton);
        buttonPanel.add(zoomOutButton);
        buttonPanel.add(resetButton);

        setLayout(new BorderLayout());
        add(buttonPanel, BorderLayout.SOUTH);

        // Key bindings
        setFocusable(true);
        requestFocusInWindow();

        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ADD, 0), "zoomIn");
        getActionMap().put("zoomIn", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                zoomIn(getWidth() / 2, getHeight() / 2);
            }
        });

        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_SUBTRACT, 0), "zoomOut");
        getActionMap().put("zoomOut", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                zoomOut(getWidth() / 2, getHeight() / 2);
            }
        });

        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_R, 0), "reset");
        getActionMap().put("reset", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetView();
            }
        });

        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "panUp");
        getActionMap().put("panUp", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panUp();
            }
        });

        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "panDown");
        getActionMap().put("panDown", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panDown();
            }
        });

        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "panLeft");
        getActionMap().put("panLeft", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panLeft();
            }
        });

        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "panRight");
        getActionMap().put("panRight", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panRight();
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                prevX = e.getX();
                prevY = e.getY();
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                double dx = e.getX() - prevX;
                double dy = e.getY() - prevY;
                offsetX -= dx / scaleX;
                offsetY += dy / scaleY;
                prevX = e.getX();
                prevY = e.getY();
                repaint();
            }
        });
        addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (e.getPreciseWheelRotation() < 0) {
                    zoomIn(e.getX(), e.getY());
                } else {
                    zoomOut(e.getX(), e.getY());
                }
            }
        });
    }

    public void setDataSeries(List<DataSeries> dataSeriesList) {
        this.dataSeriesList = dataSeriesList;
        calculateMinMaxAndScale();
        this.dataSeriesSet = true;
        repaint();
    }

    private void calculateMinMaxAndScale() {
        if (dataSeriesList == null || dataSeriesList.isEmpty()) {
            return;
        }

        minX = dataSeriesList.stream().flatMap(ds -> ds.getPoints().stream()).mapToDouble(Point::getX).min().orElse(0);
        maxX = dataSeriesList.stream().flatMap(ds -> ds.getPoints().stream()).mapToDouble(Point::getX).max().orElse(0);
        minY = dataSeriesList.stream().flatMap(ds -> ds.getPoints().stream()).mapToDouble(Point::getY).min().orElse(0);
        maxY = dataSeriesList.stream().flatMap(ds -> ds.getPoints().stream()).mapToDouble(Point::getY).max().orElse(0);

        double xRange = (maxX - minX) * zoomFactor;
        double yRange = (maxY - minY) * zoomFactor;

        int padding = margin;
        int labelPadding = 25;
        
        scaleX = ((double) getWidth() - 2 * padding - labelPadding) / xRange;
        scaleY = ((double) getHeight() - 2 * padding - labelPadding) / yRange;
    }

    private void zoomIn(int mouseX, int mouseY) {
        double newZoomFactor = zoomFactor / 1.1;
        adjustOffsetsForZoom(mouseX, mouseY, newZoomFactor / zoomFactor);
        zoomFactor = newZoomFactor;
        calculateScale();
        repaint();
    }

    private void zoomOut(int mouseX, int mouseY) {
        double newZoomFactor = zoomFactor * 1.1;
        adjustOffsetsForZoom(mouseX, mouseY, newZoomFactor / zoomFactor);
        zoomFactor = newZoomFactor;
        calculateScale();
        repaint();
    }
    
    private void calculateScale() {
//        if (dataSeriesList == null || dataSeriesList.isEmpty()) {
//            return;
//        }
//
//        minX = dataSeriesList.stream().flatMap(ds -> ds.getPoints().stream()).mapToDouble(Point::getX).min().orElse(0);
//        maxX = dataSeriesList.stream().flatMap(ds -> ds.getPoints().stream()).mapToDouble(Point::getX).max().orElse(0);
//        minY = dataSeriesList.stream().flatMap(ds -> ds.getPoints().stream()).mapToDouble(Point::getY).min().orElse(0);
//        maxY = dataSeriesList.stream().flatMap(ds -> ds.getPoints().stream()).mapToDouble(Point::getY).max().orElse(0);
//
//        double xRange = (maxX - minX) * zoomFactor;
//
//        int padding = margin;
//        int labelPadding = 25;
//        scaleX = ((double) getWidth() - 2 * padding - labelPadding) / xRange;
    }

    private void adjustOffsetsForZoom(int mouseX, int mouseY, double zoomScale) {
        // Convert mouseX and mouseY from screen coordinates to graph coordinates
        double graphX = (mouseX - margin - getWidth() / 2.0) / scaleX + offsetX;
        double graphY = (getHeight() / 2.0 - mouseY + margin) / scaleY + offsetY;

        // Adjust the offsets to keep the zoom centered on the cursor position
        offsetX += (graphX - offsetX) * (1 - zoomScale);
        offsetY += (graphY - offsetY) * (1 - zoomScale);

        // Convert the offsetY to consider the inversion of the y-axis in screen coordinates
        offsetY = offsetY * zoomScale;
    }

    private void resetView() {
        zoomFactor = 1.0;
        offsetX = 0;
        offsetY = 0;
        calculateMinMaxAndScale();
        repaint();
    }

    private void panUp() {
        offsetY -= panStep / scaleY;
        repaint();
    }

    private void panDown() {
        offsetY += panStep / scaleY;
        repaint();
    }

    private void panLeft() {
        offsetX += panStep / scaleX;
        repaint();
    }

    private void panRight() {
        offsetX -= panStep / scaleX;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (!dataSeriesSet || dataSeriesList == null || dataSeriesList.isEmpty()) {
            return;
        }

        int padding = margin;
        int labelPadding = 25;
        
        int pointWidth = 6;
        int divisionPixelSize = 50; // Fixed pixel size for each division

        int chartWidth = getWidth() - 2 * padding - labelPadding;
        int chartHeight = getHeight() - 2 * padding - labelPadding;

        int numberYDivisions = chartHeight / divisionPixelSize;
        int numberXDivisions = chartWidth / divisionPixelSize;

        // Draw white background
        g.setColor(bgColor);
        g.fillRect(padding + labelPadding, padding, chartWidth, chartHeight);
        g.setColor(axisColor);

        // Draw the axes
        g.drawLine(padding + labelPadding, getHeight() - padding - labelPadding, padding + labelPadding, padding);
        g.drawLine(padding + labelPadding, getHeight() - padding - labelPadding, getWidth() - padding, getHeight() - padding - labelPadding);

        // Draw grid lines and labels for y-axis
        for (int i = 0; i <= numberYDivisions; i++) {
            int x0 = padding + labelPadding;
            int x1 = pointWidth + padding + labelPadding;
            int y0 = getHeight() - (i * divisionPixelSize + padding + labelPadding);
            int y1 = y0;
            if (!dataSeriesList.isEmpty()) {
                g.setColor(gridColor);
                g.drawLine(padding + labelPadding + 1 + pointWidth, y0, getWidth() - padding, y1);
                g.setColor(textColor);
                String yLabel = String.format("%.2f", (minY + offsetY + (maxY - minY) * ((i * divisionPixelSize * 1.0) / chartHeight)));
                FontMetrics metrics = g.getFontMetrics();
                int labelWidth = metrics.stringWidth(yLabel);
                g.drawString(yLabel, x0 - labelWidth - 5, y0 + (metrics.getHeight() / 2) - 3);
            }
            g.drawLine(x0, y0, x1, y1);
        }

        // Draw grid lines and labels for x-axis
        for (int i = 0; i <= numberXDivisions; i++) {
            int y0 = getHeight() - padding - labelPadding;
            int y1 = y0 - pointWidth;
            int x0 = i * divisionPixelSize + padding + labelPadding;
            int x1 = x0;
            if (!dataSeriesList.isEmpty()) {
                g.setColor(gridColor);
                g.drawLine(x0, getHeight() - padding - labelPadding - 1 - pointWidth, x1, padding);
                g.setColor(textColor);
                String xLabel = String.format("%.2f", (minX + offsetX + (maxX - minX) * ((i * divisionPixelSize * 1.0) / chartWidth)));
                FontMetrics metrics = g.getFontMetrics();
                int labelWidth = metrics.stringWidth(xLabel);
                g.drawString(xLabel, x0 - labelWidth / 2, y0 + metrics.getHeight() + 3);
            }
            g.drawLine(x0, y0, x1, y1);
        }

        // Draw points for each data series
        for (DataSeries series : dataSeriesList) {
            if (series.isConnected()) {
                Integer lastX = null, lastY = null;
                g.setColor(series.getColor());
                for (Point point : series.getPoints()) {
                    int x = (int) ((point.getX() - minX - offsetX) * scaleX + padding + labelPadding);
                    int y = (int) ((maxY - point.getY() + offsetY) * scaleY + padding);
                    if (x >= padding + labelPadding && x <= getWidth() - padding && y >= padding && y <= getHeight() - padding - labelPadding) {
                        g.fillOval(x - pointWidth / 2, y - pointWidth / 2, pointWidth, pointWidth);
                        if (lastX != null && lastY != null) {
                            g.drawLine(lastX, lastY, x, y);
                        }
                        lastX = x;
                        lastY = y;
                    } else {
                        lastX = null;
                        lastY = null;
                    }
                }
            } else {
                g.setColor(series.getColor());
                for (Point point : series.getPoints()) {
                    int x = (int) ((point.getX() - minX - offsetX) * scaleX + padding + labelPadding);
                    int y = (int) ((maxY - point.getY() + offsetY) * scaleY + padding);
                    if (x >= padding + labelPadding && x <= getWidth() - padding && y >= padding && y <= getHeight() - padding - labelPadding) {
                        g.fillOval(x - pointWidth / 2, y - pointWidth / 2, pointWidth, pointWidth);
                    }
                }
            }
        }

        // Draw legend
        drawLegend(g, padding);
    }

    private void drawLegend(Graphics g, int padding) {
        int labelWidth = 0;
        for (DataSeries series : dataSeriesList) {
            labelWidth = (int) Math.max(labelWidth, g.getFontMetrics().getStringBounds(series.getName(), g).getWidth());
        }
        labelWidth += 40;

        int legendX = getWidth() - padding - labelWidth;
        int legendY = padding;
        int legendHeight = 10 + dataSeriesList.size() * 20;
        g.setColor(bgColor);
        g.fillRect(legendX, legendY, labelWidth, legendHeight);
        g.setColor(axisColor);
        g.drawRect(legendX, legendY, labelWidth, legendHeight);

        int legendEntryY = legendY + 20;
        for (DataSeries series : dataSeriesList) {
            g.setColor(series.getColor());
            g.fillRect(legendX + 10, legendEntryY - 10, 10, 10);
            g.setColor(textColor);
            g.drawString(series.getName(), legendX + 25, legendEntryY);
            legendEntryY += 20;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Scatter Plot Panel");
            ScatterPlotPanel scatterPlotPanel = new ScatterPlotPanel();
            scatterPlotPanel.setPreferredSize(new Dimension(800, 600));
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().add(scatterPlotPanel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            List<DataSeries> dataSeriesList = List.of(
                    new DataSeries("Series 1", List.of(new Point(0, 0), new Point(1, 2), new Point(2, 4)), Color.RED, true)
//                    new DataSeries("Series 2", List.of(new Point(2, 1), new Point(3, 2), new Point(4, 3)), Color.BLUE),
//                    new DataSeries("Series 3", List.of(new Point(1, 2), new Point(2, 3), new Point(3, 4)), Color.GREEN)
            );
            scatterPlotPanel.setDataSeries(dataSeriesList);
        });
    }
}

class DataSeries {

    private String name;
    private List<Point> points;
    private Color color;
    private boolean connected;

    public DataSeries(String name, List<Point> points, Color color) {
        this(name, points, color, false);
    }

    public DataSeries(String name, List<Point> points, Color color, boolean connected) {
        this.name = name;
        this.points = points;
        this.color = color;
        this.connected = connected;
    }

    public String getName() {
        return name;
    }

    public List<Point> getPoints() {
        return points;
    }

    public Color getColor() {
        return color;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

}

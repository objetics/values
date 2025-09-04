/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package volgyerdo.test.ui;

/**
 *
 * @author zsolt
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.ArrayList;

public class PlotPanel2D extends JPanel {

    private List<DataSeries> dataSeriesList;
    private double minX, maxX, minY, maxY;
    private double scaleX;
    private double scaleY;
    private boolean dataSeriesSet = false;
    private int margin = 25;

    private double offsetX = 0, offsetY = 0;
    private int panStep = 10;
    private int labelPadding = 10; // Restored original value
    private int leftPadding = 15; // Reduce left padding by 15 pixels (was 10)
    private int pointWidth = 6;
    private int divisionPixelSize = 50;

    private Point prevPoint;
    private Dimension prevSize;

    private Color bgColor = Color.WHITE;
    private Color axisColor = Color.BLACK;
    private Color textColor = Color.BLACK;
    private Color gridColor = new Color(200, 200, 200, 200);

    private String xAxisLabel = "";
    private String yAxisLabel = "";
    private int axisLabelPadding = 50;

    public PlotPanel2D() {
        this.dataSeriesList = new ArrayList<>();
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (dataSeriesSet) {
                    if (prevSize != null) {
                        double dx = (getSize().width - prevSize.width) / 2;
                        double dy = (getSize().height - prevSize.height) / 2;
                        offsetX -= dx / scaleX;
                        offsetY += dy / scaleY;
                    }
                    prevSize = getSize();
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
                prevPoint = e.getPoint();
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (prevPoint == null) {
                    return;
                }
                double dx = e.getX() - prevPoint.x;
                double dy = e.getY() - prevPoint.y;
                offsetX -= dx / scaleX;
                offsetY += dy / scaleY;
                prevPoint = e.getPoint();
                repaint();
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                int totalXPadding = (labelPadding - leftPadding) + (yAxisLabel.isEmpty() ? 0 : axisLabelPadding);
                int totalYPadding = labelPadding + (xAxisLabel.isEmpty() ? 0 : axisLabelPadding);
                if (e.getX() > margin + totalXPadding && e.getX() < getWidth() - margin
                        && e.getY() > margin && e.getY() < getHeight() - margin - totalYPadding) {
                    if (getCursor().getType() != Cursor.HAND_CURSOR) {
                        setCursor(new Cursor(Cursor.HAND_CURSOR));
                    }
                } else {
                    if (getCursor().getType() != Cursor.DEFAULT_CURSOR) {
                        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    }
                }
            }

        });
        addMouseWheelListener((MouseWheelEvent e) -> {
            if (e.getPreciseWheelRotation() < 0) {
                zoomIn(e.getX(), e.getY());
            } else {
                zoomOut(e.getX(), e.getY());
            }
        });
    }

    public void setDataSeries(List<DataSeries> dataSeriesList) {
        this.dataSeriesList = dataSeriesList;
        resetParameters();
        this.dataSeriesSet = true;
        repaint();
    }

    public void addDataSeries(DataSeries dataSeries) {
        this.dataSeriesList.add(dataSeries);
        resetParameters();
        this.dataSeriesSet = true;
        repaint();
    }

    public void setXAxisLabel(String xAxisLabel) {
        this.xAxisLabel = xAxisLabel != null ? xAxisLabel : "";
        repaint();
    }

    public void setYAxisLabel(String yAxisLabel) {
        this.yAxisLabel = yAxisLabel != null ? yAxisLabel : "";
        repaint();
    }

    public void setAxisLabels(String xAxisLabel, String yAxisLabel) {
        this.xAxisLabel = xAxisLabel != null ? xAxisLabel : "";
        this.yAxisLabel = yAxisLabel != null ? yAxisLabel : "";
        repaint();
    }

    private void resetParameters() {
        if (dataSeriesList == null || dataSeriesList.isEmpty()) {
            return;
        }
        
        offsetX = 0;
        offsetY = 0;

        minX = dataSeriesList.stream().flatMap(ds -> ds.getPoints().stream()).mapToDouble(Point2D::getX).min().orElse(0);
        maxX = dataSeriesList.stream().flatMap(ds -> ds.getPoints().stream()).mapToDouble(Point2D::getX).max().orElse(0);
        minY = dataSeriesList.stream().flatMap(ds -> ds.getPoints().stream()).mapToDouble(Point2D::getY).min().orElse(0);
        maxY = dataSeriesList.stream().flatMap(ds -> ds.getPoints().stream()).mapToDouble(Point2D::getY).max().orElse(0);

        double xRange = (maxX - minX);
        double yRange = (maxY - minY);
        
        // Handle constant values (horizontal/vertical lines)
        if (xRange == 0) {
            xRange = 1.0; // Set a minimum range for constant X values
            minX -= 0.5;
            maxX += 0.5;
        }
        if (yRange == 0) {
            yRange = 1.0; // Set a minimum range for constant Y values  
            minY -= 0.5;
            maxY += 0.5;
        }

        int totalXPadding = (labelPadding - leftPadding) + (yAxisLabel.isEmpty() ? 0 : axisLabelPadding);
        int totalYPadding = labelPadding + (xAxisLabel.isEmpty() ? 0 : axisLabelPadding);

        scaleX = ((double) getWidth() - 2 * margin - totalXPadding) / xRange;
        scaleY = ((double) getHeight() - 2 * margin - totalYPadding) / yRange;

        zoomOut(getWidth() / 2, getHeight() / 2);
    }

    private void zoomIn(int mouseX, int mouseY) {
        scaleX *= 1.1;
        scaleY *= 1.1;
        int totalXPadding = (labelPadding - leftPadding) + (yAxisLabel.isEmpty() ? 0 : axisLabelPadding);
        int totalYPadding = labelPadding + (xAxisLabel.isEmpty() ? 0 : axisLabelPadding);
        offsetX += ((getWidth() - 2 * margin - totalXPadding) * 0.1 * mouseX / getWidth()) / scaleX;
        offsetY -= ((getHeight() - 2 * margin - totalYPadding) * 0.1 * mouseY / getHeight()) / scaleY;
        repaint();
    }

    private void zoomOut(int mouseX, int mouseY) {
        scaleX /= 1.1;
        scaleY /= 1.1;
        int totalXPadding = (labelPadding - leftPadding) + (yAxisLabel.isEmpty() ? 0 : axisLabelPadding);
        int totalYPadding = labelPadding + (xAxisLabel.isEmpty() ? 0 : axisLabelPadding);
        offsetX -= ((getWidth() - 2 * margin - totalXPadding) * 0.1 * mouseX / getWidth()) / scaleX;
        offsetY += ((getHeight() - 2 * margin - totalYPadding) * 0.05 * mouseY / getHeight()) / scaleY;
        repaint();
    }

    private void resetView() {
        offsetX = 0;
        offsetY = 0;
        resetParameters();
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
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);

        if (!dataSeriesSet || dataSeriesList == null || dataSeriesList.isEmpty()) {
            return;
        }

        // Calculate dynamic padding based on axis values
        int[] padding = calculateTotalPadding(g2);
        int totalXPadding = padding[0];
        int totalYPadding = padding[1];
        
        // Get font metrics for label drawing
        FontMetrics metrics = g2.getFontMetrics();

        int chartWidth = getWidth() - 2 * margin - totalXPadding;
        int chartHeight = getHeight() - 2 * margin - totalYPadding;

        int numberYDivisions = chartHeight / divisionPixelSize;
        int numberXDivisions = chartWidth / divisionPixelSize;

        // Draw white background
        g2.setColor(bgColor);
        g2.fillRect(margin + totalXPadding, margin, chartWidth, chartHeight);
        g2.setColor(axisColor);

        // Draw the axes
        g2.drawLine(margin + totalXPadding, getHeight() - margin - totalYPadding, margin + totalXPadding, margin);
        g2.drawLine(margin + totalXPadding, getHeight() - margin - totalYPadding, getWidth() - margin, getHeight() - margin - totalYPadding);

        // Draw grid lines and labels for y-axis
        for (int i = 0; i <= numberYDivisions; i++) {
            int x0 = margin + totalXPadding;
            int x1 = pointWidth + margin + totalXPadding;
            int y0 = getHeight() - (i * divisionPixelSize + margin + totalYPadding);
            int y1 = y0;
            if (!dataSeriesList.isEmpty()) {
                g2.setColor(gridColor);
                g2.drawLine(margin + totalXPadding + 1 + pointWidth, y0, getWidth() - margin, y1);
                g2.setColor(textColor);
                String yLabel = String.format("%.2f",
                        (minY + (maxY - minY) + offsetY - chartHeight / scaleY + (i * divisionPixelSize) / scaleY));
                int labelWidth = metrics.stringWidth(yLabel);
                g2.drawString(yLabel, x0 - labelWidth - 5, y0 + (metrics.getHeight() / 2) - 3);
            }
            g2.drawLine(x0, y0, x1, y1);
        }

        // Draw grid lines and labels for x-axis
        for (int i = 0; i <= numberXDivisions; i++) {
            int y0 = getHeight() - margin - totalYPadding;
            int y1 = y0 - pointWidth;
            int x0 = i * divisionPixelSize + margin + totalXPadding;
            int x1 = x0;
            if (!dataSeriesList.isEmpty()) {
                g2.setColor(gridColor);
                g2.drawLine(x0, getHeight() - margin - totalYPadding - 1 - pointWidth, x1, margin);
                g2.setColor(textColor);
                String xLabel = String.format("%.2f", (minX + offsetX + (i * divisionPixelSize) / scaleX));
                int labelWidth = metrics.stringWidth(xLabel);
                g2.drawString(xLabel, x0 - labelWidth / 2, y0 + metrics.getHeight() + 3);
            }
            g2.drawLine(x0, y0, x1, y1);
        }

        g2.setClip(margin + totalXPadding, margin, chartWidth, chartHeight);

        // Draw points for each data series
        for (DataSeries series : dataSeriesList) {
            if (series.isConnected()) {
                g2.setColor(series.getColor());
                
                // Set line thickness with smooth stroke
                BasicStroke stroke = new BasicStroke(
                    series.getLineThickness(),
                    BasicStroke.CAP_ROUND,     // Round line caps for smoother appearance
                    BasicStroke.JOIN_ROUND     // Round line joins for smoother appearance
                );
                g2.setStroke(stroke);
                
                // Use Path2D for smoother line drawing with floating point coordinates
                java.awt.geom.Path2D.Double path = new java.awt.geom.Path2D.Double();
                boolean firstPoint = true;
                
                for (Point2D point : series.getPoints()) {
                    double x = (point.getX() - minX - offsetX) * scaleX + margin + totalXPadding;
                    double y = (maxY - point.getY() + offsetY) * scaleY + margin;
                    
                    if (firstPoint) {
                        path.moveTo(x, y);
                        firstPoint = false;
                    } else {
                        path.lineTo(x, y);
                    }
                }
                
                // Draw the smooth path
                g2.draw(path);
                
                // Draw bullets if needed
                if (series.hasBullets()) {
                    int nodeSize = series.getNodeSize();
                    for (Point2D point : series.getPoints()) {
                        double x = (point.getX() - minX - offsetX) * scaleX + margin + totalXPadding;
                        double y = (maxY - point.getY() + offsetY) * scaleY + margin;
                        if (isOnDiagram((int)x, (int)y)) {
                            g2.fillOval((int)(x - nodeSize / 2.0), (int)(y - nodeSize / 2.0), nodeSize, nodeSize);
                        }
                    }
                }
                
                // Reset stroke to default
                g2.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            } else {
                g2.setColor(series.getColor());
                int nodeSize = series.getNodeSize();
                for (Point2D point : series.getPoints()) {
                    double x = (point.getX() - minX - offsetX) * scaleX + margin + totalXPadding;
                    double y = (maxY - point.getY() + offsetY) * scaleY + margin;
                    if (isOnDiagram((int)x, (int)y)) {
                        g2.fillOval((int)(x - nodeSize / 2.0), (int)(y - nodeSize / 2.0), nodeSize, nodeSize);
                    }
                }
            }
        }

        g2.setClip(null);

        // Draw axis labels
        drawAxisLabels(g2);

        // Draw legend
        drawLegend(g2, margin);
    }

    private void drawAxisLabels(Graphics2D g2) {
        // Save original font
        Font originalFont = g2.getFont();
        
        // Create larger, bold font for axis labels
        Font axisLabelFont = new Font(originalFont.getName(), Font.BOLD, originalFont.getSize() + 4);
        g2.setFont(axisLabelFont);
        g2.setColor(textColor);
        
        FontMetrics metrics = g2.getFontMetrics();
        
        // Draw X-axis label (horizontal, at bottom)
        if (!xAxisLabel.isEmpty()) {
            // Use dynamic padding calculation for consistency
            int[] padding = calculateTotalPadding(g2);
            int totalXPadding = padding[0];
            int chartWidth = getWidth() - 2 * margin - totalXPadding;
            int labelWidth = metrics.stringWidth(xAxisLabel);
            int x = margin + totalXPadding + (chartWidth - labelWidth) / 2;
            int y = getHeight() - 45; // Moved closer to bottom (was 70)
            g2.drawString(xAxisLabel, x, y);
        }
        
        // Draw Y-axis label (vertical, at left)
        if (!yAxisLabel.isEmpty()) {
            // Use dynamic padding calculation for consistency
            int[] padding = calculateTotalPadding(g2);
            int totalYPadding = padding[1];
            int chartHeight = getHeight() - 2 * margin - totalYPadding;
            
            // Save the current transform
            java.awt.geom.AffineTransform oldTransform = g2.getTransform();
            
            // Calculate position for vertical text
            int labelWidth = metrics.stringWidth(yAxisLabel);
            int x = 25; // Reduced padding by another 5 pixels (was 30)
            int y = margin + (chartHeight + labelWidth) / 2;
            
            // Rotate and draw the text
            g2.translate(x, y);
            g2.rotate(-Math.PI / 2);
            g2.drawString(yAxisLabel, 0, 0);
            
            // Restore the original transform
            g2.setTransform(oldTransform);
        }
        
        // Restore original font
        g2.setFont(originalFont);
    }

    private boolean isOnDiagram(int x, int y) {
        // Use Graphics2D to get consistent padding calculation
        Graphics2D g2 = (Graphics2D) getGraphics();
        if (g2 == null) {
            // Fallback to simple calculation if graphics not available
            int totalXPadding = labelPadding + (yAxisLabel.isEmpty() ? 0 : axisLabelPadding);
            int totalYPadding = labelPadding + (xAxisLabel.isEmpty() ? 0 : axisLabelPadding);
            return x >= margin + totalXPadding && x <= getWidth() - margin && y >= margin && y <= getHeight() - margin - totalYPadding;
        }
        
        int[] padding = calculateTotalPadding(g2);
        int totalXPadding = padding[0];
        int totalYPadding = padding[1];
        return x >= margin + totalXPadding && x <= getWidth() - margin && y >= margin && y <= getHeight() - margin - totalYPadding;
    }

    private void drawLegend(Graphics2D g, int padding) {
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
            JFrame frame = new JFrame("Plot Panel 2D");
            PlotPanel2D scatterPlotPanel = new PlotPanel2D();
            scatterPlotPanel.setPreferredSize(new Dimension(800, 600));
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().add(scatterPlotPanel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            List<DataSeries> dataSeriesList = List.of(
                    new DataSeries("Series 1", List.of(new Point2D.Float(-1, -2), new Point2D.Float(0, 0), new Point2D.Float(1, 2), new Point2D.Double(2, 4)), Color.RED, true, true),
                    new DataSeries("Series 2", List.of(new Point2D.Float(2, 1), new Point2D.Float(3, 2), new Point2D.Float(4, 3)), Color.BLUE, true, true),
                    new DataSeries("Series 3", List.of(new Point2D.Float(1, 2), new Point2D.Float(2, 3), new Point2D.Float(3, 4)), Color.GREEN, true, true)
            );
            scatterPlotPanel.setDataSeries(dataSeriesList);
        });
    }

    private int calculateMaxYLabelWidth(FontMetrics metrics) {
        if (!dataSeriesSet || dataSeriesList == null || dataSeriesList.isEmpty()) {
            return 0;
        }
        
        int maxWidth = 0;
        int estimatedDivisions = Math.max(1, (getHeight() - 2 * margin) / divisionPixelSize);
        
        for (int i = 0; i <= estimatedDivisions; i++) {
            double yValue = minY + offsetY + (i * divisionPixelSize) / scaleY;
            String yLabel = String.format("%.2f", yValue);
            int width = metrics.stringWidth(yLabel);
            maxWidth = Math.max(maxWidth, width);
        }
        
        return maxWidth + 10; // Add some padding
    }
    
    private int calculateMaxXLabelHeight(FontMetrics metrics) {
        return metrics.getHeight() + 5; // Height plus some padding
    }
    
    private int[] calculateTotalPadding(Graphics2D g2) {
        FontMetrics metrics = g2.getFontMetrics();
        int maxYLabelWidth = calculateMaxYLabelWidth(metrics);
        int maxXLabelHeight = calculateMaxXLabelHeight(metrics);
        
        // If axis labels are set, use axisLabelPadding, otherwise use dynamic padding
        int yAxisSpace = yAxisLabel.isEmpty() ? maxYLabelWidth : axisLabelPadding;
        int xAxisSpace = xAxisLabel.isEmpty() ? maxXLabelHeight : axisLabelPadding;
        
        int totalXPadding = (labelPadding - leftPadding) + yAxisSpace;
        int totalYPadding = labelPadding + xAxisSpace;
        
        return new int[]{totalXPadding, totalYPadding};
    }
}

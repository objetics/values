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
    private int margin = 50;

    private double offsetX = 0, offsetY = 0;
    private int panStep = 10;
    private int labelPadding = 25;
    private int pointWidth = 6;
    private int divisionPixelSize = 50;

    private Point prevPoint;
    private Dimension prevSize;

    private Color bgColor = Color.WHITE;
    private Color axisColor = Color.BLACK;
    private Color textColor = Color.BLACK;
    private Color gridColor = new Color(200, 200, 200, 200);

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
                if (e.getX() > margin + labelPadding && e.getX() < getWidth() - margin
                        && e.getY() > margin && e.getY() < getHeight() - margin - labelPadding) {
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

        scaleX = ((double) getWidth() - 2 * margin - labelPadding) / xRange;
        scaleY = ((double) getHeight() - 2 * margin - labelPadding) / yRange;

        zoomOut(getWidth() / 2, getHeight() / 2);
    }

    private void zoomIn(int mouseX, int mouseY) {
        scaleX *= 1.1;
        scaleY *= 1.1;
        offsetX += ((getWidth() - 2 * margin - labelPadding) * 0.1 * mouseX / getWidth()) / scaleX;
        offsetY -= ((getHeight() - 2 * margin - labelPadding) * 0.1 * mouseY / getHeight()) / scaleY;
        repaint();
    }

    private void zoomOut(int mouseX, int mouseY) {
        scaleX /= 1.1;
        scaleY /= 1.1;
        offsetX -= ((getWidth() - 2 * margin - labelPadding) * 0.1 * mouseX / getWidth()) / scaleX;
        offsetY += ((getHeight() - 2 * margin - labelPadding) * 0.05 * mouseY / getHeight()) / scaleY;
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

        if (!dataSeriesSet || dataSeriesList == null || dataSeriesList.isEmpty()) {
            return;
        }

        int chartWidth = getWidth() - 2 * margin - labelPadding;
        int chartHeight = getHeight() - 2 * margin - labelPadding;

        int numberYDivisions = chartHeight / divisionPixelSize;
        int numberXDivisions = chartWidth / divisionPixelSize;

        // Draw white background
        g.setColor(bgColor);
        g.fillRect(margin + labelPadding, margin, chartWidth, chartHeight);
        g.setColor(axisColor);

        // Draw the axes
        g.drawLine(margin + labelPadding, getHeight() - margin - labelPadding, margin + labelPadding, margin);
        g.drawLine(margin + labelPadding, getHeight() - margin - labelPadding, getWidth() - margin, getHeight() - margin - labelPadding);

        // Draw grid lines and labels for y-axis
        for (int i = 0; i <= numberYDivisions; i++) {
            int x0 = margin + labelPadding;
            int x1 = pointWidth + margin + labelPadding;
            int y0 = getHeight() - (i * divisionPixelSize + margin + labelPadding);
            int y1 = y0;
            if (!dataSeriesList.isEmpty()) {
                g.setColor(gridColor);
                g.drawLine(margin + labelPadding + 1 + pointWidth, y0, getWidth() - margin, y1);
                g.setColor(textColor);
                String yLabel = String.format("%.2f",
                        (minY + (maxY - minY) + offsetY - chartHeight / scaleY + (i * divisionPixelSize) / scaleY));
                FontMetrics metrics = g.getFontMetrics();
                int labelWidth = metrics.stringWidth(yLabel);
                g.drawString(yLabel, x0 - labelWidth - 5, y0 + (metrics.getHeight() / 2) - 3);
            }
            g.drawLine(x0, y0, x1, y1);
        }

        // Draw grid lines and labels for x-axis
        for (int i = 0; i <= numberXDivisions; i++) {
            int y0 = getHeight() - margin - labelPadding;
            int y1 = y0 - pointWidth;
            int x0 = i * divisionPixelSize + margin + labelPadding;
            int x1 = x0;
            if (!dataSeriesList.isEmpty()) {
                g.setColor(gridColor);
                g.drawLine(x0, getHeight() - margin - labelPadding - 1 - pointWidth, x1, margin);
                g.setColor(textColor);
                String xLabel = String.format("%.2f", (minX + offsetX + (i * divisionPixelSize) / scaleX));
                FontMetrics metrics = g.getFontMetrics();
                int labelWidth = metrics.stringWidth(xLabel);
                g.drawString(xLabel, x0 - labelWidth / 2, y0 + metrics.getHeight() + 3);
            }
            g.drawLine(x0, y0, x1, y1);
        }

        g.setClip(margin + labelPadding, margin, chartWidth, chartHeight);

        // Draw points for each data series
        for (DataSeries series : dataSeriesList) {
            if (series.isConnected()) {
                Integer lastX = null, lastY = null;
                g.setColor(series.getColor());
                for (Point2D point : series.getPoints()) {
                    int x = (int) ((point.getX() - minX - offsetX) * scaleX + margin + labelPadding);
                    int y = (int) ((maxY - point.getY() + offsetY) * scaleY + margin);
                    if (lastX != null && lastY != null && (isOnDiagram(x, y) || isOnDiagram(lastX, lastY))) {
                        g.drawLine(lastX, lastY, x, y);
                    }
                    if (series.hasBullets()) {
                        g.fillOval(x - pointWidth / 2, y - pointWidth / 2, pointWidth, pointWidth);
                    }
                    lastX = x;
                    lastY = y;
                }
            } else {
                g.setColor(series.getColor());
                for (Point2D point : series.getPoints()) {
                    int x = (int) ((point.getX() - minX - offsetX) * scaleX + margin + labelPadding);
                    int y = (int) ((maxY - point.getY() + offsetY) * scaleY + margin);
                    if (isOnDiagram(x, y)) {
                        g.fillOval(x - pointWidth / 2, y - pointWidth / 2, pointWidth, pointWidth);
                    }
                }
            }
        }

        g.setClip(null);

        // Draw legend
        drawLegend(g, margin);
    }

    private boolean isOnDiagram(int x, int y) {
        return x >= margin + labelPadding && x <= getWidth() - margin && y >= margin && y <= getHeight() - margin - labelPadding;
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
}

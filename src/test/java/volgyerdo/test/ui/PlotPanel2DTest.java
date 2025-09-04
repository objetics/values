package volgyerdo.test.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.List;

/**
 * Test class for PlotPanel2D to verify constant value series plotting
 *
 * @author zsolt
 */
public class PlotPanel2DTest {
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("PlotPanel2D - Constant Values Test");
            PlotPanel2D plotPanel = new PlotPanel2D();
            plotPanel.setPreferredSize(new Dimension(800, 600));
            
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().add(plotPanel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            // Test constant Y values (horizontal line)
            List<DataSeries> dataSeriesList = List.of(
                new DataSeries("Horizontal Line", List.of(
                    new Point2D.Double(0, 1), 
                    new Point2D.Double(1, 1), 
                    new Point2D.Double(2, 1), 
                    new Point2D.Double(3, 1)
                ), Color.RED, true, true),
                
                // Test constant X values (vertical line)
                new DataSeries("Vertical Line", List.of(
                    new Point2D.Double(2, 0), 
                    new Point2D.Double(2, 1), 
                    new Point2D.Double(2, 2), 
                    new Point2D.Double(2, 3)
                ), Color.BLUE, true, true),
                
                // Test normal varying data for comparison
                new DataSeries("Normal Data", List.of(
                    new Point2D.Double(0, 0), 
                    new Point2D.Double(1, 2), 
                    new Point2D.Double(2, 1.5), 
                    new Point2D.Double(3, 3)
                ), Color.GREEN, true, true)
            );
            
            plotPanel.setDataSeries(dataSeriesList);
        });
    }
}

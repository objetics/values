package volgyerdo.test.ui;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;
import javax.swing.*;

import volgyerdo.commons.diagram.DataSeries;
import volgyerdo.commons.diagram.PlotPanel2D;
import volgyerdo.value.logic.method.assembly.AssemblyMeasure;
import volgyerdo.value.logic.method.assembly.InfoBasedAssembly;
import volgyerdo.value.logic.method.information.GZIPInfo;

/**
 * Performance teszt az AssemblyMeasure és InfoBasedAssembly[GZIPInfo] 
 * futásidejének összehasonlítására különböző méretű szöveghalmazok esetén.
 * 
 * @author zsolt
 */
public class AssemblyPerformanceTest {
    
    private static final int MIN_SIZE = 1;
    private static final int MAX_SIZE = 200000; // Kisebb tartomány a debug célokra
    private static final int WARMUP_RUNS = 1; // Kevesebb warmup
    private static final int MEASUREMENT_RUNS = 3; // Kevesebb mérés
    
    private final AssemblyMeasure assemblyMeasure;
    private final InfoBasedAssembly infoBasedAssembly;
    private final Random random;
    
    // Eredmények tárolása
    private final List<Point2D> assemblyMeasureResults;
    private final List<Point2D> infoBasedAssemblyResults;
    
    public AssemblyPerformanceTest() {
        this.assemblyMeasure = new AssemblyMeasure();
        this.infoBasedAssembly = new InfoBasedAssembly(new GZIPInfo());
        this.random = new Random(42); // Fixen seed a reprodukálhatóságért
        this.assemblyMeasureResults = new ArrayList<>();
        this.infoBasedAssemblyResults = new ArrayList<>();
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (Exception e) {
                // Nimbus Look and Feel not available, continue with default
            }
            
            AssemblyPerformanceTest test = new AssemblyPerformanceTest();
            test.createAndShowGUI();
        });
    }
    
    private void createAndShowGUI() {
        JFrame frame = new JFrame("Assembly Performance Comparison Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(Color.WHITE);
        
        // Progress bar
        JProgressBar progressBar = new JProgressBar(0, MAX_SIZE);
        progressBar.setStringPainted(true);
        progressBar.setString("Initializing...");
        progressBar.setBackground(Color.WHITE);
        frame.add(progressBar, BorderLayout.NORTH);
        
        // Plot panel
        PlotPanel2D plotPanel = new PlotPanel2D();
        plotPanel.setBackground(Color.WHITE);
        plotPanel.setAxisLabels("Collection Size", "Computation Time (s)");
        plotPanel.setPlotTitle("Computation time vs Collection Size");
        plotPanel.setPreferredSize(new Dimension(500, 500));
        
        frame.add(plotPanel, BorderLayout.CENTER);
        
        frame.setSize(500, 500);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        
        // Futási idő mérések háttérszálban
        SwingWorker<Void, Integer> worker = new SwingWorker<Void, Integer>() {
            @Override
            protected Void doInBackground() throws Exception {
                for (int size = MIN_SIZE; size <= MAX_SIZE; size += Math.max(1, size / 5)) {
                    publish(size);
                    
                    // Szöveghalmaz generálása
                    Collection<String> textCollection = generateTextCollection(size);
                    
                    // AssemblyMeasure mérése
                    double assemblyTime = measureExecutionTime(() -> {
                        assemblyMeasure.value(textCollection);
                    });
                    
                    // InfoBasedAssembly mérése
                    double infoTime = measureExecutionTime(() -> {
                        infoBasedAssembly.value(textCollection);
                    });
                    
                    assemblyMeasureResults.add(new Point2D.Double(size, assemblyTime/1000));
                    infoBasedAssemblyResults.add(new Point2D.Double(size, infoTime/1000));
                    
                    // Kis szünet, hogy a GUI frissüljön
                    Thread.sleep(10);
                }
                return null;
            }
            
            @Override
            protected void process(List<Integer> chunks) {
                if (!chunks.isEmpty()) {
                    int currentSize = chunks.get(chunks.size() - 1);
                    progressBar.setValue(currentSize);
                    progressBar.setString(String.format("Testing size %d/%d", currentSize, MAX_SIZE));
                }
            }
            
            @Override
            protected void done() {
                // Eredmények megjelenítése
                List<DataSeries> series = Arrays.asList(
                    new DataSeries("Assembly", assemblyMeasureResults, 
                                   Color.GRAY, true, true, 3, 5),
                    new DataSeries("A[I_GZIP_]", infoBasedAssemblyResults, 
                                   Color.BLACK, true, true, 3, 5)
                );
                
                plotPanel.setDataSeries(series);
                progressBar.setVisible(false);
            }
        };
        
        worker.execute();
    }
    
    /**
     * Szöveghalmaz generálása adott mérettel és exponenciális eloszlással.
     * A halmaz (size/2)-féle egyedi elemet tartalmaz.
     */
    private Collection<String> generateTextCollection(int size) {
        int uniqueElements = Math.max(1, size / 2);
        List<String> uniqueStrings = new ArrayList<>();
        
        // Egyedi szövegek generálása
        for (int i = 0; i < uniqueElements; i++) {
            // Fix 10 karakter hosszúságú szövegek, vegyes tartalom
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < 10; j++) {
                if (random.nextBoolean()) {
                    // Betű
                    sb.append((char) ('a' + random.nextInt(26)));
                } else {
                    // Szám
                    sb.append((char) ('0' + random.nextInt(10)));
                }
            }
            uniqueStrings.add(sb.toString());
        }
        
        // Exponenciális eloszlás szerinti súlyok
        double[] weights = new double[uniqueElements];
        for (int i = 0; i < uniqueElements; i++) {
            // Exponenciális csökkenés: először gyakoribb elemek
            weights[i] = Math.exp(-2.0 * i / uniqueElements);
        }
        
        // Normalizálás
        double sum = Arrays.stream(weights).sum();
        for (int i = 0; i < weights.length; i++) {
            weights[i] /= sum;
        }
        
        // Szöveghalmaz összeállítása súlyok szerint
        List<String> collection = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            double r = random.nextDouble();
            double cumulativeWeight = 0;
            for (int j = 0; j < uniqueElements; j++) {
                cumulativeWeight += weights[j];
                if (r <= cumulativeWeight) {
                    collection.add(uniqueStrings.get(j));
                    break;
                }
            }
        }
        
        return collection;
    }
    
    /**
     * Futási idő mérése bemelegítéssel és többszöri futtatással.
     */
    private double measureExecutionTime(Runnable task) {
        // Bemelegítés
        for (int i = 0; i < WARMUP_RUNS; i++) {
            task.run();
        }
        
        // Tényleges mérés
        long totalTime = 0;
        for (int i = 0; i < MEASUREMENT_RUNS; i++) {
            long startTime = System.nanoTime();
            task.run();
            long endTime = System.nanoTime();
            totalTime += (endTime - startTime);
        }
        
        // Átlag milliszekundumban
        return (totalTime / MEASUREMENT_RUNS) / 1_000_000.0;
    }
}

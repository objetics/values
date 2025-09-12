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
 * futásidejének összehasonlítására fix méretű szöveghalmazok esetén, 
 * ahol az elemek hossza változik 1-től 100-ig.
 * 
 * @author zsolt
 */
public class AssemblyLengthPerformanceTest {
    
    private static final int COLLECTION_SIZE = 100; // Fix méret
    private static final int MIN_LENGTH = 1;
    private static final int MAX_LENGTH = 250;
    private static final int WARMUP_RUNS = 0; // Kevesebb warmup
    private static final int MEASUREMENT_RUNS = 2; // Kevesebb mérés
    
    private final AssemblyMeasure assemblyMeasure;
    private final InfoBasedAssembly infoBasedAssembly;
    private final Random random;
    
    // Eredmények tárolása
    private final List<Point2D> assemblyMeasureResults;
    private final List<Point2D> infoBasedAssemblyResults;
    
    public AssemblyLengthPerformanceTest() {
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
            
            AssemblyLengthPerformanceTest test = new AssemblyLengthPerformanceTest();
            test.createAndShowGUI();
        });
    }
    
    private void createAndShowGUI() {
        JFrame frame = new JFrame("Assembly Performance vs String Length Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(Color.WHITE);
        
        // Progress bar
        JProgressBar progressBar = new JProgressBar(0, MAX_LENGTH);
        progressBar.setStringPainted(true);
        progressBar.setString("Initializing...");
        progressBar.setBackground(Color.WHITE);
        frame.add(progressBar, BorderLayout.NORTH);
        
        // Plot panel
        PlotPanel2D plotPanel = new PlotPanel2D();
        plotPanel.setBackground(Color.WHITE);
        plotPanel.setAxisLabels("Object Length", "Computation Time (s)");
        plotPanel.setPlotTitle("Computation Time vs Object Length");
        plotPanel.setPreferredSize(new Dimension(500, 500));
        
        frame.add(plotPanel, BorderLayout.CENTER);
        
        frame.setSize(500, 500);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        
        // Futási idő mérések háttérszálban
        SwingWorker<Void, Integer> worker = new SwingWorker<Void, Integer>() {
            @Override
            protected Void doInBackground() throws Exception {
                for (int length = MIN_LENGTH; length <= MAX_LENGTH; length += Math.max(1, length / 5)) {
                    publish(length);
                    
                    // Szöveghalmaz generálása fix mérettel és adott elem hosszúsággal
                    Collection<String> textCollection = generateTextCollection(length);
                    
                    // AssemblyMeasure mérése
                    double assemblyTime = measureExecutionTime(() -> {
                        assemblyMeasure.value(textCollection);
                    });
                    
                    // InfoBasedAssembly mérése
                    double infoTime = measureExecutionTime(() -> {
                        infoBasedAssembly.value(textCollection);
                    });
                    
                    assemblyMeasureResults.add(new Point2D.Double(length, assemblyTime/1000.0));
                    infoBasedAssemblyResults.add(new Point2D.Double(length, infoTime/1000.0));
                    
                    // Kis szünet, hogy a GUI frissüljön
                    Thread.sleep(10);
                }
                return null;
            }
            
            @Override
            protected void process(List<Integer> chunks) {
                if (!chunks.isEmpty()) {
                    int currentLength = chunks.get(chunks.size() - 1);
                    progressBar.setValue(currentLength);
                    progressBar.setString(String.format("Testing length %d/%d", currentLength, MAX_LENGTH));
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
     * Szöveghalmaz generálása fix mérettel (COLLECTION_SIZE) és adott elem hosszúsággal.
     * A halmaz (COLLECTION_SIZE/2)-féle egyedi elemet tartalmaz exponenciális eloszlással.
     */
    private Collection<String> generateTextCollection(int stringLength) {
        int uniqueElements = Math.max(1, COLLECTION_SIZE / 2);
        List<String> uniqueStrings = new ArrayList<>();
        
        // Egyedi szövegek generálása adott hosszúsággal
        for (int i = 0; i < uniqueElements; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < stringLength; j++) {
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
        
        // Szöveghalmaz összeállítása súlyok szerint fix mérettel
        List<String> collection = new ArrayList<>();
        for (int i = 0; i < COLLECTION_SIZE; i++) {
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

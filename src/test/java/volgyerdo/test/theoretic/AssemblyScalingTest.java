package volgyerdo.test.theoretic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.awt.geom.Point2D;
import java.awt.*;
import javax.swing.*;

import volgyerdo.test.ui.DataSeries;
import volgyerdo.test.ui.PlotPanel2D;
import volgyerdo.value.logic.method.assembly.AssemblyMeasure;
import volgyerdo.value.logic.method.assembly.GeneralAssembly;

public class AssemblyScalingTest {
    
// ======= Paraméterezés =======
    private static final int N_MIN = 200;
    private static final int N_MAX = 10000;
    private static final int N_STEPS = 30;       // ennyi N-értéket veszünk (log-skálán is jó lehet, de itt lineáris)
    private static final int FIXED_ALPHABET = 16; // fix ABC méret az 1. és 3. világhoz
    private static final long SEED = 42L;

    // Eredmények – ezeket add át a grafikus felületnek
    public final List<Point2D.Double> pointsA_uniform = new ArrayList<>();
    public final List<Point2D.Double> pointsAG_uniform = new ArrayList<>();

    public final List<Point2D.Double> pointsA_growing = new ArrayList<>();
    public final List<Point2D.Double> pointsAG_growing = new ArrayList<>();

    public final List<Point2D.Double> pointsA_zipf = new ArrayList<>();
    public final List<Point2D.Double> pointsAG_zipf = new ArrayList<>();

    // Mérők
    private final AssemblyMeasure assembly = new AssemblyMeasure();
    private final GeneralAssembly generalAssembly = new GeneralAssembly();

    // Random
    private final Random rng = new Random(SEED);

    public static void main(String[] args) {
        System.out.println("Starting Assembly Scaling Test...");
        SwingUtilities.invokeLater(() -> {
            System.out.println("In Swing thread...");
            try {
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            AssemblyScalingTest test = new AssemblyScalingTest();
            test.createAndShowGUI();
        });
    }
    
    private void createAndShowGUI() {
        JFrame frame = new JFrame("Assembly Scaling Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(Color.WHITE);
        
        // Progress bar
        JProgressBar progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setString("Running scaling tests...");
        progressBar.setIndeterminate(true);
        progressBar.setBackground(Color.WHITE);
        frame.add(progressBar, BorderLayout.NORTH);
        
        // Tabs for different test results
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(Color.WHITE);
        
        // Create plot panels
        PlotPanel2D assemblyUniformPlot = new PlotPanel2D();
        assemblyUniformPlot.setBackground(Color.WHITE);
        assemblyUniformPlot.setLegendVisible(false);
        PlotPanel2D generalAssemblyUniformPlot = new PlotPanel2D();
        generalAssemblyUniformPlot.setBackground(Color.WHITE);
        generalAssemblyUniformPlot.setLegendVisible(false);
        PlotPanel2D assemblyGrowingPlot = new PlotPanel2D();
        assemblyGrowingPlot.setBackground(Color.WHITE);
        assemblyGrowingPlot.setLegendVisible(false);
        PlotPanel2D generalAssemblyGrowingPlot = new PlotPanel2D();
        generalAssemblyGrowingPlot.setBackground(Color.WHITE);
        generalAssemblyGrowingPlot.setLegendVisible(false);
        PlotPanel2D assemblyZipfPlot = new PlotPanel2D();
        assemblyZipfPlot.setBackground(Color.WHITE);
        assemblyZipfPlot.setLegendVisible(false);
        PlotPanel2D generalAssemblyZipfPlot = new PlotPanel2D();
        generalAssemblyZipfPlot.setBackground(Color.WHITE);
        generalAssemblyZipfPlot.setLegendVisible(false);
        
        // Create panels for side-by-side display
        JPanel uniformPanel = new JPanel(new GridLayout(1, 2));
        uniformPanel.setBackground(Color.WHITE);
        JPanel leftUniform = new JPanel(new BorderLayout());
        leftUniform.setBackground(Color.WHITE);
        leftUniform.add(assemblyUniformPlot, BorderLayout.CENTER);
        JPanel rightUniform = new JPanel(new BorderLayout());
        rightUniform.setBackground(Color.WHITE);
        rightUniform.add(generalAssemblyUniformPlot, BorderLayout.CENTER);
        uniformPanel.add(leftUniform);
        uniformPanel.add(rightUniform);
        
        JPanel growingPanel = new JPanel(new GridLayout(1, 2));
        growingPanel.setBackground(Color.WHITE);
        JPanel leftGrowing = new JPanel(new BorderLayout());
        leftGrowing.setBackground(Color.WHITE);
        leftGrowing.add(assemblyGrowingPlot, BorderLayout.CENTER);
        JPanel rightGrowing = new JPanel(new BorderLayout());
        rightGrowing.setBackground(Color.WHITE);
        rightGrowing.add(generalAssemblyGrowingPlot, BorderLayout.CENTER);
        growingPanel.add(leftGrowing);
        growingPanel.add(rightGrowing);
        
        JPanel zipfPanel = new JPanel(new GridLayout(1, 2));
        zipfPanel.setBackground(Color.WHITE);
        JPanel leftZipf = new JPanel(new BorderLayout());
        leftZipf.setBackground(Color.WHITE);
        leftZipf.add(assemblyZipfPlot, BorderLayout.CENTER);
        JPanel rightZipf = new JPanel(new BorderLayout());
        rightZipf.setBackground(Color.WHITE);
        rightZipf.add(generalAssemblyZipfPlot, BorderLayout.CENTER);
        zipfPanel.add(leftZipf);
        zipfPanel.add(rightZipf);
        
        tabbedPane.addTab("Uniform", uniformPanel);
        tabbedPane.addTab("Growing Alphabet", growingPanel);
        tabbedPane.addTab("Zipf Distribution", zipfPanel);
        
        frame.add(tabbedPane, BorderLayout.CENTER);
        
        frame.setSize(1000, 500);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        
        // Run tests in background thread
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                runTests();
                return null;
            }
            
            @Override
            protected void done() {
                try {
                    // Create data series for plots
                    List<DataSeries> assemblyUniformSeries = new ArrayList<>();
                    List<DataSeries> generalAssemblyUniformSeries = new ArrayList<>();
                    List<DataSeries> assemblyGrowingSeries = new ArrayList<>();
                    List<DataSeries> generalAssemblyGrowingSeries = new ArrayList<>();
                    List<DataSeries> assemblyZipfSeries = new ArrayList<>();
                    List<DataSeries> generalAssemblyZipfSeries = new ArrayList<>();
                    
                    // Uniform results
                    List<Point2D> assemblyUniformPoints = new ArrayList<>();
                    List<Point2D> generalAssemblyUniformPoints = new ArrayList<>();
                    for (Point2D.Double point : pointsA_uniform) {
                        assemblyUniformPoints.add(new Point2D.Double(point.x, point.y));
                    }
                    for (Point2D.Double point : pointsAG_uniform) {
                        generalAssemblyUniformPoints.add(new Point2D.Double(point.x, point.y));
                    }
                    assemblyUniformSeries.add(new DataSeries("Assembly vs N", assemblyUniformPoints, Color.BLACK, true, true, 3, 6));
                    generalAssemblyUniformSeries.add(new DataSeries("IBA vs N", generalAssemblyUniformPoints, Color.BLACK, true, true, 3, 6));
                    
                    // Growing results
                    List<Point2D> assemblyGrowingPoints = new ArrayList<>();
                    List<Point2D> generalAssemblyGrowingPoints = new ArrayList<>();
                    for (Point2D.Double point : pointsA_growing) {
                        assemblyGrowingPoints.add(new Point2D.Double(point.x, point.y));
                    }
                    for (Point2D.Double point : pointsAG_growing) {
                        generalAssemblyGrowingPoints.add(new Point2D.Double(point.x, point.y));
                    }
                    assemblyGrowingSeries.add(new DataSeries("Assembly vs N", assemblyGrowingPoints, Color.BLACK, true, true, 3, 6));
                    generalAssemblyGrowingSeries.add(new DataSeries("IBA vs N", generalAssemblyGrowingPoints, Color.BLACK, true, true, 3, 6));
                    
                    // Zipf results
                    List<Point2D> assemblyZipfPoints = new ArrayList<>();
                    List<Point2D> generalAssemblyZipfPoints = new ArrayList<>();
                    for (Point2D.Double point : pointsA_zipf) {
                        assemblyZipfPoints.add(new Point2D.Double(point.x, point.y));
                    }
                    for (Point2D.Double point : pointsAG_zipf) {
                        generalAssemblyZipfPoints.add(new Point2D.Double(point.x, point.y));
                    }
                    assemblyZipfSeries.add(new DataSeries("Assembly vs N", assemblyZipfPoints, Color.BLACK, true, true, 3, 6));
                    generalAssemblyZipfSeries.add(new DataSeries("IBA vs N", generalAssemblyZipfPoints, Color.BLACK, true, true, 3, 6));
                    
                    // Set data to plots
                    assemblyUniformPlot.setDataSeries(assemblyUniformSeries);
                    assemblyUniformPlot.setAxisLabels("Scale Factor", "Assembly (A)");
                    assemblyUniformPlot.setPlotTitle("Assembly - Uniform Distribution");
                    
                    generalAssemblyUniformPlot.setDataSeries(generalAssemblyUniformSeries);
                    generalAssemblyUniformPlot.setAxisLabels("Scale Factor", "Information-based Assembly (IBA)");
                    generalAssemblyUniformPlot.setPlotTitle("IBA - Uniform Distribution");
                    
                    assemblyGrowingPlot.setDataSeries(assemblyGrowingSeries);
                    assemblyGrowingPlot.setAxisLabels("Scale Factor", "Assembly (A)");
                    assemblyGrowingPlot.setPlotTitle("Assembly - Growing Alphabet");
                    
                    generalAssemblyGrowingPlot.setDataSeries(generalAssemblyGrowingSeries);
                    generalAssemblyGrowingPlot.setAxisLabels("Scale Factor", "Information-based Assembly (IBA)");
                    generalAssemblyGrowingPlot.setPlotTitle("IBA - Growing Alphabet");
                    
                    assemblyZipfPlot.setDataSeries(assemblyZipfSeries);
                    assemblyZipfPlot.setAxisLabels("Scale Factor", "Assembly (A)");
                    assemblyZipfPlot.setPlotTitle("Assembly - Zipf Distribution");
                    
                    generalAssemblyZipfPlot.setDataSeries(generalAssemblyZipfSeries);
                    generalAssemblyZipfPlot.setAxisLabels("Scale Factor", "Information-based Assembly (IBA)");
                    generalAssemblyZipfPlot.setPlotTitle("IBA - Zipf Distribution");
                    
                    // Hide progress bar
                    progressBar.setVisible(false);
                    
                    System.out.println("Scaling tests completed and displayed.");
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "Error displaying results: " + e.getMessage(), 
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
    }

    public void runTests() {
        System.out.println("=== ASSEMBLY SCALING TEST KEZDETE ===");
        System.out.println("Paraméterek:");
        System.out.println("  N_MIN: " + N_MIN);
        System.out.println("  N_MAX: " + N_MAX);
        System.out.println("  N_STEPS: " + N_STEPS);
        System.out.println("  FIXED_ALPHABET: " + FIXED_ALPHABET);
        System.out.println("  SEED: " + SEED);
        System.out.println();
        
        // N értékek előállítása
        int[] Ns = IntStream.range(0, N_STEPS)
                .map(i -> N_MIN + i * (N_MAX - N_MIN) / (N_STEPS - 1))
                .toArray();

        System.out.println("Tesztelendő N értékek: " + Arrays.toString(Ns));
        System.out.println();

        int stepCount = 0;
        for (int N : Ns) {
            stepCount++;
            System.out.printf("=== Lépés %d/%d: N = %d ===\n", stepCount, N_STEPS, N);
            
            // 1) Uniform, fix ABC
            System.out.println("1. Egyenletes eloszlás (fix ábécé: " + FIXED_ALPHABET + " elem)");
            List<String> seqUniform = generateUniformSequence(N, FIXED_ALPHABET);
            double assemblyUniform = assembly.value(seqUniform);
            double generalAssemblyUniform = generalAssembly.value(seqUniform);
            addPoint(pointsA_uniform, N, assemblyUniform);
            addPoint(pointsAG_uniform, N, generalAssemblyUniform);
            System.out.printf("   Assembly (A): %.6f\n", assemblyUniform);
            System.out.printf("   IBA: %.6f\n", generalAssemblyUniform);

            // 2) Growing ABC: K ~ N^(1/3), de minimum 4
            int growingK = Math.max(4, (int)Math.floor(Math.cbrt(N)));
            System.out.println("2. Növekvő ábécé (K = " + growingK + ")");
            List<String> seqGrowing = generateUniformSequence(N, growingK);
            double assemblyGrowing = assembly.value(seqGrowing);
            double generalAssemblyGrowing = generalAssembly.value(seqGrowing);
            addPoint(pointsA_growing, N, assemblyGrowing);
            addPoint(pointsAG_growing, N, generalAssemblyGrowing);
            System.out.printf("   Assembly (A): %.6f\n", assemblyGrowing);
            System.out.printf("   IBA: %.6f\n", generalAssemblyGrowing);

            // 3) Zipf, fix ABC, s (exponens) választható
            System.out.println("3. Zipf eloszlás (fix ábécé: " + FIXED_ALPHABET + " elem, s=1.0)");
            List<String> seqZipf = generateZipfSequence(N, FIXED_ALPHABET, 1.0 /*s*/);
            double assemblyZipf = assembly.value(seqZipf);
            double generalAssemblyZipf = generalAssembly.value(seqZipf);
            addPoint(pointsA_zipf, N, assemblyZipf);
            addPoint(pointsAG_zipf, N, generalAssemblyZipf);
            System.out.printf("   Assembly (A): %.6f\n", assemblyZipf);
            System.out.printf("   IBA: %.6f\n", generalAssemblyZipf);
            System.out.println();
        }
        
        System.out.println("=== TESZT BEFEJEZVE ===");
        logFinalResults();
    }

    private static void addPoint(List<Point2D.Double> list, double x, double y) {
        list.add(new Point2D.Double(x, y));
    }

    private void logFinalResults() {
        System.out.println("=== VÉGSŐ EREDMÉNYEK ÖSSZEFOGLALÓJA ===");
        System.out.println();
        
        // Egyenletes eloszlás eredményei
        System.out.println("1. EGYENLETES ELOSZLÁS (fix ábécé: " + FIXED_ALPHABET + " elem):");
        System.out.println("   N\t\tAssembly (A)\t\tIBA");
        for (int i = 0; i < pointsA_uniform.size(); i++) {
            Point2D.Double aPoint = pointsA_uniform.get(i);
            Point2D.Double agPoint = pointsAG_uniform.get(i);
            System.out.printf("   %.0f\t\t%.6f\t\t%.6f\n", aPoint.x, aPoint.y, agPoint.y);
        }
        System.out.println();
        
        // Növekvő ábécé eredményei
        System.out.println("2. NÖVEKVŐ ÁBÉCÉ:");
        System.out.println("   N\t\tK\t\tAssembly (A)\t\tIBA");
        for (int i = 0; i < pointsA_growing.size(); i++) {
            Point2D.Double aPoint = pointsA_growing.get(i);
            Point2D.Double agPoint = pointsAG_growing.get(i);
            int N = (int)aPoint.x;
            int K = Math.max(4, (int)Math.floor(Math.cbrt(N)));
            System.out.printf("   %.0f\t\t%d\t\t%.6f\t\t%.6f\n", aPoint.x, K, aPoint.y, agPoint.y);
        }
        System.out.println();
        
        // Zipf eloszlás eredményei
        System.out.println("3. ZIPF ELOSZLÁS (fix ábécé: " + FIXED_ALPHABET + " elem, s=1.0):");
        System.out.println("   N\t\tAssembly (A)\t\tIBA");
        for (int i = 0; i < pointsA_zipf.size(); i++) {
            Point2D.Double aPoint = pointsA_zipf.get(i);
            Point2D.Double agPoint = pointsAG_zipf.get(i);
            System.out.printf("   %.0f\t\t%.6f\t\t%.6f\n", aPoint.x, aPoint.y, agPoint.y);
        }
        System.out.println();
        
        // Trend elemzés
        analyzeTrends();
    }
    
    private void analyzeTrends() {
        System.out.println("=== TREND ELEMZÉS ===");
        System.out.println();
        
        // Az első és utolsó pontok összehasonlítása
        if (!pointsA_uniform.isEmpty()) {
            Point2D.Double firstUniform = pointsA_uniform.get(0);
            Point2D.Double lastUniform = pointsA_uniform.get(pointsA_uniform.size() - 1);
            double uniformGrowthA = (lastUniform.y - firstUniform.y) / (lastUniform.x - firstUniform.x);
            
            Point2D.Double firstUniformIBA = pointsAG_uniform.get(0);
            Point2D.Double lastUniformIBA = pointsAG_uniform.get(pointsAG_uniform.size() - 1);
            double uniformGrowthIBA = (lastUniformIBA.y - firstUniformIBA.y) / (lastUniformIBA.x - firstUniformIBA.x);
            
            System.out.printf("Egyenletes eloszlás trend:\n");
            System.out.printf("  Assembly: %.8f / N\n", uniformGrowthA);
            System.out.printf("  IBA: %.8f / N\n", uniformGrowthIBA);
            System.out.println();
        }
        
        if (!pointsA_growing.isEmpty()) {
            Point2D.Double firstGrowing = pointsA_growing.get(0);
            Point2D.Double lastGrowing = pointsA_growing.get(pointsA_growing.size() - 1);
            double growingGrowthA = (lastGrowing.y - firstGrowing.y) / (lastGrowing.x - firstGrowing.x);
            
            Point2D.Double firstGrowingIBA = pointsAG_growing.get(0);
            Point2D.Double lastGrowingIBA = pointsAG_growing.get(pointsAG_growing.size() - 1);
            double growingGrowthIBA = (lastGrowingIBA.y - firstGrowingIBA.y) / (lastGrowingIBA.x - firstGrowingIBA.x);
            
            System.out.printf("Növekvő ábécé trend:\n");
            System.out.printf("  Assembly: %.8f / N\n", growingGrowthA);
            System.out.printf("  IBA: %.8f / N\n", growingGrowthIBA);
            System.out.println();
        }
        
        if (!pointsA_zipf.isEmpty()) {
            Point2D.Double firstZipf = pointsA_zipf.get(0);
            Point2D.Double lastZipf = pointsA_zipf.get(pointsA_zipf.size() - 1);
            double zipfGrowthA = (lastZipf.y - firstZipf.y) / (lastZipf.x - firstZipf.x);
            
            Point2D.Double firstZipfIBA = pointsAG_zipf.get(0);
            Point2D.Double lastZipfIBA = pointsAG_zipf.get(pointsAG_zipf.size() - 1);
            double zipfGrowthIBA = (lastZipfIBA.y - firstZipfIBA.y) / (lastZipfIBA.x - firstZipfIBA.x);
            
            System.out.printf("Zipf eloszlás trend:\n");
            System.out.printf("  Assembly: %.8f / N\n", zipfGrowthA);
            System.out.printf("  IBA: %.8f / N\n", zipfGrowthIBA);
            System.out.println();
        }
        
        System.out.println("=== ELEMZÉS BEFEJEZVE ===");
    }

    // ======= Sorozatgenerátorok =======

    // 1) Egyenletes eloszlás K elemű ábécéről
    private List<String> generateUniformSequence(int N, int K) {
        // ABC: s0, s1, ..., s(K-1)
        List<String> alphabet = IntStream.range(0, K)
                .mapToObj(i -> "s" + i)
                .collect(Collectors.toList());

        List<String> seq = new ArrayList<>(N);
        for (int i = 0; i < N; i++) {
            String sym = alphabet.get(rng.nextInt(K));
            seq.add(sym);
        }
        return seq;
    }

    // 2) Zipf-eloszlású mintavételezés K elemű ábécéről, paraméter s
    private List<String> generateZipfSequence(int N, int K, double s) {
        // ABC: s0 leggyakoribb, s(K-1) legritkább
        List<String> alphabet = IntStream.range(0, K)
                .mapToObj(i -> "s" + i)
                .collect(Collectors.toList());

        // Zipf valószínűségek: p(k) ~ 1 / k^s (k = 1..K), normalizálva
        double[] weights = new double[K];
        double Z = 0.0;
        for (int i = 0; i < K; i++) {
            weights[i] = 1.0 / Math.pow(i + 1, s);
            Z += weights[i];
        }
        for (int i = 0; i < K; i++) weights[i] /= Z;

        // kumulatív tömegfüggvény
        double[] cdf = new double[K];
        double sum = 0.0;
        for (int i = 0; i < K; i++) {
            sum += weights[i];
            cdf[i] = sum;
        }
        cdf[K - 1] = 1.0; // numerikus biztonság

        List<String> seq = new ArrayList<>(N);
        for (int i = 0; i < N; i++) {
            double u = rng.nextDouble();
            int idx = Arrays.binarySearch(cdf, u);
            if (idx < 0) idx = -idx - 1;
            if (idx >= K) idx = K - 1;
            seq.add(alphabet.get(idx));
        }
        return seq;
    }
}


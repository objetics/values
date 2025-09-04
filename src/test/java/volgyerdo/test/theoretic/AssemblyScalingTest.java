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
        
        // Progress bar
        JProgressBar progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setString("Running scaling tests...");
        progressBar.setIndeterminate(true);
        frame.add(progressBar, BorderLayout.NORTH);
        
        // Tabs for different test results
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Create plot panels
        PlotPanel2D assemblyUniformPlot = new PlotPanel2D();
        PlotPanel2D generalAssemblyUniformPlot = new PlotPanel2D();
        PlotPanel2D assemblyGrowingPlot = new PlotPanel2D();
        PlotPanel2D generalAssemblyGrowingPlot = new PlotPanel2D();
        PlotPanel2D assemblyZipfPlot = new PlotPanel2D();
        PlotPanel2D generalAssemblyZipfPlot = new PlotPanel2D();
        
        // Create panels for side-by-side display
        JPanel uniformPanel = new JPanel(new GridLayout(1, 2));
        uniformPanel.setBorder(BorderFactory.createTitledBorder("Uniform Distribution Test Results"));
        JPanel leftUniform = new JPanel(new BorderLayout());
        leftUniform.setBorder(BorderFactory.createTitledBorder("Assembly"));
        leftUniform.add(assemblyUniformPlot, BorderLayout.CENTER);
        JPanel rightUniform = new JPanel(new BorderLayout());
        rightUniform.setBorder(BorderFactory.createTitledBorder("General Assembly"));
        rightUniform.add(generalAssemblyUniformPlot, BorderLayout.CENTER);
        uniformPanel.add(leftUniform);
        uniformPanel.add(rightUniform);
        
        JPanel growingPanel = new JPanel(new GridLayout(1, 2));
        growingPanel.setBorder(BorderFactory.createTitledBorder("Growing Alphabet Test Results"));
        JPanel leftGrowing = new JPanel(new BorderLayout());
        leftGrowing.setBorder(BorderFactory.createTitledBorder("Assembly"));
        leftGrowing.add(assemblyGrowingPlot, BorderLayout.CENTER);
        JPanel rightGrowing = new JPanel(new BorderLayout());
        rightGrowing.setBorder(BorderFactory.createTitledBorder("General Assembly"));
        rightGrowing.add(generalAssemblyGrowingPlot, BorderLayout.CENTER);
        growingPanel.add(leftGrowing);
        growingPanel.add(rightGrowing);
        
        JPanel zipfPanel = new JPanel(new GridLayout(1, 2));
        zipfPanel.setBorder(BorderFactory.createTitledBorder("Zipf Distribution Test Results"));
        JPanel leftZipf = new JPanel(new BorderLayout());
        leftZipf.setBorder(BorderFactory.createTitledBorder("Assembly"));
        leftZipf.add(assemblyZipfPlot, BorderLayout.CENTER);
        JPanel rightZipf = new JPanel(new BorderLayout());
        rightZipf.setBorder(BorderFactory.createTitledBorder("General Assembly"));
        rightZipf.add(generalAssemblyZipfPlot, BorderLayout.CENTER);
        zipfPanel.add(leftZipf);
        zipfPanel.add(rightZipf);
        
        tabbedPane.addTab("Uniform", uniformPanel);
        tabbedPane.addTab("Growing Alphabet", growingPanel);
        tabbedPane.addTab("Zipf Distribution", zipfPanel);
        
        frame.add(tabbedPane, BorderLayout.CENTER);
        
        frame.setSize(1200, 800);
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
                    assemblyUniformSeries.add(new DataSeries("Assembly vs N", assemblyUniformPoints, Color.BLUE, true, true));
                    generalAssemblyUniformSeries.add(new DataSeries("General Assembly vs N", generalAssemblyUniformPoints, Color.RED, true, true));
                    
                    // Growing results
                    List<Point2D> assemblyGrowingPoints = new ArrayList<>();
                    List<Point2D> generalAssemblyGrowingPoints = new ArrayList<>();
                    for (Point2D.Double point : pointsA_growing) {
                        assemblyGrowingPoints.add(new Point2D.Double(point.x, point.y));
                    }
                    for (Point2D.Double point : pointsAG_growing) {
                        generalAssemblyGrowingPoints.add(new Point2D.Double(point.x, point.y));
                    }
                    assemblyGrowingSeries.add(new DataSeries("Assembly vs N", assemblyGrowingPoints, Color.BLUE, true, true));
                    generalAssemblyGrowingSeries.add(new DataSeries("General Assembly vs N", generalAssemblyGrowingPoints, Color.RED, true, true));
                    
                    // Zipf results
                    List<Point2D> assemblyZipfPoints = new ArrayList<>();
                    List<Point2D> generalAssemblyZipfPoints = new ArrayList<>();
                    for (Point2D.Double point : pointsA_zipf) {
                        assemblyZipfPoints.add(new Point2D.Double(point.x, point.y));
                    }
                    for (Point2D.Double point : pointsAG_zipf) {
                        generalAssemblyZipfPoints.add(new Point2D.Double(point.x, point.y));
                    }
                    assemblyZipfSeries.add(new DataSeries("Assembly vs N", assemblyZipfPoints, Color.BLUE, true, true));
                    generalAssemblyZipfSeries.add(new DataSeries("General Assembly vs N", generalAssemblyZipfPoints, Color.RED, true, true));
                    
                    // Set data to plots
                    assemblyUniformPlot.setDataSeries(assemblyUniformSeries);
                    generalAssemblyUniformPlot.setDataSeries(generalAssemblyUniformSeries);
                    assemblyGrowingPlot.setDataSeries(assemblyGrowingSeries);
                    generalAssemblyGrowingPlot.setDataSeries(generalAssemblyGrowingSeries);
                    assemblyZipfPlot.setDataSeries(assemblyZipfSeries);
                    generalAssemblyZipfPlot.setDataSeries(generalAssemblyZipfSeries);
                    
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
        // N értékek előállítása
        int[] Ns = IntStream.range(0, N_STEPS)
                .map(i -> N_MIN + i * (N_MAX - N_MIN) / (N_STEPS - 1))
                .toArray();

        for (int N : Ns) {
            // 1) Uniform, fix ABC
            List<String> seqUniform = generateUniformSequence(N, FIXED_ALPHABET);
            addPoint(pointsA_uniform, N, assembly.value(seqUniform));
            addPoint(pointsAG_uniform, N, generalAssembly.value(seqUniform));

            // 2) Growing ABC: K ~ N^(1/3), de minimum 4
            int growingK = Math.max(4, (int)Math.floor(Math.cbrt(N)));
            List<String> seqGrowing = generateUniformSequence(N, growingK);
            addPoint(pointsA_growing, N, assembly.value(seqGrowing));
            addPoint(pointsAG_growing, N, generalAssembly.value(seqGrowing));

            // 3) Zipf, fix ABC, s (exponens) választható
            List<String> seqZipf = generateZipfSequence(N, FIXED_ALPHABET, 1.0 /*s*/);
            addPoint(pointsA_zipf, N, assembly.value(seqZipf));
            addPoint(pointsAG_zipf, N, generalAssembly.value(seqZipf));
        }
    }

    private static void addPoint(List<Point2D.Double> list, double x, double y) {
        list.add(new Point2D.Double(x, y));
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


package volgyerdo.test.theoretic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.awt.geom.Point2D;
import java.awt.*;
import javax.swing.*;

import volgyerdo.commons.diagram.DataSeries;
import volgyerdo.commons.diagram.PlotPanel2D;
import volgyerdo.value.logic.method.assembly.AssemblyMeasure;
import volgyerdo.value.logic.method.assembly.InfoBasedAssembly;

public class AssemblyInfoTheoreticTest {

    // ======= Kísérleti beállítások =======
    private static final int N = 20_000;        // mintaméret (mindkét kísérletnél)
    private static final long SEED = 987654321L;

    // 1) Bernoulli p sweep
    private static final int P_STEPS = 33;      // 0.01..0.99 között
    private static final double P_MIN = 0.01;
    private static final double P_MAX = 0.99;

    // 2) Dirichlet K=16, alpha sweep
    private static final int K = 16;
    private static final double[] ALPHAS = new double[]{0.1, 0.2, 0.3, 0.5, 0.8, 1.0, 1.5, 2.0, 3.0, 5.0, 8.0, 10.0};

    // ======= Eredmények – ezeket add a diagram komponensednek =======
    public final List<Point2D.Double> pointsA_binomial  = new ArrayList<>();
    public final List<Point2D.Double> pointsAG_binomial = new ArrayList<>();

    public final List<Point2D.Double> pointsA_dirichlet  = new ArrayList<>();
    public final List<Point2D.Double> pointsAG_dirichlet = new ArrayList<>();

    // Mérők
    private final AssemblyMeasure assembly = new AssemblyMeasure();
    private final InfoBasedAssembly infoBasedAssembly = new InfoBasedAssembly();

    private final Random rng = new Random(SEED);

    public static void main(String[] args) {
        System.out.println("Starting application...");
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
            
            AssemblyInfoTheoreticTest test = new AssemblyInfoTheoreticTest();
            test.createAndShowGUI();
        });
    }
    
    private void createAndShowGUI() {
        JFrame frame = new JFrame("Assembly Information Theoretic Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(Color.WHITE);
        
        // Progress bar
        JProgressBar progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setString("Running tests...");
        progressBar.setIndeterminate(true);
        progressBar.setBackground(Color.WHITE);
        frame.add(progressBar, BorderLayout.NORTH);
        
        // Tabs for different test results
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(Color.WHITE);
        
        // Create plot panels
        PlotPanel2D assemblyBinomialPlot = new PlotPanel2D();
        assemblyBinomialPlot.setBackground(Color.WHITE);
        assemblyBinomialPlot.setLegendVisible(false);
        PlotPanel2D infoBasedAssemblyBinomialPlot = new PlotPanel2D();
        infoBasedAssemblyBinomialPlot.setBackground(Color.WHITE);
        infoBasedAssemblyBinomialPlot.setLegendVisible(false);
        PlotPanel2D assemblyDirichletPlot = new PlotPanel2D();
        assemblyDirichletPlot.setBackground(Color.WHITE);
        assemblyDirichletPlot.setLegendVisible(false);
        PlotPanel2D infoBasedAssemblyDirichletPlot = new PlotPanel2D();
        infoBasedAssemblyDirichletPlot.setBackground(Color.WHITE);
        infoBasedAssemblyDirichletPlot.setLegendVisible(false);
        
        // Create panels for side-by-side display
        JPanel binomialPanel = new JPanel(new GridLayout(1, 2));
        binomialPanel.setBackground(Color.WHITE);
        JPanel leftBinomial = new JPanel(new BorderLayout());
        leftBinomial.setBackground(Color.WHITE);
        leftBinomial.add(assemblyBinomialPlot, BorderLayout.CENTER);
        JPanel rightBinomial = new JPanel(new BorderLayout());
        rightBinomial.setBackground(Color.WHITE);
        rightBinomial.add(infoBasedAssemblyBinomialPlot, BorderLayout.CENTER);
        binomialPanel.add(leftBinomial);
        binomialPanel.add(rightBinomial);
        
        JPanel dirichletPanel = new JPanel(new GridLayout(1, 2));
        dirichletPanel.setBackground(Color.WHITE);
        JPanel leftDirichlet = new JPanel(new BorderLayout());
        leftDirichlet.setBackground(Color.WHITE);
        leftDirichlet.add(assemblyDirichletPlot, BorderLayout.CENTER);
        JPanel rightDirichlet = new JPanel(new BorderLayout());
        rightDirichlet.setBackground(Color.WHITE);
        rightDirichlet.add(infoBasedAssemblyDirichletPlot, BorderLayout.CENTER);
        dirichletPanel.add(leftDirichlet);
        dirichletPanel.add(rightDirichlet);
        
        tabbedPane.addTab("Bernoulli Tests", binomialPanel);
        tabbedPane.addTab("Dirichlet Tests", dirichletPanel);
        
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
                    List<DataSeries> assemblyBinomialSeries = new ArrayList<>();
                    List<DataSeries> infoBasedAssemblyBinomialSeries = new ArrayList<>();
                    List<DataSeries> assemblyDirichletSeries = new ArrayList<>();
                    List<DataSeries> infoBasedAssemblyDirichletSeries = new ArrayList<>();
                    
                    // Bernoulli results
                    List<Point2D> assemblyBinomialPoints = new ArrayList<>();
                    List<Point2D> infoBasedAssemblyBinomialPoints = new ArrayList<>();

                    for (Point2D.Double point : pointsA_binomial) {
                        assemblyBinomialPoints.add(new Point2D.Double(point.x, point.y));
                    }
                    for (Point2D.Double point : pointsAG_binomial) {
                        infoBasedAssemblyBinomialPoints.add(new Point2D.Double(point.x, point.y));
                    }
                    
                    // Print infoBasedAssemblyBinomialPoints values
                    System.out.println("\ninfoBasedAssemblyBinomialPoints értékei:");
                    for (int i = 0; i < infoBasedAssemblyBinomialPoints.size(); i++) {
                        Point2D point = infoBasedAssemblyBinomialPoints.get(i);
                        System.out.printf("[%d] H=%.6f, AG=%.6f%n", i, point.getX(), point.getY());
                    }
                    assemblyBinomialSeries.add(new DataSeries("Assembly vs Entropy", assemblyBinomialPoints, Color.BLACK, true, false, 3, 6));
                    infoBasedAssemblyBinomialSeries.add(new DataSeries("IBA vs Entropy", infoBasedAssemblyBinomialPoints, Color.BLACK, true, false, 3, 6));
                    
                    // Dirichlet results
                    List<Point2D> assemblyDirichletPoints = new ArrayList<>();
                    List<Point2D> infoBasedAssemblyDirichletPoints = new ArrayList<>();
                    for (Point2D.Double point : pointsA_dirichlet) {
                        assemblyDirichletPoints.add(new Point2D.Double(point.x, point.y));
                    }
                    for (Point2D.Double point : pointsAG_dirichlet) {
                        infoBasedAssemblyDirichletPoints.add(new Point2D.Double(point.x, point.y));
                    }
                    assemblyDirichletSeries.add(new DataSeries("Assembly vs Entropy", assemblyDirichletPoints, Color.BLACK, true, true, 3, 6));
                    infoBasedAssemblyDirichletSeries.add(new DataSeries("IBA vs Entropy", infoBasedAssemblyDirichletPoints, Color.BLACK, true, true, 3, 6));
                    
                    // Set data to plots
                    assemblyBinomialPlot.setDataSeries(assemblyBinomialSeries);
                    assemblyBinomialPlot.setAxisLabels("Entropy (H)", "Assembly (A)");
                    assemblyBinomialPlot.setPlotTitle("Assembly - Bernoulli Distribution");
                    
                    infoBasedAssemblyBinomialPlot.setDataSeries(infoBasedAssemblyBinomialSeries);
                    infoBasedAssemblyBinomialPlot.setAxisLabels("Entropy (H)", "Information-based Assembly (IBA)");
                    infoBasedAssemblyBinomialPlot.setPlotTitle("IBA - Bernoulli Distribution");
                    
                    assemblyDirichletPlot.setDataSeries(assemblyDirichletSeries);
                    assemblyDirichletPlot.setAxisLabels("Entropy (H)", "Assembly (A)");
                    assemblyDirichletPlot.setPlotTitle("Assembly - Dirichlet Distribution");
                    
                    infoBasedAssemblyDirichletPlot.setDataSeries(infoBasedAssemblyDirichletSeries);
                    infoBasedAssemblyDirichletPlot.setAxisLabels("Entropy (H)", "Information-based Assembly (IBA)");
                    infoBasedAssemblyDirichletPlot.setPlotTitle("IBA - Dirichlet Distribution");
                    
                    // Hide progress bar
                    progressBar.setVisible(false);
                    
                    // Print correlation results
                    System.out.printf("Bernoulli: corr(H, A)=%.4f, corr(H, AG)=%.4f%n",
                            pearsonX(pointsA_binomial), pearsonX(pointsAG_binomial));
                    System.out.printf("Dirichlet: corr(H, A)=%.4f, corr(H, AG)=%.4f%n",
                            pearsonX(pointsA_dirichlet), pearsonX(pointsAG_dirichlet));
                    
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
        System.out.println("Starting Bernoulli tests...");
        // 1) Bernoulli p sweep: X = H2(p), Y = A vagy AG
        for (int i = 0; i < P_STEPS; i++) {
            double p = P_MIN + (P_MAX - P_MIN) * i / (P_STEPS - 1);
            List<String> seq = generateBernoulli(N, p);
            double H = entropyFromCounts(counts(seq)); // empirikus entrópia (bit/szimbólum)

            double a  = assembly.value(seq);

            double ag = infoBasedAssembly.value(seq);

            System.out.println("Bernoulli: H=" + H + " - A=" + a + " - AG=" + ag);

            pointsA_binomial.add(new Point2D.Double(H, a));
            pointsAG_binomial.add(new Point2D.Double(H, ag));
        }
        System.out.println("Bernoulli tests completed.");

        System.out.println("Starting Dirichlet tests...");
        // 2) Dirichlet alpha sweep K=16: X = empirikus H, Y = A vagy AG
        for (double alpha : ALPHAS) {
            double[] probs = sampleDirichlet(equalAlpha(alpha, K), rng);
            List<String> seq = sampleDiscrete(N, makeAlphabet(K), probs, rng);

            System.out.println("Dirichlet");
            for(String s : seq) {
                System.out.print(s + " ");
            }
            System.out.println();
            System.out.println();

            double H = entropyFromCounts(counts(seq));

            double a  = assembly.value(seq);
            double ag = infoBasedAssembly.value(seq);

            System.out.println("Dirichlet: H=" + H + " - A=" + a + " - AG=" + ag);

            pointsA_dirichlet.add(new Point2D.Double(H, a));
            pointsAG_dirichlet.add(new Point2D.Double(H, ag));
            
            System.out.printf("alpha=%.2f, H=%.4f, A=%.4f, AG=%.4f%n", alpha, H, a, ag);
        }
        System.out.println("Dirichlet tests completed.");
    }

    // ======= 1) Bernoulli generátor =======
    private List<String> generateBernoulli(int n, double p) {
        List<String> seq = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            boolean one = rng.nextDouble() < p;
            seq.add(one ? "1" : "0");
        }
        return seq;
    }

    // ======= 2) Dirichlet alapok =======
    private static double[] equalAlpha(double alpha, int k) {
        double[] a = new double[k];
        Arrays.fill(a, alpha);
        return a;
    }

    // Gamma-trükk Dirichlet mintavételhez
    private static double[] sampleDirichlet(double[] alpha, Random rng) {
        double[] g = new double[alpha.length];
        double sum = 0.0;
        for (int i = 0; i < alpha.length; i++) {
            g[i] = sampleGamma(alpha[i], 1.0, rng);
            sum += g[i];
        }
        for (int i = 0; i < g.length; i++) g[i] /= sum;
        return g;
    }

    // Egyszerű gamma mintavétel (Marsaglia & Tsang k-shape > 0)
    private static double sampleGamma(double shape, double scale, Random rng) {
        if (shape <= 0) throw new IllegalArgumentException("shape must be > 0");
        // shape < 1 kezelése: boostolás
        if (shape < 1.0) {
            double u = rng.nextDouble();
            return sampleGamma(shape + 1.0, scale, rng) * Math.pow(u, 1.0 / shape);
        }

        // Marsaglia–Tsang
        double d = shape - 1.0 / 3.0;
        double c = 1.0 / Math.sqrt(9.0 * d);
        while (true) {
            double x, v;
            do {
                x = rng.nextGaussian();
                v = 1.0 + c * x;
            } while (v <= 0);
            v = v * v * v;
            double u = rng.nextDouble();
            if (u < 1 - 0.0331 * x * x * x * x) return scale * d * v;
            if (Math.log(u) < 0.5 * x * x + d * (1 - v + Math.log(v))) return scale * d * v;
        }
    }

    private static List<String> makeAlphabet(int k) {
        return IntStream.range(0, k).mapToObj(i -> "s" + i).collect(Collectors.toList());
    }

    private static List<String> sampleDiscrete(int n, List<String> alphabet, double[] p, Random rng) {
        if (alphabet.size() != p.length) throw new IllegalArgumentException("alphabet and p size mismatch");
        double[] cdf = toCdf(p);
        List<String> seq = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            double u = rng.nextDouble();
            int idx = Arrays.binarySearch(cdf, u);
            if (idx < 0) idx = -idx - 1;
            if (idx >= cdf.length) idx = cdf.length - 1;
            seq.add(alphabet.get(idx));
        }
        return seq;
    }

    private static double[] toCdf(double[] p) {
        double[] cdf = new double[p.length];
        double s = 0.0;
        for (int i = 0; i < p.length; i++) {
            s += p[i];
            cdf[i] = s;
        }
        cdf[cdf.length - 1] = 1.0; // numerikus biztosíték
        return cdf;
    }

    // ======= Entropia (empirikus, bit/szimbólum) =======
    private static Map<String, Integer> counts(List<String> seq) {
        Map<String, Integer> map = new HashMap<>();
        for (String x : seq) map.merge(x, 1, Integer::sum);
        return map;
    }

    private static double entropyFromCounts(Map<String, Integer> cnt) {
        int n = cnt.values().stream().mapToInt(Integer::intValue).sum();
        double H = 0.0;
        for (int c : cnt.values()) {
            double p = c / (double) n;
            if (p > 0) H -= p * log2(p);
        }
        return H;
    }

    private static double log2(double x) {
        return Math.log(x) / Math.log(2.0);
    }

    // ======= Gyors Pearson-korreláció H vs. Y (az X=H a Point2D.x-ben van) =======
    private static double pearsonX(List<Point2D.Double> pts) {
        int n = pts.size();
        double sumX = 0, sumY = 0, sumX2 = 0, sumY2 = 0, sumXY = 0;
        for (Point2D.Double p : pts) {
            double x = p.getX(), y = p.getY();
            sumX += x; sumY += y;
            sumX2 += x * x; sumY2 += y * y;
            sumXY += x * y;
        }
        double cov = sumXY - (sumX * sumY) / n;
        double varX = sumX2 - (sumX * sumX) / n;
        double varY = sumY2 - (sumY * sumY) / n;
        return cov / Math.sqrt(varX * varY + 1e-16);
    }
}
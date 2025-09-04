package volgyerdo.test.theoretic;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.awt.*;
import javax.swing.*;

import volgyerdo.test.ui.DataSeries;
import volgyerdo.test.ui.PlotPanel2D;
import volgyerdo.value.logic.method.assembly.AssemblyMeasure;
import volgyerdo.value.logic.method.assembly.GeneralAssembly;



public class AssemblySizeInvarianceTest {

    // ===== Beállítások =====
    private static final int N_MIN   = 200;
    private static final int N_MAX   = 20000;
    private static final int N_STEPS = 25;       // ennyi N-pont
    private static final int REPLICATES = 10;    // ismétlések N-enként az átlagoláshoz
    private static final int K = 16;             // fix ABC-méret Uniform/Zipf
    private static final double ZIPF_S = 1.0;    // Zipf-exponens
    private static final long SEED = 424242L;

    // Fix keverék (pozitív kontroll, ahol N-nek nem kéne befolyásolnia)
    private static final int CORE_K = 16;        // core típusok száma
    private static final int RARE_M = 16;        // ritka típusok száma
    private static final double RHO  = 0.10;     // ritka össztömeg

    // ===== Kimenet: rajzolható pontlisták (N -> átlagolt mérő) =====
    public final List<Point2D.Double> pointsA_uniform  = new ArrayList<>();
    public final List<Point2D.Double> pointsAG_uniform = new ArrayList<>();

    public final List<Point2D.Double> pointsA_zipf  = new ArrayList<>();
    public final List<Point2D.Double> pointsAG_zipf = new ArrayList<>();

    public final List<Point2D.Double> pointsA_mix  = new ArrayList<>();
    public final List<Point2D.Double> pointsAG_mix = new ArrayList<>();

    // Mérők
    private final AssemblyMeasure assembly = new AssemblyMeasure();
    private final GeneralAssembly generalAssembly = new GeneralAssembly();

    // RNG
    private final Random rng = new Random(SEED);

    public static void main(String[] args) {
        System.out.println("Starting Assembly Size Invariance Test...");
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
            
            AssemblySizeInvarianceTest test = new AssemblySizeInvarianceTest();
            test.createAndShowGUI();
        });
    }
    
    private void createAndShowGUI() {
        JFrame frame = new JFrame("Assembly Size Invariance Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(Color.WHITE);
        
        // Progress bar
        JProgressBar progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setString("Running size invariance tests...");
        progressBar.setIndeterminate(true);
        progressBar.setBackground(Color.WHITE);
        frame.add(progressBar, BorderLayout.NORTH);
        
        // Tabs for different test results
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(Color.WHITE);
        
        // Create plot panels
        PlotPanel2D assemblyUniformPlot = new PlotPanel2D();
        assemblyUniformPlot.setBackground(Color.WHITE);
        PlotPanel2D generalAssemblyUniformPlot = new PlotPanel2D();
        generalAssemblyUniformPlot.setBackground(Color.WHITE);
        PlotPanel2D assemblyZipfPlot = new PlotPanel2D();
        assemblyZipfPlot.setBackground(Color.WHITE);
        PlotPanel2D generalAssemblyZipfPlot = new PlotPanel2D();
        generalAssemblyZipfPlot.setBackground(Color.WHITE);
        PlotPanel2D assemblyMixPlot = new PlotPanel2D();
        assemblyMixPlot.setBackground(Color.WHITE);
        PlotPanel2D generalAssemblyMixPlot = new PlotPanel2D();
        generalAssemblyMixPlot.setBackground(Color.WHITE);
        
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
        
        JPanel mixPanel = new JPanel(new GridLayout(1, 2));
        mixPanel.setBackground(Color.WHITE);
        JPanel leftMix = new JPanel(new BorderLayout());
        leftMix.setBackground(Color.WHITE);
        leftMix.add(assemblyMixPlot, BorderLayout.CENTER);
        JPanel rightMix = new JPanel(new BorderLayout());
        rightMix.setBackground(Color.WHITE);
        rightMix.add(generalAssemblyMixPlot, BorderLayout.CENTER);
        mixPanel.add(leftMix);
        mixPanel.add(rightMix);
        
        tabbedPane.addTab("Uniform", uniformPanel);
        tabbedPane.addTab("Zipf", zipfPanel);
        tabbedPane.addTab("Mixture", mixPanel);
        
        frame.add(tabbedPane, BorderLayout.CENTER);
        
        frame.setSize(1200, 800);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        
        // Run tests in background thread
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                runTest();
                return null;
            }
            
            @Override
            protected void done() {
                try {
                    // Create data series for plots
                    List<DataSeries> assemblyUniformSeries = new ArrayList<>();
                    List<DataSeries> generalAssemblyUniformSeries = new ArrayList<>();
                    List<DataSeries> assemblyZipfSeries = new ArrayList<>();
                    List<DataSeries> generalAssemblyZipfSeries = new ArrayList<>();
                    List<DataSeries> assemblyMixSeries = new ArrayList<>();
                    List<DataSeries> generalAssemblyMixSeries = new ArrayList<>();
                    
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
                    
                    // Mix results
                    List<Point2D> assemblyMixPoints = new ArrayList<>();
                    List<Point2D> generalAssemblyMixPoints = new ArrayList<>();
                    for (Point2D.Double point : pointsA_mix) {
                        assemblyMixPoints.add(new Point2D.Double(point.x, point.y));
                    }
                    for (Point2D.Double point : pointsAG_mix) {
                        generalAssemblyMixPoints.add(new Point2D.Double(point.x, point.y));
                    }
                    assemblyMixSeries.add(new DataSeries("Assembly vs N", assemblyMixPoints, Color.BLUE, true, true));
                    generalAssemblyMixSeries.add(new DataSeries("General Assembly vs N", generalAssemblyMixPoints, Color.RED, true, true));
                    
                    // Set data to plots
                    assemblyUniformPlot.setDataSeries(assemblyUniformSeries);
                    assemblyUniformPlot.setAxisLabels("Sample Size", "Assembly (A)");
                    
                    generalAssemblyUniformPlot.setDataSeries(generalAssemblyUniformSeries);
                    generalAssemblyUniformPlot.setAxisLabels("Sample Size", "General Assembly (AG)");
                    
                    assemblyZipfPlot.setDataSeries(assemblyZipfSeries);
                    assemblyZipfPlot.setAxisLabels("Sample Size", "Assembly (A)");
                    
                    generalAssemblyZipfPlot.setDataSeries(generalAssemblyZipfSeries);
                    generalAssemblyZipfPlot.setAxisLabels("Sample Size", "General Assembly (AG)");
                    
                    assemblyMixPlot.setDataSeries(assemblyMixSeries);
                    assemblyMixPlot.setAxisLabels("Sample Size", "Assembly (A)");
                    
                    generalAssemblyMixPlot.setDataSeries(generalAssemblyMixSeries);
                    generalAssemblyMixPlot.setAxisLabels("Sample Size", "General Assembly (AG)");
                    
                    // Hide progress bar
                    progressBar.setVisible(false);
                    
                    // —— Eredmények összefoglalása (invariancia metrikák) ——
                    System.out.println("\n== MÉRET-INVARIANCIA (minél közelebb 0-hoz, annál jobb) ==");

                    System.out.println("\n[UNIFORM K=16]");
                    reportInvariance("A",  pointsA_uniform);
                    reportInvariance("AG", pointsAG_uniform);

                    System.out.println("\n[ZIPF K=16, s=1.0]");
                    reportInvariance("A",  pointsA_zipf);
                    reportInvariance("AG", pointsAG_zipf);

                    System.out.println("\n[MIXTURE core="+CORE_K+", rare M="+RARE_M+", rho="+RHO+"]");
                    reportInvariance("A",  pointsA_mix);
                    reportInvariance("AG", pointsAG_mix);
                    
                    System.out.println("Size invariance tests completed and displayed.");
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "Error displaying results: " + e.getMessage(), 
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
    }

    public void runTest() {
        int[] Ns = IntStream.range(0, N_STEPS)
                .map(i -> N_MIN + i * (N_MAX - N_MIN) / (N_STEPS - 1))
                .toArray();

        for (int N : Ns) {
            // UNIFORM
            double aU  = 0.0, agU  = 0.0;
            // ZIPF
            double aZ  = 0.0, agZ  = 0.0;
            // MIX (core+rare keverék)
            double aM  = 0.0, agM  = 0.0;

            for (int r = 0; r < REPLICATES; r++) {
                // Uniform minta
                List<String> u = generateUniformSequence(N, K);
                aU  += assembly.value(u);
                agU += generalAssembly.value(u);

                // Zipf minta
                List<String> z = generateZipfSequence(N, K, ZIPF_S);
                aZ  += assembly.value(z);
                agZ += generalAssembly.value(z);

                // Keverék minta (fix paraméterek, N-független komplexitás elvárt)
                List<String> m = generateMixtureSequence(N, CORE_K, RARE_M, RHO);
                aM  += assembly.value(m);
                agM += generalAssembly.value(m);
            }
            aU  /= REPLICATES; agU /= REPLICATES;
            aZ  /= REPLICATES; agZ /= REPLICATES;
            aM  /= REPLICATES; agM /= REPLICATES;

            pointsA_uniform.add (new Point2D.Double(N, aU));
            pointsAG_uniform.add(new Point2D.Double(N, agU));

            pointsA_zipf.add (new Point2D.Double(N, aZ));
            pointsAG_zipf.add(new Point2D.Double(N, agZ));

            pointsA_mix.add (new Point2D.Double(N, aM));
            pointsAG_mix.add(new Point2D.Double(N, agM));
        }
    }

    // ===== Invariancia riport (Spearman, OLS-slope, drift, elaszticitás) =====
    private void reportInvariance(String label, List<Point2D.Double> pts) {
        InvarianceStats s = invarianceStats(pts);

        System.out.printf("%s: Spearman ρ(N,Y)=%.4f,  slope[Y~logN]=%.4g,  drift=(max-min)/mean=%.4f,  elastic[logY~logN]=%.4g%n",
                label, s.spearman, s.slopeY_logN, s.relDrift, s.elasticLog);
    }

    private static class InvarianceStats {
        double spearman;     // korreláció N és Y között rang-alapon
        double slopeY_logN;  // OLS meredekség Y ~ log(N)
        double relDrift;     // (max-min)/átlag
        double elasticLog;   // OLS meredekség log(Y) ~ log(N)
    }

    private InvarianceStats invarianceStats(List<Point2D.Double> pts) {
        InvarianceStats s = new InvarianceStats();
        int n = pts.size();
        if (n < 2) return s;

        double[] xN = new double[n];
        double[] y  = new double[n];
        for (int i = 0; i < n; i++) { xN[i] = pts.get(i).x; y[i] = pts.get(i).y; }

        // Spearman ρ (rang-korr)
        double[] rx = ranks(xN);
        double[] ry = ranks(y);
        s.spearman  = pearson(rx, ry);

        // OLS Y ~ log(N)
        double[] xlog = Arrays.stream(xN).map(Math::log).toArray();
        s.slopeY_logN = olsSlope(xlog, y);

        // Relatív drift: (max-min)/mean
        double ymin = Arrays.stream(y).min().orElse(0.0);
        double ymax = Arrays.stream(y).max().orElse(0.0);
        double yavg = Arrays.stream(y).average().orElse(0.0);
        s.relDrift = (yavg != 0.0) ? (ymax - ymin) / Math.abs(yavg) : 0.0;

        // Elaszticitás: OLS log(Y) ~ log(N)
        double eps = 1e-12;
        double[] ylog = Arrays.stream(y).map(v -> Math.log(Math.max(v, eps))).toArray();
        s.elasticLog = olsSlope(xlog, ylog);

        return s;
    }

    // ===== Sorozatgenerátorok =====

    // Uniform: K-féle szimbólum egyenletesen
    private List<String> generateUniformSequence(int N, int K) {
        List<String> alphabet = IntStream.range(0, K).mapToObj(i -> "s"+i).collect(Collectors.toList());
        List<String> seq = new ArrayList<>(N);
        for (int i = 0; i < N; i++) {
            seq.add(alphabet.get(rng.nextInt(K)));
        }
        return seq;
    }

    // Zipf: p(r) ~ 1/r^s
    private List<String> generateZipfSequence(int N, int K, double s) {
        List<String> alphabet = IntStream.range(0, K).mapToObj(i -> "s"+i).collect(Collectors.toList());
        double[] w = new double[K];
        double Z = 0.0;
        for (int i = 0; i < K; i++) { w[i] = 1.0 / Math.pow(i+1, s); Z += w[i]; }
        for (int i = 0; i < K; i++) w[i] /= Z;
        double[] cdf = toCdf(w);

        List<String> seq = new ArrayList<>(N);
        for (int i = 0; i < N; i++) seq.add(alphabet.get(sampleFromCdf(cdf)));
        return seq;
    }

    // Keverék: CORE_K egyenletes a (1-rho) tömegen, M ritka típus egyenletes a rho tömegen
    private List<String> generateMixtureSequence(int N, int coreK, int rareM, double rho) {
        List<String> alphabet = new ArrayList<>(coreK + rareM);
        for (int i = 0; i < coreK; i++)  alphabet.add("c"+i);
        for (int j = 0; j < rareM; j++)  alphabet.add("r"+j);

        double[] p = new double[alphabet.size()];
        Arrays.fill(p, 0.0);
        for (int i = 0; i < coreK; i++)  p[i] = (1.0 - rho) / coreK;
        for (int j = 0; j < rareM; j++)  p[coreK + j] = rho / rareM;

        double[] cdf = toCdf(p);

        List<String> seq = new ArrayList<>(N);
        for (int i = 0; i < N; i++) seq.add(alphabet.get(sampleFromCdf(cdf)));
        return seq;
    }

    private static int sampleFromCdf(double[] cdf) {
        double u = Math.random();
        int idx = Arrays.binarySearch(cdf, u);
        if (idx < 0) idx = -idx - 1;
        if (idx >= cdf.length) idx = cdf.length - 1;
        return idx;
    }

    private static double[] toCdf(double[] p) {
        double[] c = new double[p.length];
        double s = 0.0;
        for (int i = 0; i < p.length; i++) { s += p[i]; c[i] = s; }
        c[c.length - 1] = 1.0;
        return c;
    }

    // ===== Segédfüggvények: rang, korreláció, OLS =====

    // Egyszerű rangszámítás (átlagolt rangok tie esetén)
    private static double[] ranks(double[] v) {
        int n = v.length;
        Integer[] idx = new Integer[n];
        for (int i = 0; i < n; i++) idx[i] = i;
        Arrays.sort(idx, Comparator.comparingDouble(i -> v[i]));

        double[] r = new double[n];
        int i = 0;
        while (i < n) {
            int j = i;
            while (j+1 < n && v[idx[j+1]] == v[idx[i]]) j++;
            double rank = (i + j) / 2.0 + 1.0;
            for (int k = i; k <= j; k++) r[idx[k]] = rank;
            i = j + 1;
        }
        return r;
    }

    private static double pearson(double[] x, double[] y) {
        int n = x.length;
        double sx=0, sy=0, sxx=0, syy=0, sxy=0;
        for (int i = 0; i < n; i++) {
            sx += x[i]; sy += y[i];
            sxx += x[i]*x[i]; syy += y[i]*y[i];
            sxy += x[i]*y[i];
        }
        double cov = sxy - sx*sy/n;
        double vx  = sxx - sx*sx/n;
        double vy  = syy - sy*sy/n;
        double denom = Math.sqrt(Math.max(vx, 1e-18) * Math.max(vy, 1e-18));
        return cov / denom;
    }

    private static double olsSlope(double[] x, double[] y) {
        int n = x.length;
        double sx=0, sy=0, sxx=0, sxy=0;
        for (int i = 0; i < n; i++) {
            sx += x[i]; sy += y[i];
            sxx += x[i]*x[i];
            sxy += x[i]*y[i];
        }
        double num = sxy - sx*sy/n;
        double den = sxx - sx*sx/n;
        return num / Math.max(den, 1e-18);
    }
}

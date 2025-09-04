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

public class AssemblyWeightingContrastTest  {

    // ======= Kísérleti beállítások =======
    private static final int N = 10_000;       // mintaméret (állandó, hogy a súlyozás különbsége látszódjon)
    private static final int CORE_K = 16;      // gyakori (core) ábécé méret
    private static final long SEED = 12345L;   // reprod.

    // A) ritka tömeg sweep (egy ritka típus)
    private static final int RHO_STEPS = 25;   // 0..0.4 között
    private static final double RHO_MAX = 0.40;

    // B) ritka típusok száma sweep (fix ritka tömeg)
    private static final double RHO_FIX = 0.10;
    private static final int[] RARE_M_STEPS = new int[]{1,2,4,8,16,32,64,128,256};

    // ======= Eredmények – ezeket add a diagram komponensednek =======
    public final List<Point2D.Double> pointsA_rareMass = new ArrayList<>();
    public final List<Point2D.Double> pointsAG_rareMass = new ArrayList<>();

    public final List<Point2D.Double> pointsA_rareMultiplicity = new ArrayList<>();
    public final List<Point2D.Double> pointsAG_rareMultiplicity = new ArrayList<>();

    // Mérők
    private final AssemblyMeasure assembly = new AssemblyMeasure();
    private final GeneralAssembly generalAssembly = new GeneralAssembly();

    private final Random rng = new Random(SEED);

    public static void main(String[] args) {
        System.out.println("Starting Assembly Weighting Contrast Test...");
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
            
            AssemblyWeightingContrastTest test = new AssemblyWeightingContrastTest();
            test.createAndShowGUI();
        });
    }
    
    private void createAndShowGUI() {
        JFrame frame = new JFrame("Assembly Weighting Contrast Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        
        // Progress bar
        JProgressBar progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setString("Running weighting contrast tests...");
        progressBar.setIndeterminate(true);
        frame.add(progressBar, BorderLayout.NORTH);
        
        // Tabs for different test results
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Create plot panels
        PlotPanel2D assemblyRareMassPlot = new PlotPanel2D();
        PlotPanel2D generalAssemblyRareMassPlot = new PlotPanel2D();
        PlotPanel2D assemblyRareMultiplicityPlot = new PlotPanel2D();
        PlotPanel2D generalAssemblyRareMultiplicityPlot = new PlotPanel2D();
        
        // Create panels for side-by-side display
        JPanel rareMassPanel = new JPanel(new GridLayout(1, 2));
        rareMassPanel.setBorder(BorderFactory.createTitledBorder("Rare Mass Test Results"));
        JPanel leftRareMass = new JPanel(new BorderLayout());
        leftRareMass.setBorder(BorderFactory.createTitledBorder("Assembly"));
        leftRareMass.add(assemblyRareMassPlot, BorderLayout.CENTER);
        JPanel rightRareMass = new JPanel(new BorderLayout());
        rightRareMass.setBorder(BorderFactory.createTitledBorder("General Assembly"));
        rightRareMass.add(generalAssemblyRareMassPlot, BorderLayout.CENTER);
        rareMassPanel.add(leftRareMass);
        rareMassPanel.add(rightRareMass);
        
        JPanel rareMultiplicityPanel = new JPanel(new GridLayout(1, 2));
        rareMultiplicityPanel.setBorder(BorderFactory.createTitledBorder("Rare Multiplicity Test Results"));
        JPanel leftRareMultiplicity = new JPanel(new BorderLayout());
        leftRareMultiplicity.setBorder(BorderFactory.createTitledBorder("Assembly"));
        leftRareMultiplicity.add(assemblyRareMultiplicityPlot, BorderLayout.CENTER);
        JPanel rightRareMultiplicity = new JPanel(new BorderLayout());
        rightRareMultiplicity.setBorder(BorderFactory.createTitledBorder("General Assembly"));
        rightRareMultiplicity.add(generalAssemblyRareMultiplicityPlot, BorderLayout.CENTER);
        rareMultiplicityPanel.add(leftRareMultiplicity);
        rareMultiplicityPanel.add(rightRareMultiplicity);
        
        tabbedPane.addTab("Rare Mass", rareMassPanel);
        tabbedPane.addTab("Rare Multiplicity", rareMultiplicityPanel);
        
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
                    List<DataSeries> assemblyRareMassSeries = new ArrayList<>();
                    List<DataSeries> generalAssemblyRareMassSeries = new ArrayList<>();
                    List<DataSeries> assemblyRareMultiplicitySeries = new ArrayList<>();
                    List<DataSeries> generalAssemblyRareMultiplicitySeries = new ArrayList<>();
                    
                    // Rare Mass results
                    List<Point2D> assemblyRareMassPoints = new ArrayList<>();
                    List<Point2D> generalAssemblyRareMassPoints = new ArrayList<>();
                    for (Point2D.Double point : pointsA_rareMass) {
                        assemblyRareMassPoints.add(new Point2D.Double(point.x, point.y));
                    }
                    for (Point2D.Double point : pointsAG_rareMass) {
                        generalAssemblyRareMassPoints.add(new Point2D.Double(point.x, point.y));
                    }
                    assemblyRareMassSeries.add(new DataSeries("Assembly vs Rare Mass", assemblyRareMassPoints, Color.BLUE, true, true));
                    generalAssemblyRareMassSeries.add(new DataSeries("General Assembly vs Rare Mass", generalAssemblyRareMassPoints, Color.RED, true, true));
                    
                    // Rare Multiplicity results
                    List<Point2D> assemblyRareMultiplicityPoints = new ArrayList<>();
                    List<Point2D> generalAssemblyRareMultiplicityPoints = new ArrayList<>();
                    for (Point2D.Double point : pointsA_rareMultiplicity) {
                        assemblyRareMultiplicityPoints.add(new Point2D.Double(point.x, point.y));
                    }
                    for (Point2D.Double point : pointsAG_rareMultiplicity) {
                        generalAssemblyRareMultiplicityPoints.add(new Point2D.Double(point.x, point.y));
                    }
                    assemblyRareMultiplicitySeries.add(new DataSeries("Assembly vs Rare Types", assemblyRareMultiplicityPoints, Color.BLUE, true, true));
                    generalAssemblyRareMultiplicitySeries.add(new DataSeries("General Assembly vs Rare Types", generalAssemblyRareMultiplicityPoints, Color.RED, true, true));
                    
                    // Set data to plots
                    assemblyRareMassPlot.setDataSeries(assemblyRareMassSeries);
                    assemblyRareMassPlot.setAxisLabels("Rare Mass", "Assembly (A)");
                    
                    generalAssemblyRareMassPlot.setDataSeries(generalAssemblyRareMassSeries);
                    generalAssemblyRareMassPlot.setAxisLabels("Rare Mass", "General Assembly (AG)");
                    
                    assemblyRareMultiplicityPlot.setDataSeries(assemblyRareMultiplicitySeries);
                    assemblyRareMultiplicityPlot.setAxisLabels("Rare Types", "Assembly (A)");
                    
                    generalAssemblyRareMultiplicityPlot.setDataSeries(generalAssemblyRareMultiplicitySeries);
                    generalAssemblyRareMultiplicityPlot.setAxisLabels("Rare Types", "General Assembly (AG)");
                    
                    // Hide progress bar
                    progressBar.setVisible(false);
                    
                    System.out.println("Weighting contrast tests completed and displayed.");
                    
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
        // A) Egy ritka típus tömege (rho) nő 0..RHO_MAX
        for (int i = 0; i < RHO_STEPS; i++) {
            double rho = (RHO_MAX * i) / (RHO_STEPS - 1); // 0, ..., RHO_MAX
            List<String> seq = generateMixtureSequence(N, CORE_K, /*rareTypes*/1, rho);
            double a  = assembly.value(seq);
            double ag = generalAssembly.value(seq);
            pointsA_rareMass.add(new Point2D.Double(rho, a));
            pointsAG_rareMass.add(new Point2D.Double(rho, ag));
        }

        // B) Fix ritka tömeg (rho = RHO_FIX), de egyre több ritka típusra osztjuk (M nő)
        for (int m : RARE_M_STEPS) {
            List<String> seq = generateMixtureSequence(N, CORE_K, /*rareTypes*/m, RHO_FIX);
            double a  = assembly.value(seq);
            double ag = generalAssembly.value(seq);
            pointsA_rareMultiplicity.add(new Point2D.Double(m, a));
            pointsAG_rareMultiplicity.add(new Point2D.Double(m, ag));
        }
    }

    /**
     * Keverék-szekvencia generálása:
     * - CORE_K gyakori típus egyenletesen osztozik (1 - rho) tömegen
     * - M ritka típus egyenletesen osztozik rho tömegen
     * A "ritkaság" kétféleképp skálázható: rho (teljes ritka tömeg) és M (ritka típusok száma).
     */
    private List<String> generateMixtureSequence(int N, int coreK, int rareTypesM, double rho) {
        if (rho < 0.0 || rho >= 1.0) throw new IllegalArgumentException("rho in [0,1) expected");
        if (coreK <= 0) throw new IllegalArgumentException("coreK > 0 expected");
        if (rareTypesM <= 0) throw new IllegalArgumentException("rareTypesM > 0 expected");

        // Ábécé létrehozása: c0..c(coreK-1) és r0..r(rareTypesM-1)
        List<String> coreAlphabet = IntStream.range(0, coreK)
                .mapToObj(i -> "c" + i).collect(Collectors.toList());
        List<String> rareAlphabet = IntStream.range(0, rareTypesM)
                .mapToObj(i -> "r" + i).collect(Collectors.toList());

        // Valószínűségek
        double pCoreEach = (coreK > 0) ? (1.0 - rho) / coreK : 0.0;
        double pRareEach = (rareTypesM > 0) ? rho / rareTypesM : 0.0;

        int K = coreK + rareTypesM;
        String[] alphabet = new String[K];
        double[] probs    = new double[K];
        int idx = 0;
        for (String c : coreAlphabet) {
            alphabet[idx] = c;
            probs[idx] = pCoreEach;
            idx++;
        }
        for (String r : rareAlphabet) {
            alphabet[idx] = r;
            probs[idx] = pRareEach;
            idx++;
        }

        // Numerikus normalizálás és CDF
        normalizeInPlace(probs);
        double[] cdf = toCdf(probs);

        // Mintavételezés
        List<String> seq = new ArrayList<>(N);
        for (int i2 = 0; i2 < N; i2++) {
            double u = rng.nextDouble();
            int k = Arrays.binarySearch(cdf, u);
            if (k < 0) k = -k - 1;
            if (k >= K) k = K - 1;
            seq.add(alphabet[k]);
        }
        return seq;
    }

    private static void normalizeInPlace(double[] w) {
        double sum = 0.0;
        for (double v : w) sum += v;
        if (sum <= 0) throw new IllegalArgumentException("Non-positive weight sum");
        for (int i = 0; i < w.length; i++) w[i] /= sum;
    }

    private static double[] toCdf(double[] p) {
        double[] cdf = new double[p.length];
        double s = 0.0;
        for (int i = 0; i < p.length; i++) {
            s += p[i];
            cdf[i] = s;
        }
        cdf[cdf.length - 1] = 1.0; // biztos ami biztos
        return cdf;
    }
}


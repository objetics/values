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
        frame.getContentPane().setBackground(Color.WHITE);
        
        // Progress bar
        JProgressBar progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setString("Running weighting contrast tests...");
        progressBar.setIndeterminate(true);
        progressBar.setBackground(Color.WHITE);
        frame.add(progressBar, BorderLayout.NORTH);
        
        // Tabs for different test results
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(Color.WHITE);
        
        // Create plot panels
        PlotPanel2D assemblyRareMassPlot = new PlotPanel2D();
        assemblyRareMassPlot.setBackground(Color.WHITE);
        assemblyRareMassPlot.setLegendVisible(false);
        PlotPanel2D generalAssemblyRareMassPlot = new PlotPanel2D();
        generalAssemblyRareMassPlot.setBackground(Color.WHITE);
        generalAssemblyRareMassPlot.setLegendVisible(false);
        PlotPanel2D assemblyRareMultiplicityPlot = new PlotPanel2D();
        assemblyRareMultiplicityPlot.setBackground(Color.WHITE);
        assemblyRareMultiplicityPlot.setLegendVisible(false);
        PlotPanel2D generalAssemblyRareMultiplicityPlot = new PlotPanel2D();
        generalAssemblyRareMultiplicityPlot.setBackground(Color.WHITE);
        generalAssemblyRareMultiplicityPlot.setLegendVisible(false);
        
        // Create panels for side-by-side display
        JPanel rareMassPanel = new JPanel(new GridLayout(1, 2));
        rareMassPanel.setBackground(Color.WHITE);
        JPanel leftRareMass = new JPanel(new BorderLayout());
        leftRareMass.setBackground(Color.WHITE);
        leftRareMass.add(assemblyRareMassPlot, BorderLayout.CENTER);
        JPanel rightRareMass = new JPanel(new BorderLayout());
        rightRareMass.setBackground(Color.WHITE);
        rightRareMass.add(generalAssemblyRareMassPlot, BorderLayout.CENTER);
        rareMassPanel.add(leftRareMass);
        rareMassPanel.add(rightRareMass);
        
        JPanel rareMultiplicityPanel = new JPanel(new GridLayout(1, 2));
        rareMultiplicityPanel.setBackground(Color.WHITE);
        JPanel leftRareMultiplicity = new JPanel(new BorderLayout());
        leftRareMultiplicity.setBackground(Color.WHITE);
        leftRareMultiplicity.add(assemblyRareMultiplicityPlot, BorderLayout.CENTER);
        JPanel rightRareMultiplicity = new JPanel(new BorderLayout());
        rightRareMultiplicity.setBackground(Color.WHITE);
        rightRareMultiplicity.add(generalAssemblyRareMultiplicityPlot, BorderLayout.CENTER);
        rareMultiplicityPanel.add(leftRareMultiplicity);
        rareMultiplicityPanel.add(rightRareMultiplicity);
        
        tabbedPane.addTab("Rare Mass", rareMassPanel);
        tabbedPane.addTab("Rare Multiplicity", rareMultiplicityPanel);
        
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
                    assemblyRareMassSeries.add(new DataSeries("Assembly vs Rare Mass", assemblyRareMassPoints, Color.BLACK, true, true, 3, 6));
                    generalAssemblyRareMassSeries.add(new DataSeries("IBA vs Rare Mass", generalAssemblyRareMassPoints, Color.BLACK, true, true, 3, 6));
                    
                    // Rare Multiplicity results
                    List<Point2D> assemblyRareMultiplicityPoints = new ArrayList<>();
                    List<Point2D> generalAssemblyRareMultiplicityPoints = new ArrayList<>();
                    for (Point2D.Double point : pointsA_rareMultiplicity) {
                        assemblyRareMultiplicityPoints.add(new Point2D.Double(point.x, point.y));
                    }
                    for (Point2D.Double point : pointsAG_rareMultiplicity) {
                        generalAssemblyRareMultiplicityPoints.add(new Point2D.Double(point.x, point.y));
                    }
                    assemblyRareMultiplicitySeries.add(new DataSeries("Assembly vs Rare Types", assemblyRareMultiplicityPoints, Color.BLACK, true, true, 3, 6));
                    generalAssemblyRareMultiplicitySeries.add(new DataSeries("IBA vs Rare Types", generalAssemblyRareMultiplicityPoints, Color.BLACK, true, true, 3, 6));
                    
                    // Set data to plots
                    assemblyRareMassPlot.setDataSeries(assemblyRareMassSeries);
                    assemblyRareMassPlot.setAxisLabels("Rare Mass", "Assembly (A)");
                    assemblyRareMassPlot.setPlotTitle("Assembly - Rare Mass Contrast");
                    
                    generalAssemblyRareMassPlot.setDataSeries(generalAssemblyRareMassSeries);
                    generalAssemblyRareMassPlot.setAxisLabels("Rare Mass", "Information-based Assembly (IBA)");
                    generalAssemblyRareMassPlot.setPlotTitle("IBA - Rare Mass Contrast");
                    
                    assemblyRareMultiplicityPlot.setDataSeries(assemblyRareMultiplicitySeries);
                    assemblyRareMultiplicityPlot.setAxisLabels("Rare Types", "Assembly (A)");
                    assemblyRareMultiplicityPlot.setPlotTitle("Assembly - Rare Multiplicity Contrast");
                    
                    generalAssemblyRareMultiplicityPlot.setDataSeries(generalAssemblyRareMultiplicitySeries);
                    generalAssemblyRareMultiplicityPlot.setAxisLabels("Rare Types", "Information-based Assembly (IBA)");
                    generalAssemblyRareMultiplicityPlot.setPlotTitle("IBA - Rare Multiplicity Contrast");
                    
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
        System.out.println("=== ASSEMBLY WEIGHTING CONTRAST TEST KEZDETE ===");
        System.out.println("Paraméterek:");
        System.out.println("  N (mintaméret): " + N);
        System.out.println("  CORE_K (gyakori ábécé méret): " + CORE_K);
        System.out.println("  SEED: " + SEED);
        System.out.println("  RHO_STEPS: " + RHO_STEPS);
        System.out.println("  RHO_MAX: " + RHO_MAX);
        System.out.println("  RHO_FIX: " + RHO_FIX);
        System.out.println("  RARE_M_STEPS: " + Arrays.toString(RARE_M_STEPS));
        System.out.println();
        
        // A) Egy ritka típus tömege (rho) nő 0..RHO_MAX
        System.out.println("=== A) RITKA TÖMEG VARIÁLÁSA ===");
        System.out.println("Egy ritka típus, változó ritka tömeg (0.." + RHO_MAX + ")");
        System.out.println("Rho\t\tAssembly (A)\t\tIBA");
        System.out.println("---\t\t------------\t\t---");
        
        for (int i = 0; i < RHO_STEPS; i++) {
            double rho = (RHO_MAX * i) / (RHO_STEPS - 1); // 0, ..., RHO_MAX
            List<String> seq = generateMixtureSequence(N, CORE_K, /*rareTypes*/1, rho);
            double a  = assembly.value(seq);
            double ag = generalAssembly.value(seq);
            pointsA_rareMass.add(new Point2D.Double(rho, a));
            pointsAG_rareMass.add(new Point2D.Double(rho, ag));
            
            System.out.printf("%.4f\t\t%.6f\t\t%.6f\n", rho, a, ag);
        }
        System.out.println();

        // B) Fix ritka tömeg (rho = RHO_FIX), de egyre több ritka típusra osztjuk (M nő)
        System.out.println("=== B) RITKA TÍPUSOK SZÁMÁNAK VARIÁLÁSA ===");
        System.out.println("Fix ritka tömeg (" + RHO_FIX + "), változó ritka típusok száma");
        System.out.println("M\t\tAssembly (A)\t\tIBA");
        System.out.println("---\t\t------------\t\t---");
        
        for (int m : RARE_M_STEPS) {
            List<String> seq = generateMixtureSequence(N, CORE_K, /*rareTypes*/m, RHO_FIX);
            double a  = assembly.value(seq);
            double ag = generalAssembly.value(seq);
            pointsA_rareMultiplicity.add(new Point2D.Double(m, a));
            pointsAG_rareMultiplicity.add(new Point2D.Double(m, ag));
            
            System.out.printf("%d\t\t%.6f\t\t%.6f\n", m, a, ag);
        }
        System.out.println();
        
        System.out.println("=== TESZT BEFEJEZVE ===");
        logFinalResults();
    }

    private void logFinalResults() {
        System.out.println("=== VÉGSŐ EREDMÉNYEK ÖSSZEFOGLALÓJA ===");
        System.out.println();
        
        // Ritka tömeg variálás eredményeinek elemzése
        System.out.println("1. RITKA TÖMEG VARIÁLÁS ELEMZÉSE:");
        if (!pointsA_rareMass.isEmpty()) {
            Point2D.Double firstRareMass = pointsA_rareMass.get(0);
            Point2D.Double lastRareMass = pointsA_rareMass.get(pointsA_rareMass.size() - 1);
            double rareMassChangeA = lastRareMass.y - firstRareMass.y;
            
            Point2D.Double firstRareMassIBA = pointsAG_rareMass.get(0);
            Point2D.Double lastRareMassIBA = pointsAG_rareMass.get(pointsAG_rareMass.size() - 1);
            double rareMassChangeIBA = lastRareMassIBA.y - firstRareMassIBA.y;
            
            System.out.printf("   Ritka tömeg tartomány: %.4f - %.4f\n", firstRareMass.x, lastRareMass.x);
            System.out.printf("   Assembly változás: %.6f -> %.6f (Δ = %.6f)\n", 
                             firstRareMass.y, lastRareMass.y, rareMassChangeA);
            System.out.printf("   IBA változás: %.6f -> %.6f (Δ = %.6f)\n", 
                             firstRareMassIBA.y, lastRareMassIBA.y, rareMassChangeIBA);
            System.out.printf("   Assembly/IBA arány kezdetben: %.4f\n", firstRareMass.y / firstRareMassIBA.y);
            System.out.printf("   Assembly/IBA arány végén: %.4f\n", lastRareMass.y / lastRareMassIBA.y);
        }
        System.out.println();
        
        // Ritka típusok számának variálása eredményeinek elemzése
        System.out.println("2. RITKA TÍPUSOK SZÁMÁNAK VARIÁLÁSA ELEMZÉSE:");
        if (!pointsA_rareMultiplicity.isEmpty()) {
            Point2D.Double firstRareMultiplicity = pointsA_rareMultiplicity.get(0);
            Point2D.Double lastRareMultiplicity = pointsA_rareMultiplicity.get(pointsA_rareMultiplicity.size() - 1);
            double rareMultiplicityChangeA = lastRareMultiplicity.y - firstRareMultiplicity.y;
            
            Point2D.Double firstRareMultiplicityIBA = pointsAG_rareMultiplicity.get(0);
            Point2D.Double lastRareMultiplicityIBA = pointsAG_rareMultiplicity.get(pointsAG_rareMultiplicity.size() - 1);
            double rareMultiplicityChangeIBA = lastRareMultiplicityIBA.y - firstRareMultiplicityIBA.y;
            
            System.out.printf("   Ritka típusok száma tartomány: %.0f - %.0f\n", 
                             firstRareMultiplicity.x, lastRareMultiplicity.x);
            System.out.printf("   Assembly változás: %.6f -> %.6f (Δ = %.6f)\n", 
                             firstRareMultiplicity.y, lastRareMultiplicity.y, rareMultiplicityChangeA);
            System.out.printf("   IBA változás: %.6f -> %.6f (Δ = %.6f)\n", 
                             firstRareMultiplicityIBA.y, lastRareMultiplicityIBA.y, rareMultiplicityChangeIBA);
            System.out.printf("   Assembly/IBA arány kezdetben: %.4f\n", 
                             firstRareMultiplicity.y / firstRareMultiplicityIBA.y);
            System.out.printf("   Assembly/IBA arány végén: %.4f\n", 
                             lastRareMultiplicity.y / lastRareMultiplicityIBA.y);
        }
        System.out.println();
        
        // Correlációs elemzés
        analyzeCorrelations();
    }
    
    private void analyzeCorrelations() {
        System.out.println("3. KORRELÁCIÓ ELEMZÉS:");
        System.out.println();
        
        // Ritka tömeg korreláció
        if (pointsA_rareMass.size() > 1) {
            double corrA_rareMass = calculateCorrelation(pointsA_rareMass);
            double corrIBA_rareMass = calculateCorrelation(pointsAG_rareMass);
            System.out.printf("   Ritka tömeg vs Assembly korreláció: %.4f\n", corrA_rareMass);
            System.out.printf("   Ritka tömeg vs IBA korreláció: %.4f\n", corrIBA_rareMass);
        }
        
        // Ritka típusok száma korreláció
        if (pointsA_rareMultiplicity.size() > 1) {
            double corrA_rareMultiplicity = calculateCorrelation(pointsA_rareMultiplicity);
            double corrIBA_rareMultiplicity = calculateCorrelation(pointsAG_rareMultiplicity);
            System.out.printf("   Ritka típusok száma vs Assembly korreláció: %.4f\n", corrA_rareMultiplicity);
            System.out.printf("   Ritka típusok száma vs IBA korreláció: %.4f\n", corrIBA_rareMultiplicity);
        }
        
        System.out.println();
        System.out.println("=== WEIGHTING CONTRAST ELEMZÉS BEFEJEZVE ===");
    }
    
    private double calculateCorrelation(List<Point2D.Double> points) {
        if (points.size() < 2) return 0.0;
        
        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0, sumY2 = 0;
        int n = points.size();
        
        for (Point2D.Double point : points) {
            sumX += point.x;
            sumY += point.y;
            sumXY += point.x * point.y;
            sumX2 += point.x * point.x;
            sumY2 += point.y * point.y;
        }
        
        double numerator = n * sumXY - sumX * sumY;
        double denominator = Math.sqrt((n * sumX2 - sumX * sumX) * (n * sumY2 - sumY * sumY));
        
        return denominator != 0 ? numerator / denominator : 0.0;
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


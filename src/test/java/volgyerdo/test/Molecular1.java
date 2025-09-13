package volgyerdo.test;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import volgyerdo.value.logic.method.assembly.AssemblyMeasure;
import volgyerdo.value.logic.method.assembly.InfoBasedAssembly;

public class Molecular1 {

    private static final int DATA_SET_SIZE = 500;
    private static final int MOLECULES_PER_TYPE = 100;

    private static AssemblyMeasure assemblyMeasure = new AssemblyMeasure();
    private static InfoBasedAssembly newAssemblyMeasure = new InfoBasedAssembly();

    public static void main(String[] args) {
        String filePath = "src/test/java/volgyerdo/test/molecules-artificial-natural.txt";

        List<String> artificialMolecules = new ArrayList<>();
        List<String> naturalMolecules = new ArrayList<>();
        int validPairsCount = 0;

        try {
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                System.err.println("File does not exist: " + filePath);
                return;
            }
            try (BufferedReader reader = Files.newBufferedReader(path)) {
                String line;
                // int lineNumber = 0;
                while ((line = reader.readLine()) != null && validPairsCount < DATA_SET_SIZE) {
                    // lineNumber++;
                    line = line.trim();
                    if (!line.isEmpty() && !line.startsWith("#")) {
                        String[] parts = line.split("\\s+", 2);
                        if (parts.length == 2) {
                            String artificial = parts[0].trim();
                            String natural = parts[1].trim();
                            if (!artificial.isEmpty() && !natural.isEmpty()) {
                                artificialMolecules.add(artificial);
                                naturalMolecules.add(natural);
                                validPairsCount++;
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        // Prepare for plotting
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Assembly vs. NewAssembly Mixing");
            volgyerdo.commons.diagram.PlotPanel2D plot = new volgyerdo.commons.diagram.PlotPanel2D();
            plot.setPreferredSize(new Dimension(900, 600));
            plot.setXAxisLabel("Natural molecule ratio [%]");
            plot.setYAxisLabel("Measure value");


            List<Point2D> pointsAssembly = new ArrayList<>();
            List<Point2D> pointsNewAssembly = new ArrayList<>();
            List<Point2D> pointsExternal = new ArrayList<>();

            // int total = MOLECULES_PER_TYPE * artificialMolecules.size();
            if (artificialMolecules.isEmpty() || naturalMolecules.isEmpty()) {
                System.err.println("No molecules loaded!");
                return;
            }

            // Use the first artificial and natural molecule for mixing
            String art = artificialMolecules.get(0);
            String nat = naturalMolecules.get(0);

            int N = MOLECULES_PER_TYPE * 10; // 10x more for smoother curves
            for (int percent = 1; percent <= 100; percent++) {
                int nNat = N * percent / 100;
                int nArt = N - nNat;
                List<String> mix = new ArrayList<>(N);
                for (int i = 0; i < nArt; i++) mix.add(art);
                for (int i = 0; i < nNat; i++) mix.add(nat);

                // --- AssemblyMeasure ---
                double valAssembly = Double.NaN;
                try {
                    valAssembly = assemblyMeasure.value(mix);
                } catch (Exception e) {
                    System.err.println("AssemblyMeasure error at " + percent + "%: " + e.getMessage());
                }
                pointsAssembly.add(new Point2D.Double(percent, valAssembly));

                // --- NewAssemblyMeasure ---
                double valNewAssembly = Double.NaN;
                try {
                    valNewAssembly = newAssemblyMeasure.value(mix);
                } catch (Exception e) {
                    System.err.println("NewAssemblyMeasure error at " + percent + "%: " + e.getMessage());
                }
                pointsNewAssembly.add(new Point2D.Double(percent, valNewAssembly));

                // --- External assembly index ---
                // For efficiency, cache the external index for each unique molecule
                Map<String, Double> extIndexCache = new HashMap<>();
                for (String mol : new String[]{art, nat}) {
                    if (!extIndexCache.containsKey(mol)) {
                        extIndexCache.put(mol, getExternalAssemblyIndex(mol, mol.equals(art) ? "artificial" : "natural"));
                    }
                }
                // Now compute the formula using the external values
                Map<String, Integer> counts = new HashMap<>();
                for (String o : mix) {
                    counts.put(o, counts.getOrDefault(o, 0) + 1);
                }
                int NT = mix.size();
                double sum = 0.0;
                double logNT = Math.log(NT);
                for (Map.Entry<String, Integer> entry : counts.entrySet()) {
                    String o = entry.getKey();
                    int ni = entry.getValue();
                    double aiVal = extIndexCache.getOrDefault(o, Double.NaN);
                    if (!Double.isNaN(aiVal)) {
                        sum += Math.exp(aiVal - logNT) * (ni - 1);
                    }
                }
                pointsExternal.add(new Point2D.Double(percent, sum));
            }

            List<volgyerdo.commons.diagram.DataSeries> series = new ArrayList<>();
            //series.add(new volgyerdo.commons.diagram.DataSeries("AssemblyMeasure", pointsAssembly, Color.GRAY, true, false, 2, 8));
            series.add(new volgyerdo.commons.diagram.DataSeries("NewAssemblyMeasure", pointsNewAssembly, Color.BLACK, true, false, 2, 8));
            //series.add(new volgyerdo.commons.diagram.DataSeries("ExternalAssemblyIndex", pointsExternal, Color.RED, true, false, 2, 8));
            plot.setDataSeries(series);

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(plot);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
    // Helper: get assembly index from external tool for a molecule (SMILES string)
    private static double getExternalAssemblyIndex(String smiles, String molName) {
        try {
            // Use a single shell command to generate mol, run assembly-theory, and clean up
            String command = String.format(
                "obabel -:\"%s\" -omol --gen2D -O tmp.mol 2>/dev/null && assembly-theory tmp.mol && rm tmp.mol",
                smiles.replace("\"", "\\\"")
            );
            ProcessBuilder pb = new ProcessBuilder("bash", "-c", command);
            Process proc = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line;
            double value = Double.NaN;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    try {
                        value = Double.parseDouble(line);
                        break;
                    } catch (NumberFormatException e) {
                        // skip non-numeric lines
                    }
                }
            }
            proc.waitFor();
            return value;
        } catch (Exception e) {
            System.err.println("External assembly index error for " + molName + ": " + e.getMessage());
            return Double.NaN;
        }
    }
}
package volgyerdo.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import volgyerdo.value.logic.method.assembly.AssemblyMeasure;
import volgyerdo.value.logic.method.assembly.InfoBasedAssembly;
import volgyerdo.value.logic.method.information.GZIPInfo;

public class MolecularAssemblyComparisonTest {
    
    private static final int DATA_SET_SIZE = 500;
    
    public static void main(String[] args) {
        String filePath = "src/test/java/volgyerdo/test/molecules-artificial-natural.txt";
        
        try {
            Path path = Paths.get(filePath);
            
            if (!Files.exists(path)) {
                System.err.println("File does not exist: " + filePath);
                return;
            }
            
            System.out.println("Reading file: " + filePath);
            List<String> artificialMolecules = new ArrayList<>();
            List<String> naturalMolecules = new ArrayList<>();
            int validPairsCount = 0;
            
            try (BufferedReader reader = Files.newBufferedReader(path)) {
                String line;
                int lineNumber = 0;
                
                while ((line = reader.readLine()) != null && validPairsCount < DATA_SET_SIZE) {
                    lineNumber++;
                    line = line.trim();
                    
                    if (!line.isEmpty() && !line.startsWith("#")) {
                        String[] parts = line.split("\\s+", 2);
                        if (parts.length == 2) {
                            String artificial = parts[0].trim();
                            String natural = parts[1].trim();
                            
                            if (!artificial.isEmpty() && !natural.isEmpty()) {
                                // Add molecules to lists, each molecule added lineNumber times
                                for (int i = 0; i < lineNumber; i++) {
                                    artificialMolecules.add(artificial);
                                    naturalMolecules.add(natural);
                                }
                                validPairsCount++;
                            }
                        } else {
                            System.out.println("Warning: Invalid line format at line " + lineNumber + ": " + line);
                        }
                    }
                }
            }
            
            System.out.println("Loaded " + validPairsCount + " unique molecule pairs (max " + DATA_SET_SIZE + ")");
            System.out.println("Total artificial molecules: " + artificialMolecules.size());
            System.out.println("Total natural molecules: " + naturalMolecules.size());
            
            // Print first few molecules as example
            System.out.println("\nFirst few artificial molecules:");
            for (int i = 0; i < Math.min(10, artificialMolecules.size()); i++) {
                System.out.println("  " + (i+1) + ". " + artificialMolecules.get(i));
            }
            
            System.out.println("\nFirst few natural molecules:");
            for (int i = 0; i < Math.min(10, naturalMolecules.size()); i++) {
                System.out.println("  " + (i+1) + ". " + naturalMolecules.get(i));
            }
            
            // Calculate assembly values
            System.out.println("\n=== ASSEMBLY CALCULATIONS ===");

            AssemblyMeasure assemblyMeasure = new AssemblyMeasure(new GZIPInfo());
            InfoBasedAssembly infoBasedAssembly = new InfoBasedAssembly();
            
            try {
                // Calculate values for artificial molecules
                System.out.println("\nCalculating for artificial molecules...");
                double artificialAssembly = assemblyMeasure.value(artificialMolecules);
                double artificialInfoBased = infoBasedAssembly.value(artificialMolecules);
                
                // Calculate values for natural molecules
                System.out.println("Calculating for natural molecules...");
                double naturalAssembly = assemblyMeasure.value(naturalMolecules);
                double naturalInfoBased = infoBasedAssembly.value(naturalMolecules);
                
                // Summary of results
                System.out.println("\n=== RESULTS SUMMARY ===");
                System.out.println("AssemblyMeasure:");
                System.out.println("  Artificial molecules: " + String.format("%.6f", artificialAssembly));
                System.out.println("  Natural molecules:    " + String.format("%.6f", naturalAssembly));
                System.out.println("  Difference:           " + String.format("%.6f", artificialAssembly - naturalAssembly));
                System.out.println("  Ratio (Art/Nat):      " + String.format("%.4f", artificialAssembly / naturalAssembly));
                
                System.out.println("\nInfoBasedAssembly:");
                System.out.println("  Artificial molecules: " + String.format("%.6f", artificialInfoBased));
                System.out.println("  Natural molecules:    " + String.format("%.6f", naturalInfoBased));
                System.out.println("  Difference:           " + String.format("%.6f", artificialInfoBased - naturalInfoBased));
                System.out.println("  Ratio (Art/Nat):      " + String.format("%.4f", artificialInfoBased / naturalInfoBased));
                
            } catch (Exception e) {
                System.err.println("Error calculating assembly values: " + e.getMessage());
                e.printStackTrace();
            }
            
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
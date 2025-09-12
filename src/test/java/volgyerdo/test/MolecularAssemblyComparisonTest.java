package volgyerdo.test;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.stream.Collectors;

import volgyerdo.value.logic.method.assembly.AssemblyMeasure;
import volgyerdo.value.logic.method.assembly.InfoBasedAssembly;

/**
 * Test that compares assembly and InfoBasedAssembly values for artificial vs natural molecules
 * Reads SMI files from /media/PROJECTS/Github/molecules/artificial and /media/PROJECTS/Github/molecules/natural
 * Creates 500-molecule datasets of similar-sized molecules and compares their assembly properties
 */
public class MolecularAssemblyComparisonTest {
    
    private static final Logger LOGGER = Logger.getLogger(MolecularAssemblyComparisonTest.class.getName());
    private static final String ARTIFICIAL_DIR = "/media/PROJECTS/Github/molecules/artificial";
    private static final String NATURAL_DIR = "/media/PROJECTS/Github/molecules/natural";
    private static final int DATASET_SIZE = 10;
    private static final double SIZE_TOLERANCE = 0.1; // 10% tolerance for molecular size similarity
    
    // Assembly calculators
    private final AssemblyMeasure assemblyMeasure = new AssemblyMeasure();
    private final InfoBasedAssembly infoBasedAssembly = new InfoBasedAssembly();
    
    // Data structures for molecules
    private static class Molecule {
        String structure;
        String name;
        int size;
        
        Molecule(String structure, String name) {
            this.structure = structure;
            this.name = name;
            this.size = structure.length(); // Simple size estimate based on SMILES length
        }
    }
    
    private List<Molecule> artificialMolecules = new ArrayList<>();
    private List<Molecule> naturalMolecules = new ArrayList<>();
    
    public static void main(String[] args) {
        MolecularAssemblyComparisonTest test = new MolecularAssemblyComparisonTest();
        test.runComparison();
    }
    
    public void runComparison() {
        LOGGER.info("Starting Molecular Assembly Comparison Test");
        
        try {
            // Step 1: Load artificial molecules first
            loadMoleculesFromDirectory(ARTIFICIAL_DIR, artificialMolecules, "artificial");
            LOGGER.info("Loaded " + artificialMolecules.size() + " artificial molecules");
            
            if (artificialMolecules.isEmpty()) {
                LOGGER.severe("No artificial molecules loaded. Please check directory path and SMI files.");
                return;
            }
            
            // Step 2: Create artificial dataset
            List<Molecule> artificialDataset = createSizeMatchedDataset(artificialMolecules);
            LOGGER.info("Created artificial dataset with " + artificialDataset.size() + " molecules");
            
            if (artificialDataset.isEmpty()) {
                LOGGER.severe("Could not create artificial dataset.");
                return;
            }
            
            // Step 3: For each artificial molecule, find a similar-sized natural molecule
            List<Molecule> naturalDataset = findMatchingNaturalMolecules(artificialDataset);
            LOGGER.info("Created natural dataset with " + naturalDataset.size() + " molecules");
            
            if (naturalDataset.size() != artificialDataset.size()) {
                LOGGER.warning("Warning: Natural dataset size (" + naturalDataset.size() + 
                             ") doesn't match artificial dataset size (" + artificialDataset.size() + ")");
            }
            
            // Step 4: Calculate assembly values
            compareAssemblyValues(artificialDataset, naturalDataset);
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during molecular assembly comparison", e);
        }
    }
    
    /**
     * Load molecules from all SMI files in a directory by randomly sampling
     */
    private void loadMoleculesFromDirectory(String directoryPath, List<Molecule> molecules, String type) {
        try {
            Path dir = Paths.get(directoryPath);
            if (!Files.exists(dir)) {
                LOGGER.warning("Directory does not exist: " + directoryPath);
                return;
            }
            
            List<Path> smiFiles = Files.walk(dir)
                .filter(path -> path.toString().toLowerCase().endsWith(".smi"))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
                
            if (smiFiles.isEmpty()) {
                LOGGER.warning("No SMI files found in directory: " + directoryPath);
                return;
            }
            
            LOGGER.info("Found " + smiFiles.size() + " SMI files in " + directoryPath);
            
            // Calculate how many molecules to sample from each file
            int moleculesPerFile = Math.max(1, DATASET_SIZE / smiFiles.size()); // Direct DATASET_SIZE distribution
            
            for (Path smiFile : smiFiles) {
                sampleMoleculesFromSmiFile(smiFile, molecules, type, moleculesPerFile);
            }
                
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error loading molecules from directory: " + directoryPath, e);
        }
    }
    
    /**
     * For each artificial molecule, find a matching natural molecule of similar size
     */
    private List<Molecule> findMatchingNaturalMolecules(List<Molecule> artificialMolecules) {
        List<Molecule> matchingNaturalMolecules = new ArrayList<>();
        Path naturalSmiFile = Paths.get(NATURAL_DIR, "natural.smi");
        
        if (!Files.exists(naturalSmiFile)) {
            LOGGER.severe("Natural SMI file does not exist: " + naturalSmiFile);
            return matchingNaturalMolecules;
        }
        
        LOGGER.info("Finding matching natural molecules for " + artificialMolecules.size() + " artificial molecules");
        
        for (Molecule artificialMolecule : artificialMolecules) {
            try {
                Molecule matchingNatural = findSimilarSizedMolecule(naturalSmiFile, artificialMolecule.size);
                if (matchingNatural != null) {
                    matchingNaturalMolecules.add(matchingNatural);
                    LOGGER.fine("Found natural molecule of size " + matchingNatural.size + 
                               " for artificial molecule of size " + artificialMolecule.size);
                } else {
                    LOGGER.warning("Could not find matching natural molecule for artificial molecule of size " + 
                                 artificialMolecule.size);
                }
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Error finding natural molecule match for artificial molecule " + 
                          artificialMolecule.name, e);
            }
        }
        
        return matchingNaturalMolecules;
    }
    
    /**
     * Find a molecule of similar size from the natural SMI file
     */
    private Molecule findSimilarSizedMolecule(Path smiFile, int targetSize) throws IOException {
        // Define size tolerance for matching
        int sizeToleranceAbsolute = Math.max(1, targetSize / 10); // 10% tolerance, minimum 1
        int minSize = targetSize - sizeToleranceAbsolute;
        int maxSize = targetSize + sizeToleranceAbsolute;
        
        // First, count total lines
        long totalLines = countLines(smiFile);
        if (totalLines == 0) {
            return null;
        }
        
        Random random = ThreadLocalRandom.current();
        int maxAttempts = Math.min(100, (int)totalLines); // Try up to 100 random lines
        
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            long randomLineNumber = random.nextLong(totalLines) + 1;
            
            try (BufferedReader reader = Files.newBufferedReader(smiFile)) {
                String line = null;
                long currentLine = 0;
                
                // Read to the target line
                while ((line = reader.readLine()) != null && currentLine < randomLineNumber) {
                    currentLine++;
                    if (currentLine == randomLineNumber) {
                        break;
                    }
                }
                
                if (line != null) {
                    line = line.trim();
                    if (!line.isEmpty() && !line.startsWith("#")) {
                        String[] parts = line.split("\\s+", 2);
                        if (parts.length >= 1) {
                            String structure = parts[0];
                            if (!structure.isEmpty()) {
                                int moleculeSize = structure.length();
                                
                                // Check if size matches within tolerance
                                if (moleculeSize >= minSize && moleculeSize <= maxSize) {
                                    String name = parts.length > 1 ? parts[1] : 
                                                 "natural_" + randomLineNumber;
                                    return new Molecule(structure, name);
                                }
                            }
                        }
                    }
                }
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Error reading line " + randomLineNumber + " from " + smiFile, e);
            }
        }
        
        // If we couldn't find a match with random sampling, try a more systematic approach
        LOGGER.fine("Random sampling failed for size " + targetSize + ", trying systematic search");
        return findSimilarSizedMoleculeSystematic(smiFile, targetSize, minSize, maxSize);
    }
    
    /**
     * Systematic search for a molecule of similar size (fallback method)
     */
    private Molecule findSimilarSizedMoleculeSystematic(Path smiFile, int targetSize, int minSize, int maxSize) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(smiFile)) {
            String line;
            long lineNumber = 0;
            
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();
                
                if (!line.isEmpty() && !line.startsWith("#")) {
                    String[] parts = line.split("\\s+", 2);
                    if (parts.length >= 1) {
                        String structure = parts[0];
                        if (!structure.isEmpty()) {
                            int moleculeSize = structure.length();
                            
                            if (moleculeSize >= minSize && moleculeSize <= maxSize) {
                                String name = parts.length > 1 ? parts[1] : 
                                             "natural_systematic_" + lineNumber;
                                return new Molecule(structure, name);
                            }
                        }
                    }
                }
                
                // Stop after checking a reasonable number of lines
                if (lineNumber > 10000) {
                    break;
                }
            }
        }
        
        return null; // No match found
    }
    
    /**
     * Randomly sample molecules from a single SMI file by reading only specific random lines
     * This approach is memory-efficient for gigabyte-sized files
     */
    private void sampleMoleculesFromSmiFile(Path smiFile, List<Molecule> molecules, String type, int targetSamples) {
        try {
            LOGGER.info("Sampling from " + smiFile.getFileName() + " (target: " + targetSamples + " molecules)");
            
            // First pass: count total lines (quick scan)
            long totalLines = countLines(smiFile);
            LOGGER.info("File has approximately " + totalLines + " lines");
            
            if (totalLines == 0) {
                LOGGER.warning("File appears to be empty: " + smiFile.getFileName());
                return;
            }
            
            // Generate random line numbers to read
            Set<Long> randomLineNumbers = generateRandomLineNumbers(totalLines, targetSamples);
            LOGGER.info("Will read " + randomLineNumbers.size() + " random lines");
            
            // Second pass: read only the selected lines
            List<Molecule> sampledMolecules = readSpecificLines(smiFile, randomLineNumbers, type);
            
            // Add sampled molecules to the main collection
            molecules.addAll(sampledMolecules);
            
            LOGGER.info("Successfully sampled " + sampledMolecules.size() + " molecules from " + 
                       smiFile.getFileName());
            
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error reading SMI file: " + smiFile, e);
        }
    }
    
    /**
     * Count lines in file efficiently without reading all content into memory
     */
    private long countLines(Path filePath) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            long lines = 0;
            while (reader.readLine() != null) {
                lines++;
            }
            return lines;
        }
    }
    
    /**
     * Generate set of random line numbers to read
     */
    private Set<Long> generateRandomLineNumbers(long totalLines, int targetSamples) {
        Set<Long> lineNumbers = new HashSet<>();
        Random random = ThreadLocalRandom.current();
        
        // Ensure we don't try to sample more lines than exist
        int actualSamples = Math.min(targetSamples, (int) totalLines);
        
        while (lineNumbers.size() < actualSamples) {
            long randomLine = random.nextLong(totalLines) + 1; // 1-based line numbers
            lineNumbers.add(randomLine);
        }
        
        return lineNumbers;
    }
    
    /**
     * Read only specific line numbers from the file
     */
    private List<Molecule> readSpecificLines(Path smiFile, Set<Long> targetLineNumbers, String type) throws IOException {
        List<Molecule> molecules = new ArrayList<>();
        
        try (BufferedReader reader = Files.newBufferedReader(smiFile)) {
            String line;
            long currentLineNumber = 0;
            
            while ((line = reader.readLine()) != null) {
                currentLineNumber++;
                
                // Only process if this line number is in our target set
                if (targetLineNumbers.contains(currentLineNumber)) {
                    line = line.trim();
                    
                    if (!line.isEmpty() && !line.startsWith("#")) {
                        String[] parts = line.split("\\s+", 2);
                        if (parts.length >= 1) {
                            String structure = parts[0];
                            if (!structure.isEmpty()) {
                                String name = parts.length > 1 ? parts[1] : 
                                             type + "_" + smiFile.getFileName() + "_" + currentLineNumber;
                                molecules.add(new Molecule(structure, name));
                            }
                        }
                    }
                    
                    // Remove from target set for efficiency (early termination possible)
                    targetLineNumbers.remove(currentLineNumber);
                    
                    // Early termination if we've found all target lines
                    if (targetLineNumbers.isEmpty()) {
                        break;
                    }
                }
            }
        }
        
        return molecules;
    }
    
    /**
     * Create a dataset of similar-sized molecules
     */
    private List<Molecule> createSizeMatchedDataset(List<Molecule> allMolecules) {
        if (allMolecules.isEmpty()) {
            return new ArrayList<>();
        }
        
        LOGGER.info("Creating size-matched dataset from " + allMolecules.size() + " molecules");
        
        // Calculate size statistics
        IntSummaryStatistics sizeStats = allMolecules.stream()
            .mapToInt(m -> m.size)
            .summaryStatistics();
            
        double avgSize = sizeStats.getAverage();
        int minSize = sizeStats.getMin();
        int maxSize = sizeStats.getMax();
        
        LOGGER.info("Molecular size stats - Min: " + minSize + ", Max: " + maxSize + 
                   ", Average: " + String.format("%.2f", avgSize));
        
        // Use a more flexible size matching strategy
        // If we have enough variety, use the original tolerance
        // If not, expand the tolerance to get enough molecules
        double tolerance = SIZE_TOLERANCE;
        List<Molecule> sizeMatchedMolecules;
        
        do {
            final double currentTolerance = tolerance;
            sizeMatchedMolecules = allMolecules.stream()
                .filter(m -> Math.abs(m.size - avgSize) / avgSize <= currentTolerance)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
                
            if (sizeMatchedMolecules.size() < DATASET_SIZE && tolerance < 0.5) {
                tolerance += 0.1; // Gradually expand tolerance
                LOGGER.info("Expanding size tolerance to " + String.format("%.1f", tolerance) + 
                           " to get more molecules");
            } else {
                break;
            }
        } while (tolerance <= 0.5);
        
        LOGGER.info("Found " + sizeMatchedMolecules.size() + " molecules within " + 
                   String.format("%.1f", tolerance * 100) + "% size tolerance");
            
        // Randomly select up to DATASET_SIZE molecules
        if (sizeMatchedMolecules.size() > DATASET_SIZE) {
            Collections.shuffle(sizeMatchedMolecules, ThreadLocalRandom.current());
            sizeMatchedMolecules = sizeMatchedMolecules.subList(0, DATASET_SIZE);
        }
        
        // Log final dataset size statistics
        if (!sizeMatchedMolecules.isEmpty()) {
            IntSummaryStatistics finalStats = sizeMatchedMolecules.stream()
                .mapToInt(m -> m.size)
                .summaryStatistics();
            LOGGER.info("Final dataset - Size range: [" + finalStats.getMin() + 
                       ", " + finalStats.getMax() + "], Average: " + 
                       String.format("%.2f", finalStats.getAverage()) + 
                       ", Count: " + finalStats.getCount());
        }
        
        return sizeMatchedMolecules;
    }
    
    /**
     * Calculate and compare assembly values for both datasets
     */
    private void compareAssemblyValues(List<Molecule> artificialDataset, List<Molecule> naturalDataset) {
        LOGGER.info("=== MOLECULAR ASSEMBLY COMPARISON RESULTS ===");
        
        // Calculate assembly values for artificial molecules
        LOGGER.info("\nProcessing artificial molecules...");
        AssemblyResults artificialResults = calculateAssemblyValues(artificialDataset, "artificial");
        
        // Calculate assembly values for natural molecules  
        LOGGER.info("\nProcessing natural molecules...");
        AssemblyResults naturalResults = calculateAssemblyValues(naturalDataset, "natural");
        
        // Compare results
        LOGGER.info("\n=== COMPARISON SUMMARY ===");
        logComparisonResults("Assembly Measure", artificialResults.assemblyStats, naturalResults.assemblyStats);
        logComparisonResults("InfoBasedAssembly", artificialResults.infoBasedStats, naturalResults.infoBasedStats);
        
        // Additional analysis
        analyzeDistributions(artificialResults, naturalResults);
    }
    
    /**
     * Calculate assembly values for the entire molecular dataset (not individual molecules)
     */
    private AssemblyResults calculateAssemblyValues(List<Molecule> molecules, String type) {
        List<String> moleculeStructures = molecules.stream()
            .map(m -> m.structure)
            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        
        // Create exponential distribution of repetitions
        // Some molecules appear many times, others appear few times
        List<String> augmentedStructures = createExponentialDistribution(moleculeStructures, type);
        
        LOGGER.info("Calculating assembly values for " + molecules.size() + " " + type + " molecules (" + 
                   augmentedStructures.size() + " total with exponential distribution) as a collection");
        
        try {
            // Calculate assembly values for the entire collection
            double assemblyValue = assemblyMeasure.value(augmentedStructures);
            double infoBasedValue = infoBasedAssembly.value(augmentedStructures);
            
            // Debug output: log collection assembly values
            LOGGER.info("DEBUG " + type + " Collection - Size: " + molecules.size() + 
                       " molecules (" + augmentedStructures.size() + " with duplicates) | Assembly: " + 
                       String.format("%.6f", assemblyValue) + " | InfoBased: " + String.format("%.6f", infoBasedValue));
            
            // Log first few molecules for reference
            LOGGER.info("Sample molecules from " + type + " collection:");
            for (int i = 0; i < Math.min(3, molecules.size()); i++) {
                Molecule mol = molecules.get(i);
                LOGGER.info("  " + (i+1) + ". " + mol.structure + " | " + mol.name + " | Size: " + mol.size);
            }
            
            // Since we have single values for the entire collection, we create statistics with count=1
            Statistics assemblyStats = new Statistics(assemblyValue, 0.0, assemblyValue, assemblyValue, 1);
            Statistics infoBasedStats = new Statistics(infoBasedValue, 0.0, infoBasedValue, infoBasedValue, 1);
            
            return new AssemblyResults(assemblyStats, infoBasedStats);
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error calculating assembly values for " + type + " collection", e);
            // Return empty statistics in case of error
            Statistics emptyStats = new Statistics(0, 0, 0, 0, 0);
            return new AssemblyResults(emptyStats, emptyStats);
        }
    }
    
    /**
     * Create exponential distribution of molecular repetitions
     * Some molecules appear frequently, others rarely
     */
    private List<String> createExponentialDistribution(List<String> originalMolecules, String type) {
        if (originalMolecules.isEmpty()) {
            return new ArrayList<>(originalMolecules);
        }
        
        List<String> result = new ArrayList<>();
        Random random = ThreadLocalRandom.current();
        
        // Sort molecules by some criteria (e.g., length) for consistent behavior
        List<String> sortedMolecules = new ArrayList<>(originalMolecules);
        sortedMolecules.sort(String::compareTo);
        
        // Create exponential distribution: first few molecules get many repetitions,
        // later molecules get fewer repetitions
        for (int i = 0; i < sortedMolecules.size(); i++) {
            String molecule = sortedMolecules.get(i);
            
            // Exponential decay: early molecules get more repetitions
            // λ = 1.5 gives good exponential decay
            double lambda = 1.5;
            double position = (double) i / sortedMolecules.size();
            
            // Base count (always include original) + exponential additional copies
            int baseCount = 1;
            int additionalCopies = (int) Math.round(Math.exp(-lambda * position) * 20); // max ~20 additional copies
            
            // Ensure at least one copy, but add variation
            int totalCopies = baseCount + additionalCopies + random.nextInt(3); // 0-2 random extra
            
            // Add the molecule the calculated number of times
            for (int j = 0; j < totalCopies; j++) {
                result.add(molecule);
            }
        }
        
        // Shuffle the result to mix the repetitions
        Collections.shuffle(result, random);
        
        // Log distribution statistics
        Map<String, Long> distribution = result.stream()
            .collect(Collectors.groupingBy(s -> s, Collectors.counting()));
        
        LOGGER.info("Exponential distribution for " + type + ":");
        LOGGER.info("  Original unique molecules: " + originalMolecules.size());
        LOGGER.info("  Total molecules after distribution: " + result.size());
        LOGGER.info("  Most frequent molecule appears: " + distribution.values().stream().max(Long::compare).orElse(0L) + " times");
        LOGGER.info("  Least frequent molecule appears: " + distribution.values().stream().min(Long::compare).orElse(0L) + " times");
        
        return result;
    }
    
    /**
     * Calculate basic statistics for a list of values
     */
    private Statistics calculateStatistics(List<Double> values) {
        if (values.isEmpty()) {
            return new Statistics(0, 0, 0, 0, 0);
        }
        
        double sum = values.stream().mapToDouble(Double::doubleValue).sum();
        double mean = sum / values.size();
        
        double variance = values.stream()
            .mapToDouble(v -> Math.pow(v - mean, 2))
            .sum() / values.size();
        double stdDev = Math.sqrt(variance);
        
        List<Double> sorted = new ArrayList<>(values);
        Collections.sort(sorted);
        
        double min = sorted.get(0);
        double max = sorted.get(sorted.size() - 1);
        
        return new Statistics(mean, stdDev, min, max, values.size());
    }
    
    /**
     * Log comparison results for single values (not distributions)
     */
    private void logComparisonResults(String measureName, Statistics artificial, Statistics natural) {
        LOGGER.info("\n" + measureName + " Comparison:");
        LOGGER.info("  Artificial Collection: " + String.format("%.6f", artificial.mean));
        LOGGER.info("  Natural Collection:    " + String.format("%.6f", natural.mean));
        
        double difference = artificial.mean - natural.mean;
        double relativeDiff = Math.abs(difference) / Math.max(Math.abs(artificial.mean), Math.abs(natural.mean)) * 100;
        
        LOGGER.info("  Difference: " + String.format("%.6f", difference) + 
                   " (" + String.format("%.2f", relativeDiff) + "% relative difference)");
    }
    
    /**
     * Analyze distributions and patterns for collection-level assembly values
     */
    private void analyzeDistributions(AssemblyResults artificial, AssemblyResults natural) {
        LOGGER.info("\n=== COLLECTION ANALYSIS ===");
        
        // Assembly measure analysis
        double assemblyRatio = artificial.assemblyStats.mean / natural.assemblyStats.mean;
        LOGGER.info("Assembly Measure Ratio (Artificial/Natural): " + String.format("%.4f", assemblyRatio));
        
        if (assemblyRatio > 1.1) {
            LOGGER.info("  → Artificial molecule collection shows higher assembly complexity");
        } else if (assemblyRatio < 0.9) {
            LOGGER.info("  → Natural molecule collection shows higher assembly complexity");
        } else {
            LOGGER.info("  → Similar assembly complexity between artificial and natural collections");
        }
        
        // InfoBasedAssembly analysis
        double infoRatio = artificial.infoBasedStats.mean / natural.infoBasedStats.mean;
        LOGGER.info("InfoBasedAssembly Ratio (Artificial/Natural): " + String.format("%.4f", infoRatio));
        
        if (infoRatio > 1.1) {
            LOGGER.info("  → Artificial molecule collection shows higher information-based assembly");
        } else if (infoRatio < 0.9) {
            LOGGER.info("  → Natural molecule collection shows higher information-based assembly");
        } else {
            LOGGER.info("  → Similar information-based assembly between artificial and natural collections");
        }
        
        // Summary
        LOGGER.info("\nSummary:");
        LOGGER.info("  Collections compared: " + artificial.assemblyStats.count + " vs " + natural.assemblyStats.count);
        LOGGER.info("  Assembly difference: " + String.format("%.6f", artificial.assemblyStats.mean - natural.assemblyStats.mean));
        LOGGER.info("  InfoBased difference: " + String.format("%.6f", artificial.infoBasedStats.mean - natural.infoBasedStats.mean));
    }
    
    // Helper classes for data organization
    private static class Statistics {
        final double mean;
        final double stdDev;
        final double min;
        final double max;
        final int count;
        
        Statistics(double mean, double stdDev, double min, double max, int count) {
            this.mean = mean;
            this.stdDev = stdDev;
            this.min = min;
            this.max = max;
            this.count = count;
        }
    }
    
    private static class AssemblyResults {
        final Statistics assemblyStats;
        final Statistics infoBasedStats;
        
        AssemblyResults(Statistics assemblyStats, Statistics infoBasedStats) {
            this.assemblyStats = assemblyStats;
            this.infoBasedStats = infoBasedStats;
        }
    }
}

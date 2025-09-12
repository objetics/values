package volgyerdo.test;

import java.util.*;
import java.util.logging.Logger;

import volgyerdo.value.logic.method.assembly.AssemblyMeasure;
import volgyerdo.value.logic.method.assembly.InfoBasedAssembly;

/**
 * Simple test to verify assembly algorithms are working correctly
 */
public class SimpleAssemblyTest {
    
    private static final Logger LOGGER = Logger.getLogger(SimpleAssemblyTest.class.getName());
    
    public static void main(String[] args) {
        SimpleAssemblyTest test = new SimpleAssemblyTest();
        test.runTests();
    }
    
    public void runTests() {
        AssemblyMeasure assemblyMeasure = new AssemblyMeasure();
        InfoBasedAssembly infoBasedAssembly = new InfoBasedAssembly();
        
        // Test 1: Simple string collection
        LOGGER.info("=== TEST 1: Simple Strings ===");
        List<String> simpleStrings = Arrays.asList("ABC", "DEF", "ABC", "GHI");
        testCollection(assemblyMeasure, infoBasedAssembly, simpleStrings, "Simple Strings");
        
        // Test 2: Repeated patterns
        LOGGER.info("\n=== TEST 2: Repeated Patterns ===");
        List<String> repeatedPatterns = Arrays.asList("AA", "BB", "AA", "CC", "AA");
        testCollection(assemblyMeasure, infoBasedAssembly, repeatedPatterns, "Repeated Patterns");
        
        // Test 3: Single unique item
        LOGGER.info("\n=== TEST 3: Single Unique Item ===");
        List<String> singleItem = Arrays.asList("XYZ");
        testCollection(assemblyMeasure, infoBasedAssembly, singleItem, "Single Item");
        
        // Test 4: All unique items
        LOGGER.info("\n=== TEST 4: All Unique Items ===");
        List<String> uniqueItems = Arrays.asList("A", "B", "C", "D", "E");
        testCollection(assemblyMeasure, infoBasedAssembly, uniqueItems, "All Unique");
        
        // Test 5: Some sample SMILES strings
        LOGGER.info("\n=== TEST 5: Sample SMILES ===");
        List<String> sampleSmiles = Arrays.asList(
            "CCO", "CCO", "CCC", "CCCC", "CCO"
        );
        testCollection(assemblyMeasure, infoBasedAssembly, sampleSmiles, "Sample SMILES");
        
        // Test 6: More complex SMILES-like strings
        LOGGER.info("\n=== TEST 6: Complex SMILES-like ===");
        List<String> complexSmiles = Arrays.asList(
            "C1=CC=CC=C1", "CCO", "C1=CC=CC=C1", "CCC(=O)O", "CCO"
        );
        testCollection(assemblyMeasure, infoBasedAssembly, complexSmiles, "Complex SMILES-like");
    }
    
    private void testCollection(AssemblyMeasure assemblyMeasure, InfoBasedAssembly infoBasedAssembly, 
                               List<String> collection, String name) {
        try {
            double assemblyValue = assemblyMeasure.value(collection);
            double infoBasedValue = infoBasedAssembly.value(collection);
            
            LOGGER.info(name + " Collection:");
            LOGGER.info("  Items: " + collection);
            LOGGER.info("  Unique count: " + new HashSet<>(collection).size() + "/" + collection.size());
            LOGGER.info("  Assembly Measure: " + String.format("%.6f", assemblyValue));
            LOGGER.info("  InfoBased Assembly: " + String.format("%.6f", infoBasedValue));
            
            // Basic sanity checks
            if (Double.isNaN(assemblyValue)) {
                LOGGER.warning("  WARNING: Assembly Measure returned NaN");
            }
            if (Double.isNaN(infoBasedValue)) {
                LOGGER.warning("  WARNING: InfoBased Assembly returned NaN");
            }
            if (assemblyValue < 0) {
                LOGGER.warning("  WARNING: Assembly Measure returned negative value");
            }
            if (infoBasedValue < 0) {
                LOGGER.warning("  WARNING: InfoBased Assembly returned negative value");
            }
            
        } catch (Exception e) {
            LOGGER.severe("Error testing " + name + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
